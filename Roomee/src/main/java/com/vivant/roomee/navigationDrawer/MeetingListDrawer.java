package com.vivant.roomee.navigationDrawer;

import com.vivant.roomee.model.Meeting;
import java.util.ArrayList;

/**
 * This is the interface of MeetingListDrawer
 * Created by guangbo on 8/11/13.
 */
public interface MeetingListDrawer {

    //add meeting list method
    public void addMeetingList(ArrayList<Meeting> meetings);
}
