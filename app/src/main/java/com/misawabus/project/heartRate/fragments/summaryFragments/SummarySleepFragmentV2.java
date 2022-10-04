package com.misawabus.project.heartRate.fragments.summaryFragments;

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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

        List<SleepDataUI> sortedSleepData = sortSleepListData(sleepDataUIS);
        Map<Integer,List<SleepDataUI>> map = new HashMap<>();

        int counter = 0;
        for (int i = 0; i<sortedSleepData.size(); ++i) {
            if (i > 0) {
                LocalTime localTimeSleepUp = DateUtils.getLocalTimeFromVeepooTimeDateObj(sortedSleepData.get(i - 1).getSleepUp());
                LocalTime localTimeSleepDown = DateUtils.getLocalTimeFromVeepooTimeDateObj(sortedSleepData.get(i).getSleepDown());
                int differenceTime = localTimeSleepDown.getSecond() - localTimeSleepUp.getSecond();
                if (differenceTime < 20 * 60) {
                    map.computeIfAbsent(counter, k -> new ArrayList<>());
                    map.get(counter).add(sortedSleepData.get(i - 1));
                    //map.get(counter).add(sortedSleepData.get(i));

                } else {
                    map.get(counter).add(sortedSleepData.get(i - 1));
                    ++counter;
/*                    map.computeIfAbsent(counter, k -> new ArrayList<>());
                    map.get(counter).add(sortedSleepData.get(i));*/
                }

            }
        }

        map.forEach((k,v)->{
            Log.d(TAG, "setFragmentViews: joinData  k="+ k + "   " + v);
        });



        List<SleepDataUI> joinData = new ArrayList<>();
        map.forEach((k, v)->{
            List<SleepDataUI> sleepDataUIS1 = map.get(k);
            if(sleepDataUIS1.size()>1){
                String sleepUp = sleepDataUIS1.get(0).getSleepUp();
                String sleepDown = sleepDataUIS1.get(sleepDataUIS1.size() - 1).getSleepDown();
                SleepDataUI sleepDataUI = new SleepDataUI();
                sleepDataUI.setSleepUp(sleepUp);
                sleepDataUI.setSleepDown(sleepDown);
                joinData.add(sleepDataUI);
                String JoinDataSleepLine = sleepDataUIS1.stream().map(SleepDataUI::getData).collect(Collectors.joining());
                sleepDataUI.setData(JoinDataSleepLine);
                sleepDataUI.setIdTypeDataTable( sleepDataUIS1.get(0).idTypeDataTable);
                int allSleepTime = sleepDataUIS1.stream().map(SleepDataUI::getAllSleepTime).mapToInt(Integer::intValue).sum();
                sleepDataUI.setAllSleepTime(allSleepTime);
                double avgScoreSleep = sleepDataUIS1.stream().map(SleepDataUI::getSleepQuality).mapToInt(Integer::intValue).average().orElse(0);
                sleepDataUI.setSleepQuality((int) Math.ceil(avgScoreSleep));
                joinData.add(sleepDataUI);

            }else {
                joinData.add(sleepDataUIS1.get(0));
            }
        });



        buildViewPagerView(sortedSleepData,
                getContext(),
                binding.viewPager,
                R.layout.row_layout_sleep_card_v2,
                new ViewsInSleepRowHolderV2()
        );



    }

    private List<SleepDataUI> sortSleepListData(List<SleepDataUI> sleepDataUIS) {
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

        List<SleepDataUI> sortedSleepData = collect
                .stream()
                .map(mapSleep::get)
                .collect(Collectors.toList());
        return sortedSleepData;
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
                    binding.flowCardsInfo.removeView(binding.insomniaSleepCardView);
                    binding.insomniaSleepCardView.setVisibility(View.GONE);
                    binding.flowCardsInfo.removeView(binding.remSleepCardView);
                    binding.remSleepCardView.setVisibility(View.GONE);
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


                String allSleepTime;


                LocalTime localTimeFromVeepooTimeDateObj = DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepDataUI.getSleepDown());
                String sleepDown = localTimeFromVeepooTimeDateObj.toString();

                LocalTime localTimeFromVeepooTimeDateObj1 = DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepDataUI.getSleepUp());
                String sleepUp = localTimeFromVeepooTimeDateObj1.toString();

                LocalTime localTime = localTimeFromVeepooTimeDateObj1.minusHours(localTimeFromVeepooTimeDateObj.getHour());
                LocalTime localTime1 = localTime.minusMinutes(localTimeFromVeepooTimeDateObj.getMinute());
                int hours = localTime1.getHour();
                int minutes = localTime1.getMinute();
                allSleepTime = getStringTimeFormatted(hours, minutes);

                long allSleepTimeMinutes = hours * 60L + minutes;


                Map<String, List<Integer>> sleepDataMapLines = FragmentUtil.getSleepPrecisionDataForPlotting(sleepDataUI.getData());
                List<Integer> deepSleep = sleepDataMapLines.get("deepSleep");
                String deepSleepTime = getTypeSleepFormattedStringCount(deepSleep, sleepDataUI.idTypeDataTable);
                long deepSleepCountMin = getTypeSleepFormattedCountHourMin(deepSleep, sleepDataUI.idTypeDataTable);
                int deepSleepPercen = (int) (deepSleepCountMin*(1.0/allSleepTimeMinutes)*100.0);
                binding.deepSleepBar.setProgress(deepSleepPercen);
                binding.deeSleepPercentageTextView.setText(String.format(Locale.JAPAN, "%d%c of deep sleep", deepSleepPercen, '%'));

                List<Integer> lightSleep = sleepDataMapLines.get("lightSleep");
                String lightSleepTime = getTypeSleepFormattedStringCount(lightSleep, sleepDataUI.idTypeDataTable);
                long lightSleepCountMin = getTypeSleepFormattedCountHourMin(lightSleep, sleepDataUI.idTypeDataTable);
                int lightSleepPercen = (int) (lightSleepCountMin*(1.0/allSleepTimeMinutes)*100.0);
                binding.lightSleepBar.setProgress(lightSleepPercen);
                binding.lightSleepPercenextView.setText(String.format(Locale.JAPAN, "%d%c of light sleep", lightSleepPercen, '%'));


                if( sleepDataUI.getIdTypeDataTable().equals(IdTypeDataTable.SleepPrecision)){
                    List<Integer> rapidEyeMovement = sleepDataMapLines.get("rapidEyeMovement");
                    String rapidEyeMovementTime = getTypeSleepFormattedStringCount(rapidEyeMovement, sleepDataUI.idTypeDataTable);
                    List<Integer> insomnia = sleepDataMapLines.get("insomnia");
                    String insomniaTime = getTypeSleepFormattedStringCount(insomnia, sleepDataUI.idTypeDataTable);
                    binding.remSleepCardViewTextView.setText(rapidEyeMovementTime);
                    binding.insomniaSleepTextView.setText(insomniaTime);

                    long remSleepCountMin = getTypeSleepFormattedCountHourMin(rapidEyeMovement, sleepDataUI.idTypeDataTable);
                    int remSleepPercen = (int) (remSleepCountMin*(1.0/allSleepTimeMinutes)*100.0);
                    binding.remSleepBar.setProgress(remSleepPercen);
                    binding.remSleepPercenTextView.setText(String.format(Locale.JAPAN, "%d%c of rem sleep", remSleepPercen, '%'));
                    long insomniaSleepCountMin = getTypeSleepFormattedCountHourMin(insomnia, sleepDataUI.idTypeDataTable);
                    int insomniaPercen = (int) (insomniaSleepCountMin*(1.0/allSleepTimeMinutes)*100.0);
                    binding.insomniaSleepBar.setProgress(insomniaPercen);
                    binding.insomniaPercenTextView.setText(String.format(Locale.JAPAN, "%d%c of insomnia", insomniaPercen, '%'));
                }

                List<Integer> awakeLine = sleepDataMapLines.get("wakeUp");
                String awakeTime = getTypeSleepFormattedStringCount(awakeLine, sleepDataUI.idTypeDataTable);


                int wakeCount1 = sleepDataUI.getWakeCount();
                String wakeCount;
                if(sleepDataUI.getIdTypeDataTable().equals(IdTypeDataTable.SleepPrecision)){
                    if(position>0){
                        int wakeCountInt = wakeCount1 - data.get(position - 1).getWakeCount();
                        if (wakeCountInt<0) wakeCountInt =0;
                        wakeCount = String.valueOf(wakeCountInt);
                    }else {
                        wakeCount = String.valueOf(wakeCount1);
                    }
                }else {
                    wakeCount = String.valueOf(wakeCount1);
                }








                int sleepQuality= sleepDataUI.getSleepQuality();


                binding.sleepTimeTextView.setText(allSleepTime);
                binding.awakeMinutesTextView.setText(awakeTime);
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
    private String getTypeSleepFormattedStringCount(List<Integer> typeSleep, IdTypeDataTable idTypeDataTable) {
        String typeSleepTime = "0";
        long countTypeSleep=0;

        if(typeSleep !=null && idTypeDataTable.equals(IdTypeDataTable.Sleep)){
            countTypeSleep = typeSleep.stream().filter(data -> data > 0).count()*5;
            typeSleepTime = getIntervalTime(countTypeSleep);
        }else if(typeSleep != null && idTypeDataTable.equals(IdTypeDataTable.SleepPrecision)){
            countTypeSleep = typeSleep.stream().filter(data -> data > 0).count();
            typeSleepTime = getIntervalTime(countTypeSleep);
        }
        return typeSleepTime;
    }

    @NonNull
    private String getIntervalTime(long countDeepSleep) {
        long hours = (long) Math.floor(countDeepSleep / 60.0);
        long minutes = countDeepSleep - 60 * hours;
        String time = getStringTimeFormatted(hours, minutes);
        return time;
    }



    @NonNull
    private long getTypeSleepFormattedCountHourMin(List<Integer> typeSleep, IdTypeDataTable idTypeDataTable) {
         long countDeepSleep=0;

        long hours = 0;
        long minutes = 0;
        if(typeSleep !=null && idTypeDataTable.equals(IdTypeDataTable.Sleep)){
            countDeepSleep = typeSleep.stream().filter(data -> data > 0).count()*5;

        }else if(typeSleep != null && idTypeDataTable.equals(IdTypeDataTable.SleepPrecision)){
            countDeepSleep = typeSleep.stream().filter(data -> data > 0).count();

        }

        return countDeepSleep;
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