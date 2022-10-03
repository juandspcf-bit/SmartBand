package com.misawabus.project.heartRate.fragments.summaryFragments.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class UtilsSummaryFrag {

    private static final String TAG = UtilsSummaryFrag.class.getSimpleName();
    public static Map<Integer, String> activityZonesMapping = new HashMap<>();

    static {
        activityZonesMapping.put(1, "Inactive region Zone");
        activityZonesMapping.put(2, "Healths Improvement Zone");
        activityZonesMapping.put(3, "Fitness Zone");
        activityZonesMapping.put(4, "Performance Zone");
        activityZonesMapping.put(5, "High Performance Zone");
    }

    public static void interpolateSeries(Double[] seriesDouble, Double[] axisPoints, int fullLengthSeries, List<List<Double>> seriesList) {
        if(seriesDouble.length==1) return;
        for(int i = 0; i< fullLengthSeries; i++){
            axisPoints[i]= (double) i;
            if(seriesDouble[i]>0.0){
                var join = new ArrayList<Double>();
                join.add(seriesDouble[i]);
                join.add(axisPoints[i]);
                seriesList.add(join);
            }
        }
        Log.d(TAG, "interpolateSeries: " + seriesList);
        if(seriesList.size()<3) return;

        var filteredLengthSeries = seriesList.size();
        var seriesDoubleFiltered = new double[filteredLengthSeries];
        var axisPointsFiltered = new double[filteredLengthSeries];

        for (int i = 0; i < filteredLengthSeries; i++) {
            seriesDoubleFiltered[i] = seriesList.get(i).get(0);
            axisPointsFiltered[i] = seriesList.get(i).get(1);
        }


        LinearInterpolator interpolator = new LinearInterpolator();
        PolynomialSplineFunction interpolate = interpolator.interpolate(axisPointsFiltered,
                seriesDoubleFiltered);

        for (int i = 0; i < fullLengthSeries; i++) {
            if(seriesDouble[i]==0.0 && interpolate.isValidPoint(axisPoints[i])){
                seriesDouble[i] = interpolate.value(axisPoints[i]);
            }
        }
    }

    public static ZoneObject testZone(double x, double y){
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[] {
                Color.rgb(140, 171, 217),
                Color.rgb(140, 171, 217),
                Color.rgb(140, 171, 217),
                Color.rgb(140, 171, 217)
        };

        ColorStateList myList = new ColorStateList(states, colors);

        List<BiFunction<Double, Double, ZoneObject>> biFunctions = zoneTesters();
        Optional<ZoneObject> first = biFunctions.stream()
                .map(func -> func.apply(x, y))
                .filter(zoneObject -> zoneObject.getZoneInteger() != 0)
                .findFirst();
        return first.orElse(new ZoneObject("", 0, myList));
    }


    private static List<BiFunction<Double, Double, ZoneObject>> zoneTesters() {
        List<BiFunction<Double, Double, ZoneObject>> listZones = new ArrayList<>();
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };


        double rangeAge = 15.0 - 95.0;
        BiFunction<Double, Double, ZoneObject> inactiveRegionTest = (x, y) -> {
            final double slope1 = (100.0 - 70.0) / rangeAge;
            final double c1 = 100.0 - slope1 * 15.0;
            double regionOffset = y - slope1 * x - c1;

            int[] colors = new int[] {
                    Color.rgb(140, 171, 217),
                    Color.rgb(140, 171, 217),
                    Color.rgb(140, 171, 217),
                    Color.rgb(140, 171, 217)
            };

            ColorStateList myList = new ColorStateList(states, colors);
            ZoneObject zoneObject = new ZoneObject("Inactive region Zone", 1, myList);
            return regionOffset <= 0 ? zoneObject : new ZoneObject("", 0, myList);
        };
        listZones.add(inactiveRegionTest);

        BiFunction<Double, Double, ZoneObject> healthsImproveTest = (x, y) -> {
            final double slope1 = (120.0 - 85.0) / rangeAge;
            final double c1 = 120.0 - slope1 * 15.0;
            double regionOffset = y - slope1 * x - c1;
            int rgb = Color.rgb(254, 233, 62);
            int[] colors = new int[] {
                    rgb,
                    rgb,
                    rgb,
                    rgb
            };
            ColorStateList myList = new ColorStateList(states, colors);
            ZoneObject zoneObject = new ZoneObject("Healths Improvement Zone", 2, myList);
            return regionOffset <= 0 ? zoneObject : new ZoneObject("", 0, myList);
        };
        listZones.add(healthsImproveTest);

        BiFunction<Double, Double, ZoneObject> fitnessZoneTest = (x, y) -> {
            final double slope1 = (150.0 - 105.0) / rangeAge;
            final double c1 = 150.0 - slope1 * 15.0;
            double regionOffset = y - slope1 * x - c1;
            int rgb = Color.rgb(235, 158, 30);
            int[] colors = new int[] {
                    rgb,
                    rgb,
                    rgb,
                    rgb
            };
            ColorStateList myList = new ColorStateList(states, colors);
            ZoneObject zoneObject = new ZoneObject("Fitness Zone", 3, myList);
            return regionOffset <= 0 ? zoneObject : new ZoneObject("", 0, myList);
        };
        listZones.add(fitnessZoneTest);

        BiFunction<Double, Double, ZoneObject> performanceZoneTest = (x, y) -> {
            final double slope1 = (170.0 - 120.0) / rangeAge;
            final double c1 = 170.0 - slope1 * 15.0;
            double regionOffset = y - slope1 * x - c1;
            int rgb = Color.rgb(60, 149, 85);
            int[] colors = new int[] {
                    rgb,
                    rgb,
                    rgb,
                    rgb
            };
            ColorStateList myList = new ColorStateList(states, colors);
            ZoneObject zoneObject = new ZoneObject("Performance Zone", 4, myList);
            return regionOffset <= 0 ? zoneObject : new ZoneObject("", 0, myList);
        };
        listZones.add(performanceZoneTest);

        BiFunction<Double, Double, ZoneObject> highPerformanceZoneTest = (x, y) -> {
            final double slope1 = (200.0 - 140.0) / rangeAge;
            final double c1 = 200.0 - slope1 * 15.0;
            double regionOffset = y - slope1 * x - c1;
            int rgb = Color.rgb(154, 86, 163);
            int[] colors = new int[] {
                    rgb,
                    rgb,
                    rgb,
                    rgb
            };
            ColorStateList myList = new ColorStateList(states, colors);
            ZoneObject zoneObject = new ZoneObject("High Performance Zone", 5, myList);
            return regionOffset <= 0 ? zoneObject : new ZoneObject("", 0, myList);
        };
        listZones.add(highPerformanceZoneTest);

        return listZones;
    }


    public static class ZoneObject{
        private String zone;
        private int zoneInteger;
        private ColorStateList colorStateList;

        public ZoneObject(String zone, int zoneInteger , ColorStateList colorStateList) {
            this.zone = zone;
            this.zoneInteger = zoneInteger;
            this.colorStateList = colorStateList;
        }

        public int getZoneInteger() {
            return zoneInteger;
        }

        public void setZoneInteger(int zoneInteger) {
            this.zoneInteger = zoneInteger;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }

        public ColorStateList getColorStateList() {
            return colorStateList;
        }

        public void setColorStateList(ColorStateList colorStateList) {
            this.colorStateList = colorStateList;
        }
    }


}
