package com.powsybl.cse.model;

public class ConnectivityNode {
    String pathName;
    String name;

    public ConnectivityNode(String pathName, String name) {
        this.pathName = pathName;
        this.name = name;
    }

    public String getPathName() {
        return pathName;
    }

}
