package com.misawabus.project.heartRate.adapters.viewHolders.summaryHR;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.adapters.recyclerView.ViewsInRowHolder;

public class ViewsInHRRowHolder implements ViewsInRowHolder {
    public TextView intervalHR;
    public TextView valueHR;
    public TextView hrZoneTextView;
    public ProgressBar hrZoneProgressBar;


    @Override
    public void fillViews(@NonNull View itemView) {
        intervalHR = itemView.findViewById(R.id.intervalHR);
        valueHR = itemView.findViewById(R.id.valueHR);
        hrZoneProgressBar = itemView.findViewById(R.id.HRZoneProgressBar);
        hrZoneTextView = itemView.findViewById(R.id.HRZoneTextView);

    }

    @Override
    public ViewsInRowHolder getInstance() {
        return new ViewsInHRRowHolder();
    }
}