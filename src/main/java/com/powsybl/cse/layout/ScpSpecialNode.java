package com.powsybl.cse.layout;

import com.powsybl.sld.model.FictitiousNode;
import com.powsybl.sld.model.VoltageLevelGraph;

public class ScpSpecialNode extends FictitiousNode {
    public ScpSpecialNode(String id, String componentType, VoltageLevelGraph graph) {
        super(id, id, null, componentType, graph);
    }

}
