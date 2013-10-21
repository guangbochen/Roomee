package com.vivant.roomee.model;

/**
 * Created by guangbo on 21/10/13.
 */
public class Meeting {
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
