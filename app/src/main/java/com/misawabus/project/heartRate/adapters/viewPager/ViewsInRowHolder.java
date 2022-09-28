package com.misawabus.project.heartRate.adapters.viewPager;

import android.view.View;

import androidx.annotation.NonNull;


public interface ViewsInRowHolder {

    void fillViews(@NonNull View itemView);

    ViewsInRowHolder getInstance();

}
