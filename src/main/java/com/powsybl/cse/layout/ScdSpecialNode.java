package com.powsybl.cse.layout;

import com.powsybl.sld.model.SwitchNode;
import com.powsybl.sld.model.VoltageLevelGraph;

public class ScdSpecialNode extends SwitchNode {
    public ScdSpecialNode(String id, String componentType, VoltageLevelGraph graph) {
        super(id, id, componentType, false, graph, SwitchKind.BREAKER, true);
    }

}
