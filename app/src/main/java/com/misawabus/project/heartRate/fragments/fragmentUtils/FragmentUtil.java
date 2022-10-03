package com.misawabus.project.heartRate.fragments.fragmentUtils;

import static java.util.stream.Collectors.toList;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FragmentUtil {
    public static final String TAG = FragmentUtil.class.getSimpleName();



    public static List<Integer> getSleepDataForExcel(@NonNull String sleepLine) {

        char[] chars = sleepLine.toCharArray();
        List<Integer> integerSleeps = new ArrayList<>();
        for (int i = 1; i < chars.length - 1; i++) {
            char aChar = chars[i];
            integerSleeps.add(Integer.parseInt(String.valueOf(aChar)));
        }

        List<Integer> fullSleepData = integerSleeps.stream().map(sleepLineV -> {
            if (sleepLineV == 0) {
                return 2;
            } else if (sleepLineV == 1) {
                return 1;
            } else return 3;

        }).collect(Collectors.toList());

        return fullSleepData;

    }


    public static List<Map<String, Double>> mapToList(Map<Integer, Map<String, Double>> data){
        List<Map<String, Double>> list = new ArrayList<>();
        Stream.iterate(1, i -> ++i).limit(288).forEach(index -> {
            Map<String, Double> map = data.get(index);
            list.add(map != null ? map : new HashMap<>());

        });
        Log.d(TAG, "mapToList: index: " + list);
        return list;
    }

    public static List<Map<String, Double>> parse5MinFieldData(String stringData) {
        stringData = stringData.substring(1, stringData.lastIndexOf("}"));
        stringData = stringData.trim();
        String[] originData = stringData.split("[}], ");
        List<String> collect = Arrays.stream(originData)
                .map(s1 -> {
                    s1 = s1.replace("{", "");
                    return s1.replace("}", "");
                })
                .collect(Collectors.toList());

        List<Map<String, Double>> list = new ArrayList<>();


        IntStream.range(1, 289).forEach(i -> list.add(new HashMap<>()));

        collect.forEach(s1 -> {
            int i = s1.indexOf("=");
            int index = Integer.parseInt(s1.substring(0, i));
            String data = s1.substring(i+1);
            Arrays.stream(data.split(", ")).forEach(s2 -> {
                String[] split = s2.split("=");
                String field = split[0];
                double value = Double.parseDouble(split[1]);
                list.get(index-1).put(field, value);
            });

        });

        return list;
    }

    @NonNull
    public static Map<String, List<Double>> getSportsMapFieldsWith30MinCountValues(List<Map<String, Double>> dataMap) {

        List<List<Map<String, Double>>> list30Min = new ArrayList<>();
        List<Double> listStep30Min = new ArrayList<>();
        List<Double> listCalories30Min = new ArrayList<>();
        List<Double> listDistance30Min = new ArrayList<>();



        int size =48;
        for (int i = 0; i < size; i++) {
            List<Map<String, Double>> array = new ArrayList<>();
            list30Min.add(array);
        }

        for (int i = 0; i < dataMap.size(); i++) {
            int interval = (int) (Math.floor(i / 6.0)+1);
            List<Map<String, Double>> doubles = list30Min.get(interval-1);
            doubles.add(dataMap.get(i));
        }

        list30Min.forEach(mapList -> {
            double stepValue = mapList
                    .stream()
                    .map(list30Min2 -> list30Min2.getOrDefault("stepValue",
                            0.0))
                    .mapToDouble(value -> value).sum();
            listStep30Min.add(stepValue);

            double caloriesValue = mapList
                    .stream()
                    .map(list30Min2 -> list30Min2.getOrDefault("calValue",
                            0.0))
                    .mapToDouble(value -> value).sum();
            listCalories30Min.add(caloriesValue);

            double distanceValue = mapList
                    .stream()
                    .map(list30Min2 -> list30Min2.getOrDefault("disValue",
                            0.0))
                    .mapToDouble(value -> value)
                    .sum();
            listDistance30Min.add(distanceValue);


        });

        Map<String, List<Double>> mapFieldsWith30MinValues = new HashMap<>();
        mapFieldsWith30MinValues.put("stepValue", listStep30Min);
        mapFieldsWith30MinValues.put("calValue", listCalories30Min);
        mapFieldsWith30MinValues.put("disValue", listDistance30Min);
        return mapFieldsWith30MinValues;
    }

    @NonNull
    public static Map<String, List<Double>> getHeartRateDataGroupByFieldsWith30MinSumValues(List<Map<String, Double>> dataMap) {

        List<List<Map<String, Double>>> list30Min = new ArrayList<>();
        List<Double> listPpgs30Min = new ArrayList<>();
        List<Double> listActivity30Min = new ArrayList<>();

        int size =(int) Math.floor(dataMap.size()/ 6.0);
        for (int i = 0; i < size; i++) {
            List<Map<String, Double>> array = new ArrayList<>();
            list30Min.add(array);
        }

        for (int i = 0; i < dataMap.size(); i++) {
            int interval = (int) (Math.floor(i / 6.0)+1);
            List<Map<String, Double>> doubles = list30Min.get(interval-1);
            doubles.add(dataMap.get(i));
        }

        list30Min.forEach(maps -> {
            OptionalDouble stepValue = maps
                    .stream()
                    .map(list30Min2 -> list30Min2.getOrDefault("Ppgs", 0.0))
                    .filter(value -> value>0.0)
                    .mapToDouble(value -> value).average();
            listPpgs30Min.add(stepValue.orElse(0.0));

            OptionalDouble activityValue = maps
                    .stream()
                    .map(list30Min2 -> list30Min2.getOrDefault("Activity", 0.0))
                    .filter(value -> value>0.0)
                    .mapToDouble(value -> value).average();
            listActivity30Min.add(activityValue.orElse(0.0));
        });

        Map<String, List<Double>> mapFieldsWith30MinValues = new HashMap<>();
        mapFieldsWith30MinValues.put("Ppgs", listPpgs30Min);
        mapFieldsWith30MinValues.put("Activity", listActivity30Min);

        return mapFieldsWith30MinValues;
    }




    public static List<Map<String, Double>> getBloodPressureMapFieldsWith30MinAVGValues(List<Map<String, Double>> dataMap) {

        List<List<Map<String, Double>>> list30Min = new ArrayList<>();

        int size =48;
        for (int i = 0; i < size; i++) {
            List<Map<String, Double>> array = new ArrayList<>();
            list30Min.add(array);
        }

        for (int i = 0; i < dataMap.size(); i++) {
            int interval = (int) (Math.floor(i / 6.0)+1);
            List<Map<String, Double>> doubles = list30Min.get(interval-1);
            doubles.add(dataMap.get(i));
        }

        List<Map<String, Double>> collect = list30Min.stream().map(maps -> {

            double highValue = maps
                    .stream()
                    .map(list30Min2 -> list30Min2.getOrDefault("highValue",
                            0.0))
                    .mapToDouble(value -> value)
                    .filter(value -> value > 0.0)
                    .average().orElse(0.0);


            double lowVaamlue = maps
                    .stream()
                    .map(list30Min2 -> list30Min2.getOrDefault("lowVaamlue",
                            0.0))
                    .mapToDouble(value -> value)
                    .filter(value -> value > 0.0)
                    .average().orElse(0.0);

            Map<String, Double> mapFieldsWith30MinValues = new HashMap<>();
            mapFieldsWith30MinValues.put("highValue", highValue);
            mapFieldsWith30MinValues.put("lowVaamlue", lowVaamlue);
            return mapFieldsWith30MinValues;


        }).collect(toList());


        return collect;


    }

    public static List<Map<String, Double>> getBloodPressureMapFieldWith30MinAVGValues(List<Map<String, Double>> dataMap, String field) {

        List<List<Map<String, Double>>> list30Min = new ArrayList<>();

        int size =48;
        for (int i = 0; i < size; i++) {
            List<Map<String, Double>> array = new ArrayList<>();
            list30Min.add(array);
        }

        for (int i = 0; i < dataMap.size(); i++) {
            int interval = (int) (Math.floor(i / 6.0)+1);
            List<Map<String, Double>> doubles = list30Min.get(interval-1);
            doubles.add(dataMap.get(i));
        }

        List<Map<String, Double>> collect = list30Min.stream().map(maps -> {

            double fieldValue = maps
                    .stream()
                    .map(list30Min2 -> list30Min2.getOrDefault(field,
                            0.0))
                    .mapToDouble(value -> value)
                    .filter(value -> value > 0.0)
                    .average().orElse(0.0);


            Map<String, Double> mapFieldsWith30MinValues = new HashMap<>();
            mapFieldsWith30MinValues.put(field, fieldValue);

            return mapFieldsWith30MinValues;


        }).collect(toList());


        return collect;


    }

    public static Map<String, List<Double>> getBPDataGroupByFieldsWith30MinSumValuesForExcel(List<Map<String, Double>> dataMap) {

        List<List<Map<String, Double>>> list30Min = new ArrayList<>();



        int size =(int) Math.floor(dataMap.size()/ 6.0);
        for (int i = 0; i < size; i++) {
            List<Map<String, Double>> array = new ArrayList<>();
            list30Min.add(array);
        }

        for (int i = 0; i < dataMap.size(); i++) {
            int interval = (int) (Math.floor(i / 6.0)+1);
            List<Map<String, Double>> doubles = list30Min.get(interval-1);
            doubles.add(dataMap.get(i));
        }

        getBPDataGroupByFields(list30Min);

        return getBPDataGroupByFields(list30Min);



    }

    private static Map<String, List<Double>> getBPDataGroupByFields(List<List<Map<String, Double>>> list30Min) {
        List<Double> listHighValue30Min = new ArrayList<>();
        List<Double> listLowVaamlue30Min = new ArrayList<>();
        Map<String, List<Double>> mapFieldsWith30MinValues = new HashMap<>();
        mapFieldsWith30MinValues.put("highValue", listHighValue30Min);
        mapFieldsWith30MinValues.put("lowVaamlue", listLowVaamlue30Min);

        list30Min.forEach(maps -> {

            double highValue = maps
                    .stream()
                    .map(list30Min2 -> list30Min2.getOrDefault("highValue",
                            0.0))
                    .mapToDouble(value -> value)
                    .filter(value -> value > 0.0)
                    .average().orElse(0.0);
            mapFieldsWith30MinValues.get("highValue").add(highValue);


            double lowVaamlue = maps
                    .stream()
                    .map(list30Min2 -> list30Min2.getOrDefault("lowVaamlue",
                            0.0))
                    .mapToDouble(value -> value)
                    .filter(value -> value > 0.0)
                    .average().orElse(0.0);
            mapFieldsWith30MinValues.get("lowVaamlue").add(lowVaamlue);

        });

        return mapFieldsWith30MinValues;
    }

    public static Map<String, List<Integer>> getSleepDataForPlotting(@NonNull String sleepLine) {


        char[] chars = sleepLine.toCharArray();

        List<Integer> integerSleeps = new ArrayList<>();

        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            integerSleeps.add(Integer.parseInt(String.valueOf(aChar)));
        }

        List<Integer> deepSleep = integerSleeps.stream().map(sleepLineV -> {
            if (sleepLineV == 1) {
                return 1;
            } else if (sleepLineV == 0 /*|| sleepLineV == 1*/ || sleepLineV == 2 ) {
                return 0;
            } else return 0;

        }).collect(Collectors.toList());

        List<Integer> lightSleep = integerSleeps.stream().map(sleepLineV -> {
            if (sleepLineV == 0) {
                return 2;
            } else if (/*sleepLineV == 0 || */sleepLineV == 1 || sleepLineV == 2) {
                return 0;
            } else return 0;

        }).collect(Collectors.toList());

        List<Integer> wakeUp = integerSleeps.stream().map(sleepLineV -> {
            if (sleepLineV == 2) {
                return 3;
            } else if (sleepLineV == 0 || sleepLineV == 1 /*|| sleepLineV == 2*/) {
                return 0;
            } else return 3;

        }).collect(Collectors.toList());
/*        List<Integer> lightSleep = integerSleeps.stream().map(sleepLineV -> {
            if (sleepLineV == 0) {
                return 2;
            } else if (sleepLineV == 1) {
                return 0;
            } else return 0;

        }).collect(Collectors.toList());

        List<Integer> deepSleep = integerSleeps.stream().map(sleepLineV -> {
            if (sleepLineV == 0) {
                return 0;
            } else if (sleepLineV == 1) {
                return 1;
            } else return 0;

        }).collect(Collectors.toList());

        List<Integer> wakeUp = integerSleeps.stream().map(sleepLineV -> {
            if (sleepLineV == 0) {
                return 0;
            } else if (sleepLineV == 1) {
                return 0;
            } else return 3;

        }).collect(Collectors.toList());*/


        Map<String, List<Integer>> mapSleepDegree = new HashMap<>();
        mapSleepDegree.put("lightSleep", lightSleep);
        mapSleepDegree.put("deepSleep", deepSleep);
        mapSleepDegree.put("wakeUp", wakeUp);
        return mapSleepDegree;
    }

    public static Map<String, List<Integer>> getSleepPrecisionDataForPlotting(String sleepLine) {
        char[] chars = sleepLine.toCharArray();

        List<Integer> integerSleeps = new ArrayList<>();

        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            integerSleeps.add(Integer.parseInt(String.valueOf(aChar)));
        }

        List<Integer> deepSleep = integerSleeps.stream().map(sleepLineV -> {
            if (sleepLineV == 0) {
                return 1;
            } else if (/*sleepLineV == 0 ||*/ sleepLineV == 1 || sleepLineV == 2 || sleepLineV == 3 || sleepLineV == 4) {
                return 0;
            } else return 0;

        }).collect(Collectors.toList());

        List<Integer> lightSleep = integerSleeps.stream().map(sleepLineV -> {
            if (sleepLineV == 1) {
                return 2;
            } else if (sleepLineV == 0 || /*sleepLineV == 1 ||*/ sleepLineV == 2 || sleepLineV == 3 || sleepLineV == 4) {
                return 0;
            } else return 0;

        }).collect(Collectors.toList());

        List<Integer> rapidEyeMovement = integerSleeps.stream().map(sleepLineV -> {
            if (sleepLineV == 2) {
                return 3;
            } else if (sleepLineV == 0 || sleepLineV == 1 || /*sleepLineV == 2 ||*/ sleepLineV == 3 || sleepLineV == 4) {
                return 0;
            } else return 0;

        }).collect(Collectors.toList());


        List<Integer> insomnia = integerSleeps.stream().map(sleepLineV -> {
            if (sleepLineV == 3) {
                return 4;
            } else if (sleepLineV == 0 || sleepLineV == 1 || sleepLineV == 2 || /*sleepLineV == 3 ||*/ sleepLineV == 4) {
                return 0;
            } else return 0;

        }).collect(Collectors.toList());

        List<Integer> wakeUp = integerSleeps.stream().map(sleepLineV -> {
            if (sleepLineV == 4) {
                return 5;
            } else if (sleepLineV == 0 || sleepLineV == 1 || sleepLineV == 2 || sleepLineV == 3 /*|| sleepLineV == 4*/) {
                return 0;
            } else return 0;

        }).collect(Collectors.toList());


        Map<String, List<Integer>> mapSleepDegree = new HashMap<>();
        mapSleepDegree.put("deepSleep", deepSleep);
        mapSleepDegree.put("lightSleep", lightSleep);
        mapSleepDegree.put("rapidEyeMovement", rapidEyeMovement);
        mapSleepDegree.put("insomnia", insomnia);
        mapSleepDegree.put("wakeUp", wakeUp);



        return mapSleepDegree;
    }
}
