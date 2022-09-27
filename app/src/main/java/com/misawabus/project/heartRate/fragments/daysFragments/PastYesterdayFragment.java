package com.misawabus.project.heartRate.fragments.daysFragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;

import java.util.Map;

public class PastYesterdayFragment extends DayFragment {


    private static final String TAG = PastYesterdayFragment.class.getSimpleName();

    public PastYesterdayFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dashBoardViewModel.getPastYesterdayUpdateSleepFullData().observe(getViewLifecycleOwner(), sleepDataUIList -> {
            setDaySleepPlot(this, sleepDataUIList);
        });

        dashBoardViewModel.getPastYesterdaySummary().observe(getViewLifecycleOwner(), new Observer<Map<String, Double>>() {
            @Override
            public void onChanged(Map<String, Double> doubleMap) {
                setSummaryViews(doubleMap);
            }
        });

        dashBoardViewModel.getPastYesterdayArray5MinAvgAllIntervals().observe(getViewLifecycleOwner(), new Observer<Map<String, XYDataArraysForPlotting>>() {
            @Override
            public void onChanged(Map<String, XYDataArraysForPlotting> stringXYDataArraysForPlottingMap) {
                setAllPlots(stringXYDataArraysForPlottingMap);
            }
        });

        dashBoardViewModel.getPastYesterdayFullData5MinAvgAllIntervals().observe(getViewLifecycleOwner(), new Observer<Map<String, DataFiveMinAvgDataContainer>>() {
            @Override
            public void onChanged(Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAvgDataContainerMap) {
                stringDataFiveMinAVGAllIntervalsMap = stringDataFiveMinAvgDataContainerMap;
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
