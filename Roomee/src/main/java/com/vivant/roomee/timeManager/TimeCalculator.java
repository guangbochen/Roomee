package com.vivant.roomee.timeManager;

import com.vivant.roomee.model.Meeting;

import java.util.ArrayList;
import java.util.Date;

/**
 * This is interface for time calculation methods
 * Created by guangbo on 28/10/13.
 */
public interface TimeCalculator {

    //method for get the time and calculates the time
    public String getCurrentTime(Date date);
    public Date parseStringToDate(String time);
    public ArrayList<String> getCurrentAndNextHours();
    public String calculateTimeDiff(String nextMeeting);
    public boolean compareMeetingTime(Date startDate, Date endDate, Meeting m);
    public int calculatesDuration(String startTime, String endTime);
    public int getTimeDiffByCurrentTime(String startTime);
    public int checkRoomStatus(String startTime, String endTime);

    //parse or format date in RFC339 format
    public String getRFCDateFormat(Date date);
    public String parseRFCDateToRegular(String RFCTime);
}
