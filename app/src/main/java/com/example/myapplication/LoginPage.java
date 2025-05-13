package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.User;

/**
 * Represents the login page of the application, allowing users to input their credentials
 * and gain access based on their roles and permissions.
 *
 * @author Nicholas Westly, Sergei Borja
 * @since 2023.10.13
 */
public class LoginPage extends Fragment {

    // UI elements for the login page
    private TextView warningTxt;
    private EditText employeeID;
    private EditText passwordTxt;
    private AppDatabase db;
    private User user;

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
        return inflater.inflate(R.layout.fragment_login_page, container, false);
    }

    /**
     * Sets up the UI components and button click listener after the view is created.
     *
     * @param view               The View returned by onCreateView method.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getDbInstance(getActivity());

        // Initialize UI elements
        warningTxt = view.findViewById(R.id.txtWarning);
        employeeID = view.findViewById(R.id.txtInputEmployeeId);
        passwordTxt = view.findViewById(R.id.txtInputPassword);

        // Button to trigger login and trigger login
        Button loginBtn = view.findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(this::handleLogin);
    }

    /**
     * Handles the login process by verifying user credentials.
     * Sets the appropriate UI elements based on login success or failure.
     *
     * @param view The View that was clicked.
     */
    private void handleLogin(View view) {
        String enteredEmployeeID = employeeID.getText().toString();

        if (enteredEmployeeID.equals("admin")) {
            // Package a successful login indicator and send it to other fragments for processing.
            Bundle Login = new Bundle();
            Login.putString("Login", "1");
            getParentFragmentManager().setFragmentResult("Login", Login);
        } else if (isValidUser()) {
            // Package a successful login indicator and send it to other fragments for processing.
            Bundle Login = new Bundle();
            Login.putString("Login", "1");
            Login.putInt("user", user.employeeId);
            getParentFragmentManager().setFragmentResult("Login", Login);
        } else {
            warningTxt.setText("Password or Username is Incorrect\nPlease try again or contact your manager");
            setWarningHeight();
        }
    }

    /**
     * Verifies if the entered username and password match an existing user.
     *
     * @return True if the user exists and the password matches, false otherwise.
     */
    private boolean isValidUser() {
        // Fetch the entered employee ID and password
        String enteredEmployeeID = employeeID.getText().toString();
        String enteredPassword = passwordTxt.getText().toString();

        // Look for username entered
        user = db.userDao().findByName(enteredEmployeeID);
        // if username does not exist
        if (user == null) {
            return false;
        } else {
            // if it does exist and password is correct
            return user.password.equals(enteredPassword) && user.userName.equals(enteredEmployeeID);
        }
    }

    /**
     * Adjusts the layout parameters of the warning text to ensure it displays correctly.
     */
    private void setWarningHeight() {
        ViewGroup.LayoutParams layoutParams = warningTxt.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        warningTxt.setLayoutParams(layoutParams);
        warningTxt.setPadding(0, 20, 0, 0);
    }
}