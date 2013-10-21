package com.vivant.roomee;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.vivant.roomee.json.JSONParser;
import com.vivant.roomee.model.Meeting;
import com.vivant.roomee.model.Room;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomDetailsActivity extends Activity {

    //JSON Node names;
    private final static String TAG_STATUS = "status";
    private final static String TAG_MESSAGE = "message";
    private final static String TAG_DATA = "data";
    private final static String TAG_ROOM = "room";
    private final static String TAG_ID = "id";
    private final static String TAG_NAME = "name";
    private final static String TAG_FREEBUSY = "freebusy";
    private final static String TAG_MEETINGS = "meetings";
    private final static String TAG_SUMMARY = "summary";
    private final static String TAG_CREATOR = "creator";
    private final static String TAG_START = "start";
    private final static String TAG_END = "end";
    private final static String TAG_TIME = "time";
    private static String roomId;
    private static String token;
    private Room room;
    private List<Meeting> meetingList = new ArrayList<Meeting>();

    private ImageView imageSearch;
    private ImageView imageAdd;
    private LinearLayout headerLinerLayout;
    private LinearLayout roomInfoLinerLayout;
    private LinearLayout timeInfoLinerLayout;
    private TextView txtStatus;
    private TextView txtRoomName;
    private TextView txtTime;
    private Button btnEndMeeting;
    private Button btnExtendMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        //retrieve data passed from main activity
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            int status = extras.getInt("id");
            roomId = String.valueOf(status);
            token = extras.getString("token");
            new ProgressTask(RoomDetailsActivity.this).execute();
        }


        //get the current time and updates in every sec.
        Thread timeThread = null;
        Runnable myRunnableThread = new CountDownRunner();
        timeThread= new Thread(myRunnableThread);
        timeThread.start();
    }

    private void displayItems() {

        //get layout component
        headerLinerLayout = (LinearLayout) findViewById(R.id.header);
        roomInfoLinerLayout = (LinearLayout) findViewById(R.id.roomInfo);
        timeInfoLinerLayout = (LinearLayout) findViewById(R.id.timeInfo);

        //get each view component
        txtRoomName = (TextView) findViewById(R.id.txtRoomName);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtTime = (TextView) findViewById(R.id.txtTime);
        imageSearch = (ImageView) findViewById(R.id.imgSearch);
        imageAdd = (ImageView) findViewById(R.id.imgAdd);
        btnEndMeeting = (Button) findViewById(R.id.btnEndMeeting);
        btnExtendMeeting = (Button) findViewById(R.id.btnExtendMeeting);

        //set room details background upon the room status
            //if room status is busy, then set bgcolor into red
        if(room != null)
        {
            txtRoomName.setText(room.getName());
            if(room.getStatus() == 1)
            {
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
            }
            // Apply RFC3339 format using JODA-TIME
//            DateTime dateTime = new DateTime("2013-07-04T23:37:46.782Z", DateTimeZone.UTC);
//            DateTimeFormatter dateFormatter = ISODateTimeFormat.dateTime();
//            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
//            String time = sdf.format(room.getTime());
//            txtTime.setText(time);
            txtTime.setText(room.getTime());
        }
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
     * this methods calculates the current time and updates the time to the textView
     */
    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    TextView txtCurrentTime= (TextView)findViewById(R.id.txtCurrentTime);
                    Date dt = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                    String todayStr = sdf.format(dt);
                    txtCurrentTime.setText(todayStr);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * this progressTask class sends http request and it returns the json data of the rooms
     */
    private class ProgressTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;
        private Activity activity;
        private Context context;

        public ProgressTask(Activity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            this.dialog.setMessage(" Loading room meetings... ");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            //crate Json parser instance
            JSONParser jsonParser = new JSONParser();

            //getting json string from url
//            String url = "rooms?oauth_token=" + extras.getString("token");
            String url = "rooms/"+ roomId +"?oauth_token="+ token;
            JSONObject json = jsonParser.getJSONFromUrl(url);
            try{

                String HttpStatus = json.getString(TAG_STATUS);
                if(HttpStatus.equals("success"))
                {
                    JSONObject data = json.getJSONObject(TAG_DATA);

                    //retrieves room data and saves into room object
                    JSONObject jRoom = data.getJSONObject(TAG_ROOM);
                    int id = Integer.parseInt(jRoom.getString(TAG_ID));
                    String name = jRoom.getString(TAG_NAME);
                    int freeBusy = Integer.parseInt(jRoom.getString(TAG_FREEBUSY));
                    String time = jRoom.getString(TAG_TIME);
                    room = new Room(id,name,freeBusy,time);

                    //retrieves array of meetings and add to the list
                    JSONArray meetings = jRoom.getJSONArray(TAG_MEETINGS);
                    for(int i=0; i < meetings.length(); i++)
                    {
                        JSONObject jMeeting = meetings.getJSONObject(i);
                        String summary = jMeeting.getString(TAG_SUMMARY);
                        String creator = jMeeting.getString(TAG_CREATOR);
                        String start = jMeeting.getString(TAG_START);
                        String end = jMeeting.getString(TAG_END);
                        Meeting meeting = new Meeting(summary, creator, start, end);
                        meetingList.add(meeting);
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

            //update the room details to the view
            displayItems();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_details, menu);
        return true;
    }
    
}
