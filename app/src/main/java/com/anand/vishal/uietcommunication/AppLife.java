package com.anand.vishal.uietcommunication;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 *
 * Created by Vishal Anand on 09-06-2016.
 */

public class AppLife extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this) ;
    }
}
