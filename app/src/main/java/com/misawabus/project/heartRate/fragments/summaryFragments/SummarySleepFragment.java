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
import androidx.recyclerview.widget.RecyclerView;

import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.adapters.recyclerView.FillViewsFieldsWithEntitiesValues;
import com.misawabus.project.heartRate.adapters.recyclerView.OnEntityClickListener;
import com.misawabus.project.heartRate.adapters.recyclerView.RecyclerViewBuilder;
import com.misawabus.project.heartRate.adapters.recyclerView.ViewsInRowHolder;
import com.misawabus.project.heartRate.adapters.viewHolders.summarySleep.ViewsInSleepRowHolder;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.databinding.FragmentSummarySleepBinding;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.plotting.PlotUtilsSleep;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.viewModels.SleepDataUIViewModel;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class SummarySleepFragment  extends SummaryFragment {
    private static final String TAG = SummarySleepFragment.class.getSimpleName();
    private FragmentSummarySleepBinding binding;
    private DashBoardViewModel dashBoardViewModel;
    private SleepDataUIViewModel sleepDataUIViewModel;
    private DeviceViewModel deviceViewModel;


    public SummarySleepFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
        sleepDataUIViewModel = new ViewModelProvider(requireActivity()).get(SleepDataUIViewModel.class);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_summary_sleep,
                container,
                false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button backToMainFragButton = binding.buttonBackFromSummarySleepFrag;
        Button selectDateButton = binding.buttonSleepDateSelection;
        Button shareButton = binding.buttonShareSleep;

        backToMainFragButton.setOnClickListener(view1 -> backMainFragment());
        shareButton.setOnClickListener(viewToShare -> shareScreen());

        Date todayFormattedDate = DateUtils.getTodayFormattedDate();
        getDataFromDB(todayFormattedDate, dataFromDB -> setFragmentViews(todayFormattedDate, dataFromDB));

        selectDateButton.setOnClickListener(view1 ->
                getDateFromCalendar(selectedDate -> getDataFromDB(selectedDate,
                        dataFromDB -> setFragmentViews(selectedDate, dataFromDB))));

    }

    private void setFragmentViews(Date selectedDate, List<SleepDataUI> sleepDataUIS) {
        Log.d(TAG, "setFragmentViews: " + sleepDataUIS);
        setTextButtonDate(selectedDate, binding.buttonSleepDateSelection);
        if(sleepDataUIS==null || sleepDataUIS.size()==0) {
            binding.imageViewSleep.setVisibility(View.VISIBLE);
            binding.recyclerViewSleep.setVisibility(View.GONE);
            return;
        }
        binding.imageViewSleep.setVisibility(View.GONE);
        binding.recyclerViewSleep.setVisibility(View.VISIBLE);
        buildRecyclerView(sleepDataUIS,
                getContext(),
                binding.recyclerViewSleep,
                R.layout.row_layout_sleep_card,
                new ViewsInSleepRowHolder()
        );

    }

    private void getDataFromDB(Date date, Consumer<List<SleepDataUI>> sleepDataUIList) {
        SleepDataUIViewModel.getListRows(date,
                deviceViewModel.getMacAddress()).observe(getViewLifecycleOwner(),
                sleepDataUIList::accept);
    }


    private void buildRecyclerView(List<? extends SleepDataUI> data,
                                   Context context,
                                   RecyclerView recyclerView,
                                   int layout_resource,
                                   ViewsInRowHolder viewsInRowHolder) {

        OnEntityClickListener onEntityClickListener = (id, position) -> Log.d("RECYCLER", String.valueOf(id));

        FillViewsFieldsWithEntitiesValues fillViewsFieldsWithEntitiesValues = (position, viewsInRowHolder1) -> {

            Optional<? extends SleepDataUI> first = data.stream()
                    .filter(periodSleep ->
                    {

                        return periodSleep.getIndex() == position;
                    })
                    .findFirst();
            if(!first.isPresent()) return;
            SleepDataUI sleepDataUI = first.get();

            if (viewsInRowHolder1 instanceof ViewsInSleepRowHolder) {
                ViewsInSleepRowHolder viewsInSleepRowHolder = (ViewsInSleepRowHolder) viewsInRowHolder1;



                Map<String, List<Integer>> sleepData;
                if(sleepDataUI.idTypeDataTable.equals(IdTypeDataTable.Sleep)){
                    sleepData = FragmentUtil.getSleepDataForPlotting(sleepDataUI.getData());
                    PlotUtilsSleep.plotSleepIntegerListData(sleepDataUI,
                            sleepData.get("lightSleep"),
                            sleepData.get("deepSleep"),
                            sleepData.get("wakeUp"),
                            viewsInSleepRowHolder.plot);
                }else if(sleepDataUI.idTypeDataTable.equals(IdTypeDataTable.SleepPrecision)){
                    sleepData = FragmentUtil.getSleepPrecisionDataForPlotting(sleepDataUI.getData());
                    PlotUtilsSleep.plotSleepPrecisionIntegerListData(sleepDataUI,
                            sleepData.get("deepSleep"),
                            sleepData.get("lightSleep"),
                            sleepData.get("rapidEyeMovement"),
                            sleepData.get("insomnia"),
                            sleepData.get("wakeUp"),
                            viewsInSleepRowHolder.plot);
                }

/*                Map<String, List<Integer>> sleepData = FragmentUtil.getSleepDataForPlotting(sleepDataUI.getData());
                PlotUtilsSleep.plotSleepIntegerListData(sleepDataUI, sleepData.get("lightSleep"),
                        sleepData.get("deepSleep"),
                        sleepData.get("wakeUp"),
                        viewsInSleepRowHolder.plot
                );*/
                Duration duration = Duration.ofMinutes(sleepDataUI.getAllSleepTime());
                String allSleepTime = duration.toHours() + " hours " + duration.minusHours(duration.toHours()).toMinutes() +  " minutes";
                String wakeCount = String.valueOf(sleepDataUI.getWakeCount());
                String sleepDown = DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepDataUI.getSleepDown()).toString();
                String sleepUp = DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepDataUI.getSleepUp()).toString();
                String deepSleepTime = sleepDataUI.getDeepSleepTime() + " minutes";
                String lowSleepTime = sleepDataUI.getLowSleepTime() + " minutes";
                int sleepQuality = sleepDataUI.getSleepQuality();

                viewsInSleepRowHolder.allSleepTime.setText(allSleepTime);
                viewsInSleepRowHolder.wakeCount.setText(wakeCount);
                viewsInSleepRowHolder.sleepDown.setText(sleepDown);
                viewsInSleepRowHolder.sleepUp.setText(sleepUp);
                viewsInSleepRowHolder.deepSleepTime.setText(deepSleepTime);
                viewsInSleepRowHolder.lowSleepTime.setText(lowSleepTime);
                viewsInSleepRowHolder.ratingBarSleepQuality.setRating(sleepQuality);

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
