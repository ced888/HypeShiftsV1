package com.example.myapplication;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Availability;
import com.example.myapplication.db.Employee;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EditAvailability is a activity that allows the manager to edit an
 * employee's availability.
 *
 * @author Sergei Borja
 * @since 2023.11.17
 */
public class EditAvailability extends AppCompatActivity {
    private final Map<String, Boolean> availabilityMap = new HashMap<>();
    private AppDatabase db;
    private int employeeId;

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
        setContentView(R.layout.activity_edit_availability);

        // Retrieve the employee ID from the intent
        employeeId = getIntent().getIntExtra("employeeId", -1);

        // Fetch availability from the database
        db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Availability> availabilities = db.availabilityDao().getAvailabilityByEmployee(employeeId);
        Employee employee = db.employeeDao().getEmployeeById(employeeId);

        // Sets up name for employee
        TextView nameTv = findViewById(R.id.nameTv);
        String fullName = employee.firstName + " " + employee.lastName;
        nameTv.setText(fullName);

        // Sets the functionality for the buttons.
        initializeButtons(availabilities);
        setupToolbar();
    }

    /**
     * Sets up the top app bar, adding listeners for back and save actions.
     */
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBarEditAvailability);
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
     * Displays the saves button in the top bar menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_save, menu);
        return true;
    }

    /**
     * Initialize a single button with its color and OnClickListener.
     *
     * @param buttonId The buttonId to be initialized.
     */
    private void initializeDayShiftButton(int buttonId, boolean isAvailable) {
        Button button = findViewById(buttonId);
        String color = isAvailable ? "#00C49A" : "#706F6D";
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
        button.setOnClickListener(this::onDayShiftButtonClick);
    }

    /**
     * Initialize a set of buttons.
     */
    private void initializeButtons(List<Availability> availabilities) {
        // Initialize Day Shift Buttons
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

        // Initialize Other Buttons
        Button btnResetAvailability = findViewById(R.id.btnResetAvailability);
        btnResetAvailability.setOnClickListener(this::onResetClick);
    }

    /**
     * Handles the save button click, updating the availability in the database.
     */
    private void onSaveClick() {
        // Assuming you have the employeeId for whom you are editing the availability
        for (Map.Entry<String, Boolean> entry : availabilityMap.entrySet()) {
            String key = entry.getKey();
            Boolean isAvailable = entry.getValue();

            // Extract day and shift type from the key
            String dayAbbreviation = key.substring(0, 3);
            String shiftAbbreviation = key.substring(3);
            String day = getFullDayName(dayAbbreviation);
            String shiftType = getFullShiftTypeName(shiftAbbreviation);

            // Update the availability in the database
            db.availabilityDao().updateAvailability(employeeId, day, shiftType, isAvailable);

        }

        onBackPressed();
    }

    /**
     * Handles the cancel button click, reverting any unsaved changes.
     *
     * @param view The view that triggers this action.
     */
    private void onCancelClick(View view) {
        onBackPressed();
    }

    /**
     * Handles the reset button click, resetting the availability to its original state.
     */
    private void onResetClick(View view) {
        // Reset each button to unselected state
        int[] buttonIds = {
                R.id.btnSunFull, R.id.btnMonMor, R.id.btnMonAft, R.id.btnMonEve,
                R.id.btnTueMor, R.id.btnTueAft, R.id.btnTueEve, R.id.btnWedMor,
                R.id.btnWedAft, R.id.btnWedEve, R.id.btnThuMor, R.id.btnThuAft,
                R.id.btnThuEve, R.id.btnFriMor, R.id.btnFriAft, R.id.btnFriEve,
                R.id.btnSatFull
        };
        for (int buttonId : buttonIds) {
            Button button = findViewById(buttonId);
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#706F6D"))); // Gray
        }

        // Clear or reset the availability map
        availabilityMap.clear();

        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        String[] shifts = {"Full", "Mor", "Aft", "Eve"};
        for (String day : days) {
            for (String shift : shifts) {
                availabilityMap.put(day + shift, false);
            }
        }

    }

    /**
     * When the button is switched on to green, the function performs these actions.
     *
     * @param button    The button that was switched.
     * @param day       The day of the week it was pressed in. Format = Sun, Mon, Tue, Wed, Thu, Fri, Sat.
     * @param shiftType The shift type that was pressed. Format = Full, Mor, Aft, Eve.
     */
    private void whenSelected(Button button, String day, String shiftType) {
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00C49A"))); // Green
        availabilityMap.put(day + shiftType, true);
    }

    /**
     * When the button is switched off to gray, the function performs these actions.
     *
     * @param button    The button that was switched.
     * @param day       The day of the week it was pressed in. Format = Sun, Mon, Tue, Wed, Thu, Fri, Sat.
     * @param shiftType The shift type that was pressed. Format = Full, Mor, Aft, Eve.
     */
    private void whenUnselected(Button button, String day, String shiftType) {
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#706F6D"))); // Gray
        availabilityMap.put(day + shiftType, false);
    }

    /**
     * Determines what day and shift the button corresponds to and then determine if the
     * button is selected or unselected.
     *
     * @param view The view that triggers this action.
     */
    private void onDayShiftButtonClick(View view) {
        // Gets day and shift type from button Id.
        String resourceName = getResources().getResourceEntryName(view.getId());
        String day = resourceName.substring(3, 6);
        String shiftType = resourceName.substring(6);

        // Obtains current background tint of button
        ColorStateList currentTintList = (view).getBackgroundTintList();
        int currentColor = currentTintList != null ? currentTintList.getDefaultColor() : 0;

        // Determines if button is selected or unselected based on color
        if (currentColor == Color.parseColor("#706F6D")) { // If currently unselected
            whenSelected((Button) view, day, shiftType);
        } else { // If currently selected
            whenUnselected((Button) view, day, shiftType);
        }
    }

    /**
     * Checks if an employee is available for a specific day and shift type.
     *
     * @param availabilities List of availability objects for the employee.
     * @param day            The day of the week to check availability.
     * @param shiftType      The type of shift (e.g., MORNING, AFTERNOON, etc.).
     * @return true if the employee is available for the specified day and shift, false otherwise.
     */
    private boolean checkAvailability(List<Availability> availabilities, String day, String shiftType) {
        for (Availability availability : availabilities) {
            if (availability.dayOfWeek.toString().equalsIgnoreCase(day) && availability.shiftType.toString().equalsIgnoreCase(shiftType)) {
                return availability.isAvailable;
            }
        }
        return false;
    }

    /**
     * Converts a day abbreviation to its full name.
     *
     * @param day The abbreviation of the day (e.g., "MON" for Monday).
     * @return The full name of the day (e.g., "MONDAY").
     */
    private String getFullDayName(String day) {
        switch (day.toUpperCase()) {
            case "SUN":
                return "SUNDAY";
            case "MON":
                return "MONDAY";
            case "TUE":
                return "TUESDAY";
            case "WED":
                return "WEDNESDAY";
            case "THU":
                return "THURSDAY";
            case "FRI":
                return "FRIDAY";
            case "SAT":
                return "SATURDAY";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Converts a shift type abbreviation to its full name.
     *
     * @param shift The abbreviation of the shift type (e.g., "MOR" for Morning).
     * @return The full name of the shift type (e.g., "MORNING").
     */
    private String getFullShiftTypeName(String shift) {
        switch (shift.toUpperCase()) {
            case "MOR":
                return "MORNING";
            case "AFT":
                return "AFTERNOON";
            case "EVE":
                return "EVENING";
            case "FULL":
                return "FULL";
            default:
                return "UNKNOWN";
        }
    }
}


