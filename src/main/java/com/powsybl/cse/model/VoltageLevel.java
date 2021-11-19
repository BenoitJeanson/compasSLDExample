package com.powsybl.cse.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VoltageLevel {
    private String name;
    private String desc;
    private int voltage;
    private int sxyX;
    private int sxyY;
    private List<Bay> bays = new ArrayList<>();
    Map<String, ConnectivityNode> connectivityNodes = new HashMap<>();

    public VoltageLevel(String name, String desc, int voltage, int sxyX, int sxyY) {
        this.name = name;
        this.desc = desc;
        this.voltage = voltage;
        this.sxyX = sxyX;
        this.sxyY = sxyY;
    }

    public void addBay(Bay bay) {
        bays.add(bay);
    }

    public void collectConnectivityNodes() {
        connectivityNodes = bays.stream().flatMap(Bay::getConnectivityNodesStream)
                .collect(Collectors.toMap(ConnectivityNode::getPathName, cn -> cn));
        bays.stream().flatMap(Bay::getConductingEquipmentStream).flatMap(ConductingEquipment::getTerminalsStream)
                .forEach(Terminal::updateConnectivityNode);
    }

    public ConnectivityNode gConnectivityNode(String pathName) {
        return connectivityNodes.get(pathName);
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getVoltage() {
        return voltage;
    }

    public int getSxyX() {
        return sxyX;
    }

    public int getSxyY() {
        return sxyY;
    }

    public List<Bay> getBays() {
        return bays;
    }

    public Collection<ConnectivityNode> gConnectivityNodes() {
        return connectivityNodes.values();
    }

}
