package de.haw_landshut.studienprojekt.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;


/**
 * Manages setting of the app's locale.
 * based on: https://stackoverflow.com/a/47223492
 */
public class LocaleHelper {

    public static Context onAttach(Context context) {
        String locale = getPersistedLocale(context);
        return setLocale(context, locale);
    }

    public static String getPersistedLocale(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Profile_Settings.class.getName(),Context.MODE_PRIVATE);
        return getStringForLocale(preferences.getString(SPkeys.LANGUAGE.toString(), "German"));
    }

    /**Changes String retrieved from Profile Settings Shared Preferences to a String understood by Locale.
     *
     * @param string String, preferably from Profile Settings
     * @return shortened Locale String
     */
    private static String getStringForLocale(String string){
        switch (string){
            case "English":
                return "en";

            case "Englisch":
                return "en";

            case "German":
                return "de";

            case "Deutsch":
                return "de";


        }
        return "de";
    }
    /**
     * Set the app's locale to the one specified by the given String.
     *
     * @param context
     * @param localeSpec a locale specification as used for Android resources (NOTE: does not
     *                   support country and variant codes so far); the special string "system" sets
     *                   the locale to the locale specified in system settings
     * @return
     */
    public static Context setLocale(Context context, String localeSpec) {
        Locale locale;
        if (localeSpec.equals("system")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = Resources.getSystem().getConfiguration().getLocales().get(0);
            } else {
                //noinspection deprecation
                locale = Resources.getSystem().getConfiguration().locale;
            }
        } else {
            locale = new Locale(localeSpec);
        }
        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, locale);
        } else {
            return updateResourcesLegacy(context, locale);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}