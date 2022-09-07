package com.misawabus.project.heartRate.adapters.viewHolders.summaryBP;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.adapters.recyclerView.ViewsInRowHolder;

public class ViewsInBPRowHolder implements ViewsInRowHolder {
    public TextView intervalTextViewRowBP;
    public TextView bpTextViewRow;
    public TextView scoreBP;
    public ImageView imageViewBP;


    @Override
    public void fillViews(@NonNull View itemView) {
        intervalTextViewRowBP = itemView.findViewById(R.id.intervalTextViewRowBP);
        bpTextViewRow = itemView.findViewById(R.id.bpTextViewRow);
        scoreBP = itemView.findViewById(R.id.scoreBP);
        imageViewBP = itemView.findViewById(R.id.rowImageViewBP);

    }

    @Override
    public ViewsInRowHolder getInstance() {
        return new ViewsInBPRowHolder();
    }
}
