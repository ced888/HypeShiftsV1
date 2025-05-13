package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Availability;
import com.example.myapplication.db.DayOff;
import com.example.myapplication.db.Employee;
import com.example.myapplication.db.ShiftInstance;
import com.google.android.material.appbar.MaterialToolbar;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the activity for displaying and managing an employee's profile.
 *
 * @author Osman Osman, Sergei Borja
 * @since 2023.10.14
 */
public class EmployeeProfileActivity extends AppCompatActivity implements DayOffAdapter.DayOffItemClickListener {
    private static final int EDIT_EMPLOYEE_REQUEST_CODE = 2;
    AppDatabase db;

    // UI Components
    LinearLayout noDayOffsLayout, noShiftsLayout;
    private TextView nameTvDetailed, positionTv, emailTv, homeNumTv, mobileNumTv, startTv, fullNameTv, genderTv, dobTv, addressTv, employeeIdTv, contactPrefTv, openQualTv, closeQualTv;
    private Button messageBtn, editBtn, deleteEmployeeBtn, addDayOffBtn, editAvailBtn, seeMoreDayOffsBtn, seeMoreShiftsBtn;
    private RecyclerView dayOffRecyclerView, shiftRecyclerView;
    private DayOffAdapter dayOffAdapter;
    private ProfileShiftAdapter shiftAdapter;

    // Variables
    private int employeeId;
    private int maxDayOffsToShow = 3, maxShiftsToShow = 3;

    /**
     * Initializes the activity when it is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains the data
     *                           it most recently supplied. Otherwise, it is null.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_employee_profile);

        // Initialize Database
        db = AppDatabase.getDbInstance(this.getApplicationContext());

        // Setup the display
        initializeUI();
        setupToolbar();
        fetchAndDisplayEmployeeData();
        initializeDayOffsRecyclerView();
        setupButtonListener();
    }

    /**
     * Initializes the user interface components of the activity.
     */
    private void initializeUI() {
        // Employee's Name
        nameTvDetailed = findViewById(R.id.nameTvDetailed);

        //Personal Details
        fullNameTv = findViewById(R.id.fullNameTv);
        genderTv = findViewById(R.id.genderTv);
        dobTv = findViewById(R.id.dobTv);
        addressTv = findViewById(R.id.addressTv);

        //Contact Details
        homeNumTv = findViewById(R.id.homeNumTv);
        mobileNumTv = findViewById(R.id.mobileNumTv);
        emailTv = findViewById(R.id.emailTv);
        contactPrefTv = findViewById(R.id.contactPrefTv);

        //Work Details
        positionTv = findViewById(R.id.positionTv);
        startTv = findViewById(R.id.startTv);
        employeeIdTv = findViewById(R.id.employeeIdTv);
        openQualTv = findViewById(R.id.openQualTv);
        closeQualTv = findViewById(R.id.closeQualTv);

        // Recycler Views
        dayOffRecyclerView = findViewById(R.id.upcomingDayOffs);
        dayOffRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        shiftRecyclerView = findViewById(R.id.upcomingShifts);
        shiftRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Notification
        noDayOffsLayout = findViewById(R.id.noDayOffsLayout);
        noShiftsLayout = findViewById(R.id.noShiftsLayout);

        // Initialize the buttons
        messageBtn = findViewById(R.id.messageBtn);
        editBtn = findViewById(R.id.editBtn);
        deleteEmployeeBtn = findViewById(R.id.btnDeleteEmployeePage);
        addDayOffBtn = findViewById(R.id.addDayOffBtn);
        editAvailBtn = findViewById(R.id.editAvailabilityBtn);
        seeMoreDayOffsBtn = findViewById(R.id.seeMoreDayOffsBtn);
        seeMoreShiftsBtn = findViewById(R.id.seeMoreShiftsBtn);
    }

