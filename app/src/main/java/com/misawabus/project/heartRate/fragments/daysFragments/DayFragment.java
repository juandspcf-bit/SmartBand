package com.misawabus.project.heartRate.fragments.daysFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.misawabus.project.heartRate.Database.entities.Device;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.Utils.ExcelConversionUtils;
import com.misawabus.project.heartRate.databinding.FragmentDataSummaryV2Binding;
import com.misawabus.project.heartRate.device.DataContainers.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.Sop2HData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.fragmentUtils.SetDataInViews;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummaryBPFragment;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummaryHRFragment;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummarySleepFragment;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummarySop2Fragment;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummarySportsFragment;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DayFragment extends Fragment {
    protected DeviceViewModel deviceViewModel;

    public FragmentDataSummaryV2Binding getBinding() {
        return binding;
    }

    protected FragmentDataSummaryV2Binding binding;
    protected DashBoardViewModel dashBoardViewModel;
    protected String macAddress;
    protected List<SleepDataUI> sleepDataList;
    protected Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAVGAllIntervalsMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
        macAddress = deviceViewModel.getMacAddress();
/*        dashBoardViewModel.setIsEnableFeatures(false);
        dashBoardViewModel.setIsTodayFragmentRefreshing(false);*/

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_data_summary_v2, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Objects.requireNonNull(binding.refreshLayout).setEnabled(false);
        binding.refreshLayout.setSize(SwipeRefreshLayout.LARGE);

        binding.fragmentPlot.setVisibility(View.GONE);
        binding.flowNoStepsData.setVisibility(View.VISIBLE);
        binding.fragmentRatePlot.setVisibility(View.GONE);
        binding.flowNoHeartRateData.setVisibility(View.VISIBLE);
        binding.fragmentBloodPressurePlot.setVisibility(View.GONE);
        binding.flowNoBPData.setVisibility(View.VISIBLE);
        binding.fragmentSleepPlot.setVisibility(View.GONE);
        binding.flowNoSleepData.setVisibility(View.VISIBLE);

        dashBoardViewModel.getIsEnableFeatures().observe(getViewLifecycleOwner(), isEnabled -> {
            if (!isEnabled) return;
            binding.chipShareEmail.setEnabled(true);

        });



        binding.chipShareEmail.setOnClickListener(view12 -> {
            if (!dashBoardViewModel.isWiFiEnable()) {
                Snackbar.make(binding.chipShareEmail,
                        "No Wifi connection",
                        BaseTransientBottomBar.LENGTH_LONG).show();
                return;
            }
            Device device = dashBoardViewModel.getDevice();
            ExcelConversionUtils.createWorkbook(getContext(),
                    requireActivity(),
                    macAddress,
                    stringDataFiveMinAVGAllIntervalsMap,
                    sleepDataList,
                    device);
        });
    }

    protected void onClickCardFitnessArea(View view) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView3,
                        new SummarySportsFragment()).commit();
    }

    protected void onClickCardSleepArea(View view) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView3,
                        new SummarySleepFragment())
                .commit();
    }

    protected void onClickCardBloodPressureArea(View view) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView3,
                        new SummaryBPFragment()).commit();
    }

    protected void onClickCardHeartRateArea(View view) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView3,
                        new SummaryHRFragment()).commit();
    }

    protected void onClickCardSop2Area(View view) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView3,
                        new SummarySop2Fragment()).commit();
    }


    protected void setSummaryViews(Map<String, Double> doubleMap) {
        Double stepCountDouble = doubleMap.get("stepCount");
        if (stepCountDouble != null) {
            long stepCount = Math.round(stepCountDouble);
            String stepsS = String.valueOf(stepCount);
            binding.fragmentCounterSteps.setText(stepsS);
        }

        Double caloriesCountDouble = doubleMap.get("caloriesCount");
        if (caloriesCountDouble != null) {
            String caloriesS = String.format(Locale.getDefault(), "%.1f Kcal", caloriesCountDouble);
            binding.textViewCaloriesCounter.setText(caloriesS);
        }

        Double distanceCountDouble = doubleMap.get("distanceCount");
        if (distanceCountDouble != null) {
            String distance = String.format(Locale.getDefault(), "%.1f Km", distanceCountDouble);
            binding.textViewDistanceCounter.setText(distance);
        }
    }

    protected void setAllPlots(Map<String, XYDataArraysForPlotting> stringXYDataArraysForPlottingMap) {
        XYDataArraysForPlotting sportsXYDataArraysForPlotting;
        sportsXYDataArraysForPlotting = stringXYDataArraysForPlottingMap
                .get(SportsData5MinAvgDataContainer.class.getSimpleName());
        if (sportsXYDataArraysForPlotting != null && sportsXYDataArraysForPlotting.getSeriesDoubleAVR() != null) {
            binding.fragmentPlot.setVisibility(View.VISIBLE);
            binding.flowNoStepsData.setVisibility(View.GONE);
            SetDataInViews.plotStepsData(sportsXYDataArraysForPlotting,
                    binding.fragmentPlot);
        }

        XYDataArraysForPlotting heartRateXYDataArraysForPlotting;
        heartRateXYDataArraysForPlotting = stringXYDataArraysForPlottingMap
                .get(HeartRateData5MinAvgDataContainer.class.getSimpleName());
        if (heartRateXYDataArraysForPlotting != null && heartRateXYDataArraysForPlotting.getSeriesDoubleAVR() != null) {
            binding.fragmentRatePlot.setVisibility(View.VISIBLE);
            binding.flowNoHeartRateData.setVisibility(View.GONE);
            SetDataInViews.plotHeartRateData(heartRateXYDataArraysForPlotting,
                    binding.fragmentRatePlot);
        }


        XYDataArraysForPlotting highBPRateXYDataArraysForPlotting;
        highBPRateXYDataArraysForPlotting = stringXYDataArraysForPlottingMap
                .get(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName() + "High");
        XYDataArraysForPlotting lowBPRateXYDataArraysForPlotting;
        lowBPRateXYDataArraysForPlotting = stringXYDataArraysForPlottingMap
                .get(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName() + "Low");
        if (highBPRateXYDataArraysForPlotting != null
                && highBPRateXYDataArraysForPlotting.getSeriesDoubleAVR() != null
                && lowBPRateXYDataArraysForPlotting != null
                && lowBPRateXYDataArraysForPlotting.getSeriesDoubleAVR() != null) {
            binding.fragmentBloodPressurePlot.setVisibility(View.VISIBLE);
            binding.flowNoBPData.setVisibility(View.GONE);
            SetDataInViews.plotBloodPressureData(highBPRateXYDataArraysForPlotting,
                    lowBPRateXYDataArraysForPlotting,
                    binding.fragmentBloodPressurePlot);
        }


        XYDataArraysForPlotting sop2XYDataArraysForPlotting = stringXYDataArraysForPlottingMap.get(Sop2HData5MinAvgDataContainer.class.getSimpleName());
        if (sop2XYDataArraysForPlotting != null
                && sop2XYDataArraysForPlotting.getSeriesDoubleAVR() != null
                && binding.fragmentSop2Plot != null
                && binding.flowNoSop2Data != null) {
            binding.fragmentSop2Plot.setVisibility(View.VISIBLE);
            binding.flowNoSop2Data.setVisibility(View.GONE);
            SetDataInViews.plotSop2Data(sop2XYDataArraysForPlotting,
                    binding.fragmentSop2Plot);
        }

    }
}
