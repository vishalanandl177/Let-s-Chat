package com.anand.vishal.uietcommunication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class News extends AppCompatActivity {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView notification;
        public TextView date;

        public MessageViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title_news);
            notification = (TextView) v.findViewById(R.id.message_news);
            date = (TextView) v.findViewById(R.id.date_news);
        }
    }
    private FirebaseRecyclerAdapter<NewsHolder,MessageViewHolder> mFirebaseAdapter;
    String url = "https://uiet-chat-app.firebaseio.com/news";

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    Firebase fRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.up_from_bottom);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.newsRecyclerView);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);

        Query recent = fRef.limitToLast(20);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<NewsHolder, MessageViewHolder>(

                NewsHolder.class,
                R.layout.row_news,
                MessageViewHolder.class,
                recent) {

            @Override
            protected void populateViewHolder(MessageViewHolder messageViewHolder, NewsHolder holder, int position) {
                messageViewHolder.title.setText(holder.getTitle());
                messageViewHolder.notification.setText(holder.getMessage());
                messageViewHolder.date.setText(holder.getDate());
                messageViewHolder.title.startAnimation(animation);
                messageViewHolder.notification.startAnimation(animation);
                messageViewHolder.date.startAnimation(animation);
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
