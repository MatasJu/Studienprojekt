package de.haw_landshut.studienprojekt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SimulationSettings extends AppCompatActivity {

    //This constant uses the name of the class itself as the tag.
    private static final String LOG_TAG = SimulationSettings.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_settings);
    }
}
