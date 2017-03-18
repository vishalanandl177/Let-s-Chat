package com.anand.vishal.uietcommunication;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener {


    Button done;
    EditText edit;
    SQLiteDatabase database;
    FeedBackHolder holder = null;
    String name, roll_no, email;
    Firebase fRef;


    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);

        fRef = new Firebase("https://uiet-chat-app.firebaseio.com/feedback");
        edit = (EditText) rootView.findViewById(R.id.feedback_edit_text);
        done = (Button) rootView.findViewById(R.id.feedback_done_button);

        done.setOnClickListener(this);

        try {
            database = getActivity().openOrCreateDatabase("userDetails", android.content.Context.MODE_PRIVATE, null);
            Log.e("user Details", "db open");
        } catch (Exception e) {
            e.printStackTrace();
        }
        userDataFetch();

        return rootView;
    }

    private void userDataFetch() {
        // Query all tye information of user stored in the database
        try {

            // Cursor is responsible to store the information after the query
            Cursor cursor = database.rawQuery("SELECT * FROM userdata;", null);

            // These variables store the index of the all the columns(attributes) of the table in the database

            int nameColumn = cursor.getColumnIndex("name");
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
                    roll_no = cursor.getString(rollColumn);
                    email = cursor.getString(emailColumn);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("splashScreen", "no table found");
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        String message = edit.getText().toString();
        if (!message.trim().equals("")) {
            holder = new FeedBackHolder(name, roll_no, email, message);
        }

        pushHolderToDataBase(holder);
        edit.setText("");

    }

    private void pushHolderToDataBase(FeedBackHolder holder) {

        fRef.push().setValue(holder);

        Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT).show();

    }
}
