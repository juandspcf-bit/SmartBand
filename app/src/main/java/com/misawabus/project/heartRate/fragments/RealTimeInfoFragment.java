package com.misawabus.project.heartRate.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.databinding.FragmentRealtTimeDataBinding;
import com.misawabus.project.heartRate.fragments.dialogFragments.TemperatureDialogFragment;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.fragments.dialogFragments.BloodPressureDialogFragment;
import com.misawabus.project.heartRate.fragments.dialogFragments.EcgDialogFragment;
import com.misawabus.project.heartRate.fragments.dialogFragments.HeartRateDialogFragment;
import com.misawabus.project.heartRate.fragments.dialogFragments.StepsDialogFragment;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.CustomSettingData;

import java.util.Map;

public class RealTimeInfoFragment extends Fragment {
    FragmentRealtTimeDataBinding binding;
    DeviceViewModel deviceViewModel;
    private DashBoardViewModel dashBoardViewModel;

    public RealTimeInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_realt_time_data, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dashBoardViewModel.getIsConnected().observe(getViewLifecycleOwner(),  isBluetoothConnected-> {
            if(!isBluetoothConnected) {
                disableAllButtons();
                return;
            }
            deviceViewModel.getCustomSettingDataObject().observe(getViewLifecycleOwner(), new Observer<CustomSettingData>() {
                @Override
                public void onChanged(CustomSettingData customSettingData) {
                    if(customSettingData!=null){
                        EFunctionStatus autoTemperatureDetect = customSettingData.getAutoTemperatureDetect();
                        EFunctionStatus autoHeartDetect = customSettingData.getAutoHeartDetect();
                        EFunctionStatus autoBpDetect = customSettingData.getAutoBpDetect();
                        binding.imageButtonTemp.setEnabled(EFunctionStatus.SUPPORT == autoTemperatureDetect
                                || EFunctionStatus.SUPPORT_OPEN == autoTemperatureDetect);
                        binding.imageButtonHeartRate.setEnabled(EFunctionStatus.SUPPORT == autoHeartDetect
                                || EFunctionStatus.SUPPORT_OPEN == autoHeartDetect);
                        binding.imageButtonBloodP.setEnabled(EFunctionStatus.SUPPORT == autoBpDetect
                                || EFunctionStatus.SUPPORT_OPEN == autoBpDetect);
                    }else {
                        disableAllButtons();
                    }
                }
            });
        });



        binding.imageButtonRunner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getChildFragmentManager();
                StepsDialogFragment newFragment = new StepsDialogFragment();
                    newFragment.show(fragmentManager, "dialog");
            }
        });

        binding.imageButtonHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getChildFragmentManager();
                HeartRateDialogFragment newFragment = new HeartRateDialogFragment();
                newFragment.show(fragmentManager, "dialog");
            }
        });

        binding.imageButtonBloodP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getChildFragmentManager();
                BloodPressureDialogFragment newFragment = new BloodPressureDialogFragment();
                newFragment.show(fragmentManager, "dialog");
            }
        });

        binding.imageButtonEcg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getChildFragmentManager();
                EcgDialogFragment newFragment = new EcgDialogFragment();
                newFragment.show(fragmentManager, "dialog");

            }
        });

        binding.imageButtonTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getChildFragmentManager();
                DialogFragment newFragment = new TemperatureDialogFragment();
                newFragment.show(fragmentManager, "dialog");
            }
        });
    }

    private void disableAllButtons() {
        binding.imageButtonRunner.setEnabled(false);
        binding.imageButtonTemp.setEnabled(false);
        binding.imageButtonHeartRate.setEnabled(false);
        binding.imageButtonBloodP.setEnabled(false);
        binding.imageButtonEcg.setEnabled(false);
    }
}