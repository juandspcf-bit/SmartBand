package com.misawabus.project.heartRate.fragments.daysFragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.misawabus.project.heartRate.databinding.FragmentDataSummaryV2Binding;
import com.misawabus.project.heartRate.device.DataContainers.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.Sop2HData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.daysFragments.lifecycleObservers.TodayLifeCycleObserverOnRefreshing;


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

        dashBoardViewModel.getTodaySummary().observe(getViewLifecycleOwner(), this::setSummaryViews);

        dashBoardViewModel.getTodayUpdateSleepFullData().observe(getViewLifecycleOwner(), sleepDataUIList -> setDaySleepPlot(this, sleepDataUIList));

        dashBoardViewModel.getTodayArrayTempAllIntervals().observe(getViewLifecycleOwner(), this::setTemperaturePlot);

        dashBoardViewModel.getTodayArray5MinAvgAllIntervals().observe(getViewLifecycleOwner(), this::setAllPlots);

        dashBoardViewModel.getTodaySummaryTitles().observe(getViewLifecycleOwner(), stringStringMap -> {
            String s = stringStringMap.get(SportsData5MinAvgDataContainer.class.getSimpleName());
            String s1 = stringStringMap.get(HeartRateData5MinAvgDataContainer.class.getSimpleName());
            String s2 = stringStringMap.get(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName() + "High");
            String s3 = stringStringMap.get(Sop2HData5MinAvgDataContainer.class.getSimpleName());

            if(binding.stepsSummaryTextView!=null){
                binding.stepsSummaryTextView.setText(s);
            }

            if(binding.heartRateSummaryTextView!=null){
                binding.heartRateSummaryTextView.setText(s1);
            }

            if(binding.bpSummaryTextView!=null){
                binding.bpSummaryTextView.setText(s2);
            }

            if(binding.spo2SummaryTextView!=null){
                binding.spo2SummaryTextView.setText(s3);
            }




        });

        dashBoardViewModel.getTodayFullData5MinAvgAllIntervals().observe(getViewLifecycleOwner(), stringDataFiveMinAvgDataContainerMap -> stringDataFiveMinAVGAllIntervalsMap = stringDataFiveMinAvgDataContainerMap);
        addClickObserversToPlotsWidgets(binding);

    }


    private void addClickObserversToPlotsWidgets(FragmentDataSummaryV2Binding binding) {
        if (binding == null) return;
        binding.fragmentPlot.setOnClickListener(view1 -> onClickCardFitnessArea());
        binding.fragmentStepsPlotCardView.setOnClickListener(view -> onClickCardFitnessArea());

        binding.fragmentSleepPlot.setOnClickListener(view1 -> onClickCardSleepArea());
        binding.fragmentSleepPlotCardView.setOnClickListener(view -> onClickCardSleepArea());

        binding.fragmentBloodPressurePlot.setOnClickListener(view1 -> onClickCardBloodPressureArea());
        binding.fragmentBloodPressureCardView.setOnClickListener(view -> onClickCardBloodPressureArea());

        binding.fragmentRatePlot.setOnClickListener(view1 -> onClickCardHeartRateArea());
        this.binding.fragmentRatePlotCardView.setOnClickListener(view -> onClickCardHeartRateArea());

        if (binding.fragmentSop2Plot != null) {
            binding.fragmentSop2Plot.setOnClickListener(view -> onClickCardSop2Area());
        }
        if (binding.fragmentSop2PlotCardView != null) {
            binding.fragmentSop2PlotCardView.setOnClickListener(view -> onClickCardSop2Area());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}