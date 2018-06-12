package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.haw_landshut.studienprojekt.settings.AndroidBaseActivity;

public class erklaerungrot extends AndroidBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.erklaerungrot);

    }

    public void onClickListener(View v){
        Intent intent = new Intent(getApplicationContext(),gefuehle.class);
        startActivity(intent);

    }
}
