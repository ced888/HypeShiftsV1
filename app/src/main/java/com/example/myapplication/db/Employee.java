package com.example.myapplication.db;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This class defines the structure of the 'employee' table in the database.
 * This class contains all the information related to an employee, such as personal details,
 * contact information, and professional qualifications.
 *
 * @author  Cedric De Leon, Osman Osman, Sergei Borja
 * @since  2023.11.03
 */
@Entity(tableName = "employee")
public class Employee implements Serializable {
    // Unique identifier for each employee.
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Employee's first and last name.
    @ColumnInfo(name = "first_name")
    public String firstName;
    @ColumnInfo(name = "last_name")
    public String lastName;

    // Contact details of the employee.
    @ColumnInfo(name = "home_number")
    @Nullable
    public String homeNumber;
    @ColumnInfo(name = "mobile_number")
    public String mobileNumber;
    @ColumnInfo(name = "email_address")
    public String emailAddress;
    @ColumnInfo(name = "contact_preference")
    @Nullable
    public String contactPreference;

    // Role of the employee.
    @ColumnInfo(name = "role")
    public String role;

    // Indicates if the employee is currently active (working) or not.
    @ColumnInfo(name = "is_active", defaultValue = "1")
    public boolean isActive;

    // Gender of the employee.
    @ColumnInfo(name = "gender")
    public String gender;

    // Employee's date of birth.
    @ColumnInfo(name = "dob")
    @Nullable
    public LocalDate dob;

    // Date when the employee started working.
    @ColumnInfo(name = "start_date")
    public LocalDate startDate;

    // Address of the employee.
    @ColumnInfo(name = "address")
    public String address;

    // Indicates if the employee is currently undergoing training.
    @ColumnInfo(name = "is_training", defaultValue = "1")
    public boolean is_training = true;

    // Qualification for specific responsibilities like opening or closing.
    @ColumnInfo(name = "is_qualified_opening", defaultValue = "0")
    public boolean is_qualified_opening = false;
    @ColumnInfo(name = "is_qualified_closing", defaultValue = "0")
    public boolean is_qualified_closing = false;

    // Default constructor.
    public Employee() {
    }

    // Constructors used for creating instances of Employee.
    @Ignore
    public Employee(String first_name, String last_name, String mobile_number, String email_address, String role, boolean is_active) {
        this.firstName = first_name;
        this.lastName = last_name;
        this.mobileNumber = mobile_number;
        this.emailAddress = email_address;
        this.role = role;
        this.isActive = is_active;
    }

    @Ignore
    public Employee(String first_name, String last_name, String mobile_number, String home_number, String gender, LocalDate dob, LocalDate start_date, String contactPreference, String address, String email_address, String role, boolean is_active) {
        this.firstName = first_name;
        this.lastName = last_name;
        this.gender = gender;
        this.dob = dob;
        this.contactPreference = contactPreference;
        this.address = address;
        this.homeNumber = home_number;
        this.mobileNumber = mobile_number;
        this.emailAddress = email_address;
        this.role = role;
        this.startDate = start_date;
        this.isActive = is_active;
    }

    // Standard getter and setter methods for each field.
    @Nullable
    public String getContactPreference() {
        return contactPreference;
    }

    public void setContactPreference(@Nullable String contactPreference) {
        this.contactPreference = contactPreference;
    }

    public int getEmployeeById() {
        return id;
    }

    public void setEmployeeById(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String first_name) {
        this.firstName = first_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dateOfBirth) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHomeNumber() {
        return homeNumber;
    }

    public void setHomeNumber(String home) {
        this.homeNumber = home;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobile) {
        this.mobileNumber = mobile;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate start) {
        this.startDate = start;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String last_name) {
        this.lastName = last_name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return emailAddress;
    }

    public void setEmail(String email_address) {
        this.emailAddress = email_address;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean is_active) {
        this.isActive = is_active;
    }

    public String isIs_qualified_opening() {
        if (is_qualified_opening) return "Qualified";
        return "Unqualified";
    }

    public void setIs_qualified_opening(boolean is_qualified_opening) {
        this.is_qualified_opening = is_qualified_opening;
        updateTrainingStatus();
    }

    public String isIs_qualified_closing() {
        if (is_qualified_closing) return "Qualified";
        return "Unqualified";
    }

    public void setIs_qualified_closing(boolean is_qualified_closing) {
        this.is_qualified_closing = is_qualified_closing;
        updateTrainingStatus();
    }

    public boolean isIs_training() {
        return is_training;
    }

    public void setIs_training(boolean is_training) {
        this.is_training = is_training;
    }

    // Method to update training status based on qualifications.
    private void updateTrainingStatus() {
        // If both opening and closing qualifications are true, set training to false
        is_training = !is_qualified_opening || !is_qualified_closing;
    }

}
