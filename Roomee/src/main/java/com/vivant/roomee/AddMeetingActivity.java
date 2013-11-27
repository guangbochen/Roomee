package com.vivant.roomee;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.vivant.roomee.json.JSONParser;
import com.vivant.roomee.json.JSONParserImpl;
import com.vivant.roomee.model.Constants;
import com.vivant.roomee.model.Meeting;
import com.vivant.roomee.model.Room;
import com.vivant.roomee.timeManager.TimeCalculator;
import com.vivant.roomee.timeManager.TimeCalculatorImpl;
import com.vivant.roomee.timeManager.TimePickerFragment;
import com.vivant.roomee.util.DismissKeyboard;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This class manages add new meeting activity
 * Created by guangbo on 28/10/13.
 */
public class AddMeetingActivity extends FragmentActivity implements TimePickerFragment.TimePickedListener, View.OnFocusChangeListener {

    private LinearLayout addNewMeetingLinearLayout;
    private Button btnStartTime;
    private Button btnEndTime;
    private ImageButton btnClearSummary;
    private ImageButton btnClearDesc;
    private EditText txtSummary;
    private EditText txtDescription;
    private TimeCalculator tc;
    private Room room;
    private ArrayList<Meeting> meetingList;
    private JSONParser jsonParser;
    private DismissKeyboard dismissKeyboard;
    private Date date;
    //instances for add new meeting
    private static String deviceToken;
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

        //find all the view contents
        findContentView();

        //initialise instance
        meetingList = new ArrayList<Meeting>();
        tc = new TimeCalculatorImpl();
        dismissKeyboard = new DismissKeyboard(this);
        dismissKeyboard.setupUI(addNewMeetingLinearLayout);
        setMeetingTimes();
        txtSummary.setOnFocusChangeListener(this);
        txtDescription.setOnFocusChangeListener(this);

        //set custom title for the action bar
        ActionBar ab = getActionBar();
        ab.setTitle(Constants.ADDNEWMEETINGTITLE);
        ab.setDisplayShowTitleEnabled(true);

