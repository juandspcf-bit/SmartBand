package com.misawabus.project.heartRate.adapters.viewHolders.summarySports;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.adapters.recyclerView.ViewsInRowHolder;

public class ViewsInDistanceRowHolder implements ViewsInRowHolder {
    public TextView intervalTextViewRow;
    public TextView distanceTextViewRow;
    @Override
    public void fillViews(@NonNull View itemView) {
        intervalTextViewRow = itemView.findViewById(R.id.intervalTextViewRowD);
        distanceTextViewRow = itemView.findViewById(R.id.distancesTextViewRow);
    }

    @Override
    public ViewsInRowHolder getInstance() {
        return new ViewsInDistanceRowHolder();
    }
}
