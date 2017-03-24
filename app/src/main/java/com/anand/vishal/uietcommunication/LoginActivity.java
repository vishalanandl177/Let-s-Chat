package com.anand.vishal.uietcommunication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    Intent intentMain;
    TextView forgotPassword;

    DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    OvershootInterpolator overshootInterpolator = new OvershootInterpolator(10f);

    GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;
    int RC_SIGN_IN = 1;

    Button mEmailSignInButton;
    SQLiteDatabase database;
    String user_email;

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intentMain = new Intent(LoginActivity.this, ChatActivity.class);

        try {
            database = this.openOrCreateDatabase("userDetails", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS userdata(id integer primary key AUTOINCREMENT,name VARCHAR,branch VARCHAR,year VARCHAR,roll_no VARCHAR,email VARCHAR);");
        } catch (Exception e) {
            e.printStackTrace();
        }
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        signInButton = (SignInButton) findViewById(R.id.sin_in_google);
        signInButton.setOnClickListener(this);


        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.roll);
        // populateAutoComplete();
        setTitle("Login");


        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, ResetPassword.class));
                //finish();
            }
        });


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if ((id == R.id.login || id == EditorInfo.IME_NULL)) {
                    attemptLogin();
                    return true;
                }
                //    Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        assert mEmailSignInButton != null;

        mEmailSignInButton.animate().setDuration(200);
        mEmailSignInButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mEmailSignInButton.animate().setInterpolator(decelerateInterpolator)
                            .scaleX(.7f).scaleY(.7f);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mEmailSignInButton.animate().setInterpolator(overshootInterpolator)
                            .scaleX(1f).scaleY(1f);
                }
                return false;
            }
        });


        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager cManeger = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo ninfo = cManeger.getActiveNetworkInfo();
                if (ninfo == null) {
                    Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else
                    attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                BitmapDrawable drawable = (BitmapDrawable) fab.getDrawable();
                Bitmap bm = drawable.getBitmap();
                Intent intent = new Intent(LoginActivity.this, Register.class);
                Bundle scaleBundle = ActivityOptions.makeThumbnailScaleUpAnimation(
                        fab, bm, 0, 0).toBundle();
                startActivity(intent, scaleBundle);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(LoginActivity.this, AboutPage.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
            } else {
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String roll_no = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (/*!TextUtils.isEmpty(password) && */!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid roll_no address.
        if (TextUtils.isEmpty(roll_no)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(roll_no)) {
            mEmailView.setError(getString(R.string.error_invalid_roll));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // Toast.makeText(LoginActivity.this, "showProcess", Toast.LENGTH_SHORT).show();
            showProgress(true);
            //mAuthTask = new UserLoginTask(roll_no, password);
            //mAuthTask.execute((Void) null);

            String method = "login";

            // login(roll_no, password);
            LoginBackground login = new LoginBackground(this);
            login.execute(method, roll_no.trim(), password);

        }
    }

    public void login(final String email, String pass) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e("Login", "signInWithEmail:onComplete:" + task.isSuccessful());

                        showProgress(false);

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        intentMain.putExtra("passedName", "");
                        intentMain.putExtra("passedEmail", email);
                        intentMain.putExtra("passedBranch", "");
                        intentMain.putExtra("passedRoll", "");
                        intentMain.putExtra("passedYear", "");

                        startActivity(intentMain);
                        finish();

                        if (!task.isSuccessful()) {
                            Log.e("Login", "signInWithEmail", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private boolean isEmailValid(String roll) {
        //TODO: Replace this with your own logic
        return (roll.trim().length() == 11);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        signIn();

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            final GoogleSignInAccount acc = result.getSignInAccount();
            intentMain.putExtra("passedName", acc.getDisplayName());
            intentMain.putExtra("passedEmail", acc.getEmail());
            intentMain.putExtra("passedBranch", "");
            intentMain.putExtra("passedRoll", "");
            intentMain.putExtra("passedYear", "");
            intentMain.putExtra("passedID", String.valueOf(acc.getId()));

            try {
                database.execSQL("INSERT INTO userdata (name,branch,roll_no,email,year,photo_url) VALUES('" + acc.getDisplayName() +
                        "','" + "" + "','" + "" + "','" + acc.getEmail() + "','" + "" + "','" + String.valueOf(acc.getPhotoUrl()) + "'" + ");");
                Log.e("LoginActivity", "data inserted");
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(intentMain);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(LoginActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
    }


    private class LoginBackground extends AsyncTask<String, Void, String> {

        AlertDialog.Builder alertDialog;
        Context ctx;

        LoginBackground(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(ctx);
            alertDialog.setTitle("Login Information!");

        }


        @Override
        protected String doInBackground(String... params) {
            // Here use server side php script url that is responsible to retrieve user login information
            String log_url = "yourUrl/retrieve.php";

            String method = params[0];
            String login_roll_no = params[1];
            String login_pass = params[2];

            try {
                URL url = new URL(log_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setConnectTimeout(20000);

                OutputStream outputStream = null;
                BufferedWriter buff = null;
                String data = null;
                try {
                    outputStream = httpURLConnection.getOutputStream();
                    buff = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    data = URLEncoder.encode("roll_no", "UTF-8") + "=" + URLEncoder.encode(login_roll_no, "UTF-8") + "&" +
                            URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(login_pass, "UTF-8");
                } catch (IOException e) {
                    Log.e("IOException", e.getMessage());
                    e.printStackTrace();
                }

                buff.write(data);
                buff.flush();
                buff.close();
                outputStream.close();

                //get response from server
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line = "";
                while ((line = bf.readLine()) != null) {
                    response += line;
                }
                bf.close();
                inputStream.close();
                httpURLConnection.disconnect();
                Log.e("Response    ", response);

                return response;


            } catch (SocketTimeoutException e) {
                e.printStackTrace();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Connection Timeout";
                //      Toast.makeText(ctx, "Something went wrong\nPlease try again", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
                //      Toast.makeText(ctx, "Something went wrong\nPlease try again", Toast.LENGTH_SHORT).show();
            } catch (Exception r) {
                r.printStackTrace();
            }
            // }


            return "Error";
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected void onCancelled() {
            showProgress(false);
        }

        @Override
        protected void onPostExecute(String result) {

            if (!result.equals("[]")) {
                if (ReadJsonData(result)) {

                    showProgress(false);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(intentMain, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
                    } else {
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        startActivity(intentMain);
                    }
                    finish();

                } else {
                    alertDialog.setMessage("Your email is not verified");
                    alertDialog.setNegativeButton("Cancel",null);
                    alertDialog.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        // Send Email to verify.
                            sendEmail();

                            //finish();
                        }
                    });
                    alertDialog.show();
                    showProgress(false);


                }
            } else {

                alertDialog.setMessage("Check your roll number or password");
                alertDialog.setPositiveButton("Retry", null);
                alertDialog.show();
                showProgress(false);
            }
        }

        private boolean ReadJsonData(String resultToDecode) {
            try {

                JSONObject object = new JSONObject(resultToDecode);
                Log.e("JSON", " read json data");

                String name = object.getString("name");
                String branch = object.getString("branch");
                String roll_no = object.getString("roll_no");
                String email = object.getString("email");
                user_email = email;
                String year = object.getString("year");
                String verify = object.getString("verify");
                if (verify.equals("no"))
                    return false;

                intentMain.putExtra("passedName", name);
                intentMain.putExtra("passedBranch", branch);
                intentMain.putExtra("passedRoll", roll_no);
                intentMain.putExtra("passedEmail", email);
                intentMain.putExtra("passedYear", year);

                try {
                    database.execSQL("INSERT INTO userdata (name,branch,roll_no,email,year) VALUES('" + name +
                            "','" + branch + "','" + roll_no + "','" + email + "','" + year + "');");
                    Log.e("LoginActivity", "data inserted");
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

    }
    private void sendEmail() {
        String email = user_email;

            boolean networkState = true;
            ConnectivityManager cManeger = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo ninfo = cManeger.getActiveNetworkInfo();
            if (ninfo == null) {

                // If no internet connection available
                networkState = false;
                Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

            }

            if (networkState) {
                showProgress(true);
                ResetInBackground registerInBackground = new ResetInBackground(LoginActivity.this);
                registerInBackground.execute("Reset", email);
            }else {
                Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

            }

    }
    private class ResetInBackground extends AsyncTask<String, Void, String> {

        AlertDialog.Builder alertDialog;
        Context ctx;

        ResetInBackground(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(ctx);
            alertDialog.setTitle("Email verification");
        }


        @Override
        protected String doInBackground(String... params) {
           
            // If user's email is not verified then use a php script to send mail to user's registered email address
            String reg_url = "yourUrl/verify.php";
            String method = params[0];

            String email = params[1];

            if (userEmailRegistered(email)) {
                try {
                    URL url = new URL(reg_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setConnectTimeout(20000);
                    OutputStream OS = httpURLConnection.getOutputStream();

                    BufferedWriter bufferedWriter =
                            new BufferedWriter(new OutputStreamWriter(OS));

                    String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();

                    InputStream IS = httpURLConnection.getInputStream();
                    IS.close();

                    return "Email sent";

                } catch (SocketTimeoutException e) {
                    return "Connection Timeout";
                } catch (MalformedURLException e) {

                    return "Error!";

                } catch (IOException e) {
                    e.printStackTrace();
                    return "Error!";
                } catch (Exception e) {
                    return "Email not found";
                }
            } else {
                return "Email not registered.";
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String result) {
            try {
                showProgress(false);
                if (result.equals("Email sent")) {
                    Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
                   // finish();
                } else {
                    alertDialog.setMessage(result);
                    alertDialog.setPositiveButton("Retry?", null);
                    alertDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean userEmailRegistered(String email) {
            // If user's email already register then display user to email already registered.
            String log_url = "http://yourUrl/emailexist.php";

            try {
                URL url = new URL(log_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setConnectTimeout(20000);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

                buff.write(data);
                buff.flush();
                buff.close();
                outputStream.close();

                //get response from server
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line = "";
                while ((line = bf.readLine()) != null) {
                    response += line;
                }
                bf.close();
                inputStream.close();
                httpURLConnection.disconnect();
                Log.e("Response is ", response);
                return readJSON(response, email);


            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return false;

            } catch (MalformedURLException e) {
                Log.e("da", "Something went wrong\\nPlease try again");
                e.printStackTrace();
                return false;

            } catch (IOException e) {
                Log.e("da", "Something went wrong\\nPlease try again1");
                e.printStackTrace();
                return false;

            } catch (Exception e) {
                Log.e("da", "Something went wrong\\nPlease try again2");
                e.printStackTrace();
                return false;

            }
        }

        private boolean readJSON(String response, String email) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("email").equals(email))
                    return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }
    }

}

