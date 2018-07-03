package de.haw_landshut.studienprojekt;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


public class MotionSensor {
    private static MotionSensor singleton;
    private final SensorManager sMgr;
    private final Sensor stepSensor;
    private final Sensor accelSensor;
    //Buffer for StepSensor
    private final int bufferLimit = 9;
    private long[] stepTimes = new long[bufferLimit + 1];
    private int current = 0;
    //Motion Detection
    private boolean movement;
    private long lastUpdate;

    private boolean walking;
    //shutoff
    private boolean active = true;

    /**
     * @param cont DeviceContext
     */

    private MotionSensor(Context cont) {
        sMgr = (SensorManager) cont.getSystemService(Context.SENSOR_SERVICE);
        assert sMgr != null;
        stepSensor = sMgr.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepSensor != null) {
            SensorEventListener sel = new SensorEventListener() {
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    //required method
                }

                public void onSensorChanged(SensorEvent event) {
                    if (current < bufferLimit) {
                        stepTimes[current] = event.timestamp;
                    }
                }
            };
            sMgr.registerListener(sel, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        accelSensor = sMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (accelSensor != null) {
            SensorEventListener accSel = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    double x = sensorEvent.values[0];
                    double y = sensorEvent.values[1];
                    double z = sensorEvent.values[2];
                    double acceleration = Math.sqrt(x * x + y * y + z * z);
                    if (acceleration > 1) {
                        movement = true;
                        lastUpdate = sensorEvent.timestamp;
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {
                    //required method
                }
            };
            sMgr.registerListener(accSel, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public static MotionSensor getMotionSensor() {
        return singleton;
    }


    public static void instantiate(Context cont) {
        singleton = new MotionSensor(cont);
        singleton.init();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    //getter
    public boolean isMovement() {
        return movement;
    }

    public boolean isWalking() {
        return walking;
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (active) {

                    if (current > 0) {
                        int j = 0;
                        long time = 10000000000L;
                        long[] replacement = new long[bufferLimit + 1];
                        for (int i = 0; i < current; i++) {
                            if (System.nanoTime() - stepTimes[i] < time) {
                                replacement[j] = stepTimes[i];
                                j++;
                            }
                        }
                        stepTimes = replacement;
                        if (j > 3)
                            walking = true;


                        current = j;
                    }
                    if (System.nanoTime() - lastUpdate < 10000000000L) {
                        movement = false;
                        lastUpdate = System.nanoTime();
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Log.i("MotionSensor","sleep interupted");
                    }
                }
            }
        }).start();
    }
}
