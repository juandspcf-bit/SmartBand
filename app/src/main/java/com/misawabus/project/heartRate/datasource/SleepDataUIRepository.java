package com.misawabus.project.heartRate.datasource;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.Database.rommdb.AppRoomDataBase;
import com.misawabus.project.heartRate.Database.rommdb.SleepDataUIDao;

import java.util.Date;
import java.util.List;

public class SleepDataUIRepository {
    private final SleepDataUIDao sleepDataUIDao;
    //private final LiveData<List<SleepDataUI>> allData;

    public SleepDataUIRepository(Application application){
        AppRoomDataBase db = AppRoomDataBase.getDatabase(application);
        sleepDataUIDao = db.getSleepDataUIDao();
        //allData = sleepDataUIDao.getAllData();
    }

/*    public LiveData<List<SleepDataUI>> getAllData(){
        return allData;
    }*/

    public LiveData<List<SleepDataUI>> getListRows(Date date, String macAddress){
        return sleepDataUIDao.getListRows(date, macAddress);
    }

    public LiveData<SleepDataUI> getSingleRowForU(Date date, String macAddress, int index){
        return sleepDataUIDao.getSingleRowForU(date, macAddress, index);
    }

    public LiveData<SleepDataUI> getSinglePDayRow(Date date, String macAddress){
        return sleepDataUIDao.getSinglePDayRow(date, macAddress);
    }

    public void insertSingleRow(SleepDataUI SleepDataUI){
        AppRoomDataBase.databaseWriteExecutor.execute(() -> sleepDataUIDao.insertSingleRow(SleepDataUI));
    }
    public void updateSingleRow(SleepDataUI SleepDataUI){
        AppRoomDataBase.databaseWriteExecutor.execute(()-> sleepDataUIDao.updateSingleRow(SleepDataUI));
    }
    public void deleteSingleRow(SleepDataUI SleepDataUI){
        AppRoomDataBase.databaseWriteExecutor.execute(()-> sleepDataUIDao.deleteSingleRow(SleepDataUI));
    }

    public void deleteAllData(){
        AppRoomDataBase.databaseWriteExecutor.execute(sleepDataUIDao::deleteAllData);
    }
}
