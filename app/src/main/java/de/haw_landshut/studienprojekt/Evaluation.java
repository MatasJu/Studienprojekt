package de.haw_landshut.studienprojekt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.content.ContentValues.TAG;


enum State {
    ZERO, LOW, NORMAL, HIGH, FALSE, TRUE
}

enum Tags {
    RED(Color.RED), YELLOW(Color.YELLOW), BLACK(Color.BLACK), GREEN(Color.GREEN);

    final int color;
    String text;

    Tags(int c) {
        color = c;
    }
}


public class Evaluation extends AndroidBaseActivity {


    //This constant uses the name of the class itself as the tag.
    private static final String LOG_TAG = Evaluation.class.getSimpleName();

    private static final int delayToCheckSensorsInSec = 1;

    //view vars
    private SeekBar HRseekbar;
    private SeekBar RRseekbar;
    private TextView HRtag;
    private TextView RRtag;
    private TextView HRTextView;
    private TextView RRTextView;
    private TextView PersonTag;
    private CheckBox isMoving;
    private CheckBox isWalking;
    private CheckBox mentalState;
    private State RR = State.ZERO;
    private State HR = State.ZERO;
    private State MS = State.ZERO;
    private State W = State.ZERO;
    private Tags pTag = Tags.BLACK;

    private static final int RRHIGH = 20;
    private static final int RRNORM = 12;
    private static final int HRHIGH = 120;
    private static final int HRNORM = 51;

    MotionSensor motionSensor;
    private Handler checkSensorsHandler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        int correct = getIntent().getIntExtra("correct", 0);

        Log.d("intent URI", getIntent().toUri(0));



        //initialise the private view vars.
        HRseekbar = findViewById(R.id.HRseekBar);
        RRseekbar = findViewById(R.id.RRseekBar);

        HRTextView = findViewById(R.id.HRTextView);
        RRTextView = findViewById(R.id.RRTextView);

        HRtag = findViewById(R.id.HRtag);
        RRtag = findViewById(R.id.RRtag);

        PersonTag = findViewById(R.id.PersonalTAG);

        isMoving = findViewById(R.id.movementCheckBox);
        isWalking = findViewById(R.id.walkingCheckBox3);
        mentalState = findViewById(R.id.msCheckBox);

        if (correct >= 3) {
            MS = State.TRUE;
            mentalState.setChecked(true);
        } else {
            MS = State.FALSE;
            mentalState.setChecked(false);
        }


        mentalState.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                MS = State.TRUE;}
            else{
                MS = State.FALSE;}

            calcTag();
        });

        isWalking.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                W = State.TRUE;}
            else{
                W = State.FALSE;}

            calcTag();
        });

        //test
        TextView t = findViewById(R.id.correctTV);
        t.setText(String.valueOf(correct));
        //end test
        updateStatusValues();

        //Listeners
        HRseekbar.setOnSeekBarChangeListener(seekBarListener);
        RRseekbar.setOnSeekBarChangeListener(seekBarListener);


        isWalking.setEnabled(false);
        isMoving.setEnabled(false);
        // getting boolean for motion and walking
        motionSensor = MotionSensor.getMotionSensor();
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


    /**
     * The seek bar notification listener
     * <p>
     * see https://developer.android.com/reference/android/widget/SeekBar.html
     */
    private final SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {


        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //update current progress indicator.
            updateStatusValues();
        }


        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

    };

    /**
     * Updates the values of the multiple seekBars and textViews. To be used after changes to them.
     */
    @SuppressLint("SetTextI18n")
    private void updateStatusValues() {
        HRTextView.setText(Integer.toString(HRseekbar.getProgress()));
        RRTextView.setText(Integer.toString(RRseekbar.getProgress()));

        int currentRR = RRseekbar.getProgress();
        int currentHR = HRseekbar.getProgress();
        //HR TAG COLORS AND TEXT

        if (currentHR >= HRHIGH) {
            HR = State.HIGH;
            HRtag.setBackgroundColor(Color.RED);
            HRtag.setText(getString(R.string.eval_txt_high));

        }

        if ((currentHR >= HRNORM) && currentHR < HRHIGH) {
            HR = State.NORMAL;
            HRtag.setBackgroundColor(Color.GREEN);
            HRtag.setText(getString(R.string.eval_txt_normal));

        }

        if (currentHR < HRNORM) {
            HR = State.LOW;
            HRtag.setBackgroundColor(Color.RED);
            HRtag.setText(getString(R.string.eval_txt_low));

        }
        if(currentHR==0){
            HR = State.ZERO;
            HRtag.setBackgroundColor(Color.BLACK);
            HRtag.setText(HR.name());
        }


        //RR TAG COLORS AND TEXT

        if ((currentRR>= RRHIGH)) {
            RR = State.HIGH;
            RRtag.setBackgroundColor(Color.RED);
            RRtag.setText(getString(R.string.eval_txt_high));


        }

        if ((currentRR >= RRNORM) && currentRR< RRHIGH) {
            RR = State.NORMAL;
            RRtag.setBackgroundColor(Color.GREEN);
            RRtag.setText(getString(R.string.eval_txt_normal));

        }

        if (currentRR < RRNORM) {
            RR = State.LOW;
            RRtag.setBackgroundColor(Color.RED);
            RRtag.setText(R.string.eval_txt_low);

        }
        if(currentRR==0){
            RR = State.ZERO;
            RRtag.setBackgroundColor(Color.BLACK);
            RRtag.setText(RR.name());
        }

        calcTag();

    }



    /**
     * Calculates Tag with given values.
     * <p>
     * return
     */
    private void calcTag() {

        if (W == State.TRUE) {
            pTag = Tags.GREEN;
        } else {
            //If RR == LOW or HIGH
            if (RR == State.LOW || RR == State.HIGH) {
                if (HR == State.NORMAL) {
                    pTag = Tags.YELLOW;
                } else {
                    pTag = Tags.RED;
                }
            } else {  //IF RR == Normal
                if (HR == State.NORMAL && MS == State.TRUE) {
                    pTag = Tags.GREEN;
                } else {
                    pTag = Tags.YELLOW;
                }
            }
        }

        // if Any reading is ZERO
        if(RR==State.ZERO || HR == State.ZERO)
            pTag = Tags.BLACK;


        //set color
        PersonTag.setBackgroundColor(this.pTag.color);


    }

    /**Checks the Motion Sensors for Movement state, and sets the buttons to according colors
     */
    public void checkMovement() {
        Log.d("test", "calcMotion: ");

        if (motionSensor.isMovement()){
            isMoving.setChecked(true);
        }else {
            isMoving.setChecked(false);
        }

    }

    /**Checks the Motion Sensors for Walking state, and sets the buttons to according colors.
     *
     */
    public void checkWalking() {
        Log.d("test", "calcWalking: ");

        if (motionSensor.isWalking()){
            isWalking.setChecked(true);
        }   else {
            isWalking.setChecked(false);
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

    public void menuEvaluationButtonEventHandler(View view) {
        Intent intent = new Intent(this,gefuehle.class);
        startActivity(intent);
    }

    //=========Getters/Setters=========

    private boolean getIsWalking() {
        return isWalking.isChecked();
    }

    public boolean getIsMoving() {
        return isMoving.isChecked();
    }

    public int getHRseekbarProgress() {
        return HRseekbar.getProgress();
    }

    public int getRRseekbarProgress() {
        return RRseekbar.getProgress();
    }


    //=========END Getters/Setters=========

}
