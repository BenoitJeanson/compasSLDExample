package com.powsybl.cse.model;

public class Terminal {
    VoltageLevel voltageLevel;
    String name;
    String cNodeName;
    String connectivityNodePathName;
    ConnectivityNode connectivityNode;

    public Terminal(VoltageLevel voltageLevel, String name, String connectivityNodePathName, String cNodeName) {
        this.voltageLevel = voltageLevel;
        this.name = name;
        this.cNodeName = cNodeName;
        this.connectivityNodePathName = connectivityNodePathName;
    }

    public void updateConnectivityNode() {
        connectivityNode = voltageLevel.gConnectivityNode(connectivityNodePathName);
    }

    public VoltageLevel getVoltageLevel() {
        return voltageLevel;
    }

    public String getName() {
        return name;
    }

    public String getConnectivityNodePathName() {
        return connectivityNodePathName;
    }

    public ConnectivityNode getConnectivityNode() {
        return connectivityNode;
    }

    public String getcNodeName() {
        return cNodeName;
    }

}
