package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.User;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * ChangePassword is an AppCompatActivity that allows a user to change their password.
 * It includes validation for the current and new passwords and updates the password in the database.
 *
 * @author Sergei Borja
 * @since 2023.12.03
 */
public class ChangePassword extends AppCompatActivity {
    int employeeId;
    private TextView txtNewPasswordNoMatch, txtIncorrectCurrentPassword;
    private EditText currentPasswordET, newPasswordET, newPasswordAgainET;

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
        setContentView(R.layout.activity_change_password);

        retrieveEmployeeId();
        setupToolbar();
        initializeUI();
    }

    /**
     * Retrieves the employee ID from the intent that started this activity.
     */
    private void retrieveEmployeeId() {
        Intent intent = getIntent();
        employeeId = intent.getIntExtra("employeeID", -1);
    }

    /**
     * Initializes and links the UI components with their respective views.
     */
    private void initializeUI() {
        txtNewPasswordNoMatch = findViewById(R.id.txtNewPasswordNoMatch);
        txtIncorrectCurrentPassword = findViewById(R.id.txtIncorrectCurrentPassword);
        currentPasswordET = findViewById(R.id.currentPasswordET);
        newPasswordET = findViewById(R.id.newPasswordET);
        newPasswordAgainET = findViewById(R.id.newPasswordAgainET);
    }

    /**
     * Sets up the navigation click listener for the toolbar.
     */
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBarChangePassword);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(this::onCancelClick);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.save) {
                attemptPasswordChange();
                return true;
            }
            return false;
        });
    }

    /**
     * Inflates the menu for the top app bar.
     *
     * @param menu The menu instance for the top app bar.
     * @return true to display the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_save, menu);
        return true;
    }

    /**
     * Handles the action to cancel password change and return to the previous screen.
     *
     * @param view The view (button) that triggers this action.
     */
    private void onCancelClick(View view) {
        onBackPressed();
    }

    /**
     * Attempts to change the user's password.
     * Validates the current password and updates it if the new password is valid and confirmed.
     */
    private void attemptPasswordChange() {
        String currentPassword = currentPasswordET.getText().toString();
        String newPassword = newPasswordET.getText().toString();
        String confirmPassword = newPasswordAgainET.getText().toString();

        // Proceed if the employee ID is valid
        if (employeeId != -1) {
            AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
            User currentUser = db.userDao().findByEmployeeId(employeeId);

            // Validate the current password
            if (currentUser != null && currentUser.password.equals(currentPassword)) {
                // Validate the new password length
                if (newPassword.length() < 8) {
                    displayPasswordLengthError();
                    return;
                }

                // Check if new password and confirmation match
                if (!newPassword.equals(confirmPassword)) {
                    displayPasswordMismatchError();
                    return;
                }

                // Update the password in the database and finish the activity
                db.userDao().changeUserPassword(currentUser.userName, newPassword);
                finish();
            } else {
                // Display error if the current password is incorrect
                txtIncorrectCurrentPassword.setText("Current password is incorrect");

                txtIncorrectCurrentPassword.setVisibility(View.VISIBLE);
                txtNewPasswordNoMatch.setVisibility(View.GONE);

                // Reset Input Field
                currentPasswordET.setText("");
            }
        } else {
            Toast.makeText(this, "Invalid employee ID", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Displays an error message for new password length requirement.
     */
    @SuppressLint("SetTextI18n")
    private void displayPasswordLengthError() {
        txtNewPasswordNoMatch.setText("New password must be at least 8 characters long");

        txtNewPasswordNoMatch.setVisibility(View.VISIBLE);
        txtIncorrectCurrentPassword.setVisibility(View.GONE);

        // Reset Input Field
        newPasswordET.setText("");
        newPasswordAgainET.setText("");
    }

    /**
     * Displays an error message when new passwords do not match.
     */
    @SuppressLint("SetTextI18n")
    private void displayPasswordMismatchError() {
        txtNewPasswordNoMatch.setText("New passwords do not match");

        txtNewPasswordNoMatch.setVisibility(View.VISIBLE);
        txtIncorrectCurrentPassword.setVisibility(View.GONE);

        // Reset Input Field
        newPasswordET.setText("");
        newPasswordAgainET.setText("");
    }


}