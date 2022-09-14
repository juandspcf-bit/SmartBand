package com.misawabus.project.heartRate.device.DataContainers;

import java.util.Map;

public interface DataFiveMinAvgDataContainer {
    String getStringDate();

    void setStringDate(String stringDate);

    Map<Integer, Map<String, Double>> getDoubleMap();
}
