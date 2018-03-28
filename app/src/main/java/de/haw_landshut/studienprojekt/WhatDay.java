package de.haw_landshut.studienprojekt;


import android.content.Intent;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.TextView;


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class WhatDay extends AppCompatActivity {

    private static final String TAG = WhatDay.class.getSimpleName();
    TextToSpeech t1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_day);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status == TextToSpeech.SUCCESS) {

                    Log.d(TAG, "onInit: we got here");
                    t1.setLanguage(Locale.GERMANY);
                    t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        @Override
                        public void onDone(String utteranceId) {
                            Log.d(TAG, "TTS start");
                            displaySpeechRecognizer();
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.d(TAG, "TTS erroor");
                        }

                        @Override
                        public void onStart(String utteranceId) {
                            Log.d(TAG, "TTS finished");
                        }
                    });
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

                    t1.speak(getString(R.string.what_day_question), TextToSpeech.QUEUE_ADD, map);

                }
            }
        });







    }



    private static final int SPEECH_WHAT_DAY_CODE = 7;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.GERMAN.toString());
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,false);
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.what_day_question));
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,2);

       // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_WHAT_DAY_CODE);
    }


    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SPEECH_WHAT_DAY_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String spokenText = results.get(0);
            Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "], results.toString()"+results.toString());

            // Do something with spokenText
            TextView tv = (TextView)findViewById(R.id.speakerTextView);
            tv.setText(spokenText);
            checkDayAnswer(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void checkDayAnswer(String result){


        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        boolean tag = false;

        switch (day) {
            case Calendar.SUNDAY:

                if(result.toLowerCase().contains("sontag"))
                    tag = true;

                break;

            case Calendar.MONDAY:

                if(result.toLowerCase().contains("montag"))
                    tag = true;

                break;

            case Calendar.TUESDAY:

                if(result.toLowerCase().contains("dienstag"))
                    tag = true;

                break;

            case Calendar.WEDNESDAY:

                if(result.toLowerCase().contains("mittwoch"))
                    tag = true;

                break;
            case Calendar.THURSDAY:

                if(result.toLowerCase().contains("donnerstag"))
                    tag = true;

                break;

            case Calendar.FRIDAY:

                if(result.toLowerCase().contains("freitag"))
                    tag = true;

                break;

            case Calendar.SATURDAY:

                if(result.toLowerCase().contains("samstag"))
                    tag = true;

                break;
        }

        TextView tv = (TextView)findViewById(R.id.textView2);
        //tag the person.
        tv.setText(tag?R.string.tag_color_delayed:R.string.tag_color_immediate);

        this.getWindow().getDecorView().setBackgroundColor(tag?Color.YELLOW:Color.RED);

    }
}
