package com.misawabus.project.heartRate.fragments.summaryFragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.androidplot.xy.XYPlot;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.adapters.viewHolders.summarySleep.ViewsInSleepRowHolderV2;
import com.misawabus.project.heartRate.adapters.viewPager.FillViewsFieldsWithEntitiesValues;
import com.misawabus.project.heartRate.adapters.viewPager.OnEntityClickListener;
import com.misawabus.project.heartRate.adapters.viewPager.ViewPagerAdapter;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.databinding.FragmentSummarySleepV2Binding;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.plotting.PlotUtilsSleep;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.viewModels.SleepDataUIViewModel;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SummarySleepFragmentV2 extends SummaryFragment {
    private static final String TAG = SummarySleepFragmentV2.class.getSimpleName();
    private FragmentSummarySleepV2Binding binding;
    private DashBoardViewModel dashBoardViewModel;
    private SleepDataUIViewModel sleepDataUIViewModel;
    private DeviceViewModel deviceViewModel;

    public SummarySleepFragmentV2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
        sleepDataUIViewModel = new ViewModelProvider(requireActivity()).get(SleepDataUIViewModel.class);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_summary_sleep_v2,
                container,
                false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button backToMainFragButton = binding.summarySleepFragButtonBackFromV2;
        Button selectDateButton = binding.sleepDateSelectionButtonV2;
        Button shareButton = binding.shareSleepButtonV2;

        backToMainFragButton.setOnClickListener(view1 -> backMainFragment());
        shareButton.setOnClickListener(viewToShare -> shareScreen());

        Date todayFormattedDate = DateUtils.getTodayFormattedDate();
        getDataFromDB(todayFormattedDate, dataFromDB -> setFragmentViews(todayFormattedDate, dataFromDB));

        selectDateButton.setOnClickListener(view1 ->
                getDateFromCalendar(selectedDate -> getDataFromDB(selectedDate,
                        dataFromDB -> setFragmentViews(selectedDate, dataFromDB))));

    }

    private void getDataFromDB(Date date, Consumer<List<SleepDataUI>> sleepDataUIList) {
        SleepDataUIViewModel.getListRows(date,
                deviceViewModel.getMacAddress()).observe(getViewLifecycleOwner(),
                sleepDataUIList::accept);
    }

    private void setFragmentViews(Date selectedDate, List<SleepDataUI> sleepDataUIS) {

        setTextButtonDate(selectedDate, binding.sleepDateSelectionButtonV2);
        if(sleepDataUIS==null || sleepDataUIS.size()==0) {
            binding.imageViewSleep2.setVisibility(View.VISIBLE);
            binding.groupViews.setVisibility(View.GONE);
            return;
        }
        binding.imageViewSleep2.setVisibility(View.GONE);
        binding.groupViews.setVisibility(View.VISIBLE);

        sleepDataUIS.forEach(sleepDataUI -> {
            Log.d(TAG, "SleepDown: " + DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepDataUI.getSleepDown())
                    + "  SleepUp: " + DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepDataUI.getSleepUp())
                    + "  total time: " + sleepDataUI.getAllSleepTime());
        });

        List<Integer> collect = sleepDataUIS.stream().map(sleepDataUI -> {
            LocalTime localTimeFromVeepooTimeDateObj;
            localTimeFromVeepooTimeDateObj =
                    DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepDataUI.getSleepDown());
            return localTimeFromVeepooTimeDateObj.toSecondOfDay();

        }).collect(Collectors.toList());

        Log.d(TAG, "collect.size() " + collect.size());
        Map<Integer, SleepDataUI> mapSleep = new HashMap<>();
        Stream.iterate(0, i -> ++i).limit(collect.size()).forEach(index -> {
            Log.d(TAG, "Stream.iterate: " + collect.get(index) + "  index: " + index);

            mapSleep.put(collect.get(index), sleepDataUIS.get(index));
        });

        Collections.sort(collect);

        collect.forEach(integer -> Log.d(TAG, "setFragmentViews:  " + "integer: " + integer + " sleepValue: " +    mapSleep.get(integer)));
        Log.d(TAG, "setFragmentViews: " +mapSleep);

        List<SleepDataUI> collect1 = collect
                .stream()
                .map(mapSleep::get)
                .collect(Collectors.toList());

        collect1.forEach(sleepDataUI -> Log.d(TAG, "setFragmentViews: " + sleepDataUI));

        buildViewPagerView(collect1,
                getContext(),
                binding.viewPager,
                R.layout.row_layout_sleep_card_v2,
                new ViewsInSleepRowHolderV2()
        );



    }

    private void buildViewPagerView(List<? extends SleepDataUI> data,
                                    Context context,
                                    ViewPager2 viewPager,
                                    int row_layout_sleep_card,
                                    ViewsInSleepRowHolderV2 viewsInSleepRowHolder) {

        OnEntityClickListener onEntityClickListener = (id, position) -> Log.d("RECYCLER", String.valueOf(id));

        FillViewsFieldsWithEntitiesValues fillViewsFieldsWithEntitiesValues = (position, viewsInRowHolder) -> {

            if (viewsInRowHolder instanceof ViewsInSleepRowHolderV2) {

                SleepDataUI sleepDataUI = data.get(position);
                ViewsInSleepRowHolderV2 viewsInSleepRowHolderV2 = (ViewsInSleepRowHolderV2) viewsInRowHolder;

                XYPlot xyPlot = viewsInSleepRowHolderV2.xyPlot;
                Map<String, List<Integer>> sleepData;
                if(sleepDataUI.idTypeDataTable.equals(IdTypeDataTable.Sleep)){
                    sleepData = FragmentUtil.getSleepDataForPlotting(sleepDataUI.getData());
                    PlotUtilsSleep.plotSleepIntegerListData(sleepDataUI,
                            sleepData.get("lightSleep"),
                            sleepData.get("deepSleep"),
                            sleepData.get("wakeUp"),
                            xyPlot);
                }else if(sleepDataUI.idTypeDataTable.equals(IdTypeDataTable.SleepPrecision)){
                    sleepData = FragmentUtil.getSleepPrecisionDataForPlotting(sleepDataUI.getData());
                    PlotUtilsSleep.plotSleepPrecisionIntegerListData(sleepDataUI,
                            sleepData.get("deepSleep"),
                            sleepData.get("lightSleep"),
                            sleepData.get("rapidEyeMovement"),
                            sleepData.get("insomnia"),
                            sleepData.get("wakeUp"),
                            xyPlot);
                }
            }

        };

        Log.d(TAG, "buildViewPagerView: data.size()" + data.size());
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(data.size(),
                row_layout_sleep_card,
                context,
                onEntityClickListener,
                fillViewsFieldsWithEntitiesValues,
                viewsInSleepRowHolder);

        viewPager.setAdapter(viewPagerAdapter);





        ViewPager2.OnPageChangeCallback callback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                SleepDataUI sleepDataUI = //first.get();
                        data.get(position);


                Map<String, List<Integer>> sleepDataMapLines = FragmentUtil.getSleepPrecisionDataForPlotting(sleepDataUI.getData());
                List<Integer> deepSleep = sleepDataMapLines.get("deepSleep");
                String deepSleepTime = "0";
                long countDeepSleep=0;

                if(deepSleep!=null && sleepDataUI.idTypeDataTable.equals(IdTypeDataTable.Sleep)){
                    countDeepSleep = deepSleep.stream().filter(data -> data > 0).count()*5;
                    deepSleepTime = getIntervalTime(countDeepSleep);
                }else if(deepSleep != null && sleepDataUI.idTypeDataTable.equals(IdTypeDataTable.SleepPrecision)){
                    countDeepSleep = deepSleep.stream().filter(data -> data > 0).count();
                    deepSleepTime = getIntervalTime(countDeepSleep);
                }


                List<Integer> lightSleep = sleepDataMapLines.get("lightSleep");
                String lightSleepTime = "0";
                long countLightSleep=0;
                if(lightSleep!=null && sleepDataUI.idTypeDataTable.equals(IdTypeDataTable.Sleep)){
                    countLightSleep = lightSleep.stream().filter(data -> data > 0).count()*5;
                    lightSleepTime = getIntervalTime(countLightSleep);
                }else if(deepSleep != null && sleepDataUI.idTypeDataTable.equals(IdTypeDataTable.SleepPrecision)){
                    countLightSleep = lightSleep.stream().filter(data -> data > 0).count();
                    lightSleepTime = getIntervalTime(countLightSleep);
                }



                Duration duration = Duration.ofMinutes(sleepDataUI.getAllSleepTime());
                String allSleepTime;
                if(duration.toHours()>0){
                    allSleepTime = duration.toHours() + " hours\n" + duration.minusHours(duration.toHours()).toMinutes() +  " minutes";
                }else {
                    allSleepTime = duration.minusHours(duration.toHours()).toMinutes() +  " minutes";
                }

                String wakeCount = String.valueOf(sleepDataUI.getWakeCount());

                LocalTime localTimeFromVeepooTimeDateObj = DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepDataUI.getSleepDown());
                String sleepDown = localTimeFromVeepooTimeDateObj.toString();

                LocalTime localTimeFromVeepooTimeDateObj1 = DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepDataUI.getSleepUp());
                String sleepUp = localTimeFromVeepooTimeDateObj1.toString();

                LocalTime localTime = localTimeFromVeepooTimeDateObj1.minusHours(localTimeFromVeepooTimeDateObj.getHour());
                LocalTime localTime1 = localTime.minusMinutes(localTimeFromVeepooTimeDateObj.getMinute());
                int hours = localTime1.getHour();
                int minutes = localTime1.getMinute();
                allSleepTime = getStringTimeFormatted(hours, minutes);

