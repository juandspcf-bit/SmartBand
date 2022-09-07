package com.misawabus.project.heartRate.Intervals;


import com.veepoo.protocol.model.datas.TimeData;

public class IntervalSteps {
    private final int interval;
    private final int stepValue;



    public IntervalSteps(TimeData timeData, int stepValue, double intervalWidth){
        this.interval = IntervalUtils.getInterval(timeData, intervalWidth);
        this.stepValue = stepValue;
    }

    public int getInterval(){
        return this.interval;
    }
    public int getStepValue(){return this.stepValue;}

}
