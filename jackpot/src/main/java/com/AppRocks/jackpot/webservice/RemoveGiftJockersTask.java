package com.AppRocks.jackpot.webservice;

import android.os.AsyncTask;

public class RemoveGiftJockersTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        JackpotServicesClient.getInstance().removeGiftJocker();
        return null;
    }

}
