package com.example.myapplication.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

/**
 * This defines the methods that access the 'day_off' table.
 * It includes operations to retrieve, insert, update, and delete days off data.
 *
 * @author Sergei Borja
 * @since 2023.11.03
 */
@Dao
public interface DayOffDao {

    // Retrieves a list of DayOff records for a specific employee.
    @Query("SELECT * FROM day_off WHERE employee_id = :employeeId")
    List<DayOff> getDaysOffByEmployee(int employeeId);

    // Retrieves a specific DayOff record by its ID.
    @Query("SELECT * FROM day_off WHERE id = :dayOffId")
    DayOff getDayOffById(int dayOffId);

    // Retrieves a list of DayOff records for a specific date.
    @Query("SELECT * FROM day_off WHERE date = :date")
    List<DayOff> getDaysOffByDate(LocalDate date);

    // Updates a specific DayOff record in the database.
    @Update
    void updateDayOff(DayOff dayOff);

    // Inserts a new DayOff record into the database.
    @Insert
    void insertDayOff(DayOff dayOff);

    // Deletes a specific DayOff record from the database.
    @Delete
    void deleteDayOff(DayOff dayOff);

}
