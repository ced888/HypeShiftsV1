package com.example.myapplication;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder class that represents individual Employee Shift cells within a RecyclerView.
 * This class handles the display of each employee and reacts to user interactions.
 *
 * @author Cedric De Leon, Nicholas Westly, Sergei Borja
 * @since 2023.10.28
 */
public class CreateShiftViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final View parentShiftView;
    private final CreateShiftAdapter.OnEmployeeListener onEmployeeListener;
    ImageView imgProfilePic;
    TextView txtEmployeeName, trainingPlaceHolder, txtRemove;
    LinearLayout employeeShiftContainer, trainingPlaceHolderLayout;
    int employeeId;
    String Shift;
    boolean unassigned;

    /**
     * Constructor for the CreateShiftViewHolder.
     * Initializes the view elements and sets up the click listener.
     *
     * @param itemView           The view for the individual employee shift cell.
     * @param onEmployeeListener Listener for handling click events on each cell.
     * @param Shift              Type of shift (Morning, Afternoon, Evening, Full).
     */
    public CreateShiftViewHolder(@NonNull View itemView, CreateShiftAdapter.OnEmployeeListener onEmployeeListener, String Shift) {
        super(itemView);
        // Initialize view components
        parentShiftView = itemView.findViewById(R.id.parentShiftView);
        imgProfilePic = itemView.findViewById(R.id.imgProfilePic);
        txtEmployeeName = itemView.findViewById(R.id.txtEmployeeName);
        trainingPlaceHolder = itemView.findViewById(R.id.trainingPlaceHolder);
        trainingPlaceHolderLayout = itemView.findViewById(R.id.trainingPlaceHolderLayout);
        employeeShiftContainer = itemView.findViewById(R.id.employeeShiftContainer);
        txtRemove = itemView.findViewById(R.id.txtRemove);

        // Set the shift type and listener
        this.Shift = Shift;
        this.onEmployeeListener = onEmployeeListener;

        // Set the ViewHolder as the click listener
        itemView.setOnClickListener(this);
    }

    /**
     * Handles the click event on an employee shift cell.
     * Invokes the onItemClick method of the provided onEmployeeListener with the position and details.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        // Notify the listener about the clicked item, passing the relevant details
        onEmployeeListener.onEmployeeClick(getAdapterPosition(), Shift, unassigned, employeeId);
        Log.d("ONCLICK CELL", "onClick: " + Shift);
        Log.d(null, "Employee Id: " + employeeId);
    }

    /**
     * Updates the background and visibility of elements in the ViewHolder based on the selection status.
     *
     * @param isSelected               Indicates if the employee is currently selected.
     * @param layoutToChangeVisibility The layout whose visibility needs to be changed.
     */
    public void updateBackgroundAndVisibility(boolean isSelected, View layoutToChangeVisibility) {
        if (isSelected) {
            imgProfilePic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#232323")));
            imgProfilePic.setImageTintList(ColorStateList.valueOf(Color.parseColor("#E0DED9")));

            trainingPlaceHolderLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#232323")));

            employeeShiftContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00C49A")));

            layoutToChangeVisibility.setVisibility(View.VISIBLE);
        } else {
            imgProfilePic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0DED9")));
            imgProfilePic.setImageTintList(ColorStateList.valueOf(Color.parseColor("#232323")));

            trainingPlaceHolderLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00C49A")));

            employeeShiftContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#232323")));

            layoutToChangeVisibility.setVisibility(View.GONE);
        }
    }

}
