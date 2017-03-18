package com.anand.vishal.uietcommunication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vishal Anand on 31-07-2016.
 */
public class NotificationHolder {

    String name, message, date;

    public NotificationHolder() {
    }

    public NotificationHolder(String name, String message) {
        this.name = name;
        this.message = message;
        this.date = date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String date() {
        String curDate = "";
        long msTime = System.currentTimeMillis();
        Date curDateTime = new Date(msTime);

        SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y");
        curDate = formatter.format(curDateTime);
        return curDate;
    }
}
