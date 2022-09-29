package com.misawabus.project.heartRate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidplot.ui.SeriesRenderer;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.Utils.Utils;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.databinding.FragmentGeneralSummaryBinding;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class SummaryActivityFragment extends Fragment {
    private FragmentGeneralSummaryBinding binding;
    private DeviceViewModel deviceViewModel;
    private DashBoardViewModel dashBoardViewModel;

    public SummaryActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_general_summary, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dashBoardViewModel.getRealTimeTesterClass().startTemperatureDetection();
                //dashBoardViewModel.getDeviceSettingsManager().readDeviceSettings();
            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashBoardViewModel.getRealTimeTesterClass().stopTemperatureDetection();
            }
        });






    }




    static class MyBarFormatter extends BarFormatter {

        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer doGetRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    static class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        @Override
        public MyBarFormatter getFormatter(int index, XYSeries series) {
            return getFormatter(series);
        }
    }

    static class RowContainer {
        private IdTypeDataTable idTypeDataTable;
        private double average;
        private LocalDate date;

        public RowContainer(IdTypeDataTable idTypeDataTable, double average, LocalDate date) {
            this.idTypeDataTable = idTypeDataTable;
            this.average = average;
            this.date = date;
        }



        public IdTypeDataTable getIdTypeDataTable() {
            return idTypeDataTable;
        }

        public void setIdTypeDataTable(IdTypeDataTable idTypeDataTable) {
            this.idTypeDataTable = idTypeDataTable;
        }


        public double getAverage() {
            return average;
        }

        public void setAverage(double average) {
            this.average = average;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        @NonNull
        @Override
        public String toString() {
            return "RowContainer{" +
                    "idTypeDataTable=" + idTypeDataTable +
                    ", average=" + average +
                    ", date=" + date +
                    '}';
        }
    }
}