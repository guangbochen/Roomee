package com.vivant.roomee;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RoomDetails extends Activity {

    private ImageView imageSearch;
    private ImageView imageAdd;
    private LinearLayout headerLinerLayout;
    private LinearLayout roomInfoLinerLayout;
    private LinearLayout timeInfoLinerLayout;
    private TextView txtStatus;
    private Button btnEndMeeting;
    private Button btnExtendMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        //retrieve data passed from main activity
        Bundle extras = getIntent().getExtras();
        displayItems(extras);


        //get the current time and updates in every sec.
        Thread timeThread = null;
        Runnable myRunnableThread = new CountDownRunner();
        timeThread= new Thread(myRunnableThread);
        timeThread.start();
    }

    private void displayItems(Bundle extras) {
        //get layout component
        headerLinerLayout = (LinearLayout) findViewById(R.id.header);
        roomInfoLinerLayout = (LinearLayout) findViewById(R.id.roomInfo);
        timeInfoLinerLayout = (LinearLayout) findViewById(R.id.timeInfo);

        //get each view component
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        imageSearch = (ImageView) findViewById(R.id.imgSearch);
        imageAdd = (ImageView) findViewById(R.id.imgAdd);
        btnEndMeeting = (Button) findViewById(R.id.btnEndMeeting);
        btnExtendMeeting = (Button) findViewById(R.id.btnExtendMeeting);


        //set image to imageView

        //set room details background upon the room status
        if(extras != null)
        {
            int status = extras.getInt("status");
            //if room status is busy, then set bgcolor into red
            if(status == 1)
            {
                headerLinerLayout.setBackgroundColor(getResources().getColor(R.color.header_red));
                roomInfoLinerLayout.setBackgroundColor(getResources().getColor(R.color.room_red));
                timeInfoLinerLayout.setBackgroundColor(getResources().getColor(R.color.time_red));
                txtStatus.setText("Busy for");
            }
            else {
                //if room status is free
                btnExtendMeeting.setVisibility(View.INVISIBLE);
                btnEndMeeting.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * this class implements runnable class and handles 12-hr-clock time counting
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
     * this methods calculates the current time and updates to the currentTime textView
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_details, menu);
        return true;
    }
    
}
