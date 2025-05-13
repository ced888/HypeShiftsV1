package com.example.myapplication.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * This defines the methods that access the 'employee' table.
 * It includes operations to retrieve, insert, update, and delete employee data.
 *
 * @author  Cedric De Leon, Sergei Borja
 * @since  2023.11.03
 */
@Dao
public interface EmployeeDao {
    // Retrieves an Employee by their ID.
    @Query("SELECT * FROM employee WHERE id = :id")
    Employee getEmployeeById(long id);

    // Retrieves all employees from the database.
    @Query("SELECT * FROM employee")
    List<Employee> getAll();

    // Searches for employees by their first or last name.
    @Query("SELECT * FROM employee WHERE first_name or last_name LIKE :name")
    List<Employee> findByName(String name);

    // Retrieves an employee by their unique identifier.
    @Query("SELECT * FROM employee WHERE id = :employee_id LIMIT 1")
    Employee findById(int employee_id);

    // Inserts an employee into the database. In case of conflict, replaces the existing entry.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertEmployee(Employee employee);

    // Deletes an employee from the database.
    @Delete
    void deleteEmployee(Employee employee);

    // Updates the role of an employee in the database.
    @Query("UPDATE employee set role = :role")
    void UpdateEmployeeRole(String role);

    // Updates the qualification status of an employee.
    @Query("UPDATE employee set is_training =:is_training, is_qualified_opening = :is_qualified_opening, is_qualified_closing = :is_qualified_closing")
    void UpdateEmployeeQualifications(boolean is_training, boolean is_qualified_opening, boolean is_qualified_closing);

    // Updates the entire profile of an employee.
    @Update(entity = Employee.class)
    void UpdateEmployeeProfile(Employee employee);
}
