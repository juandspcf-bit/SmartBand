package com.misawabus.project.heartRate.Database.rommdb;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.misawabus.project.heartRate.Database.entities.Sop2;
import com.misawabus.project.heartRate.Database.entities.Sports;
import com.misawabus.project.heartRate.Database.entities.Temperature;
import com.misawabus.project.heartRate.Utils.Converter;
import com.misawabus.project.heartRate.Database.entities.BloodPressure;
import com.misawabus.project.heartRate.Database.entities.Device;
import com.misawabus.project.heartRate.Database.entities.HeartRate;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        Temperature.class,
        Sop2.class,
        Sports.class,
        HeartRate.class,
        BloodPressure.class,
        Device.class,
        SleepDataUI.class}, version = 2, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class AppRoomDataBase extends RoomDatabase {
    public static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile AppRoomDataBase INSTANCE;
    private static final RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);


                }

            };

    public static AppRoomDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppRoomDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppRoomDataBase.class, "bus_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract TemperatureDao getTemperatureDao();

    public abstract Sop2Dao getSop2Dao();

    public abstract SportsDao getSportsDao();

    public abstract HeartRateDao getHeartRateDao();

    public abstract DeviceDao getDeviceDao();

    public abstract BloodPressureDao getBloodPressureDao();


    public abstract SleepDataUIDao getSleepDataUIDao();

    private static void initDatabaseWithData() {
        databaseWriteExecutor.execute(() -> {

        });

    }


}