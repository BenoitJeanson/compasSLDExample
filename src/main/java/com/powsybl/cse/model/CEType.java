package com.powsybl.cse.model;

public enum CEType {
    DIS("DIS"), CBR("CBR"), CTR("CTR"), VTR("VTR");

    String typeName;

    CEType(String typeName) {
        this.typeName = typeName;
    }

    static CEType getCEType(String strType) {
        switch (strType) {
            case "DIS":
                return DIS;
            case "CBR":
                return CBR;
            case "CTR":
                return CTR;
            case "VTR":
                return VTR;
        }
        return null;
    }
}
