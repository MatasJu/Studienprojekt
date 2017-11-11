package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //This constant uses the name of the class itself as the tag.
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void launchWhatDay(View view) {
        Log.d(LOG_TAG, "Button clicked!");
        Intent intent = new Intent(this, WhatDay.class);
        startActivity(intent);
    }


}