/*
                duration = Duration.ofMinutes(sleepDataUI.getDeepSleepTime());
                String deepSleepTime;
                if(duration.toHours()>0){
                    deepSleepTime = duration.toHours() + " hours\n" + duration.minusHours(duration.toHours()).toMinutes() + " minutes";
                }else{
                    deepSleepTime = duration.minusHours(duration.toHours()).toMinutes() + " minutes";
                }
*/

/*
                duration = Duration.ofMinutes(sleepDataUI.getLowSleepTime());
                String lowSleepTime;
                if(duration.toHours()>0){
                    lowSleepTime = duration.toHours() + " hours\n" + duration.minusHours(duration.toHours()).toMinutes() + " minutes";
                }else{
                    lowSleepTime = duration.minusHours(duration.toHours()).toMinutes() + " minutes";
                }
*/


                int sleepQuality = sleepDataUI.getSleepQuality();

                binding.sleepTimeTextView.setText(allSleepTime);
                binding.wakeUpTimesTextView.setText(wakeCount);
                binding.fallSleepTimeTextView.setText(sleepDown);
                binding.wakeUpTimeTextView.setText(sleepUp);
                binding.deepSleepTextView.setText(deepSleepTime);
                binding.lightSleepTextView.setText(lightSleepTime);
                binding.ratingBarSleepQuality2.setRating(sleepQuality);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        };

        if(dashBoardViewModel.getViewPagerCallBack().getValue()!=null){
            viewPager.unregisterOnPageChangeCallback(dashBoardViewModel.getViewPagerCallBack().getValue());
        }
        viewPager.registerOnPageChangeCallback(callback);
        dashBoardViewModel.getViewPagerCallBack().setValue(callback);
    }

    @NonNull
    private String getIntervalTime(long countDeepSleep) {
        long hours = (long) Math.floor(countDeepSleep / 60.0);
        long minutes = countDeepSleep - 60 * hours;
        String time = getStringTimeFormatted(hours, minutes);
        return time;
    }

    @NonNull
    private String getStringTimeFormatted(long hours, long minutes) {
        String time;
        if(hours >1) {
            time = hours + " hours\n" + minutes + " minutes";
        }else if(hours ==1 && minutes >0){
            time = hours + " hour\n" + minutes + " minutes";
        }else if(hours ==1 && minutes ==0){
            time = hours + " hour";
        }else {
            time = minutes + " minutes";
        }
        return time;
    }


}