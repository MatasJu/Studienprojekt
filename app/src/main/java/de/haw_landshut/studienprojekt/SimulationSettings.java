package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

public class SimulationSettings extends AppCompatActivity {

    //This constant uses the name of the class itself as the tag.
    private static final String LOG_TAG = SimulationSettings.class.getSimpleName();

    //view vars
    private SeekBar HRseekbar;
    private SeekBar RRseekbar;
    private TextView HRtag;
    private TextView RRtag;
    private TextView HRTextView;
    private TextView RRTextView;
    private Button continueBtn;
    private CheckBox isMoving;
    private CheckBox isWalking;

    //Constants for what is normal an high for Respiratory and Heart rates. Low is considered below Normal
    private final int RRHIGH =120;
    private final int RRNORM =51;

    private final int HRHIGH = 20;
    private final int HRNORM = 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_settings);

        //initialise the private view vars.
        HRseekbar = (SeekBar) findViewById(R.id.HRseekBar);
        RRseekbar = (SeekBar) findViewById(R.id.RRseekBar);

        HRTextView = (TextView) findViewById(R.id.HRTextView);
        RRTextView = (TextView) findViewById(R.id.RRTextView);

        HRtag = (TextView) findViewById(R.id.HRtag);
        RRtag = (TextView) findViewById(R.id.RRtag);

        continueBtn = (Button) findViewById(R.id.continueBtn);

        isMoving = findViewById(R.id.movementCheckBox);
        isWalking = findViewById(R.id.walkingCheckBox3);

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
    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
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

    /**Updates the values of the multiple seekbars and textviews. To be used after changes to them.
     *
     */
    private void updateStatusValues(){
        HRTextView.setText(Integer.toString(HRseekbar.getProgress()));
        RRTextView.setText(Integer.toString(RRseekbar.getProgress()));

        //HR TAG COLORS AND TEXT
        if((HRseekbar.getProgress()>=HRHIGH)){
            HRtag.setBackgroundColor(Color.RED);
            HRtag.setText("HIGH");
        }
        if((HRseekbar.getProgress()>=HRNORM) && HRseekbar.getProgress()<HRHIGH){
            HRtag.setBackgroundColor(Color.GREEN);
            HRtag.setText("NORMAL");
        }

        if(HRseekbar.getProgress()<HRNORM){
            HRtag.setBackgroundColor(Color.RED);
            HRtag.setText("LOW");
        }


        //RR TAG COLORS AND TEXT
        if((RRseekbar.getProgress()>=RRHIGH)){
            RRtag.setBackgroundColor(Color.RED);
            RRtag.setText("HIGH");
        }
        if((RRseekbar.getProgress()>=RRNORM) && RRseekbar.getProgress()<RRHIGH){
            RRtag.setBackgroundColor(Color.GREEN);
            RRtag.setText("NORMAL");
        }

        if(RRseekbar.getProgress()<RRNORM){
            RRtag.setBackgroundColor(Color.RED);
            RRtag.setText("LOW");
        }

    }

    /**Button click handler
     *
     * @param view
     */
    public void onClickBtn(View view) {
        if(view.getId()== continueBtn.getId() )
        {
            Log.d(LOG_TAG, "continueBtn Clicked!");
            Intent intent = new Intent(this, QuestionsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("HRRate",getHRseekbarProgress());
            bundle.putInt("RRRate",getRRseekbarProgress());
            bundle.putBoolean("isWalking",true);
            bundle.putBoolean("isMoving",true);


            startActivity(intent);
        }
    }



    //=========Getters/Setters=========
    public boolean getIsWalking(){
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
