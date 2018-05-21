package de.haw_landshut.studienprojekt;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Saves data to sharedPreferences
 *
 * TODO: Need More checks on values like email and such.
 *
 */
public class Profile_Settings extends AppCompatActivity implements Profile_Settings_Fragment {
    //TextViews
    TextView firstNameTV;
    TextView lastNameTV;
    TextView birthdayTV;
    TextView heightTV;
    TextView weightTV;
    TextView emailTV;

    //gender
    Spinner genderSpinner;
    String genderString;
    ArrayAdapter<CharSequence> genderAdapter;
    //Birthday vars
    int bDay;
    int bYear;
    int bMonth;
    //Buttons
    Button cancelBtn;
    Button saveBtn;
    //Other
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        //init TxtViews
        this.firstNameTV = findViewById(R.id.firstName);
        this.lastNameTV = findViewById(R.id.lastName);

        this.birthdayTV = findViewById(R.id.birthday);
        this.heightTV = findViewById(R.id.height);
        this.weightTV = findViewById(R.id.weight);
        this.emailTV = findViewById(R.id.email);

        //settings so we can use DatePicker instead of keyboard input.
        birthdayTV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showDatePickerDialog(v);
            }
        });

        birthdayTV.setKeyListener(null);

        //populate the gender "Spinner" drop down menu.
        this.genderSpinner = findViewById(R.id.gender_Spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //?
            }
        });


        //init Buttons.
        this.cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this::buttonClickListener);
        this.saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this::buttonClickListener);

        //other
        context = this;
        sharedPrefs = getPreferences(MODE_PRIVATE);


    }

    @Override
    protected void onStart() {
        super.onStart();
        //Load the Fields from SharedPreferences
        firstNameTV.append(sharedPrefs.getString(SPkeys.FIRST_NAME.toString(), ""));
        lastNameTV.append(sharedPrefs.getString(SPkeys.LAST_NAME.toString(), ""));
        heightTV.append(sharedPrefs.getString(SPkeys.HEIGHT.toString(), ""));
        weightTV.append(sharedPrefs.getString(SPkeys.WEIGHT.toString(), ""));
        emailTV.append(sharedPrefs.getString(SPkeys.EMAIL.toString(), ""));
        //gender
        genderSpinner.setSelection(genderAdapter.getPosition(
                sharedPrefs.getString(SPkeys.GENDER.toString(),"Abimegender")));
        //birthday
        setBirthdayTV(
                sharedPrefs.getInt(SPkeys.BIRTHDAY_DAY.toString(), 1),
                sharedPrefs.getInt(SPkeys.BIRTHDAY_MONTH.toString(), 1),
                sharedPrefs.getInt(SPkeys.BIRTHDAY_YEAR.toString(), 1950));

    }

    /**
     * Listens to the button clicks.
     *
     * @param view is passed on by the event.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void buttonClickListener(View view) {

        //Cancel Button
        if (view.getId() == R.id.cancelBtn) {
            //cancel
            finish(); //finishes activity and goes back.

        }
        //Save Button
        if (view.getId() == R.id.saveBtn) {
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
            editor.apply();

            if (BuildConfig.DEBUG) {
                StringBuilder temp = new StringBuilder();
                temp.append("Saved Text:\n");
                sharedPrefs.getAll().forEach((key, value) ->
                        temp.append(key).append(" = ").append(value).append('\n'));
                Toast toast = Toast.makeText(context, temp, Toast.LENGTH_LONG);
                toast.show();

            }


        }

    }



    public void setBirthdayTV(int day, int month, int year) {
        bDay = day;
        bMonth = month;
        bYear = year;
        birthdayTV.append(String.format(Locale.getDefault(), "%d.%d.%d", day, month, year));
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        FragmentManager fragmentManager = getFragmentManager();
        newFragment.show(fragmentManager, "datePicker");
    }


    /**
     * Fragment for Date Picker
     *
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
 * Enum for SharedPrefferences strings for this activity.
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
    EMAIL("email");

    private final String string;

    SPkeys(String name) {
        this.string = name;
    }

    @Override
    public String toString() {
        return this.string;
    }
}

