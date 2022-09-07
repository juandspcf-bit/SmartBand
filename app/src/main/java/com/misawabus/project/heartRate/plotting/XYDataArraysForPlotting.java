package com.misawabus.project.heartRate.plotting;

public class XYDataArraysForPlotting {
    String[] periodIntervalsArray;
    Double[] seriesDoubleAVR;

    public XYDataArraysForPlotting(String[] periodIntervalsArray, Double[] seriesDoubleAVR) {
        this.periodIntervalsArray = periodIntervalsArray;
        this.seriesDoubleAVR = seriesDoubleAVR;
    }

    public String[] getPeriodIntervalsArray() {
        return periodIntervalsArray;
    }

    public void setPeriodIntervalsArray(String[] periodIntervalsArray) {
        this.periodIntervalsArray = periodIntervalsArray;
    }

    public Double[] getSeriesDoubleAVR() {
        return seriesDoubleAVR;
    }

    public void setSeriesDoubleAVR(Double[] seriesDoubleAVR) {
        this.seriesDoubleAVR = seriesDoubleAVR;
    }
}
