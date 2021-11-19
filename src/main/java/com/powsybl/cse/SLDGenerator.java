package com.powsybl.cse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.stream.FileCacheImageOutputStream;

import com.powsybl.cse.model.CEType;
import com.powsybl.cse.model.Substation;
import com.powsybl.cse.model.Terminal;
import com.powsybl.cse.model.VoltageLevel;
import com.powsybl.sld.RawGraphBuilder;
import com.powsybl.sld.VoltageLevelDiagram;
import com.powsybl.sld.RawGraphBuilder.VoltageLevelBuilder;
import com.powsybl.sld.layout.BlockOrganizer;
import com.powsybl.sld.layout.ImplicitCellDetector;
import com.powsybl.sld.layout.LayoutParameters;
import com.powsybl.sld.layout.PositionVoltageLevelLayout;
import com.powsybl.sld.layout.PositionVoltageLevelLayoutFactory;
import com.powsybl.sld.layout.VoltageLevelLayout;
import com.powsybl.sld.layout.VoltageLevelLayoutFactory;
import com.powsybl.sld.layout.positionbyclustering.PositionByClustering;
import com.powsybl.sld.library.ComponentLibrary;
import com.powsybl.sld.library.ComponentTypeName;
import com.powsybl.sld.library.ConvergenceComponentLibrary;
import com.powsybl.sld.model.FeederNode;
import com.powsybl.sld.model.Node;
import com.powsybl.sld.model.VoltageLevelGraph;
import com.powsybl.sld.model.SwitchNode.SwitchKind;
import com.powsybl.sld.svg.DefaultDiagramStyleProvider;
import com.powsybl.sld.svg.DefaultSVGWriter;
import com.powsybl.sld.svg.DiagramLabelProvider;
import com.powsybl.sld.svg.FeederInfo;
import com.powsybl.sld.svg.LabelPosition;

public class SLDGenerator {
    private Substation substation;
    private Map<String, Node> path2Node = new HashMap<>();
    VoltageLevelBuilder vlBuilder;

    public SLDGenerator(Substation substation) {
        this.substation = substation;
    }

    public void buildSldGraph() {
        Optional<VoltageLevel> oVl = substation.getVoltageLevels().findFirst();
        VoltageLevel vl;
        if (oVl.isPresent()) {
            vl = oVl.get();
        } else {
            return;
        }

        vlBuilder = new RawGraphBuilder().createVoltageLevelBuilder(vl.getName(), vl.getVoltage());

        vl.gConnectivityNodes().forEach(cn -> {
            String pathName = cn.getPathName();
            if (pathName.contains("BusBar")) {
                path2Node.put(pathName, vlBuilder.createBusBarSection(pathName));
            } else {
                path2Node.put(pathName, vlBuilder.createFictitiousNode(pathName));
            }
        });

        vl.getBays().forEach(bay -> {
            bay.getConductingEquipmentStream().forEach(ce -> {
                List<Terminal> terminals = ce.getTerminalsStream().collect(Collectors.toList());
                Node node;
                String pathName = ce.getName();
                if (ce.getCeType() == CEType.DIS) {
                    node = vlBuilder.createSwitchNode(SwitchKind.DISCONNECTOR, pathName, false, false);
                } else if (ce.getCeType() == CEType.CBR) {
                    node = vlBuilder.createSwitchNode(SwitchKind.BREAKER, pathName, false, false);
                } else {
                    node = vlBuilder.createFictitiousNode(pathName);
                }

                Node node1 = terminalToNode(terminals.get(0));
                Node node2 = null;
                double termNb = terminals.size();
                if (termNb == 1) {
                    node2 = vlBuilder.createLoad(pathName);
                } else if (termNb == 2) {
                    node2 = terminalToNode(terminals.get(1));
                }
                vlBuilder.connectNode(node1, node2);
                System.out.println(node1.getId() + "\n" + node2.getId() + "\n");
            });
        });
    }

    private Node terminalToNode(Terminal terminal) {
        String pathName = terminal.getConnectivityNodePathName();
        if (pathName != null) {
            return path2Node.get(pathName);
        }
        return vlBuilder.createLoad(terminal.getcNodeName());
    }

    public void writeResults(String fileName) {
        VoltageLevelGraph graph = vlBuilder.getGraph();
        LayoutParameters layoutParameters = new LayoutParameters().setAdaptCellHeightToContent(true)
                .setCssLocation(LayoutParameters.CssLocation.INSERTED_IN_SVG);

        new ImplicitCellDetector().detectCells(graph);
        new BlockOrganizer().organize(graph);
        new PositionVoltageLevelLayout(graph).run(layoutParameters);

        StringWriter writer = new StringWriter();
        graph.writeJson(writer);
        writeFile(fileName + ".json", writer);

        writer = new StringWriter();
        DefaultSVGWriter svgWriter = new DefaultSVGWriter(new ConvergenceComponentLibrary(), layoutParameters);
        svgWriter.write("", graph, new RawDiagramLabelProvider(graph), new DefaultDiagramStyleProvider(), writer);
        writeFile(fileName + ".svg", writer);
    }

    void writeFile(String fileName, StringWriter writer) {
        File homeFolder = new File(System.getProperty("user.home"));
        File file = new File(homeFolder, fileName);
        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            fw.write(writer.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static class RawDiagramLabelProvider implements DiagramLabelProvider {
        private final Map<Node, List<NodeLabel>> busLabels;

        public RawDiagramLabelProvider(VoltageLevelGraph graph) {
            this.busLabels = new HashMap<>();
            LabelPosition labelPosition = new LabelPosition("default", 0, -5, true, 0);
            graph.getNodes().forEach(n -> {
                List<DiagramLabelProvider.NodeLabel> labels = new ArrayList<>();
                labels.add(new DiagramLabelProvider.NodeLabel(n.getLabel(), labelPosition, null));
                busLabels.put(n, labels);
            });
        }

        @Override
        public List<FeederInfo> getFeederInfos(FeederNode node) {
            return Arrays.asList(new FeederInfo(ComponentTypeName.ARROW_ACTIVE, Direction.OUT, "", "tata", null),
                    new FeederInfo(ComponentTypeName.ARROW_REACTIVE, Direction.IN, "", "tutu", null));
        }

        @Override
        public List<NodeLabel> getNodeLabels(Node node) {
            return busLabels.get(node);
        }

        @Override
        public List<NodeDecorator> getNodeDecorators(Node node) {
            return new ArrayList<>();
        }
    }

}
