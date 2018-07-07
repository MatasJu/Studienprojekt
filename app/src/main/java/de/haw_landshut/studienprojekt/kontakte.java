package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.haw_landshut.studienprojekt.settings.AndroidBaseActivity;
import de.haw_landshut.studienprojekt.settings.Profile_Settings;

public class kontakte extends AndroidBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kontakte);

    }

    public void onClickListener(View v){

        Intent intent;
        switch (v.getId()) {

            case R.id.jaBtn:
                intent = new Intent(getApplicationContext(),kontakte_rot.class);
                startActivity(intent);
                break;
            case R.id.neinBtn:
                intent = new Intent(getApplicationContext(), gruen.class);
                startActivity(intent);
                break;

        }
    }

    public void menükontakteButtonEventHandler(View view) {
        Intent intent = new Intent(this,gefuehle.class);
        startActivity(intent);
    }
}
