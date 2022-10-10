package com.misawabus.project.heartRate.fragments.daysFragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.Observer;

import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.databinding.FragmentDataSummaryV2Binding;
import com.misawabus.project.heartRate.device.DataContainers.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.Sop2HData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.daysFragments.lifecycleObservers.TodayLifeCycleObserverOnRefreshing;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.fragments.fragmentUtils.SetDataInViews;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class TodayFragment extends DayFragment {
    public static final String TAG = DayFragment.class.getSimpleName();

    public TodayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new TodayLifeCycleObserverOnRefreshing());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dashBoardViewModel.getTodaySummary().observe(getViewLifecycleOwner(), new Observer<Map<String, Double>>() {
            @Override
            public void onChanged(Map<String, Double> doubleMap) {
                setSummaryViews(doubleMap);
            }
        });

        dashBoardViewModel.getTodayUpdateSleepFullData().observe(getViewLifecycleOwner(), sleepDataUIList -> {
            setDaySleepPlot(this, sleepDataUIList);
        });

        dashBoardViewModel.getTodayArrayTempAllIntervals().observe(getViewLifecycleOwner(), new Observer<Map<String, XYDataArraysForPlotting>>() {
            @Override
            public void onChanged(Map<String, XYDataArraysForPlotting> stringXYDataArraysForPlottingMap) {
                setTemperaturePlot(stringXYDataArraysForPlottingMap);
            }
        });

        dashBoardViewModel.getTodayArray5MinAvgAllIntervals().observe(getViewLifecycleOwner(), new Observer<Map<String, XYDataArraysForPlotting>>() {
            @Override
            public void onChanged(Map<String, XYDataArraysForPlotting> stringXYDataArraysForPlottingMap) {
                setAllPlots(stringXYDataArraysForPlottingMap);
            }
        });

        dashBoardViewModel.getTodaySummaryTitles().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
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

        dashBoardViewModel.getTodayFullData5MinAvgAllIntervals().observe(getViewLifecycleOwner(), new Observer<Map<String, DataFiveMinAvgDataContainer>>() {
            @Override
            public void onChanged(Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAvgDataContainerMap) {
                stringDataFiveMinAVGAllIntervalsMap = stringDataFiveMinAvgDataContainerMap;
            }
        });
        addClickObserversToPlotsWidgets(binding);

    }


    private void addClickObserversToPlotsWidgets(FragmentDataSummaryV2Binding binding) {
        if (binding == null) return;
        binding.fragmentPlot.setOnClickListener(this::onClickCardFitnessArea);
        binding.fragmentStepsPlotCardView.setOnClickListener(this::onClickCardFitnessArea);

        binding.fragmentSleepPlot.setOnClickListener(this::onClickCardSleepArea);
        binding.fragmentSleepPlotCardView.setOnClickListener(this::onClickCardSleepArea);

        binding.fragmentBloodPressurePlot.setOnClickListener(this::onClickCardBloodPressureArea);
        binding.fragmentBloodPressureCardView.setOnClickListener(this::onClickCardBloodPressureArea);

        binding.fragmentRatePlot.setOnClickListener(this::onClickCardHeartRateArea);
        this.binding.fragmentRatePlotCardView.setOnClickListener(this::onClickCardHeartRateArea);

        if (binding.fragmentSop2Plot != null) {
            binding.fragmentSop2Plot.setOnClickListener(this::onClickCardSop2Area);
        }
        if (binding.fragmentSop2PlotCardView != null) {
            binding.fragmentSop2PlotCardView.setOnClickListener(this::onClickCardSop2Area);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}