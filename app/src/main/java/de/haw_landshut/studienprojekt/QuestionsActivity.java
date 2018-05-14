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
import java.util.Random;


import static de.haw_landshut.studienprojekt.BuildConfig.DEBUG;

/**
 * An Activity for asking Questions and taking answers with TTS and STT.
 * It uses google intents to translate questions to speech and read them,
 * than takes an answer through speech and looks for answer keywords in the returned String.
 */
public class QuestionsActivity extends AppCompatActivity {
    //This constant uses the name of the class itself as the tag.
    private static final String TAG = QuestionsActivity.class.getSimpleName();



    //TTS vars

    //name for the TTS engine we want to use, in this case we make sure that the user has the default android TTS, and init with it.
    private final String googleTTSPackage = "com.google.android.tts";

    /**Listener for TTS to know when it has been initialised.
     *
     * @return the TextToSpeech.OnInitListener.
     */
    private TextToSpeech.OnInitListener TTSonInitListener =
            new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        setupTTS();
                    }else {
                        handleTTSinitError();
                    }

                }
            };

    /**This function is run after TTS is initialised. We need TTS to start asking questions, so it also starts our questioning after setting up some settings.
     * We use TTS to start questioning when every TTS setup is done by speaking out empty string and catching it with utteranceProgressListener.
     * TODO: set language should be according to the localisation.
     */
    private void setupTTS() {
        textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
        textToSpeech.setLanguage(Locale.GERMANY);

        //this will start the questioning.
        addToTTSandFlush(" ","TTSdone");

    }

    private final String TTS_PAUSE = "... ";

    //For Questioning

    private final int TOTAL_RANDOM_WORD_COUNT = 499;
    private final int AMOUNT_RANDOM_WORDS = 3;
    private final String RANDOM_WORD_ID = "random_word";
    private TextToSpeech textToSpeech;

    private boolean isAskingQuestion = false;
    private int currentQuestionID;



    //For Testing
    private TextView testSpeechRecResult;
    ArrayList<String> questionList;
    private int questionNr = -2;

    private String[] randomWordsList;
    private Bundle settingsBundle;

    //------
    /**
     * Code for recognising callback from speech recogniser.
     */
    private static final int SPEECH_WHAT_DAY_CODE = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        //init Lists
        questionList = makeQuestionList(); //ArrayList for questions.

        randomWordsList = selectRandomWords(); //String[] for words to remember.    //init Lists
        questionList = makeQuestionList(); //ArrayList for questions.

        //pass along the settings for the simulated evaluation.
        settingsBundle = new Bundle();

        settingsBundle.putAll(getIntent().getExtras());

    }

    @Override
    protected void onStart() {
        super.onStart();


        //TestThings©
        testSpeechRecResult = findViewById(R.id.answer);
        //check if settings arrived well:
        testSpeechRecResult.append("\nisWalking = " + settingsBundle.getBoolean("isWalking"));
        testSpeechRecResult.append("\nisMoving = " + settingsBundle.getBoolean("isMoving"));
        testSpeechRecResult.append("\n RRate = " + settingsBundle.getInt("RRate"));
        testSpeechRecResult.append("\n HRate = " + settingsBundle.getInt("HRate"));
        //--TestThings®



        currentQuestionID = 0;

        //try to start TTS, we are hoping the default google TTS is in place.
        try {
            textToSpeech = new TextToSpeech(getApplicationContext(), TTSonInitListener, googleTTSPackage);
        }catch (Exception e){
            Log.e(TAG, "onStart: ",e );
        }

    }

    /**TTS progress listener to know when TTS has finished speaking and other states.
     *
     * @return the UtteranceProgressListener;
     */
    private UtteranceProgressListener utteranceProgressListener =
         new UtteranceProgressListener() {

            @Override
            public void onDone(String utteranceId) {
                isAskingQuestion = false;
                TTSonDoneHandler(utteranceId);
            }

            @Override
            public void onError(String utteranceId) {
                isAskingQuestion = false;
                Log.d(TAG, "TTS error");
            }

            @Override
            public void onStart(String utteranceId) {
                isAskingQuestion = true;
                Log.d(TAG, "TTS start");
            }
        };





    /**
     * Starts asking questions, depends on questionNr to increment through questions.
     * TODO: improve the flow. after changing the TTS STT functions. Mainly figure another method to run through questions.
     */
    private void askQuestion() {
        questionNr++;
        if (questionNr < questionList.size()) {
            if (questionNr == -1)
                askToRemember();
            else
                textToSpeech = TTS(questionList.get(questionNr));
        } else {
            testSpeechRecResult.append("\nOutside questionList.");
        }
    }


    private boolean questionsFlow(){

        switch (currentQuestionID){

            case 0: // Ask to Remember the Words
                isAskingQuestion = true;
                askToRemember();
                break;



        }

        return false;
    }

    private boolean askQuestion(String question){

        return false;
    }
    /**
     * Function to ask the user to remember words.
     */
    private void askToRemember() {

        String temp = getString(R.string.remember_words);
        for (int i = 0; i < AMOUNT_RANDOM_WORDS; i++) {
            temp = temp.concat(randomWordsList[i] + TTS_PAUSE);
        }

        addToTTSandFlush(temp,"askToRemember");
    }

    private ArrayList<String> makeQuestionList() {
        ArrayList<String> r = new ArrayList<>();
        r.add("Welches Jahr haben wir denn heute?");
        r.add("Welcher Monat ist es?");
        r.add(getString(R.string.what_day_question));
        r.add("Bitte wiederholen Sie die 3 Wörter von vorher!!!");


        return r;

    }

    /**
     * Creates new TTS intent and reads the given text.
     *
     * @return
     */

    private TextToSpeech TTS(final String text) {
        Log.d(TAG, "TTS() called with: text = [" + text + "]");
        return new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {

                    Log.d(TAG, "onInit: TTS start success");


                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");


                    textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, map);

                }
            }
        });
    }

    /**Adds strings to the end of TTS queue to be spoken out.
     *
     * @param text String to speak out.
     * @param uniqueID unique id that will be used to call out response handling function.
     */

    private void addToTTSQueue(String text, String uniqueID){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, uniqueID);
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, map);

    }
    private void addToTTSandFlush(String text, String uniqueID){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, uniqueID);
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, map);

    }

    /**Handles the call from TTS when it is done speaking.
     *
     * @param utteranceId TTS returns the calls "unique" id.
     */

    private void TTSonDoneHandler(String utteranceId) {
        if(utteranceId.equals("TTSdone")){
            questionsFlow();
        }

        testSpeechRecResult.append("Just Asked the words.");
    }


    /**
     * Creates and displays an intent to write speech to text.
     * TODO: Localisation, go over settings and choose the best ones, maybe allow the caller to choose them. Modularise this function
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

    /**
     * Callback function for STT.
     *
     * @param requestCode is the code of the STT intent.
     * @param resultCode  STT passes the code of success/failure.
     * @param data        data from the STT.
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

    private void checkAnswer(String result) {
        switch (questionNr) {
            case -1:

                askQuestion();
                break;
            case 0:
                checkYearAnswer(result);
                break;
            case 1:
                checkMonthAnswer(result);
                break;

            case 2:

                checkDayAnswer(result);
                break;

            case 3:
                checkWordsAnswer(result);
                break;

        }

    }

    private void checkWordsAnswer(String result) {

        Log.d(TAG, " checkWordsAnswer called with: result = [" + result + "]");
        int correct = 0;
        testSpeechRecResult.append("\nWords that have been asked to remember:");


        for (String aRandomWordsList : randomWordsList) {
            testSpeechRecResult.append("\n" + aRandomWordsList);
            if (result.toLowerCase().contains(aRandomWordsList.toLowerCase()))
                correct++;
        }

        testSpeechRecResult.append("\nTTS Answer:" + result);
        testSpeechRecResult.append("\nCorrect:" + correct);


    }


    /**
     * TODO: this.
     *
     * @param result
     */


    private void checkMonthAnswer(String result) {

        Log.d(TAG, " checkMonthAnswer called with: result = [" + result + "]");

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currentMonth = new SimpleDateFormat("MMM", Locale.GERMAN).format(date.getTime());

        if (DEBUG) {
            testSpeechRecResult.append("\nCurrent Month:" + currentMonth);
            testSpeechRecResult.append("\nAnswer:" + result);
            testSpeechRecResult.append("\nAnswer contains Month:" + result.contains(currentMonth));
        }
        boolean tag = false;

        if (result.contains(currentMonth))
            tag = true;
        else
            tag = false;

        //tag the person.
        this.getWindow().getDecorView().setBackgroundColor(tag ? Color.YELLOW : Color.RED);


        askQuestion();
    }

    private void checkYearAnswer(String result) {
        Log.d(TAG, "checkYearAnswer() called with: result = [" + result + "]");

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currentYear = new SimpleDateFormat("YYYY", Locale.GERMAN).format(date.getTime());

        if (DEBUG) {
            testSpeechRecResult.append("\nCurrent Day:" + currentYear);
            testSpeechRecResult.append("\nAnswer:" + result);
            testSpeechRecResult.append("\nAnswer contains Current Day:" + result.contains(currentYear));
        }
        boolean tag = false;

        if (result.contains(currentYear))
            tag = true;
        else
            tag = false;

        //tag the person.
        this.getWindow().getDecorView().setBackgroundColor(tag ? Color.YELLOW : Color.RED);
        askQuestion();
    }


    /**
     * Check the string for mentioning of the day
     * TODO : Localisation.
     */
    private void checkDayAnswer(String result) {
        Log.d(TAG, "checkDayAnswer() called with: result = [" + result + "]");

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currentDay = new SimpleDateFormat("EEEE", Locale.GERMAN).format(date.getTime());

        if (DEBUG) {
            testSpeechRecResult.append("\nCurrent Day:" + currentDay);
            testSpeechRecResult.append("\nAnswer:" + result);
            testSpeechRecResult.append("\nAnswer contains Current Day:" + result.contains(currentDay));
        }
        boolean tag = false;

        if (result.contains(currentDay))
            tag = true;
        else
            tag = false;

        //tag the person.
        this.getWindow().getDecorView().setBackgroundColor(tag ? Color.YELLOW : Color.RED);


        askQuestion();

    }

    //---------------------DONE Functions, low priority.
    /**
     * Returns a string with AMOUNT_RANDOM_WORDS random words from the string.xml resource.
     *
     * @return String[] with random words.
     */
    private String[] selectRandomWords() {
        String[] r = new String[AMOUNT_RANDOM_WORDS];


        for (int i = 0; i < r.length; i++) {
            r[i] = getString(getResources().getIdentifier(RANDOM_WORD_ID + (new Random().nextInt(TOTAL_RANDOM_WORD_COUNT) + 1), "string", getPackageName()));

        }

        return r;
    }


    /**TODO:A handler for when a TTS engine fails to initialise.
     *
     */
    private void handleTTSinitError() {
    }
}
