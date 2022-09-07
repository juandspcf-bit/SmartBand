package com.misawabus.project.heartRate.device.entities;

import java.util.Map;

public interface DataFiveMinAvgDataContainer {
    String getStringDate();

    void setStringDate(String stringDate);

    Map<Integer, Map<String, Double>> getDoubleMap();
}
