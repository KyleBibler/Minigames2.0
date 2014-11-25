package com.example.kyle.minigames;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.facebook.Session;


public class Preferences extends Activity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String IP = "ipKey";
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preferences, menu);
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

    public void onSetButtonClicked(View view) {
        String url = String.valueOf(((EditText) findViewById(R.id.setIp)).getText());
        if(!url.isEmpty()) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(IP, url);
            editor.commit();
        }

    }

    public void onLogOutClicked(View view) {
        //callFacebookLogout(this);
        new AlertDialog.Builder(this)
                .setTitle("To Log Out of Facebook")
                .setMessage("Log out through the official Facebook Application")
                .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
        }).show();
    }

    //TODO ACTUALLY LOG OUT OF FACEBOOK
    public static void callFacebookLogout(Context context) {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                //clear your preferences if saved
            }
        } else {

            session = new Session(context);
            Session.setActiveSession(session);

            session.closeAndClearTokenInformation();
            //clear your preferences if saved

        }

    }
}
