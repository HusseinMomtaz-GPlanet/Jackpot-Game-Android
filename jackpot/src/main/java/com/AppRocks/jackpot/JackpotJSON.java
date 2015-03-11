package com.AppRocks.jackpot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JackpotJSON {

    String jackpotJSONArray = "[jackpots:"
            + "{\"imageURL\":\"http://www.learn2crack.com/wp-content/uploads/2014/04/node-cover-720x340.png,"
            + "\"difficulty\"}]";
    int imageURL;
    int difficulty;
    JSONArray jackpotsArray;
    JSONObject jsonObj;
    JSONObject jackpotJSON;

    public String getURL(int n) {
        try {
            jsonObj = new JSONObject(jackpotJSONArray);
            jackpotsArray = jsonObj.getJSONArray("jackpots");
            jackpotJSON = jackpotsArray.getJSONObject(n);
            return jackpotJSON.getString("imageURL");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


}
