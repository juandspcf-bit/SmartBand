package com.misawabus.project.heartRate.fragments.daysFragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.misawabus.project.heartRate.device.DataContainers.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.Sop2HData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.fragments.fragmentUtils.SetDataInViews;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class YesterdayFragment extends DayFragment {


    private static final String TAG = DayFragment.class.getSimpleName();

    public YesterdayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(binding.refreshLayout).setEnabled(false);

        binding.fragmentPlot.setVisibility(View.GONE);


        dashBoardViewModel.getYesterdayUpdateSleepFullData().observe(getViewLifecycleOwner(), sleepDataUIList -> {
            if(sleepDataUIList==null || sleepDataUIList.size()==0) return;
            sleepDataList = sleepDataUIList;
            SleepDataUI sleepDataUI = sleepDataUIList.get(0);
            Map<String, List<Integer>> sleepData = FragmentUtil.getSleepDataForPlotting(sleepDataUI.getData());
            SetDataInViews.setSleepValues(sleepData.get("lightSleep"),
                    sleepData.get("deepSleep"),
                    sleepData.get("wakeUp"),
                    binding.fragmentSleepPlot,
                    sleepDataUI,
                    binding);
        });

/*        dashBoardViewModel.getYesterdayFullData5MinAvgAllIntervals().observe(getViewLifecycleOwner(), new Observer<>() {
            @Override
            public void onChanged(Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAVGAllIntervalsMap) {
                YesterdayFragment.this.stringDataFiveMinAVGAllIntervalsMap = stringDataFiveMinAVGAllIntervalsMap;
                //SetDataInViews.setSportsValues(stringDataFiveMinAVGAllIntervalsMap, binding);
                DaysFragmentUtil.plotSports(stringDataFiveMinAVGAllIntervalsMap, binding);
                DaysFragmentUtil.plotHeartRate(stringDataFiveMinAVGAllIntervalsMap, binding, getContext());
                DaysFragmentUtil.plotBloodPressure(stringDataFiveMinAVGAllIntervalsMap, binding, getContext());
                DaysFragmentUtil.plotSpO2(stringDataFiveMinAVGAllIntervalsMap, binding, getContext());
            }
        });*/

        dashBoardViewModel.getYesterdaySummary().observe(getViewLifecycleOwner(), new Observer<Map<String, Double>>() {
            @Override
            public void onChanged(Map<String, Double> doubleMap) {
                setSummaryViews(doubleMap);
            }
        });

        dashBoardViewModel.getYesterdayArray5MinAvgAllIntervals().observe(getViewLifecycleOwner(), new Observer<Map<String, XYDataArraysForPlotting>>() {
            @Override
            public void onChanged(Map<String, XYDataArraysForPlotting> stringXYDataArraysForPlottingMap) {
                setAllPlots(stringXYDataArraysForPlottingMap);

            }
        });



    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}