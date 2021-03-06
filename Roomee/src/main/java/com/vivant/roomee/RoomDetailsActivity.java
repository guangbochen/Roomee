package com.vivant.roomee;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vivant.roomee.handler.MeetingTableHandler;
import com.vivant.roomee.model.Meeting;
import com.vivant.roomee.model.Room;
import com.vivant.roomee.navigationDrawer.MeetingListDrawer;
import com.vivant.roomee.navigationDrawer.MeetingListDrawerImpl;
import com.vivant.roomee.services.RefreshRoomService;
import com.vivant.roomee.timeManager.TimeCalculator;
import com.vivant.roomee.timeManager.TimeCalculatorImpl;
import java.util.ArrayList;
import android.preference.PreferenceManager;


/**
 * this class manages displaying the RoomDetails activity and
 * update the contents if the room status is updated.
 */
public class RoomDetailsActivity extends Activity {

    private static String deviceToken;
    private static String token;
    private static boolean done;
    private Room room;
    private ArrayList<Meeting> meetingList;
    private MeetingTableHandler mtHandler;
    private TimeCalculator tc;
    private ProgressDialog dialog;
    private MeetingListDrawer mlDrawer;
    //layout components for room details
    private LinearLayout meetingDetailsLayout;
    private LinearLayout headerLinerLayout;
    private LinearLayout roomInfoLinerLayout;
    private LinearLayout timeInfoLinerLayout;
    private RelativeLayout meetingInfoLayout;
    private LinearLayout meetingListDrawerLayout;
    private DrawerLayout meetingDetailsDrawerLayout;
    //view components for room details
    private TextView txtStatus;
    private TextView txtRoomName;
    private TextView txtTime;
    private TextView timeline;
    private Button btnEndMeeting;
    private Button btnExtendMeeting;
    private final static String BROADCAST = "com.vivant.roomee.startRoomService";
    private final static String AUTOREFRESH = "com.vivant.roomee.services.autoRefresh";
    private final static String dialogMessage = "Please wait ...";
    private Thread timeThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);
        deviceToken = getDevToken();
        Log.d("DEV TOKEN DETAILS ACTIVITY", deviceToken);
        Log.d("Passed activation activity", "shared preferences stored");
        //find all of the view components in the RoomDetailsActivity
        findViewComponents();

        //initialise instances
        tc = new TimeCalculatorImpl();
        mlDrawer = new MeetingListDrawerImpl(RoomDetailsActivity.this, meetingListDrawerLayout);


//        Log.d("DEVICE TOKEN DETAILS", deviceToken);
        displayProgressDialog();
        startAutoRefreshServices(deviceToken);

        //retrieve data passed from RoomListActivity
//        Bundle extras = getIntent().getExtras();
//        if(extras != null)
//        {
//            deviceToken = extras.getString("device token");
//            token = extras.getString("token");

            //start the RefreshRoomService to update the room details
