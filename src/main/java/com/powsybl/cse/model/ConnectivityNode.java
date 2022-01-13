package com.powsybl.cse.model;

public class ConnectivityNode {
    String pathName;
    String name;
    Bay parentBay;

    public ConnectivityNode(String pathName, String name, Bay parentBay) {
        this.pathName = pathName;
        this.name = name;
        this.parentBay = parentBay;
    }

    public String getPathName() {
        return pathName;
    }

    public Bay getParentBay() {
        return parentBay;
    }

}
