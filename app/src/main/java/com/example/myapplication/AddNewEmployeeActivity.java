package com.example.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Availability;
import com.example.myapplication.db.Employee;
import com.example.myapplication.db.User;
import com.google.android.material.appbar.MaterialToolbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Activity to add a new employee to the database.
 *
 * @author  Cedric De Leon, Osman Osman, Sergei Borja
 * @since 2023.10.11
 */
public class AddNewEmployeeActivity extends AppCompatActivity {
    EditText firstNameEt, lastNameEt, dobYearET, dobMonthET, dobDayET,
            addressEt, emailEt, homeNumEt, mobileNumEt, startYearET, startMonthET, startDayET;
    Spinner roleSp, genderSp, contactPrefSp;
    AppDatabase db;

    /**
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
        setContentView(R.layout.activity_add_new_employee);

        // Sets up the database
        db = AppDatabase.getDbInstance(this);

        // Sets up the UI components
        setupToolbar();
        initializeFields();
        setupSpinners();
    }

    /**
     * Sets up the top app bar for the activity.
     */
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBarSaveAddEmployee);
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
     * Initializes the input fields for employee information.
     */
    private void initializeFields() {
        //Personal Details
        firstNameEt = findViewById(R.id.firstNameET);
        lastNameEt = findViewById(R.id.lastNameET);
        genderSp = findViewById(R.id.genderSp);
        addressEt = findViewById(R.id.addressET);
        dobYearET = findViewById(R.id.dobYearET);
        dobMonthET = findViewById(R.id.dobMonthET);
        dobDayET = findViewById(R.id.dobDayET);

        //Contact Details
        emailEt = findViewById(R.id.emailET);
        homeNumEt = findViewById(R.id.homeNumET);
        mobileNumEt = findViewById(R.id.mobileNumET);
        contactPrefSp = findViewById(R.id.contactPrefSp);

        //Work Details
        startYearET = findViewById(R.id.startYearET);
        startMonthET = findViewById(R.id.startMonthET);
        startDayET = findViewById(R.id.startDayET);
        roleSp = findViewById(R.id.roleSp);
    }

    /**
     * Sets up the spinners with appropriate adapters for roles, gender, and contact preferences.
     */
    private void setupSpinners() {
        // Sets up role spinner
        ArrayAdapter<CharSequence> rolesAdapter = ArrayAdapter.createFromResource(
                this, R.array.roles_array, android.R.layout.simple_spinner_item);
        rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSp.setAdapter(rolesAdapter);

        // Sets up gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this, R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSp.setAdapter(genderAdapter);

        // Sets up contact preference spinner
        ArrayAdapter<CharSequence> contactPrefAdapter = ArrayAdapter.createFromResource(
                this, R.array.contact_preference_array, android.R.layout.simple_spinner_item);
        contactPrefAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactPrefSp.setAdapter(contactPrefAdapter);
    }

    /**
     * Handles the saving of a new employee.
     * Gathers input data, validates it, and saves the employee to the database.
     */
    private void saveEmployee() {
        // Extracting text from EditText fields
        //Personal Details
        String firstName = firstNameEt.getText().toString().trim();
        String lastName = lastNameEt.getText().toString().trim();
        String gender = genderSp.getSelectedItem().toString().trim();
        String address = addressEt.getText().toString().trim();
        String dob = dobYearET.getText().toString().trim() + "-" +
                dobMonthET.getText().toString().trim() + "-" +
                dobDayET.getText().toString().trim();

        //Contact Details
        String homeNum = homeNumEt.getText().toString().trim();
        String mobileNum = mobileNumEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String contactPref = contactPrefSp.getSelectedItem().toString().trim();

        //Work Details
        String role = roleSp.getSelectedItem().toString().trim();
        String startDate = startYearET.getText().toString().trim() + "-" +
                startMonthET.getText().toString().trim() + "-" +
                startDayET.getText().toString().trim();


        // Input validation
        if (!firstName.isEmpty() && !lastName.isEmpty() && !mobileNum.isEmpty() && !email.isEmpty() && !role.isEmpty()) {
            try {
                // Create and insert employee data
                saveEmployeeToDatabase(firstName, lastName, gender, dob, startDate, address, homeNum, mobileNum, email, contactPref, role);
                setResult(RESULT_OK);
            } catch (Exception e) {
                // In case of any failure during the save process
                setResult(RESULT_CANCELED);
                Toast.makeText(this, "Failed to add employee.", Toast.LENGTH_SHORT).show();
            }
            finish();
        } else {
            // Not all inputs are entered
            Toast.makeText(this, "Please enter all required information.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the employee details to the database.
     *
     * @param firstName The first name of the employee.
     * @param lastName The last name of the employee.
     * @param gender The gender of the employee.
     * @param dob The date of birth of the employee.
     * @param startDate The start date of the employee.
     * @param address The address of the employee.
     * @param homeNum The home number of the employee.
     * @param mobileNum The mobile number of the employee.
     * @param email The email of the employee.
     * @param contactPref The contact preference of the employee.
     * @param role The role of the employee.
     */
    private void saveEmployeeToDatabase(String firstName, String lastName, String gender, String dob, String startDate,
                                        String address, String homeNum, String mobileNum, String email, String contactPref, String role) {
        // Create a new Employee with the provided details
        Employee emp = new Employee(firstName, lastName, mobileNum, homeNum, gender,
                LocalDate.parse(dob, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE),
                contactPref, address, email, role, true);


        // If the role is Owner, set their qualifications for opening and closing as true
        if (role.equals("Owner")) {
            emp.setIs_qualified_opening(true);
            emp.setIs_qualified_closing(true);
        }

        // Insert the new Employee into the database and get the ID
        long emp_id = db.employeeDao().insertEmployee(emp);

        // For Manager or Owner, create a User account with default credentials
        if (role.equals("Manager") || role.equals("Owner")) {
            User user = new User();
            user.employeeId = ((int) emp_id);
            user.userName = lastName.replaceAll(" ", "") + ((int) emp_id);
            user.password = "password";

            db.userDao().insertUser(user);
        }

        // Setup default availability for the new employee
        setupAvailabilityForEmployee(emp_id);
    }

    /**
     * Sets up availability for a new employee.
     * For weekends (Saturday and Sunday), it sets a full shift.
     * For weekdays, it sets morning, afternoon, and evening shifts.
     *
     * @param emp_id The employee's ID for whom the availability is being set up.
     */
    private void setupAvailabilityForEmployee(long emp_id) {
        for (Availability.DayOfWeek day : Availability.DayOfWeek.values()) {
            // Check if the day is Saturday or Sunday
            if (day == Availability.DayOfWeek.SATURDAY || day == Availability.DayOfWeek.SUNDAY) {
                Availability availability = new Availability();
                availability.employeeId = (int) emp_id;
                availability.dayOfWeek = day;
                availability.shiftType = Availability.ShiftType.FULL; // Full shift for weekends
                availability.isAvailable = true;

                // Insert the availability entry into the database
                db.availabilityDao().insertAvailability(availability);
            } else {
                // For weekdays, create morning, afternoon, and evening shifts
                for (Availability.ShiftType shiftType : new Availability.ShiftType[]{Availability.ShiftType.MORNING, Availability.ShiftType.AFTERNOON, Availability.ShiftType.EVENING}) {
                    Availability availability = new Availability();
                    availability.employeeId = (int) emp_id;
                    availability.dayOfWeek = day;
                    availability.shiftType = shiftType; // Morning, afternoon, or evening shift
                    availability.isAvailable = true;

                    // Insert the availability entry into the database
                    db.availabilityDao().insertAvailability(availability);
                }
            }
        }
    }

}
