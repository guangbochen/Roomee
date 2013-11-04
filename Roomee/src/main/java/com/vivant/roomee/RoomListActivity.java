package com.vivant.roomee;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.vivant.roomee.adapter.RoomListAdapter;
import com.vivant.roomee.json.JSONParser;
import com.vivant.roomee.json.JSONParserImpl;
import com.vivant.roomee.model.Constants;
import com.vivant.roomee.model.Room;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends Activity implements OnItemClickListener {

    private boolean done;
    private JSONParser jsonParser;
    private ListView roomListView;
    private List<Room> roomList = new ArrayList<Room>();
    private String token;

    /**
     * onCreate method initialise the view of RoomListActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_room_list);

        //set custom title for the main activity
        ActionBar ab = getActionBar();
        ab.setTitle("Welcome to Roomee");
        ab.setDisplayShowTitleEnabled(true);

        done = false;

        //validates the oauth_token is exist
        Bundle extras = getIntent().getExtras();
        if(extras != null) {

            roomListView = (ListView) findViewById(R.id.roomListView);
            token = extras.getString("token");

            //access Roomee web service server and retrieve json data
            // for meeting rooms via calling the progressTask
            new ProgressTask(RoomListActivity.this, token).execute();
            roomListView.setOnItemClickListener(this);

        }

    }

    /**
     * this progressTask class sends http request and it returns the json data of the rooms
     */
    private class ProgressTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;
        private Activity activity;
        private Context context;
        private String token;

        /**
         * ProgressTask constructor
         */
        public ProgressTask(Activity activity, String token) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
            this.token = token;
        }

        /**
         * onPreExecute initialise ProgressTask before it starts
         */
        protected void onPreExecute() {
            this.dialog.setMessage("Loading rooms...");
            this.dialog.show();
        }

        /**
         * doInBackground method parse the json data that is
         * returned from Roomee web service server
         */
        @Override
        protected Boolean doInBackground(String... strings) {

            //crate Json parser instance
            jsonParser = new JSONParserImpl();

            //add token to the url
            String url = "rooms?oauth_token=" +token;
//            String url = "http://www.json-generator.com/j/bTwZCVTxiW?indent=4";
            try{
                JSONObject json = jsonParser.getJSONFromUrl(url);
                String HttpStatus = json.getString(Constants.TAG_STATUS);
                if(HttpStatus.equals("success"))
                {
                    JSONObject data = json.getJSONObject(Constants.TAG_DATA);
                    JSONArray rooms = data.getJSONArray(Constants.TAG_ROOMS);
                    for(int i=0; i < rooms.length(); i++)
                    {
                        JSONObject JSONRoom = rooms.getJSONObject(i);
                        int id = Integer.parseInt(JSONRoom.getString(Constants.TAG_ID));
                        String name = JSONRoom.getString(Constants.TAG_NAME);
                        int freeBusy = Integer.parseInt(JSONRoom.getString(Constants.TAG_FREEBUSY));
                        String time = JSONRoom.getString(Constants.TAG_TIME);
                        Room room = new Room(id,name,freeBusy,time);
                        roomList.add(room);
                    }
                    done = true;
                }
            }
            catch (JSONException e) {
                e.printStackTrace();

                //display invalid token Toast message
                Toast toast = Toast.makeText(context, "Server internal error, please try again", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
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
                //update the room items to the list view
                RoomListAdapter adapter = new RoomListAdapter(activity,roomList);
                roomListView.setAdapter(adapter);
            }
        }

    }

    /**
     * onItemClick method handles click event for each row within the room list
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(RoomListActivity.this,RoomDetailsActivity.class);
        Room room = roomList.get(position);
        intent.putExtra("id", room.getId());
        intent.putExtra("token", token);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_list, menu);
        return false;
    }

    @Override
    public void onRestart() {
        super.onResume();
        //get room json data via calling the progressTask
//        roomList = new ArrayList<Room>();
//        new ProgressTask(RoomListActivity.this, token).execute();
    }
}
