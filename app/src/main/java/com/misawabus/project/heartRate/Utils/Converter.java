package com.misawabus.project.heartRate.Utils;

import androidx.room.TypeConverter;



import java.util.Date;

public class Converter {
    @TypeConverter
    public static Date convertTimeStampToDate(Long value){
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long convertDateToTimeStamp(Date date){
        return date == null ? null : date.getTime();
    }



}
