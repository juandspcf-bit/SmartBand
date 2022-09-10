package com.misawabus.project.heartRate.fragments.summaryFragments;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.databinding.FragmentSummarySop2Binding;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.viewModels.SleepDataUIViewModel;

public class SummarySop2Fragment extends SummaryFragment {
    private FragmentSummarySop2Binding binding;
    private DashBoardViewModel dashBoardViewModel;
    private DeviceViewModel deviceViewModel;


    public SummarySop2Fragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_summary_sop2,
                container,
                false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button backToMainFragButton = binding.buttonBackFromSummarySop2Frag;
        Button selectDateButton = binding.selectDateSop2Button;
        Button shareButton = binding.buttonShareSop2;

        backToMainFragButton.setOnClickListener(view1 -> backMainFragment());





    }
}