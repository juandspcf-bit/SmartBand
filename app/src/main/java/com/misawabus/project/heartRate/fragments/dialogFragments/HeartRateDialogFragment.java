package com.misawabus.project.heartRate.fragments.dialogFragments;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HeartRateDialogFragment extends DialogFragment {
    private ExecutorService executor;
    private DashBoardViewModel dashBoardViewModel;
    private ValueAnimator animation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.fragment_real_time_heart_rate, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        animation.cancel();
        animation.end();
        animation.removeAllUpdateListeners();
        if(executor==null) return;
        executor.shutdownNow();
        dashBoardViewModel.getRealTimeHearRateData().setValue("0");
        dashBoardViewModel.getRealTimeTesterClass().stopReadHeartRate();
        Log.d("Interrupted_on_onDestroyView", "It  was interrupted");
    }

    /** The system calls this only when creating the layout in a dialog. */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialSwitch heartRateCounterSwitch = view.findViewById(R.id.heartRateReadSwitchDialog);
        ImageView imageViewHR = view.findViewById(R.id.imageViewHeartRate);
        animation = ValueAnimator.ofFloat(0f, 1f);
        animation.setDuration(2000);
        animation.setInterpolator(new CycleInterpolator(1));
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue = (float)updatedAnimation.getAnimatedValue();
                imageViewHR.setScaleX(Math.abs(animatedValue*1.3f));
                imageViewHR.setScaleY(Math.abs(animatedValue*1.3f));
            }
        });

        heartRateCounterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    compoundButton.setText("Heart Rate started");
                    starHeartRateMeasurement();
                } else {
                    compoundButton.setText("Step count stopped");
                    animation.cancel();
                    animation.end();
                    if (executor != null) {
                        executor.shutdownNow();
                        imageViewHR.setScaleX(1);
                        imageViewHR.setScaleY(1);
                        dashBoardViewModel.getRealTimeTesterClass().stopReadHeartRate();
                    }
                }
            }
        });

        dashBoardViewModel.getRealTimeHearRateData().observe(getViewLifecycleOwner(), new Observer<>() {
            @Override
            public void onChanged(String fullData) {
                String[] split = fullData.split("---");
                TextView stepsView = view.findViewById(R.id.textViewBPHigh);
                if (split[0].equals("0")) return;
                animation.start();
                stepsView.setText(split[0]);
            }
        });

    }

    private void starHeartRateMeasurement() {
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            dashBoardViewModel.getRealTimeTesterClass().readHeartRate();
        });

    }
}
