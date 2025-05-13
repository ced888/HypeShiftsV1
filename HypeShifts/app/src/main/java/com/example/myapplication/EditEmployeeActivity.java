package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Employee;
import com.google.android.material.appbar.MaterialToolbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Activity for editing an employee's details.
 * Provides a user interface to update existing employee information in the database.
 * <p>
 * Authors: Osman Osman, Sergei Borja
 * Since: 2023.10.14
 */
public class EditEmployeeActivity extends AppCompatActivity {
    TextView nameTvDetailed;
    EditText firstNameEt, lastNameEt, addressEt, emailEt, homeNumEt, mobileNumEt, dobYearET, dobMonthET, dobDayET, startYearET, startMonthET, startDayET;
    Spinner roleSp, genderSp, contactPrefSp, openQualifySp, closeQualifySp;
    AppDatabase db;
    Employee employee;

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
        setContentView(R.layout.activity_edit_employee);

        // Obtains employeeID from intent and get employee from database
        int employeeId = getIntent().getIntExtra("employeeId", -1);
        db = AppDatabase.getDbInstance(this.getApplicationContext());
        employee = db.employeeDao().getEmployeeById(employeeId);

        setupToolbar();
        initializeFields();
        setupSpinners();
    }

    /**
     * Sets up the top app bar, adding listeners for back and save actions.
     */
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBarSaveEditEmployee);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Initialize the back arrow
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize the save button
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.save) {
                saveEmployee();
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
     * Initializes and populates UI fields with employee data.
     */
    private void initializeFields() {
        // Displays the name of the employee
        nameTvDetailed = findViewById(R.id.nameTvDetailed);
        String fullName = employee.firstName + " " + employee.lastName;
        nameTvDetailed.setText(fullName);

        // Setup the interface
        firstNameEt = findViewById(R.id.firstNameET);
        lastNameEt = findViewById(R.id.lastNameET);
        genderSp = findViewById(R.id.genderSp);

        dobYearET = findViewById(R.id.dobYearET);
        dobMonthET = findViewById(R.id.dobMonthET);
        dobDayET = findViewById(R.id.dobDayET);

        addressEt = findViewById(R.id.addressET);
        emailEt = findViewById(R.id.emailET);
        roleSp = findViewById(R.id.roleSp);
        homeNumEt = findViewById(R.id.homeNumET);
        mobileNumEt = findViewById(R.id.mobileNumET);
        contactPrefSp = findViewById(R.id.contactPrefSp);

        startYearET = findViewById(R.id.startYearET);
        startMonthET = findViewById(R.id.startMonthET);
        startDayET = findViewById(R.id.startDayET);

        openQualifySp = findViewById(R.id.openQualifySp);
        closeQualifySp = findViewById(R.id.closeQualifySp);

        // Populate the fields with data of the employee
        firstNameEt.setText(employee.getFirstName());
        lastNameEt.setText(employee.getLastName());
        addressEt.setText(employee.getAddress());
        emailEt.setText(employee.getEmail());
        homeNumEt.setText(employee.getHomeNumber());
        mobileNumEt.setText(employee.getMobileNumber());

        String dobYearSet = String.valueOf(employee.getDob().getYear());
        String dobMonthSet = String.format("%02d", employee.getDob().getMonthValue());
        String dobDaySet = String.format("%02d", employee.getDob().getDayOfMonth());

        String startYearSet = String.valueOf(employee.getStartDate().getYear());
        String startMonthSet = String.format("%02d", employee.getStartDate().getMonthValue());
        String startDaySet = String.format("%02d", employee.getStartDate().getDayOfMonth());

        dobYearET.setText(dobYearSet);
        dobMonthET.setText(dobMonthSet);
        dobDayET.setText(dobDaySet);

        startYearET.setText(startYearSet);
        startMonthET.setText(startMonthSet);
        startDayET.setText(startDaySet);
    }

    /**
     * Sets up spinners with adapters and selects the appropriate values.
     */
    private void setupSpinners() {
        // Setup the interface
        ArrayAdapter<CharSequence> roles_adapter = ArrayAdapter.createFromResource(this, R.array.roles_array, android.R.layout.simple_spinner_item);
        roles_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSp.setAdapter(roles_adapter);

        ArrayAdapter<CharSequence> gender_adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        gender_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSp.setAdapter(gender_adapter);

        ArrayAdapter<CharSequence> contact_pref_adapter = ArrayAdapter.createFromResource(this, R.array.contact_preference_array, android.R.layout.simple_spinner_item);
        contact_pref_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactPrefSp.setAdapter(contact_pref_adapter);

        ArrayAdapter<CharSequence> open_qualification_adapter = ArrayAdapter.createFromResource(this, R.array.qualifications_array, android.R.layout.simple_spinner_item);
        open_qualification_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        openQualifySp.setAdapter(open_qualification_adapter);

        ArrayAdapter<CharSequence> close_qualification_adapter = ArrayAdapter.createFromResource(this, R.array.qualifications_array, android.R.layout.simple_spinner_item);
        close_qualification_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        closeQualifySp.setAdapter(close_qualification_adapter);

        // Populate the fields with data of the employee
        setSpinnerSelection(genderSp, employee.getGender());
        setSpinnerSelection(roleSp, employee.getRole());
        setSpinnerSelection(contactPrefSp, employee.getContactPreference());
        setSpinnerSelection(openQualifySp, employee.isIs_qualified_opening());
        setSpinnerSelection(closeQualifySp, employee.isIs_qualified_closing());
    }

    /**
     * Sets the selection of a spinner based on the given value.
     *
     * @param spinner The spinner whose selection is to be set.
     * @param value   The value to match in the spinner's adapter.
     */
    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    /**
     * Saves the employee data to the database when the save button is clicked.
     */
    private void saveEmployee() {
        // Handle save action
        String firstName = firstNameEt.getText().toString().trim();
        String lastName = lastNameEt.getText().toString().trim();
        String gender = genderSp.getSelectedItem().toString().trim();

        String dobYear = dobYearET.getText().toString().trim();
        String dobMonth = dobMonthET.getText().toString().trim();
        String dobDay = dobDayET.getText().toString().trim();
        String dob = dobYear + "-" + dobMonth + "-" + dobDay;

        String startYear = startYearET.getText().toString().trim();
        String startMonth = startMonthET.getText().toString().trim();
        String startDay = startDayET.getText().toString().trim();
        String startDate = startYear + "-" + startMonth + "-" + startDay;

        String address = addressEt.getText().toString().trim();
        String homeNum = homeNumEt.getText().toString().trim();
        String mobileNum = mobileNumEt.getText().toString().trim();
        String contactPref = contactPrefSp.getSelectedItem().toString().trim();
        boolean openingQualifications = "Qualified".equals(openQualifySp.getSelectedItem().toString().trim());
        boolean closingQualifications = "Qualified".equals(closeQualifySp.getSelectedItem().toString().trim());
        String email = emailEt.getText().toString().trim();
        String role = roleSp.getSelectedItem().toString().trim();

        if (!firstName.isEmpty() && !lastName.isEmpty() && !mobileNum.isEmpty() && !email.isEmpty() && !role.isEmpty()) {
            try {
                if (employee != null) {
                    // Update the existing employee's information
                    employee.setFirstName(firstName);
                    employee.setLastName(lastName);
                    employee.setGender(gender);
                    employee.setDob(LocalDate.parse(dob, DateTimeFormatter.ISO_LOCAL_DATE));
                    employee.setStartDate(LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE));
                    employee.setAddress(address);
                    employee.setHomeNumber(homeNum);
                    employee.setMobileNumber(mobileNum);
                    employee.setContactPreference(contactPref);
                    employee.setEmail(email);
                    employee.setRole(role);
                    employee.setIs_qualified_opening(openingQualifications);
                    employee.setIs_qualified_closing(closingQualifications);

                    // Update the employee in the database
                    db.employeeDao().UpdateEmployeeProfile(employee);

                    setResult(RESULT_OK);
                    Toast.makeText(EditEmployeeActivity.this, "Employee updated!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Handle the case where the employee doesn't exist
                    Toast.makeText(EditEmployeeActivity.this, "Employee not found.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // Set the result to indicate failure and finish the activity
                setResult(RESULT_CANCELED);
                Toast.makeText(EditEmployeeActivity.this, "Failed to update employee.", Toast.LENGTH_SHORT).show();
                Log.e(null, e.getMessage());
            }
        } else {
            Toast.makeText(EditEmployeeActivity.this, "Please enter all required information.", Toast.LENGTH_SHORT).show();
        }
    }
}