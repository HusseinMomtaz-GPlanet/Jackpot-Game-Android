package com.AppRocks.jackpot.webservice;

import android.os.AsyncTask;

import com.AppRocks.jackpot.activities.Question;

public class WinTask extends AsyncTask<String, Void, Void> {

    private boolean isThisWasSavedGame;
    Question questionActivity;

    public WinTask(Question qActivity,boolean isThisWasSavedGame) {
        this.isThisWasSavedGame = isThisWasSavedGame;
        questionActivity=qActivity;
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
        questionActivity.onWinnerDataSaved();
        super.onPostExecute(result);
    }
}
