package com.powsybl.cse.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Bay {
    private String name;
    private String desc;
    private int sxyX;
    private int sxyY;
    private List<ConnectivityNode> connectivityNodes = new ArrayList<>();
    private List<ConductingEquipment> conductingEquipments = new ArrayList<>();

    public Bay(String name, String desc, int sxyX, int sxyY) {
        this.name = name;
        this.desc = desc;
        this.sxyX = sxyX;
        this.sxyY = sxyY;
    }

    public void addConnectivityNode(ConnectivityNode connectivityNode) {
        connectivityNodes.add(connectivityNode);
    }

    public void addConductingEquipments(ConductingEquipment conductingEquipment) {
        conductingEquipments.add(conductingEquipment);
    }

    public Stream<ConnectivityNode> getConnectivityNodesStream() {
        return connectivityNodes.stream();
    }

    public Stream<ConductingEquipment> getConductingEquipmentStream() {
        return conductingEquipments.stream();
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

}
