package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Availability;
import com.example.myapplication.db.CalendarDay;
import com.example.myapplication.db.DayOff;
import com.example.myapplication.db.Employee;
import com.example.myapplication.db.ShiftInstance;
import com.example.myapplication.db.ShiftType;
import com.google.android.material.appbar.MaterialToolbar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CreateShift is a method which handles the fragment_create_shift functionality. The fragment
 * handles the creation of new shifts with employees that have been added to the system
 *
 * @author Cedric De Leon, Nicholas Westly, Sergei Borja
 * @since 2023.10.28
 */
public class CreateShift extends Fragment implements CreateShiftAdapter.OnEmployeeListener {
    private final List<Employee> assignedMorningEmp = new ArrayList<>();
    private final List<Employee> assignedAfternoonEmp = new ArrayList<>();
    private final List<Employee> assignedEveningEmp = new ArrayList<>();
    private final List<Employee> assignedFullEmp = new ArrayList<>();
    private RecyclerView fullShiftUnselected, fullShiftSelected,
            morningShiftUnselected, morningShiftSelected,
            afternoonShiftUnselected, afternoonShiftSelected,
            eveningShiftUnselected, eveningShiftSelected;
    private AppDatabase db;
    private TextView fullWarningTxt;
    private TextView morningWarningTxt;
    private TextView afternoonWarningTxt;
    private TextView eveningWarningTxt;
    private LocalDate selectedDate;
    private LinearLayout weekendShifts, weekdayShifts;
    private List<Employee> unassignedMorningEmp = new ArrayList<>();
    private List<Employee> unassignedAfternoonEmp = new ArrayList<>();
    private List<Employee> unassignedEveningEmp = new ArrayList<>();
    private List<Employee> unassignedFullEmp = new ArrayList<>();

    /**
     * Displays the visuals of the fragment when needed.
     *
     * @param inflater           Used to turn the layout design into a view.
     * @param container          The main view where the fragment should attach to, if present.
     * @param savedInstanceState Data from the last saved state, if there was one.
     * @return Returns the constructed layout for the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the selected date from arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("selectedDate")) {
            selectedDate = (LocalDate) args.getSerializable("selectedDate");
        }

        return inflater.inflate(R.layout.fragment_create_shift, container, false);
    }

    /**
     * Initializes the widgets and database after the view is created.
     * Sets up toolbar actions and fills the shift views based on the selected date.
     *
     * @param view               The created View.
     * @param savedInstanceState Saved instance state.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidgets(view);

        // Initialize database
        db = AppDatabase.getDbInstance(getActivity());

        // Toolbar setup
        MaterialToolbar toolbar = view.findViewById(R.id.topBarEditShifts);

        // Set up txtSave save data to data base and switch back to Calender fragment.
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.save) {
                onTxtSaveClick();
                return true;
            }
            return false;
        });

        // Set up txtCancel to navigate back to the Calender fragment.
        toolbar.setNavigationOnClickListener(this::onTxtCancelClick);

        // Set up the shifts based on the selected date
        setAssignedEmployees(selectedDate);

        if (CalendarUtils.isWeekend(selectedDate)) {
            weekendShifts.setVisibility(View.VISIBLE);
            weekdayShifts.setVisibility(View.GONE);

            setFullShiftView();
            updateFullShiftLayouts();
        } else {
            weekendShifts.setVisibility(View.GONE);
            weekdayShifts.setVisibility(View.VISIBLE);

            setMorningShiftView();
            updateMorningShiftLayouts();

            setAfternoonShiftView();
            updateAfternoonShiftLayouts();

            setEveningShiftView();
            updateEveningShiftLayouts();
        }
    }

    /**
     * Initializes the UI components.
     *
     * @param view The root view of the fragment.
     */
    private void initWidgets(View view) {
        TextView currentDayText = view.findViewById(R.id.txtCurrentDay);
        fullWarningTxt = view.findViewById(R.id.fullWarningTxt);
        morningWarningTxt = view.findViewById(R.id.morningWarningTxt);
        afternoonWarningTxt = view.findViewById(R.id.afternoonWarningTxt);
        eveningWarningTxt = view.findViewById(R.id.eveningWarningTxt);

        weekendShifts = view.findViewById(R.id.layoutWeekendShiftsContainer);
        weekdayShifts = view.findViewById(R.id.layoutWeekdayShiftsContainer);

        fullShiftUnselected = view.findViewById(R.id.FullShiftUnselected);
        fullShiftSelected = view.findViewById(R.id.FullShiftSelected);

        morningShiftUnselected = view.findViewById(R.id.MorningShiftUnselected);
        morningShiftSelected = view.findViewById(R.id.MorningShiftSelected);

        afternoonShiftUnselected = view.findViewById(R.id.AfternoonShiftUnselected);
        afternoonShiftSelected = view.findViewById(R.id.AfternoonShiftSelected);

        eveningShiftUnselected = view.findViewById(R.id.EveningShiftUnselected);
        eveningShiftSelected = view.findViewById(R.id.EveningShiftSelected);

        currentDayText.setText(CalendarUtils.monthDayYearFromDate(selectedDate));
    }

