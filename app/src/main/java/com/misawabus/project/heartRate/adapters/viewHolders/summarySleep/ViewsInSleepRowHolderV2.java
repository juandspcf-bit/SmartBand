package com.misawabus.project.heartRate.adapters.viewHolders.summarySleep;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.androidplot.xy.XYPlot;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.adapters.recyclerView.ViewsInRowHolder;

public class ViewsInSleepRowHolderV2 implements com.misawabus.project.heartRate.adapters.viewPager.ViewsInRowHolder {
    public XYPlot xyPlot;

    @Override
    public void fillViews(@NonNull View itemView) {
        xyPlot = itemView.findViewById(R.id.sleepPlotRowV2);

    }

    @Override
    public ViewsInSleepRowHolderV2 getInstance() {
        return new ViewsInSleepRowHolderV2();
    }
}
