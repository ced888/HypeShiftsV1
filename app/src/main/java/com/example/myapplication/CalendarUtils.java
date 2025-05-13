package com.example.myapplication;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Utility class for calendar-related operations. Provides methods to format dates and generate lists of dates
 * for display in calendar views.
 *
 * @author Sergei Borja
 * @since 2023.10.14
 */
public class CalendarUtils {
    public static LocalDate selectedDate; // Currently selected date in the calendar

    /**
     * Formats a LocalDate to a string in the "Month Year" format.
     * Example: "October 2023"
     *
     * @param date The LocalDate to be formatted.
     * @return A string representing the formatted date.
     */
    public static String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    /**
     * Formats a LocalDate to a string in the "Month Day, Year" format.
     * Example: "October 14, 2023"
     *
     * @param date The LocalDate to be formatted.
     * @return A string representing the formatted date.
     */
    public static String monthDayYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        return date.format(formatter);
    }

    /**
     * Generates an ArrayList of LocalDate objects representing all days in the month of the given date.
     * The list is adjusted to fit into a full week grid by adding nulls for days outside the month.
     *
     * @param date A LocalDate object from which the month and year are taken.
     * @return An ArrayList of LocalDate objects for each day in the month, plus nulls for padding.
     */
    public static ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();

        // Find the first day of the month
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        // Loop through the grid to fill the days
        for (int i = 1; i <= 42; i++) {
            // Add null for cells before the first day of the month and after the last day
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add(null);
            } else {
                // Add the actual date for days within the month
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    /**
     * Generates an ArrayList of LocalDate objects representing each day in the week of the provided date.
     *
     * @param selectedDate The date from which the week is determined.
     * @return An ArrayList of LocalDate objects for each day in that week.
     */
    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = sundayForDate(selectedDate);

        // Return an empty list if no corresponding Sunday is found
        if (current == null) {
            return days;
        }

        // Add each day of the week to the list, starting from the identified Sunday
        LocalDate endDate = current.plusWeeks(1);

        while (current.isBefore(endDate)) {
            days.add(current);
            current = current.plusDays(1);
        }
        return days;
    }

    /**
     * Finds the Sunday on or before the given date.
     *
     * @param current The starting date for the search.
     * @return The date of the Sunday found, or null if none within a week prior.
     */
    private static LocalDate sundayForDate(LocalDate current) {
        LocalDate oneWeekAgo = current.minusWeeks(1);

        // Search backwards from the given date to find the closest Sunday
        while (current.isAfter(oneWeekAgo)) {
            if (current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;
            current = current.minusDays(1);
        }
        return null;
    }

    /**
     * Determines if a given date falls on a weekend (Saturday or Sunday).
     *
     * @param date The date to check.
     * @return True if the date is a weekend, false otherwise.
     */
    public static boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}
