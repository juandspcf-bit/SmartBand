package com.misawabus.project.heartRate.fragments.summaryFragments;

import static java.util.stream.Collectors.averagingDouble;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.inuker.bluetooth.library.Code;
import com.misawabus.project.heartRate.Database.entities.HeartRate;
import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.adapters.recyclerView.FillViewsFieldsWithEntitiesValues;
import com.misawabus.project.heartRate.adapters.recyclerView.OnEntityClickListener;
import com.misawabus.project.heartRate.adapters.recyclerView.RecyclerViewBuilder;
import com.misawabus.project.heartRate.adapters.recyclerView.ViewsInRowHolder;
import com.misawabus.project.heartRate.adapters.viewHolders.summaryHR.ViewsInHRRowHolder;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.databinding.FragmentSummaryHrBinding;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.fragments.fragmentUtils.SetDataInViews;
import com.misawabus.project.heartRate.fragments.summaryFragments.utils.UtilsSummaryFrag;
import com.misawabus.project.heartRate.fragments.summaryFragments.utils.UtilsSummaryFrag.ZoneObject;
import com.misawabus.project.heartRate.plotting.PlotUtils;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.viewModels.HeartRateViewModel;
import com.veepoo.protocol.model.datas.HeartWaringData;
import com.veepoo.protocol.model.enums.EHeartWaringStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SummaryHRFragment extends SummaryFragment {
    private static final String TAG = SummaryHRFragment.class.getSimpleName();
    private FragmentSummaryHrBinding binding;
    private DeviceViewModel deviceViewModel;
    private HeartRateViewModel hearRateViewModel;
    private DashBoardViewModel dashBoardViewModel;
    private int heartHigh;
    private int heartLow;

    public static final ExecutorService databaseSingleExecutor =
            Executors.newSingleThreadExecutor();

    public SummaryHRFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hearRateViewModel = new ViewModelProvider(requireActivity()).get(HeartRateViewModel.class);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
   }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_summary_hr,
                container,
                false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button backToMainFragButton = binding.buttonBackFromSummaryHRFrag;
        Button selectDateButton = binding.selectDateHRButton;
        Button shareButton = binding.buttonShareHeartRate;
        binding.heartRateAlertSwitch.setEnabled(false);

        backToMainFragButton.setOnClickListener(view1 -> backMainFragment());
        shareButton.setOnClickListener(viewToShare -> shareScreen());

        Date todayFormattedDate = DateUtils.getTodayFormattedDate();
        getDataFromDB(todayFormattedDate, dataFromDB -> setFragmentViews(todayFormattedDate, dataFromDB));

        selectDateButton.setOnClickListener(view1 ->
                getDateFromCalendar(selectedDate -> getDataFromDB(selectedDate,
                        dataFromDB -> setFragmentViews(selectedDate, dataFromDB))));


    }

    private void setFragmentViews(Date selectedDate, HeartRate dataFromDB) {

        if (dataFromDB == null || dataFromDB.getData() == null || dataFromDB.getData().isEmpty()) {
            binding.group.setVisibility(View.GONE);
            binding.imageView6.setVisibility(View.VISIBLE);
            return;
        }

        binding.heartRateAlertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dashBoardViewModel
                        .getRealTimeTesterClass()
                        .setHeartRateAlert(heartHigh, heartLow, isChecked, heartWaringData -> {
                            if (heartWaringData.getStatus() == EHeartWaringStatus.CLOSE_FAIL && !isChecked) {
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                binding.heartRateAlertSwitch.toggle();
                            }else if(heartWaringData.getStatus() == EHeartWaringStatus.OPEN_FAIL && isChecked){
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                binding.heartRateAlertSwitch.toggle();
                            }else if (heartWaringData.getStatus() == EHeartWaringStatus.OPEN_SUCCESS || heartWaringData.getStatus() == EHeartWaringStatus.CLOSE_SUCCESS){
                                Toast.makeText(getContext(), "Successfully set", Toast.LENGTH_SHORT).show();
                            }

                        });
            }
        });

        dashBoardViewModel.getIsConnected().observe(getViewLifecycleOwner(),  isBluetoothConnected-> {
            if(!isBluetoothConnected) {
                binding.heartRateAlertSwitch.setEnabled(false);
                return;
            }
            binding.heartRateAlertSwitch.setEnabled(true);

        });

        dashBoardViewModel.getRealTimeTesterClass().readHeartRateAlertSettings(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                if (integer != Code.REQUEST_SUCCESS) {
                    binding.heartRateAlertSwitch.setEnabled(false);
                    return;
                }
                binding.heartRateAlertSwitch.setEnabled(true);

            }
        }, new Consumer<HeartWaringData>() {
            @Override
            public void accept(HeartWaringData heartWaringData) {
                heartHigh = heartWaringData.getHeartHigh();
                heartLow = heartWaringData.getHeartLow();
                boolean isOpen = heartWaringData.isOpen();
                binding.heartRateAlertSwitch.setChecked(isOpen);
            }
        });


        databaseSingleExecutor.submit(new Runnable() {
            @Override
            public void run() {
                String heartRateData = dataFromDB.getData();

                List<Map<String, Double>> heartRateDataMap = FragmentUtil.parse5MinFieldData(heartRateData);

                Map<String, List<Double>> heartRateGroupByFieldsWith30MinSumValues;
                heartRateGroupByFieldsWith30MinSumValues = FragmentUtil
                        .getHeartRateDataGroupByFieldsWith30MinSumValues(heartRateDataMap);

                XYDataArraysForPlotting fieldXYDataArraysForPlotting =
                        PlotUtils.get5MinFieldXYDataArraysForPlottingHeartRate(heartRateDataMap, "Ppgs");

                Double[] seriesDoubleAVR = fieldXYDataArraysForPlotting.getSeriesDoubleAVR();

                Optional<Double> max = Arrays
                        .stream(seriesDoubleAVR)
                        .max(Comparator.naturalOrder());
                Optional<Double> min = Arrays
                        .stream(seriesDoubleAVR).filter(value -> value > 0.0)
                        .min(Comparator.naturalOrder());
                var maxString = String.format(Locale.getDefault(),
                        "%.1f",
                        max.orElse(0.0));
                var minString = String.format(Locale.getDefault(),
                        "%.1f",
                        min.orElse(0.0));

                Double average = Arrays
                        .stream(seriesDoubleAVR)
                        .collect(averagingDouble(Double::doubleValue));
                var averageString = String.format(Locale.getDefault(),
                        "%.1f",
                        average);


                Double[] ppgs = heartRateGroupByFieldsWith30MinSumValues.get("Ppgs").toArray(new Double[0]);
                String[] domainLabels = IntervalUtils.intervalLabels30Min;

                List<String> intervals = new ArrayList<>();
                Stream.iterate(0, i -> ++i).limit(domainLabels.length).forEach(position -> {
                    String interval = position < (domainLabels.length - 1) ?
                            domainLabels[position] + " - " + domainLabels[position + 1] :
                            domainLabels[position] + "-" + "00:00";
                    intervals.add(interval);
                });

                if (getActivity()==null) return;
                getActivity().runOnUiThread(() -> {
                    setTextButtonDate(selectedDate, binding.selectDateHRButton);
                    binding.imageView6.setVisibility(View.GONE);
                    binding.group.setVisibility(View.VISIBLE);
                    binding.highestHR.setText(maxString);
                    binding.lowestHR.setText(minString);
                    binding.averageHR.setText(averageString);

                    SetDataInViews.plotHeartRateData(fieldXYDataArraysForPlotting,
                            binding.fragmentRatePlotSummary);

                    buildRecyclerView(Arrays.asList(ppgs),
                            intervals,
                            getContext(),
                            binding.recyclerViewSummHR,
                            R.layout.row_layou_hr,
                            new ViewsInHRRowHolder());
                });


            }
        });
    }


    private void buildRecyclerView(List<Double> data,
                                   List<String> intervals,
                                   Context context,
                                   RecyclerView recyclerView,
                                   int layout_resource,
                                   ViewsInRowHolder viewsInRowHolder) {

        int dataSize = data.size();
        int age;
        Optional<Integer> value = dashBoardViewModel.getAge().getValue();
        if(value!=null && value.isPresent()){
            age = value.orElse(30);
        }else {
            age=30;
        }


        OnEntityClickListener onEntityClickListener = (id, position) -> Log.d("RECYCLER", String.valueOf(id));

        FillViewsFieldsWithEntitiesValues fillViewsFieldsWithEntitiesValues = (position, viewsInRowHolder1) -> {
            if (viewsInRowHolder1 instanceof ViewsInHRRowHolder) {
                ViewsInHRRowHolder viewsInHRRowHolder = (ViewsInHRRowHolder) viewsInRowHolder1;

                viewsInHRRowHolder.intervalHR.setText(intervals.get(position));

                viewsInHRRowHolder.valueHR.setText(String.format(Locale.getDefault(), "%.1f", data.get(position)));

                ZoneObject zoneObject = UtilsSummaryFrag.testZone(age, data.get(position));
                viewsInHRRowHolder.hrZoneTextView.setText(zoneObject.getZone());
                viewsInHRRowHolder.hrZoneProgressBar.setProgress(zoneObject.getZoneInteger());
                viewsInHRRowHolder.hrZoneProgressBar.setProgressTintList(zoneObject.getColorStateList());
            }

        };

        RecyclerViewBuilder recyclerViewBuilder = new RecyclerViewBuilder(dataSize,
                context,
                recyclerView,
                layout_resource,
                onEntityClickListener,
                fillViewsFieldsWithEntitiesValues,
                viewsInRowHolder);
        recyclerViewBuilder.build();
    }


    private void getDataFromDB(Date date, Consumer<HeartRate> hearRateData) {
        HeartRateViewModel
                .getSingleHeartRateRowForU(date, deviceViewModel.getMacAddress(), IdTypeDataTable.HeartRateFiveMin)
                .observe(getViewLifecycleOwner(),
                        dataFromDB -> {
                            if (dataFromDB == null) new HeartRate();
                            hearRateData.accept(dataFromDB);
                        });
    }


}
