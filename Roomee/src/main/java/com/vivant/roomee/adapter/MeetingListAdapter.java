package com.vivant.roomee.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.vivant.roomee.R;
import com.vivant.roomee.model.Meeting;
import java.util.ArrayList;

/**
 * Created by guangbo on 8/11/13.
 */
public class MeetingListAdapter extends BaseAdapter {
    private ArrayList<Meeting> meetings;
    private Context context;

    public MeetingListAdapter(Context context, ArrayList<Meeting> meetings) {
        this.context = context;
        this.meetings = meetings;

    }

    /**
     * viewHolder for row items in roomList
     */
    private class ViewHolder {
        TextView txtMeeting;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(view == null)
        {
            view = mInflater.inflate(R.layout.drawer_meeting_item , null);
            holder  = new ViewHolder();
            holder.txtMeeting = (TextView) view.findViewById(R.id.drawer_meeting);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        Meeting m = getItem(position);

        //set view to the screen
        holder.txtMeeting.setText(m.getSummary());
        holder.txtMeeting.setTextSize(18);

        return view;
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
