package com.thulani.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.thulani.messenger.data.Tools;
import com.thulani.messenger.utils.Preferences;

import java.util.Timer;
import java.util.TimerTask;

public class ActivitySplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (Preferences.getPrefName(ActivitySplash.this, "username").equals("")) {
                    startActivity(new Intent(ActivitySplash.this, ActivityRegister.class));
                    finish();
                } else {
                    Intent i = new Intent(getApplicationContext(), ActivityMain.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        // Show splash screen for 3 seconds
        new Timer().schedule(task, 2000);

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }
}
