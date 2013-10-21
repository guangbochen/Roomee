package com.vivant.roomee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.vivant.roomee.adapter.RoomListAdapter;
import com.vivant.roomee.json.JSONParser;
import com.vivant.roomee.model.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends Activity implements OnItemClickListener {

    //JSON Node names;
    private final static String TAG_STATUS = "status";
    private final static String TAG_MESSAGE = "message";
    private final static String TAG_DATA = "data";
    private final static String TAG_ROOMS = "rooms";
    private final static String TAG_ID = "id";
    private final static String TAG_NAME = "name";
    private final static String TAG_FREEBUSY = "freebusy";
    private final static String TAG_TIME = "time";
    private JSONParser jsonParser;
    private ListView roomListView;
    private List<Room> roomList = new ArrayList<Room>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        //validates the oauth_token is exist
//        Bundle extras = getIntent().getExtras();
//        if(extras != null) {
//        }
        roomListView = (ListView) findViewById(R.id.roomListView);

        //get room json data via calling the progressTask
        new ProgressTask(RoomListActivity.this).execute();
        roomListView.setOnItemClickListener(this);

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
            this.dialog.setMessage("Loading rooms...");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            //crate Json parser instance
            jsonParser = new JSONParser();

            //getting json string from url
//            String url = "rooms?oauth_token=" + extras.getString("token");
            String url = "rooms?oauth_token=e24cda222194876faaba860416f6ef126d328639";
            JSONObject json = jsonParser.getJSONFromUrl(url);
            try{

                String HttpStatus = json.getString(TAG_STATUS);
                if(HttpStatus.equals("success"))
                {
                    JSONObject data = json.getJSONObject(TAG_DATA);
                    JSONArray rooms = data.getJSONArray(TAG_ROOMS);
                    for(int i=0; i < rooms.length(); i++)
                    {
                        JSONObject JSONRoom = rooms.getJSONObject(i);
                        int id = Integer.parseInt(JSONRoom.getString(TAG_ID));
                        String name = JSONRoom.getString(TAG_NAME);
                        int freeBusy = Integer.parseInt(JSONRoom.getString(TAG_FREEBUSY));
                        String time = JSONRoom.getString(TAG_TIME);
                        Room room = new Room(id,name,freeBusy,time);
                        roomList.add(room);
                    }
                    //addRoomToListView();
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

            //update the room items to the list view
            RoomListAdapter adapter = new RoomListAdapter(activity,roomList);
            roomListView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_list, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(RoomListActivity.this,RoomDetailsActivity.class);
        Room room = roomList.get(position);
        intent.putExtra("id", room.getId());
        intent.putExtra("token", "e24cda222194876faaba860416f6ef126d328639");
        startActivity(intent);

    }

    @Override
    public void onResume() {
        super.onResume();
        //get room json data via calling the progressTask
//        roomList = new ArrayList<Room>();
//        new ProgressTask(RoomListActivity.this).execute();
    }
}
