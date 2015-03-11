package com.AppRocks.jackpot.webservice;

import android.os.AsyncTask;
import android.widget.Toast;

import com.AppRocks.jackpot.activities.Question;

import org.json.JSONException;
import org.json.JSONObject;

public class SaveGameTask extends AsyncTask<Integer, Void, Boolean> {

    public static final int SAVE_TASK = 1;
    public static final int DELETE_TASK = 0;
    public static final int END_TASK = -1;
    Question callerActivity;
    int choice;

    public SaveGameTask(Question callerActivity, int theChoice) {
        super();
        this.callerActivity = callerActivity;
        choice = theChoice;
    }

    public SaveGameTask(int theChoice) {
        choice = theChoice;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        JSONObject resultJOSN = JackpotServicesClient.getInstance().saveGame(params[0], params[1], params[2], params[3]);
        if (resultJOSN != null && resultJOSN.has("status")) {
            try {
                String status = resultJOSN.getString("status");
                if (status.equals("success"))
                    return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result)
            switch (choice) {
                case SAVE_TASK:
                    callerActivity.isThisSavedGame = true;
                    Toast.makeText(callerActivity.getApplicationContext(), "Yau saved game successfully", Toast.LENGTH_SHORT).show();
                    break;
                case DELETE_TASK:
                    callerActivity.isThisSavedGame = false;
                    Toast.makeText(callerActivity.getApplicationContext(), "You lost the saved game", Toast.LENGTH_SHORT).show();
                    break;
            }

        callerActivity = null;
        super.onPostExecute(result);
    }

}
