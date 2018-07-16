package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class gruen extends AndroidBaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gruen);
    }

    public void onClickListener(View v){
        Intent intent = new Intent(getApplicationContext(),gefuehle.class);
        startActivity(intent);
    }

    public void menügrünButtonEventHandler(View view) {
        Intent intent = new Intent(this,gefuehle.class);
        startActivity(intent);
    }
}
