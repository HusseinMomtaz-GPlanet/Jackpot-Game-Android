package com.AppRocks.jackpot.webservice;

import android.os.AsyncTask;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.activities.FaceBookInvitations;

import org.json.JSONObject;

public class CheckToSendGiftJokerTask extends AsyncTask<Integer, Void, JSONObject> {

    FaceBookInvitations callerActivity;

    public CheckToSendGiftJokerTask(FaceBookInvitations callerActivity) {
        super();
        this.callerActivity = callerActivity;
    }

    @Override
    protected JSONObject doInBackground(Integer... params) {
        JSONObject resultJOSN = JackpotServicesClient.getInstance().checkIfCanAddGiftJoker(JackpotApplication.TOKEN_ID);
        return resultJOSN;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        callerActivity.giftJokerAvailabilityReturned(result);
        callerActivity = null;
        super.onPostExecute(result);
    }

}
