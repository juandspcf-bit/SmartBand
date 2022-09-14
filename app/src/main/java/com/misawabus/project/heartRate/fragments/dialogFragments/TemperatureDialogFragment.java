package com.misawabus.project.heartRate.fragments.dialogFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.databinding.FragmentRealTimeTemperatureBinding;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TemperatureDialogFragment extends DialogFragment {
    private static final String TAG = TemperatureDialogFragment.class.getSimpleName();
    private FragmentRealTimeTemperatureBinding binding;
    private DashBoardViewModel dashBoardViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_real_time_temperature, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dashBoardViewModel.getRealTimeTempData().observe(getViewLifecycleOwner(), new Observer<>() {
            @Override
            public void onChanged(Map<String, Double> doubleMap) {
                Double doubleProgress = doubleMap.get("Progress");
                Double skinTemp = doubleMap.get("SkinTemp");
                Double bodyTemp = doubleMap.get("BodyTemp");
                if(doubleProgress==null || skinTemp==null || bodyTemp==null) return;
                int progress = (int) Math.round(doubleProgress);

                binding.tempCircularProgress.setProgress(progress);
                if (progress != 100) return;
                binding.tempReadButton.setEnabled(true);
                binding.tempCircularProgress.setProgress(0);
                binding.tempSkinResultTextView
                        .setText(String.format(Locale.getDefault(), "%.1f °C", skinTemp));
                binding.tempBodyResultTexView
                        .setText(String.format(Locale.getDefault(), "%.1f °C", bodyTemp));

            }
        });

        binding.tempReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashBoardViewModel.getRealTimeTesterClass().startTemperatureDetection();
                binding.tempReadButton.setEnabled(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Map<String, Double> map = new HashMap<>();
        map.put("BodyTemp", 0.0);
        map.put("SkinTemp", 0.0);
        map.put("Progress", 0.0);
        dashBoardViewModel.getRealTimeTempData().setValue(map);


    }
}
