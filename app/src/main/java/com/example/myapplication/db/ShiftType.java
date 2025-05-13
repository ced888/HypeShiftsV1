package com.example.myapplication.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

/**
 * This class defines the structure of the 'shift_instance' table in the database.
 * It represents an individual occurrence of a work shift, including information about the shift type
 * and the employee assigned to it.
 *
 * @author Cedric De Leon, Sergei Borja
 * @since 2023.11.03
 */
@Entity(tableName = "shift_type", foreignKeys = {@ForeignKey(entity = CalendarDay.class, parentColumns = {"id"}, childColumns = {"day_id"}, onDelete = ForeignKey.CASCADE)})
public class ShiftType {
    // Unique identifier for each shift type.
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Foreign key referencing the day associated with this shift type.
    @ColumnInfo(name = "day_id")
    public int dayId;

    // Name of the shift type (e.g., Morning, Afternoon, Evening, Full).
    @ColumnInfo(name = "shift_type_name")
    public String shiftTypeName;

    // Start time of the shift.
    @ColumnInfo(name = "start_date_time")
    public LocalDateTime startDateTime;

    // End time of the shift.
    @ColumnInfo(name = "end_date_time")
    public LocalDateTime endDateTime;

    // Indicates if the shift is an opening shift.
    @ColumnInfo(name = "is_opening", defaultValue = "0")
    public boolean isOpening;

    // Indicates if the shift is a closing shift.
    @ColumnInfo(name = "is_closing", defaultValue = "0")
    public boolean isClosing;

    // Minimum number of shifts required for this shift type.
    @ColumnInfo(name = "min_shifts", defaultValue = "2")
    public int minShifts;

    // Default constructor.
    public ShiftType() {
    }

    // Constructors used for creating instances of Shift Type.
    @Ignore
    public ShiftType(int dayId, String shiftTypeName, LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isOpening, boolean isClosing, int minShifts) {
        this.dayId = dayId;
        this.shiftTypeName = shiftTypeName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isOpening = isOpening;
        this.isClosing = isClosing;
        this.minShifts = minShifts;
    }
}
