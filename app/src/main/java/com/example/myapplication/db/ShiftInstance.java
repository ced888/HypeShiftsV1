package com.example.myapplication.db;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * This class defines the structure of the 'shift_instance' table in the database.
 * It represents an individual occurrence of a work shift, including information about the shift type
 * and the employee assigned to it.
 *
 * @author Cedric De Leon, Sergei Borja
 * @since 2023.11.15
 */
@Entity(tableName = "shift_instance", foreignKeys = {@ForeignKey(entity = ShiftType.class, parentColumns = {"id"}, childColumns = {"shift_type_id"}, onDelete = ForeignKey.CASCADE), @ForeignKey(entity = Employee.class, parentColumns = {"id"}, childColumns = {"employee_id"}, onDelete = ForeignKey.CASCADE)})
public class ShiftInstance {
    // Unique identifier for each shift instance.
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Foreign key referencing the shift type associated with this instance.
    @ColumnInfo(name = "shift_type_id")
    public int shiftTypeId;

    // Foreign key referencing the employee assigned to this shift instance.
    @ColumnInfo(name = "employee_id")
    public int employeeId;

    // A display name for the employee, for easier identification within the app.
    @ColumnInfo(name = "employee_display_name")
    public String employeeDisplayName;

    // Descriptive string of the shift type for this instance.
    @ColumnInfo(name = "shift_type")
    public String shiftType;

    // Default constructor.
    public ShiftInstance() {
    }

    // Constructors used for creating instances of Shift Instance.
    @Ignore
    public ShiftInstance(int shiftTypeId) {
        this.shiftTypeId = shiftTypeId;
    }

    @Ignore
    public ShiftInstance(int employeeId, String employeeDisplayName) {
        this.employeeId = employeeId;
        this.employeeDisplayName = employeeDisplayName;
    }
}
