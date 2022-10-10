package com.misawabus.project.heartRate.fragments.daysFragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.misawabus.project.heartRate.DashBoardActivity;
import com.misawabus.project.heartRate.Database.entities.Device;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.Utils.ExcelConversionUtils;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.databinding.FragmentDataSummaryV2Binding;
import com.misawabus.project.heartRate.device.DataContainers.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.Sop2HData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.Temperature5MinDataContainer;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.fragments.fragmentUtils.SetDataInViews;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummaryBPFragment;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummaryFragment;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummaryHRFragment;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummarySleepFragmentV2;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummarySop2Fragment;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummarySportsFragment;
import com.misawabus.project.heartRate.plotting.PlotUtilsSleep;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.veepoo.protocol.model.enums.EFunctionStatus;

import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class DayFragment extends Fragment {
    private static final String TAG = DayFragment.class.getSimpleName();
    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    protected DeviceViewModel deviceViewModel;
    protected FragmentDataSummaryV2Binding binding;
    protected DashBoardViewModel dashBoardViewModel;
    protected String macAddress;
    protected List<SleepDataUI> sleepDataList;
    protected Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAVGAllIntervalsMap;

    public static void setDaySleepPlot(DayFragment dayFragment, List<SleepDataUI> sleepDataUIList) {
        if (sleepDataUIList == null || sleepDataUIList.size() == 0) return;
        dayFragment.sleepDataList = sleepDataUIList;

        SleepDataUI sleepDataUI;
        sleepDataUIList = PlotUtilsSleep.sortSleepListData(sleepDataUIList);
        Log.d(TAG, "PlotUtilsSleep: " + sleepDataUIList.size());
        if (sleepDataUIList.get(0).idTypeDataTable.equals(IdTypeDataTable.SleepPrecision)) {
            sleepDataUIList = PlotUtilsSleep.joinSleepListData(sleepDataUIList);
        }
        Log.d(TAG, "PlotUtilsSleep: " + sleepDataUIList.size());
        if (sleepDataUIList.size() == 0) return;
        sleepDataUI = sleepDataUIList.get(0);

        if(dayFragment.binding.sleepTitleTextView!= null && dayFragment.binding.sleepSummaryTextView!=null){
            dayFragment.binding.sleepTitleTextView.setVisibility(View.VISIBLE);
            dayFragment.binding.sleepSummaryTextView.setVisibility(View.VISIBLE);
        }

        String sleepDown = sleepDataUI.getSleepDown();
        LocalTime dateObj1 = DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepDown);
        String sleepUp = sleepDataUI.getSleepUp();
        LocalTime dateObj = DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepUp);
        String s = dateObj1 + " to " + dateObj;
        if(dayFragment.binding.sleepSummaryTextView!=null){
            dayFragment.binding.sleepSummaryTextView.setText(s);
        }


        Map<String, List<Integer>> sleepData;
        if (sleepDataUI.idTypeDataTable.equals(IdTypeDataTable.Sleep)) {
            sleepData = FragmentUtil.getSleepDataForPlotting(sleepDataUI.getData());
            SetDataInViews.setSleepValues(sleepDataUI, sleepData.get("lightSleep"),
                    sleepData.get("deepSleep"),
                    sleepData.get("wakeUp"),
                    dayFragment.binding.fragmentSleepPlot,
                    dayFragment.binding);
        } else if (sleepDataUI.idTypeDataTable.equals(IdTypeDataTable.SleepPrecision)) {
            sleepData = FragmentUtil.getSleepPrecisionDataForPlotting(sleepDataUI.getData());
            SetDataInViews.setSleepPrecisionValues(sleepDataUI, sleepData.get("deepSleep"),
                    sleepData.get("lightSleep"),
                    sleepData.get("rapidEyeMovement"),
                    sleepData.get("insomnia"),
                    sleepData.get("wakeUp"),
                    dayFragment.binding.fragmentSleepPlot,
                    dayFragment.binding);
        }


    }

    @NonNull
    public static Stream<SummaryFragment.ContainerDouble> getContainerDoubleStream(Double[] collect, long sizeList) {
        return Stream.iterate(0, i -> ++i).limit(sizeList - 1)
                .map(index -> {
                    double nonNullVal = collect[index];
                    return new SummaryFragment.ContainerDouble(nonNullVal, index);
                })
                .filter(container -> container.getValue() > 0);
    }

    public FragmentDataSummaryV2Binding getBinding() {
        return binding;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
        macAddress = deviceViewModel.getMacAddress();
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

    protected void onClickCardFitnessArea() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.mainDashBoardFragmentContainerInActivityDashBoard,
                        new SummarySportsFragment())
                .addToBackStack(null)
                .commit();
    }

    protected void onClickCardSleepArea() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.mainDashBoardFragmentContainerInActivityDashBoard,
                        new SummarySleepFragmentV2())
                //new SummarySleepFragment())
                .addToBackStack(null)
                .commit();
    }

    protected void onClickCardBloodPressureArea() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.mainDashBoardFragmentContainerInActivityDashBoard,
                        new SummaryBPFragment())
                .addToBackStack(null)
                .commit();
    }

    protected void onClickCardHeartRateArea() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.mainDashBoardFragmentContainerInActivityDashBoard,
                        new SummaryHRFragment())
                .addToBackStack(null)
                .commit();
    }

    protected void onClickCardSop2Area() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.mainDashBoardFragmentContainerInActivityDashBoard,
                        new SummarySop2Fragment())
                .addToBackStack(null)
                .commit();
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
            if(binding.stepsTitleTextView!=null){
                binding.stepsTitleTextView.setVisibility(View.VISIBLE);
            }
            if (binding.stepsSummaryTextView!=null){
                binding.stepsSummaryTextView.setVisibility(View.VISIBLE);
            }

            binding.flowNoStepsData.setVisibility(View.GONE);
            SetDataInViews.plotStepsData(sportsXYDataArraysForPlotting,
                    binding.fragmentPlot);
        }

        XYDataArraysForPlotting heartRateXYDataArraysForPlotting;
        heartRateXYDataArraysForPlotting = stringXYDataArraysForPlottingMap
                .get(HeartRateData5MinAvgDataContainer.class.getSimpleName());
        if (heartRateXYDataArraysForPlotting != null && heartRateXYDataArraysForPlotting.getSeriesDoubleAVR() != null) {
            binding.fragmentRatePlot.setVisibility(View.VISIBLE);
            if(binding.heartRateTitleTextView!=null){
                binding.heartRateTitleTextView.setVisibility(View.VISIBLE);
            }

            if(binding.heartRateSummaryTextView!=null){
                binding.heartRateSummaryTextView.setVisibility(View.VISIBLE);
            }

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
            if(binding.bpTitleTextView!=null && binding.bpSummaryTextView!=null){
                binding.bpTitleTextView.setVisibility(View.VISIBLE);
                binding.bpSummaryTextView.setVisibility(View.VISIBLE);
            }
            binding.flowNoBPData.setVisibility(View.GONE);
            SetDataInViews.plotBloodPressureData(highBPRateXYDataArraysForPlotting,
                    lowBPRateXYDataArraysForPlotting,
                    binding.fragmentBloodPressurePlot);
        }


        XYDataArraysForPlotting sop2XYDataArraysForPlotting;
        sop2XYDataArraysForPlotting =
                stringXYDataArraysForPlottingMap.get(Sop2HData5MinAvgDataContainer.class.getSimpleName());
        if (sop2XYDataArraysForPlotting != null
                && sop2XYDataArraysForPlotting.getSeriesDoubleAVR() != null
                && binding.fragmentSop2Plot != null
                && binding.flowNoSop2Data != null) {
            binding.fragmentSop2Plot.setVisibility(View.VISIBLE);
            if(binding.spo2SummaryTextView!=null && binding.spo2TitleTextView!=null){
                binding.spo2SummaryTextView.setVisibility(View.VISIBLE);
                binding.spo2TitleTextView.setVisibility(View.VISIBLE);
            }

            binding.flowNoSop2Data.setVisibility(View.GONE);
            SetDataInViews.plotSop2Data(sop2XYDataArraysForPlotting,
                    binding.fragmentSop2Plot);
        }
    }

    protected void setSummaryTitlesInPlots(Map<String, String> stringStringMap) {
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
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color_for_main_fragment, null));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && getView()!=null) {
            WindowInsetsControllerCompat windowInsetsController =
                    WindowCompat.getInsetsController(getActivity().getWindow(), getView());
            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
        } else {
            Log.d(TAG, "onResume: DayFragment");
            DashBoardActivity.hideWindowForLesR(getActivity());
        }


        deviceViewModel.getCustomSettingDataObject().observe(getViewLifecycleOwner(), customSettingData -> {
            if (customSettingData == null) return;
            EFunctionStatus autoTemperatureDetect = customSettingData.getAutoTemperatureDetect();
            EFunctionStatus autoHeartDetect = customSettingData.getAutoHeartDetect();
            EFunctionStatus autoBpDetect = customSettingData.getAutoBpDetect();
            EFunctionStatus ppgSupport = customSettingData.getPpg();
            EFunctionStatus lowSpo2hRemain = customSettingData.getLowSpo2hRemain();

            if (EFunctionStatus.SUPPORT_OPEN != autoBpDetect) {
                binding.fragmentBloodPressureCardView.setVisibility(View.GONE);
                if(binding.bpSummaryTextView!=null && binding.bpTitleTextView!=null){
                    binding.bpSummaryTextView.setVisibility(View.GONE);
                    binding.bpTitleTextView.setVisibility(View.GONE);
                }


            }
            if (EFunctionStatus.SUPPORT_OPEN != lowSpo2hRemain && binding.fragmentSop2PlotCardView!=null) {
                binding.fragmentSop2PlotCardView.setVisibility(View.GONE);
                if(binding.spo2SummaryTextView!=null && binding.spo2TitleTextView!=null){
                    binding.spo2SummaryTextView.setVisibility(View.GONE);
                    binding.spo2TitleTextView.setVisibility(View.GONE);
                }
            }

            if (EFunctionStatus.SUPPORT_OPEN != autoTemperatureDetect && binding.fragmentTemperaturePlotCardView!=null) {
                binding.fragmentTemperaturePlotCardView.setVisibility(View.GONE);
                if(binding.tempSummaryTextView!=null && binding.tempTitleTextView!=null){
                    binding.tempSummaryTextView.setVisibility(View.GONE);
                    binding.tempTitleTextView.setVisibility(View.GONE);
                }

            } else if(binding.fragmentTemperaturePlotCardView!=null){
                binding.fragmentTemperaturePlotCardView.setVisibility(View.VISIBLE);
                if(binding.tempSummaryTextView!=null && binding.tempTitleTextView!=null){
                    binding.tempSummaryTextView.setVisibility(View.VISIBLE);
                    binding.tempTitleTextView.setVisibility(View.VISIBLE);
                }
            }

        });

    }

    protected void setTemperaturePlot(@NonNull Map<String, XYDataArraysForPlotting> stringXYDataArraysForPlottingMap) {
        XYDataArraysForPlotting tempBodyXYDataArraysForPlotting;
        tempBodyXYDataArraysForPlotting = stringXYDataArraysForPlottingMap
                .get(Temperature5MinDataContainer.class.getSimpleName() + ":body");
        if (tempBodyXYDataArraysForPlotting != null && tempBodyXYDataArraysForPlotting.getSeriesDoubleAVR() != null) {
            if(binding.fragmentTemperaturePlot!=null){
                binding.fragmentTemperaturePlot.setVisibility(View.VISIBLE);
            }
            if(binding.tempTitleTextView!=null){
                binding.tempTitleTextView.setVisibility(View.VISIBLE);
            }
            if(binding.tempSummaryTextView!=null){
                binding.tempSummaryTextView.setVisibility(View.VISIBLE);
            }
            if(binding.flowNoTemperature2Data!=null){
                binding.flowNoTemperature2Data.setVisibility(View.GONE);
            }

            SetDataInViews.plotTemperatureData(tempBodyXYDataArraysForPlotting,
                    binding.fragmentTemperaturePlot);
        }
    }


}
