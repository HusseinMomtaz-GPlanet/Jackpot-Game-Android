package com.AppRocks.jackpot.webservice;

import android.os.AsyncTask;

public class AddGiftJockersTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        JackpotServicesClient.getInstance().addGiftJocker();
        return null;
    }

}
