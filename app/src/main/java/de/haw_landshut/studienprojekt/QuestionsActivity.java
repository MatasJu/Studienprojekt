package de.haw_landshut.studienprojekt;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

import android.speech.tts.UtteranceProgressListener;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Locale;
import java.util.Random;


import de.haw_landshut.studienprojekt.settings.AndroidBaseActivity;
import de.haw_landshut.studienprojekt.settings.LocaleHelper;

import static de.haw_landshut.studienprojekt.BuildConfig.DEBUG;

/**Custom Enumerator for getting Question strings and their codes to use with TTS and STT.
 */
enum Questions {
    REMEMBER_WORDS(QuestionsActivity.getContext().getString(R.string.remember_words), QuestionsActivity.CODE_REMEMBER_WORDS),
    WHAT_YEAR(QuestionsActivity.getContext().getString(R.string.what_year_question), QuestionsActivity.CODE_WHAT_YEAR),
    WHAT_MONTH(QuestionsActivity.getContext().getString(R.string.what_month_question), QuestionsActivity.CODE_WHAT_MONTH),
    WHAT_DAY(QuestionsActivity.getContext().getString(R.string.what_day_question), QuestionsActivity.CODE_WHAT_DAY),
    REPEAT_WORDS(QuestionsActivity.getContext().getString(R.string.repeat_words_please), QuestionsActivity.CODE_REPEAT_WORDS);

    public final int qID;
    public final String qString;

    Questions(String qString, int qID) {
        this.qID = qID;
        this.qString = qString;
    }

    private static final Questions[] values = values();

    public boolean hasNext(){
        return this.ordinal()<values.length-1;
    }

    public Questions next(){
        return values[(this.ordinal()+1)%values.length];
    }


}

/**
 * An Activity for asking Questions and taking answers with TTS and STT.
 * <p>
 * It uses google intents to translate questions to speech and read them,
 * than takes an answer through speech and looks for answer keywords in the returned String.
 * <p>
 * TODO: Separate TTS and STT into their own classes could be a good idea?
 * TODO: need to stop everything when app goes toStop() or otherwise "minimised", and than continue it well forward... right now it's speaking in the background.
 * TODO: Change TTS locale according to app or phone.
 */
public class QuestionsActivity extends AndroidBaseActivity {


    //This constant uses the name of the class itself as the tag.
    private static final String TAG = QuestionsActivity.class.getSimpleName();
    //so we can reference to this from enum
    private static Context mContext;
    private int correct=0;

    public static Context getContext() {
        return mContext;
    }



    /*===============TTS VARIABLES=================*/


    /**
     * Listener for TTS to know when it has been initialised.
     *
     *
     */
    private final TextToSpeech.OnInitListener TTSonInitListener =
            status -> {
                if (status == TextToSpeech.SUCCESS) {
                    setupTTS();
                } else {
                    handleTTSinitError();
                }

            };

    /**
     * TTS progress listener to know when TTS has finished speaking and other states.
     *
     *
     */
    private final UtteranceProgressListener utteranceProgressListener =
            new UtteranceProgressListener() {

                @Override
                public void onDone(String utteranceId) {

                    TTSHandler(utteranceId);
                }

                @Override
                public void onError(String utteranceId) {

                    Log.d(TAG, "TTS error");
                }

                @Override
                public void onStart(String utteranceId) {

                    Log.d(TAG, "TTS start");
                }
            };


    private final String TTS_PAUSE = "... ";

    /*===============END TTS VARIABLES=================*/




    /*===============START STT VARIABLES=====================*/

    //HANDLER TAGS:

    private static final int REPEAT_OR_FORWARD = 652;

    String STTresultString;


    /*===============END STT VARIABLES=================*/





    /*==============START QUESTIONNAIRE VARIABLES============*/

    public static final int CODE_REMEMBER_WORDS = 100;
    public static final int CODE_WHAT_YEAR = 200;
    public static final int CODE_WHAT_DAY = 300;
    public static final int CODE_WHAT_MONTH = 400;
    public static final int CODE_REPEAT_WORDS = 500;
    private final int AMOUNT_RANDOM_WORDS = 3;


    private Questions currentQuestion;

    private String[] randomWordsList;

    /*==============END QUESTIONNAIRE VARIABLES============*/


