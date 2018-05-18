package de.haw_landshut.studienprojekt;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

enum State{
    ZERO,LOW,NORMAL,HIGH,FALSE,TRUE
}

enum Tags{
    RED(Color.RED),YELLOW(Color.YELLOW),BLACK(Color.BLACK),GREEN(Color.GREEN);

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
    private Button continueBtn;
    private CheckBox isMoving;
    private CheckBox isWalking;
    private State RR = State.ZERO;
    private State HR = State.ZERO;
    private State MS = State.ZERO;
    private State W = State.ZERO;
    private Tags pTag = Tags.BLACK;

    //test
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        int correct = getIntent().getIntExtra("correct", 0);
        if(correct >=3){
            MS= State.TRUE;
        }else {
            MS =State.FALSE;
        }

        Log.d("intent URI", getIntent().toUri(0));

        //initialise the private view vars.
        HRseekbar = findViewById(R.id.HRseekBar);
        RRseekbar = findViewById(R.id.RRseekBar);

        HRTextView = findViewById(R.id.HRTextView);
        RRTextView = findViewById(R.id.RRTextView);

        HRtag = findViewById(R.id.HRtag);
        RRtag = findViewById(R.id.RRtag);

        PersonTag = findViewById(R.id.PersonalTAG);

        continueBtn = findViewById(R.id.continueBtn);

        isMoving = findViewById(R.id.movementCheckBox);
        isWalking = findViewById(R.id.walkingCheckBox3);
        t = findViewById(R.id.correctTV);
        t.setText(String.valueOf(correct));

        updateStatusValues();

        //Listeners
        HRseekbar.setOnSeekBarChangeListener(seekBarListener);
        RRseekbar.setOnSeekBarChangeListener(seekBarListener);

    }

    /**The seek bar notification listener
     *
     * see https://developer.android.com/reference/android/widget/SeekBar.html
     *
     */
    private final SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            updateStatusValues();

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //update current progress indicator.

        }


        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

    };

    /**Updates the values of the multiple seekBars and textViews. To be used after changes to them.
     *
     */
    private void updateStatusValues(){
        HRTextView.setText(Integer.toString(HRseekbar.getProgress()));
        RRTextView.setText(Integer.toString(RRseekbar.getProgress()));

        //HR TAG COLORS AND TEXT
        int HRHIGH = 20;
        if((HRseekbar.getProgress()>= HRHIGH)){
            HRtag.setBackgroundColor(Color.RED);
            HRtag.setText("HIGH");
            HR = State.HIGH;
        }
        int HRNORM = 12;
        if((HRseekbar.getProgress()>= HRNORM) && HRseekbar.getProgress()< HRHIGH){
            HRtag.setBackgroundColor(Color.GREEN);
            HRtag.setText("NORMAL");
            HR = State.NORMAL;
        }

        if(HRseekbar.getProgress()< HRNORM){
            HRtag.setBackgroundColor(Color.RED);
            HRtag.setText("LOW");
            HR = State.LOW;
        }


        //RR TAG COLORS AND TEXT
        int RRHIGH = 120;
        if((RRseekbar.getProgress()>= RRHIGH)){
            RRtag.setBackgroundColor(Color.RED);
            RRtag.setText("HIGH");
            RR = State.HIGH;

        }
        int RRNORM = 51;
        if((RRseekbar.getProgress()>= RRNORM) && RRseekbar.getProgress()< RRHIGH){
            RRtag.setBackgroundColor(Color.GREEN);
            RRtag.setText("NORMAL");
            HR = State.NORMAL;
        }

        if(RRseekbar.getProgress()< RRNORM){
            RRtag.setBackgroundColor(Color.RED);
            RRtag.setText("LOW");
            RR= State.LOW;
        }
        if(getIsWalking()){
            W = State.TRUE;
        }else {
            W = State.FALSE;
        }

        calcTag();

        PersonTag.setBackgroundColor(pTag.color);

    }



    /**Button click handler
     *
     * @param view TODO
     */
    public void onClickBtn(View view) {
        if(view.getId()== continueBtn.getId() )
        {
            Log.d(LOG_TAG, "continueBtn Clicked!");
//            Intent intent = new Intent(this, QuestionsActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putInt("HRate",getHRseekbarProgress());
//            bundle.putInt("RRate",getRRseekbarProgress());
//            bundle.putBoolean("isWalking",getIsWalking());
//            bundle.putBoolean("isMoving",getIsMoving());
//            intent.putExtras(bundle);
//            startActivity(intent);
        }
    }

    /**Calculates Tag with given values.
     *
     * return
     */
    private void calcTag() {
        Tags tag = Tags.BLACK;
        if(W==State.TRUE){
            tag = Tags.GREEN;
        }else {
            //If RR == LOW or HIGH
            if (RR==State.LOW||RR==State.HIGH){
                if (HR==State.NORMAL){
                    tag = Tags.YELLOW;
                }else {
                    tag = Tags.RED;
                }
            }
            //IF RR == Normal
            if (RR==State.NORMAL){
                if (MS==State.TRUE){
                    tag = Tags.GREEN;
                }else {
                    tag = Tags.YELLOW;
                }
            }

        }
        pTag=tag;


    }



    //=========Getters/Setters=========
    private boolean getIsWalking(){
        return isWalking.isChecked();
    }

    public boolean getIsMoving(){
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
