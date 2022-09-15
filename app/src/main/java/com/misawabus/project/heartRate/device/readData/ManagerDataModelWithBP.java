package com.misawabus.project.heartRate.device.readData;

import androidx.lifecycle.LifecycleOwner;

import com.misawabus.project.heartRate.Utils.DBops;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.device.DataContainers.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;

import java.util.HashMap;
import java.util.Map;

public class ManagerDataModelWithBP implements ManagerData {
    private Map<String, DataFiveMinAvgDataContainer> dataFiveMinAVGAllIntervalsMap = new HashMap<>();
    private final ManagerData managerData;
    private final DataFiveMinAvgDataContainer bloodPressureDataFiveMinAvgDataContainer;
    private final DeviceViewModel deviceViewModel;
    private final LifecycleOwner activity;

    public ManagerDataModelWithBP(ManagerData managerData,
                                  DataFiveMinAvgDataContainer bloodPressureDataFiveMinAvgDataContainer,
                                  DeviceViewModel deviceViewModel,
                                  LifecycleOwner activity) {
        this.managerData = managerData;
        this.bloodPressureDataFiveMinAvgDataContainer = bloodPressureDataFiveMinAvgDataContainer;
        this.deviceViewModel = deviceViewModel;
        this.activity = activity;
    }

    @Override
    public Map<String, DataFiveMinAvgDataContainer> getDataFiveMinAVGAllIntervalsMap() {
        if(managerData==null) return new HashMap<>();
        Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAvgDataContainerMap;
        stringDataFiveMinAvgDataContainerMap = Map.copyOf(managerData.getDataFiveMinAVGAllIntervalsMap());
        stringDataFiveMinAvgDataContainerMap
                .put(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName(),
                        bloodPressureDataFiveMinAvgDataContainer);
        dataFiveMinAVGAllIntervalsMap = stringDataFiveMinAvgDataContainerMap;
        return dataFiveMinAVGAllIntervalsMap;
    }

    @Override
    public void storeInLocalDataBase() {
        managerData.storeInLocalDataBase();
        DBops.updateBloodPressureRow(IdTypeDataTable.BloodPressure,
                bloodPressureDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                bloodPressureDataFiveMinAvgDataContainer.getStringDate(),
                deviceViewModel.getMacAddress(),
                activity);
    }
}
