/**
 * @author Cedric De Leon, Sergei Borja
 * @since 2023.11.03
 */

package com.example.myapplication.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

/**
 * This defines the methods that access the 'user' table.
 * It includes operations to retrieve, insert, update, and delete user data,
 *
 * @author  Cedric De Leon, Sergei Borja
 * @since  2023.11.03
 */
@Dao
public interface UserDao {
    // Retrieves all users from the database.
    @Query("SELECT * FROM user")
    List<User> getAll();

    // Retrieves a list of users based on their IDs.
    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    // Finds a user by their username.
    @Query("SELECT * FROM user WHERE user_name LIKE :userName")
    User findByName(String userName);

    // Inserts one or more users into the database, replacing existing entries in case of conflict.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User... users);

    // Updates the password for a specific user.
    @Query("UPDATE user SET password = :newPassword WHERE user_name = :userName")
    void changeUserPassword(String userName, String newPassword);

    // Inserts a user by linking them to an existing employee ID.
    @Query("INSERT INTO user (employee_id) VALUES (:employeeId)")
    void insertUserByEmployee(int employeeId);

    // Retrieves a user based on their associated employee ID.
    @Query("SELECT * FROM user WHERE employee_id = :employeeId LIMIT 1")
    User findByEmployeeId(int employeeId);

    // Deletes a specific user from the database.
    @Delete
    void delete(User user);

}
