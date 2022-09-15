package com.misawabus.project.heartRate.device.readData;

import androidx.lifecycle.LifecycleOwner;

import com.misawabus.project.heartRate.Database.entities.Sop2;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

public interface ManagerData {

    default boolean setDashBoardViewModelLiveData(DashBoardViewModel dashBoardViewModel, DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer, Map<String, DataFiveMinAvgDataContainer> dataFiveMinAVGAllIntervalsMap) {
        String stringDate = sportsDataFiveMinAvgDataContainer.getStringDate();
        if (stringDate == null) return true;
        Date formattedDate = DateUtils.getFormattedDate(stringDate, "-");
        LocalDate localDate = DateUtils.getLocalDate(formattedDate, "/");
        if (localDate.compareTo(LocalDate.now()) == 0) {
            dashBoardViewModel.setTodayFullData5MinAvgAllIntervals(dataFiveMinAVGAllIntervalsMap);
        } else if (localDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
            dashBoardViewModel.setYesterdayFullData5MinAvgAllIntervals(dataFiveMinAVGAllIntervalsMap);
        } else if (localDate.compareTo(LocalDate.now().minusDays(2)) == 0) {
            dashBoardViewModel.setPastYesterdayFullData5MinAvgAllIntervals(dataFiveMinAVGAllIntervalsMap);
        }
        return false;
    }

    Map<String, DataFiveMinAvgDataContainer> getDataFiveMinAVGAllIntervalsMap();

    void storeInLocalDataBase();

}
