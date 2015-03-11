package com.AppRocks.jackpot.models;


public class LevelController {

    public static final String PREF_DIFFICULTY = "difficulty";
    public static final String PREF_LEVEL = "level";
    public static final String PREF_CATEGORY = "category";
    public static final String PREF_NUMBER_OF_ANSWERS = "answers";
    public static final String PREF_TOTAL_SCORE = "score";
    static final int[] numberOfQestionsPerLevels = {3, 5, 7, 9};
    public static int timer;
    public static String PREF_JACKPOT_ID;
    public int prizeDifficulty;
    public int numberOfQuestions;
    public int category;
    public int level;
    public int numberOfAnswers;
    public int totalScore = 0;
    public int newScore;
    //this flag to hide helper icons and calculate score at end of level
    public boolean oneMoreQuestion;
    public boolean isOneMoreQuestionSolved;

    public LevelController(int prizeDifficulty, int level, int answers, int score, boolean oneMoreQ) {
        super();
        if (prizeDifficulty > 4)
            prizeDifficulty = 4;
        this.prizeDifficulty = prizeDifficulty;
        this.numberOfQuestions = numberOfQestionsPerLevels[prizeDifficulty - 1];
        this.level = level;
        numberOfAnswers = answers;
        totalScore = score;
        newScore = 0;
        oneMoreQuestion = oneMoreQ;
    }

    /*
     *
    0-Science: Green
    1-History: Yellow
    2-Sports: orange
    3-Geography: Blue
    4-Art: Pink
    5-Literature: Violet
    */
    public String getCategoryNameByNumber(int categoryNumber) {
        switch (categoryNumber) {
            case 0://should be science
                return "science";
            case 1://should be history
                return "history";
            case 2:
                return "sports";
            case 3:
                return "geography";
            case 4:
                return "art";
            case 5:
                return "literature";
            default:
                return null;
        }
    }

    // I don't use it now
    public QuestionDetails nextQuestion() {
        String categoryName = getCategoryNameByNumber(category);
        if (level > 6) {
            oneMoreQuestion = true;
        }
        //else
        return null;//new QuestionDetails(categoryName, level);

    }


}
