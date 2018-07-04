package de.haw_landshut.studienprojekt.settings;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import de.haw_landshut.studienprojekt.BuildConfig;
import de.haw_landshut.studienprojekt.R;
import de.haw_landshut.studienprojekt.gefuehle;


/**
 * Saves data to sharedPreferences
 * <p>
 * TODO: Need More checks on values like email and such.
 */
public class Profile_Settings extends AndroidBaseActivity implements Profile_Settings_Fragment {
    //TextViews
    TextView firstNameTV;
    TextView lastNameTV;
    TextView birthdayTV;
    TextView heightTV;
    TextView weightTV;
    TextView emailTV;

    //buttons

    Button weiterBtn;


    //gender
    Spinner genderSpinner;
    String genderString;

    ArrayAdapter<CharSequence> genderAdapter;

    Spinner languageSpinner;
    String languageString;
    ArrayAdapter<CharSequence> languageAdapter;

    //Birthday vars
    int bDay;
    int bYear;
    int bMonth;

    //Other
    static SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);


        //====Get saved Shared Preferences ====
        sharedPrefs = getSharedPreferences(Profile_Settings.class.getName(), MODE_PRIVATE);

    }


    @Override
    protected void onStart() {
        super.onStart();

        //---------init Views---------

        //weiterBtn
        weiterBtn = findViewById(R.id.weiterBtn);
        weiterBtn.setOnClickListener(v -> {
            saveData();
            Intent intent = new Intent(getApplicationContext(), gefuehle.class);
            startActivity(intent);

        });

        //-----First Name-----
        firstNameTV = findViewById(R.id.firstName);
        //Load the Fields from SharedPreferences
        firstNameTV.setText(sharedPrefs.getString(SPkeys.FIRST_NAME.toString(), ""));
        //Set onFocusChange Listener to save data after leaving a field.



        //-----Last Name-----
        lastNameTV = findViewById(R.id.lastName);
        lastNameTV.setText(sharedPrefs.getString(SPkeys.LAST_NAME.toString(), ""));


        //-----Height-----
        heightTV = findViewById(R.id.height);
        heightTV.setText(sharedPrefs.getString(SPkeys.HEIGHT.toString(), ""));


        //-----Weight-----
        weightTV = findViewById(R.id.weightEV);
        weightTV.setText(sharedPrefs.getString(SPkeys.WEIGHT.toString(), ""));


        //-----Email-----
        emailTV = findViewById(R.id.email);
        emailTV.setText(sharedPrefs.getString(SPkeys.EMAIL.toString(), ""));




        //------Birthday-----
        birthdayTV = findViewById(R.id.birthday);

        //settings so we can use DatePicker instead of keyboard input.
        birthdayTV.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                showDatePickerDialog(v);
        });

        //this so the on-screen keyboard does not appear
        birthdayTV.setKeyListener(null);

        //Load Values, or set Default ones..
        setBirthdayTV(
                sharedPrefs.getInt(SPkeys.BIRTHDAY_DAY.toString(), 1),
                sharedPrefs.getInt(SPkeys.BIRTHDAY_MONTH.toString(), 1),
                sharedPrefs.getInt(SPkeys.BIRTHDAY_YEAR.toString(), 1950));


        //-----Gender-----


        //populate the gender "Spinner" drop down menu.
        genderSpinner = findViewById(R.id.gender_Spinner);

        //Create an ArrayAdapter using the string array and a default spinner layout
        genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);

        //Specify the layout to use when the list of choices appears
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Apply the adapter to the spinner
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderString = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Load saved value.
        genderSpinner.setSelection(
                genderAdapter.getPosition(sharedPrefs.getString(
                        SPkeys.GENDER.toString(), Objects.requireNonNull(genderAdapter.getItem(0)).toString())));



        //-----Language-----

        languageSpinner = findViewById(R.id.languageSpinner);
        languageAdapter = ArrayAdapter.createFromResource(this, R.array.language_array, android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                languageString = parent.getItemAtPosition(position).toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        languageString = languageAdapter.getItem(0).toString();

        //TODO:Because of how this is implemented, the spinner currently does not support translations.
        languageSpinner.setSelection(languageAdapter.getPosition(
                sharedPrefs.getString(
                        SPkeys.LANGUAGE.toString(), languageAdapter.getItem(0).toString())));



    }


    /**
     * Saves data to SharedPreferences.
     *
     */
    private void saveData(){

        //save
        editor = sharedPrefs.edit();
        editor.putString(SPkeys.FIRST_NAME.toString(), firstNameTV.getText().toString());
        editor.putString(SPkeys.LAST_NAME.toString(), lastNameTV.getText().toString());
        editor.putString(SPkeys.GENDER.toString(), genderString);
        editor.putInt(SPkeys.BIRTHDAY_DAY.toString(), bDay);
        editor.putInt(SPkeys.BIRTHDAY_MONTH.toString(), bMonth);
        editor.putInt(SPkeys.BIRTHDAY_YEAR.toString(), bYear);
        editor.putString(SPkeys.HEIGHT.toString(), heightTV.getText().toString());
        editor.putString(SPkeys.WEIGHT.toString(), weightTV.getText().toString());
        editor.putString(SPkeys.EMAIL.toString(), emailTV.getText().toString());
        editor.putString(SPkeys.LANGUAGE.toString(), languageString);
        editor.apply();

        if (BuildConfig.DEBUG) {
            StringBuilder temp = new StringBuilder();
            temp.append("Saved Text:\n");
            sharedPrefs.getAll().forEach((key, value) ->
                    temp.append(key).append(" = ").append(value).append('\n'));
            Toast toast = Toast.makeText(this, temp, Toast.LENGTH_LONG);
            toast.show();
        }


    }

    /**Save on back press.
     *
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveData();
    }

    /** Save on toolbar back button.
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveData();
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /** Helping function to set text to Birthday TextView.
     * Used in Date Picker Fragment.
     *
     * @param day day
     * @param month month
     * @param year year
     */
    public void setBirthdayTV(int day, int month, int year) {
        bDay = day;
        bMonth = month;
        bYear = year;
        birthdayTV.setText(String.format(Locale.getDefault(), "%d.%d.%d", day, month, year));
    }

    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        FragmentManager fragmentManager = getFragmentManager();
        newFragment.show(fragmentManager, "datePicker");
    }


    /** Fragment for Date Picker
     * @see "https://developer.android.com/guide/topics/ui/controls/pickers"
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            Profile_Settings_Fragment profile_settings_fragment = (Profile_Settings_Fragment) getActivity();
            profile_settings_fragment.setBirthdayTV(day, month, year);
        }
    }
}


/**
 * Enum for SharedPreferences strings for this activity.
 */
enum SPkeys {
    USER_INFO_FILE("user_info"),
    FIRST_NAME("first_name"),
    LAST_NAME("last_name"),
    GENDER("gender"),
    BIRTHDAY_DAY("birthday day"),
    BIRTHDAY_MONTH("birthday month"),
    BIRTHDAY_YEAR("birthday year"),
    HEIGHT("height"),
    WEIGHT("weight"),
    EMAIL("email"),
    LANGUAGE("language");

    private final String string;

    SPkeys(String name) {
        this.string = name;
    }

    @Override
    public String toString() {
        return this.string;
    }

}

