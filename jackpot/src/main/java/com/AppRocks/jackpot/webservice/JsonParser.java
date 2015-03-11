package com.AppRocks.jackpot.webservice;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class JsonParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    static JsonParser instance;
    private JSONArray jArr;

    // constructor
    private JsonParser() {

    }

    public static JsonParser getInstance() {
        if (instance == null)
            instance = new JsonParser();
        return instance;
    }

    public JSONObject getJSONFromUrl(String request_url, String postMessage, Hashtable<String, String> header) throws IOException, JSONException {

        // Making HTTP request
        try {
            json = "";
            // replace spaces in the url with %
            String url = request_url;
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type",
                    "application/json");

            if (postMessage != null)
                httpPost.setEntity(new ByteArrayEntity(
                        postMessage.toString().getBytes("UTF8")));
            if (header != null && header.isEmpty() != true)
                httpPost.setHeader(header.keys().nextElement(), header.get(header.keys().nextElement()));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            String js = sb.substring(sb.indexOf("{"));
            System.out.println("response :" + js);
            json = js.toString();
            jObj = new JSONObject(json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
            return null;
        }
        // return JSON String//
        return jObj;

    }

    public String getHTMLFromUrl(String request_url, String postMessage, Hashtable<String, String> header) throws IOException, JSONException {

        // Making HTTP request
        try {
            json = "";
            // replace spaces in the url with %
            String url = request_url;
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type",
                    "application/json");

            if (postMessage != null)
                httpPost.setEntity(new ByteArrayEntity(
                        postMessage.toString().getBytes("UTF8")));
            if (header != null && header.isEmpty() != true)
                httpPost.setHeader(header.keys().nextElement(), header.get(header.keys().nextElement()));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
            return null;
        }
        // return JSON String//
        return json;

    }

    public JSONArray getJSONArrayFromUrl(String request_url, String postMessage, Hashtable<String, String> header) throws IOException, JSONException {

        // Making HTTP request
        try {
            json = "";
            // replace spaces in the url with %
            String url = request_url;
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type",
                    "application/json");

            if (postMessage != null)
                httpPost.setEntity(new ByteArrayEntity(
                        postMessage.toString().getBytes("UTF8")));
            if (header != null && header.isEmpty() != true)
                httpPost.setHeader(header.keys().nextElement(), header.get(header.keys().nextElement()));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();

            json = sb.toString();
            jArr = new JSONArray(json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
            return null;
        }
        // return JSON String//
        return jArr;

    }

    public JSONObject loadJSONFromAsset(Context context, String fName) {
        String json = null;
        JSONObject json_file = null;
        try {

            InputStream is = context.getAssets().open(fName);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");
            json_file = new JSONObject(json);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json_file;

    }

    public Boolean saveWidgetNews(Activity activity, JSONObject news) {
        try {
            FileOutputStream fos = activity.openFileOutput("aw_news.json",
                    Activity.MODE_PRIVATE);
            fos.write(news.toString().getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    public JSONObject loadJSONFromInternalStorage(Context context, String fName) {
        String json = null;
        JSONObject json_file = null;
        FileInputStream fis = null;
        try {

            fis = context.openFileInput(fName);

            int size = fis.available();

            byte[] buffer = new byte[size];

            fis.read(buffer);

            fis.close();

            json = new String(buffer, "UTF-8");
            json_file = new JSONObject(json);

        } catch (Exception ex) {
            ex.printStackTrace();
            return new JSONObject();
        }
        return json_file;

    }
}