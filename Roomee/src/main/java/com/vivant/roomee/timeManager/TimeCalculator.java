package com.vivant.roomee.timeManager;

import java.util.ArrayList;

/**
 * This is interface for time calculation methods
 * Created by guangbo on 28/10/13.
 */
public interface TimeCalculator {

    //method for get the time and calculates the time
    public String getCurrentTime();
    public ArrayList<String> getCurrentAndNextHours();
    public String calculateTimeDiff(String nextMeeting);
    public boolean compareMeetingTime(String meetingStart, String meetingEnd, String startTime, String endTime);
    public int calculatesDuration(String startTime, String endTime);
    public int getTimeDiffByCurrentTime(String startTime);
    public int checkRoomStatus(String startTime, String endTime);

    //parse or format date in RFC339 format
    public String getRFCDateFormat(int hour, int mins);
    public String parseRFCDateToRegular(String RFCTime);
}
