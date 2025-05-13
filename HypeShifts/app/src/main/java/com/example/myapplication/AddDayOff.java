package com.example.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
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
 * This activity allows a manager to create a day off for an employee,
 * marking them as unavailable for that day.
 *
 * @author Sergei Borja
 * @since 2023.11.17
 */
public class AddDayOff extends AppCompatActivity {
    TextInputEditText yearET, monthET, dayET, reasonET;
    private AppDatabase db;
    private Employee employee;

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
        setContentView(R.layout.activity_add_day_off);

        // Initialize database and
        db = AppDatabase.getDbInstance(this.getApplicationContext());

        // Retrieve employee ID from the intent that started this activity
        int employeeId = getIntent().getIntExtra("employeeId", -1);
        employee = db.employeeDao().getEmployeeById(employeeId);

        // Initialize UI
        initializeUI();

        // Initialize the top app bar
        setupTopAppBar();
    }

    private void initializeUI() {
        TextView nameTv = findViewById(R.id.nameTv);

        yearET = findViewById(R.id.yearET);
        monthET = findViewById(R.id.monthET);
        dayET = findViewById(R.id.dayET);
        reasonET = findViewById(R.id.reasonET);

        // Set up the employee's full name for display
        String fullName = employee.firstName + " " + employee.lastName;
        nameTv.setText(fullName);
    }

    /**
     * Sets up the top app bar, adding listeners for back and save actions.
     */
    private void setupTopAppBar() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBarSaveEditEmployee);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Initialize the back arrow
        toolbar.setNavigationOnClickListener(this::onCancelClick);

        // Initialize the save button
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.save) {
                onSaveClick();
                return true;
            }
            return false;
        });
    }

    /**
     * Inflates the menu for the top app bar.
     *
     * @param menu The menu instance for the top app bar.
     * @return true to display the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_save, menu);
        return true;
    }

    /**
     * Handles the save action. It gathers input data, validates it,
     * and saves the day off to the database.
     */
    private void onSaveClick() {
        try {
            // Parse and validate the date from input fields
            int year = Integer.parseInt(yearET.getText().toString());
            int month = Integer.parseInt(monthET.getText().toString());
            int day = Integer.parseInt(dayET.getText().toString());
            String reason = reasonET.getText().toString();

            LocalDate date = LocalDate.of(year, month, day);
            LocalDate currentDate = LocalDate.now();

            // Check if the date is in the past
            if (date.isBefore(currentDate)) {
                Toast.makeText(this, "The date cannot be in the past. Please select a valid date.", Toast.LENGTH_LONG).show();
                return;
            }

            // Create and insert a new DayOff record into the database
            DayOff newDayOff = new DayOff();

            newDayOff.employeeId = employee.id;
            newDayOff.date = date;
            newDayOff.reason = reason;
            newDayOff.isAvailable = false;

            db.dayOffDao().insertDayOff(newDayOff);

            // Close the activity after saving
            finish();
        } catch (NumberFormatException | DateTimeException e) {
            Toast.makeText(this, "Invalid input. Please check the date fields.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handles the cancel action. Simply finishes the activity.
     *
     * @param view The view (usually a button) that triggered this action.
     */
    private void onCancelClick(View view) {
        onBackPressed();
    }
}
