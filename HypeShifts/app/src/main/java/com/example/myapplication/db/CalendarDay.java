package com.example.myapplication.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

/**
 * This class defines the structure of the 'calendar_day' table in the database.
 * It is used to represent and store individual days, particularly for work scheduling purposes.
 *
 * @author Cedric De Leon, Sergei Borja
 * @since 2023.11.03
 */
@Entity(tableName = "calendar_day")
public class CalendarDay {
    // Unique identifier for each calendar day.
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Column storing the date of the workday.
    @ColumnInfo(name = "work_date")
    public LocalDate workDate;

    // Default constructor.
    public CalendarDay() {
    }

    // Constructor used for creating instances of CalendarDay.
    @Ignore
    public CalendarDay(LocalDate workDate) {
        this.workDate = workDate;
    }
}
