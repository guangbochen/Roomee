package com.vivant.roomee.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vivant.roomee.R;

/**
 * Created by guangbo on 24/10/13.
 */
public class MeetingTableHandler {

    private TableLayout meetingTableHeader;
    private TableLayout meetingTableTime;
    private Context context;

    public MeetingTableHandler(Context context, TableLayout meetingTableHeader, TableLayout meetingTableTime) {
        this.context = context;
        this.meetingTableHeader = meetingTableHeader;
        this.meetingTableTime = meetingTableTime;
    }

    public void setMeetingTableHeader() {

        //displays time table
        TableRow header = new TableRow(context);
        header.setId(10);
//        header.setBackgroundColor(Color.GRAY);

        for(int i=0; i<36; i++)
        {
            TextView label_time = new TextView(context);
            label_time.setGravity(Gravity.CENTER);
            label_time.setTextColor(Color.WHITE);
            label_time.setId(i);
            String time = String.valueOf(i);
            if(i%6 == 0 ) label_time.setText(time + " am");
            if(i == 35 ) label_time.setText(time + " am");

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
        timeZone.setWeightSum(10.f);

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
