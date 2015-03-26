package com.AppRocks.jackpot;

import android.app.Application;
import android.content.Context;

public class JackpotApplication extends Application {

    // getalljackpots fields
    public static final String TAG_ID = "id";
    public static final String TAG_IMAGE = "image";
    public static final String TAG_Active = "active";
    public static final String TAG_FREE = "free";
    public static final String TAG_DIFFICULTY = "difficulty";
    public static final String TAG_WINNER = "winner";
    public static final String TAG_TITLE = "title";
    public static final String TAG_HASWINNER = "has_winner";
    public static final String TAG_Bloked = "is_blocked";

    //getjackpotdetails fields
    public static final String TAG_DESCRIPTION = "description";
    public static final String TAG_COMPANY = "company";
    public static final String TAG_BRAND = "brand";
    public static final String TAG_FLY = "fly";
    public static final String TAG_YOUR_VIEWS = "yourviews";
    public static final String TAG_WORLD_VIEWS = "woldviews";
    public static final String TAG_VIDEOS_ARRAY = "videos";
    public static final String TAG_VIDEOS_LINK = "link";
    // winner fileds
    public static final String TAG_WON = "won";
    public static final String TAG_WON_USER = "user";
    public static final String TAG_WON_USER_FIRST_NAME = "first_name";
    public static final String TAG_WON_USER_LAST_NAME = "last_name";

    // getquestion fields
    public static final String TAG_ANSWER_1 = "answer_1";
    public static final String TAG_ANSWER_2 = "answer_2";
    public static final String TAG_ANSWER_3 = "answer_3";
    public static final String TAG_ANSWER_4 = "answer_4";
    public static final String TAG_QUESTION = "question";
    public static final String TAG_QUESTION_ID = "id";
    public static final String TAG_MY_JOKERS = "my_jokers";
    public static final String TAG_MY_FLOATS = "my_floats";

    //get top score fields
    public static final String TAG_NICKNAME = "nickname";
    public static final String TAG_TOTAL_SCORE = "score";
    public static final String TAG_TOP_FIVE = "top5";
    public static final String TAG_YOU_SCORE = "user";

    public static final String TAG_AD_IMAGE = "image";
    public static final String TAG_JOCKERS_NUMBER = "jokars_count";
    public static final String TAG_FLOATS_NUMBERS = "float_count";
    public static final String TAG_JOCKERS_GIFT = "giftJockers";


    public static final String BASE_URL = "http://game.wikimasry.com/"; //"http://gplanet.ahmadelghazaly.com/";
    //http://gplanet.ahmadelghazaly.com/api/v1/
    public static final String BASE_URL_WEB_SERVICES = "http://game.wikimasry.com/api/v1/";
    public static final String PREF_IS_REGISTER_FREE = "registerFree";
    public static final String PREF_NICKNAME = "nickname";
    public static final String PREF_LAST_JACKPOT_ID = "lastJackpotID";
    public static final String PREF_LAST_JACKPOT_DIFFICULTY = "lastJackpotDifficulty";
    public static final String PREF_SAVED_JACKPOT_ID = "savedJackpotID";
    public static final String PREF_USER_TOKEN = "user_token";
    public static String TOKEN_ID;
    public static String JACKPOT_ID = "";
    public static int JACKPOT_DIFFICULTY;
    public static String logoURL;
    public static boolean isWined = false;
    //used to update score for premium jackpot
    public static boolean isPlayFreeJackpot = false;
    // allowed to use on every question
    public static int jokerAllowed = 0;
    public static int floatyAllowed = 0;

    // already has and may increasing
    public static int jokerHas = 3;
    public static int floatyHas = 5;
    public static int jokerGifts;

    //using this if one player win the jackpot
    //to less 25 points for each Jokers used,  and 10 for each Floaty used
    public static int numberOfjokerUsed = 0;
    public static int numberOfFloatyUsed = 0;

    public static int getJockerHas(Context c) {
        JackpotParameters p = new JackpotParameters(c);
        return p.getInt("jocker", 0);
    }

    public static int getFloatyHas(Context c) {
        JackpotParameters p = new JackpotParameters(c);
        return p.getInt("floaty", 0);
    }

    public static int getJockerAllowed(int difficulty, int level) {
        switch (difficulty) {
            case 1:
                return 1;
            case 2:
                if (level == 1 || level == 2)
                    return 2;
                else
                    return 1;
            case 3:
                if (level == 5 || level == 6)
                    return 1;
                else
                    return 2;
            case 4:
                if (level == 1 || level == 2)
                    return 3;
                else
                    return 2;
            default:
                return 0;
        }
    }

    public static int getFloatyAllowed(int difficulty, int level) {
        switch (difficulty) {
            case 1:
                if (level == 1 || level == 2)
                    return 2;
                else
                    return 1;
            case 2:
                if (level == 5 || level == 6)
                    return 1;
                else
                    return 2;
            case 3:
                if (level == 1 || level == 2)
                    return 3;
                else
                    return 2;
            case 4:
                if (level == 1 || level == 2)
                    return 4;
                else if (level == 3 || level == 4)
                    return 3;
                else
                    return 2;
            default:
                return 0;
        }
    }

}