        //retrieve data passed from RoomDetailsActivity
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            //retrieve room and meeting data
            deviceToken = extras.getString("device token");
            room = (Room) extras.get("room");
            meetingList = extras.getParcelableArrayList("meetingList");
        }
    }

    /**
     * this method set the original time of meeting time picker
     */
    private void setMeetingTimes()
    {
        date = new Date();
        btnStartTime.setText(tc.getCurrentTime(date));
        date.setHours(date.getHours()+1);
        btnEndTime.setText(tc.getCurrentTime(date));

    }

    /**
     * this method empty the content of selected editText
     * @param view, ImageButton view
     */
    public void emptyEditText(View view)
    {
        if(view.getId() == btnClearSummary.getId())
            txtSummary.setText("");
        else if (view.getId() == btnClearDesc.getId())
            txtDescription.setText("");
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
        startTime = btnStartTime.getText().toString();
        endTime = btnEndTime.getText().toString();

        //validate meeting summary input
        if(summary.length() == 0) {
            txtSummary.requestFocus();
            invalidMessage(Constants.EMPTYSUMMARY);
            return false;
        }

        //validate meeting description input
        if(description.length() == 0) {
            txtDescription.requestFocus();
            invalidMessage(Constants.EMPTYDESC);
            return false;
        }

        //validate meeting start and end time
        Date startDate = tc.parseStringToDate(startTime);
        Date endDate = tc.parseStringToDate(endTime);
        if(endDate.compareTo(startDate) <= 0) {
            //validate meeting end time
            invalidMessage(Constants.INVALIDENDMEETING);
            return false;
        }
        else{
            //validate meeting start time and the duration time
            if(validateMeetingTime(startDate, endDate)) return true;
        }

        return false;
    }


    /**
     * this method validates inputted meeting start time and the meeting duration time
     * @param startDate, Date start meeting time
     * @param endDate, Date end meeting time
     * @return true, if the meeting time is validate
     */
    private boolean validateMeetingTime(Date startDate, Date endDate) {

        date = new Date();
        //validate start meeting time
        if((startDate.getHours() < date.getHours()) ||
                (startDate.getHours() == date.getHours() && startDate.getMinutes()<date.getMinutes()))
        {
            String message = "Meeting start time should be greater or equal to the current time ("+tc.getCurrentTime(null) +")";
            invalidMessage(message);
            return false;
        }
        else
        {
            //check whether there is a meeting between selected time
            for(Meeting m : meetingList)
            {
                if(!tc.compareMeetingTime(startDate, endDate, m))
                {
                    invalidMessage(Constants.BOOKEDROOM);
                    return false;
                }
            }
            //converting regular meeting time to RFC3339 format
            startTime =  tc.getRFCDateFormat(startDate);
            endTime =  tc.getRFCDateFormat(endDate);
        }
        return true;
    }

    /**
     * get view components of the activity
     */
    private void findContentView()
    {
        addNewMeetingLinearLayout = (LinearLayout) findViewById(R.id.addNewMeetingLayout);
        txtSummary = (EditText) findViewById(R.id.txtSummary);
        txtDescription = (EditText) findViewById(R.id.txtDesc);
        btnStartTime = (Button) findViewById(R.id.btnStartTime);
        btnEndTime = (Button) findViewById(R.id.btnEndTime);
        btnClearSummary = (ImageButton) findViewById(R.id.btn_clear_summary);
        btnClearDesc = (ImageButton) findViewById(R.id.btn_clear_desc);
        addNewMeetingLinearLayout.requestFocus();
    }

    /**
     * this method displays invalid token message
     * @param message, string message
     */
    private void invalidMessage(String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("\n"+message+"\n");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    /**
     * this method creates the menu bar of the activity
     * @param menu, Menu
     * @return false, do not display the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_new_meeting, menu);
        return true;
    }

    /**
     * this method respond to menu action buttons
     * @param item, menu item
     * @return true if button is clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_cancel:
                cancelButtonOnClicked();
                return true;
            case R.id.action_submit:
                submitButtonOnclicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * this method cancel the add new meeting activity
     */
    public void cancelButtonOnClicked()
    {
        AddMeetingActivity.this.finish();
    }

    /**
     * this method cancel the add new meeting activity
     */
    public void submitButtonOnclicked()
    {
        if(validateAddMeetingInput())
        {
            new AlertDialog.Builder(this)
                    .setMessage("\n"+ Constants.SUBMIT +"\n")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //processing submit new meeting request
                            new AddNewMeetingTask(AddMeetingActivity.this).execute();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    /**
     * this method displays a timePicker if the time picker button is clicked
     * @param view, Button view
     */
    public void showTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment(view);
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }

    /**
     * this method update the timePicker button's content after time is picked by user
     * @param time, Calendar picked time
     * @param view, timePicker button
     */
    @Override
    public void onTimePicked(Calendar time, View view) {
        Date date = time.getTime();
        ((Button) view).setText(tc.getCurrentTime(date));
    }

    /**
     * this method manages onFocusChange action of the meeting
     * @param view
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View view, boolean hasFocus) {

        btnClearDesc.setVisibility(View.INVISIBLE);
        btnClearSummary.setVisibility(View.INVISIBLE);
        if(view.getId() == txtSummary.getId() && hasFocus)
            btnClearSummary.setVisibility(View.VISIBLE);
        else if(view.getId() == txtDescription.getId() && hasFocus)
            btnClearDesc.setVisibility(View.VISIBLE);
    }

    /**
     * this is private AuthenticationTask class that validates user
     * whoever request to access the Roomee web service
     */
    private class AddNewMeetingTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;
        private Context context;
        private Boolean done;

        /**
         * AddNewMeetingTask constructor
         */
        public AddNewMeetingTask(Activity activity) {
            context = activity;
            dialog = new ProgressDialog(context);
        }

        /**
         * onPreExecute initialise AuthenticationTask before it starts
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
                String url = "meeting";

                //set value to add new meeting data
                List<NameValuePair> meetingData = new ArrayList<NameValuePair>(6);
                meetingData.add(new BasicNameValuePair("device_token", deviceToken));
                meetingData.add(new BasicNameValuePair("id", String.valueOf(room.getId())));
                meetingData.add(new BasicNameValuePair("summary", summary));
                meetingData.add(new BasicNameValuePair("description", description));
                meetingData.add(new BasicNameValuePair("startTime", startTime));
                meetingData.add(new BasicNameValuePair("endTime", endTime));

                String jsonError = meetingData.toString();
                Log.d("JSON ERROR", jsonError);
                //process add new meeting action
                JSONObject json = jsonParser.postJSONToUrl(url, meetingData);

                if(json != null)
                {
                    String HttpStatus = json.getString(Constants.TAG_STATUS);
                    if(HttpStatus.equals("success")) done = true;
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
                builder.setMessage("\n"+ Constants.SUBMITSUCCESS +"\n")
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
                invalidMessage(Constants.SUBMISIONFAILED);
            }
        }
    }
}