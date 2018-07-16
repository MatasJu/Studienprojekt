package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class notruf extends AndroidBaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notruf);
    }

    public void menüNotrufButtonEventHandler(View view) {
        Intent intent = new Intent(this,gefuehle.class);
        startActivity(intent);
    }
}
