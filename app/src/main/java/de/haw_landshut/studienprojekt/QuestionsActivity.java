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

    private ArrayList<String> makeQuestionList = new ArrayList<>();

    {

        makeQuestionList.add(getString(R.string.remember_words));
        makeQuestionList.add(getString(R.string.what_year_question));
        makeQuestionList.add(getString(R.string.what_month_question));
        makeQuestionList.add(getString(R.string.what_day_question));
        makeQuestionList.add(getString(R.string.repeat_words_please));

    }


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


        randomWordsList = selectRandomWords(); //String[] for words to remember.    //init Lists

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

    /** this controls the whole Questioning spiel.
     * depends on the currentQuestionID to ask the next question.
     *@see <code>currentQuestionID</code>
     * @return true wenn all questions have been asked.
     */
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

                //askQuestion();
                break;
            case 1:
                checkYearAnswer(result);
                break;

            case 2:
                checkMonthAnswer(result);
                break;

            case 3:
                checkDayAnswer(result);
                break;

            case 4:
                checkWordsAnswer(result);
                break;

        }

    }

    //---------------------DONE Functions, low priority.

    /**Checks the result string for 3 words mentioned before. Returns an int of correct answers.
     *
     * @param result string to be checked for 3 words.
     * @return amount of correct answers.
     */
    private int checkWordsAnswer(String result) {


        int correct = 0;



        for (String aRandomWordsList : randomWordsList) {
            testSpeechRecResult.append("\n" + aRandomWordsList);
            if (result.toLowerCase().contains(aRandomWordsList.toLowerCase()))
                correct++;
        }
        if (DEBUG) {
            Log.d(TAG, " checkWordsAnswer called with: result = [" + result + "]");
            testSpeechRecResult.append("\nTTS Answer:" + result);
            testSpeechRecResult.append("\nCorrect:" + correct);
            testSpeechRecResult.append("\nWords that have been asked to remember:");
        }
        return correct;
    }




    /** Checks if the String has the current month in it.
     *
     *
     * @param result string to be checked.
     */
    private boolean checkMonthAnswer(String result) {


        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currentMonth = new SimpleDateFormat("MMM", Locale.GERMAN).format(date.getTime());

        if (DEBUG) {
            Log.d(TAG, " checkMonthAnswer called with: result = [" + result + "]");
            testSpeechRecResult.append("\nCurrent Month:" + currentMonth);
            testSpeechRecResult.append("\nAnswer:" + result);
            testSpeechRecResult.append("\nAnswer contains Month:" + result.contains(currentMonth));
        }
        boolean tag = false;

        if (result.contains(currentMonth))
            tag = true;
        else
            tag = false;

    }

    /**Checks if the string has current year in it.
     *
     * @param result
     */
    private boolean checkYearAnswer(String result) {


        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currentYear = new SimpleDateFormat("YYYY", Locale.GERMAN).format(date.getTime());

        if (DEBUG) {
            Log.d(TAG, "checkYearAnswer() called with: result = [" + result + "]");
            testSpeechRecResult.append("\nCurrent Day:" + currentYear);
            testSpeechRecResult.append("\nAnswer:" + result);
            testSpeechRecResult.append("\nAnswer contains Current Day:" + result.contains(currentYear));
        }

        return result.contains(currentYear);



    }


    /**Check the string for mentioning of the day
     *
     * TODO : Localisation.
     */
    private boolean checkDayAnswer(String result) {


        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currentDay = new SimpleDateFormat("EEEE", Locale.GERMAN).format(date.getTime());

        if (DEBUG) {
            Log.d(TAG, "checkDayAnswer() called with: result = [" + result + "]");
            testSpeechRecResult.append("\nCurrent Day:" + currentDay);
            testSpeechRecResult.append("\nAnswer:" + result);
            testSpeechRecResult.append("\nAnswer contains Current Day:" + result.contains(currentDay));
        }

        return result.contains(currentDay);


    }


    /**
     * Returns a string with AMOUNT_RANDOM_WORDS random words from the string.xml resource.
     *
     * @return String[] with random words.
     */
    private String[] selectRandomWords() {
        String[] r = new String[AMOUNT_RANDOM_WORDS];


        for (int i = 0; i < r.length; i++) {
            r[i] = getString(getResources().getIdentifier(
                    RANDOM_WORD_ID +(new Random().nextInt(TOTAL_RANDOM_WORD_COUNT) + 1),
                    "string", getPackageName())
            );

        }

        return r;
    }


    /**TODO:A handler for when a TTS engine fails to initialise.
     *
     */
    private void handleTTSinitError() {
    }
}
