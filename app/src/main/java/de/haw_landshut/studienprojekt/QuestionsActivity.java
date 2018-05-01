package de.haw_landshut.studienprojekt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import static de.haw_landshut.studienprojekt.BuildConfig.DEBUG;

/** An Activity for asking Questions and taking answers with TTS and STT.
 * It uses google intents to translate questions to speech and read them,
 * than takes an answer through speech and looks for answer keywords in the returned String.
 */
public class QuestionsActivity extends AppCompatActivity {
    //This constant uses the name of the class itself as the tag.
    private static final String TAG = QuestionsActivity.class.getSimpleName();

    //
    private TextToSpeech textToSpeech;
    //For Testing
    private TextView testSpeechRecResult;
    ArrayList<String> qList;
    private int qNr = 0;

    //------
    /** Code for recognising callback from speech recogniser.
     */
    private static final int SPEECH_WHAT_DAY_CODE = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        qList = makeQuestionList();
        testSpeechRecResult = findViewById(R.id.answer);




        
        //Problems if user disabled all the TTS engines.


        askQuestion();

    }

    private void askQuestion(){

        if(qNr==0)
            askToRemember();
        else
            textToSpeech = TTS(qList.get(qNr));

        }


    private void askToRemember() {
         textToSpeech = TTS("Bitte erinnern Sie sich an diese drei Wörtern :" +
                 " Sonne." +
                 " Waser." +
                 " Traum.");
    }

   private ArrayList<String> makeQuestionList(){
        ArrayList<String> r=  new ArrayList<>();
        r.add(getString(R.string.what_day_question));
        r.add("Welcher Monat ist es?");
        r.add("Welches Jahr haben wir denn heute?");

        return r;

    }

    /** Creates new TTS intent and reads the given text.
     *
     * @return
     */

    private TextToSpeech TTS(final String text){
       return new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {

                    Log.d(TAG, "onInit: TTS start success");
                    textToSpeech.setLanguage(Locale.GERMANY);
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        @Override
                        public void onDone(String utteranceId) {
                            Log.d(TAG, "TTS finished");
                            displaySpeechRecognizer();
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.d(TAG, "TTS error");
                        }

                        @Override
                        public void onStart(String utteranceId) {
                            Log.d(TAG, "TTS start");
                        }
                    });
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
                    map.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, "1");

                    textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, map);

                }
            }
        });


    }


    /**Creates and displays an intent to write speech to text.
     * TODO: Localisation, go over settings and choose the best ones, maybe allow the caller to choose them. Modulise this function
     */
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.GERMAN.toString());
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
        //Offline recognistion will work oply obove api lvl 23, also has to be downloaded outside the app in the language settings.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        }
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.what_day_question));
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 2);

        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_WHAT_DAY_CODE);
    }

    /** Callback function for STT.
     *
     * @param requestCode is the code of the STT intent.
     * @param resultCode STT passes the code of success/failure.
     * @param data data from the STT.
     */
    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == SPEECH_WHAT_DAY_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String spokenText = results.get(0);
            Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "], results.toString()" + results.toString());

            // Do something with spokenText

            testSpeechRecResult.setText(spokenText);
            checkAnswer(spokenText);

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void checkAnswer(String result){
        switch (qNr){

            case 0:
                checkDayAnswer(result);

                break;
            case 1:
                checkMonthAnswer(result);

                break;

            case 2:
                checkYearAnswer(result);

                break;

            case 3:
                checkWordsAnswer(result);
                break;

            case 4:
                checkWordsAnswers(result);
                break;
        }

    }

    private void checkWordsAnswers(String result) {




    }

    /**
     * TODO: this.
     * @param result
     */

    private void checkWordsAnswer(String result) {
    }

    private void checkMonthAnswer(String result) {

        Log.d(TAG, "checkDayAnswer() called with: result = [" + result + "]");

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currentMonth = new SimpleDateFormat("MMM",Locale.GERMAN).format(date.getTime());

        if(DEBUG) {
            testSpeechRecResult.append("\nCurrent Month:" + currentMonth);
            testSpeechRecResult.append("\nAnswer:" + result);
            testSpeechRecResult.append("\nAnswer contains Month:" + result.contains(currentMonth));
        }
        boolean tag = false;

        if(result.contains(currentMonth))
            tag = true;
        else
            tag = false;

        //tag the person.
        this.getWindow().getDecorView().setBackgroundColor(tag ? Color.YELLOW : Color.RED);

        qNr++;
        askQuestion();
    }

    private void checkYearAnswer(String result) {
        Log.d(TAG, "checkDayAnswer() called with: result = [" + result + "]");

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currentYear = new SimpleDateFormat("YYYY",Locale.GERMAN).format(date.getTime());

        if(DEBUG) {
            testSpeechRecResult.append("\nCurrent Day:" + currentYear);
            testSpeechRecResult.append("\nAnswer:" + result);
            testSpeechRecResult.append("\nAnswer contains Current Day:" + result.contains(currentYear));
        }
        boolean tag = false;

        if(result.contains(currentYear))
            tag = true;
        else
            tag = false;

        //tag the person.
        this.getWindow().getDecorView().setBackgroundColor(tag ? Color.YELLOW : Color.RED);

        qNr++;
        askQuestion();
    }



    /**Check the string for mentioning of the day
     * TODO : Localisation.
     */
    private void checkDayAnswer(String result) {
        Log.d(TAG, "checkDayAnswer() called with: result = [" + result + "]");

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currentDay = new SimpleDateFormat("EEEE",Locale.GERMAN).format(date.getTime());

        if(DEBUG) {
            testSpeechRecResult.append("\nCurrent Day:" + currentDay);
            testSpeechRecResult.append("\nAnswer:" + result);
            testSpeechRecResult.append("\nAnswer contains Current Day:" + result.contains(currentDay));
        }
        boolean tag = false;

        if(result.contains(currentDay))
            tag = true;
        else
            tag = false;

        //tag the person.
        this.getWindow().getDecorView().setBackgroundColor(tag ? Color.YELLOW : Color.RED);

        qNr++;
        askQuestion();

    }
}
