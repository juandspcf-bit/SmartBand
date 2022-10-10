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

        dashBoardViewModel.getPastYesterdaySummaryTitles().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> stringStringMap) {
                String s = stringStringMap.get(SportsData5MinAvgDataContainer.class.getSimpleName());
                String s1 = stringStringMap.get(HeartRateData5MinAvgDataContainer.class.getSimpleName());
                String s2 = stringStringMap.get(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName() + "High");
                String s3 = stringStringMap.get(Sop2HData5MinAvgDataContainer.class.getSimpleName());

                binding.stepsSummaryTextView.setText(s);
                binding.heartRateSummaryTextView.setText(s1);
                binding.bpSummaryTextView.setText(s2);
                binding.spo2SummaryTextView.setText(s3);

            }
        });

        dashBoardViewModel.getPastYesterdayArrayTempAllIntervals().observe(getViewLifecycleOwner(), new Observer<Map<String, XYDataArraysForPlotting>>() {
            @Override
            public void onChanged(Map<String, XYDataArraysForPlotting> stringXYDataArraysForPlottingMap) {
                setTemperaturePlot(stringXYDataArraysForPlottingMap);
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
