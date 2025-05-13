package com.example.myapplication.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Defines the methods that access the 'availability' table.
 * It includes operations to retrieve, insert, update, and delete availability data.
 *
 * @author Cedric De Leon, Sergei Borja
 * @since 2023.11.03
 */
@Dao
public interface AvailabilityDao {
    // Retrieves a list of Availability records for a specific employee.
    @Query("SELECT * FROM availability where employee_id = :employeeId")
    List<Availability> getAvailabilityByEmployee(int employeeId);

    // Retrieves a list of Employees who are available on a specific day of the week.
    @Query("SELECT employee.* FROM employee JOIN availability ON employee.id = availability.employee_id WHERE availability.day_of_week = :dayOfWeek")
    List<Employee> getAvailableEmployee(int dayOfWeek);

    // Updates the availability status of an employee for a specific day of the week and shift type.
    @Query("UPDATE availability SET is_available = :isAvailable WHERE employee_id = :employeeId AND day_of_week = :dayOfWeek AND shift_type = :shiftType")
    void updateAvailability(int employeeId, String dayOfWeek, String shiftType, boolean isAvailable);

    // Retrieves a list of Availability records for a specific day of the week.
    @Query("SELECT * FROM availability WHERE day_of_week = :dayOfWeek")
    List<Availability> getAvailabilityByDay(Availability.DayOfWeek dayOfWeek);

    // Inserts one or more Availability records into the database.
    @Insert
    void insertAvailability(Availability... availabilities);

    // Deletes a specific Availability record from the database.
    @Delete
    void deleteAvailability(Availability availability);
}
