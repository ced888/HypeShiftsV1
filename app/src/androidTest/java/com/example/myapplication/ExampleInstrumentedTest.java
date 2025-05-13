package com.example.myapplication;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.action.ViewActions;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;
import static androidx.test.espresso.Espresso.onView;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.CalendarDay;
import com.example.myapplication.db.Employee;
import com.example.myapplication.db.EmployeeDao;
import com.example.myapplication.db.ShiftDao;
import com.example.myapplication.db.ShiftInstance;
import com.example.myapplication.db.ShiftType;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExampleInstrumentedTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    //database test
    //add employee, update employee, delete employee
    private EmployeeDao employeeDao;
    private ShiftDao shiftDao;
    private Employee emp;
    @Test
    public void useAppContext() {

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.myapplication", appContext.getPackageName());
    }
    @Before
    public void CreateDb()
    {
        AppDatabase test_db = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AppDatabase.class
        ).allowMainThreadQueries().build();
        employeeDao = test_db.employeeDao();
        shiftDao = test_db.shiftDao();

        emp = new Employee("Cedric",
                "De Leon",
                "3287423423",
                "8768976223",
                "Male",
                LocalDate.parse("2000-01-01", DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse("2023-11-04", DateTimeFormatter.ISO_LOCAL_DATE),
                "E-mail",
                "123 Test Avenue",
                "cd@gmail.com",
                "Manager",
                true);

    }

    @Test
    public void AddEmployee()
    {
        employeeDao.insertEmployee(emp);
        assertNotNull(employeeDao.getAll());
    }

    @Test
    public void EmployeeDelete(){
        employeeDao.deleteEmployee(emp);
        assertTrue(employeeDao.getAll().isEmpty());
    }

    @Test
    public void CreateEmptyShifts(){
        LocalDate test_day = LocalDate.parse("2023-11-13",DateTimeFormatter.ISO_LOCAL_DATE);
        //Add day
        CalendarDay workDay = new CalendarDay(test_day);
        int dayId = (int)shiftDao.insertDay(workDay);

        //Add Shift Types
        LocalDateTime startTime = LocalDateTime.parse("2023-11-13T08:00:00",DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endTime = LocalDateTime.parse("2023-11-13T12:00:00",DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ShiftType morningShift = new ShiftType(dayId,"Morning", startTime, endTime, true, false, 3);
        int shiftTypeId = (int) shiftDao.insertShiftType(morningShift);

        //Add Shift Instance with null employees
        List<ShiftInstance> shiftInstances = new ArrayList<ShiftInstance>(Arrays.asList(
            new ShiftInstance(shiftTypeId),
            new ShiftInstance(shiftTypeId),
            new ShiftInstance(shiftTypeId))
        );

        shiftDao.insertShiftInstances(shiftInstances);
        List<ShiftInstance> si = shiftDao.getAllShifts();
        List<ShiftType> st = shiftDao.getAllShiftTypeByDay(dayId);
        Assert.assertNotNull(si);
    }


    //UI TESTING
    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityRule = new ActivityScenarioRule<>(MainActivity.class);
    @Test
    public void ui_HomePage() {
        onView(withId(R.id.HomePage));
    }
    @Test
    public void ui_Login(){
        onView(withId(R.id.txtInputEmployeeId)).perform(ViewActions.typeText("admin"));
        onView(withId(R.id.txtInputPassword)).perform(ViewActions.typeText("admin"));
        onView(withId(R.id.btnLogin)).perform(ViewActions.click());
    }

}