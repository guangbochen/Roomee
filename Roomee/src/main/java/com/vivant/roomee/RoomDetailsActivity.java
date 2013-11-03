package com.vivant.roomee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import com.vivant.roomee.handler.MeetingTableHandler;
import com.vivant.roomee.json.JSONParser;
import com.vivant.roomee.json.JSONParserImpl;
import com.vivant.roomee.model.Constants;
import com.vivant.roomee.model.Meeting;
import com.vivant.roomee.model.Room;
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
    private ArrayList<Meeting> meetingList = new ArrayList<Meeting>();
    private JSONParser jsonParser;
    private MeetingTableHandler meetingTableHandler;
    private TimeCalculator tc;

    //view components
    private LinearLayout headerLinerLayout;
    private LinearLayout roomInfoLinerLayout;
    private LinearLayout timeInfoLinerLayout;
    private TextView txtStatus;
    private TextView txtRoomName;
    private TextView txtTime;
    private Button btnEndMeeting;
    private Button btnExtendMeeting;
    private TableLayout meetingTableHeader;
    private TableLayout meetingTableTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        //initialise timeCalculator instance
        tc = new TimeCalculatorImpl();

        //get every view components in the RoomDetails activity
        getViewComponents();

        //retrieve data passed from main activity
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            int id = extras.getInt("id");
            roomId = String.valueOf(id);
            token = extras.getString("token");

            //call ProgressRoomDetails to synchronise meeting room details with remote server
            new ProgressRoomDetails(RoomDetailsActivity.this).execute();
        }

        //get the current time and updates in every sec.
        Thread timeThread = null;
        Runnable myRunnableThread = new CountDownRunner();
        timeThread= new Thread(myRunnableThread);
        timeThread.start();


    }


    /**
     * this class implements runnable class and handles current time counting
     */
    class CountDownRunner implements Runnable{
        // @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    doWork();
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
    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    //get and set current time to the header view
                    TextView txtCurrentTime= (TextView)findViewById(R.id.txtCurrentTime);
                    txtCurrentTime.setText(tc.getCurrentTime());

                    //calculate the time diff via timeCalculator class
                    String timeDiff = tc.CalculateTimeDif(room.getTime());
                    txtTime.setText(timeDiff);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * this progressTask class sends http request and it returns the json data of the rooms
     */
    private class ProgressRoomDetails extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;
        private Activity activity;
        private Context context;

        public ProgressRoomDetails(Activity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

        /**
         * onPreExecute initalise AuthenticationTask before it starts
         */
        protected void onPreExecute() {
            this.dialog.setMessage(" Loading room details... ");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            //crate Json parser instance
            jsonParser = new JSONParserImpl();

            //getting json string from url
            String url = "rooms/"+ roomId +"?oauth_token="+ token;
//            String url = "http://www.json-generator.com/j/bUIYPQhxYO?indent=4";
            JSONObject json = jsonParser.getJSONFromUrl(url);
            try{
                if(json != null)
                {
                    String HttpStatus = json.getString(Constants.TAG_STATUS);
                    if(HttpStatus.equals("success"))
                    {
                        JSONObject data = json.getJSONObject(Constants.TAG_DATA);

                        //retrieves room data and saves into room object
                        JSONObject jRoom = data.getJSONObject(Constants.TAG_ROOM);
                        int id = Integer.parseInt(jRoom.getString(Constants.TAG_ID));
                        String name = jRoom.getString(Constants.TAG_NAME);
                        int freeBusy = Integer.parseInt(jRoom.getString(Constants.TAG_FREEBUSY));
                        String time = jRoom.getString(Constants.TAG_TIME);
                        room = new Room(id,name,freeBusy,time);

                        //retrieves array of meetings and add to the list
                        JSONArray meetings = jRoom.getJSONArray(Constants.TAG_MEETINGS);
                        for(int i=0; i < meetings.length(); i++)
                        {
                            JSONObject jMeeting = meetings.getJSONObject(i);
                            String summary = jMeeting.getString(Constants.TAG_SUMMARY);
                            String creator = jMeeting.getString(Constants.TAG_CREATOR);
                            String start = jMeeting.getString(Constants.TAG_START);
                            String end = jMeeting.getString(Constants.TAG_END);
                            Meeting meeting = new Meeting(summary, creator, start, end);
                            meetingList.add(meeting);
                        }
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            //dismiss the loading dialog
            if(dialog.isShowing()) dialog.dismiss();

            //update the room and meeting timetable details to the view
            displayItems();
            displayMeetingdetails();
        }
    }


    /**
     * this method displays meeting timetable and its meeting details
     */
    private void displayMeetingdetails() {

        //displays meeting time table
        meetingTableHeader = (TableLayout) findViewById(R.id.meetingTableHeader);
        meetingTableTime = (TableLayout) findViewById(R.id.meetingTableTime);
        meetingTableHandler = new MeetingTableHandler(RoomDetailsActivity.this, meetingTableHeader,meetingTableTime);
        meetingTableHandler.setMeetingTableHeader();
        meetingTableHandler.setMeetingTableTimeZone();
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
                btnExtendMeeting.setVisibility(View.VISIBLE);
                btnEndMeeting.setVisibility(View.VISIBLE);
                txtStatus.setText("Busy for");
            }
            else {
                //if room status is free
                headerLinerLayout.setBackgroundColor(getResources().getColor(R.color.header_green));
                roomInfoLinerLayout.setBackgroundColor(getResources().getColor(R.color.room_green));
                timeInfoLinerLayout.setBackgroundColor(getResources().getColor(R.color.time_green));
                btnExtendMeeting.setVisibility(View.INVISIBLE);
                btnEndMeeting.setVisibility(View.INVISIBLE);
                txtStatus.setText("Free for");
            }

            //set value to the other room components
            txtRoomName.setText(room.getName());
            //calculate the time diff via timeCalculator class
            String timeDiff = tc.CalculateTimeDif(room.getTime());
            txtTime.setText(timeDiff);
        }
    }


    private void getViewComponents()
    {
        //get layout component
        headerLinerLayout = (LinearLayout) findViewById(R.id.header);
        roomInfoLinerLayout = (LinearLayout) findViewById(R.id.roomInfo);
        timeInfoLinerLayout = (LinearLayout) findViewById(R.id.timeInfo);

        //get each view component
        txtRoomName = (TextView) findViewById(R.id.txtRoomName);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtTime = (TextView) findViewById(R.id.txtTime);
        btnEndMeeting = (Button) findViewById(R.id.btnEndMeeting);
        btnExtendMeeting = (Button) findViewById(R.id.btnExtendMeeting);
    }


    public void addNewMeetingOnClick(View view)
    {
        Intent intent = new Intent(getApplicationContext(), AddMeetingActivity.class);
        intent.putExtra("token", token);
        intent.putExtra("roomId", room.getId());
        intent.putExtra("room", room);
        intent.putParcelableArrayListExtra("meetingList", meetingList);
        startActivity(intent);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_details, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("id", roomId);
        savedInstanceState.putString("token", token);
        super.onSaveInstanceState(savedInstanceState);
    }
    
}
