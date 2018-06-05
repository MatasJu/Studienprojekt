package de.haw_landshut.studienprojekt;

import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.View;

public class MainActivity extends AndroidBaseActivity {



    //This constant uses the name of the class itself as the tag.
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MotionSensor.instantiate(this);
        setContentView(R.layout.activity_main);
    }



    /**Start Button OnClick Event Handler
     *  Starts an activity, currently the settings for simulation.
     * @param view - view sent from the button.
     */
    public void startButtonEventHandler(View view) {
        Log.d(LOG_TAG, "StartButton Clicked!");
        Intent intent = new Intent(this, QuestionsActivity.class);

        startActivity(intent);
    }



    public void openMyProfile(View view) {
        Intent intent = new Intent(this,Profile_Settings.class);
        startActivity(intent);
    }




}
