package com.anand.vishal.uietcommunication;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseException;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatActivity extends AppCompatActivity implements Serializable, AdapterView.OnItemLongClickListener {


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView message;
        public TextView date;

        View views;

        public View getViews() {
            return views;
        }

        public MessageViewHolder(View v) {
            super(v);
            views = v;
            message = (TextView) v.findViewById(R.id.messageTextView);
            name = (TextView) v.findViewById(R.id.nameTextView);
            date = (TextView) v.findViewById(R.id.dateTextView);
        }


    }

    String UserName = "", UserBranch = "", UserYear = "", UserRollNo = "", UserEmail = "";
    private static final int ACTIVITY_EDIT = 1;
    Button sendBtn;
    EditText messageText;
    ListView messageList;
    Firebase fRef;

    SQLiteDatabase database;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (!Firebase.getDefaultConfig().isPersistenceEnabled()) {
            Firebase.getDefaultConfig().setPersistenceEnabled(true);
            Log.e("Persistence", "Enabled");
        } else {
            Log.e("Persistence", "Already Enabled");
        }
        UserName = intent.getStringExtra("passedName");
        UserBranch = intent.getStringExtra("passedBranch");
        UserRollNo = intent.getStringExtra("passedRoll");
        UserEmail = intent.getStringExtra("passedEmail");
        UserYear = intent.getStringExtra("passedYear");

        setTitle(getString(R.string.app_name));

        try {
            database = this.openOrCreateDatabase("userDetails", MODE_PRIVATE, null);
            Log.e("user Details", "db open");
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendBtn = (Button) findViewById(R.id.send_message);
        messageText = (EditText) findViewById(R.id.new_message);
        // Enter your firebaseio url
        String url = "https://yourlink";
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

        try {
            fRef = new Firebase(url);
            fRef.keepSynced(true);
        } catch (FirebaseException e) {
            Log.e("Firebase", e.getMessage());
            AlertDialog.Builder alertDialog;
            alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Something went wrong");
            alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                    finish();
                }
            });
            alertDialog.show();
        }
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String temp = messageText.getText().toString();
                if (temp.trim().length() > 0) {
                    while (temp.startsWith(" ") || temp.startsWith("\n")) {
                        temp = temp.substring(1, temp.length());
                    }
                    while (temp.endsWith(" ") || temp.endsWith("\n")) {
                        temp = temp.substring(0, temp.length() - 1);
                    }

                    ChatMessage chat = new ChatMessage(UserName, temp, UserRollNo);
                    fRef.push().setValue(chat);
                }

                messageText.setText("");
            }
        });
        Query recent = fRef.limitToLast(100);

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mMessageRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(

                ChatMessage.class,
                R.layout.row,
                MessageViewHolder.class,
                recent) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, ChatMessage holder, int position) {

                viewHolder.message.setText(holder.getMessage());
                viewHolder.date.setText(holder.getDate());

                ViewHolder finalHolder = createViewHolder(viewHolder.views);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) finalHolder.content.getLayoutParams();

                if (!holder.getRollno().equals(UserRollNo)) {
                    layoutParams.gravity = Gravity.LEFT;
                    finalHolder.content.setLayoutParams(layoutParams);
                    viewHolder.message.setBackgroundResource(R.drawable.out_message_bg);
                    viewHolder.message.setTextColor(getResources().getColor(R.color.black));
                    viewHolder.name.setVisibility(View.VISIBLE);
                    viewHolder.name.setText(holder.getName());


                } else {
                    layoutParams.gravity = Gravity.RIGHT;
                    finalHolder.content.setLayoutParams(layoutParams);
                    viewHolder.message.setBackgroundResource(R.drawable.in_message_bg);
                    viewHolder.message.setTextColor(getResources().getColor(R.color.black));
                    viewHolder.name.setVisibility(View.GONE);

                }
            }

            private ViewHolder createViewHolder(View views) {
                ViewHolder holder = new ViewHolder();
                holder.content = (RelativeLayout) views.findViewById(R.id.content);
                return holder;
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, final View v, final int position, final long id) {

        final ChatMessage cm = (ChatMessage) messageList.getItemAtPosition(position);

        AlertDialog.Builder theDialog = new AlertDialog.Builder(this);
        theDialog.setMessage("Add this to notes?");
        theDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveToDatabase(cm.getMessage());
                Toast.makeText(ChatActivity.this, "Successful", Toast.LENGTH_SHORT).show();
            }
        });
        theDialog.setNegativeButton("Cancel", null);
        theDialog.show();
        return true;
    }

    private void saveToDatabase(String body) {
        try {
            String title = "";
            NotesDbAdapter mDbHelper = new NotesDbAdapter(this);
            mDbHelper.open();
            long msTime = System.currentTimeMillis();
            Date curDateTime = new Date(msTime);

            SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y");
            String curDate = formatter.format(curDateTime);

            if (!body.trim().equals("")) {
                if (title.trim().equals(""))
                    if (body.length() > 10) {
                        title = body.substring(0, 10);
                    } else
                        title = body;

                long id = mDbHelper.createNote(title, body, curDate);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static class ViewHolder {
        public RelativeLayout content;
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.content = (RelativeLayout) v.findViewById(R.id.content);
        return holder;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent;

        switch (id) {

            case R.id.news:
                intent = new Intent(ChatActivity.this, News.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ChatActivity.this).toBundle());
                } else {
                    overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
                    startActivity(intent);
                }
                break;

            case R.id.note:
                intent = new Intent(ChatActivity.this, ListNoteActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ChatActivity.this).toBundle());
                } else {
                    overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
                    startActivity(intent);
                }
                break;
            case R.id.notification:
                intent = new Intent(ChatActivity.this, Notification.class);

                intent.putExtra("passedName", UserName);
                intent.putExtra("passedBranch", UserBranch);
                intent.putExtra("passedRoll", UserRollNo);
                intent.putExtra("passedEmail", UserEmail);
                intent.putExtra("passedYear", UserYear);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ChatActivity.this).toBundle());
                } else {
                    overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
                    startActivity(intent);
                }
                break;


            case R.id.gall:
                intent = new Intent(ChatActivity.this, StudentGallery.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ChatActivity.this).toBundle());
                } else {
                    overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
                    startActivity(intent);
                }
                break;
            case R.id.editinfo:
                intent = new Intent(ChatActivity.this, EditInfoActivity.class);

                intent.putExtra("passedName", UserName);
                intent.putExtra("passedBranch", UserBranch);
                intent.putExtra("passedRoll", UserRollNo);
                intent.putExtra("passedEmail", UserEmail);
                intent.putExtra("passedYear", UserYear);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ChatActivity.this).toBundle());
                } else {
                    overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
                    startActivity(intent);
                }
                break;
            case R.id.logout:
                try {
                    database.execSQL("DROP TABLE userdata;");
                    Log.e("User data", " table Dropped");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                intent = new Intent(ChatActivity.this, LoginActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ChatActivity.this).toBundle());
                } else {
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    startActivity(intent);
                }
                finish();
                break;
        }

        return true;

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
