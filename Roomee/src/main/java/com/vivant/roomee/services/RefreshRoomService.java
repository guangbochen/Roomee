package com.vivant.roomee.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vivant.roomee.json.JSONParser;
import com.vivant.roomee.json.JSONParserImpl;
import com.vivant.roomee.json.Util;
import com.vivant.roomee.model.Constants;
import com.vivant.roomee.model.Meeting;
import com.vivant.roomee.model.Room;
import com.vivant.roomee.timeManager.TimeCalculator;
import com.vivant.roomee.timeManager.TimeCalculatorImpl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * This is Android Service that manages to auto refresh the room and meeting data and send
 * Broadcast message to the RoomDetails Activity to update the room and meeting contents
 * Created by guangbo on 11/11/13.
 */
public class RefreshRoomService extends Service {

    private final static String AUTOREFRESH = "com.vivant.roomee.services.autoRefresh";
    private final static String BROADCAST = "com.vivant.roomee.startRoomService";
    private IntentFilter mIntentFilter;
    private Room room;
    private ArrayList<Meeting> meetingList;
    private JSONParser jsonParser;
    private String deviceToken;
    private static String token;
    private Thread timeThread;
    private TimeCalculator tc;
    private Date date;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * this method initialize the server before onStart is called
     */
    @Override
    public void onCreate()
    {
        //register the broadcast receiver
        meetingList = new ArrayList<Meeting>();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BROADCAST);
        registerReceiver(mBroadcastReceiver,mIntentFilter);
        tc = new TimeCalculatorImpl(); // used in process of checking if meetings today
    }

    /**
     * this method is called when service is start
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//            //start the auto refresh counter
        Runnable myRunnableThread = new AutoRefreshRoomdetails();
        timeThread =  new Thread(myRunnableThread);
        timeThread.start();
        return START_STICKY;
    }

    /**
     * this method destroy the service once it is on called
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        timeThread.interrupt();
        unregisterReceiver(mBroadcastReceiver);
    }

    public String getDevToken(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        deviceToken = preferences.getString("device_token", "device token not retrieved");
        return deviceToken;

//        SharedPreferences myPrefs = this.getSharedPreferences("roomee prefs", MODE_WORLD_READABLE);

//
//        Context context = getApplicationContext();
//        SharedPreferences pref = context.getSharedPreferences("roomee_prefs", 0);
//        String answer = pref.getString("device_token", "poop");
//        if(answer!=null){
//            deviceToken = answer;
//        }
//        return deviceToken;
    }

    /**
     * this class implements runnable class and handles current time counting
     */
    class AutoRefreshRoomdetails implements Runnable{
        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    Log.d("Services", "Thread sleep 30 sec.");
                    new DownloadThread().execute();
                    Thread.sleep(30000); // Pause of 30 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }catch(Exception e){
                }
            }
        }
    }

    /**
     * This class implements the Broadcast receiver and pull data from web service
     */
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BROADCAST) && intent.getExtras() != null)
            {
                deviceToken = intent.getStringExtra("device token");
//                token = intent.getStringExtra("token");
                new DownloadThread().execute();
            }
        }
    };

    /**
     * This class implements AsyncTask to manages the sleep thread
     */
    private class DownloadThread extends AsyncTask<Integer, Integer, Long> {
        private String message;

        /**
         * onPreExecute initialises DownloadThread before it starts
         */
        protected void onPreExecute() {
            this.message = Constants.NOINTERNET;
        }

        @Override
        protected Long doInBackground(Integer... arg0) {
            //crate Json parser instance
            jsonParser = new JSONParserImpl();
            meetingList = new ArrayList<Meeting>();
            JSONObject json = new JSONObject();
            //getting json string from url
            String url = "room/"+ "?device_token=" + deviceToken;
            json = jsonParser.getJSONFromUrl(url);
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

                              Date date = Util.parseRFC3339Date(start);
                            Log.d("REFRESH PARSE", date.toString());
//                            Date date = (new SimpleDateFormat(start));
//                            Log.d("DATE CONVERSION", date.toString());
//                            Stringmanager = new StringConvert();
//                            String startDate = convertToString(iso, start);
//                            Date startDate = parse3339(start);
//                            Date startDate = tc.parseStringToDate(start);

                            if(checkMeetingToday(date)){
                            Meeting meeting = new Meeting(summary, creator, start, end);
                            meetingList.add(meeting);
                            }
                        }
                        message = HttpStatus;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private boolean checkMeetingToday(Date startTime){

            date = new Date();
            //validate start meeting time
            if((startTime.getDate() < date.getDate() || startTime.getDate() > date.getDate()))
            {
                return false;
            }
            else
            {
                return true;
                //converting regular meeting time to RFC3339 format
//                startTime =  tc.getRFCDateFormat(startDate);
//                endTime =  tc.getRFCDateFormat(endDate);
            }
        }

        /**
         * this method sends a broadcast message once finished parsing the JSON data
         */
        @Override
        protected void onPostExecute(Long Interval) {
            //once the request is complete it sends a broadcast message
            Intent intent = new Intent();
            intent.putExtra("message", message);
            intent.putParcelableArrayListExtra("meetingList", meetingList);
            intent.putExtra("room", room);
            intent.setAction(AUTOREFRESH);
            sendBroadcast(intent);
        }
    }

}
