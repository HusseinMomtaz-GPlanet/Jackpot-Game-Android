package com.AppRocks.jackpot.webservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.activities.Main_;
import com.AppRocks.jackpot.activities.Question;
import com.AppRocks.jackpot.models.LevelController;

import org.json.JSONException;
import org.json.JSONObject;

public class ContinueGameTask extends AsyncTask<Void, Void, Integer> {

    private final static int ERROR = -1;
    private final static int NO_SAVE = 0;
    private final static int YES_SAVE = 1;
    final ProgressDialog ringProgressDialog;
    Question callerActivity;

    public ContinueGameTask(Question callerActivity) {
        super();
        this.callerActivity = callerActivity;
        ringProgressDialog = ProgressDialog.show(callerActivity, "Please wait ...", "Getting your game ...", true);
    }

    @Override
    protected void onPreExecute() {
        ringProgressDialog.setCancelable(false);
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        JSONObject resultJOSN = JackpotServicesClient.getInstance().ContinueGameTask();
        if (resultJOSN != null) {
            try {
                if (resultJOSN.has("jackpot_id")) {
                    JackpotApplication.JACKPOT_ID = "" + resultJOSN.getInt("jackpot_id");
                    int difficulty = resultJOSN.getInt("difficulty");
                    int level = resultJOSN.getInt("level_num");
                    int question = resultJOSN.getInt("question_num");
                    int score = resultJOSN.getInt("score");
                    //get jackpot details
                    JSONObject jackpotDetails=JackpotServicesClient.getInstance().getJackpotDetails(JackpotApplication.JACKPOT_ID,JackpotApplication.TOKEN_ID);
                    callerActivity.jackpotDetailsJson=jackpotDetails;
                    if (level == 1 && question == 0 && score == 0) {
                        callerActivity.isThisSavedGame = false;
                        callerActivity.level = new LevelController(difficulty, level, question, score, false);
                    } else {
                        callerActivity.isThisSavedGame = true;
                        callerActivity.level = new LevelController(difficulty, level, question, score, true);
                        // oneMoreQ is always true because he can save game only at cheese question
                    }
                    return YES_SAVE;
                } else if (resultJOSN.has("status")) {
                    return NO_SAVE;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ERROR;
    }

    @Override
    protected void onPostExecute(Integer result) {
        ringProgressDialog.dismiss();
        // if return saved game
        if (result == YES_SAVE) {
            // if it is actually saved game then continue from roulette screen
            if (callerActivity.isThisSavedGame) {
                callerActivity.showRouletteView();
                // now we can get help numbers depend on jackpot_id
                callerActivity.initJockersAndFloats();
                //update score txt
                callerActivity.txtScore.setText("" + callerActivity.level.totalScore);
            }
            // it isn't saved game but it is deleted game so start from beginning
            else {
                Toast.makeText(callerActivity, "You don't have saved game.", Toast.LENGTH_LONG).show();
                callerActivity.startActivity(new Intent(callerActivity, Main_.class));
            }
        }
        // no exit saved game
        else if (result == NO_SAVE) {
            Toast.makeText(callerActivity, "You don't have saved game.", Toast.LENGTH_LONG).show();
            callerActivity.startActivity(new Intent(callerActivity, Main_.class));
        }

        // something is wrong so can't retrieve the saved game
        else {
            Toast.makeText(callerActivity, "Sorry, can't retrieve your saved game.\nPlease, retry again", Toast.LENGTH_LONG).show();
            callerActivity.startActivity(new Intent(callerActivity, Main_.class));
        }
        callerActivity = null;
        super.onPostExecute(result);
    }

}
