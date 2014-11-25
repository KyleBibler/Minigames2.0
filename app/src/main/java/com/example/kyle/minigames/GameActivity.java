package com.example.kyle.minigames;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;

/**
 * Created by Kyle on 11/25/2014.
 */
public abstract class GameActivity extends Activity implements SensorEventListener {

    protected boolean cheating, stripActive;
    protected SharedPreferences sharedPreferences;
    protected String url, urlFull;
    protected SensorManager sm;
    protected Sensor s;
    protected String game;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String IP = "ipKey";
    private UiLifecycleHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        cheating = false;
        stripActive = false;
        url = "";
        urlFull = "";
        game = "";
        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);


        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(IP))
        {
            url = sharedPreferences.getString(IP, "");
            urlFull = "http//:" + url + "/rpi";
            stripActive = true;
        }

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void checkCheat(double ax, double ay) {
        if((Math.abs(ax) >= 2.0 || (ay >= 1.0 || ay <= -3.0)) && !cheating) {
            cheating = true;
            new AlertDialog.Builder(this)
                    .setTitle("Someone is Cheating")
                    .setMessage("Please level the device before you continue.")
                    .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            cheating = false;
                        }
                    }).show();
        }
    }

    protected void gameOver(String playerName, final int score) {
        new AlertDialog.Builder(this)
                .setTitle(playerName + " wins!")
                .setMessage(playerName + " won with a score of: " + score)
                .setPositiveButton("Share Score", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        shareOnFacebook(score);
                    }
                })
                .setNegativeButton("Return to Game", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //returns to game
                    }
                }).show();
    }

    private void shareOnFacebook(int score) {
        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                .setLink("https://developers.facebook.com/apps/1507559206197809/dashboard/")
                .setCaption("I just won a game of " + game + " with a score of " + score)
                .build();
        uiHelper.trackPendingDialogCall(shareDialog.present());
    }

    protected abstract void restartGame();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        sm.unregisterListener(this, s);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            checkCheat(event.values[0], event.values[1]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}


