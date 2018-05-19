package de.haw_landshut.studienprojekt;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

enum State {
    ZERO, LOW, NORMAL, HIGH, FALSE, TRUE
}

enum Tags {
    RED(Color.RED), YELLOW(Color.YELLOW), BLACK(Color.BLACK), GREEN(Color.GREEN);

    final int color;

    Tags(int c) {
        color = c;
    }
}


public class Evaluation extends AppCompatActivity {


    static public List<State> States = new ArrayList<>();


    //This constant uses the name of the class itself as the tag.
    private static final String LOG_TAG = Evaluation.class.getSimpleName();

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

    private static final int HRHIGH = 20;
    private static final int HRNORM = 12;
    private static final int RRHIGH = 120;
    private static final int RRNORM = 51;


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
            HRtag.setText(HR.name());

        }

        if ((currentHR >= HRNORM) && currentHR < HRHIGH) {
            HR = State.NORMAL;
            HRtag.setBackgroundColor(Color.GREEN);
            HRtag.setText(HR.name());

        }

        if (currentHR < HRNORM) {
            HR = State.LOW;
            HRtag.setBackgroundColor(Color.RED);
            HRtag.setText(HR.name());

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
            RRtag.setText(RR.name());


        }

        if ((currentRR >= RRNORM) && currentRR< RRHIGH) {
            RR = State.NORMAL;
            RRtag.setBackgroundColor(Color.GREEN);
            RRtag.setText(RR.name());

        }

        if (currentRR < RRNORM) {
            RR = State.LOW;
            RRtag.setBackgroundColor(Color.RED);
            RRtag.setText(RR.name());

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