//            displayProgressDialog();
//            startAutoRefreshServices(deviceToken, token);
        //}

        //calculates and update the meeting and current time in every seconds
        Runnable myRunnableThread = new CountDownRunner();
        timeThread= new Thread(myRunnableThread);
        timeThread.start();
    }

    private String getDevToken(){
        Context context = getApplicationContext();
        SharedPreferences pref = context.getSharedPreferences("roomee_prefs", MODE_WORLD_READABLE);
        deviceToken = pref.getString("device_token", null);
        return deviceToken;
    }

    /**
     * this method shows a progress dialog of loading room details
     */
    private void displayProgressDialog()
    {
        dialog = new ProgressDialog(RoomDetailsActivity.this);
        this.dialog.setMessage(dialogMessage);
        this.dialog.show();
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.setCancelable(false);
    }

    /**
     * this class implements runnable class and handles current time counting
     */
    class CountDownRunner implements Runnable{
        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    countsTime();
                    Thread.sleep(1000); // Pause of 1 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }catch(Exception e){
                }
            }
        }
    }

    /**
     * this methods get the current time and calculate the
     * time difference between next meeting in every seconds
     */
    public void countsTime() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    //display the current time to the header of the view
                    TextView txtCurrentTime= (TextView)findViewById(R.id.txtCurrentTime);
                    txtCurrentTime.setText(tc.getCurrentTime(null));

                    //calculate and update the meeting time
                    if(room.getTime() != null) {
                        String timeDiff = tc.calculateTimeDiff(room.getTime());
                        txtTime.setText(timeDiff);
                    }

                    //update meeting table clock line in every second
                    if(mtHandler!=null) mtHandler.addTimeline(timeline);

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * this method find all the view components of the room details activity
     */
    private void findViewComponents()
    {
        //find parent layout
        meetingDetailsDrawerLayout = (DrawerLayout) findViewById(R.id.meeting_details_drawer_layout);

        //find children layout
        meetingDetailsLayout = (LinearLayout) findViewById(R.id.meeting_details_layout);
        headerLinerLayout = (LinearLayout) findViewById(R.id.header);
        roomInfoLinerLayout = (LinearLayout) findViewById(R.id.roomInfo);
        timeInfoLinerLayout = (LinearLayout) findViewById(R.id.timeInfo);
        meetingInfoLayout = (RelativeLayout) findViewById(R.id.meetingInfo);

        //get individual view component
        txtRoomName = (TextView) findViewById(R.id.txtRoomName);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtTime = (TextView) findViewById(R.id.txtTime);
        btnEndMeeting = (Button) findViewById(R.id.btnEndMeeting);
        btnExtendMeeting = (Button) findViewById(R.id.btnExtendMeeting);
        timeline = (TextView) findViewById(R.id.timeMinutesHand);

        //get drawer layout
        meetingListDrawerLayout = (LinearLayout) findViewById(R.id.meetingListDrawerLayout);
    }

    /**
     * this method displays meeting room details
     */
    private void displayItems() {

        //set room details background upon the room status
        if(room != null)
        {
            if(room.getStatus() == 1)
            {
                //if room status is busy, then set colors into red
                headerLinerLayout.setBackgroundColor(getResources().getColor(R.color.header_red));
                roomInfoLinerLayout.setBackgroundColor(getResources().getColor(R.color.room_red));
                timeInfoLinerLayout.setBackgroundColor(getResources().getColor(R.color.time_red));
                meetingInfoLayout.setBackgroundColor(getResources().getColor(R.color.time_red));
                btnExtendMeeting.setVisibility(View.VISIBLE);
                btnEndMeeting.setVisibility(View.VISIBLE);
                txtStatus.setText("Busy for");
            }
            else
            {
                //if room status is free
                headerLinerLayout.setBackgroundColor(getResources().getColor(R.color.header_green));
                roomInfoLinerLayout.setBackgroundColor(getResources().getColor(R.color.room_green));
                timeInfoLinerLayout.setBackgroundColor(getResources().getColor(R.color.time_green));
                meetingInfoLayout.setBackgroundColor(getResources().getColor(R.color.time_green));
                btnExtendMeeting.setVisibility(View.INVISIBLE);
                btnEndMeeting.setVisibility(View.INVISIBLE);
                txtStatus.setText("Free for");
            }

            //set value to the other room components
            txtRoomName.setText(room.getName());
        }
    }

    /**
     * this method displays meeting timetable and its meeting details
     */
    private void displayMeetingdetails() {

        //initialise meeting table handler
        mtHandler = new MeetingTableHandler(RoomDetailsActivity.this, meetingInfoLayout);
        mtHandler.eraseMeetingTableView();
        //set meeting table details
        mtHandler.setMeetingTableHeader();
        mtHandler.setMeetingTimeLineHeader(room.getStatus());
        mtHandler.setMeetingTimeLineFooter(room.getStatus());
        mtHandler.addMeetingToTimeTable(meetingList, room.getStatus());
    }

    /**
     * this method handles add new meeting button onClick event
     * @param view, Image Button
     */
    public void addNewMeetingOnClick(View view)
    {
        Intent intent = new Intent(getApplicationContext(), AddMeetingActivity.class);
        intent.putExtra("device token", deviceToken);
        intent.putExtra("room", room);
        intent.putParcelableArrayListExtra("meetingList", meetingList);
        startActivity(intent);
    }

    /**
     * this method handles add searching meeting button onClick event
     * @param view, Image Button
     */
    public void searchMeetingOnClick(View view)
    {
        meetingDetailsDrawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_details, menu);
        return false;
    }

    /**
     * this method calls the RefreshRoomService and start the service
     * @param deviceToken, String Device Token
     */
    private void startAutoRefreshServices(String deviceToken) {
        if(isMyServiceRunning() == true)
        {
            //start download file by sending broadcast intent to MyService
                Intent i = new Intent();
                i.putExtra("device token", deviceToken);
                i.setAction(BROADCAST);
                sendBroadcast(i);
        }
        else
        {
            Intent i = new Intent(RoomDetailsActivity.this, RefreshRoomService.class);
            i.putExtra("device token", deviceToken);
            startService(i);
        }
    }

    /**
     * this method checks whether MyService is running or not
     * @return false if service is not running
     */
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RefreshRoomService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /*************************************
     ** manages the activity life cycle **
     ************************************/

    /**
     * this method manages activity when it restart from onStop status
     */
    @Override
    public void onRestart() {
        super.onRestart();
        startAutoRefreshServices(deviceToken);
    }

    /**
     * this method is called when the activity interacting with the user
     */
    @Override
    public void onResume()
    {
        super.onResume();
        // intent to filter for auto-refresh broadcast message
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AUTOREFRESH);
        //register the receiver
        registerReceiver(AutoRefreshReceiver, intentFilter);
    }

    /**
     * this method manages activity of onPause situation, when a semi-transparent activity opens
     */
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(AutoRefreshReceiver);
    }

    /**
     * this method manages the actions before the activity is destroyed
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        timeThread.interrupt();
        stopService(new Intent(RoomDetailsActivity.this, RefreshRoomService.class));
    }

    /**
     * this class implements the BroadcastReceiver message that is send by the MyService
     */
    private BroadcastReceiver AutoRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //lazy init instance
            if(room == null || meetingList == null) {
                meetingList = new ArrayList<Meeting>();
                room = new Room();
            }

            //if receives auto refresh action request it will update the view automatically
            if(intent.getAction().equals(AUTOREFRESH))
            {
                displayRefreshServiceMessage(intent);
            }
            //dismiss the loading dialog
            if(dialog.isShowing()) dialog.dismiss();
        }

        /**
         * this method displays the data send by the RefreshRoomService
         * @param intent, Android Intent
         */
        private void displayRefreshServiceMessage(Intent intent)
        {
            String message = intent.getStringExtra("message");
            if(!message.equals("success"))
            {
                if(done!= false)invalidMessage(message);
                done = false;
            }
            else
            {
                done = true;
                meetingList = intent.getParcelableArrayListExtra("meetingList");
                room = (Room) intent.getSerializableExtra("room");

                //update the room and meeting timetable details to the view
                displayItems();
                displayMeetingdetails();

                //update meeting list for the searching meeting drawer
                mlDrawer.addMeetingList(meetingList);
            }
        }

    };

    /**
     * this method displays invalid token message
     * @param message, string message
     */
    private void invalidMessage(String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle("Error");
        alertDialog.setMessage("\n"+message+"\n");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                done = true;
            }
        });
        alertDialog.show();
    }

}
