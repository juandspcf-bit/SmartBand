package com.misawabus.project.heartRate.fragments.summaryFragments;

import static com.misawabus.project.heartRate.Utils.Constants.DEFAULT_AGE;
import static com.misawabus.project.heartRate.Utils.DateUtils.getTodayFormattedDate;
import static com.misawabus.project.heartRate.plotting.PlotUtils.getSubArrayWithReplacedZeroValuesAsAvgV2;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.misawabus.project.heartRate.Database.entities.Sop2;
import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.databinding.FragmentSummarySop2Binding;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.fragments.summaryFragments.spo2InfoFields.Spo2InfoFieldsFragment;
import com.misawabus.project.heartRate.plotting.PlotUtils;
import com.misawabus.project.heartRate.plotting.PlotUtilsSpo2;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.viewModels.Sop2ViewModel;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SummarySop2Fragment extends SummaryFragment {
    private static final String TAG = SummarySop2Fragment.class.getSimpleName();
    private FragmentSummarySop2Binding binding;
    private DashBoardViewModel dashBoardViewModel;
    private DeviceViewModel deviceViewModel;
    private Button selectDateButton;


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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        selectDateButton = binding.selectDateSop2Button;
        Button shareButton = binding.buttonShareSop2;

        backToMainFragButton.setOnClickListener(view1 -> backMainFragment());
        shareButton.setOnClickListener(viewToShare -> shareScreen());

        Date todayFormattedDate = getTodayFormattedDate();
        getDataFromDB(todayFormattedDate, dataFromDB -> setFragmentViews(todayFormattedDate, dataFromDB));

        selectDateButton.setOnClickListener(view1 ->
                getDateFromCalendar(selectedDate -> getDataFromDB(selectedDate,
                        dataFromDB -> setFragmentViews(selectedDate, dataFromDB))));

    }


    private void setFragmentViews(Date selectedDate, Sop2 data) {
        setTextButtonDate(selectedDate, selectDateButton);
        if (data == null) {
            binding.spo2ImageView.setVisibility(View.VISIBLE);
            binding.group2.setVisibility(View.GONE);
            return;
        }
        if (data.getData() == null || data.getData().isEmpty()) {
            binding.group2.setVisibility(View.GONE);
            binding.spo2ImageView.setVisibility(View.VISIBLE);
            return;
        }

        String stringSop2Data = data.getData();
        List<Map<String, Double>> sop2DataMap = FragmentUtil.parse5MinFieldData(stringSop2Data);


        Log.d(TAG, "setFragmentViews: " + sop2DataMap);

        PlotUtils.AxisClass oxygenValue1 = getSubArrayWithReplacedZeroValuesAsAvgV2(sop2DataMap, "oxygenValue", IntervalUtils.intervalLabels5Min);

        XYDataArraysForPlotting sop2XYDataArraysForPlotting;
        sop2XYDataArraysForPlotting = new XYDataArraysForPlotting(oxygenValue1.getTimeAxis(),
                oxygenValue1.getRangeAxis());

        PlotUtilsSpo2.plotSpo2DoubleIntervalsData(sop2XYDataArraysForPlotting.getPeriodIntervalsArray(),
                sop2XYDataArraysForPlotting.getSeriesDoubleAVR(),
                binding.fragmentSop2PlotSummary);

        double apneaResult = sop2DataMap.stream()
                .map(sop2Data -> sop2Data.get("apneaResult)"))
                .filter(value -> value != null && value > 0.0)
                .collect(Collectors.averagingDouble(Double::doubleValue));
        String apneaQuality = getApneaQuality(apneaResult);


        double oxygenValue = sop2DataMap.stream()
                .limit(84) //limit to 7:00 am
                .map(sop2Data -> sop2Data.get("oxygenValue"))
                .filter(value -> value != null && value > 0.0)
                .collect(Collectors.averagingDouble(Double::doubleValue));

        double respirationRate = sop2DataMap.stream()
                .limit(84) //limit to 7:00 am
                .map(sop2Data -> sop2Data.get("respirationRate"))
                .filter(value -> value != null && value > 0.0)
                .collect(Collectors.averagingDouble(Double::doubleValue));
        String respirationRateQuality = getRespirationRateQuality(respirationRate);


        double isHypoxia = sop2DataMap.stream()
                .map(sop2Data -> sop2Data.get("isHypoxia"))
                .filter(value -> value != null && value > 0.0)
                .collect(Collectors.averagingDouble(Double::doubleValue));
        String hypoxiaQuality = getHypoxiaQuality(isHypoxia);

        double cardiacLoad = sop2DataMap.stream()
                .limit(84) //limit to 7:00 am
                .map(sop2Data -> sop2Data.get("cardiacLoad"))
                .filter(value -> value != null && value > 0.0)
                .collect(Collectors.averagingDouble(Double::doubleValue));
        String cardiacLoadQuality = getCardiacLoadQuality(cardiacLoad);

        if(oxygenValue<70){
            binding.group2.setVisibility(View.GONE);
            binding.spo2ImageView.setVisibility(View.VISIBLE);
            return;
        }
        binding.group2.setVisibility(View.VISIBLE);
        binding.spo2ImageView.setVisibility(View.GONE);

        long roundApneaResult = Math.round(apneaResult);
        binding.apneaResultValue.setText(String.valueOf(roundApneaResult));
        binding.apneaResultProgressBar.setProgress((int) roundApneaResult);
        binding.apneaResultTextView.setText(apneaQuality);

        long roundOxygenValue = Math.round(oxygenValue);
        binding.bloodOxygenValue.setText(String.valueOf(roundOxygenValue));
        binding.bloodOxygenProgressBar.setProgress((int) roundOxygenValue);
        binding.resultBloodOxygenTextView.setText(roundOxygenValue >= 95 ? "Normal" : "Low");

        long roundRespirationRate = Math.round(respirationRate);
        binding.resultRespirationRateTextView.setText(respirationRateQuality);
        binding.respirationRateValue.setText(String.valueOf(roundRespirationRate));
        binding.respirationRateProgressBar.setProgress((int) roundRespirationRate);

        long roundIsHypoxia = Math.round(isHypoxia);
        binding.hypoxiaTimeValue.setText(String.valueOf(roundIsHypoxia));
        binding.hypoxiaTimeProgressBar.setProgress((int) roundIsHypoxia);
        binding.resultHypoxiaTimeTextView.setText(hypoxiaQuality);

        long roundCardiacLoad = Math.round(cardiacLoad);
        binding.cardiacLoadValue.setText(String.valueOf(roundCardiacLoad));
        binding.cardiacLoadProgressBar.setProgress((int) roundCardiacLoad);
        binding.resultCardiacLoadTextView.setText(cardiacLoadQuality);


        dashBoardViewModel.getIsConnected().observe(getViewLifecycleOwner(), isBluetoothConnected -> {
            if (!isBluetoothConnected) {
                binding.spo2AlertSwitch.setEnabled(false);
                return;
            }
            binding.spo2AlertSwitch.setEnabled(true);

        });
        dashBoardViewModel.getRealTimeTesterClass().readSpo2AlertStatus(isOpen -> {
            if (isOpen) {
                binding.spo2AlertSwitch.setEnabled(true);
                binding.spo2AlertSwitch.setChecked(true);
            } else {
                binding.spo2AlertSwitch.setEnabled(true);
                binding.spo2AlertSwitch.setChecked(false);
            }
        });

        binding.spo2AlertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) dashBoardViewModel.getRealTimeTesterClass().setOpenSpo2AlertStatus();
                else dashBoardViewModel.getRealTimeTesterClass().setCloseSpo2AlertStatus();
            }
        });

        binding.apneaIconButton.setOnClickListener(this::listenerInfoFieldsButtons);
        binding.bloodOxygenIconButton.setOnClickListener(this::listenerInfoFieldsButtons);
        binding.respirationRateIconButton.setOnClickListener(this::listenerInfoFieldsButtons);
        binding.hypoxiaTimeIconButton.setOnClickListener(this::listenerInfoFieldsButtons);
        binding.cardiacLoadIconButton.setOnClickListener(this::listenerInfoFieldsButtons);


    }

    private String getApneaQuality(double roundApneaResult) {
        if (roundApneaResult >= 5 && roundApneaResult < 15) {
            return "Mild";
        } else if (roundApneaResult >= 15 && roundApneaResult < 30) {
            return "Moderate";
        } else if (roundApneaResult >= 30) {
            return "Serious";
        } else {
            return "Normal";
        }
    }

    private String getHypoxiaQuality(double isHypoxia) {
        if (isHypoxia <= 20) {
            return "Normal";
        } else {
            return "Abnormal";
        }
    }

    private String getCardiacLoadQuality(double cardiacLoad) {
        if (cardiacLoad < 20) {
            return "Mild";
        } else if (cardiacLoad >= 20 && cardiacLoad < 40) {
            return "Normal";
        } else {
            return "Abnormal";
        }
    }

    private String getRespirationRateQuality(double respirationRate) {
        String rate = "";
        int age = DEFAULT_AGE;
        Optional<Integer> value = dashBoardViewModel.getAge().getValue();
        if (value != null && value.isPresent()) {
            age = value.orElse(DEFAULT_AGE);
            if (age == 0) age = DEFAULT_AGE;
        }

        Log.d(TAG, "getRespirationRateQuality: " + age);
        if (age >= 18 && age <= 65) {
            Log.d(TAG, "getRespirationRateQuality: " + "right interval");
            if (respirationRate < 12) {
                rate = "Low";
            } else if (respirationRate > 20) {
                rate = "High";
            } else {
                Log.d(TAG, "getRespirationRateQuality: right status");
                rate = "Normal";
            }
        } else if (age >= 65 && age <= 80) {
            if (respirationRate < 12) {
                rate = "Low";
            } else if (respirationRate > 28) {
                rate = "High";
            } else {
                rate = "Normal";
            }

        } else if (age > 80) {
            if (respirationRate < 10) {
                rate = "Low";
            } else if (respirationRate > 30) {
                rate = "High";
            } else {
                rate = "Normal";
            }

        } else if (age >= 13 && age <= 17) {
            if (respirationRate < 12) {
                rate = "Low";
            } else if (respirationRate > 20) {
                rate = "High";
            } else {
                rate = "Normal";
            }

        } else if (age >= 6 && age <= 12) {
            if (respirationRate < 18) {
                rate = "Low";
            } else if (respirationRate > 30) {
                rate = "High";
            } else {
                rate = "Normal";
            }

        }
        Log.d(TAG, "getRespirationRateQuality: " + rate);
        return rate;

    }


    private void getDataFromDB(Date date, Consumer<Sop2> listData) {
        Sop2ViewModel
                .getSingleRow(date, deviceViewModel.getMacAddress())
                .observe(getViewLifecycleOwner(),
                        dataFromDB -> {
                            if (dataFromDB == null) new Sop2();
                            listData.accept(dataFromDB);
                        });
    }


    private void listenerInfoFieldsButtons(View view) {
        int id = view.getId();

        if (id == R.id.apneaIconButton) {
            dashBoardViewModel.setCurrentTitleSpo2InfoFields("Apnea");
            goToSpo2InfoFieldsFragment();
        } else if (id == R.id.bloodOxygenIconButton) {
            dashBoardViewModel.setCurrentTitleSpo2InfoFields("Blood Oxygen");
            goToSpo2InfoFieldsFragment();
        } else if (id == R.id.respirationRateIconButton) {
            dashBoardViewModel.setCurrentTitleSpo2InfoFields("Respiration Rate");
            goToSpo2InfoFieldsFragment();
        } else if (id == R.id.hypoxiaTimeIconButton) {
            dashBoardViewModel.setCurrentTitleSpo2InfoFields("Hypoxia");
            goToSpo2InfoFieldsFragment();
        } else if (id == R.id.cardiacLoadIconButton) {
            dashBoardViewModel.setCurrentTitleSpo2InfoFields("Cardiac Load");
            goToSpo2InfoFieldsFragment();
        }

    }

    private void goToSpo2InfoFieldsFragment() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.mainDashBoardFragmentContainerInActivityDashBoard,
                        new Spo2InfoFieldsFragment())
                //new SummarySleepFragment())
                .addToBackStack(null)
                .commit();
    }
}