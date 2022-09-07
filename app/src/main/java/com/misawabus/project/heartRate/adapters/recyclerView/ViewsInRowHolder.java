package com.misawabus.project.heartRate.adapters.recyclerView;

import android.view.View;

import androidx.annotation.NonNull;


public interface ViewsInRowHolder {

    void fillViews(@NonNull View itemView);

    ViewsInRowHolder getInstance();

}
