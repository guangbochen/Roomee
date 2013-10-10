package com.vivant.roomee.model;

/**
 * Created by guangbo on 10/10/13.
 */
public class Room {
    private int id;
    private String name;
    private int status;
    private String time;
    private int checkDetails;

    public Room(int id, String name, int status, String time, int checkDetails) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.time = time;
        this.checkDetails = checkDetails;
    }

    public int getCheckDetails() {
        return checkDetails;
    }

    public void setCheckDetails(int checkDetails) {
        this.checkDetails = checkDetails;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
