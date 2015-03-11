package com.AppRocks.jackpot.webservice;

import android.os.AsyncTask;

import com.AppRocks.jackpot.activities.Jackpot;

public class GetTermsTask extends AsyncTask<Integer, Void, String> {

    Jackpot callerActivity;

    public GetTermsTask(Jackpot callerActivity) {
        super();
        this.callerActivity = callerActivity;
    }

    @Override
    protected String doInBackground(Integer... params) {
        String resultJOSN = JackpotServicesClient.getInstance().getTerms();
        return resultJOSN;
    }

    @Override
    protected void onPostExecute(String result) {
        callerActivity.onGetTermsTaskFinish(result);
        callerActivity = null;
        super.onPostExecute(result);
    }

}
