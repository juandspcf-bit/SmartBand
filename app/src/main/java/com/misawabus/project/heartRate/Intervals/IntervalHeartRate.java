package com.misawabus.project.heartRate.Intervals;

import com.veepoo.protocol.model.datas.TimeData;

public class IntervalHeartRate {
    private final int interval;
    private final int heartRate;
    private final TimeData timeData;


    public IntervalHeartRate(TimeData timeData, int heartRate, double intervalWidth){
        this.timeData = timeData;
        this.interval = IntervalUtils.getInterval(timeData, intervalWidth);
        this.heartRate = heartRate;

    }

    public int getInterval(){
        return this.interval;
    }
    public int getHeartRate(){return this.heartRate;}

    public TimeData getTimeData() {
        return timeData;
    }
}
