package com.example.myapplication.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * This class serves as the main database for the application, using Room Persistence Library.
 * It defines the database configuration and serves as the main access point for the underlying connection to the app's persisted data.
 *
 * @author Cedric De Leon, Sergei Borja
 * @since 2023.11.03
 */
@Database(
        entities = {Employee.class, User.class, ShiftInstance.class, Availability.class, ShiftType.class, CalendarDay.class, DayOff.class},
        version = 16,
        exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    /**
     * Creates or retrieves an instance of the database.
     * This method ensures that only one instance of the database is created throughout the application.
     *
     * @param context Application context used to construct the database.
     * @return The single instance of the database.
     */
    public static AppDatabase getDbInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "scheduler.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        return INSTANCE;
    }

    // Abstract methods for different DAOs (Data Access Objects).
    public abstract UserDao userDao();

    public abstract EmployeeDao employeeDao();

    public abstract ShiftDao shiftDao();

    public abstract AvailabilityDao availabilityDao();

    public abstract DayOffDao dayOffDao();
}