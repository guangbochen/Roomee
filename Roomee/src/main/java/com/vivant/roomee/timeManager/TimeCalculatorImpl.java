package com.vivant.roomee.timeManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * This class manges time calculation methods and parse or format time into RFC3339 format
 * Created by guangbo on 28/10/13.
 */
public class TimeCalculatorImpl implements  TimeCalculator{

    private static SimpleDateFormat datetimeParser = null;
    private static SimpleDateFormat datePrinter = null;
    private static SimpleDateFormat timePrinter = null;
    private static SimpleDateFormat hourMinPrinter = null;
    private static SimpleDateFormat hourPrinter = null;
    private final static int SECONDS = 1000;
    private final static int MINUTES = 60;
    private final static int HOURS = 24;
    private ArrayList<String> hours;


    /**
     * default constructor initialise instances
     */
    public TimeCalculatorImpl() {
        //lazy init & re-use date time format
        if(datetimeParser==null) {datetimeParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");};
        if(datePrinter==null) {datePrinter = new SimpleDateFormat("yyyy-MM-dd");};
        if(timePrinter==null) {timePrinter = new SimpleDateFormat("hh:mm aa");};
        if(hourMinPrinter==null) {hourMinPrinter = new SimpleDateFormat("hh:mm");};
        if(hourPrinter==null) {hourPrinter = new SimpleDateFormat("hh aa");};
    }

    /**
     * this method returns the current time
     * @return currentTime, String time in timePrinter format
     */
    public String getCurrentTime() {

        Date dt = new Date();
        String currentTime = timePrinter.format(dt);
        return  currentTime;
    }

    /**
     * this method returns a list of time for the next 6 hours upon the current time
     * @return hours, ArrayList of String of time
     */
    @Override
    public ArrayList<String> getCurrentAndNextHours() {

        hours = new ArrayList<String>();
        Date date = new Date();

        //add a list of time for the next 6 hours
        for(int i=0; i<7; i++)
        {
            if(i!=0) date.setHours(date.getHours()+1);
            String time = hourPrinter.format(date);
            time = time.replaceAll("AM","am").replaceAll("PM", "pm");
            hours.add(time);
        }
        return  hours;
    }

    /**
     * this method calculates the time difference between current time and next meeting time
     * @param nextMeeting, String RFC3339 date format
     * @return timeDiff, String time difference
     */
    @Override
    public String calculateTimeDiff(String nextMeeting) {

        //special case when nextMeeting time is null
        if(nextMeeting.equals("null")) return  "Forever";

        try {
            //get current date time and next meeting date
            Date current = new Date();
            Date next = datetimeParser.parse(nextMeeting);

            //calculate the time diff
            long diff = next.getTime() - current.getTime();
            long diffMinutes = diff / (MINUTES * SECONDS) % MINUTES;
            long diffHours = diff / (MINUTES * MINUTES * SECONDS) % HOURS;

            String hours , minutes;
            //manages the hours displaying
            if (diffHours <= 0) hours = "";
            else if(diffHours>1) hours = String.valueOf(diffHours) + " hours ";
            else  hours = String.valueOf(diffHours) + " hour ";

            //manages the minutes displaying
            if(diffMinutes == 0) minutes = "";
            else if (diffHours > 0) minutes = "and " + String.valueOf(diffMinutes) + " minutes";
            else minutes = String.valueOf(diffMinutes) + " minutes";

            //if both hour and minutes difference is 0, return 0 minutes
            if(hours.length() == 0 && minutes.length() ==0) return " less than 1 minutes";
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
     * @return false, if there is no existing meeting during the new meeting time
     */
    @Override
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
     * this method calculates meeting duration in minutes
     * @param startTime, String start meeting time
     * @param endTime, String end meeting time
     * @return duration, int meeting duration in minutes
     */
    @Override
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


    /**
     * this method calculates and returns the time difference between
     * the startTime of the next meeting and the current time
     * @param startTime, meeting start time in RFC3339 format
     * @return difference, int time difference
     */
    @Override
    public int getTimeDiffByCurrentTime(String startTime)
    {
        int difference = 0;
        try
        {
            Date meeting = datetimeParser.parse(startTime);
            Date date = new Date();

            int hourDiff = meeting.getHours() - date.getHours();
            int minDiff = meeting.getMinutes();

            //calculates the time difference based on minutes
            hourDiff *= 60;
            difference = minDiff+ hourDiff;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return difference;
    }

    /**
     * this method check the meeting status of whether it is finished ,processing or finished
     * @param startTime,String meeting start time in RFC3339 format
     * @param endTime, String meeting end time (RFC3339)
     * @return 0, if is in processing
     */
    @Override
    public int checkRoomStatus(String startTime, String endTime) {
        try {
            Date date = new Date();
            //get the existing start meeting time
            Date sTime = datetimeParser.parse(startTime);
            //get the existing end meeting time
            Date eTime = datetimeParser.parse(endTime);
            if(sTime.compareTo(date) > 0) return -1;
            if(eTime.compareTo(date) < 0) return 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * this method returns date and time in RFC3339 format
     * @param hour, selected meeting hour
     * @param mins, selected meeting minutes
     * @return time, String time in RFC3339 format
     */
    @Override
    public String getRFCDateFormat(int hour, int mins)
    {
        Date date = new Date();
        date.setHours(hour);
        date.setMinutes(mins);
        String time = datetimeParser.format(date);
        return time;
    }

    /**
     * this method parse RFC Date format into regular date time
     * @param RFCTime, String RFCTIME in RFC3339 date format
     * @return time , String time in timePrinter format
     */
    @Override
    public String parseRFCDateToRegular(String RFCTime)
    {
        String time = "";
        try {
            Date date = datetimeParser.parse(RFCTime);
            time = timePrinter.format(date);
            time = time.replaceAll("AM","am").replaceAll("PM", "pm");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

}
