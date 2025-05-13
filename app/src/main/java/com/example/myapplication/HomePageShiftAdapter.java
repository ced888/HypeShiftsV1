package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.db.Employee;

import java.util.List;

/**
 * Adapter for displaying employees' shifts on the HomePage fragment.
 * It manages a list of employees and shows their names and roles for a specific shift.
 *
 * @author Sergei Borja
 * @since 2023.10.10
 */
public class HomePageShiftAdapter extends RecyclerView.Adapter<HomePageShiftAdapter.EmployeeViewHolder> {
    // List of employees to be displayed
    private final List<Employee> employees;

    /**
     * Constructor for HomePageShiftAdapter.
     *
     * @param employees List of Employee objects to be displayed.
     * @param ignoredShiftType The type of shift (e.g., Morning, Afternoon, Evening, Full).
     */
    public HomePageShiftAdapter(List<Employee> employees, String ignoredShiftType) {
        this.employees = employees;
        // The type of shift for which these employees are listed
    }

    /**
     * Creates a new ViewHolder for employee items.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new EmployeeViewHolder that holds a View of the employee_item_layout.
     */
    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_item_layout, parent, false);
        return new EmployeeViewHolder(view);
    }

    /**
     * Binds the employee data to the ViewHolder.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employees.get(position);
        holder.bind(employee);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The size of the employees list.
     */
    @Override
    public int getItemCount() {
        return employees.size();
    }

    /**
     * ViewHolder class for employee items.
     * It holds the layout for each employee item in the RecyclerView.
     */
    class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView trainingPlaceHolder;

        EmployeeViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.txtEmployeeName);
            trainingPlaceHolder = itemView.findViewById(R.id.trainingPlaceHolder);
        }

        /**
         * Binds an Employee object to the ViewHolder.
         *
         * @param employee The Employee object to be bound to the ViewHolder.
         */
        void bind(Employee employee) {
            // Format the display name as 'Firstname LastInitial.'
            String displayName = employee.getFirstName() + " " + employee.getLastName().charAt(0) + '.';
            textViewName.setText(displayName);

            // Display role and training status
            boolean isTraining = employee.isIs_training();
            String role = employee.role;

            // Set visibility and text of trainingPlaceHolder based on employee's role and training status
            if (isTraining) {
                trainingPlaceHolder.setVisibility(View.VISIBLE);
                trainingPlaceHolder.setText(role.equals("Manager") ? "Training Manager" : "Training");
            } else {
                trainingPlaceHolder.setVisibility(role.equals("Manager") || role.equals("Owner") ? View.VISIBLE : View.GONE);
                trainingPlaceHolder.setText(role);
            }
        }
    }
}
