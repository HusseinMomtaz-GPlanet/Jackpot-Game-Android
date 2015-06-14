package com.AppRocks.jackpot.models;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.activities.Question;
import com.AppRocks.jackpot.webservice.JackpotServicesClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class QuestionDetails {

    Activity activity;
    JSONObject questionDetailsJson;
    int level;
    int category;
    String question;
    String answer;
    String[] allChoices;
    String id;
    ArrayList<String> allWrongAnswers;
    int n;
    private GetQuestionDetailsTask getQuestionTask;

    public QuestionDetails(int level, int category, String question,
                           String answer, String[] allChoices) {
        super();
        this.level = level;
        this.category = category;
        this.question = question;
        this.allChoices = allChoices;
    }

    public QuestionDetails(String categoryName, int levelNumber, Activity a) {
        //Log.w("elsawaf", "Create question object");
        activity = a;
        level = levelNumber;
        // FIXME TEST Question
        /*setQuestion("TEST");
        setAllChoices(new String[]{"right", "wrong1 Test longgggggg", "Clintss Eastwoods", "Alfonsoo Zapatero"});
		setAnswer("right");
		*/
        getQuestionTask = new GetQuestionDetailsTask();
        getQuestionTask.execute(categoryName);
    }

    public ArrayList<String> getAllWrongAnswers() {
        return allWrongAnswers;
    }

    public void setAllWrongAnswers() {
        // entry all wrong answers after get right answer
        Collections.shuffle(Arrays.asList(allChoices));
        allWrongAnswers = new ArrayList<String>();
        for (int i = 0; i < 4; i++) {
            if (allChoices[i].equals(getAnswer())) {
                // nothing
            } else {
                allWrongAnswers.add(allChoices[i]);
            }
        }
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
        setAllWrongAnswers();
    }

    public String[] getAllChoices() {
        return allChoices;
    }

    public void setAllChoices(String[] allChoices) {
        Collections.shuffle(Arrays.asList(allChoices));
        this.allChoices = allChoices;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    class GetQuestionDetailsTask extends AsyncTask<String, Void, Integer> {

        final int ERROR = 1; // someone already won
        final int SUCCESS = 2; // get question details


        void generateTestQuestion() {
            setQuestion("Test Question");
            setAllChoices(new String[]{"Yes", "No", "No", "No"});

        }

        // this method return false only if winned is true
        protected Integer doInBackground(String... params) {
            //Log.w("elsawaf", "Start task");
            questionDetailsJson = JackpotServicesClient.getInstance()
                    .getQuestion(level, params[0], JackpotApplication.isPlayFreeJackpot);
            if (questionDetailsJson != null) {
                if (questionDetailsJson.has(JackpotApplication.TAG_QUESTION)) {
                    try {
                        Log.w("elsawafQ", questionDetailsJson.toString());
                        String q = questionDetailsJson.getString(JackpotApplication.TAG_QUESTION);
                        String w1 = questionDetailsJson.getString(JackpotApplication.TAG_ANSWER_1);
                        String w2 = questionDetailsJson.getString(JackpotApplication.TAG_ANSWER_2);
                        String w3 = questionDetailsJson.getString(JackpotApplication.TAG_ANSWER_3);
                        String w4 = questionDetailsJson.getString(JackpotApplication.TAG_ANSWER_4);

                        setId(questionDetailsJson.getString(JackpotApplication.TAG_QUESTION_ID));
                        //has joker should be written once
                        JackpotApplication.jokerHas = questionDetailsJson.getInt(JackpotApplication.TAG_MY_JOKERS);
                        JackpotApplication.floatyHas = questionDetailsJson.getInt(JackpotApplication.TAG_MY_FLOATS);

                        setQuestion(q);
                        setAllChoices(new String[]{w1, w2, w3, w4});
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // has question data
                    return SUCCESS;
                } else if (questionDetailsJson.has("status")) {
                    // this is ooops screen that another person win
                    // NO Question (WIN)
                    return ERROR;
                }
				/*else{// the json doesn't have question tag or winner tag
					Log.w("elsawafQ", "no Question data");
					setQuestion("Error, Sorry No Question");
					setAllChoices(new String[]{" ", " ", " ", " "});
					// problem to get question information
					return -1;
					}*/
            }

            // go here only if the json is null
            Log.w("elsawafQ", "no Question data");
            setQuestion("Error, Sorry No Question");
            setAllChoices(new String[]{" ", " ", " ", " "});
            // problem to get question information
            return SUCCESS;
        }

        protected void onPostExecute(Integer result) {
            if (result == SUCCESS) {
                Question.isGetQuestionTaskEnd = true;
                if (Question.isQuestionViewsShow) {
                    ((Question) activity).setQuestionDetailsOnScreen();
                    Question.isGetQuestionTaskEnd = false;
                    Question.isQuestionViewsShow = false;
                }
            } else if (result == ERROR) {
                // ooops screen
                ((Question) activity).showOoopsScreen();
            }

            super.onPostExecute(result);
        }
    }

}
