package com.misawabus.project.heartRate.adapters.viewHolders.summarySleep;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.androidplot.xy.XYPlot;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.adapters.recyclerView.ViewsInRowHolder;

public class ViewsInSleepRowHolder implements ViewsInRowHolder {
    public XYPlot plot;
    public TextView allSleepTime;
    public TextView wakeCount;
    public TextView sleepDown;
    public TextView sleepUp;
    public TextView deepSleepTime;
    public TextView lowSleepTime;
    public RatingBar ratingBarSleepQuality;

    @Override
    public void fillViews(@NonNull View itemView) {
        plot = itemView.findViewById(R.id.sleepPlotRow);
        ratingBarSleepQuality = itemView.findViewById(R.id.ratingBarSleepQuality);
        allSleepTime = itemView.findViewById(R.id.allSleepTime);
        wakeCount = itemView.findViewById(R.id.wakeCount);
        sleepDown = itemView.findViewById(R.id.sleepDown);
        sleepUp = itemView.findViewById(R.id.sleepUp);
        deepSleepTime = itemView.findViewById(R.id.deepSleepTime);
        lowSleepTime = itemView.findViewById(R.id.lowSleepTime);
    }

    @Override
    public ViewsInRowHolder getInstance() {
        return new ViewsInSleepRowHolder();
    }
}
