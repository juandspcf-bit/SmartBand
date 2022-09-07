package com.misawabus.project.heartRate.device.entities;

import java.util.HashMap;
import java.util.Map;

public class SportsData5MinAvgDataContainer implements DataFiveMinAvgDataContainer {
    private final Map<Integer, Map<String, Double>> doubleMap = new HashMap<>();
    private String stringDate;

    public SportsData5MinAvgDataContainer() {
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
