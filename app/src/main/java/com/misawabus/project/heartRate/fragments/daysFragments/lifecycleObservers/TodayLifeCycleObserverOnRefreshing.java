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


    public TodayLifeCycleObserverOnRefreshing() {

    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        TodayFragment todayFragment = (TodayFragment) owner;
        FragmentDataSummaryV2Binding binding = todayFragment.getBinding();

        Log.d(TAG, "onResume: ");

        DashBoardViewModel dashBoardViewModel = new ViewModelProvider(todayFragment.requireActivity()).get(DashBoardViewModel.class);


        Observer<Boolean> booleanObserver = isDeviceConnectedUpdated -> {
            if (!isDeviceConnectedUpdated) {
                dashBoardViewModel.setIsEnableFeatures(false);
                dashBoardViewModel.setIsTodayFragmentRefreshing(false);
                binding.refreshLayout.setRefreshing(false);
                binding.refreshLayout.setEnabled(false);
                return;
            }


            dashBoardViewModel.getIsEnableFeatures().observe(todayFragment.getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean isEnabledFeatures) {
                    whenEnabledFeatures(isEnabledFeatures, dashBoardViewModel, binding, todayFragment);
                }
            });

            //Boolean isEnabledFeatures = dashBoardViewModel.getIsEnableFeatures().getValue();
            //whenEnabledFeatures(isEnabledFeatures, dashBoardViewModel, binding, todayFragment);

        };


        dashBoardViewModel.getIsConnected().observe(todayFragment.getViewLifecycleOwner(), booleanObserver);


    }

    private void whenEnabledFeatures(Boolean isEnabledFeatures, DashBoardViewModel dashBoardViewModel, FragmentDataSummaryV2Binding binding, TodayFragment todayFragment) {
        if ((isEnabledFeatures != null && !isEnabledFeatures) && dashBoardViewModel.getIsFetching() == View.VISIBLE) {
            binding.refreshLayout.setRefreshing(false);
            binding.refreshLayout.setEnabled(false);
        } else if ((isEnabledFeatures != null && isEnabledFeatures) && dashBoardViewModel.getIsFetching() == View.INVISIBLE) {
            Log.d(TAG, "onChanged: ready for refreshing actions");
            listeningRefreshing(todayFragment, binding, dashBoardViewModel, true);
        }
    }

    private void listeningRefreshing(TodayFragment todayFragment,
                                     FragmentDataSummaryV2Binding binding,
                                     DashBoardViewModel dashBoardViewModel,
                                     boolean isDeviceConnected) {
        Observer<Boolean> booleanObserver = isTodayFragmentRefreshing -> {
            Log.d(TAG, "onChanged: ready for refreshing actions   isTodayFragmentRefreshing -> {");
            Boolean value = dashBoardViewModel.getIsEnableFeatures().getValue();
            if (isDeviceConnected && !isTodayFragmentRefreshing && (value != null && !value) && dashBoardViewModel.getIsFetching() == View.VISIBLE) {
                binding.refreshLayout.setRefreshing(false);
                binding.refreshLayout.setEnabled(false);

                binding.fragmentPlot.setEnabled(false);
                binding.fragmentSleepPlot.setEnabled(false);
                binding.fragmentBloodPressurePlot.setEnabled(false);

            } else if (isDeviceConnected && !isTodayFragmentRefreshing && dashBoardViewModel.getIsFetching() == View.INVISIBLE) {

                //dashBoardViewModel.setIsEnableFeatures(true);
                binding.refreshLayout.setEnabled(true);
                binding.refreshLayout.setRefreshing(false);

                binding.fragmentPlot.setEnabled(true);
                binding.fragmentSleepPlot.setEnabled(true);
                binding.fragmentBloodPressurePlot.setEnabled(true);

                Log.d(TAG, "onViewCreated: dashBoardViewModel.getIsTodayFragmentRefreshing()   refreshing finished: ");

            } else if (isDeviceConnected && isTodayFragmentRefreshing && dashBoardViewModel.getIsFetching() == View.INVISIBLE) {
                Log.d(TAG, "onChanged: ready for refreshing actions  if (isDeviceConnected && isTodayFragmentRefreshing");
                binding.refreshLayout.setEnabled(true);
                binding.refreshLayout.setRefreshing(true);
                //dashBoardViewModel.setIsEnableFeatures(false);

                binding.fragmentPlot.setEnabled(false);
                binding.fragmentSleepPlot.setEnabled(false);
                binding.fragmentBloodPressurePlot.setEnabled(false);

            }/*else if(!isDeviceConnected){
                binding.refreshLayout.setRefreshing(false);
                binding.refreshLayout.setEnabled(false);
            }*/


        };

        if (dashBoardViewModel.getObserverMutableLiveData().getValue() != null) {
            dashBoardViewModel.getIsTodayFragmentRefreshing().removeObserver(dashBoardViewModel.getObserverMutableLiveData().getValue());
            dashBoardViewModel.setObserverMutableLiveData(booleanObserver);
            dashBoardViewModel.getIsTodayFragmentRefreshing().observe(todayFragment.getViewLifecycleOwner(),
                    booleanObserver);
        } else {
            Log.d(TAG, "onResume:it does not have observers");
            dashBoardViewModel.setObserverMutableLiveData(booleanObserver);
            dashBoardViewModel.getIsTodayFragmentRefreshing().observe(todayFragment.getViewLifecycleOwner(),
                    Objects.requireNonNull(dashBoardViewModel.getObserverMutableLiveData().getValue()));
        }
    }

    private void refreshing(TodayFragment todayFragment, FragmentDataSummaryV2Binding binding, DashBoardViewModel dashBoardViewModel, boolean isDeviceConnected) {
        SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (dashBoardViewModel.getIsTodayFragmentRefreshing().getValue() != null
                        && dashBoardViewModel.getIsTodayFragmentRefreshing().getValue()) {
                    binding.refreshLayout.setRefreshing(true);
                    Log.d(TAG, "onRefresh: already refreshing");
                    return;
                }

                if (!isDeviceConnected) return;
                Log.d(TAG, "onRefresh: connect anyways");
                dashBoardViewModel.getHealthsReadDataManager().getSmartWatchDataSingleDay(0);
                dashBoardViewModel.setIsEnableFeatures(false);
                dashBoardViewModel.setIsTodayFragmentRefreshing(true);
                binding.fragmentPlot.setEnabled(false);
                binding.fragmentSleepPlot.setEnabled(false);
                binding.fragmentBloodPressurePlot.setEnabled(false);
                binding.fragmentSleepPlot.setEnabled(false);
                binding.chipShareEmail.setEnabled(false);

            }
        };

        dashBoardViewModel.getIsConnected().observe(todayFragment.getViewLifecycleOwner(), isDeviceConnectedUpdated -> {

            Boolean isTodayFragmentRefreshing = dashBoardViewModel.getIsTodayFragmentRefreshing().getValue();
            if (isDeviceConnectedUpdated && isTodayFragmentRefreshing != null && isTodayFragmentRefreshing) {
                // this.isDeviceConnected = true;
                binding.refreshLayout.setEnabled(true);
                binding.refreshLayout.setOnRefreshListener(onRefreshListener);
                binding.refreshLayout.setRefreshing(true);

                return;
            }

            if (isDeviceConnectedUpdated) {
                // this.isDeviceConnected = true;
                binding.refreshLayout.setEnabled(true);
                binding.refreshLayout.setOnRefreshListener(onRefreshListener);
                binding.refreshLayout.setRefreshing(false);

                return;
            } else {
                dashBoardViewModel.setIsEnableFeatures(false);
                //this.isDeviceConnected = false;
                binding.refreshLayout.setRefreshing(false);
                binding.refreshLayout.setEnabled(false);
            }

        });
    }
}
