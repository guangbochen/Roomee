package com.vivant.roomee.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is Meeting object that implements Parcelable interface
 * Created by guangbo on 21/10/13.
 */
public class Meeting implements Parcelable {
    private String summary;
    private String creator;
    private String start;
    private String end;


    /**
     * constructor with instance fields
     * @param summary, meeting summary
     * @param creator, meeting creator
     * @param start, meeting start time(RFC339)
     * @param end, meeting end time(RFC339)
     */
    public Meeting(String summary, String creator, String start, String end) {
        this.summary = summary;
        this.creator = creator;
        this.start = start;
        this.end = end;
    }

    /**
     * constructor for meeting parcelable interface
     * @param in, input
     */
    public Meeting(Parcel in)
    {
        this.summary = in.readString();
        this.creator = in.readString();
        this.start = in.readString();
        this.end = in.readString();
    }


    /**
     * this method write meeting object into parcel
     * @param dest, Parcel object
     * @param flags, int flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.summary);
        dest.writeString(this.creator);
        dest.writeString(this.start);
        dest.writeString(this.end);

    }

    /**
     * this method create a new parceable meeting object
     */
    public static final Creator CREATOR = new Creator() {
        public Meeting createFromParcel(Parcel in) {
            return new Meeting(in);
        }

        public Meeting[] newArray(int size) {
            return new Meeting[size];
        }
    };

    /**
     * this method describe the parcelable meeting contents
     * @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @return meeting summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * set meeting summary
     * @param summary, String meeting summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return parcel meeting creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * set parcel meeting creator
     * @param creator, String creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return meeting start time
     */
    public String getStart() {
        return start;
    }

    /**
     * set meeting start time
     * @param start, String meeting start time
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * @return meeting end time
     */
    public String getEnd() {
        return end;
    }

    /**
     * set meeting end time
     * @param end, String meeting end time
     */
    public void setEnd(String end) {
        this.end = end;
    }

}
