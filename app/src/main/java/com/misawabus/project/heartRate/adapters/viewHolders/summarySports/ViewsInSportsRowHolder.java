package com.misawabus.project.heartRate.adapters.viewHolders.summarySports;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.adapters.recyclerView.ViewsInRowHolder;

public class ViewsInSportsRowHolder implements ViewsInRowHolder {

    public TextView intervalTextViewRow;
    public TextView stepsTextViewRow;



    public ViewsInSportsRowHolder(){

    }

    public void fillViews(@NonNull View itemView){

        intervalTextViewRow = itemView.findViewById(R.id.intervalTextViewRow);
        stepsTextViewRow = itemView.findViewById(R.id.stepsTextViewRow);

    }

    public ViewsInRowHolder getInstance(){
        return new ViewsInSportsRowHolder();
    }

}
