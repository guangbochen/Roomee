package com.vivant.roomee.timeManager;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by guangbo on 28/10/13.
 */
public interface TimeCalculator {


    public String getCurrentTime();
    public ArrayList<String> getCurrentAndNextHours();
    public String CalculateTimeDif(String nextMeeting);
    public boolean compareMeetingTime(String meetingStart, String meetingEnd, String startTime, String endTime);
    public String getRFCDateFormat(int hour, int mins);
    public int calculatesDuration(String startTime, String endTime);
    public Date getRegularDateFormat(String RFCTime);
}
