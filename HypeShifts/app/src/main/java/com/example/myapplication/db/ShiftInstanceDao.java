/**
 * @author Cedric De Leon, Sergei Borja
 * @since 2023.11.15
 */

package com.example.myapplication.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * This defines the methods that access the 'shift_instance' table.
 * It includes operations to retrieve, insert, update, and delete shift instance data.
 *
 * @author  Cedric De Leon, Sergei Borja
 * @since  2023.11.03
 */
@Dao
public interface ShiftInstanceDao {

    @Query("Select * from shift_instance")
    List<ShiftInstance> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertShift(ShiftInstance... shiftInstances);

    @Delete
    void deleteShift(ShiftInstance shiftInstance);

    @Query("DELETE FROM shift_instance")
    void deleteAllShifts();
}
