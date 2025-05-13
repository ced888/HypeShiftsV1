package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.CalendarDay;
import com.example.myapplication.db.Employee;
import com.example.myapplication.db.ShiftInstance;
import com.example.myapplication.db.ShiftType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * HomePage is a fragment that displays the main hub for the user.
 * It contains buttons that goes to important features of the app.
 *
 * @author Cedric De Leon, Keegan Vanstone, Sergei Borja
 * @since 2023.10.10
 */
public class HomePage extends Fragment {
    Button btnChangePassword, btnLogout;
    private RecyclerView shiftRecyclerView;
    private AppDatabase db;
    private TextView noShiftTxtHomePage;
    private Employee employee;

    /**
     * Displays the visuals of the fragment when needed.
     *
     * @param inflater           Used to turn the layout design into a view.
     * @param container          The main view where the fragment should attach to, if present.
     * @param savedInstanceState Data from the last saved state, if there was one.
     * @return Returns the constructed layout for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getDbInstance(requireContext());

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("userID")) {
            int userId = arguments.getInt("userID");
            if (userId != -1) {
                employee = db.employeeDao().getEmployeeById((userId));
            }
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    /**
     * Initialization after the view is created.
     *
     * @param view               The View returned by onCreateView.
     * @param savedInstanceState A Bundle that provides data about the previous instance of the fragment.
     */
    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        updateDateViews(view);

        // Initialize your RecyclerView
        shiftRecyclerView = view.findViewById(R.id.currentShiftsHomePage);

        TextView shiftsIndicatorTxt = view.findViewById(R.id.shiftsIndicatorTxt);
        noShiftTxtHomePage = view.findViewById(R.id.noShiftTxtHomePage);
        TextView txtWelcomeUserName = view.findViewById(R.id.txtWelcomeUserName);

        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Determine the day of the week and the current hour
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Determine the shift type based on day and time
        String shiftType = timeOfDay(dayOfWeek, hour);

        // Set the RecyclerView based on the determined shift type
        setAssignedEmployees(shiftType);

        // Update the text to indicate the current shifts
        shiftsIndicatorTxt.setText(shiftType + " Shifts:");

        if (employee != null) {
            txtWelcomeUserName.setText(employee.firstName + " " + employee.lastName);
        }

        btnChangePassword.setOnClickListener(this::onChangePasswordClick);
        btnLogout.setOnClickListener(this::onLogoutClick);
    }

    /**
     * Determines the type of shift based on the day of the week and the current hour.
     *
     * @param dayOfWeek The day of the week.
     * @param hour      The current hour of the day.
     * @return The shift type based on the day and time.
     */
    private String timeOfDay(int dayOfWeek, int hour) {
        boolean isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;

        if (isWeekend)
            return "Full";
        else if (hour < 12) {
            return "Morning";
        } else if (hour < 16) {
            return "Afternoon";
        } else if (hour < 20) {
            return "Evening";
        }
        return null;
    }


    /**
     * Updates the specified view with the current date and day of the week.
     * The date is formatted based on the default locale of the device.
     *
     * @param view The view containing the TextViews to be updated.
     */
    public void updateDateViews(View view) {
        // Initialize a calendar instance to get the current date and time in the local format.
        Calendar calendar = Calendar.getInstance();
        Locale currentLocale = Locale.getDefault();

        // Set up the current date string
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, currentLocale);
        String currentDate = dateFormat.format(calendar.getTime());

        // Set up the current day string
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", currentLocale);
        String currentDay = dayFormat.format(calendar.getTime());

        // Update the found TextViews with the formatted date and day
        TextView textViewDate = view.findViewById(R.id.txtDateHomePage);
        TextView textViewDay = view.findViewById(R.id.txtDayHomePage);

        textViewDate.setText(currentDate);
        textViewDay.setText(currentDay);
    }

    /**
     * Sets up the RecyclerView with employees assigned to the current shift.
     *
     * @param shiftType The type of shift to display.
     */
    private void setAssignedEmployees(String shiftType) {

        // Fetch shift types from the database and populate the employee list
        CalendarDay day = db.shiftDao().getCalendarWorkDay(LocalDate.now());
        List<ShiftType> shiftTypes = new ArrayList<>();
        if (day != null)
            shiftTypes = db.shiftDao().getAllShiftTypeByDay(day.id);

        List<Employee> employeeList = new ArrayList<>();

        for (ShiftType st : shiftTypes) {
            if (st.shiftTypeName.equals(shiftType)) {
                List<ShiftInstance> shiftInstances = db.shiftDao().getAllShiftInstanceByShiftType(st.id);
                for (ShiftInstance si : shiftInstances) {
                    if (si.shiftType.equals(st.shiftTypeName)) {
                        employeeList.add(db.employeeDao().getEmployeeById(si.employeeId));
                    }
                }
            }
        }

        // Determine the visibility of RecyclerView and No Shift TextView based on the employee list
        if (employeeList.isEmpty()) {
            shiftRecyclerView.setVisibility(View.GONE);
            noShiftTxtHomePage.setVisibility(View.VISIBLE);
        } else {
            noShiftTxtHomePage.setVisibility(View.GONE);
            shiftRecyclerView.setVisibility(View.VISIBLE);

            // Set up the RecyclerView with the selected list
            HomePageShiftAdapter adapter = new HomePageShiftAdapter(employeeList, shiftType);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            shiftRecyclerView.setAdapter(adapter);
            shiftRecyclerView.setLayoutManager(layoutManager);
        }
    }

    /**
     * Handles the click event for changing the password.
     *
     * @param view The view that was clicked.
     */
    private void onChangePasswordClick(View view) {
        if (employee != null) {
            // Create an Intent to start the ChangePassword activity
            Intent intent = new Intent(getContext(), ChangePassword.class);

            // Add the employee ID as an extra. Replace "EXTRA_EMPLOYEE_ID" with your key
            intent.putExtra("employeeID", employee.id);

            // Start the ChangePassword activity
            startActivity(intent);
        } else {
            // Optionally handle the case where employee is null
            Toast.makeText(getContext(), "Employee data not available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the click event for logging out.
     *
     * @param view The view that was clicked.
     */
    private void onLogoutClick(View view) {
        getParentFragmentManager().setFragmentResult("Logout", new Bundle());
    }


}