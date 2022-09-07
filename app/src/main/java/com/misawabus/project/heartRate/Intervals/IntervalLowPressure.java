package com.misawabus.project.heartRate.Intervals;

import com.veepoo.protocol.model.datas.TimeData;

public class IntervalLowPressure {
    private final int interval;
    private final int lowPressure;



    public IntervalLowPressure(TimeData timeData, int lowPressure, double intervalWidth){
        this.interval = IntervalUtils.getInterval(timeData, intervalWidth);
        this.lowPressure = lowPressure;
    }

    public int getInterval(){
        return this.interval;
    }
    public int getLowPressure(){return this.lowPressure;}
}
