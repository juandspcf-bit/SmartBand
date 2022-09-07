package com.misawabus.project.heartRate.Intervals;

import com.veepoo.protocol.model.datas.TimeData;

public class IntervalDistance {
    private final int interval;
    private final double distanceValue;



    public IntervalDistance(TimeData timeData, double distanceValue, double intervalWidth){
        this.interval = IntervalUtils.getInterval(timeData, intervalWidth);
        this.distanceValue = distanceValue;
    }

    public int getInterval(){
        return this.interval;
    }
    public double getDistanceValue(){return this.distanceValue;}
}
