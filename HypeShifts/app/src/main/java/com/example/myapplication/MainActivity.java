package com.example.myapplication;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.databinding.ActivityMainBinding;

/**
 * The primary activity of the application and is responsible for initializing the user interface.
 *
 * @author Sergei Borja, Nicholas Westly
 * @since 2023.10.02
 */
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    int userId;

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
        initializeUI();
        setInitialFragment();
        setUpLoginResultListener();
        configureBottomNavBar();
        setUpLogoutResultListener();
    }

    /**
     * Initializes the user interface using View Binding.
     */
    private void initializeUI() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    /**
     * Sets the initial fragment when the activity is first created.
     */
    private void setInitialFragment() {
        binding.bottomNavigationBar.setVisibility(View.GONE);
        replaceFragment(new LoginPage());
    }

    /**
     * Sets up a listener to handle login results. On successful login, updates the UI accordingly.
     */
    private void setUpLoginResultListener() {
        getSupportFragmentManager().setFragmentResultListener("Login", this, (requestKey, result) -> {
            String loginStatus = result.getString("Login");

            if ("1".equals(loginStatus)) {
                userId = result.getInt("user");

                Bundle bundle = new Bundle();
                bundle.putInt("userID", userId);

                HomePage homePageFragment = new HomePage();
                homePageFragment.setArguments(bundle);
                replaceFragment(homePageFragment);
                binding.bottomNavigationBar.setVisibility(View.VISIBLE);
            }

        });
    }


    /**
     * Configures the bottom navigation bar to switch between different fragments based on the selected item.
     */
    private void configureBottomNavBar() {
        binding.bottomNavigationBar.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            Bundle bundle = new Bundle();
            bundle.putInt("userID", userId);

            if (item.getItemId() == R.id.HomePage) {
                fragment = new HomePage();
                fragment.setArguments(bundle); // Set the arguments for HomePage
            } else if (item.getItemId() == R.id.Calender) {
                fragment = new CalendarMonthView();
            } else if (item.getItemId() == R.id.StaffList) {
                fragment = new StaffList();
                fragment.setArguments(bundle);
            }
            if (fragment != null) {
                replaceFragment(fragment);
            }
            return true;
        });
    }

    /**
     * Replaces the current fragment displayed in the frame layout with a new fragment.
     *
     * @param fragment The new fragment to be displayed.
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frameLayoutMain, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Sets up a listener to handle logout requests. On logout, switches to the login page and hides the bottom navigation bar.
     */
    private void setUpLogoutResultListener() {
        getSupportFragmentManager().setFragmentResultListener("Logout", this, (requestKey, result) -> {
            // User wants to logout, replace the fragment with LoginPage
            replaceFragment(new LoginPage());
            binding.bottomNavigationBar.setVisibility(View.GONE); // Hide bottom navigation bar
        });
    }

}