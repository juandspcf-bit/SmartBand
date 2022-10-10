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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


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
            setDaySleepPlot(this, sleepDataUIList);
        });

        dashBoardViewModel.getYesterdayArrayTempAllIntervals().observe(getViewLifecycleOwner(), new Observer<Map<String, XYDataArraysForPlotting>>() {
            @Override
            public void onChanged(Map<String, XYDataArraysForPlotting> stringXYDataArraysForPlottingMap) {
                setTemperaturePlot(stringXYDataArraysForPlottingMap);
            }
        });

        dashBoardViewModel.getYesterdaySummaryTitles().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
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

       dashBoardViewModel.getYesterdayFullData5MinAvgAllIntervals().observe(getViewLifecycleOwner(), new Observer<>() {
            @Override
            public void onChanged(Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAvgDataContainerMap) {
                stringDataFiveMinAVGAllIntervalsMap = stringDataFiveMinAvgDataContainerMap;
            }
       });

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