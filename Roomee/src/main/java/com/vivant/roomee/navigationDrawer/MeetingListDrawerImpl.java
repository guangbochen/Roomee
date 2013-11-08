package com.vivant.roomee.navigationDrawer;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.vivant.roomee.R;
import com.vivant.roomee.adapter.MeetingListAdapter;
import com.vivant.roomee.model.Meeting;
import java.util.ArrayList;

/**
 * Created by guangbo on 8/11/13.
 */
public class MeetingListDrawerImpl implements MeetingListDrawer, AdapterView.OnItemClickListener {

    private Context context;
    private ListView meetingListView;
    private MeetingListAdapter adapter;

    public MeetingListDrawerImpl(Context context, ListView meetingListView) {
        this.context = context;
        this.meetingListView = meetingListView;
    }

    /**
     * this method add a list of meeting to the left drawable listView
     * @param meetings, arrayList of meeting
     */
    @Override
    public void addMeetingList(ArrayList<Meeting> meetings) {
        meetingListView.setBackgroundResource(R.color.white);
        adapter = new MeetingListAdapter(context,meetings);
        meetingListView.setAdapter(adapter);
        meetingListView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
