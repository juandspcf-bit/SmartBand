package com.misawabus.project.heartRate.device.DataContainers;

import java.util.HashMap;
import java.util.Map;

public class Sop2HData5MinAvgDataContainer implements DataFiveMinAvgDataContainer {
    private final Map<Integer, Map<String, Double>> doubleMap = new HashMap<>();
    private String stringDate;

    public Sop2HData5MinAvgDataContainer() {
    }

    public String getStringDate() {
        return stringDate;
    }

    public void setStringDate(String stringDate) {
        this.stringDate = stringDate;
    }

    public Map<Integer, Map<String, Double>> getDoubleMap() {
        return doubleMap;
    }

}
