package com.misawabus.project.heartRate.fragments.daysFragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.databinding.FragmentDataSummaryV2Binding;
import com.misawabus.project.heartRate.device.DataContainers.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.Sop2HData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.fragments.fragmentUtils.SetDataInViews;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class TodayFragment extends DayFragment {
    public static final String TAG = DayFragment.class.getSimpleName();
    SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    public TodayFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dashBoardViewModel.getTodayUpdateSleepFullData().observe(getViewLifecycleOwner(), sleepDataUIList -> {
            if (sleepDataUIList == null || sleepDataUIList.size() == 0) {

                return;
            }
            sleepDataList = sleepDataUIList;
            Optional<SleepDataUI> max = sleepDataUIList.stream().
                    max(Comparator.comparingInt(SleepDataUI::getAllSleepTime));
            SleepDataUI sleepDataUI = max.orElse(sleepDataUIList.get(0));
            Map<String, List<Integer>> sleepData = FragmentUtil.getSleepDataForPlotting(sleepDataUI.getData());
            SetDataInViews.setSleepValues(sleepData.get("lightSleep"),
                    sleepData.get("deepSleep"),
                    sleepData.get("wakeUp"),
                    binding.fragmentSleepPlot,
                    sleepDataUI,
                    binding);
        });


        dashBoardViewModel.getTodaySummary().observe(getViewLifecycleOwner(), new Observer<Map<String, Double>>() {
            @Override
            public void onChanged(Map<String, Double> doubleMap) {
                setSummaryViews(doubleMap);
            }
        });


        dashBoardViewModel.getTodayArray5MinAvgAllIntervals().observe(getViewLifecycleOwner(), new Observer<Map<String, XYDataArraysForPlotting>>() {
            @Override
            public void onChanged(Map<String, XYDataArraysForPlotting> stringXYDataArraysForPlottingMap) {
                setAllPlots(stringXYDataArraysForPlottingMap);

            }
        });


        dashBoardViewModel.getIsConnected().observe(getViewLifecycleOwner(), isDeviceConnected -> {
            if (isDeviceConnected) return;
            binding.refreshLayout.setRefreshing(false);
            binding.refreshLayout.setEnabled(false);
        });


        onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (dashBoardViewModel.getIsTodayFragmentRefreshing().getValue() != null
                        && dashBoardViewModel.getIsTodayFragmentRefreshing().getValue()) {
                    return;
                }
                dashBoardViewModel.getHealthsReadDataManager().getSmartWatchDataSingleDay(0);
                dashBoardViewModel.setIsTodayFragmentRefreshing(true);
                binding.fragmentPlot.setEnabled(false);
                binding.fragmentSleepPlot.setEnabled(false);
                binding.fragmentBloodPressurePlot.setEnabled(false);
                binding.fragmentSleepPlot.setEnabled(false);

            }
        };
        binding.refreshLayout.setOnRefreshListener(onRefreshListener);

        if (dashBoardViewModel.getIsTodayFragmentRefreshing().getValue() != null && dashBoardViewModel.getIsTodayFragmentRefreshing().getValue()) {
            binding.refreshLayout.post(() -> binding.refreshLayout.setRefreshing(true));
        }


        dashBoardViewModel
                .getIsTodayFragmentRefreshing()
                .observe(getViewLifecycleOwner(),
                        aBoolean -> {
                            if (aBoolean) return;
                            binding.refreshLayout.setRefreshing(false);
                            binding.fragmentPlot.setEnabled(true);
                            binding.fragmentSleepPlot.setEnabled(true);
                            binding.fragmentBloodPressurePlot.setEnabled(true);
                        });

        dashBoardViewModel.getIsEnableFeatures().observe(getViewLifecycleOwner(), isEnabled -> {
            if (!isEnabled) return;
            binding.refreshLayout.setRefreshing(false);
            binding.refreshLayout.setEnabled(true);
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (dashBoardViewModel.getIsTodayFragmentRefreshing().getValue() != null
                && dashBoardViewModel.getIsTodayFragmentRefreshing().getValue()) {
            binding.refreshLayout.setRefreshing(false);
            binding.refreshLayout.setRefreshing(true);
            onRefreshListener.onRefresh();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}