package com.vivant.roomee.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by guangbo on 21/10/13.
 */
public class Meeting implements Parcelable {
    private String summary;
    private String creator;
    private String start;
    private String end;

    public Meeting() {
    }

    public Meeting(String summary, String creator, String start, String end) {
        this.summary = summary;
        this.creator = creator;
        this.start = start;
        this.end = end;
    }

    public Meeting(Parcel in)
    {
        this.summary = in.readString();
        this.creator = in.readString();
        this.start = in.readString();
        this.end = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.summary);
        dest.writeString(this.creator);
        dest.writeString(this.start);
        dest.writeString(this.end);

    }

    public static final Creator CREATOR = new Creator() {
        public Meeting createFromParcel(Parcel in) {
            return new Meeting(in);
        }

        public Meeting[] newArray(int size) {
            return new Meeting[size];
        }
    };

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

}