    /*===========START ACTIVITY VARIABLES===========*/
    private TextToSpeech TTSEngine;

    private TextView testSpeechRecResult;
    //end for testing

    /*===========END ACTIVITY VARIABLES===========*/


    /*===========ACTIVITY FUNCTIONS===========*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        mContext = this;

        //init Lists
        randomWordsList = selectRandomWords(); //String[] for words to remember.    //init Lists


    }

    @Override
    protected void onStart() {
        super.onStart();


        //TestThings©
        testSpeechRecResult = findViewById(R.id.answer);
        //make the testTextView scrollable:
        testSpeechRecResult.setMovementMethod(new ScrollingMovementMethod());
        //--TestThings®


        currentQuestion = Questions.REMEMBER_WORDS;


        //try to start TTS, we are hoping the default google TTS is in place.
        try {
            String googleTTSPackage = "com.google.android.tts";
            TTSEngine = new TextToSpeech(getApplicationContext(), TTSonInitListener, googleTTSPackage);
        } catch (Exception e) {
            Log.e(TAG, "onStart: ", e);
        }

    }

    /*===========END ACTIVITY FUNCTIONS ===========*/

    /*===========QUESTIONNAIRE FUNCTIONS===========*/

    /**
     * this controls the whole Questioning spiel.
     * depends on the currentQuestionID to ask the next question.
     *
     * @return true if all questions have been asked.
     */
    private boolean questionsFlow() {

        if(currentQuestion.qID==CODE_REMEMBER_WORDS) {
            askToRemember();
        }else {
            askQuestion();
        }

        return false;
    }

    /**Asks a normal question""
     *
     */
    private void askQuestion() {
        addToTTSQueue(currentQuestion.qString,"GetAnswer");
    }

    /**
     * Function to ask the user to remember words.
     */
    private void askToRemember() {

        String temp = getString(R.string.remember_words);
        for (int i = 0; i < AMOUNT_RANDOM_WORDS; i++) {
            temp = temp.concat(randomWordsList[i] + TTS_PAUSE);
        }

        addToTTSQueue(temp, "QuestionDone");
    }

    private void checkAnswer(String result) {
        switch (currentQuestion.qID) {

            case CODE_WHAT_YEAR:
                if(checkYearAnswer(result)){
                    correct++;
                }
                    askToRepeatOrGoForward() ;


                break;

            case CODE_WHAT_MONTH:
                if(checkMonthAnswer(result)){
                    correct++;
                }
                    askToRepeatOrGoForward();

                break;

            case CODE_WHAT_DAY:
                if(checkDayAnswer(result)) {
                    correct++;
                }
                    askToRepeatOrGoForward();

                break;
            //TODO: So currently if a person repeats the three words he gets extra points, why not? must be conscious if he can repeat them many times...
            case CODE_REPEAT_WORDS:
                correct+=checkWordsAnswer(result);
                askToRepeatOrGoForward();
                break;

        }

    }


    /*===========END QUESTIONNAIRE METHODS===========*/

    /*===========TTS METHODS=================*/

    /**
     * This function is run after TTS is initialised. We need TTS to start asking questions, so it also starts our questioning after setting up some settings.
     * We use TTS to start questioning when every TTS setup is done by speaking out empty string and catching it with utteranceProgressListener.
     * TODO: set language should be according to the localisation.
     */
    private void setupTTS() {
        TTSEngine.setOnUtteranceProgressListener(utteranceProgressListener);


        TTSEngine.setLanguage(Locale.getDefault());

        //this will start the questioning.
        addToTTSandFlush(" ", "TTSdone");

    }

    /**
     * Adds strings to the end of TTS queue to be spoken out.
     *
     * @param text     String to speak out.
     * @param uniqueID unique id that will be used to call out response handling function.
     */

