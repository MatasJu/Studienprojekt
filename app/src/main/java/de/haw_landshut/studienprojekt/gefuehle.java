package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import de.haw_landshut.studienprojekt.settings.AndroidBaseActivity;
import de.haw_landshut.studienprojekt.settings.Profile_Settings;

public class gefuehle extends AndroidBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gefuehle);

    }

    public void onClickListener(View v){
        Intent intent;
        switch (v.getId()) {

            case R.id.greenBtn:
                intent = new Intent(getApplicationContext(), gruen.class);
                startActivity(intent);
                break;
            case R.id.yellowBtn:
                intent = new Intent(getApplicationContext(), verletzung.class);
                startActivity(intent);
                break;
            case R.id.redBtn:
                intent = new Intent(getApplicationContext(), kontakte_rot.class);
                startActivity(intent);
                break;
            case R.id.questionnaireBTN:
                intent = new Intent(getApplicationContext(),QuestionsActivity.class);
                startActivity(intent);
                break;
            case R.id.evaliuationBTN:
                intent = new Intent(getApplicationContext(),Evaluation.class);
                startActivity(intent);
        }
    }

    public void menüButtonEventHandler(View view) {
        Intent intent = new Intent(this,Profile_Settings.class);
        startActivity(intent);
        setContentView(R.layout.activity_profile_settings);
    }

}
