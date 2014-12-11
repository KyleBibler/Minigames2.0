package com.example.kyle.minigames;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

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
    protected UiLifecycleHelper uiHelper;

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
            urlFull = "http://" + url + "/rpi";
            stripActive = true;
        }

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected abstract void pauseGame();
    protected abstract void initializeGame();

    private void checkCheat(double ax, double ay) {
        if((Math.abs(ax) >= 2.0 || (ay >= 1.0 || ay <= -3.0)) && !cheating) {
            pauseGame();
            cheating = true;
            new AlertDialog.Builder(this)
                    .setTitle("Someone is Cheating")
                    .setMessage("Please level the device before you continue.")
                    .setNeutralButton("Continue (Restarts Game)", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            cheating = false;
                            restartGame();
                        }
                    }).show();


        }
    }

    protected void gameOver(String playerName, int score) {
        final int finalScore = score;

        new AlertDialog.Builder(this)
                .setTitle(playerName + " wins!")
                .setMessage(playerName + " won with a score of: " + finalScore)
                .setPositiveButton("Share Score", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        shareOnFacebook(finalScore);
                    }
                })
                .setNegativeButton("Restart Game", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //returns to game
                        restartGame();
                    }
                }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {

            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
        restartGame();
    }

    private void shareOnFacebook(int score) {
        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                .setLink("https://developers.facebook.com/apps/1510588855892720/dashboard/")
                .setCaption("I just won a game of " + game + " with a score of " + score)
                .build();
        uiHelper.trackPendingDialogCall(shareDialog.present());
    }

    protected void restartGame() {
        finish();
        startActivity(getIntent());
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        sm.unregisterListener(this, s);
    }
}


