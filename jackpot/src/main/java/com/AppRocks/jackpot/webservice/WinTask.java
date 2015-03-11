package com.AppRocks.jackpot.webservice;

import android.os.AsyncTask;

public class WinTask extends AsyncTask<String, Void, Void> {

    private boolean isThisWasSavedGame;


    public WinTask(boolean isThisWasSavedGame) {
        this.isThisWasSavedGame = isThisWasSavedGame;
    }

    @Override
    protected Void doInBackground(String... params) {
        JackpotServicesClient.getInstance().win(params[0], params[1]);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // then delete saved game
        if (isThisWasSavedGame) {
            new SaveGameTask(SaveGameTask.END_TASK).execute(0, 0, 0, 0);
        }
        super.onPostExecute(result);
    }
}
