package com.example.kyle.minigames;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class TugOfWar extends GameActivity {

    private int bluepoint, clicks;
    private LightModel lights;
    private String url;
    private String urlFull;
    private ArrayList<Light> lightValues;
    private TextView score;
    private ArrayList<Light> allBlue;
    private LightModel blueLights;

    private LightStrip strip;
    private LightStrip.LightStripThread lightStripThread;
    private BlockingQueue<LightModel> queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tug_of_war);
        game = "Tug of War";

        bluepoint = 17;
        clicks = 0;
        score = (TextView) findViewById(R.id.scoreView);




        queue = new ArrayBlockingQueue<LightModel>(16);
        strip = (LightStrip) findViewById(R.id.strip);
        strip.createThread(queue);

        restartGame();
    }

    @Override
    protected void restartGame() {
        queue.clear();
        bluepoint = 17;
        clicks = 0;
        score.setText("Red: " + (bluepoint - 1) + "\t\tBlue: " + (33 - bluepoint));
        lightValues = new ArrayList<Light>();
        lightValues.add(new Light(1, 255, 0, 0, 0.7));
        lightValues.add(new Light(bluepoint, 0, 0, 255, 0.7));
        lights = new LightModel(lightValues, true);

        allBlue = new ArrayList<Light>(1);
        allBlue.add(new Light(1, 0, 0, 255, 0.7));
        blueLights = new LightModel(allBlue, true);

        if(stripActive) {
            startGameStrip(urlFull);
        }
        //TODO LIGHT STRIP QUEUE GOES HERE
        queue.add(lights);
    }

    public void startGameStrip(String url) {
        new ApiTask().execute(url, lights.serialize());
    }

    public void onPlayer1Clicked(View view) {


        if (bluepoint <= 1) {
            //Blue wins
            score.setText("BLUE WINS");
            gameOver("Player 1", 32);
            restartGame();
            return;
        } else {
            bluepoint--;
        }
        playerBtnClicked();
    }

    public void onPlayer2Clicked(View view) {

        if (bluepoint > 32) {
            //Red wins
            score.setText("RED WINS");
            gameOver("Player 2", 32);
            restartGame();
            return;
        } else {
            bluepoint++;
        }
        playerBtnClicked();

    }

    private void playerBtnClicked() {
        // if-else should fix issue where one red
        // light stays on when blue is about to win
        if (bluepoint == 1) {
            lights = blueLights;
        } else {
            lightValues.get(1).setId(bluepoint);
            lights = new LightModel(lightValues, true);
        }
        score.setText("Red: " + (bluepoint - 1) + "\t\tBlue: " + (33 - bluepoint));
        if(stripActive) {
            clicks++;
            if (clicks >= 3) {
                new ApiTask().execute(urlFull, lights.serialize());
                clicks = 0;
            }
        }
        //TODO ADD LIGHTS TO QUEUE
        queue.add(lights);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
