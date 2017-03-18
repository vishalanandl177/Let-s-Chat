package com.anand.vishal.uietcommunication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

public class EditInfoActivity extends AppCompatActivity {

    EditText name, email;
    String UserName, UserBranch, UserYear, UserRollNo, UserEmail;
    String newName, newBranch, newYear, newEmail;
    TextView roll_no;
    Spinner spinner, yearSpinner;
    ProgressBar mProgressView;
    SQLiteDatabase database;
    FloatingActionButton fab = null;

    private View mEditInfoForm;

    public EditInfoActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText) findViewById(R.id.name);
        spinner = (Spinner) findViewById(R.id.bSpinner);
        yearSpinner = (Spinner) findViewById(R.id.ySpinner);
        email = (EditText) findViewById(R.id.email);
        roll_no = (TextView) findViewById(R.id.rollnumber);
        mEditInfoForm = (View) findViewById(R.id.edit_info_form);
        mProgressView = (ProgressBar) findViewById(R.id.edit_info_progress);

        try {
            database = this.openOrCreateDatabase("userDetails", MODE_PRIVATE, null);
            Log.e("user Details", "db open");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        UserName = intent.getStringExtra("passedName");
        UserBranch = intent.getStringExtra("passedBranch");
        UserRollNo = intent.getStringExtra("passedRoll");
        UserEmail = intent.getStringExtra("passedEmail");
        UserYear = intent.getStringExtra("passedYear");

        name.setText(UserName);
        roll_no.setText(UserRollNo);
        email.setText(UserEmail);


        ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(this,
                R.array.branch, android.R.layout.simple_spinner_item);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(branchAdapter);

        switch (UserBranch) {

            case "CSE":
                spinner.setSelection(0);
                break;
            case "IT":
                spinner.setSelection(1);
                break;
            case "MEE":
                spinner.setSelection(2);
                break;
            case "ECE":
                spinner.setSelection(3);
                break;
            case "CHE":
                spinner.setSelection(4);
                break;
            case "MSME":
                spinner.setSelection(5);
                break;
            default:
                spinner.setSelection(0);
        }

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.year, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        switch (UserYear) {
            case "First":
                yearSpinner.setSelection(0);
                break;
            case "Second":
                yearSpinner.setSelection(1);
                break;
            case "Third":
                yearSpinner.setSelection(2);
                break;
            case "Fourth":
                yearSpinner.setSelection(3);
                break;
            default:
                yearSpinner.setSelection(0);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (valid()) {
                    showProgress(true);
                    Snackbar.make(view, "This may take a minute.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reset();
                }
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private boolean valid() {
        String nameToTest, emailToTest;
        nameToTest = name.getText().toString();
        emailToTest = email.getText().toString();

        if (nameToTest.trim().length() < 3) {
            name.setError(getString(R.string.error_invalid_username));
            name.requestFocus();
            return false;
        } else if (!emailToTest.contains("@") || !emailToTest.contains(".") || emailToTest.length() < 3 || emailToTest.contains(" ")) {
            email.setError(getString(R.string.error_invalid_email));
            email.requestFocus();
            return false;
        } else
            return true;

    }

    private void reset() {
        newName = name.getText().toString();
        newBranch = spinner.getSelectedItem().toString();
        newEmail = email.getText().toString();
        newYear = yearSpinner.getSelectedItem().toString();


        ResetInBackground registerInBackground = new ResetInBackground(EditInfoActivity.this);
        registerInBackground.execute("Reset", newName, newBranch, newEmail, UserRollNo, newYear);
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
            //showProgress(true);

        }


        @Override
        protected String doInBackground(String... params) {
            String reg_url = "http://uietchatzone.3eeweb.com/resetprofile.php";
            String method = params[0];

            String new_name = params[1];
            String new_branch = params[2];
            String new_email = params[3];
            String roll = params[4];
            String y = params[5];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(20000);
                OutputStream OS = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter =
                        new BufferedWriter(new OutputStreamWriter(OS));

                String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(new_name, "UTF-8") + "&" +
                        URLEncoder.encode("branch", "UTF-8") + "=" + URLEncoder.encode(new_branch, "UTF-8") + "&" +
                        URLEncoder.encode("roll_no", "UTF-8") + "=" + URLEncoder.encode(UserRollNo, "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(new_email, "UTF-8") + "&" +
                        URLEncoder.encode("year", "UTF-8") + "=" + URLEncoder.encode(y, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                InputStream IS = httpURLConnection.getInputStream();
                IS.close();

                return "Profile Updated";

            } catch (SocketTimeoutException e) {
                return "Connection Timeout";
            } catch (MalformedURLException e) {

                return "Error!";

            } catch (IOException e) {
                e.printStackTrace();
                return "Error!";
            }


        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        protected void onPostExecute(String result) {
            try {
                showProgress(false);

                if (result.equals("Profile Updated")) {
                    Toast.makeText(ctx, result + "\n" + "Please login again to see the change", Toast.LENGTH_SHORT).show();

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

    private void showProgress(final boolean show) {

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mEditInfoForm.setVisibility(show ? View.GONE : View.VISIBLE);
        fab.setVisibility(show ? View.GONE : View.VISIBLE);

    }

}
