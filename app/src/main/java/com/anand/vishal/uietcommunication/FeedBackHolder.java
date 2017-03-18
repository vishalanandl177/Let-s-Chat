package com.anand.vishal.uietcommunication;

/**
 * Created by Vishal Anand on 25-07-2016.
 */
public class FeedBackHolder {

    String name, roll_no, email, message;

    public FeedBackHolder() {
    }

    public FeedBackHolder(String name, String roll_no, String email, String message) {
        this.name = name;
        this.roll_no = roll_no;
        this.email = email;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoll_no() {
        return roll_no;
    }

    public void setRoll_no(String roll_no) {
        this.roll_no = roll_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
