package com.misawabus.project.heartRate.device.DataContainers;

import java.util.HashMap;
import java.util.Map;

public class BloodPressureDataFiveMinAvgDataContainer implements DataFiveMinAvgDataContainer {
    private final Map<Integer, Map<String, Double>> doubleMap = new HashMap<>();
    private String stringDate;

    public BloodPressureDataFiveMinAvgDataContainer(){
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
