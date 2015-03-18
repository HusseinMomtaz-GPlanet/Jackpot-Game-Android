package com.AppRocks.jackpot.webservice;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.activities.Jackpot;
import com.AppRocks.jackpot.activities.Question;

public class PlayJackpotTask extends AsyncTask<Void, Void, Void> {

    Activity callerActivity;


    public PlayJackpotTask(Activity callerActivity) {
        super();
        this.callerActivity = callerActivity;
    }


    @Override
    protected Void doInBackground(Void... arg0) {
        JackpotServicesClient.getInstance().playJackpot(
                JackpotApplication.JACKPOT_ID, JackpotApplication.TOKEN_ID);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (callerActivity instanceof Jackpot) {
            Jackpot activity = (Jackpot) callerActivity;
            Question.jackpotDetailsJson = activity.jackpotJsonDetails;
            Intent questionsIntent = new Intent(activity, Question.class);
            activity.startActivity(questionsIntent);
            activity.playMusicContinue = false;
        } else if (callerActivity instanceof Question) {
            Question activity = (Question) callerActivity;
            activity.playJackpotCallback();
            Toast.makeText(activity, "This is new game", Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(result);
    }
}
