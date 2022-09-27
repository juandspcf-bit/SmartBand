package com.misawabus.project.heartRate.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtils {
    @Nullable
    public static Date getFormattedDate(String myDate, String regex) {
        String fullDate = getStringFormattedDate(myDate, regex);

        Date formattedDate = null;
        try {
            formattedDate = DateFormat.getDateInstance().parse(fullDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    @NonNull
    public static String getStringFormattedDate(String myDate, String regex) {
        String[] split = myDate.split(regex);
        int parseMonthI = Integer.parseInt(split[1]);
        String parseMonth = parserMonth(parseMonthI);
        String fullDate = parseMonth + " " + split[2] + ", " + split[0];
        return fullDate;
    }

    public static LocalDate getLocalDate(Date date, String regex){
        String dateString = DateFormat
                .getDateInstance(DateFormat.SHORT,
                        Locale.JAPAN)
                .format(date);
        final String[] split = dateString.split(regex);
        LocalDate localDate = LocalDate.of(Integer.parseInt(split[0]),
                Integer.parseInt(split[1]),
                Integer.parseInt(split[2]));
        return localDate;
    }

    public static LocalTime getLocalTimeFromVeepooTimeDateObj(String time){
        time = time.substring(time.lastIndexOf("[") + 1, time.lastIndexOf("]"));
        String sleepUpTime = time.split(" ")[1];
        String[] sleepUpTimeSplit = sleepUpTime.split(":");

        return LocalTime.of(Integer.parseInt(sleepUpTimeSplit[0]),
                Integer.parseInt(sleepUpTimeSplit[1]),
                Integer.parseInt(sleepUpTimeSplit[2]));

    }


    public static String parserMonth(int month){
        List<String> monthsList = Arrays.
                asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" );
        return ((month-1)<=11 && (month-1)>=0)? monthsList.get(month-1):"";
    }



    public static Date getTodayFormattedDate() {
        return getDate(LocalDate.now(), 0);
    }
    public static Date getPastYesterdayFormattedDate() {
        return getDate(LocalDate.now(), 2);
    }

    public static Date getYesterdayFormattedDate() {
        return getDate(LocalDate.now(), 1);
    }

    @Nullable
    private static Date getDate(LocalDate now, int i) {
        LocalDate date = now.minusDays(i);
        String s = date.getYear() + "/" + date.getMonthValue() + "/" + date.getDayOfMonth();
        return getFormattedDate(s, "/");
    }
}
