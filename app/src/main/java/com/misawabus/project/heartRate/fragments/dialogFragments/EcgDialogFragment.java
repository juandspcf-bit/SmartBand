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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.databinding.FragmentRealTimeEcgBinding;
import com.misawabus.project.heartRate.device.readRealTimeData.EcgHeartRealthView;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.orhanobut.logger.Logger;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IECGDetectListener;
import com.veepoo.protocol.model.datas.EcgDetectInfo;
import com.veepoo.protocol.model.datas.EcgDetectResult;
import com.veepoo.protocol.model.datas.EcgDetectState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EcgDialogFragment extends DialogFragment {
    private FragmentRealTimeEcgBinding binding;
    private final static String TAG = EcgDialogFragment.class.getSimpleName();
    private final WriteResponse writeResponse = new WriteResponse();
    private final List<int[]> dataEcg = new ArrayList<>();
    private IECGDetectListener iPttDetectListener;
    private EcgHeartRealthView ecgHeartRealthView;
    private DashBoardViewModel dashBoardViewModel;
    private Boolean inProgress = false;
    private EcgDetectState currentEcgDetectState;



    public EcgDialogFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_real_time_ecg, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
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



        iPttDetectListener = new IECGDetectListener() {
            @Override
            public void onEcgDetectInfoChange(EcgDetectInfo ecgDetectInfo) {
                Log.d("ECG测量基本信息(波形频率,采样频率):", ecgDetectInfo.toString());
            }

            @Override
            public void onEcgDetectStateChange(EcgDetectState ecgDetectState) {
                currentEcgDetectState = ecgDetectState;
                Log.d("PTT_INFO", ecgDetectState.toString());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.hPulseValueTextView.setText(String.valueOf(currentEcgDetectState.getHr1()));
                        binding.qtcValueTextView.setText(String.valueOf(currentEcgDetectState.getQtc()));
                        binding.hrvEcgValueTextView.setText(String.valueOf(currentEcgDetectState.getHrv()));
                        binding.linearProgressIndicator.setProgress(currentEcgDetectState.getProgress());

                    }
                });
                if (ecgDetectState.getProgress() == 100) {
                    inProgress = false;
                }
            }

            @Override
            public void onEcgDetectResultChange(EcgDetectResult ecgDetectResult) {
            }

            @Override
            public void onEcgADCChange(final int[] data) {
                if (currentEcgDetectState.getWear() == 1 && currentEcgDetectState.getProgress() >= 1)
                    return;
                if (getActivity() == null || data == null || data.length == 0) return;

                int[] data2 = Arrays.stream(data).filter(value -> Math.abs(value) < 2000).toArray();
                dataEcg.add(data2);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        binding.pttRealView.changeData(data2, data2.length);
                    }
                });



            }


        };



        binding.pttRealView.clearData();


        binding.startDetectECG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enter();
            }
        });

        binding.stopDetectECG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.linearProgressIndicator.setProgress(0);
                exitModel();
            }
        });

    }

    public void enter() {
        binding.pttRealView.clearData();
        if (getContext() == null) return;
        VPOperateManager.getMangerInstance(getContext()).startDetectECG(writeResponse, true, iPttDetectListener);
        inProgress = true;
    }


    public void exitModel() {
        inProgress = false;
        if (getContext() == null) return;
        VPOperateManager.getMangerInstance(getContext()).stopDetectECG(writeResponse, true, iPttDetectListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (inProgress) exitModel();
        if (dataEcg.size() == 0) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dashBoardViewModel.setDataEcg(dataEcg);
            }
        });
    }

    static class WriteResponse implements IBleWriteResponse {

        @Override
        public void onResponse(int code) {
            Logger.t(TAG).i("write cmd status:" + code);
        }
    }
}
