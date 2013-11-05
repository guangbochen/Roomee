package com.vivant.roomee.timeManager;

import android.provider.ContactsContract;
import android.util.Log;

import com.vivant.roomee.model.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This class calculates the time difference between current time and next meeting time
 * Created by guangbo on 28/10/13.
 */
public class TimeCalculatorImpl implements  TimeCalculator{

    private static SimpleDateFormat datetimeParser = null;
    private static SimpleDateFormat timePrinter = null;
    private static SimpleDateFormat datePrinter = null;
    private static SimpleDateFormat hourPrinter = null;
    private static SimpleDateFormat hourMinPrinter = null;
    private final static int SECONDS = 1000;
    private final static int MINUTES = 60;
    private final static int HOURS = 24;
    private String currentTime;
    private ArrayList<String> hours;


    /**
     * default constructor initialise instances
     */
    public TimeCalculatorImpl() {
        //lazy init & re-use date time format
        if(datetimeParser==null) {datetimeParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");};
        if(datePrinter==null) {datePrinter = new SimpleDateFormat("yyyy-MM-dd");};
        if(timePrinter==null) {timePrinter = new SimpleDateFormat("hh:mm aa");};
        if(hourPrinter==null) {hourPrinter = new SimpleDateFormat("hh aa");};
        if(hourMinPrinter==null) {hourMinPrinter = new SimpleDateFormat("hh:mm");};

        currentTime = "";
    }

    /**
     * this method returns the current time
     * @return currentTime, String time in timePrinter format
     */
    public String getCurrentTime() {

        Date dt = new Date();
        currentTime = timePrinter.format(dt);
        return  currentTime;
    }

    /**
     * this method returns a list of time upon the current time
     * @return hours, ArrayList of String for the current and next 6 hours time
     */
    public ArrayList<String> getCurrentAndNextHours() {

        hours = new ArrayList<String>();
        Date previous_time = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(previous_time);

        for(int i=0; i<7; i++)
        {
            if(i==0) calendar.add(Calendar.HOUR, 0);
            else  calendar.add(Calendar.HOUR, 1);
            previous_time = calendar.getTime();
            currentTime = hourPrinter.format(previous_time);
            hours.add(currentTime);
        }
        return  hours;
    }

    /**
     * this method calculates the time difference between current time and next meeting time
     * @param nextMeeting, String RFC3339 date format
     * @return timeDiff, String time difference
     */
    public String CalculateTimeDif(String nextMeeting) {

        //special case when nextMeeting time is null
        if(nextMeeting.equals("null")) return  "Forever";

        try {
            //get current date time
            Date current = new Date();

            //get next meeting date
            Date next    = datetimeParser.parse(nextMeeting);

            //calculate the time diff
            long diff = next.getTime() - current.getTime();
            //long diffSeconds = diff / SECONDS % MINUTES;
            long diffMinutes = diff / (MINUTES * SECONDS) % MINUTES;
            long diffHours = diff / (MINUTES * MINUTES * SECONDS) % HOURS;

            String hours , minutes;

            //manages the hours displaying
            if (diffHours <= 0) hours = "";
            else if(diffHours>1) hours = String.valueOf(diffHours) + " hours and ";
            else  hours = String.valueOf(diffHours) + " hour and ";

            //manages the minutes displaying
            if(diffMinutes == 0) minutes = "";
            else minutes = String.valueOf(diffMinutes) + " minutes";

            //if both hour and minutes difference is 0, return 0 minutes
            if(hours.length() == 0 && minutes.length() ==0) return " 0 minutes";
            return hours + minutes;

        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return "Forever";
    }

    /**
     * this method check whether the room is already booked by another meeting or not
     * @param meetingStart, existing meeting start time
     * @param meetingEnd, existing meeting end time
     * @param startTime, new added meeting start time
     * @param endTime, new added meeting end time
     * @return true, if there is no existing meeting during the new meeting time
     */
    public boolean compareMeetingTime(String meetingStart, String meetingEnd, String startTime, String endTime)
    {
        try
        {
            //get the existing start meeting time
            Date sMeeting = datetimeParser.parse(meetingStart);
            //get the existing end meeting time
            Date eMeeting = datetimeParser.parse(meetingEnd);
            String sMeetingTime = sMeeting.getHours() + ":" + sMeeting.getMinutes();
            String eMeetingTime = eMeeting.getHours() + ":" + eMeeting.getMinutes();

            //if the new meeting time is before or after the existing meeting time then return as validated
            if(startTime.compareTo(eMeetingTime) > 0 || endTime.compareTo(sMeetingTime) < 0)
                return true;

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * this method returns date and time in RFC3339 format
     * @param hour, selected meeting hour
     * @param mins, selected meeting minutes
     * @return time, String time in RFC3339 format
     */
    public String getRFCDateFormat(int hour, int mins)
    {
        Date date = new Date();
        date.setHours(hour);
        date.setMinutes(mins);
        String time = datetimeParser.format(date);
        return time;
    }

    public Date getRegularDateFormat(String RFCTime)
    {
        Date date = new Date();
        try
        {
            date = datetimeParser.parse(RFCTime);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return date;
    }

    /**
     * this method calculates meeting duration in minutes
     * @param startTime, String start meeting time
     * @param endTime, String end meeting time
     * @return duration, int meeting duration in minutes
     */
    public int calculatesDuration(String startTime, String endTime)
    {
        int duration =0;
        try
        {
            Date start = datetimeParser.parse(startTime);
            Date end = datetimeParser.parse(endTime);
            //in milliseconds
            long diff = end.getTime() - start.getTime();
            long diffMinutes = diff / (60 * 1000);
            duration = (int) diffMinutes;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return duration;
    }

}
