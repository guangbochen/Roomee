package com.vivant.roomee.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import com.vivant.roomee.json.JSONParser;
import com.vivant.roomee.json.JSONParserImpl;
import com.vivant.roomee.model.Constants;
import com.vivant.roomee.model.Meeting;
import com.vivant.roomee.model.Room;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by guangbo on 11/11/13.
 */
public class RefreshRoomService extends Service {

    private final static String AUTOREFRESH = "com.vivant.roomee.services.autoRefresh";
    private final static String BROADCAST = "com.vivant.roomee.startRoomService";
    private IntentFilter mIntentFilter;
    private Room room;
    private ArrayList<Meeting> meetingList;
    private JSONParser jsonParser;
    private static String roomId;
    private static String token;

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
    }

    /**
     * this method is called when service is start
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getExtras() != null)
        {
            roomId = intent.getStringExtra("roomId");
            token = intent.getStringExtra("token");

            //start the auto refresh counter
            Runnable myRunnableThread = new AutoRefreshRoomdetails();
            Thread timeThread =  new Thread(myRunnableThread);
            timeThread.start();
        }
        return START_STICKY;
    }

    /**
     * this method stop the music player and stop the service once onDestroy is called
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        unregisterReceiver(mBroadcastReceiver);
    }


    /**
     * this class implements runnable class and handles current time counting
     */
    class AutoRefreshRoomdetails implements Runnable{
        // @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    Log.d("TEST", "Thread sleep 30 sec.");
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
                roomId = intent.getStringExtra("roomId");
                token = intent.getStringExtra("token");
                new DownloadThread().execute();
            }
        }
    };

    /**
     * This class implements AsyncTask to manages the sleep thread
     */
    private class DownloadThread extends AsyncTask<Integer, Integer, Long> {
        @Override
        protected Long doInBackground(Integer... arg0) {
            //crate Json parser instance
            jsonParser = new JSONParserImpl();
            meetingList = new ArrayList<Meeting>();

            //getting json string from url
            String url = "rooms/"+ roomId +"?oauth_token="+ token;
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
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * this method sends a broadcast message once finished parsing the JSON data
         */
        @Override
        protected void onPostExecute(Long Interval) {
            //once the request is complete it sends a broadcast message
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("meetingList", meetingList);
            intent.putExtra("room", room);
            intent.setAction(AUTOREFRESH);
            sendBroadcast(intent);
        }
    }

}
