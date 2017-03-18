package com.anand.vishal.uietcommunication;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteEditActivity extends AppCompatActivity {

    public static int numTitle = 1;
    public static String curDate = "";
    public static String curText = "";
    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private static final int SETTING_INFO = 1;

    private Cursor note;

    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        TextView mDateText = (TextView) findViewById(R.id.notelist_date);

        long msTime = System.currentTimeMillis();
        Date curDateTime = new Date(msTime);

        SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y");
        curDate = formatter.format(curDateTime);

        mDateText.setText("" + curDate);


        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                    : null;
        }

        populateFields();
        updateNoteText();
        mBodyText.requestFocus();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public static class LineEditText extends EditText {
        // we need this constructor for LayoutInflater
        public LineEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            mRect = new Rect();
            mPaint = new Paint();
          //  mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setColor(Color.LTGRAY);
        }

        private Rect mRect;
        private Paint mPaint;

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int height = getHeight();
            int line_height = getLineHeight();

            int count = height / line_height;

            if (getLineCount() > count)
                count = getLineCount();

            Rect r = mRect;
            Paint paint = mPaint;
            int baseline = getLineBounds(0, r);

            for (int i = 0; i < count; i++) {

                canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
                baseline += getLineHeight();

            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.noteedit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                if (note != null) {
                    note.close();
                    note = null;
                }
                if (mRowId != null) {
                    mDbHelper.deleteNote(mRowId);
                }
                finish();
                return true;
            case R.id.menu_save:
                saveState();
                finish();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivityForResult(intent, SETTING_INFO);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        if (!body.trim().equals("")) {
            if (title.trim().equals(""))
                if (body.length() > 10) {
                    title = body.substring(0, 10);
                } else
                    title = body;

            if (mRowId == null) {

                long id = mDbHelper.createNote(title, body, curDate);
                if (id > 0) {
                    mRowId = id;
                } else {
                    Log.e("saveState", "failed to create note");
                }
            } else {
                if (!mDbHelper.updateNote(mRowId, title, body, curDate)) {
                    Log.e("saveState", "failed to update note");
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTING_INFO) {
            updateNoteText();
        }
    }

    private void populateFields() {
        if (mRowId != null) {
            note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            setTitle(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            curText = note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
        }
    }

    private void updateNoteText() {

        //Log.e("Main","UpdateNoteText");
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean("pref_text_bold", false)) {

            mBodyText.setTypeface(null, Typeface.BOLD);
            //    Log.e("Main","BOLD");
        } else {
            mBodyText.setTypeface(null, Typeface.NORMAL);
            //  Log.e("Main", "Normal Text");
        }
        if (sharedPreferences.getBoolean("pref_text_italic", false)) {

            //Log.e("Main", "Italic");
            mBodyText.setTypeface(null, Typeface.ITALIC);
        }
        if ((sharedPreferences.getBoolean("pref_text_italic", false)) && (sharedPreferences.getBoolean("pref_text_bold", false))) {

            //Log.e("Main","BOLD ITALIC");
            mBodyText.setTypeface(null, Typeface.BOLD_ITALIC);
        }

        //Log.e("Main", "color");
        String colorString = sharedPreferences.getString("pref_text_color", "1");

        int color = Integer.parseInt(colorString);
        switch (color) {
            case 1:
                mBodyText.setTextColor(Color.BLACK);
                break;
            case 2:
                mBodyText.setTextColor(Color.GRAY);
                break;
            case 3:
                mBodyText.setTextColor(Color.RED);
                break;
            case 4:
                mBodyText.setTextColor(Color.GREEN);
                break;
            case 5:
                mBodyText.setTextColor(Color.BLUE);
                break;
            case 6:
                mBodyText.setTextColor(Color.MAGENTA);
                break;
        }

        String textSizeStr = sharedPreferences.getString("pref_text_size", "16");
        float textSizeFloat = Float.parseFloat(textSizeStr);
        mBodyText.setTextSize(textSizeFloat);

    }


}
