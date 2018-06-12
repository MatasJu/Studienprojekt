package de.haw_landshut.studienprojekt.settings;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import static android.view.View.AUTOFILL_TYPE_NONE;


/**A base class for Android depended subclasses. Currently it implements a custom LocaleHelper to deal with changing
 * the language for the app.
 *
 * Extend from this class to support multiple Languages for the activity.
 *
 */
public abstract class AndroidBaseActivity extends AppCompatActivity {

    private String initialLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
