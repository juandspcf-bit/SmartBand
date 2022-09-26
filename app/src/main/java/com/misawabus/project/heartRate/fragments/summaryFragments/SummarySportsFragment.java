package com.misawabus.project.heartRate.fragments.summaryFragments;


import static com.misawabus.project.heartRate.Utils.DateUtils.getTodayFormattedDate;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.misawabus.project.heartRate.Database.entities.Sports;
import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.adapters.recyclerView.FillViewsFieldsWithEntitiesValues;
import com.misawabus.project.heartRate.adapters.recyclerView.OnEntityClickListener;
import com.misawabus.project.heartRate.adapters.recyclerView.RecyclerViewBuilder;
import com.misawabus.project.heartRate.adapters.recyclerView.ViewsInRowHolder;
import com.misawabus.project.heartRate.adapters.viewHolders.summarySports.ViewsInCaloriesRowHolder;
import com.misawabus.project.heartRate.adapters.viewHolders.summarySports.ViewsInDistanceRowHolder;
import com.misawabus.project.heartRate.adapters.viewHolders.summarySports.ViewsInSportsRowHolder;
import com.misawabus.project.heartRate.databinding.FragmentSportsSummaryBinding;
import com.misawabus.project.heartRate.fragments.DataViews;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.plotting.PlotUtilsSports;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.viewModels.SportsViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class SummarySportsFragment extends SummaryFragment {
    private static final String TAG = SummarySportsFragment.class.getSimpleName();
    private final DataViews dataViews = new DataViews();
    private FragmentSportsSummaryBinding binding;
    private List<Double> stepsDoubleList;
    private List<Double> caloriesDoubleList;
    private List<Double> distancesDoubleList;
    private List<String> stringsIntervalHours;
    private int sumSteps;
    private double sumCalories;
    private double sumDistances;
    private DeviceViewModel deviceViewModel;
    private boolean isInitDone;
    private Button backToMainFragButton;
    private Button selectDateButton;
    private Button shareButton;

    public SummarySportsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sports_summary, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        dataViews.valueTexView = "0";
        binding.setSumData(dataViews);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setSumData(dataViews);

        backToMainFragButton = binding.buttonBackDailyActivities;
        selectDateButton = binding.buttonDateSelection;
        shareButton = binding.buttonShareFitness;

        backToMainFragButton.setOnClickListener(view1 -> backMainFragment());
        shareButton.setOnClickListener(viewToShare -> shareScreen());

        Date todayFormattedDate = getTodayFormattedDate();
        getDataFromDB(todayFormattedDate, dataFromDB ->
                setFragmentViews(todayFormattedDate, dataFromDB));

        selectDateButton.setOnClickListener(view1 ->
                getDateFromCalendar(selectedDate ->
                        getDataFromDB(selectedDate, dataFromDB ->
                                setFragmentViews(selectedDate, dataFromDB)
                        )));

    }

    private void setFragmentViews(Date selectedDate, Sports data) {
        setTextButtonDate(selectedDate, selectDateButton);
        if (data == null) {
            binding.groupDataSports.setVisibility(View.GONE);
            binding.imageViewSports.setVisibility(View.VISIBLE);
            return;
        }
        binding.imageViewSports.setVisibility(View.GONE);
        binding.groupDataSports.setVisibility(View.VISIBLE);

        String sportsData = data.getData();

        List<Map<String, Double>> sportsDataMap = FragmentUtil.parse5MinFieldData(sportsData);
        Map<String, List<Double>> mapFieldsWith30MinValues = FragmentUtil.getSportsMapFieldsWith30MinCountValues(sportsDataMap);

        Double[] stepDoubleArray = mapFieldsWith30MinValues.get("stepValue").toArray(new Double[0]);
        stepsDoubleList = Arrays.asList(stepDoubleArray);
        caloriesDoubleList = Arrays.asList(mapFieldsWith30MinValues.get("calValue").toArray(new Double[0]));
        distancesDoubleList = Arrays.asList(mapFieldsWith30MinValues.get("disValue").toArray(new Double[0]));
        String[] intervalHours = IntervalUtils.getStringHalfHourMinutesIntervals(distancesDoubleList.size());
        stringsIntervalHours = Arrays.asList(intervalHours);


        setSummingValues();
        buildStepsRecyclerView(stepsDoubleList,
                stringsIntervalHours,
                requireContext(),
                binding.recyclerViewSports,
                R.layout.row_layout_sports,
                new ViewsInSportsRowHolder());

        binding.toggleButton.check(R.id.buttonStepsList);
        binding.toggleButton.setEnabled(true);

        if (selectedDate.toString().equals(getTodayFormattedDate().toString()) && !isInitDone) {
            isInitDone = true;
            PlotUtilsSports.initSeriesForSummarySteps(binding.plotStepsDailySummary);
            binding.toggleButton.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (R.id.buttonStepsList == checkedId && isChecked) {
                    dataViews.valueTexView = sumSteps + " Steps";
                    binding.setSumData(dataViews);

                    buildStepsRecyclerView(stepsDoubleList,
                            stringsIntervalHours,
                            requireContext(),
                            binding.recyclerViewSports,
                            R.layout.row_layout_sports,
                            new ViewsInSportsRowHolder());
                } else if (R.id.buttonCaloriesList == checkedId && isChecked) {
                    dataViews.valueTexView = String.format(Locale.getDefault(),
                            "%.1f",
                            sumCalories) + " Kcal";
                    binding.setSumData(dataViews);

                    buildStepsRecyclerView(caloriesDoubleList,
                            stringsIntervalHours,
                            requireContext(),
                            binding.recyclerViewSports,
                            R.layout.row_layout_calories,
                            new ViewsInCaloriesRowHolder());
                } else if (R.id.buttonDistancesList == checkedId && isChecked) {
                    dataViews.valueTexView = String.format(Locale.getDefault(), "%.1f", sumDistances) + " Km";

                    binding.setSumData(dataViews);

                    buildStepsRecyclerView(distancesDoubleList,
                            stringsIntervalHours,
                            requireContext(),
                            binding.recyclerViewSports,
                            R.layout.row_layout_distance,
                            new ViewsInDistanceRowHolder());
                }
            });
        }

        PlotUtilsSports.processingIntervalsSummarySteps(binding.plotStepsDailySummary, stepDoubleArray);

    }

    private void setSummingValues() {
        sumSteps = (int) stepsDoubleList.stream().mapToDouble(number -> number).sum();
        sumCalories = caloriesDoubleList.stream().mapToDouble(number -> number).sum();
        sumDistances = distancesDoubleList.stream().mapToDouble(number -> number).sum();
        dataViews.valueTexView = sumSteps + " Steps";
        binding.setSumData(dataViews);
    }


    private void getDataFromDB(Date date, Consumer<Sports> listData) {
        SportsViewModel
                .getSinglePDayRow(date, deviceViewModel.getMacAddress())
                .observe(getViewLifecycleOwner(),
                        dataFromDB -> {
                            if (dataFromDB == null) new ArrayList<Sports>();
                            listData.accept(dataFromDB);
                        });
    }


    private void buildStepsRecyclerView(List<? extends Number> data,
                                        List<String> intervals,
                                        Context context,
                                        RecyclerView recyclerView,
                                        int layout_resource,
                                        ViewsInRowHolder viewsInRowHolder) {

        OnEntityClickListener onEntityClickListener = (id, position) -> Log.d("RECYCLER", String.valueOf(id));

        FillViewsFieldsWithEntitiesValues fillViewsFieldsWithEntitiesValues = (position, viewsInRowHolder1) -> {

            if (viewsInRowHolder1 instanceof ViewsInSportsRowHolder) {
                ViewsInSportsRowHolder viewsInSportsRowHolder = (ViewsInSportsRowHolder) viewsInRowHolder1;
                if (position < (data.size() - 1)) {
                    String dataS = intervals.get(position) + " - " + intervals.get(position + 1);
                    viewsInSportsRowHolder.intervalTextViewRow.setText(dataS);
                } else {
                    viewsInSportsRowHolder.intervalTextViewRow.setText(intervals.get(position) + "-" + "00:00");
                }
                String steps = data.get(position) + " steps";
                viewsInSportsRowHolder.stepsTextViewRow.setText(steps);
            }
            if (viewsInRowHolder1 instanceof ViewsInCaloriesRowHolder) {
                ViewsInCaloriesRowHolder viewsInCaloriesRowHolder = (ViewsInCaloriesRowHolder) viewsInRowHolder1;
                if (position != (data.size() - 1)) {
                    String dataS = intervals.get(position) + " - " + intervals.get(position + 1);
                    viewsInCaloriesRowHolder.intervalTextViewRow.setText(dataS);
                } else {
                    viewsInCaloriesRowHolder.intervalTextViewRow.setText(intervals.get(position) + "-" + "00:00");
                }
                String value = String.format(Locale.getDefault(), "%.1f kcal", data.get(position).doubleValue());
                viewsInCaloriesRowHolder.caloriesTextViewRow.setText(value);
            }

            if (viewsInRowHolder1 instanceof ViewsInDistanceRowHolder) {
                ViewsInDistanceRowHolder viewsInDistanceRowHolder = (ViewsInDistanceRowHolder) viewsInRowHolder1;
                if (position != (data.size() - 1)) {
                    String dataS = intervals.get(position) + " - " + intervals.get(position + 1);
                    viewsInDistanceRowHolder.intervalTextViewRow.setText(dataS);
                } else {
                    viewsInDistanceRowHolder.intervalTextViewRow.setText(intervals.get(position) + "-" + "00:00");
                }
                String value = String.format(Locale.getDefault(), "%.1f km", data.get(position).doubleValue());
                viewsInDistanceRowHolder.distanceTextViewRow.setText(value);
            }
        };

        RecyclerViewBuilder recyclerViewBuilder = new RecyclerViewBuilder(data.size(),
                context,
                recyclerView,
                layout_resource,
                onEntityClickListener,
                fillViewsFieldsWithEntitiesValues,
                viewsInRowHolder);
        recyclerViewBuilder.build();
    }


}
