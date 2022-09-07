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
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.Utils.ExcelConversionUtils;
import com.misawabus.project.heartRate.databinding.FragmentDataSummaryV2Binding;
import com.misawabus.project.heartRate.device.entities.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummarySop2Fragment;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummaryBPFragment;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummaryHRFragment;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummarySleepFragment;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummarySportsFragment;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.Database.entities.Device;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DayFragment extends Fragment {
    protected DeviceViewModel deviceViewModel;
    protected FragmentDataSummaryV2Binding binding;
    protected DashBoardViewModel dashBoardViewModel;
    protected String macAddress;
    protected String fullSportsData;
    protected List<SleepDataUI> sleepDataList;
    protected Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAVGAllIntervalsMap;

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


}
