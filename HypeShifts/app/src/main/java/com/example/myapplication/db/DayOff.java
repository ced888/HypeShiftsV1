package com.example.myapplication.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

/**
 * This class defines the structure of the 'day_off' table in the database.
 * It specifies how days off for employees are stored and managed, including information
 * like the date, availability, and reason for the day off.
 *
 * @author Sergei Borja
 * @since 2023.11.29
 */
@Entity(tableName = "day_off", foreignKeys = @ForeignKey(entity = Employee.class, parentColumns = "id", childColumns = "employee_id", onDelete = ForeignKey.CASCADE))
public class DayOff {
    // Unique identifier for each day off.
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Column to store the associated employee's ID.
    @ColumnInfo(name = "employee_id")
    public int employeeId;

    // Column to store the date of the day off.
    @ColumnInfo(name = "date")
    public LocalDate date;

    // Boolean column to indicate if the employee is available on this day.
    @ColumnInfo(name = "is_available")
    public boolean isAvailable;

    // Column to store the reason for the day off.
    @ColumnInfo(name = "reason")
    public String reason;

    // Default constructor.
    public DayOff() {
    }

    // Constructor used for creating instances of DayOff.
    @Ignore
    public DayOff(int id, int employeeId, LocalDate date, boolean isAvailable, String reason) {
        this.id = id;
        this.employeeId = employeeId;
        this.date = date;
        this.isAvailable = isAvailable;
        this.reason = reason;
    }

    // Standard getter and setter methods for each field.
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
