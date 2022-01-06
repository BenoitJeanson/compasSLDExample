package com.powsybl.cse.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.powsybl.sld.library.ComponentTypeName;
import com.powsybl.sld.model.FeederNode;
import com.powsybl.sld.model.Node;
import com.powsybl.sld.model.VoltageLevelGraph;
import com.powsybl.sld.svg.DiagramLabelProvider;
import com.powsybl.sld.svg.FeederInfo;
import com.powsybl.sld.svg.LabelPosition;

public final class RawDiagramLabelProvider implements DiagramLabelProvider {
    private final Map<Node, List<NodeLabel>> busLabels;

    public RawDiagramLabelProvider(VoltageLevelGraph graph) {
        this.busLabels = new HashMap<>();
        LabelPosition labelPosition = new LabelPosition("default", 0, -5, true, 0);
        graph.getNodes().forEach(n -> {
            List<DiagramLabelProvider.NodeLabel> labels = new ArrayList<>();
            labels.add(new DiagramLabelProvider.NodeLabel(n.getName(), labelPosition));
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
