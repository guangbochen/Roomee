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

import java.sql.Time;
import java.util.Calendar;

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
        }

    }

    /**
     * this method validates new meeting user inputs
     * @return
     */
    private boolean validateAddMeetingInput()
    {
        String summary = txtSummary.getText().toString();
        String desc = txtDescription.getText().toString();

        Integer sHour = pickerStartTime.getCurrentHour();
        Integer sMin = pickerStartTime.getCurrentMinute();

        Integer eHour = pickerEndTime.getCurrentHour();
        Integer eMin = pickerEndTime.getCurrentMinute();

        if(summary.length() == 0) {
            txtSummary.requestFocus();
            invalidMessage("Meeting summary can't be empty");
            return false;
        }
        else if(desc.length() == 0) {
            txtDescription.requestFocus();
            invalidMessage("Meeting description can't be empty");
            return false;
        }
        else if(sHour >= eHour && sMin >= eMin)
        {
            invalidMessage("Invalid Meeting End Time ");
            return false;
        }
        else{

            onSubmitProcessed();
        }

        Log.d(Constants.MAD, summary);
        Log.d(Constants.MAD, desc);
        Log.d(Constants.MAD, String.valueOf(sHour));
        Log.d(Constants.MAD, String.valueOf(sMin));
        Log.d(Constants.MAD, String.valueOf(eHour));
        Log.d(Constants.MAD, String.valueOf(eMin));

        return false;
    }


    private void findContentView()
    {
        pickerStartTime = (TimePicker) findViewById(R.id.pickerStartTime);
        pickerEndTime =  (TimePicker) findViewById(R.id.pickerEndTime);
        txtSummary = (EditText) findViewById(R.id.txtSummary);
        txtDescription = (EditText) findViewById(R.id.txtDesc);
    }

    public void cancelButtonOnClicked(View view)
    {
//        Intent intent = new Intent(getApplicationContext(), RoomDetailsActivity.class);
//        intent.putExtra("token", token);
//        intent.putExtra("roomId", roomId);
//        startActivity(intent);
        AddMeetingActivity.this.finish();
    }

    public void submitButtonOnclicked(View view)
    {
        if(validateAddMeetingInput())
        {
            Intent intent = new Intent(getApplicationContext(), RoomDetailsActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("roomId", roomId);
            startActivity(intent);
        }
        else
        {
        }
    }


    private void invalidMessage(String message)
    {
        Toast toast = Toast.makeText(AddMeetingActivity.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER| Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

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