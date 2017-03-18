package com.anand.vishal.uietcommunication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.File;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    Button mSkipButton;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    // Create the database for the first time when app is launched
    SQLiteDatabase database = null;

    // These variables will hold the value of user name , branch, roll number and email after query from database
    // These values will pass from this activity to ChatActivity if user already logged in.
    String name = "", branch = "", roll = "", email = "", year = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        createDatabase();

        mSkipButton = (Button) findViewById(R.id.dummy_button);
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Intent to start a new activity by passing the current context and class name of the activity to be launched
                Intent intent;

                // variables contains no data means user not logged in the start the LoginActivity,
                // If user does logged in then start ChatActivity
                if (name.equals("")) {
                    intent = new Intent(FullscreenActivity.this, LoginActivity.class);
                } else {
                    intent = new Intent(FullscreenActivity.this, ChatActivity.class);

                    // Pass all the information of user to the ChatActivity
                    // passedName, passedBranch, passedRoll, passedEmail used as a keyword to catch desired value
                    intent.putExtra("passedName", name);
                    intent.putExtra("passedBranch", branch);
                    intent.putExtra("passedRoll", roll);
                    intent.putExtra("passedEmail", email);
                    intent.putExtra("passedYear", year);
                }

                // Start animation when a new activity is start
                // Check the OS version for different transition animation

                startActivity(intent);
                finish();
            }
        });


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    private void createDatabase() {
        // Create the database to hold the user information
        // Try - Catch block will handle the exceptions occur during creating database
        try {

            // If the app is launched first time then the openOrCreateDatabase will create a new database called userDetails
            // Every time when app is launched openOrCreateDatabase will check the database called userDetails exists or not.
            // If not then it will create a new database, If database does exist the it will not create.
            // Mode_private is responsible to open the database in private mode
            database = this.openOrCreateDatabase("userDetails", MODE_PRIVATE, null);

            // Create the table called "userdata" to store user data for login purpose
            // If the database is already exist then it will not create it.
            database.execSQL("CREATE TABLE IF NOT EXISTS userdata(id integer primary key AUTOINCREMENT,name VARCHAR,branch VARCHAR,year VARCHAR,roll_no VARCHAR,email VARCHAR);");


            //String query = "insert into userdata (name,branch,year,roll_no,email) values ('Vishal Anand','CSE','Fourth','13001390050','vishalanandl177@gmail.com','');";
            //database.execSQL(query);


            // Check the database called userDetails is created or not
            // userDetails database will created as a file on the root folder of mobile phone as userDetails.db
            File file = getApplicationContext().getDatabasePath("userDetails.db");
            if (!file.exists())
                Log.e("database", "database created");
            else
                Log.e("database", "database missing");


            // Query all tye information of user stored in the database
            try {

                // Cursor is responsible to store the information after the query
                Cursor cursor = database.rawQuery("SELECT * FROM userdata;", null);

                // These variables store the index of the all the columns(attributes) of the table in the database
                int nameColumn = cursor.getColumnIndex("name");
                int branchColumn = cursor.getColumnIndex("branch");
                int yearColumn = cursor.getColumnIndex("year");
                int rollColumn = cursor.getColumnIndex("roll_no");
                int emailColumn = cursor.getColumnIndex("email");

                // Move the cursor at the very first position to iterate from beginning
                cursor.moveToFirst();

                // Check database is respond using "cursor != null" .
                // If it does respond then check Cursor have some information using "cursor.getCount() > 0"
                // If the table does not contains any information that means user is not already logged in then every of the
                // String variables (name, branch, roll, email) wil, contains no data
                if ((cursor.getCount() > 0) && cursor != null) {
                    do {
                        name = cursor.getString(nameColumn);
                        branch = cursor.getString(branchColumn);
                        year = cursor.getString(yearColumn);
                        roll = cursor.getString(rollColumn);
                        email = cursor.getString(emailColumn);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                Log.e("splashScreen", "no table found");
            }

        } catch (Exception e) {
            Log.e("database", " something went wrong");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}
