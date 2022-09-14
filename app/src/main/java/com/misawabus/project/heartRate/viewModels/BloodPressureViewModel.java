package com.misawabus.project.heartRate.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.datasource.BloodPressureRepository;
import com.misawabus.project.heartRate.Database.entities.BloodPressure;

import java.util.Date;

public class BloodPressureViewModel extends AndroidViewModel {
    public static BloodPressureRepository bloodPressureRepository;

    public BloodPressureViewModel(@NonNull Application application) {
        super(application);
        bloodPressureRepository = new BloodPressureRepository(application);
    }

    public static void insertSingleRow(BloodPressure bloodPressure){
        bloodPressureRepository.insertSingleRow(bloodPressure);
    }

    public static LiveData<BloodPressure> getSingleRowForU(Date date, String macAddress, IdTypeDataTable idTypeDataTable){
        return bloodPressureRepository.getSingleRowForU(date, macAddress, idTypeDataTable);
    }

    public static LiveData<BloodPressure> getSinglePDayRow(Date date, String macAddress){
        return bloodPressureRepository.getSinglePDayRow(date, macAddress);
    }

    public static void updateSingleRow(BloodPressure bloodPressure){
        bloodPressureRepository.updateSingleRow(bloodPressure);
    }


}
