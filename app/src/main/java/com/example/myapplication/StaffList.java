package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Employee;

import java.util.List;

/**
 * This fragment displays a list of staff members in a ListView.
 * Each staff member has a name and profile photo displayed.
 *
 * @author Cedric De Leon, Osman Osman, Sergei Borja
 * @since 2023.10.02
 */
public class StaffList extends Fragment {

    private static final int ADD_EMPLOYEE_REQUEST_CODE = 1;
    TextView emptyMessage, nameTv;
    private ListView listView;
    private StaffListAdapter staffListAdapter;
    private Employee employee;

    /**
     * Displays the visuals of the fragment when needed.
     *
     * @param inflater           Used to turn the layout design into a view.
     * @param container          The main view where the fragment should attach to, if present.
     * @param savedInstanceState Data from the last saved state, if there was one.
     * @return Returns the constructed layout for the fragment.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppDatabase db = AppDatabase.getDbInstance(requireContext());

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("userID")) {
            int userId = arguments.getInt("userID");
            if (userId != -1) {
                employee = db.employeeDao().getEmployeeById((userId));
            }
        }

        View view = inflater.inflate(R.layout.fragment_staff_list, container, false);

        listView = view.findViewById(R.id.list);
        emptyMessage = view.findViewById(R.id.emptyEmployeeListTxt);
        nameTv = view.findViewById(R.id.nameTv);

        if (employee != null) {
            nameTv.setText(employee.firstName + " " + employee.lastName);
        }

        view.findViewById(R.id.addBtn).setOnClickListener(v -> {
            // Start the activity to add a new employee
            Intent intent = new Intent(getActivity(), AddNewEmployeeActivity.class);
            startActivityForResult(intent, ADD_EMPLOYEE_REQUEST_CODE);
        });

        employeeListView(listView);

        return view;
    }

    /**
     * Called when the Fragment is visible to the user.
     * Refreshes the staff list to reflect any changes.
     */
    @Override
    public void onStart() {
        super.onStart();
        updateEmployeeList(); // This will refresh the list every time the fragment becomes visible
    }

    /**
     * Updates the staff list with current data from the database.
     */
    private void updateEmployeeList() {
        AppDatabase db = AppDatabase.getDbInstance(this.getContext());
        List<Employee> employees = db.employeeDao().getAll();
        staffListAdapter.updateEmployeeList(employees);

        // Sort employees alphabetically by full name
        employees.sort((emp1, emp2) -> {
            String fullName1 = emp1.firstName + " " + emp1.lastName;
            String fullName2 = emp2.firstName + " " + emp2.lastName;
            return fullName1.compareToIgnoreCase(fullName2);
        });

        if (employees.isEmpty()) {
            listView.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);
        }
    }

    /**
     * Populates the ListView with employee data and sets item click behavior.
     */
    private void employeeListView(ListView listView) {
        AppDatabase db = AppDatabase.getDbInstance(this.getContext());
        List<Employee> employees = db.employeeDao().getAll();

        // Sort employees alphabetically by full name
        employees.sort((emp1, emp2) -> {
            String fullName1 = emp1.firstName + " " + emp1.lastName;
            String fullName2 = emp2.firstName + " " + emp2.lastName;
            return fullName1.compareToIgnoreCase(fullName2);
        });

        staffListAdapter = new StaffListAdapter(getActivity(), employees);
        listView.setAdapter(staffListAdapter);

        if (employees.isEmpty()) {
            listView.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                Employee clickedEmp = employees.get(position);
                Intent intent = new Intent(getActivity(), EmployeeProfileActivity.class);
                intent.putExtra("id", clickedEmp.id);
                startActivity(intent);
            });
        }
    }

    /**
     * Called when an activity launched here (for result) exits, giving the requestCode started it with.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EMPLOYEE_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            updateEmployeeList();

            // Toast message to indicate that the employee has been added
            Toast.makeText(getContext(), "Employee added successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
