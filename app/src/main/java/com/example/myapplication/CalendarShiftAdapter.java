package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.db.Employee;

import java.util.List;

/**
 * Adapter for a RecyclerView to display employees assigned to different shifts.
 * It handles the layout and data binding for each shift cell.
 *
 * @author Sergei Borja
 * @since 2023.12.02
 */
public class CalendarShiftAdapter extends RecyclerView.Adapter<CalendarShiftViewHolder> {
    public final String Shift; // The shift type (e.g., "Morning", "Afternoon")
    public final boolean unassigned; // Flag to indicate if the shift is unassigned
    private final List<Employee> Employees; // List of employees to be displayed

    /**
     * Constructor for CalendarShiftAdapter.
     *
     * @param Employees  List of employees to be displayed in the RecyclerView.
     * @param shift      The shift type associated with these employees.
     * @param unassigned Indicates if the shift is unassigned.
     */
    public CalendarShiftAdapter(List<Employee> Employees, String shift, boolean unassigned) {
        this.Employees = Employees;
        this.Shift = shift;
        this.unassigned = unassigned;
    }

    /**
     * Creates a new ViewHolder for employee shift cells.
     * This method inflates the layout from XML and initializes the ViewHolder.
     *
     * @param parent   The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new View.
     * @return A new instance of CalendarShiftViewHolder.
     */
    @NonNull
    @Override
    public CalendarShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.employee_shift_cell, parent, false);

        // Create and return a new ViewHolder for the inflated layout
        return new CalendarShiftViewHolder(view, Shift);
    }

    /**
     * Binds employee data to the ViewHolder at the specified position.
     * Sets the employee's name, ID, and training status in the shift cell.
     *
     * @param holder   The ViewHolder to be updated.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull CalendarShiftViewHolder holder, int position) {
        // Bind data to the ViewHolder
        holder.txtEmployeeName.setText(createName(position));
        holder.employeeId = Employees.get(position).id;
        holder.unassigned = this.unassigned;

        // Determine if the employee is in training and set visibility of training placeholders
        boolean isTraining = Employees.get(position).isIs_training();
        String role = Employees.get(position).role;
        setupRoleAndTraining(holder, isTraining, role);
    }

    /**
     * Creates a formatted name string for an employee.
     *
     * @param position The position of the employee in the list.
     * @return A string combining the first name and the initial of the last name.
     */
    private String createName(int position) {
        String name = Employees.get(position).firstName;
        String lastInitial = Employees.get(position).lastName;
        return name + " " + lastInitial.charAt(0) + '.';
    }

    /**
     * Sets up the role and training placeholders in the ViewHolder.
     *
     * @param holder     The ViewHolder for the employee.
     * @param isTraining Indicates if the employee is in training.
     * @param role       The role of the employee.
     */
    private void setupRoleAndTraining(CalendarShiftViewHolder holder, boolean isTraining, String role) {
        if (isTraining) {
            holder.trainingPlaceHolder.setVisibility(View.VISIBLE);
            holder.trainingPlaceHolder.setText(role.equals("Manager") ? "Training Manager" : "Training");
        } else {
            holder.trainingPlaceHolder.setVisibility(role.equals("Manager") || role.equals("Owner") ? View.VISIBLE : View.GONE);
            holder.trainingPlaceHolder.setText(role);
        }
    }

    /**
     * Returns the total count of employees in the adapter.
     *
     * @return The size of the 'days' array.
     */
    @Override
    public int getItemCount() {
        return Employees.size();
    }
}
