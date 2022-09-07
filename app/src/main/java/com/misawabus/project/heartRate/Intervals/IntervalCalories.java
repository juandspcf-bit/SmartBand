package com.misawabus.project.heartRate.Intervals;

import com.veepoo.protocol.model.datas.TimeData;

public class IntervalCalories {
    private final int interval;
    private final double caloriesValue;



    public IntervalCalories(TimeData timeData, double caloriesValue, double intervalWidth){
        this.interval = IntervalUtils.getInterval(timeData, intervalWidth);
        this.caloriesValue = caloriesValue;
    }

    public int getInterval(){
        return this.interval;
    }
    public double getCaloriesValue(){return this.caloriesValue;}
}
