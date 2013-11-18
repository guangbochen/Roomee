package com.vivant.roomee.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.vivant.roomee.R;
import com.vivant.roomee.model.Meeting;
import com.vivant.roomee.timeManager.TimeCalculator;
import com.vivant.roomee.timeManager.TimeCalculatorImpl;
import java.util.ArrayList;

/**
 * This class displays a list of meeting to the searching MeetingList Drawer
 * Created by guangbo on 8/11/13.
 */
public class MeetingListAdapter extends BaseAdapter {
    private ArrayList<Meeting> meetings;
    private Context context;
    private TimeCalculator tc;

    /**
     * constructor to initalise instances
     * @param context, context
     * @param meetings, ArrayList of meeting
     */
    public MeetingListAdapter(Context context, ArrayList<Meeting> meetings) {
        this.context = context;
        this.meetings = meetings;
    }

    /**
     * viewHolder for row items in roomList
     */
    private class ViewHolder {
        ImageView imgStatus;
        TextView txtCreator;
        TextView txtSummary;
        TextView txtTime;
    }

    /**
     * this method returns a list view of meetings that the MeetingList Drawer contains
     * @param position, index position
     * @param view, View
     * @param parent, ViewGroup about parent view
     * @return View, meeting view
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(view == null)
        {
            view = mInflater.inflate(R.layout.drawer_meeting_item , null);
            holder  = new ViewHolder();
            holder.imgStatus = (ImageView) view.findViewById(R.id.drawer_mStatus);
            holder.txtCreator = (TextView) view.findViewById(R.id.drawer_mCreator);
            holder.txtSummary = (TextView) view.findViewById(R.id.drawer_mSummary);
            holder.txtTime = (TextView) view.findViewById(R.id.drawer_mTime);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        //set view to the view component
        Meeting m = getItem(position);
        setViewHolder(holder,m);

        return view;
    }

    /**
     * this method set view details of each ViewHolder
     * @param holder
     * @param m
     */
    private void setViewHolder(ViewHolder holder,Meeting m)
    {
        //initialise time calculator instance
        tc = new TimeCalculatorImpl();

        if(tc.checkRoomStatus(m.getStart(), m.getEnd()) == 0)
            holder.imgStatus.setImageResource(R.color.danger);
        else if(tc.checkRoomStatus(m.getStart(), m.getEnd()) < 0)
            holder.imgStatus.setImageResource(R.color.success);
        else
            holder.imgStatus.setImageResource(R.color.finished);

        //set view to the screen
        holder.txtCreator.setText(m.getCreator());
        holder.txtSummary.setText(m.getSummary());
        String startTime = tc.parseRFCDateToRegular(m.getStart());
        String endTime = tc.parseRFCDateToRegular(m.getEnd());
        holder.txtTime.setText("From " + startTime + " to " + endTime);
    }


    /**
     * this method notify the list view to update its data
     * @param meetings, ArrayList of meeting
     */
    public void updateResults(ArrayList<Meeting> meetings) {
        this.meetings = meetings;
        //Triggers the list update
        notifyDataSetChanged();
    }


    /**
     * this method returns total count of list items
     */
    @Override
    public int getCount() {
        return meetings.size();
    }

    /**
     * this method returns the selected room object upon its position
     */
    @Override
    public Meeting getItem(int position) {
        return meetings.get(position);
    }

    /**
     * this method returns index of selected row in roomList
     */
    @Override
    public long getItemId(int position) {
        return meetings.indexOf(getItem(position));
    }

}
