package com.vivant.roomee;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.vivant.roomee.json.JSONParser;
import com.vivant.roomee.json.JSONParserImpl;
import com.vivant.roomee.model.Constants;
import com.vivant.roomee.model.Meeting;
import com.vivant.roomee.model.Room;
import com.vivant.roomee.timeManager.TimeCalculator;
import com.vivant.roomee.timeManager.TimeCalculatorImpl;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by guangbo on 28/10/13.
 */
public class AddMeetingActivity extends Activity {

    private TimePicker pickerStartTime;
    private TimePicker pickerEndTime;
    private EditText txtSummary;
    private EditText txtDescription;
    private EditText txtRoomName;
    private TimeCalculator tc;
    private Room room;
    private ArrayList<Meeting> meetingList = new ArrayList<Meeting>();
    private JSONParser jsonParser;


    //required data for add new meeting
    private static String token;
    private static String roomId;
    private static String summary;
    private static String description;
    private static String startTime;
    private static String endTime;


    /**
     * the onCreated method initialise the add new meeting view once it is start
     * @param savedInstanceState
     */
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
            //retrieve room and meeting data
            roomId = extras.getString("roomId");
            token = extras.getString("token");
            room = (Room) extras.get("room");
            meetingList = extras.getParcelableArrayList("meetingList");

            //set meeting room name
            txtRoomName.setText(room.getName());
        }

    }

    /**
     * this method validates user inputs of add new meeting activity
     * @return true, if all of the data are validated
     */
    private boolean validateAddMeetingInput()
    {
        //get user inputs of meeting details
        summary = txtSummary.getText().toString();
        description = txtDescription.getText().toString();

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
        else if(description.length() == 0) {
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
            if(validateMeetingTime(sHour,sMin, eHour, eMin)) return true;
        }

        //return false if any exception occurs
        return false;
    }


    /**
     * this method validates inputted meeting start time and the meeting duration time
     * @param sHour, int meeting start hour
     * @param sMin, int meeting start minutes
     * @param eHour, int meeting end hour
     * @param eMin, int meeting end minutes
     * @return true, if the meeting time is validate
     */
    private boolean validateMeetingTime(int sHour, int sMin, int eHour, int eMin) {

        //initialise instances
        tc = new TimeCalculatorImpl();
        Date date = new Date();
        int hour = date.getHours();
        int min = date.getMinutes();
        //validate start meeting time
        if((sHour < hour) || (sHour == hour && sMin<min))
        {
            String message = "Meeting start time should be greater than the current time ("+tc.getCurrentTime() +")";
            invalidMessage(message);
            return false;
        }
        else
        {
            //check there should be no meeting between selected time
            for(Meeting m : meetingList)
            {
                String sMeeting = m.getStart();
                String eMeeting = m.getEnd();
                String mStHour = String.valueOf(sHour);
                String mStMin = String.valueOf(sMin);
                String sTime = mStHour + ":" + mStMin;
                String mEndHour = String.valueOf(eHour);
                String mEndMin = String.valueOf(eMin);
                String eTime = mEndHour + ":" + mEndMin;
                if(!tc.compareMeetingTime(sMeeting,eMeeting, sTime, eTime))
                {
                    String message = "The room is already booked by another meeting between the selected time";
                    invalidMessage(message);
                    return false;
                }
            }
            //converting regular meeting time to RFC3339 format
            startTime =  tc.getRFCDateFormat(sHour, sMin);
            endTime =  tc.getRFCDateFormat(eHour, eMin);
            Log.d("Test", startTime);
            Log.d("Test", endTime);
        }

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
        txtRoomName = (EditText) findViewById(R.id.txtRoomName);
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
        {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to submit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //onSubmitProcessed();
                            new AddNewMeetingTask(AddMeetingActivity.this).execute();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
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
     * this is private AuthenticationTask class that validates user
     * whoever request to access the Roomee web service
     */
    private class AddNewMeetingTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;
        private Activity activity;
        private Context context;
        private Boolean done;
        private String message;

        /**
         * AddNewMeetingTask constructor
         */
        public AddNewMeetingTask(Activity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

        /**
         * onPreExecute initalise AuthenticationTask before it starts
         */
        protected void onPreExecute() {
            this.dialog.setMessage("Add new meeting ...");
            this.dialog.setCancelable(false);
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

        /**
         * doInBackground method validates user key in the background
         * via communicating with Roomee web service server
         * and parse the json data that is returned by the server
         */
        @Override
        protected Boolean doInBackground(String... strings) {

            done = false;
            try{
                //crate Json parser instance
                jsonParser = new JSONParserImpl();
                String url = "meetings";

                //set value to add new meeting data
                List<NameValuePair> meetingData = new ArrayList<NameValuePair>(6);
                meetingData.add(new BasicNameValuePair("oauth_token", token));
                meetingData.add(new BasicNameValuePair("id", String.valueOf(room.getId())));
                meetingData.add(new BasicNameValuePair("summary", summary));
                meetingData.add(new BasicNameValuePair("description", description));
                meetingData.add(new BasicNameValuePair("startTime", startTime));
                meetingData.add(new BasicNameValuePair("endTime", endTime));

                //process add new meeting action
                JSONObject json = jsonParser.postJSONToUrl(url, meetingData);

                if(json != null)
                {
                    String HttpStatus = json.getString(Constants.TAG_STATUS);
                    if(HttpStatus.equals("success"))
                    {
//                        JSONObject data = json.getJSONObject(Constants.TAG_DATA);
//                        token = data.getString(Constants.TAG_OAUTH);
                        done = true;
                    }
                }
            }
            catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }

        /**
         * onPostExecute method manges action should be done
         * after the doInBackground method is finished
         */
        @Override
        protected void onPostExecute(final Boolean success) {

            //dismiss the loading dialog
            if(dialog.isShowing()) dialog.dismiss();

            if(done == true)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddMeetingActivity.this);
                builder.setMessage("Thanks, you have created new meeting successfully.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AddMeetingActivity.this.finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else
            {
                message = "Submission failed, please try again";
                invalidMessage(message);
            }
        }
    }
}