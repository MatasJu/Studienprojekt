package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.haw_landshut.studienprojekt.settings.AndroidBaseActivity;

public class verletzung extends AndroidBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verletzung);

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
}

