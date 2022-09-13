package com.misawabus.project.heartRate.fragments.daysFragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.device.entities.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.fragments.fragmentUtils.SetDataInViews;

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
            if(sleepDataUIList==null || sleepDataUIList.size()==0 ) {

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




        dashBoardViewModel.getTodayFullData5MinAvgAllIntervals().observe(getViewLifecycleOwner(), new Observer<>() {
            @Override
            public void onChanged(Map<String, DataFiveMinAvgDataContainer> dataFiveMinAVGContainers) {
                TodayFragment.this.stringDataFiveMinAVGAllIntervalsMap = dataFiveMinAVGContainers;
                SetDataInViews.setSportsValues(dataFiveMinAVGContainers, binding, getContext());

                DaysFragmentUtil.plotSports(dataFiveMinAVGContainers, TodayFragment.this.binding);
                DaysFragmentUtil.plotHeartRate(dataFiveMinAVGContainers, TodayFragment.this.binding, TodayFragment.this.getContext());
                DaysFragmentUtil.plotBloodPressure(dataFiveMinAVGContainers, TodayFragment.this.binding, TodayFragment.this.getContext());
                DaysFragmentUtil.plotSpO2(dataFiveMinAVGContainers, TodayFragment.this.binding, TodayFragment.this.getContext());
            }
        });


        dashBoardViewModel.getIsConnected().observe(getViewLifecycleOwner(), isDeviceConnected -> {
            if(isDeviceConnected) return;
            binding.refreshLayout.setRefreshing(false);
            binding.refreshLayout.setEnabled(false);
        });


        onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(dashBoardViewModel.getIsTodayFragmentRefreshing().getValue()!=null
                        && dashBoardViewModel.getIsTodayFragmentRefreshing().getValue()){
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

        if(dashBoardViewModel.getIsTodayFragmentRefreshing().getValue()!=null && dashBoardViewModel.getIsTodayFragmentRefreshing().getValue()){
            binding.refreshLayout.post(()-> binding.refreshLayout.setRefreshing(true));
        }



        dashBoardViewModel
                .getIsTodayFragmentRefreshing()
                .observe(getViewLifecycleOwner(),
                        aBoolean -> {
                            if(aBoolean) return;
                            binding.refreshLayout.setRefreshing(false);
                            binding.fragmentPlot.setEnabled(true);
                            binding.fragmentSleepPlot.setEnabled(true);
                            binding.fragmentBloodPressurePlot.setEnabled(true);
                        });

        dashBoardViewModel.getIsEnableFeatures().observe(getViewLifecycleOwner(), isEnabled -> {
            if(!isEnabled) return;
            binding.refreshLayout.setRefreshing(false);
            binding.refreshLayout.setEnabled(true);
        });


        addClickObserversToPlotsWidgets();

    }


    private void addClickObserversToPlotsWidgets() {
        binding.fragmentPlot.setOnClickListener(this::onClickCardFitnessArea);
        binding.fragmentStepsPlotCardView.setOnClickListener(this::onClickCardFitnessArea);

        binding.fragmentSleepPlot.setOnClickListener(this::onClickCardSleepArea);
        binding.fragmentSleepPlotCardView.setOnClickListener(this::onClickCardSleepArea);

        binding.fragmentBloodPressurePlot.setOnClickListener(this::onClickCardBloodPressureArea);
        binding.fragmentBloodPressureCardView.setOnClickListener(this::onClickCardBloodPressureArea);

        binding.fragmentRatePlot.setOnClickListener(this::onClickCardHeartRateArea);
        binding.fragmentRatePlotCardView.setOnClickListener(this::onClickCardHeartRateArea);

        binding.fragmentSop2Plot.setOnClickListener(this::onClickCardSop2Area);
        binding.fragmentSop2PlotCardView.setOnClickListener(this::onClickCardSop2Area);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {super.onResume();

        if(dashBoardViewModel.getIsTodayFragmentRefreshing().getValue()!=null
                && dashBoardViewModel.getIsTodayFragmentRefreshing().getValue()){
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