    private void addToTTSQueue(String text, String uniqueID) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, uniqueID);
        TTSEngine.speak(text, TextToSpeech.QUEUE_ADD, map);

    }

    private void addToTTSandFlush(String text, String uniqueID) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, uniqueID);
        TTSEngine.speak(text, TextToSpeech.QUEUE_FLUSH, map);

    }

    /**
     * Handles the call from TTS when it is done speaking.
     *
     * @param utteranceId TTS returns the calls "unique" id.
     */

    private void TTSHandler(String utteranceId) {
        switch (utteranceId) {
            case "GetAnswer":
                displaySpeechRecognizer(currentQuestion.qString,currentQuestion.qID);
                break;
            case "QuestionDone":
                askToRepeatOrGoForward();
                break;
            case "AskedWhatToDo":
                displaySpeechRecognizer(getString(R.string.next_question) + getString(R.string.repeat_question), REPEAT_OR_FORWARD);
                break;
            default:
                questionsFlow();
                break;
        }


    }

    private void askToRepeatOrGoForward() {
        addToTTSandFlush( getString(R.string.repeat_question)+ TTS_PAUSE + getString(R.string.next_question), "AskedWhatToDo");
    }

    /*================END TTS METHODS==================*/

    /*==================STT METHODS======================*/

    /**
     * Creates and displays an intent to write speech to text.
     *
     * @param question    The question to be display on title.
     * @param requestCode requestCode to be handled on done for STThandler();
     *                    <p>
     *                    TODO: Localisation, go over settings and choose the best ones, maybe allow the caller to choose them. Modularise this function
     */
    private void displaySpeechRecognizer(String question, int requestCode) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, LocaleHelper.getPersistedLocale(this));
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);

        //Offline recognition will work only above api lvl 23, also has to be downloaded outside the app in the language settings.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        }
        //Title for the intent
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, question);
        //how many extra recognised strings we want, as i understood the first is the most likely, so for now 1 extra is more than needed.
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, requestCode);
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

        if (resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (DEBUG) {
                testSpeechRecResult.append("Spoken Text: " + results.get(0));
            }
            //Call a handler with the text.
            //the result at place 0 is the best guess at this time....
            STTHandler(requestCode,results.get(0));

        }

        //super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * Handles the STT result.
     */

    private void STTHandler(int requestCode,String result) {
        switch (requestCode) {
            case REPEAT_OR_FORWARD:
                repeatOrForward(result);
                break;
            default:
                checkAnswer(result);
                break;
        }
    }

    private void repeatOrForward(String result) {
        //if result had "forward" in it select next question.
        if (result.contains(getString(R.string.answer_next_question))) {
            if(currentQuestion.hasNext()) {
                currentQuestion= currentQuestion.next();
                questionsFlow();
            }else {
                done();
            }
        //else just repeat same question.
        } else {
            questionsFlow();
        }

    }




    /*============== END STT functions====================*/

    /**When we done with questions:
     * TODO: THIS;
     */
    private void done() {
        testSpeechRecResult.append("Points:" +correct);
        TTSEngine.stop();


        Intent intent = new Intent(this, Evaluation.class);
        intent.putExtra("correct",correct);
        startActivity(intent);

    }


    //---------------------DONE Functions, low priority.

    /**
     * Checks the result string for 3 words mentioned before. Returns an int of correct answers.
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


    /**
     * Checks if the String has the current month in it.
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

        return result.contains(currentMonth);

    }

    /**
     * Checks if the string has current year in it.
     *
     * @param result true false
     */
    private boolean checkYearAnswer(String result) {


        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currentYear = new SimpleDateFormat("YYYY", Locale.GERMAN).format(date.getTime());

        if (DEBUG) {
            Log.d(TAG, "checkYearAnswer() called with: result = [" + result + "]");
            testSpeechRecResult.append("\nCurrent Year:" + currentYear);
            testSpeechRecResult.append("\nAnswer:" + result);
            testSpeechRecResult.append("\nAnswer contains Current Year:" + result.contains(currentYear));
        }

        return result.contains(currentYear);

    }


    /**
     * Check the string for mentioning of the day
     * <p>
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
            String RANDOM_WORD_ID = "random_word";
            int TOTAL_RANDOM_WORD_COUNT = 499;
            r[i] = getString(getResources().getIdentifier(
                    RANDOM_WORD_ID + (new Random().nextInt(TOTAL_RANDOM_WORD_COUNT) + 1),
                    "string", getPackageName())
            );

        }

        return r;
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        if(TTSEngine != null){
            TTSEngine.shutdown();
        }
    }


    /**
     * TODO:A handler for when a TTS engine fails to initialise.
     */
    private void handleTTSinitError() {
    }

}
