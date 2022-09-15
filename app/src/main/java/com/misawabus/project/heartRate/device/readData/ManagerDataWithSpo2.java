package com.misawabus.project.heartRate.device.readData;

import androidx.lifecycle.LifecycleOwner;

import com.misawabus.project.heartRate.Utils.DBops;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.Sop2HData5MinAvgDataContainer;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;

import java.util.HashMap;
import java.util.Map;

public class ManagerDataWithSpo2 implements ManagerData{
    private Map<String, DataFiveMinAvgDataContainer> dataFiveMinAVGAllIntervalsMap = new HashMap<>();
    private final ManagerData managerData;
    private final DataFiveMinAvgDataContainer spo2DataFiveMinAvgDataContainer;
    private final DeviceViewModel deviceViewModel;
    private final LifecycleOwner activity;

    public ManagerDataWithSpo2(ManagerData managerData,
                               DataFiveMinAvgDataContainer spo2DataFiveMinAvgDataContainer,
                               DeviceViewModel deviceViewModel,
                               LifecycleOwner activity) {
        this.managerData = managerData;
        this.spo2DataFiveMinAvgDataContainer = spo2DataFiveMinAvgDataContainer;
        this.deviceViewModel = deviceViewModel;
        this.activity = activity;
    }

    @Override
    public Map<String, DataFiveMinAvgDataContainer> getDataFiveMinAVGAllIntervalsMap() {
        if(managerData==null) return new HashMap<>();
        Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAvgDataContainerMap;
        stringDataFiveMinAvgDataContainerMap = Map.copyOf(managerData.getDataFiveMinAVGAllIntervalsMap());
        stringDataFiveMinAvgDataContainerMap
                .put(Sop2HData5MinAvgDataContainer.class.getSimpleName(),
                        spo2DataFiveMinAvgDataContainer);
        dataFiveMinAVGAllIntervalsMap = stringDataFiveMinAvgDataContainerMap;
        return dataFiveMinAVGAllIntervalsMap;
    }

    @Override
    public void storeInLocalDataBase() {
        managerData.storeInLocalDataBase();
        DBops.updateSpo2Row(spo2DataFiveMinAvgDataContainer.getDoubleMap().toString(),
                spo2DataFiveMinAvgDataContainer.getStringDate(),
                deviceViewModel.getMacAddress(),
                activity);

    }
}
