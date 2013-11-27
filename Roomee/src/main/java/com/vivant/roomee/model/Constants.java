package com.vivant.roomee.model;

/**
 * This is constant class that contains all the public constant variables
 * Created by guangbo on 24/10/13.
 */
public class Constants {

    //Shared JSON Node Names
    public final static String TAG_STATUS = "status";
    public final static String TAG_MESSAGE = "message";
    public final static String TAG_DATA = "data";
    //JSON Node for rooms and individual room;
    public final static String TAG_ROOMS = "rooms";
    public final static String TAG_ID = "id";
    public final static String TAG_NAME = "name";
    public final static String TAG_FREEBUSY = "freebusy";
    public final static String TAG_TIME = "time";
    //JSON Node for individual room
    public final static String TAG_ROOM = "room";
    public final static String TAG_MEETINGS = "meetings";
    public final static String TAG_SUMMARY = "summary";
    public final static String TAG_CREATOR = "organiser";
    public final static String TAG_START = "start";
    public final static String TAG_END = "end";
    //JSON Node for Auth
    public static final String TAG_OAUTH = "oauth_token";
    public static final String TAG_ACCESS_TOKEN = "access_token";
    //JSON Node for add new meeting
    public static final String TAG_MEETING = "meeting";

    //Public Constants
    public static final String MAD = "MAD";
    public static final String NOINTERNET = "Please check internet connection";

    //const string for addNewMeeting Activity
    public final static String ADDNEWMEETINGTITLE = "New Meeting";
    public final static String BOOKEDROOM="There is already a meeting set to take place during your selected times.";
    public final static String SUBMIT="Are you sure you want to book this meeting?";
    public final static String SUBMITSUCCESS="Thanks, your meeting has been successfully created!";
    public final static String SUBMISIONFAILED = "Submission failed, please try again";
    public final static String EMPTYSUMMARY = "Meeting summary can't be empty.";
    public final static String EMPTYDESC = "Meeting description can't be empty.";
    public final static String INVALIDENDMEETING = "Invalid meeting end time.";

}
