package com.example.kyle.minigames;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Alex on 11/2/2014.
 */
public final class ApiTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
        URL url = null;
        try {
            Log.d("[API]", "URL is: " + params[0]);
            url = new URL(params[0]);
        } catch (MalformedURLException ex) {
            Log.d("[API]", ex.getMessage());
            return null;
        }

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(params[0]);
            StringEntity se = new StringEntity(params[1]);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            httpClient.execute(httpPost);
        } catch (Exception ex) {
            Log.d("[API]", ex.getMessage());
        }

//        HttpURLConnection request = null;
//        try {
//            request = (HttpURLConnection) url.openConnection();
//            request.setRequestMethod("POST");
//            request.setDoOutput(true);
//            request.setChunkedStreamingMode(0); // size of POST body is unknown
//            request.setRequestProperty("Content-Type", "application/json");
//
//            OutputStream out = new BufferedOutputStream(request.getOutputStream());
//            out.write(params[1].getBytes("UTF-8"));
//            out.close();
//
//            request.connect();
//            Log.d("[API]", "Response code is: " + request.getResponseCode());
//        } catch (IOException ex) {
//            Log.d("[API]", ex.getMessage());
//        } finally {
//            request.disconnect();
//        }
        return null;
    }
}