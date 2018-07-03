package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.os.Bundle;
import android.os.CpuUsageInfo;
import android.util.Log;
import android.view.View;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;
import de.haw_landshut.studienprojekt.MotionSensor;
import de.haw_landshut.studienprojekt.settings.AndroidBaseActivity;
import de.haw_landshut.studienprojekt.settings.Profile_Settings;

public class verletzung extends AndroidBaseActivity {


    MotionSensor ms;

    Button motionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verletzung);
        MotionSensor.instantiate(this);


        ms = MotionSensor.getMotionSensor();
        ms.isMovement();
        ms.isWalking();

        motionBtn = findViewById(R.id.motionBtn);
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
            case R.id.motionBtn:
                calcMotion();
                break;
        }
    }

    public void menüverletzungButtonEventHandler(View view) {
        Intent intent = new Intent(this,Profile_Settings.class);
        startActivity(intent);
        setContentView(R.layout.activity_profile_settings);
    }

    private void calcMotion() {

        Log.d("test", "calcMotion: ");

        if (ms.isMovement()){
            motionBtn.setBackgroundColor(Color.GREEN);
        }else {
            motionBtn.setBackgroundColor(Color.GREEN);
        }

        // motion = findViewById(R.id.motionBtn);
        // motion.backgroundTint(this.motion.redColor);


    }

//    private void calcWalking() {
//
//        if (isWalking()==true){
//            walkingBtn.setBackgroundTint(R.color.greenColor);
//        }else {
//            walkingBtn.setBackgroundTint(R.color.redColor);
//        }
//    }

}

