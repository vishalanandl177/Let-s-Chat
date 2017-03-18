package com.anand.vishal.uietcommunication;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

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

public class ResetPassword extends AppCompatActivity {

    EditText Email;//, Password, rePassword;
    Button Proceed;
    String email;//, pass, repass;
    ProgressBar mProgressView;
    DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    OvershootInterpolator overshootInterpolator = new OvershootInterpolator(10f);

    private View mResetForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Email = (EditText) findViewById(R.id.emailEditText);
        // Password = (EditText) findViewById(R.id.passwordEditText);
        //rePassword = (EditText) findViewById(R.id.retry_passwordEditText);
        mProgressView = (ProgressBar) findViewById(R.id.sign_progress);
        mResetForm = (View) findViewById(R.id.reset_form);

        Proceed = (Button) findViewById(R.id.proceed);
        Proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // resetPass();
                resetPassword();

            }
        });

        Proceed.animate().setDuration(200);
        Proceed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Proceed.animate().setInterpolator(decelerateInterpolator)
                            .scaleX(.7f).scaleY(.7f);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Proceed.animate().setInterpolator(overshootInterpolator)
                            .scaleX(1f).scaleY(1f);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void resetPass(){
        Firebase fref = new Firebase("https://uiet-chat-app.firebaseio.com/");
        fref.resetPassword(Email.getText().toString().trim(), new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {

                Log.e("Password","reset successful");
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Log.e("Password","reset error " + firebaseError);
            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetPassword() {
        email = Email.getText().toString();
        if (email.contains(".") && email.contains("@") && !email.contains(" ") && email.length() > 3 && !email.contains(" ")) {

            boolean networkState = true;
            ConnectivityManager cManeger = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo ninfo = cManeger.getActiveNetworkInfo();
            if (ninfo == null) {

                // If no internet connection available
                networkState = false;
                Toast.makeText(ResetPassword.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

            }

            if (networkState) {
                showProgress(true);
                ResetInBackground registerInBackground = new ResetInBackground(ResetPassword.this);
                registerInBackground.execute("Reset", email);
            }else {
                Toast.makeText(ResetPassword.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

            }
        } else {

            Email.setError(getString(R.string.error_invalid_email));
            Email.requestFocus();
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
            alertDialog.setTitle("No Email Found!");
        }


        @Override
        protected String doInBackground(String... params) {
            String reg_url = "http://chatzone.netau.net/mail.php";
            String method = params[0];
            // if (method.equals("register")) {

            //user_name,user_branch,user_roll_no,user_email,user_password
            String email = params[1];
            //String pass = params[2];


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
                    finish();
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

            String log_url = "http://uietchatzone.3eeweb.com/emailexist.php";

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

    private void showProgress(final boolean show) {

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mResetForm.setVisibility(show ? View.GONE : View.VISIBLE);

    }

}
