package com.example.myapplication;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * ViewHolder class for individual calendar cells within a RecyclerView.
 * This class displays each day and responds to user interactions by implementing View.OnClickListener.
 *
 * @author Sergei Borja
 * @since 2023.10.14
 */
public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final View parentView;
    public final TextView dayOfMonth;
    private final ArrayList<LocalDate> days;
    private final CalendarAdapter.OnItemListener onItemListener;

    /**
     * Constructor for CalendarViewHolder.
     * Initializes the view elements and sets up the click listener.
     *
     * @param itemView       The view for the individual calendar cell.
     * @param onItemListener Listener for handling click events on each cell.
     * @param days           List of LocalDate objects the RecyclerView will display.
     */
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days) {
        super(itemView);
        parentView = itemView.findViewById(R.id.parentView);
        dayOfMonth = itemView.findViewById(R.id.txtDayCell);

        this.onItemListener = onItemListener;
        this.days = days;

        itemView.setOnClickListener(this);
    }

    /**
     * Handles the click event on a calendar cell.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            LocalDate date = days.get(position);
            onItemListener.onItemClick(position, date);
        }
    }
}
