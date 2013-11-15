package com.vivant.roomee;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.vivant.roomee.handler.MeetingTableHandler;
import com.vivant.roomee.json.JSONParser;
import com.vivant.roomee.json.JSONParserImpl;
import com.vivant.roomee.model.Constants;
import com.vivant.roomee.model.Meeting;
import com.vivant.roomee.model.Room;
import com.vivant.roomee.navigationDrawer.MeetingListDrawer;
import com.vivant.roomee.navigationDrawer.MeetingListDrawerImpl;
import com.vivant.roomee.services.RefreshRoomService;
import com.vivant.roomee.timeManager.TimeCalculator;
import com.vivant.roomee.timeManager.TimeCalculatorImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class RoomDetailsActivity extends Activity {

    private static String roomId;
    private static String token;
    private Room room;
    private ArrayList<Meeting> meetingList;
    private MeetingTableHandler meetingTableHandler;
    private TimeCalculator tc;
    private ProgressDialog dialog;
    private MeetingListDrawer mlDrawer;
    //layout components for room details
    private LinearLayout meetingDetailsLayout;
    private LinearLayout headerLinerLayout;
    private LinearLayout roomInfoLinerLayout;
    private LinearLayout timeInfoLinerLayout;
    private LinearLayout meetingInfoLinerLayout;
    private LinearLayout meetingListDrawerLayout;
    private DrawerLayout meetingDetailsDrawerLayout;
    //view components for room details
    private TextView txtStatus;
    private TextView txtRoomName;
    private TextView txtTime;
    private Button btnEndMeeting;
    private Button btnExtendMeeting;
    private TextView txtClockLine;
    private final static String BROADCAST = "com.vivant.roomee.services.broadcast";
    private final static String AUTOREFRESH = "com.vivant.roomee.services.autoRefresh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        //find all of the view components in the RoomDetailsActivity
        findViewComponents();

        //initialise instances
        tc = new TimeCalculatorImpl();
        meetingList = new ArrayList<Meeting>();
        room = new Room();
        mlDrawer = new MeetingListDrawerImpl(RoomDetailsActivity.this, meetingListDrawerLayout);

        //retrieve data passed from RoomListActivity
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            roomId = extras.getString("id");
            token = extras.getString("token");

            //start the RefreshRoomService to update the room details
            displayProgressDialog();
            startAutoRefreshServices(roomId, token);
        }

        //calculates and update the meeting and current time in every seconds
        Runnable myRunnableThread = new CountDownRunner();
        Thread timeThread= new Thread(myRunnableThread);
        timeThread.start();
    }

    /**
     * this method shows a progress dialog of loading room details
     */
    private void displayProgressDialog()
    {
        dialog = new ProgressDialog(RoomDetailsActivity.this);
        this.dialog.setMessage(" Loading room details ... ");
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

                    //update meeting table clock line in every second
                    if(meetingTableHandler!=null) meetingTableHandler.setClockMinutesHand(txtClockLine);

                    //calculate and update the meeting time
                    if(room != null)
                    {
                        String timeDiff = tc.calculateTimeDiff(room.getTime());
                        txtTime.setText(timeDiff);
                    }

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
        meetingInfoLinerLayout = (LinearLayout) findViewById(R.id.meetingInfo);

        //get individual view component
        txtRoomName = (TextView) findViewById(R.id.txtRoomName);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtTime = (TextView) findViewById(R.id.txtTime);
        btnEndMeeting = (Button) findViewById(R.id.btnEndMeeting);
        btnExtendMeeting = (Button) findViewById(R.id.btnExtendMeeting);
        txtClockLine = (TextView) findViewById(R.id.timeMinutesHand);

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
                meetingInfoLinerLayout.setBackgroundColor(getResources().getColor(R.color.time_red));
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
                meetingInfoLinerLayout.setBackgroundColor(getResources().getColor(R.color.time_green));
                btnExtendMeeting.setVisibility(View.INVISIBLE);
                btnEndMeeting.setVisibility(View.INVISIBLE);
                txtStatus.setText("Free for");
            }

            //set value to the other room components
            txtRoomName.setText(room.getName());
//            //calculate the time diff via timeCalculator class
//            String timeDiff = tc.calculateTimeDiff(room.getTime());
//            txtTime.setText(timeDiff);
        }
    }

    /**
     * this method displays meeting timetable and its meeting details
     */
    private void displayMeetingdetails() {

        //initialise meeting table handler
        meetingTableHandler = new MeetingTableHandler(RoomDetailsActivity.this, meetingInfoLinerLayout);
        meetingTableHandler.eraseMeetingTableView();
        //set meeting table details
        meetingTableHandler.addMeetingToTimeTable(meetingList);
        meetingTableHandler.setMeetingTableHeader();
        meetingTableHandler.setMeetingTimeLineHeader(room.getStatus());
        meetingTableHandler.setMeetingTimeLineFooter(room.getStatus());
        meetingTableHandler.setClockMinutesHand(txtClockLine);
    }

    /**
     * this method handles add new meeting button onClick event
     * @param view, Image Button
     */
    public void addNewMeetingOnClick(View view)
    {
        Intent intent = new Intent(getApplicationContext(), AddMeetingActivity.class);
        intent.putExtra("token", token);
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
//        if(!meetingDetailsDrawerLayout.isDrawerOpen(Gravity.LEFT))
//        {
//            Log.d("TEST", "open");
//            meetingDetailsDrawerLayout.openDrawer(Gravity.LEFT);
//            meetingDetailsLayout.setPadding(250,0,-250,0);
//        }
//        meetingDetailsLayout.setPadding(-250,0,250,0);
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
     * @param roomId, String room id
     * @param token, String authentication token
     */
    private void startAutoRefreshServices(String roomId, String token) {
        Intent serviceIntent = new Intent(RoomDetailsActivity.this, RefreshRoomService.class);
        serviceIntent.putExtra("roomId", roomId);
        serviceIntent.putExtra("token", token);
        startService(serviceIntent);
    }

    /**
     * this method is called when the activity interacting with the user
     */
    @Override
    public void onResume()
    {
        super.onResume();
        // intent to filter for AUTOREFRESH broadcast message
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AUTOREFRESH);
        //register the receiver
        registerReceiver(AutoRefreshReceiver, intentFilter);
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(AutoRefreshReceiver);
    }


    /**
     * this class implements the BroadcastReceiver message that is send by the MyService
     */
    private BroadcastReceiver AutoRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //if receives auto refresh action request it will update the view automatically
            if(intent.getAction().equals(AUTOREFRESH))
            {
                Log.d(Constants.MAD, "download completed and start auto refresh");
                meetingList = intent.getParcelableArrayListExtra("meetingList");
                room = (Room) intent.getSerializableExtra("room");

                //update the room and meeting timetable details to the view
                displayItems();
                displayMeetingdetails();

                //display meeting list in the left drawer
                mlDrawer.addMeetingList(meetingList);

                //dismiss the loading dialog
                if(dialog.isShowing()) dialog.dismiss();
            }
        }
    };
}
