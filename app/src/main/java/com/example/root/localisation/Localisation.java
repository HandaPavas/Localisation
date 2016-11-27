package com.example.root.localisation;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by root on 11/28/16.
 */
public class Localisation extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
