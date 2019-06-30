package com.thulani.messenger;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MessengerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
