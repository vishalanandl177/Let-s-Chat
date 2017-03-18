package com.anand.vishal.uietcommunication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vishal Anand on 30-10-2016.
 */

public class NewsHolder {
    String title, message, date;

    public NewsHolder() {
    }

    public NewsHolder(String title, String message) {
        this.title = title;
        this.message = message;
        this.date = date();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
