package com.powsybl.cse.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ConductingEquipment {
    CEType ceType;
    private String name;
    private int sxyX;
    private int sxyY;
    List<Terminal> terminals = new ArrayList<>();

    public ConductingEquipment(String strType, String name, int sxyX, int sxyY) {
        this.ceType = CEType.getCEType(strType);
        this.name = name;
        this.sxyX = sxyX;
        this.sxyY = sxyY;
    }

    public void addTerminal(Terminal terminal) {
        terminals.add(terminal);
    }

    public Stream<Terminal> getTerminalsStream() {
        return terminals.stream();
    }

    public CEType getCeType() {
        return ceType;
    }

    public String getName() {
        return name;
    }

    public int getSxyX() {
        return sxyX;
    }

    public int getSxyY() {
        return sxyY;
    }

}
