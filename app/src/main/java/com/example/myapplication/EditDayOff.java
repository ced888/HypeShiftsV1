package com.example.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.DayOff;
import com.example.myapplication.db.Employee;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * Activity for editing and deleting an employee's day off.
 * Allows managers to change the date and reason for a day off or remove it entirely.
 *
 * @author Sergei Borja
 * @since 2023.12.17
 */
public class EditDayOff extends AppCompatActivity {
    private TextInputEditText yearET, monthET, dayET,reasonET;
    private AppDatabase db;
    private Employee employee;
    private int employeeId, dayOffId;
    DayOff dayOff;

    /**
     * Initializes the activity when it is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains the data
     *                           it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_day_off);

        // Retrieve the employee ID and Day Off from the intent
        db = AppDatabase.getDbInstance(this.getApplicationContext());

        employeeId = getIntent().getIntExtra("employeeId", -1);
        employee = db.employeeDao().getEmployeeById(employeeId);

        dayOffId = getIntent().getIntExtra("dayOffId", -1);
        dayOff = db.dayOffDao().getDayOffById(dayOffId);

        // Sets up name for employee
        TextView nameTv = findViewById(R.id.nameTv);
        String fullName = employee.firstName + " " + employee.lastName;
        nameTv.setText(fullName);

        // Sets the functionality for the top app bar.
        MaterialToolbar toolbar = findViewById(R.id.topAppBarEditDayOff);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // When top app bar back button is pressed.
        toolbar.setNavigationOnClickListener(this::onCancelClick);

        // When top app bar save button is pressed.
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.save) {
                onSaveClick();
                return true;
            }
            return false;
        });

        // Initialize and sets the OnClickListener for the delete button.
        Button deleteDayOffBtn = findViewById(R.id.btnDeleteDayOff);
        deleteDayOffBtn.setOnClickListener(this::onDayOffDeleteClick);

        // Gather input data
        yearET = findViewById(R.id.yearET);
        monthET = findViewById(R.id.monthET);
        dayET = findViewById(R.id.dayET);
        reasonET = findViewById(R.id.reasonET);

        // Get strings from class
        String dayOffYear = String.valueOf(dayOff.getDate().getYear());
        String dayOffMonth = String.valueOf(dayOff.getDate().getMonthValue());
        String dayOffDay = String.valueOf(dayOff.getDate().getDayOfMonth());
        String dayOffReason = String.valueOf(dayOff.getReason());

        // Sets input fields to selected day off values
        yearET.setText(dayOffYear);
        monthET.setText(dayOffMonth);
        dayET.setText(dayOffDay);
        reasonET.setText(dayOffReason);
    }

    /**
     * Displays the saves button in the top bar menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_save, menu);
        return true;
    }

    /**
     * Handles the save action, updating the day off in the database.
     */
    private void onSaveClick() {
        try {
            // Parse the date from the input fields
            int year = Integer.parseInt(yearET.getText().toString());
            int month = Integer.parseInt(monthET.getText().toString());
            int day = Integer.parseInt(dayET.getText().toString());
            String reason = reasonET.getText().toString();

            LocalDate date = LocalDate.of(year, month, day);
            LocalDate currentDate = LocalDate.now();

            // Check if the date is in the future
            if (date.isBefore(currentDate)) {
                Toast.makeText(this, "The date cannot be in the future. Please select a valid date.", Toast.LENGTH_LONG).show();
                return;
            }

            // Update the day off details
            dayOff.setDate(date);
            dayOff.setReason(reason);

            // Update the day off in the database
            db.dayOffDao().updateDayOff(dayOff);
            finish();

        } catch (NumberFormatException | DateTimeException e) {
            Toast.makeText(this, "Invalid input. Please check the date fields.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Cancels the current operation and returns to the previous screen.
     *
     * @param view The view that triggers this action.
     */
    private void onCancelClick(View view) {
        onBackPressed();
    }

    /**
     * Deletes the selected day off from the database.
     *
     * @param view The view (button) that triggers this action.
     */
    private void onDayOffDeleteClick(View view) {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());

        int dayOffId = getIntent().getIntExtra("dayOffId", -1);
        DayOff dayOff = db.dayOffDao().getDayOffById(dayOffId);

        db.dayOffDao().deleteDayOff(dayOff);
        onBackPressed();
    }
}