package com.AppRocks.jackpot.webservice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.R;
import com.AppRocks.jackpot.activities.Jackpot;
import com.AppRocks.jackpot.activities.Main;
import com.AppRocks.jackpot.activities.WinnerScreen;
import com.AppRocks.jackpot.activities.youtube.FullscreenDemoActivity2;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GetJackpotDetailsForWinnerTask extends AsyncTask<Object, Void, Void> {

    HashMap<String, String> jackpotDetails;
    Main callerActivity;
    private JSONObject jackpotDetailsJSON;
    private String imgFly;
    private String img;
    private String videoUrl;
    private HashMap<String, String> winnerDetails;

    public GetJackpotDetailsForWinnerTask(Main a) {
        callerActivity = a;
    }

    //"won" is null or has winner info
    @Override
    protected Void doInBackground(Object... params) {
        jackpotDetailsJSON = JackpotServicesClient.getInstance()
                .getJackpotDetails(JackpotApplication.JACKPOT_ID, JackpotApplication.TOKEN_ID);

        if (jackpotDetailsJSON != null) {
            try {

                if (!jackpotDetailsJSON.isNull(JackpotApplication.TAG_WON)) {
                    //get winner info
                    JackpotApplication.isWined = true;
                    JSONObject wonJson = jackpotDetailsJSON.getJSONObject(JackpotApplication.TAG_WON);
                    winnerDetails = new HashMap<String, String>();
                    winnerDetails.put(JackpotApplication.TAG_COMPANY,
                            jackpotDetailsJSON
                                    .getString(JackpotApplication.TAG_COMPANY));
                    winnerDetails.put(JackpotApplication.TAG_BRAND,
                            jackpotDetailsJSON
                                    .getString(JackpotApplication.TAG_BRAND));
                    winnerDetails.put(JackpotApplication.TAG_FLY,
                            jackpotDetailsJSON
                                    .getString(JackpotApplication.TAG_FLY));
                    winnerDetails.put(JackpotApplication.TAG_IMAGE,
                            jackpotDetailsJSON
                                    .getString(JackpotApplication.TAG_IMAGE));

                    JSONObject userWonJson = wonJson.getJSONObject(JackpotApplication.TAG_WON_USER);
                    winnerDetails.put(JackpotApplication.TAG_WON_USER_FIRST_NAME, userWonJson.getString(JackpotApplication.TAG_WON_USER_FIRST_NAME));
                    winnerDetails.put(JackpotApplication.TAG_WON_USER_LAST_NAME, userWonJson.getString(JackpotApplication.TAG_WON_USER_LAST_NAME));
                    winnerDetails.put(JackpotApplication.TAG_WON_USER_Nick_NAME, userWonJson.getString(JackpotApplication.TAG_WON_USER_Nick_NAME));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (jackpotDetailsJSON != null) {
            if (JackpotApplication.isWined) {
                // start activity winner
                Intent i = new Intent(callerActivity.getApplicationContext(), WinnerScreen.class);

                i.putExtra("winner", winnerDetails);
                callerActivity.startActivity(i);
            }
        } else {
            // json response is null
            Toast.makeText(callerActivity.getApplicationContext(), "No Data: Please, try again.", Toast.LENGTH_LONG).show();
        }
        super.onPostExecute(result);
    }

}
