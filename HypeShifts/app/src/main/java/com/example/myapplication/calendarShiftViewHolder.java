package com.example.myapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder for a calendar shift item in a RecyclerView.
 * Holds the UI elements for displaying an employee's shift details.
 *
 * @author Sergei Borja
 * @since 2023.12.02
 */
class CalendarShiftViewHolder extends RecyclerView.ViewHolder {
    public final View parentShiftView;
    ImageView imgProfilePic;
    TextView txtEmployeeName;
    TextView trainingPlaceHolder;
    int employeeId;
    String Shift;
    boolean unassigned;

    /**
     * Constructor for CalendarShiftViewHolder.
     * Initializes the view elements from the layout.
     *
     * @param itemView The view of the calendar shift item.
     * @param Shift    The shift type associated with this ViewHolder.
     */
    public CalendarShiftViewHolder(@NonNull View itemView, String Shift) {
        super(itemView);
        // Initialize the UI components from the itemView
        parentShiftView = itemView.findViewById(R.id.parentShiftView);
        imgProfilePic = itemView.findViewById(R.id.imgProfilePic);
        txtEmployeeName = itemView.findViewById(R.id.txtEmployeeName);
        trainingPlaceHolder = itemView.findViewById(R.id.trainingPlaceHolder);

        // Set the shift type and initialize the employee ID and unassigned flag
        this.Shift = Shift;
        employeeId = 0;
        unassigned = false;
    }
}
