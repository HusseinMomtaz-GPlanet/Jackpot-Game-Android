package com.AppRocks.jackpot.webservice;

import android.os.AsyncTask;
import android.util.Log;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.activities.Question;

import org.json.JSONException;
import org.json.JSONObject;

public class GetJockersAndFloatsTask extends AsyncTask<Void, Void, Boolean> {

    Question callerActivity;
    private JSONObject jockersAndFloatsJSON;


    public GetJockersAndFloatsTask(Question callerActivity) {
        super();
        this.callerActivity = callerActivity;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        jockersAndFloatsJSON = JackpotServicesClient.getInstance().GetJockersAndFloats();
        if (jockersAndFloatsJSON != null) {
            try {
                if (!jockersAndFloatsJSON.isNull(JackpotApplication.TAG_JOCKERS_NUMBER)) {
                    JackpotApplication.jokerHas = jockersAndFloatsJSON.getInt(JackpotApplication.TAG_JOCKERS_NUMBER);
                    JackpotApplication.livesHas = jockersAndFloatsJSON.getInt(JackpotApplication.TAG_FLOATS_NUMBERS);
                    JackpotApplication.jokerGifts = jockersAndFloatsJSON.getInt(JackpotApplication.TAG_JOCKERS_GIFT);
                    //to make jokerhas the same value as returned from question details
                    JackpotApplication.jokerHas += JackpotApplication.jokerGifts;
                    Log.d("elsawaf-" + getClass().getSimpleName(), "#jocker has= " + JackpotApplication.jokerHas
                            + "\n#gift jocker= " + JackpotApplication.jokerGifts);
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        /*if (result && callerActivity instanceof Question){
            Question q = (Question) callerActivity;*/
        if (result) {
            callerActivity.updateJocker();
            callerActivity.updateFloaty();
        }
        super.onPostExecute(result);
    }

}
