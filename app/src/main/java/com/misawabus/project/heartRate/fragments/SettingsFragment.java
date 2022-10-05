package com.misawabus.project.heartRate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.inuker.bluetooth.library.Code;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.ScanConnectionActivity;
import com.misawabus.project.heartRate.databinding.FragmentSettingsBinding;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.veepoo.protocol.model.datas.HeartWaringData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.EHeartWaringStatus;
import com.veepoo.protocol.model.settings.CustomSettingData;

import java.util.function.Consumer;


public class SettingsFragment extends Fragment {
    FragmentSettingsBinding binding;
    private DashBoardViewModel dashBoardViewModel;
    private View vista;
    private boolean isOpen;
    private DeviceViewModel deviceViewModel;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

        vista = LayoutInflater.from(getContext()).inflate(R.layout.botton_sheet_heart_rate_alert_values, null);
        NumberPicker numberPickerHighValue = vista.findViewById(R.id.numberPickerHighValue);
        numberPickerHighValue.setMaxValue(150);
        numberPickerHighValue.setMinValue(0);

        NumberPicker numberPickerLowValue = vista.findViewById(R.id.numberPickerLowValue);
        numberPickerLowValue.setMaxValue(150);
        numberPickerLowValue.setMinValue(0);

        Button confirmButton = vista.findViewById(R.id.confirmHRAlertButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int heartHigh = numberPickerHighValue.getValue();
                int heartLow = numberPickerLowValue.getValue();
                if (heartLow > heartHigh) return;

                dashBoardViewModel.getRealTimeTesterClass().setHeartRateAlert(heartHigh, heartLow, isOpen, heartWaringData -> {
                    if(!heartWaringData.isOpen()){
                        Toast.makeText(getContext(), "Error, enable the switch alert in order to set its values", Toast.LENGTH_LONG).show();
                        numberPickerHighValue.setValue(heartWaringData.getHeartHigh());
                        numberPickerLowValue.setValue(heartWaringData.getHeartLow());
                        return;
                    }
                    numberPickerHighValue.setValue(heartHigh);
                    numberPickerLowValue.setValue(heartLow);
                });
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.setContentView(vista);


        dashBoardViewModel.getRealTimeTesterClass().readHeartRateAlertSettings(integer -> {
                    //Snackbar.make(view, integer == Code.REQUEST_SUCCESS ? "Yes" : "No", Snackbar.LENGTH_SHORT).show();
                }
                , new Consumer<HeartWaringData>() {
                    @Override
                    public void accept(HeartWaringData heartWaringData) {
                        int heartHigh = heartWaringData.getHeartHigh();
                        numberPickerHighValue.setValue(heartHigh);
                        int heartLow = heartWaringData.getHeartLow();
                        numberPickerLowValue.setValue(heartLow);
                        isOpen = heartWaringData.isOpen();
                    }
                });

        binding.hearRateAlarmSetValuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        binding.disconecctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.disconecctButton.getText()=="Connect"){
                    Intent myIntent = new Intent(getActivity(), ScanConnectionActivity.class);
                    startActivity(myIntent);
                }

                dashBoardViewModel.getDeviceSettingsManager().disconnectDevice(code -> {
                    if(code==Code.REQUEST_SUCCESS)
                        Toast.makeText(getContext(), "Successfully disconnected", Toast.LENGTH_SHORT).show();
                    binding.disconecctButton.setText("Connect");

                });
            }
        });


        dashBoardViewModel.getIsConnected().observe(getViewLifecycleOwner(),  isBluetoothConnected-> {
            if(!isBluetoothConnected) {
                binding.disconecctButton.setText("Connect");
            }

        });


    }
}