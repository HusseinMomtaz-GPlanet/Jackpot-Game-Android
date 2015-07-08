package com.AppRocks.jackpot.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.R;
import com.AppRocks.jackpot.util.ConnectionDetector;
import com.AppRocks.jackpot.webservice.JackpotServicesClient;
import com.AppRocks.jackpot.webservice.ScoreboardAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Scoreboard extends Activity {

    private ListView lv;
    private ArrayList<HashMap<String, String>> scoreData;
    private TextView youScoreTxt;
    private TextView yourPosition;
    private int youScore;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        lv = (ListView) findViewById(R.id.listViewScores);
        youScoreTxt = (TextView) findViewById(R.id.youScoreTxt);
        yourPosition = (TextView) findViewById(R.id.your_score);

        ConnectionDetector cd = new ConnectionDetector(Scoreboard.this);
        if (cd.isConnectingToInternet()) {
            new GetTopScoresTask().execute();
        }

    }

    class GetTopScoresTask extends AsyncTask<Void, Void, Boolean> {

        private JSONArray topScoreArrayJson;

        @Override
        protected Boolean doInBackground(Void... arg0) {

            scoreData = new ArrayList<HashMap<String, String>>();
            JSONObject getTopJSON = JackpotServicesClient.getInstance().getTopScores(JackpotApplication.TOKEN_ID);


            if (getTopJSON != null) {
                try {
                    topScoreArrayJson = getTopJSON.getJSONArray(JackpotApplication.TAG_TOP_FIVE);
                    for (int i = 0; i < topScoreArrayJson.length(); i++) {
                        JSONObject j = topScoreArrayJson.getJSONObject(i);
                        HashMap<String, String> scoreUser = new HashMap<String, String>();
                        scoreUser.put(JackpotApplication.TAG_Mail, j.getString(JackpotApplication.TAG_Mail));
                        scoreUser.put(JackpotApplication.TAG_NICKNAME, j.getString(JackpotApplication.TAG_NICKNAME));
                        scoreUser.put(JackpotApplication.TAG_TOTAL_SCORE, j.getString(JackpotApplication.TAG_TOTAL_SCORE));

                        scoreData.add(scoreUser);
                    }

                    JSONObject youScoreJSON = getTopJSON.getJSONObject(JackpotApplication.TAG_YOU_SCORE);
                    youScore = youScoreJSON.getInt(JackpotApplication.TAG_TOTAL_SCORE);
                    position=getTopJSON.getInt("position");
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                ScoreboardAdapter adapter = new ScoreboardAdapter(Scoreboard.this, scoreData);
                lv.setAdapter(adapter);
                youScoreTxt.setText("" + youScore);
                yourPosition.setText(""+position);
            }

            super.onPostExecute(result);
        }

    }

}
