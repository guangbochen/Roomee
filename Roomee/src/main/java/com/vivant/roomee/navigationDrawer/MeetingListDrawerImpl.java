package com.vivant.roomee.navigationDrawer;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.vivant.roomee.R;
import com.vivant.roomee.adapter.MeetingListAdapter;
import com.vivant.roomee.model.Meeting;
import java.util.ArrayList;

/**
 * Created by guangbo on 8/11/13.
 */
public class MeetingListDrawerImpl implements MeetingListDrawer {

    private Context context;
    private LinearLayout meetingListDrawerLayout;
    private ListView meetingListView;
    private MeetingListAdapter adapter;
    private final static String hint = "slide to close <";

    public MeetingListDrawerImpl(Context context, LinearLayout meetingListDrawerLayout) {
        this.context = context;
        this.meetingListDrawerLayout = meetingListDrawerLayout;
        this.meetingListView = (ListView) meetingListDrawerLayout.findViewById(R.id.meetingListDrawer);
    }

    /**
     * this method add a list of meeting to the left drawable listView
     * @param meetings, arrayList of meeting
     */
    @Override
    public void addMeetingList(ArrayList<Meeting> meetings) {
        adapter = new MeetingListAdapter(context,meetings);
        meetingListView.setAdapter(adapter);
        setView();
    }

    private void setView()
    {
        //set details of meeting drawer hint
//        meetingDrawerHint.setTextSize(14);
//        meetingDrawerHint.setText(hint);
    }

}
