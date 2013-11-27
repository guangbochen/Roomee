package com.vivant.roomee;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.*;
import com.vivant.roomee.adapter.RoomListAdapter;
import com.vivant.roomee.json.JSONParser;
import com.vivant.roomee.json.JSONParserImpl;
import com.vivant.roomee.json.RoomeeRestClient;
import com.vivant.roomee.model.Constants;
import com.vivant.roomee.model.Room;
import com.vivant.roomee.services.RefreshRoomService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DeviceActivation extends Activity implements OnItemClickListener {

    private static boolean done;
    private List<Room> roomList;
    private String token;
    private String uid;
    private String accountCode;
    private String deviceToken;
    private final static String title ="Meeting rooms";
    private final static String dialogMessage ="Loading rooms ...";
    private ProgressDialog dialog;
    private RoomListAdapter adapter;

    /**
     * onCreate method initialise the view of RoomListActivity
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_room_list);

        //initialise instance
        if(checkPrefs()!=null)
        {
            Context context = getApplicationContext();
            SharedPreferences pref = context.getSharedPreferences("roomee_prefs", MODE_PRIVATE);
            String answer = pref.getString("device_token", null);
            String access = pref.getString("access_token", null);
            Log.d("ACES TOK RETRIEVE", access);
                deviceToken = answer;
                token = access;
        }else
        {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                //calls async task to load list of rooms
                uid = extras.getString("serial number"); //getting device serial number
                token = extras.getString("token");
            displayProgressDialog();

            }
            UpdateRoomList();
        }


    }





    /**
     * this method find the activity views
     */
    private void findViewComponents()
    {
//        roomListView = (ListView) findViewById(R.id.roomListView);

        //set custom title for the main activity
        ActionBar ab = getActionBar();
        ab.setTitle(title);
        ab.setDisplayShowTitleEnabled(true);
    }

    /**
     * this method displays the progress dialog
     */
    private void displayProgressDialog() {
        this.dialog = new ProgressDialog(DeviceActivation.this);
        this.dialog.setMessage(dialogMessage);
        this.dialog.show();
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.setCancelable(false);
        done = true; //should be false
    }

    /**
     * this method displays invalid token message
     * @param message, string message
     */
    private void invalidMessage(String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("\n"+message+"\n");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    /**
     * onItemClick method handles click event for each row within the room list
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(DeviceActivation.this,RoomDetailsActivity.class);
        Room room = roomList.get(position);
        intent.putExtra("id", String.valueOf(room.getId()));
        intent.putExtra("token", token);
        startActivity(intent);
    }

    /**
     * this method manages activity being restarted from stopped state
     */
    @Override
    public void onRestart() {
        super.onRestart();

//        UpdateRoomList();
    }


    /**
     * this method calls the remote web services to load a list of meeting rooms
     */
    public void UpdateRoomList() {
        //initialise instance
            done = false;

            String url = "device/"+uid+"/activate" + "?access_token="+token;
            Log.d("TESTING", url);
            RoomeeRestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject json) {
                try{
//                    // Pull out the first event on the public timeline
                    String HttpStatus = json.getString(Constants.TAG_STATUS);
                    if(HttpStatus.equals("success"))
                    {
                        JSONObject data = json.getJSONObject(Constants.TAG_DATA);
                        deviceToken = data.getString("device_token");
                        Intent intent = new Intent(getApplicationContext(), RoomDetailsActivity.class);
                        intent.putExtra("token", token);
                        intent.putExtra("device token", deviceToken);
                        intent.putExtra("account code", accountCode);
                        Log.d("DEVICE TOKEN", deviceToken);
                        storeDevToken(deviceToken);
                        startActivity(intent);
                } else if(HttpStatus.equals("fail")){
                        Log.d("TESTING", "Failed http status");

                }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("TESTING", "didn't work");
                    if(dialog.isShowing()) dialog.dismiss();
                }
            }
        });}
    //}

    private String checkPrefs(){
        Context context = getApplicationContext();
        SharedPreferences pref = context.getSharedPreferences("roomee_prefs", 0);
        String answer = pref.getString("device_token", null);
        String access = pref.getString("access_token", null);
        if(answer!=null){
            return answer;
        }else{
            return null;
        }
    }

    private void storeDevToken(String deviceToken){
        SharedPreferences pref = this.getSharedPreferences("roomee_prefs", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("device_token", deviceToken);
        editor.commit();
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
            case R.id.action_refresh:
            {
//                displayProgressDialog();
                UpdateRoomList();
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * this method display the activity menu bar
     * @param menu, menu bar
     * @return false, not showing the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_list, menu);
        return true;
    }

    public void logThis(String msg){
        Log.d("TESTING", msg);
    }
}
