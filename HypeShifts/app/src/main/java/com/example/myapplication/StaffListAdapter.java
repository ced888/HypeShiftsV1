package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.db.Employee;

import java.util.List;

/**
 * Adapter for a ListView widget to display a list of staff members.
 * Each staff member is represented with their name and training status.
 *
 * @author Cedric De Leon, Osman Osman, Sergei Borja
 */
public class StaffListAdapter extends BaseAdapter {
    private List<Employee> employees;
    private final Context context;

    /**
     * Constructor for StaffListAdapter.
     *
     * @param context      Context in which the adapter is operating.
     * @param employeeList List of Employee objects to be displayed in the ListView.
     */
    public StaffListAdapter(Context context, List<Employee> employeeList) {
        this.employees = employeeList;
        this.context = context;
    }

    /**
     * Updates the employee list and refreshes the ListView.
     *
     * @param updatedList Updated list of Employee objects.
     */
    public void updateEmployeeList(List<Employee> updatedList) {
        this.employees = updatedList;
        notifyDataSetChanged(); // Notify the adapter to refresh the view
    }

    @Override
    public int getCount() {
        // Return the size of the employee list
        return employees.size();
    }

    @Override
    public Object getItem(int position) {
        // Returns the employee object at the specified position
        return employees.get(position);
    }

    @Override
    public long getItemId(int position) {
        // Return the ID of the item at the specified position
        return employees.get(position).id;
    }

    /**
     * Creates or updates the view for each item in the ListView.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.employee_row, null);

        TextView nameTv, trainingPlaceHolder;
        nameTv = row.findViewById(R.id.nameTv);
        trainingPlaceHolder = row.findViewById(R.id.trainingPlaceHolder);

        Employee employee = employees.get(position);
        String fullName = employee.firstName + " " + employee.lastName;
        nameTv.setText(fullName);

        // Display the employee's training status or role
        setupTrainingPlaceholder(trainingPlaceHolder, employee);

        // Set onClick listener for the row
        row.setOnClickListener(v -> launchEmployeeProfile(employee));

        return row;
    }

    /**
     * Sets up the training placeholder view based on the employee's training status and role.
     *
     * @param trainingPlaceHolder TextView for displaying the training status or role.
     * @param employee            The Employee object.
     */
    private void setupTrainingPlaceholder(TextView trainingPlaceHolder, Employee employee) {
        boolean isTraining = employee.isIs_training();
        if (isTraining) {
            trainingPlaceHolder.setVisibility(View.VISIBLE);
            trainingPlaceHolder.setText(employee.role.equals("Manager") ? "Training Manager" : "Training");
        } else {
            if (employee.role.equals("Manager") || employee.role.equals("Owner")) {
                trainingPlaceHolder.setVisibility(View.VISIBLE);
                trainingPlaceHolder.setText(employee.role);
            } else {
                trainingPlaceHolder.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Launches the EmployeeProfileActivity with details of the selected employee.
     *
     * @param employee The selected Employee object.
     */
    private void launchEmployeeProfile(Employee employee) {
        Intent intent = new Intent(context, EmployeeProfileActivity.class);
        intent.putExtra("id", employee.id);
        intent.putExtra("name", employee.firstName + " " + employee.lastName);
        intent.putExtra("gender", employee.gender);
        intent.putExtra("dob", employee.dob.toString());
        intent.putExtra("address", employee.address);
        intent.putExtra("home_number", employee.homeNumber);
        intent.putExtra("mobile_number", employee.mobileNumber);
        intent.putExtra("email", employee.emailAddress);
        intent.putExtra("start_date", employee.startDate.toString());
        intent.putExtra("position", employee.role);
        context.startActivity(intent);
    }
}
