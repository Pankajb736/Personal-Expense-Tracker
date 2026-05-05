package com.example.finora;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            // Enable disk persistence once per app process
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception ignored) {
            // Firebase may throw if called more than once in same process during hot-reload; safe to ignore
        }
    }
}
