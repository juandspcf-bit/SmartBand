package com.misawabus.project.heartRate.fragments.summaryFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidplot.xy.XYPlot;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.adapters.viewHolders.summarySleep.ViewsInSleepRowHolder;
import com.misawabus.project.heartRate.adapters.viewHolders.summarySleep.ViewsInSleepRowHolderV2;
import com.misawabus.project.heartRate.adapters.viewPager.FillViewsFieldsWithEntitiesValues;
import com.misawabus.project.heartRate.adapters.viewPager.OnEntityClickListener;
import com.misawabus.project.heartRate.adapters.viewPager.ViewPagerAdapter;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.databinding.FragmentSummarySleepBinding;
import com.misawabus.project.heartRate.databinding.FragmentSummarySleepV2Binding;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.plotting.PlotUtilsSleep;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.viewModels.SleepDataUIViewModel;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
        Log.d(TAG, "setFragmentViews: " + sleepDataUIS);
        setTextButtonDate(selectedDate, binding.sleepDateSelectionButtonV2);
        if(sleepDataUIS==null || sleepDataUIS.size()==0) {
            //binding.imageViewSleep.setVisibility(View.VISIBLE);
            //binding.recyclerViewSleep.setVisibility(View.GONE);
            return;
        }
        //binding.imageViewSleep.setVisibility(View.GONE);
        //binding.recyclerViewSleep.setVisibility(View.VISIBLE);

        buildViewPagerView(sleepDataUIS,
                getContext(),
                binding.viewPager,
                R.layout.row_layout_sleep_card_v2,
                new ViewsInSleepRowHolderV2()
        );

    }

    private void buildViewPagerView(List<?> data,
                                    Context context,
                                    ViewPager2 viewPager,
                                    int row_layout_sleep_card,
                                    ViewsInSleepRowHolderV2 viewsInSleepRowHolder) {

        OnEntityClickListener onEntityClickListener = (id, position) -> Log.d("RECYCLER", String.valueOf(id));

        FillViewsFieldsWithEntitiesValues fillViewsFieldsWithEntitiesValues = (position, viewsInRowHolder) -> {

            if (viewsInRowHolder instanceof ViewsInSleepRowHolderV2) {


                SleepDataUI sleepDataUI = (SleepDataUI) data.get(position);


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


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(data.size(),
                row_layout_sleep_card,
                context,
                onEntityClickListener,
                fillViewsFieldsWithEntitiesValues,
                viewsInSleepRowHolder);
        viewPager.setAdapter(viewPagerAdapter);

    }


}