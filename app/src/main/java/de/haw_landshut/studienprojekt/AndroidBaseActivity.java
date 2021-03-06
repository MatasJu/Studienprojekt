package de.haw_landshut.studienprojekt;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;


/**A base class for Android depended subclasses. Currently it implements a custom LocaleHelper to deal with changing
 * the language for the app.
 *
 * Extend from this class to support multiple Languages for the activity.
 *
 */

public abstract class AndroidBaseActivity extends Activity {

    private String initialLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Set screen for all activities to prevent from rotating and causing more bugs atm.
        initialLocale = LocaleHelper.getPersistedLocale(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (initialLocale != null && !initialLocale.equals(LocaleHelper.getPersistedLocale(this))) {
            recreate();
        }
    }
}
