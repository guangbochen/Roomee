package com.vivant.roomee.handler;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vivant.roomee.R;
import com.vivant.roomee.model.Meeting;
import com.vivant.roomee.timeManager.TimeCalculator;
import com.vivant.roomee.timeManager.TimeCalculatorImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class manages the view of meeting time table in the RoomDetails Activity
 * Created by guangbo on 24/10/13.
 */
public class MeetingTableHandler {

    private final static int HOUR = 60;
    private final static int timeRange = 12;
    private Context context;
    private TimeCalculator tc;
    private static ArrayList<String> hours;
    private LinearLayout meetingTimelineHeader;
    private LinearLayout meetingTimelineFooter;
    private LinearLayout meetingTableHeader;
    private RelativeLayout meetingTimeTable;

    /**
     * default constructor initialise instances
     * @param context
     * @param meetingInfoLayout, LinearLayout of meeting info
     */
    public MeetingTableHandler(Context context, RelativeLayout meetingInfoLayout) {
        this.context = context;
        this.meetingTableHeader = (LinearLayout) meetingInfoLayout.findViewById(R.id.meetingTableHeader);
        this.meetingTimeTable = (RelativeLayout) meetingInfoLayout.findViewById(R.id.meetingTableTime);
        this.meetingTimelineHeader = (LinearLayout) meetingInfoLayout.findViewById(R.id.meetingTimeLineHeader);
        this.meetingTimelineFooter = (LinearLayout) meetingInfoLayout.findViewById(R.id.meetingTimeLineFooter);
        tc = new TimeCalculatorImpl();
    }

    /**
     * this method erase the old components of views
     */
    public void eraseMeetingTableView()
    {
        meetingTableHeader.removeAllViews();
        meetingTimelineHeader.removeAllViews();
        meetingTimelineFooter.removeAllViews();
        meetingTimeTable.removeAllViews();
    }

    /**
     * this method add a list of time line to the top of meeting table
     */
    public void setMeetingTableHeader() {

        //displays time table
        hours = new ArrayList<String>();
        hours = tc.getCurrentAndNextHours();
        int index  =0;
        for(int i=0; i<timeRange; i++)
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
                    //if is the last hour set position to the right side
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                    lp.setMargins(0,0,0,0);
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
     * @param status, int room status
     */
    public void addMeetingToTimeTable(ArrayList<Meeting> meetings, int status)
    {
        int totalHours = 6;
        int tableWidth = meetingTimeTable.getWidth();
        int hourWidth = tableWidth / totalHours;
        for(Meeting m : meetings)
        {
            //add meeting details to each meeting
            addMeetingDetails(m,hourWidth, status);
        }
    }

    /**
     * this method add meeting details to each meeting container
     * @param m, meeting object
     * @param hourWidth, layout width for per hour
     * @param status, int room status
     */
    private void addMeetingDetails(Meeting m, int hourWidth, int status)
    {
        //calculates the meeting duration
        int duration = tc.calculatesDuration(m.getStart(),m.getEnd());
        int width = ((duration*100)/HOUR)*hourWidth/100;

        //calculates the start position of the meeting
        int timeDiff = tc.getTimeDiffByCurrentTime(m.getStart());
        int padding = ((timeDiff*100)/HOUR)*hourWidth/100;

        //add meeting view to the meeting time table
        TextView meeting = new TextView(context);
        //set layout parameters for each meeting
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(padding, 20, 0, 20);
        meeting.setLayoutParams(lp);

        //set meeting component details
        String meetingDuration = tc.parseRFCDateToRegular(m.getStart()) +" to "
                + tc.parseRFCDateToRegular(m.getEnd());
        String info = m.getSummary() + "\n" + meetingDuration;

        //reset meeting details position after moving container's margin to the left
        if(timeDiff<0) meeting.setPadding(-padding,5,5,5);
        else  meeting.setPadding(5,5,5,5);

        //set meeting container details
        meeting.setMaxLines(6);
        if(status == 1) meeting.setBackgroundResource(R.color.busy_meeting_bg);
        else  meeting.setBackgroundResource(R.color.free_meeting_bg);
        meeting.setTextColor(Color.WHITE);
        meeting.setGravity(Gravity.CENTER);
        meeting.setText(info);
        meeting.setWidth(width);

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

    /**
     * this method add meeting time line header
     * @param status, int room status
     */
    public void setMeetingTimeLineHeader(int status)
    {
        for(int i=0; i<timeRange; i++)
        {
            TextView time = new TextView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            //if is first item, set padding to the left side
            time.setLayoutParams(lp);
            //check the room status and set the timeLine's background
            if(status == 1) time.setBackgroundResource(R.drawable.busy_timeline_header);
            else time.setBackgroundResource(R.drawable.free_timeline_header);
            //add time zone
            meetingTimelineHeader.addView(time);
        }
    }

    /**
     * this method add meeting time line footer
     * @param status, int room status
     */
    public void setMeetingTimeLineFooter(int status)
    {
        for(int i=0; i<timeRange; i++)
        {
            TextView time = new TextView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            //if is first item, set padding to the left side
            time.setLayoutParams(lp);
            //check the room status and set the timeLine's background
            if(status == 1) time.setBackgroundResource(R.drawable.busy_timeline_footer);
            else time.setBackgroundResource(R.drawable.free_timeline_footer);
            //add time zone
            meetingTimelineFooter.addView(time);
        }
    }


    /**
     * this method add minutes hand to the meeting time table
     */
    public void addTimeline(TextView timeline)
    {
        timeline.invalidate();
        int padding = compareToTimelines();
        //set timeLine container layout
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        //get the clock minutes hand location via calculating the current time
        lp.setMargins(padding,0,0,0);
        lp.addRule(RelativeLayout.BELOW, meetingTimelineHeader.getId());
        timeline.setLayoutParams(lp);
        timeline.setBackgroundResource(R.drawable.timeline);
    }


    /**
     * this method calculates the position that the timeline should moves
     * @return
     */
    private int compareToTimelines()
    {
        int padding = 0;
        //count the hour width
        int totalHours = 6;
        int tableWidth = meetingTimelineHeader.getWidth();
        int hourWidth = tableWidth / totalHours;

        //count the time line location
        Date date = new Date();
        int mins = date.getMinutes();
        padding = ((mins*100)/HOUR)*hourWidth/100;
        //compare the current hour to the timelins first hour
        int diff = tc.compareTimelineHours(date);
        padding += hourWidth*diff;

        if(padding>1) padding -= 2;
        return padding;
    }

}
