package com.misawabus.project.heartRate.Intervals;

import com.veepoo.protocol.model.datas.TimeData;

public class IntervalHighPressure {
    private final int interval;
    private final int HighPressure;



    public IntervalHighPressure(TimeData timeData, int HighPressure, double intervalWidth){
        this.interval = IntervalUtils.getInterval(timeData, intervalWidth);
        this.HighPressure = HighPressure;
    }

    public int getInterval(){
        return this.interval;
    }
    public int getHighPressure(){return this.HighPressure;}
}
