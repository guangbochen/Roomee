package com.vivant.roomee.handler;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.vivant.roomee.R;
import com.vivant.roomee.model.Meeting;
import com.vivant.roomee.timeManager.TimeCalculator;
import com.vivant.roomee.timeManager.TimeCalculatorImpl;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by guangbo on 24/10/13.
 */
public class MeetingTableHandler {

    private LinearLayout meetingTableHeader;
    private LinearLayout meetingTimeTable;
    private Context context;
    private TimeCalculator tc;
    private static ArrayList<String> hours;
    private final static int HOUR = 60;

    /**
     * default constructor initialise instances
     * @param context
     * @param meetingTableHeader
     * @param meetingTimeTable
     */
    public MeetingTableHandler(Context context, LinearLayout meetingTableHeader, LinearLayout meetingTimeTable) {
        this.context = context;
        this.meetingTableHeader = meetingTableHeader;
        this.meetingTimeTable =  meetingTimeTable;
        tc = new TimeCalculatorImpl();
    }

    public void setMeetingTableHeader() {
        //displays time table
        hours = new ArrayList<String>();
        hours = tc.getCurrentAndNextHours();
        int index  =0;
        for(int i=0; i<12; i++)
        {
            TextView label_time = new TextView(context);
            label_time.setTextColor(Color.WHITE);
            if(i%2 ==0)
            {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                if(i!=0) lp.setMargins(-20,0,0,0);
                label_time.setText(hours.get(index));
                label_time.setLayoutParams(lp);
                index++;
            }
            else
            {
                label_time.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                if(i==11)
                {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                    lp.setMargins(0,0,0,0);
                    //get last hour element
                    label_time.setText(hours.get(6));
                    label_time.setGravity(Gravity.RIGHT);
                    label_time.setLayoutParams(lp);
                }
            }
            //add time zone
            meetingTableHeader.addView(label_time);
        }
    }

    public void setMeetingTableTimeZone(ArrayList<Meeting> meetings)
    {
        int totalHours = 6;
        int tableWidth = meetingTimeTable.getWidth();
        int hourWidth = tableWidth / totalHours;
        for(Meeting m : meetings)
        {
            //calculates the meeting duration
            int duration = tc.calculatesDuration(m.getStart(),m.getEnd());
            int width = ((duration*100)/HOUR)*hourWidth/100;

            //calculates the start position of the meeting
            Date startTime = tc.getRegularDateFormat(m.getStart());
            getStartPositon(startTime);


            //add meeting view to the meeting time table
            TextView meeting = new TextView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0,0,0,0);
            meeting.setLayoutParams(lp);
            meeting.setBackgroundColor(R.color.transparent);
            meeting.setBackgroundResource(R.drawable.table_shape);
            meeting.setTextColor(Color.WHITE);
            meeting.setGravity(Gravity.CENTER);
            meeting.setText(m.getSummary());
            meeting.setWidth((int)width);
            meetingTimeTable.addView(meeting);
        }
    }


    private int getStartPositon(Date startTime)
    {
        int position = 0;
        Date time = new Date();
        if(startTime.getHours() >= time.getHours())
        {
            //calculates the meeting duration
//            int duration = tc.calculatesDuration(m.getStart(),startTime);
//            int width = ((duration*100)/HOUR)*hourWidth/100;
        }

        return  position;
    }


}
