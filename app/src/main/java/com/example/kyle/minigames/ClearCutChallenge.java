package com.example.kyle.minigames;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class ClearCutChallenge extends GameActivity {

    private int clicks;
    private int player1Target;
    private int player2Target;
    //private ArrayList<Integer> player1LogIndices;
    //private ArrayList<Integer> player2LogIndices;
    private int player1Log;
    private int player2Log;
    private int player1Score;
    private int player2Score;

    private Light player1TargetLight;
    private Light player2TargetLight;
    private Light player1LogLight;
    private Light player2LogLight;

    private int p1TargetIndex = 0;
    private int p2TargetIndex = 1;
    private int p1LogIndex = 2;
    private int p2LogIndex = 3;

    private LightModel lights;
    private ArrayList<Light> lightValues;

    private TextView score1;
    private TextView score2;
    Button player1Button;
    Button player2Button;

    private boolean gameStopped, player1Clicked, player2Clicked;

    private LightStrip strip;
    private BlockingQueue<LightModel> queue;

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_cut_challenge);
        game = "Clear Cut Challenge";

        // call updateLogs every 1/4 second
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(!gameStopped) {
                    updateLogs();
                    handler.postDelayed(this, 350);
                }
                else {
                    handler.removeCallbacks(this);
                }
            }
        };

        queue = new ArrayBlockingQueue<LightModel>(16);
        strip = (LightStrip) findViewById(R.id.strip);
        strip.createThread(queue);


//        handler.postDelayed(runnable, 3000); // 3 seconds to ready,set,go
        initializeGame();
        // TODO ready, set, go message
        //put handler.postDelayed() in restart game

    }

    @Override
    protected void pauseGame() {
        queue.clear();
        handler.removeCallbacks(runnable);
    }

    protected void initializeGame() {
        queue.clear();
        gameStopped = false;
        strip.createThread(queue);

        player1Clicked = false;
        player2Clicked = false;

        player1Target = 4;
        player2Target = 29;
        player1Score = 0;
        player2Score = 0;

        player1Log = 16;
        player2Log = 17;

        player1TargetLight = new Light(player1Target, 255, 0, 0, 0.7);
        player2TargetLight = new Light(player2Target, 255, 0, 0, 0.7);

        player1LogLight = new Light(player1Log, 92, 51, 23, 0.7);
        player2LogLight = new Light(player2Log, 92, 51, 23, 0.7);

        score1 = (TextView) findViewById(R.id.player1ScoreView);
        score2 = (TextView) findViewById(R.id.player2ScoreView);

        player1Button = (Button) findViewById(R.id.player1Button);
        player2Button = (Button) findViewById(R.id.player2Button);

        lightValues = new ArrayList<Light>();
        lightValues.add(player1TargetLight);
        lightValues.add(player1LogLight);
        lightValues.add(player2LogLight);
        lightValues.add(player2TargetLight);

        p1TargetIndex = lightValues.indexOf(player1TargetLight);
        p2TargetIndex = lightValues.indexOf(player2TargetLight);
        p1LogIndex = lightValues.indexOf(player1LogLight);
        p2LogIndex = lightValues.indexOf(player2LogLight);

        lights = new LightModel(lightValues, false);
        if(stripActive) {
            startGameStrip(urlFull);
        }


        queue.add(lights);


        new AlertDialog.Builder(this)
                .setTitle("Prepare yourselves!")
                .setMessage("Press the button to start the game (only when both players are ready).")
                .setNeutralButton("Start Game in 3...", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        handler.postDelayed(runnable, 3000);
                    }
                }).show();

        //Internal lights
    }

    public void startGameStrip(String url) {
        stripActive = true;
        lights = new LightModel(lightValues, false);
        new ApiTask().execute(url, lights.serialize());
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }


    public void onPlayer1Clicked(View view) {
        int score = 12 - Math.abs(player1Target - player1Log);
        player1Score = score;
        score1.setText("Score: " + score);
        score1.setEnabled(false);
        view.setEnabled(false);
        player1Clicked = true;

    }

    public void onPlayer2Clicked(View view) {
        int score = 12 - Math.abs(player2Target - player2Log);
        player2Score = score;
        score2.setText("Score: " + score);
        score2.setEnabled(false);
        view.setEnabled(false);
        player2Clicked = true;
    }

    private void updateLogs() {
        if (player1Log > 1) {
            if(player1Log == player1Target) {
                lightValues.get(p1TargetIndex).setColor(0, 255, 0);
                lightValues.remove(p1TargetIndex);
                lightValues.remove(p1LogIndex);
                lightValues.add(p1TargetIndex, player1LogLight);
                lightValues.add(p1LogIndex, player1TargetLight);
                int swap = p1LogIndex;
                p1LogIndex = p1TargetIndex;
                p1TargetIndex = swap;
            }
            // TODO change target back to red from green
            player1Log--;
            lightValues.get(p1LogIndex).setId(player1Log);
        }
        if (player2Log < 32) {
            if(player2Log == player2Target) {
                lightValues.get(p2TargetIndex).setColor(0, 255, 0);
                lightValues.remove(p2TargetIndex);
                lightValues.remove(p2LogIndex);
                lightValues.add(p2LogIndex, player2TargetLight);
                lightValues.add(p2TargetIndex, player2LogLight);

                int swap = p2LogIndex;
                p2LogIndex = p2TargetIndex;
                p2TargetIndex = swap;
            }
            // TODO change target back to red from green
            player2Log++;
            lightValues.get(p2LogIndex).setId(player2Log);
        }
        lights = new LightModel(lightValues, false);

        if(stripActive) {
            new ApiTask().execute(urlFull, lights.serialize());
        }
        if(player1Log <= 1 && !player1Clicked) {
            player1Score = 0;
            player1Clicked = true;
        }
        if (player2Log >= 32 && !player2Clicked) {
            player2Score = 0;
            player2Clicked = true;
        }


        queue.add(lights);
        if (player1Clicked && player2Clicked && !gameStopped) { // both players have attempted to chop the log
            // TODO game is over
            handler.removeCallbacks(runnable);
            gameStopped = true;
            String winner = (player1Score > player2Score) ? "Player 1" : "Player 2";
            int score = (player1Score > player2Score) ? player1Score : player2Score;
            gameOver(winner, score);
            //restartGame();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tug_of_war, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
