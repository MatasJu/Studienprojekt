package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.haw_landshut.studienprojekt.settings.AndroidBaseActivity;

public class kontakte_rot extends AndroidBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kontakte_rot);

    }

    public void onClickListener(View v){
        Intent intent = new Intent(getApplicationContext(),notruf.class);
        startActivity(intent);

    }

    public void menüKontakte_rot_ButtonEventHandler(View view) {
        Intent intent = new Intent(this,gefuehle.class);
        startActivity(intent);
    }

}
