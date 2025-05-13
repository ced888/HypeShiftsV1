package com.example.myapplication;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Adapter class for RecyclerView to manage the display of dates in a calendar format.
 * Handles the creation and binding of view holders that represent each day in the calendar.
 *
 * @author Sergei Borja
 * @since 2023.10.14
 */
class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;

    /**
     * Constructor for CalendarAdapter.
     *
     * @param days           List of LocalDate objects representing the days.
     * @param onItemListener Listener for handling item clicks.
     */
    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    /**
     * Inflates the layout for individual calendar cells and returns a ViewHolder.
     *
     * @param parent   The ViewGroup into which the new view will be added.
     * @param viewType The view type of the new view.
     * @return A new CalendarViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);

        return new CalendarViewHolder(view, onItemListener, days);
    }

    /**
     * Binds data to the view holder for each calendar cell.
     *
     * @param holder   The CalendarViewHolder which should be updated.
     * @param position The position of the item within the adapter's dataset.
     */
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final LocalDate date = days.get(position); // Get the date at the current position.

        // Set the text or leave it blank depending on whether the date is null.
        if (date == null) {
            holder.dayOfMonth.setText("");
        } else {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));

            // Highlight the selected date with a different background and text color.
            if (date.equals(CalendarUtils.selectedDate)) {
                holder.parentView.setBackgroundResource(R.drawable.calendar_cell_background);
                holder.dayOfMonth.setTextColor(Color.parseColor("#E0DED9"));
            } else {
                // Reset to default styles for non-selected dates.
                holder.parentView.setBackgroundColor(Color.TRANSPARENT);
                holder.dayOfMonth.setTextColor(Color.parseColor("#232323"));
            }
        }
    }

    /**
     * Returns the total count of days in the adapter.
     *
     * @return The size of the days array.
     */
    @Override
    public int getItemCount() {

        return days.size();
    }

    /**
     * Interface for handling click events on calendar cells.
     */
    public interface OnItemListener {
        void onItemClick(int position, LocalDate date);
    }
}
