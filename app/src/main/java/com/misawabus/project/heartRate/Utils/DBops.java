package com.misawabus.project.heartRate.Utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.misawabus.project.heartRate.Database.entities.BloodPressure;
import com.misawabus.project.heartRate.Database.entities.HeartRate;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.Database.entities.Sports;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.viewModels.BloodPressureViewModel;
import com.misawabus.project.heartRate.viewModels.HeartRateViewModel;
import com.misawabus.project.heartRate.viewModels.SleepDataUIViewModel;
import com.misawabus.project.heartRate.viewModels.SportsViewModel;

import java.util.Date;
import java.util.HashMap;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class DBops {

    private HeartRateViewModel heartRateViewModel;
    private BloodPressureViewModel bloodPressureViewModel;

    private SleepDataUIViewModel sleepDataUIViewModel;
    private SportsViewModel sportsViewModel;

    public DBops(){

    }


    public void initViewModels(ViewModelStoreOwner viewModelStoreOwner){
        sportsViewModel = new ViewModelProvider(viewModelStoreOwner).get(SportsViewModel.class);
        heartRateViewModel = new ViewModelProvider(viewModelStoreOwner).get(HeartRateViewModel.class);
        bloodPressureViewModel = new ViewModelProvider(viewModelStoreOwner).get(BloodPressureViewModel.class);
        sleepDataUIViewModel = new ViewModelProvider(viewModelStoreOwner).get(SleepDataUIViewModel.class);
    }


    public static void updateSleepData(SleepDataUI dataMap, String macAddress, LifecycleOwner lifecycleOwner, int index){
        updateSleepRow(dataMap, dataMap.getDateData(), macAddress, lifecycleOwner, index);
    }

    public static void insertHeartRateRow(String data, String myDate, String macAddress, IdTypeDataTable idTable){
        HeartRateViewModel.insertSingleHeartRateRow(getNewHeartRateObject(data, myDate, macAddress, idTable));
    }

    public static void insertBloodPressureRow(IdTypeDataTable idTypeDataTable, String data, String myDate, String macAddress){
        BloodPressureViewModel.insertSingleRow(getNewBloodPressureObject(idTypeDataTable, data, myDate, macAddress));
    }

    public static void insertSportsRow(String data, String myDate, String macAddress){
        SportsViewModel.insertSingleRow(getNewSportsObject(data, myDate, macAddress));
    }

    public static void insertSleepRow(SleepDataUI data, Date myDate, String macAddress){
        data.setMacAddress(macAddress);
        SleepDataUIViewModel.insertSingleRow(data);
    }


    public static void updateSleepRow(SleepDataUI data, Date formattedDate, String macAddress, LifecycleOwner lifecycleOwner, int index){

        LiveData<SleepDataUI> sleepDataUILiveData = SleepDataUIViewModel.getSingleRowForU(formattedDate, macAddress, index);
        Observer<SleepDataUI> MyObserver = new Observer<>() {
            @Override
            public void onChanged(SleepDataUI sleepDataUI) {
                if (sleepDataUI == null) {
                    insertSleepRow(data, formattedDate, macAddress);
                    sleepDataUILiveData.removeObserver(this);
                } else if (!sleepDataUI.getData().equals(data.getData())) {
                    data.setMacAddress(macAddress);
                    SleepDataUIViewModel.updateSingleRow(data);
                    sleepDataUILiveData.removeObserver(this);
                }
            }
        };
        sleepDataUILiveData.observe(lifecycleOwner, MyObserver);
    }

    public static void updateSportsRow(IdTypeDataTable idTypeDataTable, String data, String myDate, String macAddress, LifecycleOwner lifecycleOwner){
        Date formattedDate = DateUtils.getFormattedDate(myDate, "-");

        LiveData<Sports> singleSportsRowForU = SportsViewModel.getSingleRowForU(formattedDate, macAddress, idTypeDataTable);
        Observer<Sports> MyObserver = new Observer<>() {
            @Override
            public void onChanged(Sports sports) {
                if (sports == null) {
                    insertSportsRow(data, myDate, macAddress);
                    singleSportsRowForU.removeObserver(this);
                } else if (!sports.getData().equals(data)) {
                    sports.setData(data);
                    SportsViewModel.updateSingleRow(sports);
                    singleSportsRowForU.removeObserver(this);
                }
            }
        };
        singleSportsRowForU.observe(lifecycleOwner, MyObserver);
    }


    public static void updateHeartRateRow(IdTypeDataTable idTable, String data, String stringDate, String macAddress, LifecycleOwner lifecycleOwner){
        Date formattedDate = DateUtils.getFormattedDate(stringDate,"-");
        LiveData<HeartRate> singleHeartRateRowForU = HeartRateViewModel.getSingleHeartRateRowForU(formattedDate, macAddress, idTable);
        Observer<HeartRate> MyObserver = new Observer<>() {
            @Override
            public void onChanged(HeartRate heartRate) {
                if (heartRate == null) {
                    insertHeartRateRow(data, stringDate, macAddress, idTable);
                    singleHeartRateRowForU.removeObserver(this);
                } else if (!heartRate.getData().equals(data)) {
                    heartRate.setData(data);
                    HeartRateViewModel.updateSingleHeartRateRow(heartRate);
                    singleHeartRateRowForU.removeObserver(this);
                }
            }
        };
        singleHeartRateRowForU.observe(lifecycleOwner, MyObserver);
    }

    //TODO remove unnecessary idTypeDataTable parameter
    public static void updateBloodPressureRow(IdTypeDataTable idTypeDataTable, String data, String myDate, String macAddress, LifecycleOwner lifecycleOwner){
        Date formattedDate = DateUtils.getFormattedDate(myDate, "-");

        LiveData<BloodPressure> singleBloodPressureRowForU = BloodPressureViewModel.getSingleRowForU(formattedDate, macAddress, idTypeDataTable);
        Observer<BloodPressure> MyObserver = new Observer<>() {
            @Override
            public void onChanged(BloodPressure bloodPressure) {
                if (bloodPressure == null) {
                    insertBloodPressureRow(idTypeDataTable, data, myDate, macAddress);
                    singleBloodPressureRowForU.removeObserver(this);
                } else if (!bloodPressure.getData().equals(data)) {
                    bloodPressure.setData(data);
                    BloodPressureViewModel.updateSingleRow(bloodPressure);
                    singleBloodPressureRowForU.removeObserver(this);
                }
            }
        };
        singleBloodPressureRowForU.observe(lifecycleOwner, MyObserver);
    }

    private static Sports getNewSportsObject(String data, String myDate, String macAddress) {
        Date formattedDate = DateUtils.getFormattedDate(myDate, "-");
        Sports sports = new Sports();
        sports.setIdTypeDataTable(IdTypeDataTable.SportsFiveMin);
        sports.setMacAddress(macAddress);
        sports.setData(data);
        sports.setDateData(formattedDate);
        return sports;
    }


    @NonNull
    private static HeartRate getNewHeartRateObject(String data, String myDate, String macAddress, IdTypeDataTable idTable) {
        Date formattedDate = DateUtils.getFormattedDate(myDate,"-");
        HeartRate heartRate = new HeartRate();
        heartRate.setIdTable(idTable);
        heartRate.setMacAddress(macAddress);
        heartRate.setData(data);
        heartRate.setDateData(formattedDate);
        return heartRate;
    }


    @NonNull
    private static BloodPressure getNewBloodPressureObject(IdTypeDataTable idTypeDataTable, String data, String myDate, String macAddress){
        Date formattedDate = DateUtils.getFormattedDate(myDate, "-");
        BloodPressure bloodPressureObj = new BloodPressure();
        bloodPressureObj.setIdTypeDataTable(idTypeDataTable);
        bloodPressureObj.setMacAddress(macAddress);
        bloodPressureObj.setData(data);
        bloodPressureObj.setDateData(formattedDate);
        return bloodPressureObj;
    }


}
