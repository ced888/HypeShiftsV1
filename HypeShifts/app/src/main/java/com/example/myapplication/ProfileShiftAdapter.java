package com.example.myapplication;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.ShiftInstance;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Adapter for displaying shift details in a RecyclerView within the Employee Profile Activity.
 * This class binds shift data to views for individual shift items.
 *
 * @author Sergei Borja
 * @since 2023.12.02
 */
public class ProfileShiftAdapter extends RecyclerView.Adapter<ProfileShiftAdapter.ShiftViewHolder> {

    private final AppDatabase db;
    private List<ShiftInstance> shifts;

    /**
     * Constructor for the ProfileShiftAdapter.
     *
     * @param shifts List of ShiftInstance objects to be displayed.
     * @param db     Reference to the AppDatabase for accessing shift details.
     */
    public ProfileShiftAdapter(List<ShiftInstance> shifts, AppDatabase db) {
        this.shifts = shifts;
        this.db = db;
    }

    @NonNull
    @Override
    public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each shift item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_shift_item, parent, false);
        return new ShiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftViewHolder holder, int position) {
        // Get the shift instance for the current position
        ShiftInstance shift = shifts.get(position);

        // Retrieve additional details like shift type and date
        String shiftType = db.shiftDao().getShiftTypeNameByShiftInstanceId(shift.id);
        LocalDate date = db.shiftDao().getDateOfShift(shift.id);

        // Format the date and day of the week for display
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        String dateString = date.format(dateFormatter);
        DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE");
        String dayOfWeekString = date.format(dayOfWeekFormatter);

        // Determine the shift time based on the shift type
        String shiftTime = determineShiftTime(shiftType);

        // Set the text in the ViewHolder's TextViews with formatted shift details
        holder.dateTxt.setText(dateString);
        holder.dayOfTheWeekTxt.setText(dayOfWeekString);
        holder.shiftTypeTxt.setText(shiftType);
        holder.timeTxt.setText(shiftTime);
    }

    @Override
    public int getItemCount() {
        // Return the total number of shift items
        return shifts.size();
    }

    /**
     * Updates the list of shifts and notifies the adapter of the data change.
     * This is used when the data set has been modified.
     *
     * @param newShifts Updated list of ShiftInstance objects.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateShiftsList(List<ShiftInstance> newShifts) {
        shifts = newShifts;
        notifyDataSetChanged();
    }

    /**
     * Determines the shift time string based on the shift type.
     * This method maps shift types to their corresponding time ranges.
     *
     * @param shiftType The type of shift (e.g., "Full", "Morning", "Afternoon", "Evening").
     * @return Formatted shift time string.
     */
    private String determineShiftTime(String shiftType) {
        switch (shiftType) {
            case "Full":
                return "10:00 AM - 6:00 PM";
            case "Morning":
                return "8:00 AM - 12:00 PM";
            case "Afternoon":
                return "12:00 PM - 4:00 PM";
            default:
                return "4:00 PM - 8:00 PM";
        }
    }

    /**
     * ViewHolder class for individual shift items.
     * This class represents each shift item and is responsible for setting its view elements.
     */
    static class ShiftViewHolder extends RecyclerView.ViewHolder {
        TextView dateTxt, dayOfTheWeekTxt, timeTxt, shiftTypeTxt;

        public ShiftViewHolder(View itemView) {
            super(itemView);
            // Change the text in the TextViews from the layout
            dateTxt = itemView.findViewById(R.id.dateTxt);
            dayOfTheWeekTxt = itemView.findViewById(R.id.dayOfTheWeekTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            shiftTypeTxt = itemView.findViewById(R.id.shiftTypeTxt);
        }
    }
}


