package com.vivant.roomee.handler;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vivant.roomee.R;
import com.vivant.roomee.timeManager.TimeCalculator;
import com.vivant.roomee.timeManager.TimeCalculatorImpl;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by guangbo on 24/10/13.
 */
public class MeetingTableHandler {

    private TableLayout meetingTableHeader;
    private TableLayout meetingTableTime;
    private Context context;
    private TimeCalculator tc;

    public MeetingTableHandler(Context context, TableLayout meetingTableHeader, TableLayout meetingTableTime) {
        this.context = context;
        this.meetingTableHeader = meetingTableHeader;
        this.meetingTableTime = meetingTableTime;
        tc = new TimeCalculatorImpl();
    }

    public void setMeetingTableHeader() {

        //displays time table
        TableRow header = new TableRow(context);
        header.setId(10);
//        header.setBackgroundColor(Color.GRAY);

        int index = 0;
        for(int i=0; i<36; i++)
        {
            TextView label_time = new TextView(context);
            label_time.setGravity(Gravity.CENTER);
            label_time.setTextColor(Color.WHITE);
            label_time.setId(i);
            String time = String.valueOf(i);

            //set gap between
            if(i%6 == 0 || i == 35)
            {
                ArrayList<String> hours = tc.getCurrentAndNextHours();
                label_time.setText(hours.get(index++));
            }

            //add time zone
            header.addView(label_time);
        }

        meetingTableHeader.addView(header);
    }


    public void setMeetingTableTimeZone()
    {
        //displays time table
        TableRow timeZone = new TableRow(context);
        timeZone.setId(20);
        timeZone.setWeightSum(1);

        for(int i=0; i<36; i++)
        {
            TextView ten_sec = new TextView(context);
            ten_sec.setTextColor(Color.WHITE);
            ten_sec.setId(i);
//            five_sec.setText(String.valueOf(i));
            ten_sec.setBackgroundResource(R.drawable.table_shape);
            if(i%6 == 0)
            {
                ten_sec.setBackgroundResource(R.drawable.table_shape);
            }

            //add time zone
            timeZone.addView(ten_sec);
        }
        setMeeting(meetingTableTime);
        meetingTableTime.addView(timeZone);
    }


    private void setMeeting(TableLayout meetingTableTime)
    {
    }


}
