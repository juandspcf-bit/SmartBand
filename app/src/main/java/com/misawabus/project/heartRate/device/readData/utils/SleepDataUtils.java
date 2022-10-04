package com.misawabus.project.heartRate.device.readData.utils;

import androidx.annotation.NonNull;

import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.SleepPrecisionData;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SleepDataUtils {


    @NonNull
    @Contract("_, _ -> new")
    public static SleepDataUI getSleepDataUIObject(SleepData sleepData, String MacAddresses) {
        SleepDataUI sleepDataUIObject = new SleepDataUI();

        if(sleepData == null) return sleepDataUIObject;

        sleepDataUIObject.setMacAddress(MacAddresses);

        Date formattedDate = DateUtils.getFormattedDate(sleepData.getDate(), "-");
        sleepDataUIObject.setDateData(formattedDate);

        sleepDataUIObject.setCaliFlag(sleepData.getCali_flag());

        sleepDataUIObject.setSleepQuality(sleepData.getSleepQulity());

        sleepDataUIObject.setWakeCount(sleepData.getWakeCount());

        sleepDataUIObject.setDeepSleepTime(sleepData.getDeepSleepTime());

        sleepDataUIObject.setLowSleepTime(sleepData.getLowSleepTime());

        sleepDataUIObject.setAllSleepTime(sleepData.getAllSleepTime());

        sleepDataUIObject.setData(sleepData.getSleepLine());

        sleepDataUIObject.setSleepDown(sleepData.getSleepDown().toString());

        sleepDataUIObject.setSleepUp(sleepData.getSleepUp().toString());

        if(sleepData instanceof SleepPrecisionData){
            sleepDataUIObject.setIdTypeDataTable(IdTypeDataTable.SleepPrecision);
            sleepDataUIObject.setSleepEfficiencyScore(((SleepPrecisionData) sleepData).getSleepEfficiencyScore());

        }else {
            sleepDataUIObject.setIdTypeDataTable(IdTypeDataTable.Sleep);
        }


        return sleepDataUIObject;
    }
}
