package com.vivant.roomee.handler;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    private RelativeLayout meetingTimeTable;
    private Context context;
    private TimeCalculator tc;
    private static ArrayList<String> hours;
    private final static int HOUR = 60;
    private LinearLayout meetingTimelineHeader;
    private LinearLayout meetingTimelineFooter;

    /**
     * default constructor initialise instances
     * @param context
     * @param meetingTableHeader
     * @param meetingTimeTable
     */
    public MeetingTableHandler(Context context, LinearLayout meetingTableHeader,RelativeLayout meetingTimeTable,
                               LinearLayout meetingTimelineHeader, LinearLayout meetingTimelineFooter) {
        this.context = context;
        this.meetingTableHeader = meetingTableHeader;
        this.meetingTimeTable =  meetingTimeTable;
        this.meetingTimelineHeader = meetingTimelineHeader;
        this.meetingTimelineFooter = meetingTimelineFooter;
        tc = new TimeCalculatorImpl();
    }

    public void setMeetingTableHeader() {

        meetingTableHeader.removeAllViews();
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
                //if is first item, set padding to the left side
                if(i!=0) lp.setMargins(-22,0,0,0);
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

    /**
     * this method add a list of meeting to the time table layout
     * @param meetings, list of meeting
     */
    public void addMeetingToTimeTable(ArrayList<Meeting> meetings)
    {
        meetingTimeTable.removeAllViews();
        int totalHours = 6;
        int tableWidth = meetingTimeTable.getWidth();
        int hourWidth = tableWidth / totalHours;
        for(Meeting m : meetings)
        {
            //add meeting details to each meeting
            addMeetingDetails(m,hourWidth);
        }
    }

    /**
     * this method add meeting details to each meeting container
     * @param m, meeting object
     * @param hourWidth, layout width for per hour
     */
    private void addMeetingDetails(Meeting m, int hourWidth)
    {
        //calculates the meeting duration
        int duration = tc.calculatesDuration(m.getStart(),m.getEnd());
        int width = ((duration*100)/HOUR)*hourWidth/100;

        //calculates the start position of the meeting
        int timeDiff = tc.getTimeDiffByCurrentTime(m.getStart());
        Log.d("TEST", String.valueOf(timeDiff));

        int padding = ((timeDiff*100)/HOUR)*hourWidth/100;
        Log.d("TEST padding left = ", String.valueOf(padding));
        //add meeting view to the meeting time table
        TextView meeting = new TextView(context);
        //set layout parameters for each meeting
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.setMarginStart(padding);
        meeting.setLayoutParams(lp);

        //set meeting component details
        String meetingDuration = tc.parseRFCDateToRegular(m.getStart()) +" to "
                + tc.parseRFCDateToRegular(m.getEnd());
        String info = m.getSummary() + "\n" + meetingDuration;

        //reset meeting details position after moving container's margin to the left
        if(timeDiff<0) meeting.setPadding(-padding,5,5,5);
        else  meeting.setPadding(5,5,5,5);

        //set meeting container details
        meeting.setMaxLines(5);
        meeting.setBackgroundColor(R.color.transparent);
        meeting.setBackgroundResource(R.drawable.table_shape);
        meeting.setTextColor(Color.WHITE);
        meeting.setGravity(Gravity.CENTER);
        meeting.setText(info);
        meeting.setWidth((int)width);

        //set font size upon the meeting duration time
        int meetingTime = tc.calculatesDuration(m.getStart(), m.getEnd());
        if(meetingTime >30) meeting.setTextSize(14);
        else if(meetingTime >= 15 ) meeting.setTextSize(12);
        else {
            meeting.setTextSize(8);
            meeting.setText(m.getSummary());
        }

        //add view to the time table
        meetingTimeTable.addView(meeting);
    }


    public void setMeetingTimeLineHeader()
    {
        meetingTimelineHeader.removeAllViews();
        for(int i=0; i<12; i++)
        {
            TextView time = new TextView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            //if is first item, set padding to the left side
            time.setLayoutParams(lp);
            time.setBackgroundResource(R.drawable.timelines);
            //add time zone
            meetingTimelineHeader.addView(time);
        }
    }

    public void setMeetingTimeLineFooter()
    {
        meetingTimelineFooter.removeAllViews();
        for(int i=0; i<12; i++)
        {
            TextView time = new TextView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            //if is first item, set padding to the left side
            time.setLayoutParams(lp);
            time.setBackgroundResource(R.drawable.busy_timeline_footer);
            //add time zone
            meetingTimelineFooter.addView(time);
        }
    }

}