    /**
     * Sets up the toolbar for the activity.
     */
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBarEmployee);
        toolbar.setTitle(getIntent().getStringExtra("name"));
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * Fetches and displays the employee data based on the employee ID passed from the intent.
     */
    @SuppressLint("SetTextI18n")
    private void fetchAndDisplayEmployeeData() {
        Intent intent = getIntent();
        employeeId = intent.getIntExtra("id", -1);

        if (employeeId != -1) {
            Employee employee = db.employeeDao().getEmployeeById(employeeId);

            if (employee != null) {
                String fullName = employee.getFirstName() + " " + employee.getLastName();
                nameTvDetailed.setText(fullName);
                fullNameTv.setText("Full Name: " + fullName);
                genderTv.setText("Gender: " + employee.getGender());
                dobTv.setText("Date of Birth: " + employee.getDob().toString());
                addressTv.setText("Address: " + employee.getAddress());
                homeNumTv.setText("Home Number: " + employee.getHomeNumber());
                mobileNumTv.setText("Mobile Number: " + employee.getMobileNumber());
                emailTv.setText("Email: " + employee.getEmail());
                startTv.setText("Start Date: " + employee.getStartDate().toString());
                contactPrefTv.setText("Contact Preference: " + employee.getContactPreference());
                employeeIdTv.setText("Employee ID: " + employeeId);
                positionTv.setText("Position: " + employee.getRole());
                openQualTv.setText("Opening: " + employee.isIs_qualified_opening());
                closeQualTv.setText("Closing: " + employee.isIs_qualified_closing());

                List<Availability> availabilities = db.availabilityDao().getAvailabilityByEmployee(employeeId);
                initializeAvailability(availabilities);
            }
        }
    }

    /**
     * Initializes the RecyclerView for displaying the day offs of the employee.
     */
    private void initializeDayOffsRecyclerView() {
        List<DayOff> dayOffList = db.dayOffDao().getDaysOffByEmployee(employeeId);

        dayOffAdapter = new DayOffAdapter(dayOffList, this);
        dayOffRecyclerView.setAdapter(dayOffAdapter);
    }

    /**
     * Sets up the listeners for various buttons in the activity.
     */
    private void setupButtonListener() {
        // Top of Profile
        messageBtn.setOnClickListener(this::onMessageClick);
        editBtn.setOnClickListener(this::onEditProfileClick);
        addDayOffBtn.setOnClickListener(this::onAddDayOffClick);
        editAvailBtn.setOnClickListener(this::onEditAvailabilityClick);

        // In Employee Details
        seeMoreDayOffsBtn.setOnClickListener(this::onSeeMoreDayOffsClick);
        seeMoreShiftsBtn.setOnClickListener(this::onSeeMoreShiftsClick);

        // Very Bottom of Profile
        deleteEmployeeBtn.setOnClickListener(this::onDeleteEmployeeClick);
    }

    /**
     * Handles the click event for sending a message to the employee.
     *
     * @param view The view that triggers this action.
     */
    public void onMessageClick(View view) {
        // Retrieve and split the preferred contact method
        String contactPreference = contactPrefTv.getText().toString().trim();
        String homePhoneNumber = homeNumTv.getText().toString().trim();
        String mobilePhoneNumber = mobileNumTv.getText().toString().trim();
        String email = emailTv.getText().toString().trim();

        // Check if the preference is Email and if the email is available
        if (contactPreference.equals("Contact Preference: E-mail") && !email.isEmpty()) {
            // Extract the actual email address
            String actualEmail = email.split("Email: ")[1];

            try {
                // Create an intent to open an email app
                Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + actualEmail));
                startActivity(emailIntent);
            } catch (Exception e) {
                Toast.makeText(this, "There is no email app available", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        // Check if the preference is SMS and if the phone number is available
        else if (contactPreference.equals("Contact Preference: Mobile Phone") && !mobilePhoneNumber.isEmpty()) {
            // Extract the actual phone number
            String actualPhoneNumber = mobilePhoneNumber.split("Mobile Number: ")[1];

            // Open the SMS app
            Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + actualPhoneNumber));
            startActivity(smsIntent);
        } else if (contactPreference.equals("Contact Preference: Home Phone") && !homePhoneNumber.isEmpty()) {
            // Extract the actual phone number
            String actualPhoneNumber = homePhoneNumber.split("Home Number: ")[1];

            // Open the SMS app
            Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + actualPhoneNumber));
            startActivity(smsIntent);
        } else {
            Toast.makeText(this, "Valid contact information not available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Launches the EditEmployeeActivity for editing the employee's profile.
     *
     * @param view The view that triggers this action.
     */
    public void onEditProfileClick(View view) {
        Intent intent = new Intent(EmployeeProfileActivity.this, EditEmployeeActivity.class);

        intent.putExtra("employeeId", employeeId);

        startActivity(intent);
    }

    /**
     * Launches the EditAvailability activity for editing the employee's availability.
     *
     * @param view The view that triggers this action.
     */
    private void onEditAvailabilityClick(View view) {
        Intent intent = new Intent(EmployeeProfileActivity.this, EditAvailability.class);

        intent.putExtra("employeeId", employeeId);

        startActivity(intent);
    }

    /**
     * Launches the AddDayOff activity for adding a day off for the employee.
     *
     * @param view The view that triggers this action.
     */
    private void onAddDayOffClick(View view) {
        Intent intent = new Intent(EmployeeProfileActivity.this, AddDayOff.class);

        intent.putExtra("employeeId", employeeId);

        startActivity(intent);
    }

    /**
     * Handles the click event on a day off item in the RecyclerView.
     *
     * @param dayOff The DayOff object representing the day off that was clicked.
     */
    @Override
    public void onDayOffClick(DayOff dayOff) {
        Intent intent = new Intent(EmployeeProfileActivity.this, EditDayOff.class);

        intent.putExtra("employeeId", employeeId);
        intent.putExtra("dayOffId", dayOff.id);

        startActivity(intent);
    }

    /**
     * Handles the click event for deleting the employee.
     *
     * @param view The view that triggers this action.
     */
    public void onDeleteEmployeeClick(View view) {
        Employee employee = db.employeeDao().getEmployeeById(employeeId);
        if (employee != null) {
            db.employeeDao().deleteEmployee(employee);
            finish();
        } else {
            Toast.makeText(EmployeeProfileActivity.this, "Employee not found!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Refreshes employee data, shifts, and availability when resuming the activity.
     */
    @Override
    protected void onResume() {
        super.onResume();

        refreshEmployeeData(employeeId);
        fetchAndDisplayShiftData(employeeId);
        updateAvailabilityUI(employeeId);
        refreshDayOffsList(employeeId);
    }

    /**
     * Handle the result from the EditEmployeeProfileActivity.
     *
     * @param requestCode The request code to identify where this result came from.
     * @param resultCode  The result code returned by the edit employee activity through its setResult().
     * @param data        An Intent, which store data to move from one activity to another.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_EMPLOYEE_REQUEST_CODE) {
            updateEmployeeUI(employeeId);
        }
    }

    /**
     * Refreshes the employee data and updates the UI accordingly.
     *
     * @param employeeId The ID of the employee to refresh data for.
     */
    private void refreshEmployeeData(int employeeId) {
        Log.d("EmployeeProfileActivity", "Refreshing employee data for ID: " + employeeId);
        updateEmployeeUI(employeeId); // Re-using the method to avoid repetition
    }

    /**
     * Updates the UI with the latest employee data.
     *
     * @param employeeId The ID of the employee whose data is to be updated.
     */
    @SuppressLint("SetTextI18n")
    private void updateEmployeeUI(int employeeId) {
        Employee updatedEmployee = db.employeeDao().getEmployeeById(employeeId);

        if (updatedEmployee != null) {
            // Update UI components with employee data
            String fullName = updatedEmployee.getFirstName() + " " + updatedEmployee.getLastName();
            nameTvDetailed.setText(fullName);
            fullNameTv.setText("Full Name: " + fullName);
            genderTv.setText("Gender: " + updatedEmployee.getGender());
            dobTv.setText("Date of Birth: " + updatedEmployee.getDob().toString());
            addressTv.setText("Address: " + updatedEmployee.getAddress());
            homeNumTv.setText("Home Number: " + updatedEmployee.getHomeNumber());
            mobileNumTv.setText("Mobile Number: " + updatedEmployee.getMobileNumber());
            emailTv.setText("Email: " + updatedEmployee.getEmail());
            startTv.setText("Start Date: " + updatedEmployee.getStartDate().toString());
            contactPrefTv.setText("Contact Preference: " + updatedEmployee.getContactPreference());
            employeeIdTv.setText("Employee ID: " + employeeId);
            positionTv.setText("Position: " + updatedEmployee.getRole());
            openQualTv.setText("Opening: " + updatedEmployee.isIs_qualified_opening());
            closeQualTv.setText("Closing: " + updatedEmployee.isIs_qualified_closing());
        } else {
            Log.e("EmployeeProfileActivity", "No employee data found for ID: " + employeeId);
        }
    }

    /**
     * Initializes the buttons for showing employee availability.
     *
     * @param buttonId    The ID of the button to initialize.
     * @param isAvailable Whether the employee is available or not.
     */
    private void initializeDayShiftButton(int buttonId, boolean isAvailable) {
        Button button = findViewById(buttonId);
        String color = isAvailable ? "#00C49A" : "#706F6D";
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
    }

    /**
     * Initializes the buttons based on the employee's availability data.
     *
     * @param availabilities List of availability instances for the employee.
     */
    private void initializeAvailability(List<Availability> availabilities) {
        // Initialize each button with availability data
        initializeDayShiftButton(R.id.btnSunFull, checkAvailability(availabilities, "SUNDAY", "FULL"));

        initializeDayShiftButton(R.id.btnMonMor, checkAvailability(availabilities, "MONDAY", "MORNING"));
        initializeDayShiftButton(R.id.btnMonAft, checkAvailability(availabilities, "MONDAY", "AFTERNOON"));
        initializeDayShiftButton(R.id.btnMonEve, checkAvailability(availabilities, "MONDAY", "EVENING"));

        initializeDayShiftButton(R.id.btnTueMor, checkAvailability(availabilities, "TUESDAY", "MORNING"));
        initializeDayShiftButton(R.id.btnTueAft, checkAvailability(availabilities, "TUESDAY", "AFTERNOON"));
        initializeDayShiftButton(R.id.btnTueEve, checkAvailability(availabilities, "TUESDAY", "EVENING"));

        initializeDayShiftButton(R.id.btnWedMor, checkAvailability(availabilities, "WEDNESDAY", "MORNING"));
        initializeDayShiftButton(R.id.btnWedAft, checkAvailability(availabilities, "WEDNESDAY", "AFTERNOON"));
        initializeDayShiftButton(R.id.btnWedEve, checkAvailability(availabilities, "WEDNESDAY", "EVENING"));

        initializeDayShiftButton(R.id.btnThuMor, checkAvailability(availabilities, "THURSDAY", "MORNING"));
        initializeDayShiftButton(R.id.btnThuAft, checkAvailability(availabilities, "THURSDAY", "AFTERNOON"));
        initializeDayShiftButton(R.id.btnThuEve, checkAvailability(availabilities, "THURSDAY", "EVENING"));

        initializeDayShiftButton(R.id.btnFriMor, checkAvailability(availabilities, "FRIDAY", "MORNING"));
        initializeDayShiftButton(R.id.btnFriAft, checkAvailability(availabilities, "FRIDAY", "AFTERNOON"));
        initializeDayShiftButton(R.id.btnFriEve, checkAvailability(availabilities, "FRIDAY", "EVENING"));

        initializeDayShiftButton(R.id.btnSatFull, checkAvailability(availabilities, "SATURDAY", "FULL"));
    }

    /**
     * Checks the availability of an employee for a specific day and shift.
     *
     * @param availabilities List of the employee's availability data.
     * @param day            The day to check availability for.
     * @param shift          The shift to check availability for.
     * @return True if the employee is available, false otherwise.
     */
    private boolean checkAvailability(List<Availability> availabilities, String day, String shift) {
        for (Availability availability : availabilities) {
            if (availability.dayOfWeek.toString().equals(day) && availability.shiftType.toString().equals(shift)) {
                return availability.isAvailable;
            }
        }
        return false; // Default to unavailable if not found
    }

    /**
     * Updates the UI to reflect the current availability of the employee.
     *
     * @param employeeId The ID of the employee whose availability UI is to be updated.
     */
    private void updateAvailabilityUI(int employeeId) {
        List<Availability> availabilities = db.availabilityDao().getAvailabilityByEmployee(employeeId);
        updateAvailabilityButtons(availabilities);
    }

    /**
     * Updates the buttons based on the employee's availability data.
     *
     * @param availabilities List of availability instances for the employee.
     */
    private void updateAvailabilityButtons(List<Availability> availabilities) {
        // Initialize each button with availability data
        setButtonColor(R.id.btnSunFull, checkAvailability(availabilities, "SUNDAY", "FULL"));

        setButtonColor(R.id.btnMonMor, checkAvailability(availabilities, "MONDAY", "MORNING"));
        setButtonColor(R.id.btnMonAft, checkAvailability(availabilities, "MONDAY", "AFTERNOON"));
        setButtonColor(R.id.btnMonEve, checkAvailability(availabilities, "MONDAY", "EVENING"));

        setButtonColor(R.id.btnTueMor, checkAvailability(availabilities, "TUESDAY", "MORNING"));
        setButtonColor(R.id.btnTueAft, checkAvailability(availabilities, "TUESDAY", "AFTERNOON"));
        setButtonColor(R.id.btnTueEve, checkAvailability(availabilities, "TUESDAY", "EVENING"));

        setButtonColor(R.id.btnWedMor, checkAvailability(availabilities, "WEDNESDAY", "MORNING"));
        setButtonColor(R.id.btnWedAft, checkAvailability(availabilities, "WEDNESDAY", "AFTERNOON"));
        setButtonColor(R.id.btnWedEve, checkAvailability(availabilities, "WEDNESDAY", "EVENING"));

        setButtonColor(R.id.btnThuMor, checkAvailability(availabilities, "THURSDAY", "MORNING"));
        setButtonColor(R.id.btnThuAft, checkAvailability(availabilities, "THURSDAY", "AFTERNOON"));
        setButtonColor(R.id.btnThuEve, checkAvailability(availabilities, "THURSDAY", "EVENING"));

        setButtonColor(R.id.btnFriMor, checkAvailability(availabilities, "FRIDAY", "MORNING"));
        setButtonColor(R.id.btnFriAft, checkAvailability(availabilities, "FRIDAY", "AFTERNOON"));
        setButtonColor(R.id.btnFriEve, checkAvailability(availabilities, "FRIDAY", "EVENING"));

        setButtonColor(R.id.btnSatFull, checkAvailability(availabilities, "SATURDAY", "FULL"));
    }

    /**
     * Sets the color of a button based on availability status.
     *
     * @param buttonId    The ID of the button to update.
     * @param isAvailable Whether the employee is available or not.
     */
    private void setButtonColor(int buttonId, boolean isAvailable) {
        Button button = findViewById(buttonId);
        String color = isAvailable ? "#00C49A" : "#706F6D";
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
    }

    /**
     * Refreshes the list of day offs for the employee.
     *
     * @param employeeId The ID of the employee whose day offs are to be refreshed.
     */
    private void refreshDayOffsList(int employeeId) {
        List<DayOff> dayOffList = db.dayOffDao().getDaysOffByEmployee(employeeId);

        LocalDate currentDate = LocalDate.now();
        List<DayOff> filteredDayOffs = dayOffList.stream()
                .filter(dayOff -> !dayOff.date.isBefore(currentDate))
                .sorted(Comparator.comparing(dayOff -> dayOff.date))
                .limit(maxDayOffsToShow)
                .collect(Collectors.toList());

        updateDayOffsAdapter(filteredDayOffs);
        dayOffRecyclerView.setVisibility(filteredDayOffs.isEmpty() ? View.GONE : View.VISIBLE);
        noDayOffsLayout.setVisibility(filteredDayOffs.isEmpty() ? View.VISIBLE : View.GONE);
        seeMoreDayOffsBtn.setVisibility(dayOffList.size() > maxDayOffsToShow ? View.VISIBLE : View.GONE);
    }

    /**
     * Updates the RecyclerView adapter for day offs with the latest data.
     *
     * @param dayOffs The list of day offs to update the adapter with.
     */
    private void updateDayOffsAdapter(List<DayOff> dayOffs) {
        if (dayOffAdapter != null) {
            dayOffAdapter.updateDayOffsList(dayOffs);
        } else {
            dayOffAdapter = new DayOffAdapter(dayOffs, this);
            dayOffRecyclerView.setAdapter(dayOffAdapter);
        }
    }

    /**
     * Handles the "See More Day Offs" button click.
     * Increases the number of day offs displayed in the RecyclerView.
     *
     * @param view The view that triggers this action.
     */
    public void onSeeMoreDayOffsClick(View view) {
        // Increase the maximum size
        maxDayOffsToShow += 3;

        // Refresh the list with the new maximum size
        refreshDayOffsList(employeeId);
    }

    /**
     * Fetches and displays the upcoming shift data for the employee.
     *
     * @param employeeId The ID of the employee whose shift data is to be displayed.
     */
    private void fetchAndDisplayShiftData(int employeeId) {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<ShiftInstance> shiftList = db.shiftDao().getAllShiftInstanceByEmployeeID(employeeId);

        LocalDate currentDate = LocalDate.now();
        List<ShiftInstance> filteredShifts = shiftList.stream()
                .filter(shift -> {
                    LocalDate shiftDate = db.shiftDao().getDateOfShift(shift.id);
                    return shiftDate != null && !shiftDate.isBefore(currentDate);
                }).sorted(Comparator.comparing(shift -> db.shiftDao().getDateOfShift(shift.id)))
                .limit(maxShiftsToShow)
                .collect(Collectors.toList());

        updateShiftsRecyclerView(filteredShifts, shiftList);
    }

    /**
     * Updates the RecyclerView for shifts with the latest data.
     *
     * @param shifts    The list of shifts to update the RecyclerView with.
     * @param allShifts The full list of all shifts for the employee.
     */
    private void updateShiftsRecyclerView(List<ShiftInstance> shifts, List<ShiftInstance> allShifts) {
        if (shifts.isEmpty()) {
            shiftRecyclerView.setVisibility(View.GONE);
            noShiftsLayout.setVisibility(View.VISIBLE);
        } else {
            shiftRecyclerView.setVisibility(View.VISIBLE);
            noShiftsLayout.setVisibility(View.GONE);

            if (shiftAdapter != null) {
                shiftAdapter.updateShiftsList(shifts);
            } else {
                shiftAdapter = new ProfileShiftAdapter(shifts, db);
                shiftRecyclerView.setAdapter(shiftAdapter);
            }
        }
        seeMoreShiftsBtn.setVisibility(allShifts.size() > maxShiftsToShow ? View.VISIBLE : View.GONE);
    }

    /**
     * Handles the "See More Shifts" button click.
     * Increases the number of shifts displayed in the RecyclerView.
     *
     * @param view The view (button) that triggers this action.
     */
    public void onSeeMoreShiftsClick(View view) {
        maxShiftsToShow += 3;
        fetchAndDisplayShiftData(employeeId);
    }
}



