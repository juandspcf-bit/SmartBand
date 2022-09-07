package com.misawabus.project.heartRate.datasource;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.misawabus.project.heartRate.Database.entities.Sports;
import com.misawabus.project.heartRate.Database.rommdb.AppRoomDataBase;
import com.misawabus.project.heartRate.Database.rommdb.SportsDao;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;

import java.util.Date;

public class SportsRepository {
    private final SportsDao sportsDao;

    public SportsRepository(Application application){
        AppRoomDataBase db = AppRoomDataBase.getDatabase(application);
        sportsDao = db.getSportsDao();
    }


    public LiveData<Sports> getSingleRowForU(Date date, String macAddress, IdTypeDataTable idTypeDataTable){
        return sportsDao.getSingleRowForU(date, macAddress, idTypeDataTable);
    }

    public LiveData<Sports> getSinglePDayRow(Date date, String macAddress){
        return sportsDao.getSinglePDayRow(date, macAddress);
    }

    public void insertSingleRow(Sports sports){
        AppRoomDataBase.databaseWriteExecutor.execute(() -> sportsDao.insertSingleRow(sports));
    }
    public void updateSingleRow(Sports sports){
        AppRoomDataBase.databaseWriteExecutor.execute(()-> sportsDao.updateSingleRow(sports));
    }
    public void deleteSingleRow(Sports sports){
        AppRoomDataBase.databaseWriteExecutor.execute(()-> sportsDao.deleteSingleRow(sports));
    }

    public void deleteAllData(){
        AppRoomDataBase.databaseWriteExecutor.execute(()-> sportsDao.deleteAllData());
    }
}
