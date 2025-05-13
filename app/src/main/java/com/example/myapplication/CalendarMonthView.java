package com.example.myapplication;

import static com.example.myapplication.CalendarUtils.daysInMonthArray;
import static com.example.myapplication.CalendarUtils.selectedDate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.CalendarDay;
import com.example.myapplication.db.Employee;
import com.example.myapplication.db.ShiftInstance;
import com.example.myapplication.db.ShiftType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * CalendarMonthView is a fragment that displays a calendar with the ability to
 * navigate between months and switch to a list view representation.
 *
 * @author Cedric De Leon, Nicholas Westly, Sergei Borja
 * @since 2023.10.15
 */
public class CalendarMonthView extends Fragment implements CalendarAdapter.OnItemListener {
    private final List<Employee>
            assignedFullEmp = new ArrayList<>();
    private final List<Employee> assignedMorningEmp = new ArrayList<>();
    private final List<Employee> assignedAfternoonEmp = new ArrayList<>();
    private final List<Employee> assignedEveningEmp = new ArrayList<>();
    private TextView monthYearText, currentDayText;
    private RecyclerView calendarRecyclerView, fullShiftList, morningShiftList, afternoonShiftList, eveningShiftList;
    private LinearLayout weekendShifts;
    private LinearLayout weekdayShifts;
    private AppDatabase db;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    /**
     * Initialization after the view is created.
     *
     * @param view               The View returned by onCreateView.
     * @param savedInstanceState A Bundle that provides data about the previous instance of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the database and current date
        db = AppDatabase.getDbInstance(requireContext());
        CalendarUtils.selectedDate = LocalDate.now();

        // Sets up the UI components
        initializeUI(view);
        setMonthView();


    }

    /**
     * Initializes the UI components of the fragment.
     *
     * @param view The root view of the fragment.
     */
    private void initializeUI(View view) {
        // Set up Buttons
        ImageView previousButton = view.findViewById(R.id.btnPreviousMonth);
        ImageView nextButton = view.findViewById(R.id.btnNextMonth);
        LinearLayout viewButton = view.findViewById(R.id.btnView);
        CardView createShiftButton = view.findViewById(R.id.btnAddShifts);

        // Set up Weekend or Weekday Containers
        weekendShifts = view.findViewById(R.id.layoutWeekendShiftsContainer);
        weekdayShifts = view.findViewById(R.id.layoutWeekdayShiftsContainer);

        // Set up shift lists
        fullShiftList = view.findViewById(R.id.fullShiftList);
        morningShiftList = view.findViewById(R.id.morningShiftList);
        afternoonShiftList = view.findViewById(R.id.afternoonShiftList);
        eveningShiftList = view.findViewById(R.id.eveningShiftList);

        // Set up calendar
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.txtMonthYearTextView);
        currentDayText = view.findViewById(R.id.txtCurrentDay);

