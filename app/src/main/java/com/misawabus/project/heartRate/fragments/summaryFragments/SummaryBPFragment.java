package com.misawabus.project.heartRate.fragments.summaryFragments;

import static com.misawabus.project.heartRate.constans.IdTypeDataTable.HighPressure;
import static com.misawabus.project.heartRate.constans.IdTypeDataTable.LowPressure;
import static com.misawabus.project.heartRate.fragments.summaryFragments.utils.UtilsSummaryFragments.Companion;
import static com.misawabus.project.heartRate.plotting.PlotUtils.getSubArrayWithReplacedZeroValuesAsAvgV2;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.misawabus.project.heartRate.Database.entities.BloodPressure;
import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.adapters.recyclerView.FillViewsFieldsWithEntitiesValues;
import com.misawabus.project.heartRate.adapters.recyclerView.OnEntityClickListener;
import com.misawabus.project.heartRate.adapters.recyclerView.RecyclerViewBuilder;
import com.misawabus.project.heartRate.adapters.recyclerView.ViewsInRowHolder;
import com.misawabus.project.heartRate.adapters.viewHolders.summaryBP.ViewsInBPRowHolder;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.databinding.FragmentSummaryBpBinding;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.plotting.PlotUtils;
import com.misawabus.project.heartRate.plotting.PlotUtilsBloodPressure;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;
import com.misawabus.project.heartRate.viewModels.BloodPressureViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SummaryBPFragment extends SummaryFragment {
    private static final String TAG = SummaryBPFragment.class.getSimpleName();
    private FragmentSummaryBpBinding binding;
    private BloodPressureViewModel bpDataUIViewModel;
    public static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    Message msg;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    public SummaryBPFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bpDataUIViewModel = new ViewModelProvider(requireActivity()).get(BloodPressureViewModel.class);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_summary_bp,
                container,
                false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        Button backToMainFragButton = binding.buttonBackFromSummaryBPFrag;
        Button selectDateButton = binding.selectDateBPButton;
        Button shareButton = binding.buttonShareBP;

        backToMainFragButton.setOnClickListener(view1 -> backMainFragment());
        shareButton.setOnClickListener(viewToShare -> shareScreen());

        Date todayFormattedDate = DateUtils.getTodayFormattedDate();
        getDataFromDB(todayFormattedDate, dataFromDB -> setFragmentViews(todayFormattedDate, dataFromDB));

        selectDateButton.setOnClickListener(view1 ->
                getDateFromCalendar(selectedDate -> getDataFromDB(selectedDate,
                        dataFromDB -> setFragmentViews(selectedDate, dataFromDB))));

        binding.imageViewBP.setVisibility(View.VISIBLE);
        binding.groupBPSummary.setVisibility(View.GONE);
    }

    private void setFragmentViews(Date selectedDate, BloodPressure bloodPressure) {
        setTextButtonDate(selectedDate, binding.selectDateBPButton);
        if (bloodPressure == null || bloodPressure.getData() == null) {
            binding.imageViewBP.setVisibility(View.VISIBLE);
            binding.groupBPSummary.setVisibility(View.GONE);
            return;
        }

        List<Map<String, Double>> bloodPressureDataMap = FragmentUtil.parse5MinFieldData(bloodPressure.getData());
        List<Map<String, Double>> bPMapFieldsForEach30Min = FragmentUtil.getBloodPressureMapFieldsWith30MinAVGValues(bloodPressureDataMap);

        XYDataArraysForPlotting lowValuesSampleContainer;
        PlotUtils.AxisClass lowVaamlue = getSubArrayWithReplacedZeroValuesAsAvgV2(bPMapFieldsForEach30Min, "lowVaamlue", IntervalUtils.intervalLabels30Min);
        lowValuesSampleContainer = new XYDataArraysForPlotting(lowVaamlue.getTimeAxis(),
                lowVaamlue.getRangeAxis());
        XYDataArraysForPlotting highValuesSampleContainer;
        PlotUtils.AxisClass highValue = getSubArrayWithReplacedZeroValuesAsAvgV2(bPMapFieldsForEach30Min, "highValue", IntervalUtils.intervalLabels30Min);
        highValuesSampleContainer = new XYDataArraysForPlotting(highValue.getTimeAxis(),
                highValue.getRangeAxis());

        //XYDataArraysForPlotting highValuesSampleContainer = PlotUtils.get30MinFieldXYDataArraysForPlotting(bPMapFieldsForEach30Min, "highValue");
        //XYDataArraysForPlotting lowValuesSampleContainer = PlotUtils.get30MinFieldXYDataArraysForPlotting(bPMapFieldsForEach30Min, "lowVaamlue");
        Double[] highValuesSampleArray = highValuesSampleContainer.getSeriesDoubleAVR();
        Double[] lowValuesSampleArray = lowValuesSampleContainer.getSeriesDoubleAVR();
        List<Double> HPList = Arrays.asList(highValuesSampleArray);
        List<Double> LPlist = Arrays.asList(lowValuesSampleArray);
        List<String> stringsIntervalHours = Arrays.asList(highValuesSampleContainer.getPeriodIntervalsArray());
        int fullLengthSeries = highValuesSampleArray.length;


        Map<IdTypeDataTable, List<Double>> mapForHighPressureLowPressure = new HashMap<>();
        mapForHighPressureLowPressure.put(HighPressure, HPList);
        mapForHighPressureLowPressure.put(LowPressure, LPlist);

        int fullDataSize = 48;
        List<String> intervalsFull = Arrays.asList(IntervalUtils.getStringHalfHourMinutesIntervals(fullDataSize));
        buildRecyclerView(mapForHighPressureLowPressure,
                intervalsFull,
                getContext(),
                binding.recyclerViewBP,
                R.layout.row_layout_bp,
                new ViewsInBPRowHolder()
        );

        databaseWriteExecutor.execute(() -> {

            Optional<ContainerDouble> optionalMaxIndex =
                    getContainerDoubleStream(mapForHighPressureLowPressure, fullLengthSeries)
                            .max(Comparator.comparing(ContainerDouble::getValue));

            Optional<ContainerDouble> optionalMinIndex =
                    getContainerDoubleStream(mapForHighPressureLowPressure, fullLengthSeries)
                            .min(Comparator.comparing(ContainerDouble::getValue));


            if (!(optionalMaxIndex.isPresent() && optionalMinIndex.isPresent())) return;


            int maxIndex = optionalMaxIndex.get().getIndex();
            String stringMax = String.format(Locale.getDefault(),
                    "%.1f/%.1f",
                    HPList.get(maxIndex),
                    LPlist.get(maxIndex));

            int minIndex = optionalMinIndex.get().getIndex();
            String stringMin = String.format(Locale.getDefault(),
                    "%.1f/%.1f",
                    HPList.get(minIndex),
                    LPlist.get(minIndex));

            mHandler.post(()-> extracted(highValuesSampleContainer,
                    lowValuesSampleContainer,
                    stringsIntervalHours,
                    fullLengthSeries,
                    mapForHighPressureLowPressure,
                    maxIndex,
                    stringMax,
                    minIndex,
                    stringMin));

        });

    }

    private void extracted(XYDataArraysForPlotting highValuesSampleContainer, XYDataArraysForPlotting lowValuesSampleContainer, List<String> stringsIntervalHours, int fullLengthSeries, Map<IdTypeDataTable, List<Double>> mapForHighPressureLowPressure, int maxIndex, String stringMax, int minIndex, String stringMin) {
        if (fullLengthSeries >= 3) {
            PlotUtilsBloodPressure.plotBloodPressureDoubleIntervalsData(highValuesSampleContainer.getPeriodIntervalsArray(),
                    highValuesSampleContainer.getSeriesDoubleAVR(),
                    lowValuesSampleContainer.getSeriesDoubleAVR(),
                    binding.fragmentBPSummaryPlot);
        }

        binding.imageViewBP.setVisibility(View.GONE);
        binding.groupBPSummary.setVisibility(View.VISIBLE);

        binding.highestBP.setText(stringMax);
        binding.highestBPTime.setText(stringsIntervalHours.get(maxIndex));
        binding.lowestBP.setText(stringMin);
        binding.lowestBPTime.setText(stringsIntervalHours.get(minIndex));


    }

    @NonNull
    public Stream<ContainerDouble> getContainerDoubleStream(Map<IdTypeDataTable, List<Double>> collect, long sizeList) {
        return Stream.iterate(0, i -> ++i).limit(sizeList - 1)
                .map(index -> {
                    List<Double> val = collect.get(HighPressure);
                    double nonNullVal = val != null ? val.get(index) : 0;
                    return new ContainerDouble(nonNullVal, index);
                })
                .filter(container -> container.getValue() > 0);
    }


    private void getDataFromDB(Date date, Consumer<BloodPressure> bloodPressureConsumer) {
        BloodPressureViewModel
                .getSinglePDayRow(date, deviceViewModel.getMacAddress())
                .observe(getViewLifecycleOwner(),
                        listData -> {
                            if (listData == null) listData = new BloodPressure();
                            bloodPressureConsumer.accept(listData);
                        });

    }

    private void buildRecyclerView(Map<IdTypeDataTable, List<Double>> data,
                                   List<String> intervalsFull,
                                   Context context,
                                   RecyclerView recyclerView,
                                   int layout_resource,
                                   ViewsInRowHolder viewsInRowHolder) {




        List<Double> doublesHP = Arrays.asList(new Double[intervalsFull.size()]);
        List<Double> doublesLP = Arrays.asList(new Double[intervalsFull.size()]);
        Collections.fill(doublesHP, 0.0);
        Collections.fill(doublesLP, 0.0);

        Collections.copy(doublesHP, data.get(HighPressure));
        Collections.copy(doublesLP, data.get(LowPressure));

        data.replace(HighPressure, doublesHP);
        data.replace(LowPressure, doublesLP);

        for (int i = 0; i < data.get(HighPressure).size(); i++) {
            Log.d(TAG, "getBloodPressureMapFieldsWith30MinAVGValues: dataMap" + i + " : " + data.get(HighPressure).get(i));
        }

        long currentSize = intervalsFull.size();        //intervals.size();

        OnEntityClickListener onEntityClickListener = (id, position) -> Log.d("RECYCLER", String.valueOf(id));

        FillViewsFieldsWithEntitiesValues fillViewsFieldsWithEntitiesValues = (position, viewsInRowHolder1) -> {
            String intervalsS = position < (currentSize - 1) ?
                    intervalsFull.get(position) + " - " + intervalsFull.get(position + 1) :
                    intervalsFull.get(position) + "-" + "00:00";


            Double hpVal = data.get(HighPressure).get(position);

            Double lpVal = data.get(LowPressure).get(position);
            String dataBP = String.format(Locale.getDefault(),
                    "%.1f / %.1f",
                    hpVal,
                    lpVal);

            String bpCategory = Companion.getBPCategory(hpVal, lpVal);

            if (viewsInRowHolder1 instanceof ViewsInBPRowHolder) {
                ViewsInBPRowHolder viewsInBPRowHolder = (ViewsInBPRowHolder) viewsInRowHolder1;
                viewsInBPRowHolder.intervalTextViewRowBP.setText(intervalsS);
                viewsInBPRowHolder.bpTextViewRow.setText(dataBP);
                viewsInBPRowHolder.scoreBP.setText(bpCategory);
                viewsInBPRowHolder.imageViewBP.setImageResource(Companion
                        .getMapCategories()
                        .get(bpCategory));
            }

        };

        RecyclerViewBuilder recyclerViewBuilder = new RecyclerViewBuilder((int) currentSize,
                context,
                recyclerView,
                layout_resource,
                onEntityClickListener,
                fillViewsFieldsWithEntitiesValues,
                viewsInRowHolder);
        recyclerViewBuilder.build();
    }

    private void sendMsg(String message, int what) {
        msg = Message.obtain();
        msg.what = what;
        msg.obj = message;
        mHandler.sendMessage(msg);
    }


}
