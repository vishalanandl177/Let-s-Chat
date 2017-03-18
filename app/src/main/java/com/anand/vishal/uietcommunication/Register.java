package com.anand.vishal.uietcommunication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class Register extends AppCompatActivity {

    private View mProgressView;
    EditText name, roll_no, email, password, retry_password;
    String user_name, user_branch, user_year, user_roll_no, user_email, user_password, user_retry;
    Button register;
    ProgressBar progressBar;
    Spinner spinner, yearSpinner;

    private View mRegisterForm;

    DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    OvershootInterpolator overshootInterpolator = new OvershootInterpolator(10f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Register");

        //Initialize all editable text
        mProgressView = findViewById(R.id.sign_progress);

        name = (EditText) findViewById(R.id.name);
        spinner = (Spinner) findViewById(R.id.bSpinner);
        yearSpinner = (Spinner) findViewById(R.id.ySpinner);
        roll_no = (EditText) findViewById(R.id.rollnumber);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        retry_password = (EditText) findViewById(R.id.retry_password);
        register = (Button) findViewById(R.id.register_button);
        progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
        mRegisterForm = (View) findViewById(R.id.register_form);

        ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(this,
                R.array.branch, android.R.layout.simple_spinner_item);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(branchAdapter);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.year, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    TextView tx = (TextView) view;
                    user_year = tx.getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                user_year = "First";
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    TextView tx = (TextView) view;
                    user_branch = tx.getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                user_branch = "CSE";
            }
        });


        //Register new user

        register.animate().setDuration(200);
        register.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    register.animate().setInterpolator(decelerateInterpolator)
                            .scaleX(.7f).scaleY(.7f);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    register.animate().setInterpolator(overshootInterpolator)
                            .scaleX(1f).scaleY(1f);
                }
                return false;
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_name = name.getText().toString();
                user_roll_no = roll_no.getText().toString().trim();
                user_email = email.getText().toString().trim();
                user_password = password.getText().toString().trim();
                user_retry = retry_password.getText().toString().trim();

                if (user_name.trim().length() < 5) {
                    name.setError(getString(R.string.error_invalid_username));
                    name.requestFocus();
                } else if (user_roll_no.trim().length() != 11) {
                    roll_no.setError(getString(R.string.error_invalid_roll));
                    roll_no.requestFocus();
                } else if (!user_email.contains("@") || !user_email.contains(".") || user_email.length() < 5 || user_email.contains(" ")) {
                    email.setError(getString(R.string.error_invalid_email));
                    email.requestFocus();
                } else if (user_password.length() < 5) {
                    password.setError(getString(R.string.error_invalid_password));
                    password.requestFocus();
                } else if (user_password.equals(user_retry)) {
                    String method = "register";
                    showProgress(true);

                    // createUser(user_email, user_password);
                    RegisterInBackground registerInBackground = new RegisterInBackground(Register.this);
                    registerInBackground.execute(method, user_name, user_branch, user_roll_no, user_email, user_password, user_year);
                } else {
                    password.setError(getString(R.string.error_incorrect_password_match));
                    password.requestFocus();
                }
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void createUser(final String email, String pass) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,
                pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Log.e("Successful in ", " creating a new account" + task.isSuccessful());
                    Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
                 /*  firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()) {
                               Log.e("send Email", "Email sent.");
                           }
                       }
                   });
                    */
                    showProgress(false);
                    finish();
                } else {
                    Log.e("task Email Already taken", "email taken " + task);
                    Toast.makeText(Register.this, "Error or Email Already taken", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            }
        });
    }


    /*  @Override
      public boolean onOptionsItemSelected(MenuItem item) {
          onBackPressed();
          return true;
      }
  */
    private class RegisterInBackground extends AsyncTask<String, Void, String> {

        AlertDialog.Builder alertDialog;
        Context ctx;

        RegisterInBackground(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(ctx);
            alertDialog.setTitle("Registration Information!");

        }


        @Override
        protected String doInBackground(String... params) {
            String reg_url = "http://uietchatzone.3eeweb.com/Register.php";
            String method = params[0];

            String name = params[1];
            String branch = params[2];
            String rollNo = params[3];
            String email = params[4];
            String pass = params[5];
            String year = params[6];

            if (!UserAlreadyExist(rollNo)) {

                try {
                    URL url = new URL(reg_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setConnectTimeout(20000);
                    OutputStream OS = httpURLConnection.getOutputStream();

                    BufferedWriter bufferedWriter =
                            new BufferedWriter(new OutputStreamWriter(OS));

                    String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                            URLEncoder.encode("branch", "UTF-8") + "=" + URLEncoder.encode(branch, "UTF-8") + "&" +
                            URLEncoder.encode("roll_no", "UTF-8") + "=" + URLEncoder.encode(rollNo, "UTF-8") + "&" +
                            URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                            URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8") + "&" +
                            URLEncoder.encode("year", "UTF-8") + "=" + URLEncoder.encode(year, "UTF-8") + "&" +
                            URLEncoder.encode("verify", "UTF-8") + "=" + URLEncoder.encode("no", "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();

                    InputStream IS = httpURLConnection.getInputStream();
                    IS.close();

                    return "Registration Successful";

                } catch (SocketTimeoutException e) {
                    return "Connection Timeout";
                } catch (MalformedURLException e) {

                    return "Error!";

                } catch (IOException e) {
                    e.printStackTrace();
                    return "Error!";
                }

            } else
                return "Roll number already exist!";
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        private boolean UserAlreadyExist(String roll) {

            String log_url = "http://uietchatzone.3eeweb.com/validateuser.php";

            try {
                URL url = new URL(log_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setConnectTimeout(20000);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("roll_no", "UTF-8") + "=" + URLEncoder.encode(roll, "UTF-8");

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

                return readJSONroll(roll, response);

            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                Toast.makeText(ctx, "Time out", Toast.LENGTH_SHORT).show();
            } catch (MalformedURLException e) {
                Log.e("da", "Something went wrong\\nPlease try again");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("da", "Something went wrong\\nPlease try again1");
                e.printStackTrace();
            } catch (Exception e) {
                Log.e("da", "Something went wrong\\nPlease try again2");
                e.printStackTrace();

            }
            return false;
        }

        private boolean readJSONroll(String roll, String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);

                return roll.equals(jsonObject.getString("roll_no"));
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }


        protected void onPostExecute(String result) {
            try {
                showProgress(false);

                if (result.equals("Registration Successful")) {
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

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
            mRegisterForm.setVisibility(show ? View.GONE : View.VISIBLE);

        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterForm.setVisibility(show ? View.GONE : View.VISIBLE);

        }
    }


}