        // Set up navigation and action buttons
        previousButton.setOnClickListener(this::previousMonthAction);
        nextButton.setOnClickListener(this::nextMonthAction);
        viewButton.setOnClickListener(this::switchToListViewFragment);
        createShiftButton.setOnClickListener(this::onAddShiftsClick);
    }

    /**
     * Sets up the month view in the calendar. This involves displaying the months and any assigned shifts.
     */
    private void setMonthView() {
        // Display the month and year of the selected date.
        monthYearText.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));

        // Get the list of days for the selected month.
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);

        // Create and set the adapter to populate the RecyclerView.
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext().getApplicationContext(), 7);

        // Apply the layout and adapter to the RecyclerView.
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        // Sets the date below the calendar
        currentDayText.setText(CalendarUtils.monthDayYearFromDate(CalendarUtils.selectedDate));

        // Decide which shift layouts to show based on the day of the week
        if (CalendarUtils.isWeekend(selectedDate)) {
            weekendShifts.setVisibility(View.VISIBLE);
            weekdayShifts.setVisibility(View.GONE);
        } else {
            weekendShifts.setVisibility(View.GONE);
            weekdayShifts.setVisibility(View.VISIBLE);
        }

        if (!db.employeeDao().getAll().isEmpty())
            setAssignedEmployees(selectedDate);
    }

    /**
     * /**
     * Handles the action to navigate to the previous month in the calendar.
     *
     * @param view The view that triggered this action.
     */
    public void previousMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    /**
     * Handles the action to navigate to the next month in the calendar.
     *
     * @param view The view that triggered this action.
     */
    public void nextMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    /**
     * Switch to the list view of the calendar.
     *
     * @param view The view (button) that triggers this action.
     */
    public void switchToListViewFragment(View view) {
        FragmentActivity activity = getActivity();

        // Check if the fragment is currently associated with an activity.
        if (activity != null) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Replace the current fragment with the list view fragment.
            fragmentTransaction.replace(R.id.frameLayoutMain, new CalendarListView());
            fragmentTransaction.commit();
        }
    }

    /**
     * Handles the selection of a calendar day.
     *
     * @param position The position of the clicked day in the calendar grid.
     * @param date     The actual date of the clicked day.
     */
    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null) {
            // Update the selected date and refresh the view.
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    /**
     * Sets the employees assigned to shifts for the selected date.
     *
     * @param date The date for which to set assigned employees.
     */
    private void setAssignedEmployees(LocalDate date) {
        CalendarDay day = db.shiftDao().getCalendarWorkDay(date);
        List<ShiftType> shiftTypes = new ArrayList<>();
        if (day != null)
            shiftTypes = db.shiftDao().getAllShiftTypeByDay(day.id);

        // Clears the list of assigned employees for all the shift types
        assignedFullEmp.clear();
        assignedMorningEmp.clear();
        assignedAfternoonEmp.clear();
        assignedEveningEmp.clear();

        // Filters employees to the different assigned shift lists based on time of day they are working
        for (ShiftType st : shiftTypes) {
            List<ShiftInstance> shiftInstances = db.shiftDao().getAllShiftInstanceByShiftType(st.id);
            List<Employee> employeeList = new ArrayList<>();

            for (ShiftInstance si : shiftInstances) {
                if (si.shiftType.equals(st.shiftTypeName)) {
                    employeeList.add(db.employeeDao().getEmployeeById(si.employeeId));
                }
            }

            switch (st.shiftTypeName) {
                case "Full":
                    assignedFullEmp.addAll(employeeList);
                    break;
                case "Morning":
                    assignedMorningEmp.addAll(employeeList);
                    break;
                case "Afternoon":
                    assignedAfternoonEmp.addAll(employeeList);
                    break;
                case "Evening":
                    assignedEveningEmp.addAll(employeeList);
                    break;
            }
        }

        // Update the RecyclerViews with the assigned employees
        updateRecyclerView(fullShiftList, assignedFullEmp, "Full");
        updateRecyclerView(morningShiftList, assignedMorningEmp, "Morning");
        updateRecyclerView(afternoonShiftList, assignedAfternoonEmp, "Afternoon");
        updateRecyclerView(eveningShiftList, assignedEveningEmp, "Evening");
    }

    /**
     * Updates the display of a RecyclerView with the assigned employees for a shift.
     *
     * @param recyclerView The RecyclerView to update.
     * @param employees    The list of employees assigned to a shift.
     * @param shiftType    The type of shift (e.g., "Morning", "Afternoon").
     */
    private void updateRecyclerView(RecyclerView recyclerView, List<Employee> employees, String shiftType) {
        TextView noShiftsText = getNoShiftsTextView(shiftType);

        // Show or hide the 'no shifts' message based on whether there are assigned employees
        if (employees.isEmpty() || employees.stream().allMatch(Objects::isNull)) {
            recyclerView.setVisibility(View.GONE);
            if (noShiftsText != null) {
                noShiftsText.setVisibility(View.VISIBLE);
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            if (noShiftsText != null) {
                noShiftsText.setVisibility(View.GONE);
            }
            // Displays the recycler view for the shift
            CalendarShiftAdapter adapter = new CalendarShiftAdapter(employees, shiftType, false);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext().getApplicationContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    /**
     * Returns the TextView corresponding to the 'no shifts' message for a given shift type.
     *
     * @param shiftType The type of shift.
     * @return The TextView that shows the 'no shifts' message.
     */
    private TextView getNoShiftsTextView(String shiftType) {
        switch (shiftType) {
            case "Full":
                return getView().findViewById(R.id.noFullShiftTxt);
            case "Morning":
                return getView().findViewById(R.id.noMorningShiftTxt);
            case "Afternoon":
                return getView().findViewById(R.id.noAfternoonShiftTxt);
            case "Evening":
                return getView().findViewById(R.id.noEveningShiftTxt);
            default:
                return null;
        }
    }

    /**
     * Populates empty (unassigned) shift instances for a selected date.
     *
     * @param selectedDate The date for which to create empty shifts.
     */
    private void createEmptyShifts(LocalDate selectedDate) {
        String date = selectedDate.toString();
        LocalDate test_day = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);

        //Add day
        CalendarDay workDay = new CalendarDay(test_day);
        int dayId = (int) db.shiftDao().insertDay(workDay);

        //Add Shift Types with unassigned Shift Instances
        //Weekday, from Monday to Friday
        if (selectedDate.getDayOfWeek().getValue() <= 5) {
            insertEmptyShiftsByType(dayId, date, "Morning", true, false, 2);
            insertEmptyShiftsByType(dayId, date, "Afternoon", false, false, 2);
            insertEmptyShiftsByType(dayId, date, "Evening", false, true, 2);
        }
        //Weekend, Saturday & Sunday
        else {
            insertEmptyShiftsByType(dayId, date, "Full", true, true, 3);
        }
        Toast.makeText(requireContext(), "Empty shifts added", Toast.LENGTH_SHORT).show();
    }

    /**
     * Inserts empty shifts by type (Morning, Afternoon, etc.) for a specific day.
     *
     * @param dayId         The ID of the day.
     * @param date          The date string.
     * @param shiftTypeName The name of the shift type.
     * @param isOpening     Indicates if the shift is an opening shift.
     * @param isClosing     Indicates if the shift is a closing shift.
     * @param minShifts     The minimum number of shifts required.
     */
    private void insertEmptyShiftsByType(int dayId, String date, String shiftTypeName, boolean isOpening, boolean isClosing, int minShifts) {
        String startTime = "";
        String endTime = "";
        switch (shiftTypeName) {
            case "Morning":
                startTime = "08:00:00";
                endTime = "12:00:00";
                break;
            case "Afternoon":
                startTime = "12:00:00";
                endTime = "16:00:00";
                break;
            case "Evening":
                startTime = "16:00:00";
                endTime = "20:00:00";
                break;
            case "Full":
                startTime = "10:00:00";
                endTime = "18:00:00";
                break;
        }
        LocalDateTime startDateTime = LocalDateTime.parse(date + "T" + startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endDateTime = LocalDateTime.parse(date + "T" + endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ShiftType shiftType = new ShiftType(dayId, shiftTypeName, startDateTime, endDateTime, isOpening, isClosing, minShifts);
        int shiftTypeId = (int) db.shiftDao().insertShiftType(shiftType);
    }

    /**
     * Switches to the Create Shift View fragment.
     *
     * @param view The view that triggered this action (usually a button).
     */
    public void onAddShiftsClick(View view) {
        FragmentActivity activity = getActivity();
        // Creates empty shifts to be assigned by create shift adapter
        if (db.shiftDao().getCalendarWorkDay(selectedDate) == null)
            createEmptyShifts(selectedDate);
        // Check if the fragment is currently associated with an activity.
        if (activity != null) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Pass the selectedDate to the next fragment as an argument
            CreateShift newFragment = new CreateShift(); // Corrected class name
            Bundle args = new Bundle();
            args.putSerializable("selectedDate", selectedDate);
            newFragment.setArguments(args);

            // Replace the current fragment with the new fragment
            fragmentTransaction.replace(R.id.frameLayoutMain, newFragment);
            fragmentTransaction.commit();
        }
    }
}