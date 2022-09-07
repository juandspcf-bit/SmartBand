package com.misawabus.project.heartRate.datasource;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.Database.entities.BloodPressure;
import com.misawabus.project.heartRate.Database.rommdb.AppRoomDataBase;
import com.misawabus.project.heartRate.Database.rommdb.BloodPressureDao;

import java.util.Date;

public class BloodPressureRepository {
    private final BloodPressureDao bloodPressureDao;
    //private final LiveData<List<BloodPressure>> allData;

    public BloodPressureRepository(Application application){
        AppRoomDataBase db = AppRoomDataBase.getDatabase(application);
        bloodPressureDao = db.getBloodPressureDao();
    }

    public LiveData<BloodPressure> getSingleRowForU(Date date, String macAddress, IdTypeDataTable idTypeDataTable){
        return bloodPressureDao.getSingleRowForU(date, macAddress, idTypeDataTable);
    }

    public LiveData<BloodPressure> getSinglePDayRow(Date date, String macAddress){
        return bloodPressureDao.getSinglePDayRow(date, macAddress);
    }

    public void insertSingleRow(BloodPressure bloodPressure){
        AppRoomDataBase.databaseWriteExecutor.execute(() -> bloodPressureDao.insertSingleRow(bloodPressure));
    }
    public void updateSingleRow(BloodPressure bloodPressure){
        AppRoomDataBase.databaseWriteExecutor.execute(()-> bloodPressureDao.updateSingleRow(bloodPressure));
    }
    public void deleteSingleRow(BloodPressure bloodPressure){
        AppRoomDataBase.databaseWriteExecutor.execute(()-> bloodPressureDao.deleteSingleRow(bloodPressure));
    }

    public void deleteAllData(){
        AppRoomDataBase.databaseWriteExecutor.execute(()-> bloodPressureDao.deleteAllData());
    }
}
