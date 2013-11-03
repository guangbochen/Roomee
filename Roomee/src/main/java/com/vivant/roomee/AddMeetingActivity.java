package com.vivant.roomee;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.vivant.roomee.model.Constants;
import com.vivant.roomee.model.Meeting;
import com.vivant.roomee.model.Room;
import com.vivant.roomee.timeManager.TimeCalculator;
import com.vivant.roomee.timeManager.TimeCalculatorImpl;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by guangbo on 28/10/13.
 */
public class AddMeetingActivity extends Activity {

    private TimePicker pickerStartTime;
    private TimePicker pickerEndTime;
    private EditText txtSummary;
    private EditText txtDescription;
    private static String roomId;
    private static String token;
    private TimeCalculator tc;
    private Room room;
    private ArrayList<Meeting> meetingList = new ArrayList<Meeting>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_meeting);

        //set custom title for the main activity
        ActionBar ab = getActionBar();
        ab.setTitle("Add new meeting");
        ab.setDisplayShowTitleEnabled(true);

        //find all the view contents
        findContentView();

        //retrieve data passed from main activity
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            roomId = extras.getString("roomId");
            token = extras.getString("token");
            room = (Room) extras.get("room");
            meetingList = extras.getParcelableArrayList("meetingList");
            Log.d("room end meeting", room.getTime());
            for(Meeting m : meetingList)
            Log.d("meeting details", m.getSummary());
        }

    }

    /**
     * this method validates user inputs of add new meeting activity
     * @return true, if all of the data are validated
     */
    private boolean validateAddMeetingInput()
    {
        //get user inputs of meeting details
        String summary = txtSummary.getText().toString();
        String desc = txtDescription.getText().toString();

        Integer sHour = pickerStartTime.getCurrentHour();
        Integer sMin = pickerStartTime.getCurrentMinute();

        Integer eHour = pickerEndTime.getCurrentHour();
        Integer eMin = pickerEndTime.getCurrentMinute();

        if(summary.length() == 0) {
            //validate meeting summary input
            txtSummary.requestFocus();
            invalidMessage("Meeting summary can't be empty");
            return false;
        }
        else if(desc.length() == 0) {
            //validate meeting description input
            txtDescription.requestFocus();
            invalidMessage("Meeting description can't be empty");
            return false;
        }
        else if((sHour > eHour) || (sHour == eHour && eMin <= sMin))
        {
            //validate meeting end time
            invalidMessage("Invalid Meeting End Time ");
            return false;
        }
        else{
            //validate meeting start time and the duration time
            if(validateMeetingTime(sHour,sMin)) return true;
        }

        //return false if any exception occurs
        return false;
    }


    /**
     * this method validates inputted meeting start time and the meeting duration time
     * @param sHour, int meeting start hour
     * @param sMin, int meeting start minutes
     * @return true, if the input is validated
     */
    private boolean validateMeetingTime(int sHour, int sMin) {

        //initialise instances
        tc = new TimeCalculatorImpl();
        Date date = new Date();
        int hour = date.getHours();
        int min = date.getMinutes();
        //validate start meeting time
        if((sHour < hour) || (sHour == hour && sMin<min))
        {
            String message = "Meeting start time should greater than the current time ("+tc.getCurrentTime() +")";
            invalidMessage(message);
            return false;
        }

        Log.d(Constants.MAD, String.valueOf(hour));
        Log.d(Constants.MAD, String.valueOf(min));
        return true;
    }

    /**
     * get view components of the activity
     */
    private void findContentView()
    {
        pickerStartTime = (TimePicker) findViewById(R.id.pickerStartTime);
        pickerEndTime =  (TimePicker) findViewById(R.id.pickerEndTime);
        txtSummary = (EditText) findViewById(R.id.txtSummary);
        txtDescription = (EditText) findViewById(R.id.txtDesc);
    }

    /**
     * this method cancel the add new meeting activity
     * @param view, button widget
     */
    public void cancelButtonOnClicked(View view)
    {
        AddMeetingActivity.this.finish();
    }

    /**
     * this method cancel the add new meeting activity
     * @param view, button widget
     */
    public void submitButtonOnclicked(View view)
    {
        if(validateAddMeetingInput())
            onSubmitProcessed();
    }


    /**
     * this method displays invalid token message
     * @param message, string message
     */
    private void invalidMessage(String message)
    {
        Toast toast = Toast.makeText(AddMeetingActivity.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER| Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    /**
     * this method displays confirm dialog once user clicked submit button
     */
    private void onSubmitProcessed()
    {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to submit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddMeetingActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}