package com.misawabus.project.heartRate.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.misawabus.project.heartRate.Database.entities.Temperature;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.datasource.TemperatureRepository;

import java.util.Date;

public class TemperatureViewModel extends AndroidViewModel {
    public static TemperatureRepository temperatureRepository;

    public TemperatureViewModel(@NonNull Application application) {
        super(application);
        temperatureRepository = new TemperatureRepository(application);
    }

    public static void insertSingleRow(Temperature temperature){
        temperatureRepository.insertSingleRow(temperature);
    }

    public static LiveData<Temperature> getSingleRowForU(Date date, String macAddress, IdTypeDataTable idTypeDataTable){
        return temperatureRepository.getSingleRowForU(date, macAddress, idTypeDataTable);
    }

    public static LiveData<Temperature> getSinglePDayRow(Date date, String macAddress){
        return temperatureRepository.getSinglePDayRow(date, macAddress);
    }

    public static void updateSingleRow(Temperature temperature){
        temperatureRepository.updateSingleRow(temperature);
    }


}
