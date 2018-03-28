package de.haw_landshut.studienprojekt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import AlizeSpkRec.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SpeakerRecognitionTest extends AppCompatActivity {

    private static final String TAG = SpeakerRecognitionTest.class.getSimpleName();
    private static final int RECORD_AUDIO_PERMISSION_REQUEST = 001;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST = 002;

    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;

    private static TextView debugView;

    //Alize

    SimpleSpkDetSystem alizeSystem;

    //Recording test buttons.
    private static String audioFilePath;
    private static Button stopButton;
    private static Button playButton;
    private static Button recordButton;

    //Speaker Recog
    private static Button trainButton;
    private static Button stopTrain;

    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    private int nr = 1;

    private String audioPCMFilePath =
            Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/pcmAudio.pcm";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_recognition_test);
        ///--------------///

        //init textView for debuging.
        debugView = (TextView) findViewById(R.id.speakerTextView);
        debugView.setText("System status: SpeakerRec started\n");

        //check for permissions for recording
        askForPermisions();

        //test For recording audio.
        prepRecordingTest();

        // init Speaker Recognition.
        try {
            initSR();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlizeException e) {
            e.printStackTrace();
        }

        setButtonHandlers();



        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);



    }


    private void setButtonHandlers() {
        ((Button) findViewById(R.id.trainButton)).setOnClickListener(buttonListener);
        ((Button) findViewById(R.id.stopTrain)).setOnClickListener(buttonListener);


    }

    private void enableButton(int id, boolean isEnable) {
        ((Button) findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons() {
        enableButton(R.id.trainButton, !isRecording);
        enableButton(R.id.stopTrain, isRecording);
    }



    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format

    private void startRecording() {

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        recorder.startRecording();
        isRecording = true;

        enableButtons();

    }


    private void stopRecording() {
        // stops the recording activity

        short sData[] = new short[BufferElements2Rec];


        if (null != recorder) {
            recorder.read(sData, 0, BufferElements2Rec);
            System.out.println("Short data" + sData.toString());

            isRecording =false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }



        trainASpeaker(sData);
    }






    //initialise Alize
    void initSR() throws IOException, AlizeException {

        InputStream configAsset = getResources().openRawResource(R.raw.sr_config);
        alizeSystem = new SimpleSpkDetSystem(configAsset, getApplicationContext().getFilesDir().getPath());
        configAsset.close();

        InputStream backgroundModelAsset = getResources().openRawResource(R.raw.world);
        alizeSystem.loadBackgroundModel(backgroundModelAsset);
        backgroundModelAsset.close();

        debugView.append("System status:\n");
        debugView.append("  # of features: " + alizeSystem.featureCount()+"\n");
        debugView.append("  # of models: " + alizeSystem.speakerCount()+"\n");
        debugView.append("  UBM is loaded: " + alizeSystem.isUBMLoaded()+"\n");








    }

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.trainButton: {
                    startRecording();
                    enableButtons();
                    break;
                }
                case R.id.stopTrain: {
                    stopRecording();
                    enableButtons();
                    break;
                }
            }


        }
    };

    private boolean trainASpeaker(short[] inputAudioStream){


        InputStream configAsset = getResources().openRawResource(R.raw.sr_config);
        try {
            alizeSystem = new SimpleSpkDetSystem(configAsset, getApplicationContext().getFilesDir().getPath());
            configAsset.close();

            InputStream backgroundModelAsset = getResources().openRawResource(R.raw.world);
            alizeSystem.loadBackgroundModel(backgroundModelAsset);
            backgroundModelAsset.close();
        } catch (AlizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        //always returning true for now.

        // Record audio
        // The system takes 16-bit, signed integer linear PCM, at the frequency specified in the configuration file.
        short[] audio = inputAudioStream;

         // Send audio to the system
        try {
            alizeSystem.addAudio(audio);
        } catch (AlizeException e) {
            e.printStackTrace();
        }

        // Train a model with the audio
        try {
            alizeSystem.createSpeakerModel("Somebody"+nr);
        } catch (AlizeException e) {
            e.printStackTrace();
        }


        debugView.setText("System status:\n");
        try {
            debugView.append("  # of features: " + alizeSystem.featureCount()+"\n");
            debugView.append("  # of models: " + alizeSystem.speakerCount()+"\n");
            debugView.append("  UBM is loaded: " + alizeSystem.isUBMLoaded()+"\n");
        } catch (AlizeException e) {
            e.printStackTrace();
        }



        return true;

    }



    //this is just for testing if audio is recording. to be removed later.
    private void prepRecordingTest() {

        System.out.println("System status: Button init");
        recordButton = (Button) findViewById(R.id.recordButton);
        playButton = (Button) findViewById(R.id.playButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        if (!hasMicrophone())
        {
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            recordButton.setEnabled(false);
        } else {
            playButton.setEnabled(false);
            stopButton.setEnabled(false);
        }

        audioFilePath =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/myaudio.3gp";


    }



    //When the user touches the Record button, the recordAudio() method will be called. This method will need to enable and disable the appropriate buttons, configure the MediaRecorder instance with information about the source of the audio, the output format and encoding and the location of the file into which the audio is to be stored. Finally, the prepare() and start() methods of the MediaRecorder object will need to be called.
    public void recordAudio (View view) throws IOException
    {

        stopButton.setEnabled(true);
        playButton.setEnabled(false);
        recordButton.setEnabled(false);

        try {
            mediaRecorder = new MediaRecorder();
            resetRecorder();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }





    // this process must be done prior to the start of recording
    private void resetRecorder() {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setAudioEncodingBitRate(16);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    //The stopAudio() method is responsible for enabling the Play button, disabling the Stop button and then stopping and resetting the MediaRecorder instance.

    public void stopAudio (View view)
    {

        stopButton.setEnabled(false);
        playButton.setEnabled(true);

        if (isRecording)
        {
            Log.d(TAG, "stopAudio: if");
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            playButton.setEnabled(true);
            stopButton.setEnabled(false);
        } else {
            mediaPlayer.release();
            mediaPlayer = null;
            recordButton.setEnabled(true);
        }
    }


    //The playAudio() method will simply create a new MediaPlayer instance, assign the audio file located on the SD card as the data source and then prepare and start the playback:

    public void playAudio (View view) throws IOException
    {
        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        stopButton.setEnabled(true);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(audioFilePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }



    void askForPermisions(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        RECORD_AUDIO_PERMISSION_REQUEST);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            case WRITE_EXTERNAL_STORAGE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

            }
            return;
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    // This involves creating an instance of the Android PackageManager class and then making a call to the object’s hasSystemFeature() method. In this case, the feature of interest is PackageManager.FEATURE_MICROPHONE.
    protected boolean hasMicrophone() {
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }

}
