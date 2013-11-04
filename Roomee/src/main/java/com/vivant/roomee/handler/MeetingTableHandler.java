package com.vivant.roomee.handler;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.vivant.roomee.R;
import com.vivant.roomee.model.Meeting;
import com.vivant.roomee.timeManager.TimeCalculator;
import com.vivant.roomee.timeManager.TimeCalculatorImpl;
import java.util.ArrayList;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by guangbo on 24/10/13.
 */
public class MeetingTableHandler {

    private TableLayout meetingTableHeader;
    private LinearLayout meetingTimeTable;
    private Context context;
    private TimeCalculator tc;

    public MeetingTableHandler(Context context, TableLayout meetingTableHeader, LinearLayout meetingTimeTable) {
        this.context = context;
        this.meetingTableHeader = meetingTableHeader;
        this.meetingTimeTable =  meetingTimeTable;
        tc = new TimeCalculatorImpl();
    }

    public void setMeetingTableHeader() {

        //displays time table
        TableRow header = new TableRow(context);
        header.setId(10);
//        header.setBackgroundColor(Color.GRAY);

        int index = 0;
        for(int i=0; i<12; i++)
        {
            TextView label_time = new TextView(context);
            label_time.setGravity(Gravity.CENTER);
            label_time.setTextColor(Color.WHITE);
            label_time.setId(i);
            String time = String.valueOf(i);

            //set gap between
            if(i%2 == 0 || i == 11)
            {
                ArrayList<String> hours = tc.getCurrentAndNextHours();
                label_time.setText(hours.get(index++));
            }

            //add time zone
            header.addView(label_time);
        }

        meetingTableHeader.addView(header);
    }


    public void setMeetingTableTimeZone(ArrayList<Meeting> meetings)
    {
        for(int i=0; i<12; i++)
        {
            TextView ten_sec = new TextView(context);
            ten_sec.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1f));
            ten_sec.setTextColor(Color.WHITE);
//            ten_sec.setText(String.valueOf(i));
            for(Meeting m : meetings)
            {
                ten_sec.setBackgroundResource(R.drawable.table_shape);
            }
            //ten_sec.setBackgroundResource(R.drawable.table_shape);

            meetingTimeTable.addView(ten_sec);
        }
    }



}
