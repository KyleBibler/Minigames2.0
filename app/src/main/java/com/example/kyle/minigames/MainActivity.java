package com.example.kyle.minigames;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.pm.Signature;
import android.view.View;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;


public class MainActivity extends Activity {

    private UiLifecycleHelper uiHelper;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        try{
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.example.kyle.minigames", PackageManager.GET_SIGNATURES);
//
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);


//        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
//                .setLink("https://developers.facebook.com/apps/1507559206197809/dashboard/")
//                .build();
//        uiHelper.trackPendingDialogCall(shareDialog.present());


    }

    public void onTugOfWarClicked(View view) {
        Intent i = new Intent(this, TugOfWar.class);
        startActivity(i);
    }

    public void onClearCutClicked(View view) {
        Intent i = new Intent(this, ClearCutChallenge.class);
        startActivity(i);
    }

    public void onPrefsClicked(View view) {
        Intent i = new Intent(this, Preferences.class);
        startActivity(i);
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            onPrefsClicked(this.getCurrentFocus());
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        session = Session.getActiveSession();
        if (session != null) {
            session.closeAndClearTokenInformation();
        }
    }

    private void logHash() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.kyle.minigames", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
