package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;

public class verletzung extends AndroidBaseActivity {

    private static final String TAG = "verletzung";

    /**This is the amount of seconds between checks of the Motion Sensors
     */
    private static final int delayToCheckSensorsInSec = 1;


    private MotionSensor motionSensor;
    private Handler checkSensorsHandler = new Handler();
    TextView motionBtn;
    TextView walkingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verletzung);

//        // getting boolean for motion and walking
        motionSensor = MotionSensor.getMotionSensor();
        motionBtn = findViewById(R.id.motionBtn);
        walkingBtn = findViewById(R.id.walkingBtn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

        // Starts thread for switching color
        runOnUiThread(checkMotionWalking);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        checkSensorsHandler.removeCallbacks(checkMotionWalking);
    }

    public void onClickListener(View v){
        Intent intent;
        switch (v.getId()) {

            case R.id.greenBtn2:
                intent = new Intent(getApplicationContext(), gruen.class);
                startActivity(intent);
                break;
            case R.id.yellowBtn2:
                intent = new Intent(getApplicationContext(), kontakte.class);
                startActivity(intent);
                break;
            case R.id.redBtn2:
                intent = new Intent(getApplicationContext(), kontakte_rot.class);
                startActivity(intent);
                break;
        }
    }

    public void menuVerletzungButtonEventHandler(View view) {
        Intent intent = new Intent(this,gefuehle.class);
        startActivity(intent);
    }

    /**Checks the Motion Sensors for Movement state, and sets the buttons to according colors
     */
    public void checkMovement() {
        if (motionSensor.isMovement()){
            Log.d("test", "calcMotion:  true");
            motionBtn.setBackgroundColor(Color.GREEN);
            motionBtn.setTextColor(Color.BLACK);
        }else {
            Log.d("test", "calcMotion: false");
            motionBtn.setBackgroundColor(Color.RED);
            motionBtn.setTextColor(Color.WHITE);
        }

    }

    /**Checks the Motion Sensors for Walking state, and sets the buttons to according colors.
     *
     */
    public void checkWalking() {

        if (motionSensor.isWalking()){
            Log.d("test", "calcWalking: true ");
            walkingBtn.setBackgroundColor(Color.GREEN);
            walkingBtn.setTextColor(Color.BLACK);
        }   else {
            Log.d("test", "calcWalking: false");
            walkingBtn.setBackgroundColor(Color.RED);
            walkingBtn.setTextColor(Color.WHITE);
        }
    }

    /**A runnable to be run on UI Thread for checking the Motion Sensors Regulary
     *
      */
    private Runnable checkMotionWalking = new Runnable() {

        @Override
        public void run() {
            {
                checkWalking();
                checkMovement();
                checkSensorsHandler.postDelayed(this, delayToCheckSensorsInSec * 1000);
            }
        }
    };
}

