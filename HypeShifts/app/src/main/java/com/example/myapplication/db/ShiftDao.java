package com.example.myapplication.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Upsert;

import java.time.LocalDate;
import java.util.List;

/**
 * This defines the methods that access the 'shift' table.
 * It includes operations to retrieve, insert, update, and delete shift data.
 *
 * @author  Cedric De Leon, Sergei Borja
 * @since  2023.11.03
 */
@Dao
public interface ShiftDao {
    // Inserts a new calendar day into the database.
    @Insert
    long insertDay(CalendarDay calendarDay);

    // Inserts a new shift type into the database.
    @Insert
    long insertShiftType(ShiftType shiftType);

    // Inserts or updates a list of shift instances.
    @Upsert
    void insertShiftInstances(List<ShiftInstance> shiftInstances);

    // Deletes a list of shift instances.
    @Delete
    void deleteShiftInstances(List<ShiftInstance> shiftInstances);

    // Inserts a shift type by specifying a day.
    @Query("INSERT INTO shift_type (day_id) VALUES (:dayId)")
    void insertShiftTypeByDay(int dayId);

    // Inserts a shift instance by specifying a shift type.
    @Query("INSERT INTO shift_instance (shift_type_id) VALUES (:shiftTypeId)")
    void insertShiftInstanceByType(int shiftTypeId);

    // Deletes shifts associated with a specific calendar day.
    @Delete
    void deleteShiftsByDay(CalendarDay day);

    // Retrieves a calendar day by date.
    @Query("SELECT * FROM calendar_day WHERE :date LIMIT 1")
    CalendarDay getCalendarDay(LocalDate date);

    // Retrieves all shift instances.
    @Query("SELECT * FROM shift_instance")
    List<ShiftInstance> getAllShifts();

    // Retrieves all shift instances for a specific shift type.
    @Query("SELECT * FROM shift_instance WHERE shift_type_id = :shiftTypeId")
    List<ShiftInstance> getAllShiftInstanceByShiftType(int shiftTypeId);

    // Retrieves all shift instances for a specific employee.
    @Query("SELECT * FROM shift_instance WHERE employee_id = :employeeId")
    List<ShiftInstance> getAllShiftInstanceByEmployeeID(int employeeId);

    // Retrieves all shift types for a specific day.
    @Query("SELECT * FROM shift_type WHERE day_id=:dayId")
    List<ShiftType> getAllShiftTypeByDay(int dayId);

    // Retrieves all shift types for a specific date.
    @Query("SELECT * FROM shift_type JOIN calendar_day ON calendar_day.id = shift_type.day_id WHERE work_date = :date")
    List<ShiftType> getAllShiftTypeByDate(LocalDate date);

    // Retrieves a calendar day based on a specific work date.
    @Query("SELECT * FROM calendar_day WHERE work_date = :date")
    CalendarDay getCalendarWorkDay(LocalDate date);

    // Retrieves a specific shift instance by employee and shift type.
    @Query("SELECT * FROM shift_instance WHERE employee_id = :empId AND shift_type_id = :shiftTypeId")
    ShiftInstance getShiftInstanceByEmpAndType(int empId, int shiftTypeId);

    // Retrieves the name of the shift type for a specific shift instance.
    @Query("SELECT shift_type.shift_type_name FROM shift_instance " +
            "JOIN shift_type ON shift_instance.shift_type_id = shift_type.id " +
            "WHERE shift_instance.id = :shiftInstanceId")
    String getShiftTypeNameByShiftInstanceId(int shiftInstanceId);

    // Retrieves the work date of a shift for a specific shift instance.
    @Query("SELECT calendar_day.work_date FROM calendar_day " +
            "JOIN shift_type ON calendar_day.id = shift_type.day_id " +
            "JOIN shift_instance ON shift_type.id = shift_instance.shift_type_id " +
            "WHERE shift_instance.id = :shiftInstanceId")
    LocalDate getDateOfShift(int shiftInstanceId);

    // Retrieves a shift type by its name.
    @Query("SELECT * FROM shift_type WHERE shift_type_name = :shiftTypeName LIMIT 1")
    ShiftType getShiftTypeByName(String shiftTypeName);

}
