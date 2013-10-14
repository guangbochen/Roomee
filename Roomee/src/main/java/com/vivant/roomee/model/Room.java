package com.vivant.roomee.model;

/**
 * Created by guangbo on 10/10/13.
 */
public class Room {
    private int id;
    private String name;
    private int status;
    private String time;

    public Room(int id, String name, int status, String time) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.time = time;
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

    public int getStatus() {
        return status;
    }


    public String getTime() {
        return time;
    }

}