    /**
     * Sets the employees assigned to shifts for the selected date.
     *
     * @param date The date for which to set assigned employees.
     */
    private void setAssignedEmployees(LocalDate date) {
        CalendarDay day = db.shiftDao().getCalendarWorkDay(date);
        List<ShiftType> shiftTypes = db.shiftDao().getAllShiftTypeByDay(day.id);

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
                case "Morning":
                    assignedMorningEmp.clear();
                    assignedMorningEmp.addAll(employeeList);
                    break;
                case "Afternoon":
                    assignedAfternoonEmp.clear();
                    assignedAfternoonEmp.addAll(employeeList);
                    break;
                case "Evening":
                    assignedEveningEmp.clear();
                    assignedEveningEmp.addAll(employeeList);
                    break;
                case "Full":
                    assignedFullEmp.clear();
                    assignedFullEmp.addAll(employeeList);
                    break;
            }
        }
    }

    /**
     * Sets up the view for managing full shifts, including selecting and assigning employees.
     * Filters available employees based on their availability and existing assignments.
     */
    private void setFullShiftView() {
        // Retrieve all employees
        unassignedFullEmp = db.employeeDao().getAll();

        // Convert the selected date's day of the week to the corresponding availability
        Availability.DayOfWeek selectedDayOfWeek = convertToAvailabilityDayOfWeek(selectedDate.getDayOfWeek());

        // Retrieve availability and day off information for the selected day
        List<Availability> availabilities = db.availabilityDao().getAvailabilityByDay(selectedDayOfWeek);
        List<DayOff> daysOff = db.dayOffDao().getDaysOffByDate(selectedDate);

        // Filter employees to find those who are available and not on a day off for a full shift
        unassignedFullEmp = unassignedFullEmp.stream()
                .filter(emp -> availabilities.stream().anyMatch(avail ->
                        avail.getEmployeeId() == emp.id &&
                                avail.getShiftType() == Availability.ShiftType.FULL &&
                                avail.isAvailable()) &&
                        daysOff.stream().noneMatch(doff -> doff.getEmployeeId() == emp.id))
                .collect(Collectors.toList());

        // Remove already assigned employees from the unassigned list
        for (Employee assignedEmp : assignedFullEmp) {
            unassignedFullEmp.removeIf(e -> e.id == assignedEmp.id);
        }

        // Set up RecyclerView adapters for unassigned and assigned employee lists
        String shiftType = "Full";
        CreateShiftAdapter unassignedAdapter = new CreateShiftAdapter(unassignedFullEmp, this, shiftType, true, assignedFullEmp);
        fullShiftUnselected.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        fullShiftUnselected.setAdapter(unassignedAdapter);

        CreateShiftAdapter assignedAdapter = new CreateShiftAdapter(assignedFullEmp, this, shiftType, false, assignedFullEmp);
        fullShiftSelected.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        fullShiftSelected.setAdapter(assignedAdapter);
    }

    /**
     * Updates the layouts for full shifts, showing or hiding placeholders based on the number of assigned or available employees.
     */
    private void updateFullShiftLayouts() {
        if (unassignedFullEmp.isEmpty()) {
            // No employees available
            getView().findViewById(R.id.noFullEmployeesLayout).setVisibility(View.VISIBLE);
        } else {
            // There are employees available
            getView().findViewById(R.id.noFullEmployeesLayout).setVisibility(View.GONE);
        }

        if (assignedFullEmp.isEmpty()) {
            // No employees scheduled
            getView().findViewById(R.id.noFullShiftsLayout).setVisibility(View.VISIBLE);
        } else {
            // There are employees scheduled
            getView().findViewById(R.id.noFullShiftsLayout).setVisibility(View.GONE);
        }
    }

    /**
     * Sets up the view for managing morning shifts, including selecting and assigning employees.
     * Filters available employees based on their availability and existing assignments.
     */
    private void setMorningShiftView() {
        // Retrieve all employees
        unassignedMorningEmp = db.employeeDao().getAll();

        // Convert the selected date's day of the week to the corresponding availability
        Availability.DayOfWeek selectedDayOfWeek = convertToAvailabilityDayOfWeek(selectedDate.getDayOfWeek());

        // Retrieve availability and day off information for the selected day
        List<Availability> availabilities = db.availabilityDao().getAvailabilityByDay(selectedDayOfWeek);
        List<DayOff> daysOff = db.dayOffDao().getDaysOffByDate(selectedDate);

        // Filter employees to find those who are available and not on a day off for a morning shift
        unassignedMorningEmp = unassignedMorningEmp.stream()
                .filter(emp -> availabilities.stream().anyMatch(avail ->
                        avail.getEmployeeId() == emp.id &&
                                avail.getShiftType() == Availability.ShiftType.MORNING &&
                                avail.isAvailable()) &&
                        daysOff.stream().noneMatch(doff -> doff.getEmployeeId() == emp.id))
                .collect(Collectors.toList());

        // Remove already assigned employees from the unassigned list
        for (Employee assignedEmp : assignedMorningEmp) {
            unassignedMorningEmp.removeIf(e -> e.id == assignedEmp.id);
        }

        // Set up RecyclerView adapters for unassigned and assigned employee lists
        String shiftType = "Morning";
        CreateShiftAdapter unassignedAdapter = new CreateShiftAdapter(unassignedMorningEmp, this, shiftType, true, assignedMorningEmp);
        morningShiftUnselected.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        morningShiftUnselected.setAdapter(unassignedAdapter);

        CreateShiftAdapter assignedAdapter = new CreateShiftAdapter(assignedMorningEmp, this, shiftType, false, assignedMorningEmp);
        morningShiftSelected.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        morningShiftSelected.setAdapter(assignedAdapter);
    }

    /**
     * Updates the layouts for morning shifts, showing or hiding placeholders based on the number of assigned or available employees.
     */
    private void updateMorningShiftLayouts() {
        if (unassignedMorningEmp.isEmpty()) {
            // No employees available
            getView().findViewById(R.id.noMorningEmployeesLayout).setVisibility(View.VISIBLE);
        } else {
            // There are employees available
            getView().findViewById(R.id.noMorningEmployeesLayout).setVisibility(View.GONE);
        }

        if (assignedMorningEmp.isEmpty()) {
            // No employees scheduled
            getView().findViewById(R.id.noMorningShiftsLayout).setVisibility(View.VISIBLE);
        } else {
            // There are employees scheduled
            getView().findViewById(R.id.noMorningShiftsLayout).setVisibility(View.GONE);
        }
    }

    /**
     * Sets up the view for managing afternoon shifts, including selecting and assigning employees.
     * Filters available employees based on their availability and existing assignments.
     */
    private void setAfternoonShiftView() {
        // Retrieve all employees
        unassignedAfternoonEmp = db.employeeDao().getAll();

        // Convert the selected date's day of the week to the corresponding availability enum
        Availability.DayOfWeek selectedDayOfWeek = convertToAvailabilityDayOfWeek(selectedDate.getDayOfWeek());

        // Retrieve availability and day off information for the selected day
        List<Availability> availabilities = db.availabilityDao().getAvailabilityByDay(selectedDayOfWeek);
        List<DayOff> daysOff = db.dayOffDao().getDaysOffByDate(selectedDate);

        // Filter employees to find those who are available and not on a day off for a afternoon shift
        unassignedAfternoonEmp = unassignedAfternoonEmp.stream()
                .filter(emp -> availabilities.stream().anyMatch(avail ->
                        avail.getEmployeeId() == emp.id &&
                                avail.getShiftType() == Availability.ShiftType.AFTERNOON &&
                                avail.isAvailable()) &&
                        daysOff.stream().noneMatch(doff -> doff.getEmployeeId() == emp.id))
                .collect(Collectors.toList());

        // Remove already assigned employees from the unassigned list
        for (Employee assignedEmp : assignedAfternoonEmp) {
            unassignedAfternoonEmp.removeIf(e -> e.id == assignedEmp.id);
        }

        // Set up RecyclerView adapters for unassigned and assigned employee lists
        String shiftType = "Afternoon";
        CreateShiftAdapter unassignedAdapter = new CreateShiftAdapter(unassignedAfternoonEmp, this, shiftType, true, assignedAfternoonEmp);
        afternoonShiftUnselected.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        afternoonShiftUnselected.setAdapter(unassignedAdapter);

        CreateShiftAdapter assignedAdapter = new CreateShiftAdapter(assignedAfternoonEmp, this, shiftType, false, assignedAfternoonEmp);
        afternoonShiftSelected.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        afternoonShiftSelected.setAdapter(assignedAdapter);
    }

    /**
     * Updates the layouts for afternoon shifts, showing or hiding placeholders based on the number of assigned or available employees.
     */
    private void updateAfternoonShiftLayouts() {
        if (unassignedAfternoonEmp.isEmpty()) {
            // No employees available
            getView().findViewById(R.id.noAfternoonEmployeesLayout).setVisibility(View.VISIBLE);
        } else {
            // There are employees available
            getView().findViewById(R.id.noAfternoonEmployeesLayout).setVisibility(View.GONE);
        }

        if (assignedAfternoonEmp.isEmpty()) {
            // No employees scheduled
            getView().findViewById(R.id.noAfternoonShiftsLayout).setVisibility(View.VISIBLE);
        } else {
            // There are employees scheduled
            getView().findViewById(R.id.noAfternoonShiftsLayout).setVisibility(View.GONE);
        }
    }

    /**
     * Sets up the view for managing evening shifts, including selecting and assigning employees.
     * Filters available employees based on their availability and existing assignments.
     */
    private void setEveningShiftView() {
        // Retrieve all employees
        unassignedEveningEmp = db.employeeDao().getAll();

        // Convert the selected date's day of the week to the corresponding availability enum
        Availability.DayOfWeek selectedDayOfWeek = convertToAvailabilityDayOfWeek(selectedDate.getDayOfWeek());

        // Retrieve availability and day off information for the selected day
        List<Availability> availabilities = db.availabilityDao().getAvailabilityByDay(selectedDayOfWeek);
        List<DayOff> daysOff = db.dayOffDao().getDaysOffByDate(selectedDate);

        // Filter employees to find those who are available and not on a day off for a evening shift
        unassignedEveningEmp = unassignedEveningEmp.stream()
                .filter(emp -> availabilities.stream().anyMatch(avail ->
                        avail.getEmployeeId() == emp.id &&
                                avail.getShiftType() == Availability.ShiftType.EVENING &&
                                avail.isAvailable()) &&
                        daysOff.stream().noneMatch(doff -> doff.getEmployeeId() == emp.id))
                .collect(Collectors.toList());

        // Remove already assigned employees from the unassigned list
        for (Employee assignedEmp : assignedEveningEmp) {
            unassignedEveningEmp.removeIf(e -> e.id == assignedEmp.id);
        }

        // Set up RecyclerView adapters for unassigned and assigned employee lists
        String shiftType = "Evening";
        CreateShiftAdapter unassignedAdapter = new CreateShiftAdapter(unassignedEveningEmp, this, shiftType, true, assignedEveningEmp);
        eveningShiftUnselected.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        eveningShiftUnselected.setAdapter(unassignedAdapter);

        CreateShiftAdapter assignedAdapter = new CreateShiftAdapter(assignedEveningEmp, this, shiftType, false, assignedEveningEmp);
        eveningShiftSelected.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        eveningShiftSelected.setAdapter(assignedAdapter);
    }

    /**
     * Updates the layouts for evening shifts, showing or hiding placeholders based on the number of assigned or available employees.
     */
    private void updateEveningShiftLayouts() {
        if (unassignedEveningEmp.isEmpty()) {
            // No employees available
            getView().findViewById(R.id.noEveningEmployeesLayout).setVisibility(View.VISIBLE);
        } else {
            // There are employees available
            getView().findViewById(R.id.noEveningEmployeesLayout).setVisibility(View.GONE);
        }

        if (assignedEveningEmp.isEmpty()) {
            // No employees scheduled
            getView().findViewById(R.id.noEveningShiftsLayout).setVisibility(View.VISIBLE);
        } else {
            // There are employees scheduled
            getView().findViewById(R.id.noEveningShiftsLayout).setVisibility(View.GONE);
        }
    }

    /**
     * Converts a java.time.DayOfWeek to an Availability.DayOfWeek.
     *
     * @param dayOfWeek The java.time.DayOfWeek to convert.
     * @return The converted Availability.DayOfWeek.
     */
    private Availability.DayOfWeek convertToAvailabilityDayOfWeek(java.time.DayOfWeek dayOfWeek) {
        return Availability.DayOfWeek.valueOf(dayOfWeek.name());
    }

    /**
     * Handles the action when the save button is clicked.
     * Validates if the shift assignments meet the criteria before saving.
     */
    @SuppressLint("SetTextI18n")
    private void onTxtSaveClick() {
        boolean isValid = true;

        // Reset warning messages for each shift type
        fullWarningTxt.setVisibility(View.GONE);
        morningWarningTxt.setVisibility(View.GONE);
        afternoonWarningTxt.setVisibility(View.GONE);
        eveningWarningTxt.setVisibility(View.GONE);

        // Check if it's a weekday or weekend and validate the shifts accordingly
        if (CalendarUtils.isWeekend(selectedDate)) {
            // Check for enough employees and qualified employees in each shift
            if (!areEnoughEmployeesAssigned("Full", assignedFullEmp)) {
                fullWarningTxt.setText("Not enough employees assigned to the full shift");
                fullWarningTxt.setVisibility(View.VISIBLE);
                isValid = false;
            } else if (!hasQualifiedOpeningEmployee(assignedFullEmp) && !hasQualifiedClosingEmployee(assignedFullEmp)) {
                fullWarningTxt.setText("At least one qualified opening and closing employee is required for the full shift");
                fullWarningTxt.setVisibility(View.VISIBLE);
                isValid = false;
            }
        } else {
            // Check for enough employees and qualified employees in each shift
            if (!areEnoughEmployeesAssigned("Morning", assignedMorningEmp)) {
                morningWarningTxt.setText("Not enough employees assigned to the morning shift");
                morningWarningTxt.setVisibility(View.VISIBLE);
                isValid = false;
            } else if (!hasQualifiedOpeningEmployee(assignedMorningEmp)) {
                morningWarningTxt.setText("At least one qualified opening employee is required for the morning shift");
                morningWarningTxt.setVisibility(View.VISIBLE);
                isValid = false;
            }
            // Check for enough employees in each shift
            if (!areEnoughEmployeesAssigned("Afternoon", assignedAfternoonEmp)) {
                afternoonWarningTxt.setText("Not enough employees assigned to the afternoon shift");
                afternoonWarningTxt.setVisibility(View.VISIBLE);
                isValid = false;
            }

            // Check for enough employees and qualified employees in each shift
            if (!areEnoughEmployeesAssigned("Evening", assignedEveningEmp)) {
                eveningWarningTxt.setText("Not enough employees assigned to the evening shift");
                eveningWarningTxt.setVisibility(View.VISIBLE);
                isValid = false;
            } else if (!hasQualifiedClosingEmployee(assignedEveningEmp)) {
                eveningWarningTxt.setText("At least one qualified closing employee is required for the evening shift");
                eveningWarningTxt.setVisibility(View.VISIBLE);
                isValid = false;
            }
        }

        // Save data only if all conditions are met
        if (isValid) {
            if (assignEmployeeShift())
                switchToMonthViewFragment();

        }
    }

    /**
     * Assigns employees to their respective shifts and saves the shift instances in the database.
     *
     * @return True if all shifts are successfully assigned and saved, False otherwise.
     */
    private boolean assignEmployeeShift() {
        CalendarDay day = db.shiftDao().getCalendarWorkDay(selectedDate);
        if (day == null)
            return true;

        List<ShiftType> shiftTypes = db.shiftDao().getAllShiftTypeByDay(day.id);
        for (ShiftType st : shiftTypes) {
            if (!assignShiftInstance(st)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Assigns employees to a specific shift type and updates the database with the new shift instances.
     *
     * @param shiftType The ShiftType to which employees are being assigned.
     * @return True if the assignment is successful, False otherwise.
     */
    private boolean assignShiftInstance(ShiftType shiftType) {
        // Prepare lists for tracking shift assignments
        List<ShiftInstance> shiftInstances = new ArrayList<>();
        List<Employee> employeeList = new ArrayList<>();
        List<Employee> unassignEmployees = new ArrayList<>();
        List<ShiftInstance> removeShifts = new ArrayList<>();

        // Assign employees and handle qualifications based on shift type
        switch (shiftType.shiftTypeName) {
            case "Morning":
                employeeList = assignedMorningEmp;
                unassignEmployees = unassignedMorningEmp;
                if (!hasQualifiedOpeningEmployee(employeeList)) {
                    return false;
                }
                break;
            case "Afternoon":
                employeeList = assignedAfternoonEmp;
                unassignEmployees = unassignedAfternoonEmp;
                break;
            case "Evening":
                employeeList = assignedEveningEmp;
                if (!hasQualifiedClosingEmployee(employeeList)) {
                    return false;
                }
                unassignEmployees = unassignedEveningEmp;
                break;
            case "Full":
                employeeList = assignedFullEmp;
                if (!hasQualifiedOpeningEmployee(employeeList) && !hasQualifiedClosingEmployee(employeeList)) {
                    return false;
                }
                unassignEmployees = unassignedFullEmp;
                break;
        }

        // Check if there are enough employees for the shift
        if (employeeList.size() < shiftType.minShifts) {
            Log.d(null, "Not enough Employees assigned to shift: " + shiftType.shiftTypeName);
            return false;
        }

        // Create shift instances for assigned employees and remove instances for unassigned ones
        for (int i = 0; i < employeeList.size(); i++) {
            ShiftInstance si = new ShiftInstance(shiftType.id);
            Employee emp = employeeList.get(i);
            if (db.shiftDao().getShiftInstanceByEmpAndType(emp.id, shiftType.id) == null) {
                si.employeeId = emp.id;
                si.employeeDisplayName = emp.firstName + " " + emp.lastName.charAt(0) + ".";
                si.shiftType = shiftType.shiftTypeName;
                shiftInstances.add(si);
            }
        }
        for (int i = 0; i < unassignEmployees.size(); i++) {
            Employee emp = unassignEmployees.get(i);
            if (db.shiftDao().getShiftInstanceByEmpAndType(emp.id, shiftType.id) != null)
                removeShifts.add(db.shiftDao().getShiftInstanceByEmpAndType(emp.id, shiftType.id));
        }

        if (!shiftInstances.isEmpty()) {
            db.shiftDao().insertShiftInstances(shiftInstances);
        }
        if (removeShifts.size() > 0)
            db.shiftDao().deleteShiftInstances(removeShifts);

        return true;
    }

    /**
     * Checks if the assigned employees for a shift include at least one qualified opening employee.
     *
     * @param employeeList The list of employees assigned to the shift.
     * @return True if there is at least one qualified opening employee.
     */
    private boolean hasQualifiedOpeningEmployee(List<Employee> employeeList) {
        return employeeList.stream().anyMatch(emp -> "Qualified".equals(emp.isIs_qualified_opening()));
    }

    /**
     * Checks if the assigned employees for a shift include at least one qualified closing employee.
     *
     * @param employeeList The list of employees assigned to the shift.
     * @return True if there is at least one qualified closing employee.
     */
    private boolean hasQualifiedClosingEmployee(List<Employee> employeeList) {
        return employeeList.stream().anyMatch(emp -> "Qualified".equals(emp.isIs_qualified_closing()));
    }

    /**
     * Checks if the number of assigned employees for a shift meets the minimum requirement.
     *
     * @param shiftType         The type of the shift (e.g., "Morning").
     * @param assignedEmployees The list of employees assigned to the shift.
     * @return True if the number of assigned employees meets or exceeds the minimum requirement.
     */
    private boolean areEnoughEmployeesAssigned(String shiftType, List<Employee> assignedEmployees) {
        ShiftType shiftTypeInfo = db.shiftDao().getShiftTypeByName(shiftType);
        if (shiftTypeInfo != null) {
            return assignedEmployees.size() >= shiftTypeInfo.minShifts;
        }
        return false;
    }

    /**
     * Navigates back to the CalendarMonthView fragment when the cancel button is clicked.
     *
     * @param view The view that triggers this action.
     */
    private void onTxtCancelClick(View view) {
        switchToMonthViewFragment();
    }

    /**
     * Switches the current fragment to the CalendarMonthView.
     * This method is used to return to the calendar view after saving or canceling shift creation.
     */
    private void switchToMonthViewFragment() {
        FragmentActivity activity = getActivity();

        // Check if the fragment is currently associated with an activity.
        if (activity != null) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Replace the current fragment with the list view fragment.
            fragmentTransaction.replace(R.id.frameLayoutMain, new CalendarMonthView());
            fragmentTransaction.commit();
        }
    }

    /**
     * Handles the action when an employee is clicked in the shift adapter.
     * Moves the employee between the selected and unselected lists and refreshes the layouts.
     *
     * @param position   Position of the clicked employee.
     * @param shift      The shift type.
     * @param unassigned Indicates if the employee is currently unassigned.
     * @param employeeId The ID of the employee.
     */
    @Override
    public void onEmployeeClick(int position, String shift, boolean unassigned, int employeeId) {
        moveToSelected(position, shift, unassigned, employeeId);

        updateMorningShiftLayouts();
        updateAfternoonShiftLayouts();
        updateEveningShiftLayouts();
        updateFullShiftLayouts();
    }

    /**
     * Moves an employee between the selected and unselected lists based on the shift type and their current assignment status.
     * This method updates the RecyclerView adapters to reflect the changes in the employee lists.
     *
     * @param position   The position of the employee in the current list.
     * @param shift      The type of shift (Morning, Afternoon, Evening, Full).
     * @param unassigned Indicates if the employee is currently unassigned.
     * @param employeeId The unique ID of the employee.
     */
    private void moveToSelected(int position, String shift, boolean unassigned, int employeeId) {
        // Query the employee to be moved based on their ID
        Employee employee = db.employeeDao().getEmployeeById(employeeId);

        // Based on the shift type and whether the employee is currently unassigned, move them between lists
        switch (shift) {
            case "Morning":
                moveEmployeeBetweenLists(unassigned, position, employee, assignedMorningEmp, unassignedMorningEmp, morningShiftSelected, morningShiftUnselected);
                break;
            case "Afternoon":
                moveEmployeeBetweenLists(unassigned, position, employee, assignedAfternoonEmp, unassignedAfternoonEmp, afternoonShiftSelected, afternoonShiftUnselected);
                break;
            case "Evening":
                moveEmployeeBetweenLists(unassigned, position, employee, assignedEveningEmp, unassignedEveningEmp, eveningShiftSelected, eveningShiftUnselected);
                break;
            case "Full":
                moveEmployeeBetweenLists(unassigned, position, employee, assignedFullEmp, unassignedFullEmp, fullShiftSelected, fullShiftUnselected);
                break;
        }
    }

    /**
     * Helper method to move an employee between assigned and unassigned lists and update the corresponding RecyclerViews.
     *
     * @param unassigned             Indicates if the employee is currently unassigned.
     * @param position               The position of the employee in the current list.
     * @param employee               The employee to be moved.
     * @param assignedList           The list of assigned employees.
     * @param unassignedList         The list of unassigned employees.
     * @param selectedRecyclerView   The RecyclerView for selected employees.
     * @param unselectedRecyclerView The RecyclerView for unselected employees.
     */
    private void moveEmployeeBetweenLists(boolean unassigned, int position, Employee employee,
                                          List<Employee> assignedList, List<Employee> unassignedList,
                                          RecyclerView selectedRecyclerView, RecyclerView unselectedRecyclerView) {
        if (unassigned) {
            // Move employee from unassigned to assigned list
            assignedList.add(0, employee);
            selectedRecyclerView.getAdapter().notifyItemInserted(0);

            unassignedList.remove(position);
            unselectedRecyclerView.getAdapter().notifyItemRemoved(position);
        } else {
            // Move employee from assigned to unassigned list
            unassignedList.add(0, employee);
            unselectedRecyclerView.getAdapter().notifyItemInserted(0);

            assignedList.remove(position);
            selectedRecyclerView.getAdapter().notifyItemRemoved(position);
        }
    }

}
