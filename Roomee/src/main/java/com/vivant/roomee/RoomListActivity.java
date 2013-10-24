package com.vivant.roomee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
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

    //JSON Node names;
    private boolean done;
    private JSONParser jsonParser;
    private ListView roomListView;
    private List<Room> roomList = new ArrayList<Room>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        done = false;

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
            jsonParser = new JSONParserImpl();

            //getting json string from url
//            String url = "rooms?oauth_token=" + extras.getString("token");
//            String url = "rooms?oauth_token=e24cda222194876faaba860416f6ef126d328639";
            String url = "http://www.json-generator.com/j/bTwZCVTxiW?indent=4";
            JSONObject json = jsonParser.getJSONFromUrl(url);
            try{
                if(json != null)
                {
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
                    }
                done = true;
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

            if(done == true)
            {
                //update the room items to the list view
                RoomListAdapter adapter = new RoomListAdapter(activity,roomList);
                roomListView.setAdapter(adapter);
            }
            else
            {
                //display invalid token Toast message
                Toast toast = Toast.makeText(context, "Server internal error, please try again", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
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
