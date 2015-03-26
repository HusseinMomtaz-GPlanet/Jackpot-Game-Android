package com.AppRocks.jackpot.webservice;

import android.os.AsyncTask;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.activities.Question;
import com.AppRocks.jackpot.models.LevelController;

import org.json.JSONException;
import org.json.JSONObject;

public class AnswerTask extends AsyncTask<String, Void, Integer> {

    final int RIGHT_ANSWER = 1;
    final int WRONG_ANSWER = 0;
    final int WINNER = 2;
    final int TIME_IS_UP = 3;
    final int JOCKER_USED = 4;
    Question callerActivity;
    boolean isJockerUsed;

    public AnswerTask(Question callerActivity) {
        super();
        this.callerActivity = callerActivity;
    }

    public AnswerTask(Question callerActivity, boolean isJocker) {
        super();
        this.callerActivity = callerActivity;
        isJockerUsed = isJocker;
    }

    @Override
    protected Integer doInBackground(String... params) {
        // params 0 >> the click answer
        // params 1 >> the floats used
        // params 2 >> the jocker used

        int isExtra = 0;
        if (callerActivity.level.oneMoreQuestion)
            isExtra = 1;

        JSONObject answerJSON = JackpotServicesClient.getInstance().answer(isExtra,
                callerActivity.question.getId(), params[0], "" + LevelController.timer,
                JackpotApplication.JACKPOT_ID, params[1], params[2], callerActivity.isLast, "" + callerActivity.level.level);

        if (answerJSON != null && answerJSON.has("status")) {
            try {
                // wright answer
                if (answerJSON.getInt("is_correct") == 1) {
                    callerActivity.level.newScore = answerJSON.getInt("question_score");
                    //--- as been modified to correct total score
                    //callerActivity.level.totalScore = answerJSON.getInt("new_score");
                    // the winner
                    if (answerJSON.has("has_won")) {
                        return WINNER;
                    } else if (isJockerUsed) {
                        return JOCKER_USED;
                    }
                    return RIGHT_ANSWER;
                }

                //wrong answer
                else {
                    if (JackpotApplication.numberOfFloatyUsed == 0)
                        callerActivity.question.setAnswer(answerJSON.getString("answer"));
                    // time is up screen
                    if (params[0].equals("Jackpot time is up")) {
                        return TIME_IS_UP;
                    }
                    return WRONG_ANSWER;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return -1;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == RIGHT_ANSWER) {
            callerActivity.rightAnswer();
        } else if (result == JOCKER_USED) {
            callerActivity.jockerUpdateUIAfterGetAnswer();
        } else if (result == WRONG_ANSWER) {
            callerActivity.wrongAnswer();
        } else if (result == WINNER) {
            callerActivity.wineer();
        } else if (result == TIME_IS_UP) {
            callerActivity.timer.schedule(callerActivity.timeIsUpTask, 1500);
        } else {
            Toast.makeText(callerActivity.getApplicationContext(), "Sorry, something is wrong .. please try again.", Toast.LENGTH_SHORT).show();
            callerActivity.finish();
        }
        super.onPostExecute(result);
    }

}
