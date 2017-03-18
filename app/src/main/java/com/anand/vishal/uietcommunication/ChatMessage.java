package com.anand.vishal.uietcommunication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vishal Anand on 11-06-2016.
 */
public class ChatMessage {
    String name, message, date;
    String rollno;

    ChatMessage() {

    }

    public ChatMessage(String name, String message, String rollno) {
        this.name = name;
        this.message = message;
        this.date = date();
        this.rollno = rollno;
    }

    public String getRollno() {
        return rollno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
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
