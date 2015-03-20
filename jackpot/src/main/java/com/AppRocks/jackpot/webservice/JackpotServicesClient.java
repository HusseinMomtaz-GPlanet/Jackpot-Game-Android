package com.AppRocks.jackpot.webservice;

import android.content.Context;
import android.util.Log;

import com.AppRocks.jackpot.JackpotApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Hashtable;

public class JackpotServicesClient {

    public static String baseURL = JackpotApplication.BASE_URL_WEB_SERVICES;
    public static JackpotServicesClient instance = null;
    static Context myContext;
    static String errorMessage = "Someting went wrong when connecting to server";

    public static JackpotServicesClient getInstance() {
        if (instance == null) {
            instance = new JackpotServicesClient();
            //myContext = context;
        }
        return instance;
    }

    public JSONObject register(String username, String firstName,
                               String lastName, String nick, String country, String email,
                               String password) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("username", username);
            msg.put("first_name", firstName);
            msg.put("last_name", lastName);
            msg.put("nickname", nick);
            msg.put("country", country);
            msg.put("email", email);
            msg.put("password", password);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "register", msg.toString(), null);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject login(String email, String password) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("email", email);
            msg.put("password", password);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "clogin", msg.toString(), null);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject registerFree(String nick) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("nickname", nick);
            msg.put("deviceID", JackpotApplication.JACKPOT_ID);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "register", msg.toString(), null);
            return response;
        } catch (Exception e) {
            // TODO: handle exception
            //Toast.makeText(myContext, errorMessage, Toast.LENGTH_LONG);
            return null;
        }
    }

    /*	public JSONArray getAllJackpots(String deviceID_header) {
            // creating connection detector class instance
            try {
                Hashtable<String, String> header = new Hashtable<String, String>();
                header.put("id", deviceID_header);

                JSONArray response = JsonParser.getInstance().getJSONArrayFromUrl(
                        baseURL + "getalljackpots", null, header);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }*/
    public JSONObject getAllJackpots(String deviceID_header) {
        // creating connection detector class instance
        try {
            Hashtable<String, String> header = new Hashtable<String, String>();
            header.put("id", deviceID_header);

            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "getalljackpots", null, header);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject checkIfCanAddGiftJoker(String deviceID_header) {
        // creating connection detector class instance
        try {
            Hashtable<String, String> header = new Hashtable<String, String>();
            header.put("id", deviceID_header);

            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "canGetJoker", null, header);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getTopScores(String deviceID_header) {
        try {
            Hashtable<String, String> header = new Hashtable<String, String>();
            header.put("id", deviceID_header);

            JSONObject response = JsonParser.getInstance().getJSONFromUrl(baseURL + "gettop", null, header);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getJackpotDetails(String id, String deviceID_header) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("id", id);
            Log.w("elsawaf", deviceID_header + " --- " + id);

            Hashtable<String, String> header = new Hashtable<String, String>();
            header.put("id", deviceID_header);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "getjackpotdetails", msg.toString(), header);
            //Log.d("elsawaf1", response.toString());
            return response;
        } catch (Exception e) {
            // TODO: handle exception
            //Toast.makeText(myContext, errorMessage, Toast.LENGTH_LONG);
            return null;
        }
    }


    public void incrementVideoViews(String id, String deviceID_header) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("id", id);

            Hashtable<String, String> header = new Hashtable<String, String>();
            header.put("id", deviceID_header);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "incrementjackpotviews", msg.toString(), header);
            Log.w("elsawaf " + JackpotServicesClient.this.getClass().getSimpleName(), response.toString());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void playJackpot(String id, String deviceID_header) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("jackpot_id", id);

            Hashtable<String, String> header = new Hashtable<String, String>();
            header.put("id", deviceID_header);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "playjackpot", msg.toString(), header);
            Log.w("elsawafPlay " + JackpotServicesClient.this.getClass().getSimpleName(), response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateScore(String id, String score, String deviceID_header) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("score", score);
            msg.put("jackpot_id", id);

            Hashtable<String, String> header = new Hashtable<String, String>();
            header.put("id", deviceID_header);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "updatescore", msg.toString(), header);
            Log.w("elsawafScore " + JackpotServicesClient.this.getClass().getSimpleName(), response.toString());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void win(String email, String phone) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("jackpot_id", JackpotApplication.JACKPOT_ID);
            msg.put("contactMail", email);
            msg.put("contactPhone", phone);

            Hashtable<String, String> header = new Hashtable<String, String>();
            header.put("id", JackpotApplication.TOKEN_ID);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "win", msg.toString(), header);
            Log.w("elsawafWin " + JackpotServicesClient.this.getClass().getSimpleName(), response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            // FIXME should handle everything if not submit winner information
        }
    }

    public JSONObject getQuestion(int level, String category, boolean isfree) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("level", level);
            msg.put("category", category);
            msg.put("free", isfree ? "1" : "0"); //0 false - 1 true it is free
            msg.put("jackpot", JackpotApplication.JACKPOT_ID);//TODO this field sould be deleted - By Hussien

            Hashtable<String, String> header = new Hashtable<String, String>();
            header.put("id", JackpotApplication.TOKEN_ID);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "getquestion", msg.toString(), header);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject answer(int isExtra, String questionID, String answer, String second,
                             String jackpotID, String used_floats, String used_jokers, String isLast, String level) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("is_extra", isExtra); //0 false - 1 true
            msg.put("question_id", questionID);
            msg.put("answer", answer);
            msg.put("second", second);
            msg.put("jackpot", jackpotID);
            msg.put("used_floats", used_floats);
            msg.put("used_jokers", used_jokers);
            msg.put("level", level);
            if (isLast.equals("1"))
                msg.put("is_last", isLast);

            Hashtable<String, String> header = new Hashtable<String, String>();
            header.put("id", JackpotApplication.TOKEN_ID);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "answer", msg.toString(), header);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject help(String questionID, String jackpotID, String used_floats, String used_jokers) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("question_id", questionID);
            msg.put("jackpot", jackpotID);
            msg.put("used_floats", used_floats);
            msg.put("used_jokers", used_jokers);

            Hashtable<String, String> header = new Hashtable<String, String>();
            header.put("id", JackpotApplication.TOKEN_ID);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "help", msg.toString(), header);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getAdImg() {
        try {
            Hashtable<String, String> header = new Hashtable<String, String>();
            header.put("id", JackpotApplication.TOKEN_ID);//deviceID_header

            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "getImage", null, header);
            return response;
        } catch (Exception e) {
            //Toast.makeText(myContext, errorMessage, Toast.LENGTH_LONG);
            return null;
        }
    }

    public JSONObject decrementFloats(int count) {
        Hashtable<String, String> header = new Hashtable<String, String>();
        header.put("id", JackpotApplication.TOKEN_ID);

        JSONObject msg = new JSONObject();
        try {
            msg.put("jackpot_id", JackpotApplication.JACKPOT_ID);
            msg.put("floats", count);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "decfloats", msg.toString(), header);
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject decrementJockers(int count) {
        Hashtable<String, String> header = new Hashtable<String, String>();
        header.put("id", JackpotApplication.TOKEN_ID);

        JSONObject msg = new JSONObject();
        try {
            msg.put("jackpot_id", JackpotApplication.JACKPOT_ID);
            msg.put("jokers", count);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "decjokers", msg.toString(), header);
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject addGiftJocker() {
        Hashtable<String, String> header = new Hashtable<String, String>();
        header.put("id", JackpotApplication.TOKEN_ID);

        JSONObject msg = new JSONObject();
        try {
            msg.put("jackpot_id", JackpotApplication.JACKPOT_ID);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "addGiftJocker", msg.toString(), header);
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject removeGiftJocker() {
        Hashtable<String, String> header = new Hashtable<String, String>();
        header.put("id", JackpotApplication.TOKEN_ID);

        JSONObject msg = new JSONObject();
        try {
            msg.put("jackpot_id", JackpotApplication.JACKPOT_ID);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "removeGiftJocker", msg.toString(), header);
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject GetJockersAndFloats() {
        Hashtable<String, String> header = new Hashtable<String, String>();
        header.put("id", JackpotApplication.TOKEN_ID);

        JSONObject msg = new JSONObject();
        try {
            msg.put("jackpot_id", JackpotApplication.JACKPOT_ID);
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "floatsjokers", msg.toString(), header);
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject saveGame(int jackpot, int level, int question, int totalScore) {
        Hashtable<String, String> header = new Hashtable<String, String>();
        header.put("id", JackpotApplication.TOKEN_ID);

        JSONObject msg = new JSONObject();
        try {
            msg.put("jackpot_id", jackpot);
            msg.put("level_num", level);
            msg.put("question_num", question);
            msg.put("score", totalScore);

            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "save", msg.toString(), header);
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getTerms() {
        Hashtable<String, String> header = new Hashtable<String, String>();
        if(JackpotApplication.TOKEN_ID!=null)
            header.put("id", JackpotApplication.TOKEN_ID);

        JSONObject msg = new JSONObject();
        try {

            String response = JsonParser.getInstance().getHTMLFromUrl(
                    baseURL + "getConditions", msg.toString(), header);
            return response;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }


    public JSONObject ContinueGameTask() {
        Hashtable<String, String> header = new Hashtable<String, String>();
        header.put("id", JackpotApplication.TOKEN_ID);

        try {
            JSONObject response = JsonParser.getInstance().getJSONFromUrl(
                    baseURL + "continue", null, header);
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
