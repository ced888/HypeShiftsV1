package com.example.myapplication;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.db.Employee;

import java.util.List;

/**
 * Adapter Method for CreateShift, handles the recycler views;MorningShiftView,AfternoonShiftView,
 * EveningShiftView, on the fragment_create_shift using the employee_shift_cell as the list element
 *
 * @author Cedric De Leon, Nicholas Westly, Sergei Borja
 * @since 2023.10.28
 */
public class CreateShiftAdapter extends RecyclerView.Adapter<CreateShiftViewHolder> {
    private final List<Employee> Employees;
    private final List<Employee> assignedEmployees;
    private final CreateShiftAdapter.OnEmployeeListener onEmployeeListener;
    private final String Shift;
    private final boolean unassigned;

    /**
     * Constructor for CreateShiftAdapter.
     * Initializes the adapter with employee lists, listener, shift type, and assignment status.
     *
     * @param Employees          List of employees.
     * @param onEmployeeListener Listener for handling click events on employee items.
     * @param shift              Type of shift.
     * @param unassigned         Indicates if the employees are currently unassigned.
     * @param assignedEmployees  List of already assigned employees.
     */
    public CreateShiftAdapter(List<Employee> Employees, OnEmployeeListener onEmployeeListener, String shift, boolean unassigned, List<Employee> assignedEmployees) {
        this.Employees = Employees;
        this.onEmployeeListener = onEmployeeListener;
        this.Shift = shift;
        this.unassigned = unassigned;
        this.assignedEmployees = assignedEmployees;
    }

    /**
     * Creates a new ViewHolder for employee shift cells.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new instance of CreateShiftViewHolder.
     */
    @NonNull
    @Override
    public CreateShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.employee_shift_cell, parent, false);
        return new CreateShiftViewHolder(view, onEmployeeListener, Shift);
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     *
     * @param holder   The ViewHolder which should be updated.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull CreateShiftViewHolder holder, int position) {
        holder.txtEmployeeName.setText(createName(position));
        holder.employeeId = Employees.get(position).id;
        holder.unassigned = this.unassigned;

        // Set visibility and content of trainingPlaceHolder based on employee's role and training status
        updateTrainingPlaceHolder(holder, position);

        // Check if the employee is selected and update the background and visibility accordingly
        boolean isSelected = assignedEmployees.contains(Employees.get(position));
        holder.updateBackgroundAndVisibility(isSelected, holder.txtRemove);
    }

    /**
     * Updates the trainingPlaceHolder in the ViewHolder based on the employee's role and training status.
     *
     * @param holder   The ViewHolder to update.
     * @param position The position of the employee in the list.
     */
    private void updateTrainingPlaceHolder(@NonNull CreateShiftViewHolder holder, int position) {
        boolean isTraining = Employees.get(position).isIs_training();
        String role = Employees.get(position).role;

        if (isTraining) {
            holder.trainingPlaceHolder.setVisibility(View.VISIBLE);
            holder.trainingPlaceHolder.setText(role.equals("Manager") ? "Training Manager" : "Training");
        } else {
            holder.trainingPlaceHolder.setVisibility(role.equals("Manager") || role.equals("Owner") ? View.VISIBLE : View.GONE);
            holder.trainingPlaceHolder.setText(role);
        }
    }

    /**
     * Generates a formatted name for an employee.
     *
     * @param position The position of the employee in the list.
     * @return A formatted String representing the employee's name.
     */
    private String createName(int position) {
        String name = Employees.get(position).firstName;
        String lastInitial = Employees.get(position).lastName;
        return name + " " + lastInitial.charAt(0) + '.';
    }

    @Override
    public int getItemCount() {
        return Employees.size();
    }

    /**
     * Interface for handling click events on individual Employee Shift Cells.
     */
    public interface OnEmployeeListener {
        void onEmployeeClick(int position, String Shift, boolean unassigned, int employeeId);
    }
}

