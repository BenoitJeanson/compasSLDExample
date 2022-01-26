package com.powsybl.cse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.powsybl.cse.layout.RawDiagramLabelProvider;
import com.powsybl.cse.layout.ScdGraphBuilder;
import com.powsybl.cse.layout.ScdGraphBuilder.VoltageLevelBuilder;
import com.powsybl.cse.model.CEType;
import com.powsybl.cse.model.Substation;
import com.powsybl.cse.model.Terminal;
import com.powsybl.cse.model.VoltageLevel;
import com.powsybl.sld.layout.BlockOrganizer;
import com.powsybl.sld.layout.ImplicitCellDetector;
import com.powsybl.sld.layout.LayoutParameters;
import com.powsybl.sld.layout.PositionFinder;
import com.powsybl.sld.layout.PositionVoltageLevelLayout;
import com.powsybl.sld.layout.positionbyclustering.PositionByClustering;
import com.powsybl.sld.layout.positionfromextension.PositionFromExtension;
import com.powsybl.sld.library.ResourcesComponentLibrary;
import com.powsybl.sld.model.Node;
import com.powsybl.sld.model.VoltageLevelGraph;
import com.powsybl.sld.model.BusCell.Direction;
import com.powsybl.sld.model.SwitchNode.SwitchKind;
import com.powsybl.sld.svg.BasicStyleProvider;
import com.powsybl.sld.svg.DefaultSVGWriter;

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

        vlBuilder = new ScdGraphBuilder().createVoltageLevelBuilder(vl.getName(), vl.getVoltage());

        vl.gConnectivityNodes().forEach(cn -> {
            String pathName = cn.getPathName();
            if (pathName.contains("BusBar")) {
                path2Node.put(pathName, vlBuilder.createBusBarSection(pathName, cn.getParentBay().getSxyY(),
                        cn.getParentBay().getSxyX()));
            } else {
                path2Node.put(pathName, vlBuilder.createFictitiousNode(pathName));
            }
        });

        vl.getBays().forEach(bay -> bay.getConductingEquipmentStream().forEach(ce -> {
            List<Terminal> terminals = ce.getTerminalsStream().collect(Collectors.toList());
            Node node;
            String pathName = ce.getName();
            if (ce.getCeType() == CEType.DIS) {
                node = vlBuilder.createSwitchNode(SwitchKind.DISCONNECTOR, bay.getName() + " " + pathName, false,
                        false);
            } else if (ce.getCeType() == CEType.CBR) {
                node = vlBuilder.createSwitchNode(SwitchKind.BREAKER, pathName, false, false, bay.getSxyX(),
                        Direction.TOP);
            } else if (ce.getCeType() == CEType.VTR || ce.getCeType() == CEType.CTR) {
                node = vlBuilder.createScpSpecialNode(pathName, ce.getCeType().getTypeName());
            } else {
                node = vlBuilder.createFictitiousNode(pathName);
            }

            int x = bay.getSxyX();

            Node node1 = terminalToNode(terminals.get(0), x);
            Node node2 = null;
            double termNb = terminals.size();
            if (termNb == 1) {
                node2 = vlBuilder.createLoad(pathName, x, Direction.TOP);
            } else if (termNb == 2) {
                node2 = terminalToNode(terminals.get(1), x);
            }
            vlBuilder.connectNode(node, node1);
            vlBuilder.connectNode(node, node2);
        }));

    }

    private Node terminalToNode(Terminal terminal, int x) {
        String pathName = terminal.getConnectivityNodePathName();
        if (pathName != null) {
            return path2Node.get(pathName);
        }
        return vlBuilder.createLoad(terminal.getcNodeName(), x, Direction.TOP);
    }

    public void computeAndWriteResults(String fileName, boolean stacked, boolean semiAutomaticPlacement) {
        VoltageLevelGraph graph = vlBuilder.getGraph();
        LayoutParameters layoutParameters = new LayoutParameters().setAdaptCellHeightToContent(true)
                .setCssLocation(LayoutParameters.CssLocation.INSERTED_IN_SVG).setShowInternalNodes(true);

        PositionFinder pf = semiAutomaticPlacement ? new PositionFromExtension() : new PositionByClustering();

        new ImplicitCellDetector().detectCells(graph);
        new BlockOrganizer(pf, stacked).organize(graph);
        new PositionVoltageLevelLayout(graph).run(layoutParameters);

        StringWriter writer = new StringWriter();
        graph.writeJson(writer);
        writeFile(fileName + ".json", writer);

        writer = new StringWriter();
        DefaultSVGWriter svgWriter = new DefaultSVGWriter(
                new ResourcesComponentLibrary("SCD", "/ConvergenceLibrary", "/ScdComponentsLibrary"), layoutParameters);
        svgWriter.write("", graph, new RawDiagramLabelProvider(graph), new BasicStyleProvider(), writer);
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
}
