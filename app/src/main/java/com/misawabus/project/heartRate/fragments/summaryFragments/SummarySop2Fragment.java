package com.misawabus.project.heartRate.fragments.summaryFragments;

import static com.misawabus.project.heartRate.Utils.Constants.DEFAULT_AGE;
import static com.misawabus.project.heartRate.Utils.DateUtils.getTodayFormattedDate;
import static com.misawabus.project.heartRate.plotting.PlotUtils.getSubArrayWithReplacedZeroValuesAsAvg;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        if(data == null) return;
        if( data.getData()==null || data.getData().isEmpty()) return;
        String stringSop2Data = data.getData();

        List<Map<String, Double>> sop2DataMap = FragmentUtil.parse5MinFieldData(stringSop2Data);

        Double[] subArrayWithReplacedZeroValuesAsAvg = getSubArrayWithReplacedZeroValuesAsAvg(sop2DataMap, "oxygenValue");
        int lengthSubArray = subArrayWithReplacedZeroValuesAsAvg.length;
        String[] timeAxisSubArray = IntervalUtils.getStringFiveMinutesIntervals(lengthSubArray);

        XYDataArraysForPlotting sop2XYDataArraysForPlotting;
        sop2XYDataArraysForPlotting = new XYDataArraysForPlotting(timeAxisSubArray,
                subArrayWithReplacedZeroValuesAsAvg);

        PlotUtilsSpo2.plotSpo2DoubleIntervalsData(sop2XYDataArraysForPlotting.getPeriodIntervalsArray(),
                sop2XYDataArraysForPlotting.getSeriesDoubleAVR(),
                binding.fragmentSop2PlotSummary);

        double apneaResult = sop2DataMap.stream()
                .map(sop2Data -> sop2Data.get("apneaResult)"))
                .filter(value -> value != null && value > 0.0)
                .collect(Collectors.averagingDouble(Double::doubleValue));


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

        double cardiacLoad = sop2DataMap.stream()
                .limit(84) //limit to 7:00 am
                .map(sop2Data -> sop2Data.get("cardiacLoad"))
                .filter(value -> value != null && value > 0.0)
                .collect(Collectors.averagingDouble(Double::doubleValue));

        double sleepActivity = sop2DataMap.stream()
                .limit(84) //limit to 7:00 am
                .map(sop2Data -> sop2Data.get("SleepActivity"))
                .filter(value -> value != null && value > 0.0)
                .collect(Collectors.averagingDouble(Double::doubleValue));



        long roundApneaResult = Math.round(apneaResult);
        if(roundApneaResult>=5 && roundApneaResult <15){
            binding.apneaResultTextView.setText("Mild");
        }else if(roundApneaResult>=15 && roundApneaResult <30){
            binding.apneaResultTextView.setText("Moderate");
        }else if(roundApneaResult>=30){
            binding.apneaResultTextView.setText("Serious");
        }else {
            binding.apneaResultTextView.setText("Normal");
        }
        binding.apneaResultValue.setText(String.valueOf(roundApneaResult));
        binding.apneaResultProgressBar.setProgress((int) roundApneaResult);

        long roundOxygenValue = Math.round(oxygenValue);
        binding.bloodOxygenValue.setText(String.valueOf(roundOxygenValue));
        binding.bloodOxygenProgressBar.setProgress((int) roundOxygenValue);

        long roundRespirationRate = Math.round(respirationRate);
        binding.respirationRateValue.setText(String.valueOf(roundRespirationRate));
        binding.respirationRateProgressBar.setProgress((int) roundRespirationRate);

        long roundIsHypoxia = Math.round(isHypoxia);
        binding.hypoxiaTimeValue.setText(String.valueOf(roundIsHypoxia));
        binding.hypoxiaTimeProgressBar.setProgress((int) roundIsHypoxia);

        long roundCardiacLoad = Math.round(cardiacLoad);
        binding.cardiacLoadValue.setText(String.valueOf(roundCardiacLoad));
        binding.cardiacLoadProgressBar.setProgress((int) roundCardiacLoad);

        long roundSleepActivity = Math.round(sleepActivity);
        binding.resultRespirationRateTextView.setText(respirationRateQuality);
        binding.sleepActivityValue.setText(String.valueOf(roundSleepActivity));
        binding.sleepActivityProgressBar.setProgress((int) roundSleepActivity);

        binding.resultBloodOxygenTextView.setText(roundOxygenValue >= 95 ? "Normal" : "Low");
        String resultResRate = "";
        if(roundRespirationRate<=16 && roundRespirationRate>=15){
            resultResRate = "Normal";
        }else if(roundRespirationRate<=13){
            resultResRate = "Low";
        }
        binding.resultRespirationRateTextView.setText(resultResRate);

    }

    private String getRespirationRateQuality(double respirationRate) {
        String rate = "";
        int age;
        Optional<Integer> value = dashBoardViewModel.getAge().getValue();
        if(value!=null && value.isPresent()){
            age = value.orElse(30);
        }else {
            age= DEFAULT_AGE;
        }

        if(age>=18 && age<=65){
            if(respirationRate<12){
                rate="Low";
            }else if(respirationRate>20){
                rate="High";
            }else{
                rate="Normal";
            }
        }else if(age>=65 && age<=80){
            if(respirationRate<12){
                rate="Low";
            }else if(respirationRate>28){
                rate="High";
            }else{
                rate="Normal";
            }

        }else if(age>80){
            if(respirationRate<10){
                rate="Low";
            }else if(respirationRate>30){
                rate="High";
            }else{
                rate="Normal";
            }

        }else if(age>=13 && age<=17){
            if(respirationRate<12){
                rate="Low";
            }else if(respirationRate>20){
                rate="High";
            }else{
                rate="Normal";
            }

        } else if(age>=6 && age<=12){
            if(respirationRate<18){
                rate="Low";
            }else if(respirationRate>30){
                rate="High";
            }else{
                rate="Normal";
            }

        }

        return rate;

    }


    private void getDataFromDB(Date date, Consumer<Sop2> listData) {
        Sop2ViewModel
                .getSingleRow(date, deviceViewModel.getMacAddress())
                .observe(getViewLifecycleOwner(),
                        dataFromDB -> {
                            if (dataFromDB == null) new  Sop2();
                            listData.accept(dataFromDB);
                        });
    }
}