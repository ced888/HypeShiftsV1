
package com.example.myapplication;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.db.DayOff;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Adapter for displaying a list of days off in a RecyclerView.
 * Each item represents a day off with its date and reason.
 *
 * @author Sergei Borja
 * @since 2023.11.29
 */
public class DayOffAdapter extends RecyclerView.Adapter<DayOffAdapter.DayOffViewHolder> {
    private final DayOffItemClickListener clickListener; // Listener for click events on day off items
    private List<DayOff> dayOffList; // List of day off records

    /**
     * Constructor for DayOffAdapter.
     *
     * @param dayOffList    List of DayOff objects to be displayed.
     * @param clickListener Listener for handling click events on day off items.
     */
    public DayOffAdapter(List<DayOff> dayOffList, DayOffItemClickListener clickListener) {
        this.dayOffList = dayOffList;
        this.clickListener = clickListener;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * This method creates a new ViewHolder and initializes some private fields to be used by RecyclerView.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View, for different types of items in the RecyclerView.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public DayOffViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_off_item, parent, false);
        return new DayOffViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method updates the contents of the ViewHolder to reflect the item at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(DayOffViewHolder holder, int position) {
        DayOff dayOff = dayOffList.get(position);
        holder.bind(dayOff, clickListener);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return dayOffList.size();
    }

    /**
     * Updates the list of days off with new data and refreshes the RecyclerView.
     *
     * @param newDayOffs The new list of DayOff objects.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateDayOffsList(List<DayOff> newDayOffs) {
        this.dayOffList = newDayOffs;
        notifyDataSetChanged();
    }

    /**
     * Interface for handling clicks on day off items.
     */
    public interface DayOffItemClickListener {
        void onDayOffClick(DayOff dayOff);
    }

    /**
     * ViewHolder class for day off items. Binds the day off data to the view.
     */
    public static class DayOffViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTextView;
        private final TextView reasonTextView;

        public DayOffViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            reasonTextView = itemView.findViewById(R.id.reasonTextView);
        }

        /**
         * Binds a DayOff object to the view.
         *
         * @param dayOff        The DayOff object to bind.
         * @param clickListener The listener for handling click events.
         */
        @SuppressLint("SetTextI18n")
        public void bind(DayOff dayOff, DayOffItemClickListener clickListener) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            dateTextView.setText(dayOff.date.format(formatter));
            reasonTextView.setText("Reason: " + dayOff.reason);

            itemView.setOnClickListener(v -> clickListener.onDayOffClick(dayOff));
        }
    }
}
