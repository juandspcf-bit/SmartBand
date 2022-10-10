package com.misawabus.project.heartRate.fragments.daysFragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.misawabus.project.heartRate.device.DataContainers.Temperature5MinDataContainer;

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


        dashBoardViewModel.getYesterdayUpdateSleepFullData().observe(getViewLifecycleOwner(), sleepDataUIList -> setDaySleepPlot(this, sleepDataUIList));

        dashBoardViewModel.getYesterdayArrayTempAllIntervals().observe(getViewLifecycleOwner(), this::setTemperaturePlot);

        dashBoardViewModel.getYesterdayTempSummaryTitle().observe(getViewLifecycleOwner(), stringStringMap -> {
            String s = stringStringMap.get(Temperature5MinDataContainer.class.getSimpleName() + ":body");
            if(binding.tempSummaryTextView!=null){
                binding.tempSummaryTextView.setText(s);
            }
        });

        dashBoardViewModel.getYesterdaySummaryTitles().observe(getViewLifecycleOwner(), this::setSummaryTitlesInPlots);

       dashBoardViewModel.getYesterdayFullData5MinAvgAllIntervals().observe(getViewLifecycleOwner(), stringDataFiveMinAvgDataContainerMap -> stringDataFiveMinAVGAllIntervalsMap = stringDataFiveMinAvgDataContainerMap);

        dashBoardViewModel.getYesterdaySummary().observe(getViewLifecycleOwner(), this::setSummaryViews);

        dashBoardViewModel.getYesterdayArray5MinAvgAllIntervals().observe(getViewLifecycleOwner(), this::setAllPlots);



    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}