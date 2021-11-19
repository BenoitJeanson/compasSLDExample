package com.powsybl.cse.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Substation {
    private String name;
    private String desc;
    private List<VoltageLevel> voltageLevels = new ArrayList<>();

    public Substation(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public void addVoltageLevel(VoltageLevel voltageLevel) {
        voltageLevels.add(voltageLevel);
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Stream<VoltageLevel> getVoltageLevels() {
        return voltageLevels.stream();
    }

}
