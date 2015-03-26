package com.AppRocks.jackpot.webservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.activities.Login;
import com.AppRocks.jackpot.activities.Main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

@SuppressLint("NewApi")
public class GetAllJackpotsTask extends AsyncTask<Object, Void, Integer> {

    final String TAG_JACKPOTS_JSONARRAY = "jackpots";
    final int SUCCES = 1;
    final int INVALID_TOKEN = 0;
    Main callerActivity;
    private JSONArray jackpotsArrayJson;

    public GetAllJackpotsTask(Main a) {
        callerActivity = a;
    }

    @Override
    protected void onPreExecute() {
        if (TextUtils.isEmpty(JackpotApplication.TOKEN_ID)) {
            if(callerActivity!=null)
                JackpotApplication.TOKEN_ID = callerActivity.p.getString(JackpotApplication.PREF_USER_TOKEN);
        }
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Object... params) {
        callerActivity.jackpotsList = new ArrayList<HashMap<String, String>>();

        //jackpotsArrayJson = JackpotServicesClient.getInstance().getAllJackpots(JackpotApplication.TOKEN_ID);
        JSONObject getAllJackpotsJSON = JackpotServicesClient.getInstance().getAllJackpots(JackpotApplication.TOKEN_ID);


        if (getAllJackpotsJSON != null) {
            try {
                if (getAllJackpotsJSON.has(TAG_JACKPOTS_JSONARRAY)) {
                    jackpotsArrayJson = getAllJackpotsJSON.getJSONArray(TAG_JACKPOTS_JSONARRAY);
                    for (int i = 0; i < jackpotsArrayJson.length(); i++) {
                        JSONObject j = jackpotsArrayJson.getJSONObject(i);

                        if (j.getString(JackpotApplication.TAG_Active).contains("1")) {

                            HashMap<String, String> jackpot = new HashMap<String, String>();
                            jackpot.put(JackpotApplication.TAG_ID, j.getString(JackpotApplication.TAG_ID));
                            jackpot.put(JackpotApplication.TAG_IMAGE, j.getString(JackpotApplication.TAG_IMAGE));
                            jackpot.put(JackpotApplication.TAG_TITLE, j.getString(JackpotApplication.TAG_TITLE));
                            jackpot.put(JackpotApplication.TAG_FREE, j.getString(JackpotApplication.TAG_FREE));
                            jackpot.put(JackpotApplication.TAG_DIFFICULTY, j.getString(JackpotApplication.TAG_DIFFICULTY));
                            jackpot.put(JackpotApplication.TAG_HASWINNER, j.getString(JackpotApplication.TAG_HASWINNER));
                            jackpot.put(JackpotApplication.TAG_Bloked, j.getString(JackpotApplication.TAG_Bloked));

                            callerActivity.jackpotsList.add(jackpot);
                        }
                    }
                    return SUCCES;
                } else if (getAllJackpotsJSON.has("status")) {
                    return INVALID_TOKEN;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return -1;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == SUCCES) {
            // order list then show it
            orderJackpotsList();
            callerActivity.vp.setAdapter(callerActivity.pagesAdapter);
        } else if (result == INVALID_TOKEN) {
            Toast.makeText(callerActivity.getApplicationContext(), "Please, Login again.", Toast.LENGTH_SHORT).show();
            callerActivity.p.setString("", JackpotApplication.PREF_USER_TOKEN);
            Intent i = new Intent(callerActivity, Login.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            callerActivity.startActivity(i);
        } else {
            Toast.makeText(callerActivity.getApplicationContext(), "Sorry, something is wrong .. please try again.", Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(result);
    }

    private void orderJackpotsList() {
        //Sorting
        Collections.sort(callerActivity.jackpotsList, new Comparator<HashMap<String, String>>() {

            @Override
            public int compare(HashMap<String, String> map1,
                               HashMap<String, String> map2) {
                return map1.get(JackpotApplication.TAG_DIFFICULTY).compareTo(map2.get(JackpotApplication.TAG_DIFFICULTY));
            }
        });
    }

}
