package com.misawabus.project.heartRate.fragments.daysFragments.lifecycleObservers;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.misawabus.project.heartRate.databinding.FragmentDataSummaryV2Binding;
import com.misawabus.project.heartRate.fragments.daysFragments.TodayFragment;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;

import java.util.Objects;

public class TodayLifeCycleObserverOnRefreshing implements DefaultLifecycleObserver {
    public static final String TAG = TodayLifeCycleObserverOnRefreshing.class.getSimpleName();
    private DashBoardViewModel dashBoardViewModel;
    private com.misawabus.project.heartRate.databinding.FragmentDataSummaryV2Binding binding;
    private TodayFragment todayFragment;


    public TodayLifeCycleObserverOnRefreshing() {

    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        todayFragment = (TodayFragment) owner;
        binding = todayFragment.getBinding();

        Log.d(TAG, "onResume: ");

        dashBoardViewModel = new ViewModelProvider(todayFragment.requireActivity()).get(DashBoardViewModel.class);

        dashBoardViewModel.getIsConnected().observe(todayFragment.getViewLifecycleOwner(), isDeviceConnected -> {
            if (isDeviceConnected) {
                binding.refreshLayout.setEnabled(true);
                binding.refreshLayout.setOnRefreshListener(() -> {
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
            binding.refreshLayout.setRefreshing(false);
            binding.refreshLayout.setEnabled(false);
        });

    }

    private void handleIsEnabledFeatures() {
        Observer<Boolean> booleanObserver1 = isEnabled -> {
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
        dashBoardViewModel.getIsEnableFeatures().observe(todayFragment.getViewLifecycleOwner() ,
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

        };

        Observer<Boolean> value = dashBoardViewModel.getObserverMutableLiveData().getValue();
        if (value != null) {
            dashBoardViewModel.getIsTodayFragmentRefreshing().removeObserver(value);
        }
        dashBoardViewModel.setObserverMutableLiveData(booleanObserver);
        dashBoardViewModel.getIsTodayFragmentRefreshing().observe(todayFragment.getViewLifecycleOwner(),
                booleanObserver);
    }

    }