package com.anand.vishal.uietcommunication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseException;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;

public class Notification extends AppCompatActivity {


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView notification;
        public TextView date;

        public MessageViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            notification = (TextView) v.findViewById(R.id.notification_message);
            date = (TextView) v.findViewById(R.id.date);
        }
    }


    private FirebaseRecyclerAdapter<NotificationHolder, MessageViewHolder> mFirebaseAdapter;
    
    // Here use firebase database url/notification to store notifications
    String url = "https:YourUrl/notifications";
    Firebase fRef;
    String UserName, UserBranch, UserYear, UserRollNo, UserEmail;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.up_from_bottom);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        UserName = intent.getStringExtra("passedName");
        UserBranch = intent.getStringExtra("passedBranch");
        UserRollNo = intent.getStringExtra("passedRoll");
        UserEmail = intent.getStringExtra("passedEmail");
        UserYear = intent.getStringExtra("passedYear");

        try {
            switch (UserBranch) {
                case "CSE":
                    url += "/cse";
                    break;
                case "IT":
                    url += "/it";
                    break;
                case "MEE":
                    url += "/mee";
                    break;
                case "ECE":
                    url += "/ece";
                    break;
                case "CHE":
                    url += "/che";
                    break;
                case "MSME":
                    url += "/msme";
                    break;
                default:
                    url += "/cse";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (UserYear) {
            case "First":
                url += "/first";
                break;
            case "Second":
                url += "/second";
                break;
            case "Third":
                url += "/third";
                break;
            case "Fourth":
                url += "/fourth";
                break;
            default:
                url += "/fourth";
        }


        //Firebase.setAndroidContext(this);
        if (!Firebase.getDefaultConfig().isPersistenceEnabled()) {
            Firebase.getDefaultConfig().setPersistenceEnabled(true);
            Log.e("Persistence", "Enabled");
        } else {
            Log.e("Persistence", "Already Enabled");
        }

        try {
            fRef = new Firebase(url);
            fRef.keepSynced(true);

        } catch (FirebaseException e) {
            Log.e("Firebase", e.getMessage());
            AlertDialog.Builder alertDialog;
            alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Something went wrong");
            alertDialog.setPositiveButton("Retry", null);
            alertDialog.show();
        }


        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);

        Query recent = fRef.limitToLast(20);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<NotificationHolder, MessageViewHolder>(

                NotificationHolder.class,
                R.layout.row_notification,
                MessageViewHolder.class,
                recent) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, NotificationHolder holder, int position) {
                viewHolder.title.setText(holder.getName());
                viewHolder.notification.setText(holder.getMessage());
                viewHolder.date.setText(holder.getDate());
                viewHolder.title.startAnimation(animation);
                viewHolder.notification.startAnimation(animation);
                viewHolder.date.startAnimation(animation);

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mMessageRecyclerView.scrollToPosition(positionStart);

            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
