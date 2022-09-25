package com.misawabus.project.heartRate.fragments.daysFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.databinding.FragmentDataSummaryV2Binding;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.fragments.fragmentUtils.SetDataInViews;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class TodayFragment extends DayFragment {
    public static final String TAG = DayFragment.class.getSimpleName();


    private boolean isDeviceConnected = false;
    private Boolean isEnableGlobal;


    public TodayFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
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

        dashBoardViewModel.getIsConnected().observe(getViewLifecycleOwner(), isDeviceConnected -> {
            if (isDeviceConnected) {
                this.isDeviceConnected = true;
                binding.refreshLayout.setEnabled(true);
                binding.refreshLayout.setOnRefreshListener(() -> {
                    Log.d(TAG, "onRefresh: connect anyways");
                    dashBoardViewModel.getHealthsReadDataManager().getSmartWatchDataSingleDay(0);
                    dashBoardViewModel.setIsEnableFeatures(false);
                    dashBoardViewModel.setIsTodayFragmentRefreshing(true);
                    binding.fragmentPlot.setEnabled(false);
                    binding.fragmentSleepPlot.setEnabled(false);
                    binding.fragmentBloodPressurePlot.setEnabled(false);
                    binding.fragmentSleepPlot.setEnabled(false);
                });
                binding.refreshLayout.setRefreshing(false);
                handleIsEnabledFeatures();
                handleIsTodayRefreshing();
                return;
            }
            dashBoardViewModel.setIsEnableFeatures(false);
            dashBoardViewModel.setIsTodayFragmentRefreshing(false);
            this.isDeviceConnected = false;
            binding.refreshLayout.setRefreshing(false);
            binding.refreshLayout.setEnabled(false);
        });


    }

    private void handleIsEnabledFeatures() {
        Observer<Boolean> booleanObserver1 = isEnabled -> {
            isEnableGlobal = isEnabled;
            if (!isEnabled && dashBoardViewModel.getIsFetching() == View.VISIBLE) {
                binding.refreshLayout.setRefreshing(false);
                binding.refreshLayout.setEnabled(false);

            }


        };

        Observer<Boolean> value = dashBoardViewModel.getObserverEnabledMutableLiveData().getValue();
        if (value != null) {
            dashBoardViewModel.getIsEnableFeatures().removeObserver(value);
        }
        dashBoardViewModel.setObserverEnabledMutableLiveData(booleanObserver1);
        dashBoardViewModel.getIsEnableFeatures().observe(getViewLifecycleOwner(),
                booleanObserver1);
    }

    private void handleIsTodayRefreshing() {
        Observer<Boolean> booleanObserver = isTodayFragmentRefreshing -> {

            Boolean isEnabled = dashBoardViewModel.getIsEnableFeatures().getValue();

            if (isTodayFragmentRefreshing && (isEnabled!=null && !isEnabled)) {
                Log.d(TAG, "onChanged: dashBoardViewModel.getIsTodayFragmentRefreshing()   if (isDeviceConnected && isTodayFragmentRefreshing) ");
                binding.refreshLayout.setRefreshing(true);
                binding.fragmentPlot.setEnabled(false);

                binding.fragmentSleepPlot.setEnabled(false);
                binding.fragmentBloodPressurePlot.setEnabled(false);
                return;
            }


            binding.fragmentPlot.setEnabled(true);
            binding.fragmentSleepPlot.setEnabled(true);
            binding.fragmentBloodPressurePlot.setEnabled(true);

            binding.refreshLayout.setEnabled(true);
            binding.refreshLayout.setRefreshing(false);

                Log.d(TAG, "onViewCreated: dashBoardViewModel.getIsTodayFragmentRefreshing()   refreshing finished: ");

        };

        Observer<Boolean> value = dashBoardViewModel.getObserverMutableLiveData().getValue();
        if (value != null) {
            dashBoardViewModel.getIsTodayFragmentRefreshing().removeObserver(value);
        }
        dashBoardViewModel.setObserverMutableLiveData(booleanObserver);
        dashBoardViewModel.getIsTodayFragmentRefreshing().observe(getViewLifecycleOwner(),
                booleanObserver);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }


}