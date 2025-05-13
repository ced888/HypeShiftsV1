package com.example.myapplication.db;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Provides methods to convert Java Time API types (LocalDate, LocalDateTime, LocalTime) to and from strings for database storage.
 *
 * @author Cedric De Leon
 * @since 2023.11.03
 */
public class Converters {
    /**
     * Converts a String to a LocalDateTime.
     *
     * @param dateString The string representation of the date and time.
     * @return A LocalDateTime object or null if the input string is null.
     */
    @TypeConverter
    public static LocalDateTime fromLocalDateTimeToDate(String dateString) {
        return dateString == null ? null : LocalDateTime.parse(dateString);
    }

    /**
     * Converts a LocalDateTime to a String.
     *
     * @param date The LocalDateTime object to be converted.
     * @return A string representation of the LocalDateTime or null if the date is null.
     */
    @TypeConverter
    public static String toDateString(LocalDateTime date) {
        return date == null ? null : date.toString();
    }

    /**
     * Converts a String to a LocalDate.
     *
     * @param date The string representation of the date.
     * @return A LocalDate object or null if the input string is null.
     */
    @TypeConverter
    public static LocalDate fromLocalDateToDate(String date) {
        return date == null ? null : LocalDate.parse(date);
    }

    /**
     * Converts a LocalDate to a String.
     *
     * @param date The LocalDate object to be converted.
     * @return A string representation of the LocalDate or null if the date is null.
     */
    @TypeConverter
    public static String toDateString(LocalDate date) {
        return date == null ? null : date.toString();
    }

    /**
     * Converts a String to a LocalTime.
     *
     * @param time The string representation of the time.
     * @return A LocalTime object or null if the input string is null.
     */
    @TypeConverter
    public static LocalTime fromLocalTimetoTime(String time) {
        return time == null ? null : LocalTime.parse(time);
    }

    /**
     * Converts a LocalTime to a String.
     *
     * @param time The LocalTime object to be converted.
     * @return A string representation of the LocalTime or null if the time is null.
     */
    @TypeConverter
    public static String toTimeString(LocalTime time) {
        return time == null ? null : time.toString();
    }
}