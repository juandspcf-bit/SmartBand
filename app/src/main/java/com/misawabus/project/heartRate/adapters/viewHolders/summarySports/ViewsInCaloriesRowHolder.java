package com.misawabus.project.heartRate.adapters.viewHolders.summarySports;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.adapters.recyclerView.ViewsInRowHolder;

public class ViewsInCaloriesRowHolder implements ViewsInRowHolder {
    public TextView intervalTextViewRow;
    public TextView caloriesTextViewRow;
    @Override
    public void fillViews(@NonNull View itemView) {
        intervalTextViewRow = itemView.findViewById(R.id.intervalTextViewRowC);
        caloriesTextViewRow = itemView.findViewById(R.id.distancesTextViewRow);

    }

    public ViewsInRowHolder getInstance(){
        return new ViewsInCaloriesRowHolder();
    }


}
