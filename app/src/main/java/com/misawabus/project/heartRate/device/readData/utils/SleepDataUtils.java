package com.misawabus.project.heartRate.device.readData.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.veepoo.protocol.model.datas.SleepData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SleepDataUtils {
    public static SleepDataUI getSleepDataUIObject(@NonNull String fullData) {
        int indexOfOpenBracket = fullData.indexOf("[");
        int indexOfCloseBracket = fullData.lastIndexOf("]");
        String data = fullData.substring(indexOfOpenBracket + 1, indexOfCloseBracket);

        String[] split = data.split(", ");
        SleepDataUI sleepDataUI = new SleepDataUI();
        sleepDataUI.setIdTypeDataTable(IdTypeDataTable.Sleep);

        if (split[0].contains("date")) {
            Log.d("dateSplit", split[0]);
            String s = split[0].split("=")[1];
            String dateString = split[0].split("=")[1].substring(0, s.length());
            Date formattedDate = DateUtils.getFormattedDate(dateString, "-");
            sleepDataUI.setDateData(formattedDate);
        }
        if (split[1].contains("cali_flag")){
            sleepDataUI.setCaliFlag(Integer.parseInt(split[1].split("=")[1]));
        }
        if (split[2].contains("sleepQulity")) {
            sleepDataUI.setSleepQuality(Integer.parseInt(split[2].split("=")[1]));
        }
        if (split[3].contains("wakeCount")) {
            sleepDataUI.setWakeCount(Integer.parseInt(split[3].split("=")[1]));
        }
        if (split[4].contains("deepSleepTime")) {
            sleepDataUI.setDeepSleepTime(Integer.parseInt(split[4].split("=")[1]));
        }
        if (split[5].contains("lowSleepTime")) {
            sleepDataUI.setLowSleepTime(Integer.parseInt(split[5].split("=")[1]));
        }
        if (split[6].contains("allSleepTime")) {
            sleepDataUI.setAllSleepTime(Integer.parseInt(split[6].split("=")[1]));
        }
        if (split[7].contains("sleepLine")) {
            sleepDataUI.setData(split[7].split("=")[1]);
        }

        if (split[8].contains("sleepDown")) {
            sleepDataUI.setSleepDown(split[8].split("=")[1]);
        }


        if (split[9].contains("sleepUp")) {
            sleepDataUI.setSleepUp(split[9].split("=")[1]);
        }
        return sleepDataUI;
    }

    public static String processingSleepData(SleepData sleepData) {
        List<String> list= new ArrayList<>();
        if (sleepData.getSleepLine().isEmpty()) return "";
        list.add("date="+ sleepData.getSleepUp().year+"-" + sleepData.getSleepUp().getDate());
        list.add("cali_flag=" + sleepData.getCali_flag());
        list.add("sleepQulity=" + sleepData.getSleepQulity());
        list.add("wakeCount=" + sleepData.getWakeCount());
        list.add("deepSleepTime=" + sleepData.getDeepSleepTime());
        list.add("lowSleepTime=" + sleepData.getLowSleepTime());
        list.add("allSleepTime=" + sleepData.getAllSleepTime());
        list.add("sleepLine=" + sleepData.getSleepLine());
        list.add("sleepDown=" + sleepData.getSleepDown());
        list.add("sleepUp=" + sleepData.getSleepUp());

        return list.toString();

    }
}
