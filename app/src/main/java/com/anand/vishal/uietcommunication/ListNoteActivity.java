package com.anand.vishal.uietcommunication;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ListNoteActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private static final int DELETE_ID = Menu.FIRST;
    private int mNoteNumber = 1;
    private static final int SETTING_INFO = 1;

    private NotesDbAdapter mDbHelper;
    ListView listView;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //adapter = new ArrayAdapter<String>(this, R.layout.notes_row, listItems);
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(ListNoteActivity.this);
        listView.setOnItemLongClickListener(ListNoteActivity.this);

        setTitle("Notes");
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        fillData();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNote();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notelist_menu, menu);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings1:
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNote() {
        Intent i = new Intent(this, NoteEditActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        Intent i = new Intent(this, NoteEditActivity.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(i, ACTIVITY_EDIT, ActivityOptions.makeSceneTransitionAnimation(ListNoteActivity.this).toBundle());
        } else {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            startActivityForResult(i, ACTIVITY_EDIT);

        }
    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);


        String[] from = new String[]{NotesDbAdapter.KEY_TITLE, NotesDbAdapter.KEY_DATE};
        int[] to = new int[]{R.id.text1, R.id.date_row};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        listView.setAdapter(notes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
        AlertDialog.Builder theDialog = new AlertDialog.Builder(ListNoteActivity.this);
        theDialog.setMessage("Delete this note?");
        theDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDbHelper.deleteNote(id);
                fillData();
            }
        });
        theDialog.setNegativeButton("Cancel",null);

        theDialog.show();
        return true;
    }

}
