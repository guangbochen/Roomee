package com.vivant.roomee.timeManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by guangbo on 15/11/13.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private TimePickedListener mListener;
    private Button btnTime;
    private TimeCalculator tc;

    public TimePickerFragment(View view) {
        btnTime = (Button) view;
        tc = new TimeCalculatorImpl();
    }

    /**
     * this method creates the new time picker dialog
     * @param savedInstanceState, Bundle instance
     * @return TimePickerDialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int hour;
        int minute;
        //set the current time to the picked time
        String time = btnTime.getText().toString();
        if(time.length()!=0)
        {
            Date date = tc.parseStringToDate(time);
            hour = date.getHours();
            minute = date.getMinutes();
        }
        else
        {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,false);
    }

    /**
     * this method creating event callbacks to the activity
     * @param activity, android activity
     */
    @Override
    public void onAttach(Activity activity)
    {
        // when the fragment is initially shown (i.e. attached to the activity), cast the activity to the callback interface type
        super.onAttach(activity);
        try
        {
            mListener = (TimePickedListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement " + TimePickedListener.class.getName());
        }
    }

    /**
     * this method manages the work with the time chosen by the user
     * @param view, timePicker
     * @param hourOfDay, int hour
     * @param minute, int minute
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // when the time is selected, send it to the activity via its callback interface method
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        mListener.onTimePicked(c, btnTime);
    }

    public static interface TimePickedListener
    {
        public void onTimePicked(Calendar time, View view);
    }
}

