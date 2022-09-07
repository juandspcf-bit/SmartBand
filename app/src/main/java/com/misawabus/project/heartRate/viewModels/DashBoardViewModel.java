package com.misawabus.project.heartRate.viewModels;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.misawabus.project.heartRate.Database.entities.Device;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.device.entities.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.readData.DeviceReadData;
import com.misawabus.project.heartRate.device.readRealTimeData.RealTimeTesterClass;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DashBoardViewModel extends ViewModel {
    private Device device;
    private boolean isWiFiEnable;
    private int isFetching = View.VISIBLE;
    private volatile int watchData;

    private final MutableLiveData<String> deviceInfo = new MutableLiveData<>();
    private final MutableLiveData<Optional<Integer>> age = new MutableLiveData<>(Optional.of(0));

    private final MutableLiveData<String> realTimeSportsData = new MutableLiveData<>();
    private final MutableLiveData<String> realTimeHearRateData = new MutableLiveData<>();
    private final MutableLiveData<String> realTimeBPData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isEnableFeatures = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isTodayFragmentRefreshing = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>();



    private final MutableLiveData<List<SleepDataUI>> todayUpdateSleepFullData = new MutableLiveData<>();
    private final MutableLiveData<List<SleepDataUI>> yesterdayUpdateSleepFullData = new MutableLiveData<>();
    private final MutableLiveData<List<SleepDataUI>> pastYesterdayUpdateSleepFullData = new MutableLiveData<>();

    private  final MutableLiveData<Map<String, DataFiveMinAvgDataContainer>> todayFullData5MinAvgAllIntervals = new MutableLiveData<>();
    private final MutableLiveData<Map<String, DataFiveMinAvgDataContainer>> yesterdayFullData5MinAvgAllIntervals = new MutableLiveData<>();
    private final MutableLiveData<Map<String, DataFiveMinAvgDataContainer>> pastYesterdayFullData5MinAvgAllIntervals = new MutableLiveData<>();




    private DeviceReadData healthsDataManager;
    private RealTimeTesterClass realTimeTesterClass;
    private List<int[]> dataEcg;



    public DashBoardViewModel() {
        isEnableFeatures.setValue(false);

    }


    public int getWatchData() {
        return watchData;
    }

    public void setWatchData(int watchData) {
        this.watchData = watchData;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public boolean isWiFiEnable() {
        return isWiFiEnable;
    }

    public void setWiFiEnable(boolean wiFiEnable) {
        isWiFiEnable = wiFiEnable;
    }

    public DeviceReadData getHealthsDataManager() {
        return healthsDataManager;
    }

    public void setHealthsDataManager(DeviceReadData healthsDataManager) {
        this.healthsDataManager = healthsDataManager;
    }

    public RealTimeTesterClass getRealTimeTesterClass() {
        return realTimeTesterClass;
    }

    public void setRealTimeTesterClass(RealTimeTesterClass realTimeTesterClass) {
        this.realTimeTesterClass = realTimeTesterClass;
    }

    public List<int[]> getDataEcg() {
        return dataEcg;
    }

    public void setDataEcg(List<int[]> dataEcg) {
        this.dataEcg = dataEcg;
    }


    public void setDeviceInfo(String deviceInfo){
        this.deviceInfo.setValue(deviceInfo);
    }

    public MutableLiveData<Boolean> getIsEnableFeatures() {
        return isEnableFeatures;
    }
    public void setIsEnableFeatures(Boolean isEnableFeatures){
        this.isEnableFeatures.setValue(isEnableFeatures);
    }

    public MutableLiveData<Boolean> getIsTodayFragmentRefreshing() {
        return isTodayFragmentRefreshing;
    }
    public void setIsTodayFragmentRefreshing(Boolean isTodayFragmentRefreshing){
        this.isTodayFragmentRefreshing.setValue(isTodayFragmentRefreshing);
    }

    public MutableLiveData<Boolean> getIsConnected() {
        return isConnected;
    }
    public void setIsConnected(Boolean isConnected){
        this.isConnected.setValue(isConnected);
    }






//  stores te most recent data




    public MutableLiveData<List<SleepDataUI>> getTodayUpdateSleepFullData() {
        return todayUpdateSleepFullData;
    }
    public void setTodayUpdateSleepFullData(List<SleepDataUI> todayUpdateSleepFullData){
        this.todayUpdateSleepFullData.setValue(todayUpdateSleepFullData);
    }


    public MutableLiveData<List<SleepDataUI>> getYesterdayUpdateSleepFullData() {
        return yesterdayUpdateSleepFullData;
    }
    public void setYesterdayUpdateSleepFullData(List<SleepDataUI> yesterdayUpdateSleepFullData){
        this.yesterdayUpdateSleepFullData.setValue(yesterdayUpdateSleepFullData);
    }


    public MutableLiveData<List<SleepDataUI>> getPastYesterdayUpdateSleepFullData() {
        return pastYesterdayUpdateSleepFullData;
    }
    public void setPastYesterdayUpdateSleepFullData(List<SleepDataUI> pastYesterdayUpdateSleepFullData){
        this.pastYesterdayUpdateSleepFullData.setValue(pastYesterdayUpdateSleepFullData);
    }

//   Variables for real time

    public MutableLiveData<String> getRealTimeSportsData() {
        return realTimeSportsData;
    }
    public void setRealTimeSportsData(String realTimeSportsData){
        this.realTimeSportsData.setValue(realTimeSportsData);
    }

    public MutableLiveData<String> getRealTimeHearRateData() {
        return realTimeHearRateData;
    }
    public void setRealTimeHearRateData(String realTimeHearRateData){
        this.realTimeHearRateData.setValue(realTimeHearRateData);
    }

    public MutableLiveData<String> getRealTimeBPData() {
        return realTimeBPData;
    }
    public void setRealTimeBPData(String realTimeBPData){
        this.realTimeBPData.setValue(realTimeBPData);
    }


    public int getIsFetching() {
        return isFetching;
    }

    public void setIsFetching(int isFetching) {
        this.isFetching = isFetching;
    }


    public MutableLiveData<Optional<Integer>> getAge() {
        return age;
    }

    public void setAge(int age) {
        Optional<Integer> ageOP = Optional.of(age);
        this.age.setValue(ageOP);
    }



    public MutableLiveData<Map<String, DataFiveMinAvgDataContainer>> getTodayFullData5MinAvgAllIntervals() {
        return todayFullData5MinAvgAllIntervals;
    }
    public void setTodayFullData5MinAvgAllIntervals(Map<String, DataFiveMinAvgDataContainer> todayFullData5MinAvgAllIntervals) {
        this.todayFullData5MinAvgAllIntervals.setValue(todayFullData5MinAvgAllIntervals);
    }

    public MutableLiveData<Map<String, DataFiveMinAvgDataContainer>> getYesterdayFullData5MinAvgAllIntervals() {
        return yesterdayFullData5MinAvgAllIntervals;
    }

    public void setYesterdayFullData5MinAvgAllIntervals(Map<String, DataFiveMinAvgDataContainer> yesterdayFullData5MinAvgAllIntervals) {
        this.yesterdayFullData5MinAvgAllIntervals.setValue(yesterdayFullData5MinAvgAllIntervals);
    }

    public MutableLiveData<Map<String, DataFiveMinAvgDataContainer>> getPastYesterdayFullData5MinAvgAllIntervals() {
        return pastYesterdayFullData5MinAvgAllIntervals;
    }

    public void setPastYesterdayFullData5MinAvgAllIntervals(Map<String, DataFiveMinAvgDataContainer> pastYesterdayFullData5MinAvgAllIntervals) {
        this.pastYesterdayFullData5MinAvgAllIntervals.setValue(pastYesterdayFullData5MinAvgAllIntervals);
    }


}
