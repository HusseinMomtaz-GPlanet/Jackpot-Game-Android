package com.AppRocks.jackpot.webservice;

import android.os.AsyncTask;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.activities.Question;

import org.json.JSONException;
import org.json.JSONObject;

public class HelpTask extends AsyncTask<String, Void, Integer> {

    final int JOKER_USED = 1;
    final int FLOAT_USED = 2;
    Question callerActivity;

    public HelpTask(Question callerActivity) {
        super();
        this.callerActivity = callerActivity;
    }

    @Override
    protected Integer doInBackground(String... params) {
        // params 0 >> the floats used
        // params 1 >> the jocker used

        JSONObject helpJSON = JackpotServicesClient.getInstance().help(callerActivity.question.getId(),
                JackpotApplication.JACKPOT_ID, params[0], params[1]);

        if (helpJSON != null && helpJSON.has("status")) {
            try {
                // return right answer
                if (helpJSON.get("status").equals("success")) {
                    callerActivity.question.setAnswer(helpJSON.getString("answer"));
                    if (params[0].equals("1"))
                        return FLOAT_USED;
                    return JOKER_USED;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return -1;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == FLOAT_USED) {
            Toast.makeText(callerActivity, "Float User", Toast.LENGTH_LONG).show();
            callerActivity.deleteRandomWrongAnswer();
        } else if (result == JOKER_USED) {
            Toast.makeText(callerActivity, "Joker User", Toast.LENGTH_LONG).show();
            new AnswerTask(callerActivity, true).execute(callerActivity.question.getAnswer(), "" + JackpotApplication.numberOfFloatyUsed,
                    "1");
        } else {
            Toast.makeText(callerActivity.getApplicationContext(), "Sorry, something is wrong ...", Toast.LENGTH_SHORT).show();
            callerActivity.finish();
        }
        super.onPostExecute(result);
    }

}
