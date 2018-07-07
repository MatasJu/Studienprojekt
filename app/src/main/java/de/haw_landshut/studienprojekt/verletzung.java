package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.os.Bundle;
import android.os.CpuUsageInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;
import de.haw_landshut.studienprojekt.MotionSensor;
import de.haw_landshut.studienprojekt.settings.AndroidBaseActivity;
import de.haw_landshut.studienprojekt.settings.Profile_Settings;

public class verletzung extends AndroidBaseActivity {

    private static final String TAG = "verletzung";

    MotionSensor ms;
    MotionSensor ws;

    Button motionBtn;
    Button walkingBtn;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verletzung);
        MotionSensor.instantiate(this);

        // getting boolean for motion and walking
        ms = MotionSensor.getMotionSensor();
        ws = MotionSensor.getMotionSensor();

        motionBtn = findViewById(R.id.motionBtn);
        walkingBtn = findViewById(R.id.walkingBtn);

        // Starts thread for switching color
        MotionWalkingThread thread = new MotionWalkingThread(100000);
        thread.start();
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

    public void menüverletzungButtonEventHandler(View view) {
        Intent intent = new Intent(this,gefuehle.class);
        startActivity(intent);
    }

    // Switching MotionButton Color - Green / Red
    public void calcMotion() {
        Log.d("test", "calcMotion: ");

        if (ms.isMovement()){
            motionBtn.setBackgroundColor(Color.GREEN);
        }else {
            motionBtn.setBackgroundColor(Color.RED);
        }

    }

    // Switching WalkingButton Color - Green / Red
    public void calcWalking() {
        Log.d("test", "calcWalking: ");

        if (ws.isWalking()){
            walkingBtn.setBackgroundColor(Color.GREEN);
        }   else {
            walkingBtn.setBackgroundColor(Color.RED);
        }

    }


   /* public void startThread(View view)  {

        // 100000 seconds = ~ 27 hours runtime
         MotionWalkingThread thread = new MotionWalkingThread(100000);
         thread.start();
    }*/

    // Checks every second the walking and motion value
    class MotionWalkingThread extends Thread {
        int seconds;

        MotionWalkingThread(int seconds) {
            this.seconds = seconds;
        }

        @Override
        public void run() {
            for (int i = 0; i < seconds; i++) {
                calcWalking();
                calcMotion();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

