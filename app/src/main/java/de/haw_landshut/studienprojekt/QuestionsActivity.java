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
    private final int TOTAL_RANDOM_WORD_COUNT = 499;
    private final int AMOUNT_RANDOM_WORDS = 3;
    private final String RANDOM_WORD_ID = "random_word";
    private TextToSpeech textToSpeech;
    //For Testing
    private TextView testSpeechRecResult;
    ArrayList<String> questionList;
    private int questionNr = -2;

    private String[] randomWordsList;

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

        randomWordsList = selectRandomWords(); //String[] for words to remember.

        //TestThings©
        testSpeechRecResult = findViewById(R.id.answer);
        //--TestThings®


        askQuestion();

    }

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

    /**
     * Function to ask the user to remember words.
     */
    private void askToRemember() {

        String temp = getString(R.string.remember_words);
        for (int i = 0; i < AMOUNT_RANDOM_WORDS; i++) {
            temp = temp.concat(randomWordsList[i] + ".");
        }

        textToSpeech = TTS(temp);
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
                    textToSpeech.setLanguage(Locale.GERMANY);
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        @Override
                        public void onDone(String utteranceId) {
                            Log.d(TAG, "TTS finished");
                            if (questionNr >= 0) {
                                displaySpeechRecognizer();
                            } else {
                                Log.d(TAG, "onDone() called with: utteranceId = [" + utteranceId + "]");
                                askQuestion();

                            }
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
}
