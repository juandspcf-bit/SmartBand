package com.misawabus.project.heartRate.datasource;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.misawabus.project.heartRate.Database.entities.BloodPressure;
import com.misawabus.project.heartRate.Database.entities.Temperature;
import com.misawabus.project.heartRate.Database.rommdb.AppRoomDataBase;
import com.misawabus.project.heartRate.Database.rommdb.TemperatureDao;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;

import java.util.Date;

public class TemperatureRepository {
    private final TemperatureDao temperatureDao;
    //private final LiveData<List<BloodPressure>> allData;

    public TemperatureRepository(Application application){
        AppRoomDataBase db = AppRoomDataBase.getDatabase(application);
        temperatureDao = db.getTemperatureDao();
    }

    public LiveData<Temperature> getSingleRowForU(Date date, String macAddress, IdTypeDataTable idTypeDataTable){
        return temperatureDao.getSingleRowForU(date, macAddress, idTypeDataTable);
    }

    public LiveData<Temperature> getSinglePDayRow(Date date, String macAddress){
        return temperatureDao.getSinglePDayRow(date, macAddress);
    }

    public void insertSingleRow(Temperature temperature){
        AppRoomDataBase.databaseWriteExecutor.execute(() -> temperatureDao.insertSingleRow(temperature));
    }
    public void updateSingleRow(Temperature temperature){
        AppRoomDataBase.databaseWriteExecutor.execute(()-> temperatureDao.updateSingleRow(temperature));
    }
    public void deleteSingleRow(Temperature temperature){
        AppRoomDataBase.databaseWriteExecutor.execute(()-> temperatureDao.deleteSingleRow(temperature));
    }

    public void deleteAllData(){
        AppRoomDataBase.databaseWriteExecutor.execute(temperatureDao::deleteAllData);
    }
}
