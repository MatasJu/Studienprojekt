package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class SimulationSettings extends AppCompatActivity {

    //This constant uses the name of the class itself as the tag.
    private static final String LOG_TAG = SimulationSettings.class.getSimpleName();

    //
    private SeekBar HRseekbar;
    private SeekBar RRseekbar;
    private TextView HRtag;
    private TextView RRtag;
    private TextView HRTextView;
    private TextView RRTextView;
    private Button continueBtn;

    private final int RRHIGH =120;
    private final int RRNORM =51;

    private final int HRHIGH = 20;
    private final int HRNORM = 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_settings);

        //init
        HRseekbar = (SeekBar) findViewById(R.id.HRseekBar);
        RRseekbar = (SeekBar) findViewById(R.id.RRseekBar);

        HRTextView = (TextView) findViewById(R.id.HRTextView);
        RRTextView = (TextView) findViewById(R.id.RRTextView);

        HRtag = (TextView) findViewById(R.id.HRtag);
        RRtag = (TextView) findViewById(R.id.RRtag);

        continueBtn = (Button) findViewById(R.id.continueBtn);

        updateStatusValues();

        //Listeners
        HRseekbar.setOnSeekBarChangeListener(seekBarListener);
        RRseekbar.setOnSeekBarChangeListener(seekBarListener);

    }

    // The seek bar notification listener https://developer.android.com/reference/android/widget/SeekBar.html
    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            updateStatusValues();
//            switch(seekBar.getId())
//            {
//                case R.id.HRseekBar:
//                    HRTextView.setText(Integer.toString(i));
//                    return;
//                case R.id.RRseekBar:
//                    RRTextView.setText(Integer.toString(i));
//                    return;
//
//            }

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //update current progress indicator.

        }


        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

    };

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


    public void onClickBtn(View view) {
        if(view.getId()== continueBtn.getId() )
            {
                Log.d(LOG_TAG, "continueBtn Clicked!");
                Intent intent = new Intent(this, QuestionsActivity.class);
                startActivity(intent);
            }


    }
}
