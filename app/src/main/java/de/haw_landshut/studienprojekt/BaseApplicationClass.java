package de.haw_landshut.studienprojekt;

import android.app.Application;
import android.content.Context;


import org.acra.ACRA;
import org.acra.annotation.AcraMailSender;


@AcraMailSender(mailTo = "s-mjurev@haw-landshut.de")
public class BaseApplicationClass extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
