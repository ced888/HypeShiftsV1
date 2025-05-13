package com.example.myapplication.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * This class defines the structure of the 'availability' table in the database.
 * It specifies how the availability data is stored and retrieved.
 *
 * @author Cedric De Leon, Sergei Borja
 * @since 2023.11.03
 */
@Entity(tableName = "availability", foreignKeys = {@ForeignKey(entity = Employee.class, parentColumns = "id", childColumns = "employee_id", onDelete = ForeignKey.CASCADE)})
public class Availability {
    // Unique identifier for each availability.
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Column to store the associated employee's ID.
    @ColumnInfo(name = "employee_id", index = true)
    @NonNull
    public int employeeId;

    // Column to store the day of the week.
    @ColumnInfo(name = "day_of_week")
    @NonNull
    public DayOfWeek dayOfWeek;

    // Column to store the type of shift.
    @ColumnInfo(name = "shift_type")
    @NonNull
    public ShiftType shiftType;

    // Boolean column to indicate if the employee is available on this day and shift.
    @ColumnInfo(name = "is_available")
    public boolean isAvailable;

    // Default constructor.
    public Availability() {
    }

    // Constructor used for creating instances of Availability.
    @Ignore
    public Availability(int id, int employeeId, @NonNull DayOfWeek dayOfWeek, @NonNull ShiftType shiftType) {
        this.id = id;
        this.employeeId = employeeId;
        this.dayOfWeek = dayOfWeek;
        this.shiftType = shiftType;
        this.isAvailable = true;
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

    @NonNull
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(@NonNull DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @NonNull
    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(@NonNull ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // Enum defining the days of the week.
    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    // Enum defining the types of shifts.
    public enum ShiftType {
        MORNING, AFTERNOON, EVENING, FULL
    }
}



