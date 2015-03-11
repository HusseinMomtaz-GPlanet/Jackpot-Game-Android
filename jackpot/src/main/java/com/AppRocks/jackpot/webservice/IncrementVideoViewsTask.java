package com.AppRocks.jackpot.webservice;

import android.os.AsyncTask;

import com.AppRocks.jackpot.JackpotApplication;

public class IncrementVideoViewsTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub
        JackpotServicesClient.getInstance()
                .incrementVideoViews(JackpotApplication.JACKPOT_ID, JackpotApplication.TOKEN_ID);
        return null;
    }

}
