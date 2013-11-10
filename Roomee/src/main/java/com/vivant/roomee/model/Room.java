package com.vivant.roomee.model;

import java.io.Serializable;

/**
 * This is a Serializable Room object
 * Created by guangbo on 10/10/13.
 */
public class Room implements Serializable{
    private int id;
    private String name;
    private int status;
    private String time;

    /**
     * constructor
     */
    public Room() {
        super();
    }

    /**
     * constructor with instance fields
     * @param id, int room id
     * @param name, string room name
     * @param status, int room status
     * @param time, String room time
     */
    public Room(int id, String name, int status, String time) {
        super();
        this.id = id;
        this.name = name;
        this.status = status;
        this.time = time;
    }

    /**
     * @return int room id
     */
    public int getId() {
        return id;
    }

    /**
     * set room id
     * @param id, int room id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return String room name
     */
    public String getName() {
        return name;
    }

    /**
     * set room name
     * @param name, string room name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return, int room status
     */
    public int getStatus() {
        return status;
    }

    /**
     * set room status
     * @param status, int room status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return String room time
     */
    public String getTime() {
        return time;
    }

    /**
     * set room time
     * @param time, String room time
     */
    public void setTime(String time) {
        this.time = time;
    }
}
