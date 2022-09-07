package com.misawabus.project.heartRate.fragments.dialogFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BloodPressureDialogFragment extends DialogFragment {
    private ExecutorService executor;
    private DashBoardViewModel dashBoardViewModel;
    private final boolean isEnable = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.fragment_real_time_blood_pressue, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dashBoardViewModel.setRealTimeBPData("0" + "---" + "0" + "---" + "0");
        if(executor==null) return;
        executor.shutdownNow();
        dashBoardViewModel.getRealTimeTesterClass().stopBloodPressure();
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
        Button bpCounterButton = view.findViewById(R.id.bPReadButton);
        CircularProgressIndicator circularProgressIndicator = view.findViewById(R.id.progressInBP);
        TextView highPressure = view.findViewById(R.id.textViewBPHigh);
        TextView lowPressure = view.findViewById(R.id.textViewBPLow);
        highPressure.setText(MessageFormat.format("{0}: {1}", getString(R.string.higPressure), 0));
        lowPressure.setText(MessageFormat.format("{0}: {1}", getString(R.string.lowPressure), 0));

        bpCounterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circularProgressIndicator.setProgress(0, true);
                starBPMeasurement();
                bpCounterButton.setEnabled(false);
            }
        });

        dashBoardViewModel.getRealTimeBPData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String fullData) {
                String[] split = fullData.split("---");

                int progress = Integer.parseInt(split[2]);
                circularProgressIndicator.setProgress(progress);

                highPressure.setText(MessageFormat.format("{0}: {1}", getString(R.string.higPressure), split[0]));
                lowPressure.setText(MessageFormat.format("{0}: {1}", getString(R.string.lowPressure), split[1]));

                if(progress==100){
                    bpCounterButton.setEnabled(true);
                    stopBPMeasurement();
                }
            }
        });

    }

    private void starBPMeasurement() {
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            dashBoardViewModel.getRealTimeTesterClass().configBloodPressureMeasurement();
        });
    }

    private void stopBPMeasurement(){
        if(executor==null) return;
        executor.shutdownNow();
        dashBoardViewModel.getRealTimeTesterClass().stopBloodPressure();
    }
}