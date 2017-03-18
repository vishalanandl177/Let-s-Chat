package com.anand.vishal.uietcommunication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NoInternet extends Activity implements View.OnClickListener {

    Button Retry;
    Button Exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        Retry = (Button)findViewById(R.id.retry);
        Exit = (Button)findViewById(R.id.exit);

        Retry.setOnClickListener(this);
        Exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exit:
                finish();
                break;
            case R.id.retry:
                startActivity(new Intent(NoInternet.this,LoginActivity.class));
                finish();
                break;
        }
    }
}
