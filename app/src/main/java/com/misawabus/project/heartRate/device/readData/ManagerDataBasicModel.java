package com.misawabus.project.heartRate.device.readData;

import androidx.lifecycle.LifecycleOwner;

import com.misawabus.project.heartRate.Utils.DBops;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;

import java.util.HashMap;
import java.util.Map;

public class ManagerDataBasicModel implements ManagerData {
    private final Map<String, DataFiveMinAvgDataContainer> dataFiveMinAVGAllIntervalsMap = new HashMap<>();
    private final DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer;
    private final DataFiveMinAvgDataContainer heartRateDataFiveMinAvgDataContainer;
    private final DeviceViewModel deviceViewModel;
    private final LifecycleOwner activity;

    public ManagerDataBasicModel(DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer,
                                 DataFiveMinAvgDataContainer heartRateDataFiveMinAvgDataContainer,
                                 DeviceViewModel deviceViewModel,
                                 LifecycleOwner activity) {
        this.sportsDataFiveMinAvgDataContainer = sportsDataFiveMinAvgDataContainer;
        this.heartRateDataFiveMinAvgDataContainer = heartRateDataFiveMinAvgDataContainer;
        this.deviceViewModel = deviceViewModel;
        this.activity = activity;
    }

    @Override
    public Map<String, DataFiveMinAvgDataContainer> getDataFiveMinAVGAllIntervalsMap() {
        dataFiveMinAVGAllIntervalsMap
                .put(SportsData5MinAvgDataContainer.class.getSimpleName(),
                        sportsDataFiveMinAvgDataContainer);
        dataFiveMinAVGAllIntervalsMap
                .put(HeartRateData5MinAvgDataContainer.class.getSimpleName(),
                        heartRateDataFiveMinAvgDataContainer);

        return dataFiveMinAVGAllIntervalsMap;

    }

    @Override
    public void storeInLocalDataBase() {
        DBops.updateSportsRow(IdTypeDataTable.SportsFiveMin,
                sportsDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                sportsDataFiveMinAvgDataContainer.getStringDate(),
                deviceViewModel.getMacAddress(),
                activity);
        DBops.updateHeartRateRow(IdTypeDataTable.HeartRateFiveMin,
                heartRateDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                heartRateDataFiveMinAvgDataContainer.getStringDate(),
                deviceViewModel.getMacAddress(),
                activity
        );


    }
}
