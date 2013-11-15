package com.vivant.roomee.navigationDrawer;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.vivant.roomee.R;
import com.vivant.roomee.adapter.MeetingListAdapter;
import com.vivant.roomee.model.Meeting;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guangbo on 8/11/13.
 */
public class MeetingListDrawerImpl implements MeetingListDrawer {

    private Context context;
    private LinearLayout meetingListDrawerLayout;
    private ListView meetingListView;
    private MeetingListAdapter adapter;

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
        if(adapter == null){
            Log.d("DD", "create new");
            adapter = new MeetingListAdapter(context,meetings);
        }
        else  {
            Log.d("DD", "update ");
            adapter.updateResults(meetings);
        }
        meetingListView.setAdapter(adapter);
    }
}
