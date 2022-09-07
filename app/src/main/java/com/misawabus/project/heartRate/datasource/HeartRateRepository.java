package com.misawabus.project.heartRate.datasource;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.Database.entities.HeartRate;
import com.misawabus.project.heartRate.Database.rommdb.AppRoomDataBase;
import com.misawabus.project.heartRate.Database.rommdb.HeartRateDao;

import java.util.Date;
import java.util.List;

public class HeartRateRepository {
    private final HeartRateDao heartRateDao;
    private LiveData<List<HeartRate>> allHeartRateData;
    private LiveData<Integer> countHeartRateData;


    public HeartRateRepository(Application application){
        AppRoomDataBase db = AppRoomDataBase.getDatabase(application);
        heartRateDao = db.getHeartRateDao();
        //allHeartRateData = heartRateDao.getAllHeartRateData();
        //countHeartRateData = heartRateDao.getCountHeartRateData();
    }

    public LiveData<List<HeartRate>> getAllHeartRateData(){
        return allHeartRateData;
    }

    public LiveData<Integer> getCountHeartRateData(){
        return countHeartRateData;
    }

    public LiveData<HeartRate> getSingleHeartRateRow(Date date, String macAddress){
        return heartRateDao.getSingleHeartRateRow(date, macAddress);
    }

    public LiveData<HeartRate> getSingleHeartRateRowForU(Date date, String macAddress, IdTypeDataTable idTable){
        return heartRateDao.getSingleHeartRateRowForU(date, macAddress, idTable);
    }

    public LiveData<HeartRate> getSinglePDayHeartRateRow(Date date, String macAddress){
        return heartRateDao.getSinglePDayHeartRateRow(date, macAddress);
    }

    public void insertSingleHeartRateRow(HeartRate heartRate){
        AppRoomDataBase.databaseWriteExecutor.execute(() -> heartRateDao.insertSingleHeartRateRow(heartRate));
    }
    public void updateSingleHeartRateRow(HeartRate heartRate){
        AppRoomDataBase.databaseWriteExecutor.execute(()-> heartRateDao.updateSingleHeartRateRow(heartRate));
    }
    public void deleteSingleHeartRateRow(HeartRate heartRate){
        AppRoomDataBase.databaseWriteExecutor.execute(()-> heartRateDao.deleteSingleHeartRateRow(heartRate));
    }

    public void deleteAllHeartRateData(){
        AppRoomDataBase.databaseWriteExecutor.execute(heartRateDao::deleteAllHeartRateData);

    }



}
