package com.misawabus.project.heartRate.fragments.daysFragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.misawabus.project.heartRate.device.DataContainers.Temperature5MinDataContainer;

public class PastYesterdayFragment extends DayFragment {


    private static final String TAG = PastYesterdayFragment.class.getSimpleName();

    public PastYesterdayFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dashBoardViewModel.getPastYesterdayUpdateSleepFullData().observe(getViewLifecycleOwner(), sleepDataUIList -> setDaySleepPlot(this, sleepDataUIList));

        dashBoardViewModel.getPastYesterdaySummary().observe(getViewLifecycleOwner(), this::setSummaryViews);

        dashBoardViewModel.getPastYesterdaySummaryTitles().observe(getViewLifecycleOwner(), this::setSummaryTitlesInPlots);

        dashBoardViewModel
                .getPastYesterdayArrayTempAllIntervals()
                .observe(getViewLifecycleOwner(),
                        this::setTemperaturePlot);

        dashBoardViewModel.getPastYesterdayTempSummaryTitle().observe(getViewLifecycleOwner(), stringStringMap -> {
            String s = stringStringMap.get(Temperature5MinDataContainer.class.getSimpleName() + ":body");
            if(binding.tempSummaryTextView!=null){
                binding.tempSummaryTextView.setText(s);
            }
        });

        dashBoardViewModel
                .getPastYesterdayArray5MinAvgAllIntervals()
                .observe(getViewLifecycleOwner(),
                        this::setAllPlots);

        dashBoardViewModel
                .getPastYesterdayFullData5MinAvgAllIntervals()
                .observe(getViewLifecycleOwner(),
                        stringDataFiveMinAvgDataContainerMap -> stringDataFiveMinAVGAllIntervalsMap = stringDataFiveMinAvgDataContainerMap);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
