package com.misawabus.project.heartRate.fragments.dialogFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.misawabus.project.heartRate.R;
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
import java.util.List;

public class EcgDialogFragment extends DialogFragment {
    private final static String TAG = EcgDialogFragment.class.getSimpleName();
    private final WriteResponse writeResponse = new WriteResponse();
    private IECGDetectListener iPttDetectListener;
    private EcgHeartRealthView ecgHeartRealthView;
    private final List<int[]> dataEcg = new ArrayList<>();
    private DashBoardViewModel dashBoardViewModel;
    private Boolean inProgress = false;

    public EcgDialogFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_real_time_ecg, container, false);
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

        ecgHeartRealthView = view.findViewById(R.id.ptt_real_view);

        iPttDetectListener = new IECGDetectListener() {
            @Override
            public void onEcgDetectInfoChange(EcgDetectInfo ecgDetectInfo) {
                Log.d("ECG测量基本信息(波形频率,采样频率):", ecgDetectInfo.toString());
            }

            @Override
            public void onEcgDetectStateChange(EcgDetectState ecgDetectState) {
                Logger.t(TAG).i("ECG测量过程中的状态,设置顶部文本:" + ecgDetectState.toString());
                Log.d("PTT_INFO", ecgDetectState.toString());
                if(ecgDetectState.getProgress()==100){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            inProgress=false;
                        }
                    });
                }
            }

            @Override
            public void onEcgDetectResultChange(EcgDetectResult ecgDetectResult) {
            }

            @Override
            public void onEcgADCChange(final int[] data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataEcg.add(data);
                        ecgHeartRealthView.changeData(data, 20);
                    }
                });

            }


        };

        listenModel();

        Button startDetectECG = view.findViewById(R.id.startDetectECG);
        Button stopDetectECG = view.findViewById(R.id.stopDetectECG);
        ecgHeartRealthView.clearData();

        startDetectECG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enter();
            }
        });

        stopDetectECG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitModel();
            }
        });

    }

    private void listenModel() {
        VPOperateManager.getMangerInstance(getContext()).startDetectECG(writeResponse, true , iPttDetectListener);
    }



    public void enter() {
        ecgHeartRealthView.clearData();
        VPOperateManager.getMangerInstance(getContext()).startDetectECG(writeResponse, true, iPttDetectListener);
        inProgress=true;
    }


    public void exitModel() {
        inProgress=false;
        VPOperateManager.getMangerInstance(getContext()).stopDetectECG(writeResponse, true, iPttDetectListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(inProgress) exitModel();
        if(dataEcg.size()==0)return;
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
