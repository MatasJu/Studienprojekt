package de.haw_landshut.studienprojekt;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**Saves data to sharedPreferences
 *
 *
 *
 *
 *
 *
 *
 */
public class Profile_Settings extends AppCompatActivity {
    //TextViews
    TextView firstNameTV;
    TextView lastNameTV;
    TextView genderTV;
    TextView birthdayTV;
    TextView heightTV;
    TextView weightTV;
    TextView emailTV;
    //Buttons
    Button cancelBtn;
    Button saveBtn;
    //Other
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    SharedPreferencesEnum SPenum;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        //init TxtViews
        this.firstNameTV = findViewById(R.id.firstName);
        this.lastNameTV = findViewById(R.id.lastName);
        this.genderTV = findViewById(R.id.gender);
        this.birthdayTV = findViewById(R.id.birthday);
        this.heightTV = findViewById(R.id.height);
        this.weightTV = findViewById(R.id.weight);
        this.emailTV = findViewById(R.id.email);
        //init Buttons.
        this.cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this::buttonClickListener);
        this.saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this::buttonClickListener);

        //other
        context = this;
        sharedPrefs = getPreferences(MODE_PRIVATE);


    }

    /**Listens to the button clicks.
     *
     * @param view is passed on by the event.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void buttonClickListener(View view) {
        if (view.getId() == R.id.cancelBtn) {
            //cancel
            finish();

        }
        if (view.getId() == R.id.saveBtn) {
            //save
            editor = sharedPrefs.edit();
            editor.putString(SharedPreferencesEnum.FIRST_NAME.toString(),firstNameTV.getText().toString());
            editor.putString(SharedPreferencesEnum.LAST_NAME.toString(), lastNameTV.getText().toString());
            editor.putString(SharedPreferencesEnum.GENDER.toString(), genderTV.getText().toString());
            editor.putString(SharedPreferencesEnum.BIRTHDAY.toString(), birthdayTV.getText().toString());
            editor.putString(SharedPreferencesEnum.HEIGHT.toString(), heightTV.getText().toString());
            editor.putString(SharedPreferencesEnum.WEIGHT.toString(), weightTV.getText().toString());
            editor.putString(SharedPreferencesEnum.EMAIL.toString(), emailTV.getText().toString());
            editor.apply();

            if(BuildConfig.DEBUG){
                StringBuilder temp = new StringBuilder();
                temp.append("Saved Text:\n");
                sharedPrefs.getAll().forEach((key, value) ->
                        temp.append(key).append(" = ").append((String) value).append('\n'));
                Toast toast = Toast.makeText(context,temp,Toast.LENGTH_LONG);
                toast.show();

            }


        }

    }


}