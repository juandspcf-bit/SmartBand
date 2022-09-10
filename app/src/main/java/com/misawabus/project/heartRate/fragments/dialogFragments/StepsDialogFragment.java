package com.misawabus.project.heartRate.fragments.dialogFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StepsDialogFragment extends DialogFragment {
private ExecutorService executor;
    private DashBoardViewModel dashBoardViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.fragment_realt_time_steps, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(executor==null) return;
        executor.shutdownNow();
        Log.d("Interrupted_on_onDestroyView", "It  was interrupted");
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwitchMaterial stepsCounterSwitch = view.findViewById(R.id.stepsReadSwitchDialog);
        stepsCounterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    compoundButton.setText("Step count started");
                    startStepsMeasurement();
                } else {
                    compoundButton.setText("Step count stopped");
                    if (executor != null) {
                        executor.shutdownNow();
                    }
                }
            }
        });

        dashBoardViewModel.getRealTimeSportsData().observe(getViewLifecycleOwner(), new Observer<>() {
            @Override
            public void onChanged(String fullData) {
                String[] split = fullData.split("---");
                TextView stepsView = view.findViewById(R.id.textViewRealStepsCounterDialog);
                stepsView.setText(split[0]);
                TextView distanceView = view.findViewById(R.id.textViewRealDistanceCounterDialog);
                distanceView.setText(split[1]);
                TextView caloriesView = view.findViewById(R.id.textViewRealCaloriesCounterDialog);
                caloriesView.setText(split[2]);
            }
        });

    }

    private void startStepsMeasurement() {
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            while (true){
                synchronized (this){
                    try {
                        dashBoardViewModel.getRealTimeTesterClass().readSportSteps();
                        wait(3000);
                    } catch (InterruptedException e) {
                        Log.d("Interrupted", "It  was interrupted");
                        break;
                    }
                }
            }

        });

    }
}
