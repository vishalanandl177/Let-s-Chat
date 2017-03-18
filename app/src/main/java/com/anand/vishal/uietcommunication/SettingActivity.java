package com.anand.vishal.uietcommunication;

import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.preferences);

    }
}
