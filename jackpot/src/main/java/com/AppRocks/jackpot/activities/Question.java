package com.AppRocks.jackpot.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.JackpotParameters;
import com.AppRocks.jackpot.R;
import com.AppRocks.jackpot.models.LevelController;
import com.AppRocks.jackpot.models.QuestionDetails;
import com.AppRocks.jackpot.util.AlarmTimer;
import com.AppRocks.jackpot.util.AnimationRandom;
import com.AppRocks.jackpot.util.ConnectionDetector;
import com.AppRocks.jackpot.util.RouletteView;
import com.AppRocks.jackpot.util.RouletteView.RotationEndCallBack;
import com.AppRocks.jackpot.util.SoundPlayer;
import com.AppRocks.jackpot.webservice.AnswerTask;
import com.AppRocks.jackpot.webservice.ContinueGameTask;
import com.AppRocks.jackpot.webservice.GetAdImageTask;
import com.AppRocks.jackpot.webservice.GetJockersAndFloatsTask;
import com.AppRocks.jackpot.webservice.HelpTask;
import com.AppRocks.jackpot.webservice.PlayJackpotTask;
import com.AppRocks.jackpot.webservice.SaveGameTask;
import com.AppRocks.jackpot.webservice.WinTask;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Question extends Activity implements RotationEndCallBack,
        AnimationListener {

    private static final int ANIMATION_CHOICES = 1;
    private static final int ANIMATION_TRUE = 3;
    private static final int ANIMATION_FALSE = 4;
    private static final int ANIMATION_JOCKER = 5;
    private static final int ANIMATION_Question = 2;
    public static boolean isGetQuestionTaskEnd = false;
    public static boolean isQuestionViewsShow = false;
    public static JSONObject jackpotDetailsJson;
    public TextView txtAnswer1;
    public TextView txtAnswer2;
    public TextView txtAnswer3;
    public TextView txtAnswer4;
    public TextView txtAlarm;
    public TextView txtScore;
    public TextView question_counter;
    public ImageView right_logo;
    public QuestionDetails question;
    public LevelController level;
    public Timer timer;
    public MediaPlayer mp;
    // to cancel saved game if he lose
    public boolean isThisSavedGame;
    // flag for last Q to win
    public String isLast = "0";
    public ImageButton adImgBtn;
    public ProgressBar adProgressBar;
    public TimeIsUpTask timeIsUpTask;
    public boolean isTryingAgain;
    public ImageLoader imageLoader;
    RotateAnimation rotation;
    int progress;
    int newProgress;
    JackpotParameters jackpotParams;
    int animN;
    private Handler messageHandler = new Handler() {

        public void handleMessage(Message msg) {
            // update your view here
            if (deleteRandomWrongAnswer()) {
                // this delete one wrong answer every time
                // and the last time will go to other conditions.
                return;
            } else if (animN == ANIMATION_TRUE) {
                // this condition is important when using Jocker
                return;
            } else {
                // showYouLoseScreen after delete all wrong answers
                showYouLoseScreen();
            }
        }
    };
    ArrayList<Integer> choisedCategory;
    boolean isAnimationEnd;
    WindowManager.LayoutParams params;
    DisplayImageOptions options;
    ProgressBar spinner = null;
    private View page;
    private TextView txtQuestion;
    private ImageView imgNiddle;
    private AlarmTimer alarmTimer;
    private FrameLayout floatyIconFrame;
    private FrameLayout jockerIconFrame;
    private ImageButton floatyIcon;
    private ImageButton jockerIcon;
    private RelativeLayout rouletteLayout;
    private RouletteView roulette;
    private RelativeLayout rootLayout;
    private FrameLayout choicesFrame;
    private LinearLayout bottomBar;
    private Animation qAnimation;
    private ImageView imgTrue;
    private Animation trueAnimation;
    private TextView txtCategory;
    private TextView txtLevel;
    private Animation choicesAnimation;
    private LinearLayout questionBackground;
    private TextView txtScoreWord;
    private View rouletteDim;
    private TextView txtLevelRoulette;
    private TextView txtQuestionRoulette;
    private ImageButton btnPushRoulette;
    private TextView txtStar;
    private RelativeLayout starLayout;
    private TextView txtLevelRouletteTitle;
    private TextView txtQuestionRouletteTitle;
    private ImageView imgCategory;
    private StartQuestionTask startQTask;
    private WindowManager windowManager;
    private View getReadyView;
    private ImageView imgCheeseLeft;
    private ImageView imgCheeseRight;
    private AnswerClickListener answerClickListener;
    private Button btnSaveGame;
    private JokerClickListener jockerClickListener;
    /*
     * We have five elements invisible at beginning (imgNiddle, questionFrame,
	 * bottomBar, leftIcon, rightIcon)
	 */
    private FloatyClickListener floatyClickListener;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);

        // Should load sound to be ready when we need it
        SoundPlayer.initSounds(Question.this);
        jackpotParams = new JackpotParameters(Question.this);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View page = inflater.inflate(R.layout.page, null);
        spinner = (ProgressBar) page
                .findViewById(R.id.loading);
        //imageLoader=new ImageLoader(this.getApplicationContext());

        intiUI();
        initUILConfig();

        showJackpotLogoOnRightCircle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getBooleanExtra("savedGame", false)) {
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            if (cd.isConnectingToInternet()) {
                new ContinueGameTask(this).execute();
            }
        }
        // it is a new game
        else {
            // some cases we need the difficulty
            if (JackpotApplication.JACKPOT_DIFFICULTY == 0) {
                JackpotApplication.JACKPOT_DIFFICULTY = jackpotParams.getInt(JackpotApplication.PREF_LAST_JACKPOT_DIFFICULTY, 5);
            }
            level = new LevelController(JackpotApplication.JACKPOT_DIFFICULTY, 1, 0, 0, false);
            showGetReadyScreen();
            initJockersAndFloats();
        }
        // FIXME For test level = new LevelController(1, 1, 0, 0, false);


        if (rouletteLayout.getVisibility() == View.VISIBLE) {
            updateRouletteText();
            // the suitble place to execute getquestiontask
        }
        /*else if (choicesFrame.getVisibility() == View.VISIBLE)
			startTimer();*/

    }

    @Override
    protected void onPause() {
        super.onPause();


        if (rouletteLayout.getVisibility() == View.VISIBLE) {
            // check if roulette spin to stop it
            if (!btnPushRoulette.isClickable())
                roulette.stop();
        } else if (choicesFrame.getVisibility() == View.VISIBLE) {
            stopTimer();
            deleteTheSavedGame();
            if (timer != null) {
                timer.cancel();
            }
        } else {
            hideScreen();
            deleteTheSavedGame();
        }
        finish();
    }


    @Override
    public void onBackPressed() {
        getWindowManager().removeView(getReadyView);
        super.onBackPressed();
    }

    private void initUILConfig() {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                Question.this).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                        // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                        //.imageScaleType(ImageScaleType.EXACTLY)
                        //.bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        imageLoader = ImageLoader.getInstance();
    }

    private void intiUI() {
        txtQuestion = (TextView) findViewById(R.id.txt_question);
        txtAnswer1 = (TextView) findViewById(R.id.answer_1);
        txtAnswer2 = (TextView) findViewById(R.id.answer_2);
        txtAnswer3 = (TextView) findViewById(R.id.answer_3);
        txtAnswer4 = (TextView) findViewById(R.id.answer_4);
        question_counter = (TextView) findViewById(R.id.question_counter);
        right_logo = (ImageView) findViewById(R.id.company_rigth_logo);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        roulette = (RouletteView) findViewById(R.id.roulette);
        rouletteLayout = (RelativeLayout) findViewById(R.id.rouletteLayout);
        rouletteDim = findViewById(R.id.rouletteDim);
        txtLevelRoulette = (TextView) findViewById(R.id.txtRouletteLevel);
        txtQuestionRoulette = (TextView) findViewById(R.id.txtRouletteQuestion);
        txtLevelRouletteTitle = (TextView) findViewById(R.id.txtRouletteLevelTitle);
        txtQuestionRouletteTitle = (TextView) findViewById(R.id.txtRouletteQuestionTitle);
        btnPushRoulette = (ImageButton) findViewById(R.id.btnRolettePush);
        imgTrue = (ImageView) findViewById(R.id.imgTrue);
        imgNiddle = (ImageView) findViewById(R.id.imgNiddle);
        txtAlarm = (TextView) findViewById(R.id.txtAlarm);
        txtScore = (TextView) findViewById(R.id.txtScore);
        txtCategory = (TextView) findViewById(R.id.txtCategory);
        txtLevel = (TextView) findViewById(R.id.txtLevel);
        txtStar = (TextView) findViewById(R.id.txtStar);
        starLayout = (RelativeLayout) findViewById(R.id.starLayout);
        choicesFrame = (FrameLayout) findViewById(R.id.choicesFrame);
        bottomBar = (LinearLayout) findViewById(R.id.bottomBar);
        txtScoreWord = (TextView) findViewById(R.id.scoreWord);
        floatyIconFrame = (FrameLayout) findViewById(R.id.floatyIconFrame);
        jockerIconFrame = (FrameLayout) findViewById(R.id.jockerIconFrame);
        floatyIcon = (ImageButton) findViewById(R.id.floatyIcon);
        jockerIcon = (ImageButton) findViewById(R.id.jockerIcon);
        questionBackground = (LinearLayout) findViewById(R.id.question_body);
        imgCategory = (ImageView) findViewById(R.id.imgCategoryIcon);
        imgCheeseLeft = (ImageView) findViewById(R.id.imgCheeseLeft);
        imgCheeseRight = (ImageView) findViewById(R.id.imgCheeseRight);
        btnSaveGame = (Button) findViewById(R.id.btnSaveGame);
        adImgBtn = (ImageButton) findViewById(R.id.adImgBtn);
        adProgressBar = (ProgressBar) findViewById(R.id.AdLoading);


        answerClickListener = new AnswerClickListener();
        jockerClickListener = new JokerClickListener();
        floatyClickListener = new FloatyClickListener();

        btnPushRoulette.setOnClickListener(new PushRouletteClickListener());

        // TODO delete it on final version
        // SoundPlayer.initSounds(Question.this);

        loadFonts();

        qAnimation = AnimationUtils.loadAnimation(this,
                R.anim.anim_translate_alpha);
        qAnimation.setAnimationListener(this);

        choicesAnimation = AnimationUtils
                .loadAnimation(this, R.anim.accelerate);
        choicesAnimation.setAnimationListener(this);

        trueAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_flash);
        trueAnimation.setAnimationListener(this);

        choisedCategory = new ArrayList<Integer>(6);
    }

    private void showJackpotLogoOnRightCircle() {
        String logoURL = null;
        try {
            if(jackpotDetailsJson==null)
                return;
            logoURL = jackpotDetailsJson.getString(JackpotApplication.TAG_IMAGE);
            String jackpotLogoURL = JackpotApplication.BASE_URL
                    + logoURL.substring(logoURL.indexOf("uploads"));
            //imageLoader.DisplayImage(logoURL,right_logo);
            if (imageLoader != null) {
                imageLoader.displayImage(jackpotLogoURL, right_logo, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                spinner.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view,
                                                        FailReason failReason) {
                                String message = null;
                                switch (failReason.getType()) {
                                    case IO_ERROR:
                                        message = "Input/Output error";
                                        break;
                                    case DECODING_ERROR:
                                        message = "Image can't be decoded";
                                        break;
                                    case NETWORK_DENIED:
                                        message = "Downloads are denied";
                                        break;
                                    case OUT_OF_MEMORY:
                                        message = "Out Of Memory error";
                                        break;
                                    case UNKNOWN:
                                        message = "Unknown error";
                                        break;
                                }
                                Toast.makeText(Question.this, message,
                                        Toast.LENGTH_SHORT).show();

                                spinner.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                spinner.setVisibility(View.GONE);
                            }
                        });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showCompanyLogoOnCenterAvomenetr() {
        String logoURL = null;
        try {
            logoURL = jackpotDetailsJson.getString(JackpotApplication.TAG_FLY);
            String jackpotLogoURL = JackpotApplication.BASE_URL
                    + logoURL.substring(logoURL.indexOf("uploads"));
            //imageLoader.DisplayImage(logoURL,right_logo);
            if (imageLoader != null) {
                imageLoader.displayImage(jackpotLogoURL, imgTrue, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                spinner.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view,
                                                        FailReason failReason) {
                                String message = null;
                                switch (failReason.getType()) {
                                    case IO_ERROR:
                                        message = "Input/Output error";
                                        break;
                                    case DECODING_ERROR:
                                        message = "Image can't be decoded";
                                        break;
                                    case NETWORK_DENIED:
                                        message = "Downloads are denied";
                                        break;
                                    case OUT_OF_MEMORY:
                                        message = "Out Of Memory error";
                                        break;
                                    case UNKNOWN:
                                        message = "Unknown error";
                                        break;
                                }
                                Toast.makeText(Question.this, message,
                                        Toast.LENGTH_SHORT).show();

                                spinner.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                spinner.setVisibility(View.GONE);
                            }
                        });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadFonts() {
        Typeface font = Typeface.createFromAsset(getAssets(),
                "BigSoftie-Fat.otf");
        txtQuestion.setTypeface(font);
        txtAnswer1.setTypeface(font);
        txtAnswer2.setTypeface(font);
        txtAnswer3.setTypeface(font);
        txtAnswer4.setTypeface(font);
        question_counter.setTypeface(font);
        txtCategory.setTypeface(font);
        txtLevel.setTypeface(font);
        txtAlarm.setTypeface(font);
        txtScore.setTypeface(font);
        txtScoreWord.setTypeface(font);
        txtLevelRoulette.setTypeface(font);
        txtQuestionRoulette.setTypeface(font);
        txtLevelRouletteTitle.setTypeface(font);
        txtQuestionRouletteTitle.setTypeface(font);
        txtStar.setTypeface(font);
    }

    /*
     * this method called after initialize "level" object,
     * so in the first we get the help allowed depend on level,
     * then we get the help number user has,
     * finally we update UI
     */
    public void initJockersAndFloats() {
        // should update them by this method at the beginning of game,
        // and using method initStartNewLevel() to update them at the beginning of level
        JackpotApplication.numberOfFloatyUsed = 0;
        JackpotApplication.numberOfjokerUsed = 0;

        JackpotApplication.jokerAllowed = JackpotApplication
                .getJockerAllowed(level.prizeDifficulty, level.level);
        JackpotApplication.floatyAllowed = JackpotApplication
                .getFloatyAllowed(level.prizeDifficulty, level.level);

        ConnectionDetector cd = new ConnectionDetector(Question.this);
        if (cd.isConnectingToInternet()) {
            new GetJockersAndFloatsTask(this).execute();
        }
    }

    public void showGetReadyScreen() {
        // elsawaf
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);// context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getReadyView = inflator.inflate(R.layout.get_ready_screen, null);

        int windowHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, windowHeight / 2,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.windowAnimations = R.style.screen_animation;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = windowHeight / 5;
        windowManager.addView(getReadyView, params);

        TextView getReadyTxt = (TextView) getReadyView
                .findViewById(R.id.txt_get_ready);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "BigSoftie-Fat.otf");
        getReadyTxt.setTypeface(font);

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        HideScreenTask hideTask = new HideScreenTask();
        timer.schedule(hideTask, 3000);

    }

    public void hideGetReadyOrTimeIsUpScreen() {
        // elsawaf
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // elsawaf
                if (getReadyView != null) {
                    windowManager.removeView(getReadyView);
                    getReadyView = null;
                }
                if (choicesFrame.getVisibility() == View.INVISIBLE) {
                    // this was get ready screen
                    rouletteDim.setVisibility(View.INVISIBLE);
                    showNextQuestion();
                } else
                    // this was time's up screen
                    rouletteDim.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void hideScreen() {
        // elsawaf
        if (getReadyView != null) {
            windowManager.removeView(getReadyView);
            getReadyView = null;
        }
    }

    private void showYouLoseScreen() {
        rouletteDim.setVisibility(View.VISIBLE);
        mp = MediaPlayer
                .create(this,
                        R.raw.when_user_lose_the_game_and_it_appears_the_try_again_screen_option_b);
        mp.start();
        level.isOneMoreQuestionSolved = true;
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);// context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getReadyView = inflator.inflate(R.layout.you_lose_screen, null);

        int windowHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, // (windowHeight/2),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.windowAnimations = R.style.screen_animation;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = windowHeight / 5;
        windowManager.addView(getReadyView, params);

        TextView youLoseTxt = (TextView) getReadyView
                .findViewById(R.id.txt_you_lose);
        TextView scoreTitleTxt = (TextView) getReadyView
                .findViewById(R.id.txtScoreTitleYouLose);
        TextView scoreTxt = (TextView) getReadyView
                .findViewById(R.id.txtScoreYouLose);
        TextView levelTxt = (TextView) getReadyView.findViewById(R.id.txtLevel);
        Button tryAgainBtn = (Button) getReadyView
                .findViewById(R.id.btnTryAgain);
        Button menuBtn = (Button) getReadyView
                .findViewById(R.id.btnMenu);

        ImageView niddleYouLose = (ImageView) getReadyView
                .findViewById(R.id.imgNiddle);

        Typeface font = Typeface.createFromAsset(getAssets(),
                "BigSoftie-Fat.otf");
        youLoseTxt.setTypeface(font);
        scoreTitleTxt.setTypeface(font);
        scoreTxt.setTypeface(font);
        levelTxt.setTypeface(font);
        tryAgainBtn.setTypeface(font);
        menuBtn.setTypeface(font);

        scoreTxt.setText("" + level.totalScore);
        updateTxtLevel(levelTxt);
        rotateNiddle(niddleYouLose, level.level);


        //send him to the main menu
        menuBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // elsawaf
                mp.release();
                getWindowManager().removeView(getReadyView);
                level.isOneMoreQuestionSolved = false;
                startActivity(new Intent(Question.this, Main_.class));
            }
        });

        //make the game restart
        tryAgainBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // elsawaf
                mp.release();
                // call play jackpot service to reset the score and initialize new game
                new PlayJackpotTask(Question.this).execute();
                //initJockersAndFloats();
                level.isOneMoreQuestionSolved = false;
                isTryingAgain = true;
                getWindowManager().removeView(getReadyView);
            }
        });
    }


    public void showTimeIsUpScreen() {
        //get the right answer from task
        // only if we don't has it
        ConnectionDetector cd = new ConnectionDetector(Question.this);
        if (cd.isConnectingToInternet()) {
            new AnswerTask(Question.this).execute("Jackpot time is up", "0", "0", "0");
        }
        deleteTheSavedGame();
        rouletteDim.setVisibility(View.VISIBLE);
        stopTimer();
        mp = MediaPlayer.create(this, R.raw.when_the_clock_times_up);
        mp.start();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);// context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getReadyView = inflator.inflate(R.layout.time_is_up_screen, null);

        int windowHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, // (windowHeight/2),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.windowAnimations = R.style.screen_animation;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = windowHeight / 5;
        windowManager.addView(getReadyView, params);

        TextView youLoseTxt = (TextView) getReadyView
                .findViewById(R.id.txt_you_lose);
        TextView scoreTitleTxt = (TextView) getReadyView
                .findViewById(R.id.txtScoreTitleYouLose);

        Typeface font = Typeface.createFromAsset(getAssets(),
                "BigSoftie-Fat.otf");
        youLoseTxt.setTypeface(font);
        scoreTitleTxt.setTypeface(font);

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        timeIsUpTask = new TimeIsUpTask();
        //timer.schedule(timeIsUpTask, 3000);

    }

    private void showCheeseLevelScreen() {
        rouletteDim.setVisibility(View.VISIBLE);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);// context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getReadyView = inflator.inflate(R.layout.cheese_level_screen, null);

        int windowHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.windowAnimations = R.style.screen_animation;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = windowHeight / 5;
        windowManager.addView(getReadyView, params);

        Button OkBtn = (Button) getReadyView.findViewById(R.id.btnTryAgain);
        TextView cheeseLevelNumberTxt = (TextView) getReadyView
                .findViewById(R.id.txt_you_lose);
        TextView cheeseLevelInfoTxt = (TextView) getReadyView
                .findViewById(R.id.txt_cheese_info);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "BigSoftie-Fat.otf");
        cheeseLevelNumberTxt.setTypeface(font);
        cheeseLevelInfoTxt.setTypeface(font);
        OkBtn.setTypeface(font);

        cheeseLevelNumberTxt.append("" + level.level);
        cheeseLevelInfoTxt.append("" + getLevelNumberWord(level.level)
                + " cheese!");

        OkBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // elsawaf
                hideScreen();
                showRouletteView();
            }
        });
    }

    private void showLastCheeseScreen() {
        rouletteDim.setVisibility(View.VISIBLE);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);// context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getReadyView = inflator.inflate(R.layout.last_cheese_screen, null);

        int windowHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.windowAnimations = R.style.screen_animation;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = windowHeight / 5;
        windowManager.addView(getReadyView, params);

        Button OkBtn = (Button) getReadyView.findViewById(R.id.btnTryAgain);
        TextView cheeseLevelNumberTxt = (TextView) getReadyView
                .findViewById(R.id.txt_you_lose);
        TextView cheeseLevelInfoTxt = (TextView) getReadyView
                .findViewById(R.id.txt_cheese_info);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "BigSoftie-Fat.otf");
        cheeseLevelNumberTxt.setTypeface(font);
        cheeseLevelInfoTxt.setTypeface(font);
        OkBtn.setTypeface(font);

        OkBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // elsawaf
                hideScreen();
                showRouletteView();
            }
        });
    }

    private void showLevelCompleteScreen() {
        playLevelCompleteSound();
        rouletteDim.setVisibility(View.VISIBLE);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);// context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getReadyView = inflator.inflate(R.layout.level_complete_screen, null);

        int windowHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,// (windowHeight/2),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.windowAnimations = R.style.screen_animation;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = windowHeight / 5;
        windowManager.addView(getReadyView, params);

        TextView youLoseTxt = (TextView) getReadyView
                .findViewById(R.id.txt_you_lose);
        TextView scoreTitleTxt = (TextView) getReadyView
                .findViewById(R.id.txtScoreTitleYouLose);
        TextView scoreTxt = (TextView) getReadyView
                .findViewById(R.id.txtScoreYouLose);
        TextView levelTxt = (TextView) getReadyView.findViewById(R.id.txtLevel);
        Button continueBtn = (Button) getReadyView
                .findViewById(R.id.btnTryAgain);
        ImageView niddleYouLose = (ImageView) getReadyView
                .findViewById(R.id.imgNiddle);

        Typeface font = Typeface.createFromAsset(getAssets(),
                "BigSoftie-Fat.otf");
        youLoseTxt.setTypeface(font);
        scoreTitleTxt.setTypeface(font);
        scoreTxt.setTypeface(font);
        levelTxt.setTypeface(font);
        continueBtn.setTypeface(font);

        ForegroundColorSpan fcs;
        SpannableStringBuilder sb = new SpannableStringBuilder(" "
                + level.level);
        fcs = new ForegroundColorSpan(Color.rgb(217, 23, 14));
        StyleSpan ssb = new StyleSpan(android.graphics.Typeface.BOLD);

        sb.setSpan(fcs, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(ssb, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        // level # text
        scoreTitleTxt.append(sb);

        updateTxtLevel(levelTxt);
        rotateNiddle(niddleYouLose, level.level);

        continueBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // elsawaf
                initStartNewLevel();

                hideScreen();
                //showRouletteView();
                rouletteDim.setVisibility(View.INVISIBLE);
                hideQuestionViewes();
                showNextQuestion();
            }
        });
    }

    protected void initStartNewLevel() {
        level.level++;
        level.numberOfAnswers = 0;
        JackpotApplication.numberOfFloatyUsed = 0;
        JackpotApplication.numberOfjokerUsed = 0;

        JackpotApplication.jokerAllowed = JackpotApplication
                .getJockerAllowed(level.prizeDifficulty, level.level);
        JackpotApplication.floatyAllowed = JackpotApplication
                .getFloatyAllowed(level.prizeDifficulty, level.level);
        updateTxtLevel(txtLevel);
        level.oneMoreQuestion = false;
        level.isOneMoreQuestionSolved = false;

        txtLevelRouletteTitle.setText("Level : ");
        txtQuestionRouletteTitle.setText("Question : ");
        imgCheeseLeft.setVisibility(View.INVISIBLE);
        imgCheeseRight.setVisibility(View.INVISIBLE);
    }

    private void playLevelCompleteSound() {
		/*if (mp != null) {
			mp.release();
			mp = null;
		}*/
        switch (level.level) {
            case 1:
                mp = MediaPlayer.create(this, R.raw.when_user_win_a_cheese_and_pass_level_1_2_and_3);
                break;
            case 2:
                mp = MediaPlayer.create(this, R.raw.when_user_win_a_cheese_and_pass_level_1_2_and_3);
                break;
            case 3:
                mp = MediaPlayer.create(this, R.raw.when_user_win_a_cheese_and_pass_level_1_2_and_3);
                break;
            case 4:
                mp = MediaPlayer.create(this, R.raw.when_user_win_a_cheese_and_pass_level_in_4_and_5_level);
                break;
            case 5:
                mp = MediaPlayer.create(this, R.raw.when_user_win_a_cheese_and_pass_level_in_4_and_5_level);
                break;
            default:
                break;

        }
        mp.start();
    }

    public void showOoopsScreen() {
        mp = MediaPlayer.create(this, R.raw.when_other_user_wins_the_same_jackpot__ooops_screen);
        mp.start();
        //let the this thread sleeps till the sound effect finish to show the popup after it
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        rouletteDim.setVisibility(View.VISIBLE);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);// context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getReadyView = inflator.inflate(R.layout.ooops_screen, null);

        int windowHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.windowAnimations = R.style.screen_animation;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = windowHeight / 5;
        windowManager.addView(getReadyView, params);

        Button OkBtn = (Button) getReadyView.findViewById(R.id.btnTryAgain);
        TextView cheeseLevelNumberTxt = (TextView) getReadyView
                .findViewById(R.id.txt_you_lose);
        TextView cheeseLevelInfoTxt = (TextView) getReadyView
                .findViewById(R.id.txt_cheese_info);
        TextView tryAgaintxt = (TextView) getReadyView
                .findViewById(R.id.tryagainTextView);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "BigSoftie-Fat.otf");

        cheeseLevelNumberTxt.setTypeface(font);
        cheeseLevelInfoTxt.setTypeface(font);
        tryAgaintxt.setTypeface(font);
        OkBtn.setTypeface(font);

        OkBtn.setOnClickListener(new OnClickListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View arg0) {
                // elsawaf
                hideScreen();
                Intent intent = new Intent(Question.this, Main_.class);
				/*
				 * for API <=10 now one can use IntentCompat class for the same.
				 * One can use IntentCompat.FLAG_ACTIVITY_CLEAR_TASK flag to
				 * clear task. So you can support pre API level 11 as well.
				 */
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    public void playJackpotCallback() {
        startActivity(new Intent(Question.this, Question.class));
        isTryingAgain = false;
    }

    private void showGetDataScreen() {
        Dialog pop_up = new Dialog(this);
        pop_up.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pop_up.getWindow().setGravity(Gravity.CENTER);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getReadyView = inflator.inflate(R.layout.get_data_screen, null);
        pop_up.setContentView(getReadyView);

        final EditText contactEmail = (EditText) getReadyView
                .findViewById(R.id.txt_get_ready);
        final EditText contactPhone = (EditText) getReadyView
                .findViewById(R.id.txt_get_ready2);
        Button submitBtn = (Button) getReadyView
                .findViewById(R.id.btnTryAgain);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "BigSoftie-Fat.otf");
        contactEmail.setTypeface(font);
        contactPhone.setTypeface(font);
        submitBtn.setTypeface(font);
            submitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // elsawaf
                ConnectionDetector cd = new ConnectionDetector(Question.this);
                if (cd.isConnectingToInternet()) {
                    new WinTask(isThisSavedGame).execute(contactEmail.getText().toString(), contactPhone.getText().toString());
                }
                startActivity(new Intent(Question.this, Main_.class));
            }
        });

        pop_up.show();
    }

    private String getLevelNumberWord(int n) {
        switch (n) {
            case 1:
                return "first";

            case 2:
                return "second";

            case 3:
                return "third";

            case 4:
                return "fourth";

            case 5:
                return "fifth";

            case 6:
                return "sixth";

            default:
                return " ";
        }
    }

    // generate random numbers from 0 to 5
    private int getRandomNumber() {
        Random rGenerator = new Random();
        level.category = rGenerator.nextInt(6);
        // to restore categories for last six Q and more one Q
        if (level.level <= 6 && level.oneMoreQuestion) {
            if (choisedCategory.contains(level.category))
                getRandomNumber();
        } else if (level.level > 6) {
            level.category = choisedCategory.get(0);
            choisedCategory.remove(0);
        }
        // Log.d("Elsawaf", ""+ n);
        return level.category;
    }

    private void getCategory(int n) {
        roulette.rotateTo(n, this);
    }

    @Override
    public void onRotationStart() {
		/*
		 * if (level.level > 6) question = level.nextLast6Question(); else
		 */
        question = nextQuestion();
    }

    private QuestionDetails nextQuestion() {
        String categoryName = level.getCategoryNameByNumber(level.category);
		/*
		 * if (level.level > 6){ level.oneMoreQuestion = true; }
		 */
        return new QuestionDetails(categoryName, level.level, this);

    }

    @Override
    public void onRotationEnd() {
        showQuestionViewes();
    }

    protected void updateCategoryBackground() {
        // TODO Auto-generated method stub
        Animation categoryAnimtion = AnimationUtils.loadAnimation(Question.this, R.anim.slide_in);
        txtCategory.startAnimation(categoryAnimtion);
        if (level.category == 0) {
            rootLayout
                    .setBackgroundResource(R.drawable.spaingame_questionbk_science);
            txtCategory.setText("Science");
            imgCategory.setBackgroundResource(R.drawable.science_logo);
        } else if (level.category == 1) {
            rootLayout
                    .setBackgroundResource(R.drawable.spaingame_questionbk_history);
            txtCategory.setText("History");
            imgCategory.setBackgroundResource(R.drawable.history);
        } else if (level.category == 2) {
            rootLayout
                    .setBackgroundResource(R.drawable.spaingame_questionbk_sports);
            txtCategory.setText("Sports");
            imgCategory.setBackgroundResource(R.drawable.sports);
        } else if (level.category == 3) {
            rootLayout
                    .setBackgroundResource(R.drawable.spaingame_questionbk_geo);
            txtCategory.setText("Geography");
            imgCategory.setBackgroundResource(R.drawable.geography);
        } else if (level.category == 4) {
            rootLayout
                    .setBackgroundResource(R.drawable.spaingame_questionbk_art);
            txtCategory.setText("Art");
            imgCategory.setBackgroundResource(R.drawable.art);
        } else {
            rootLayout
                    .setBackgroundResource(R.drawable.spaingame_questionbk_literature);
            txtCategory.setText("Literature");
            imgCategory.setBackgroundResource(R.drawable.literature);
        }
    }

    public void updateTxtLevel(TextView txtLevel) {
        if (level.level == 1) {
            txtLevel.setText("One");
        } else if (level.level == 2) {
            txtLevel.setText("Two");
        } else if (level.level == 3) {
            txtLevel.setText("Three");
        } else if (level.level == 4) {
            txtLevel.setText("Four");
        } else if (level.level == 5) {
            txtLevel.setText("Five");
        } else if (level.level == 6) {
            txtLevel.setText("Six");
        }

    }

    public void setQuestionDetailsOnScreen() {
        alarmTimer = new AlarmTimer(11000, 1000, this);

        setQuestionData();

        startTimer();

        if (level.oneMoreQuestion) {
            floatyIconFrame.setVisibility(View.INVISIBLE);
            jockerIconFrame.setVisibility(View.INVISIBLE);
        } else {
            JackpotApplication.numberOfFloatyUsed = 0;
            JackpotApplication.numberOfjokerUsed = 0;
            updateJocker();
            updateFloaty();
        }
    }

    public void setQuestionData() {
        txtQuestion.setText(question.getQuestion());
        txtAnswer1.setText(question.getAllChoices()[0]);
        txtAnswer2.setText(question.getAllChoices()[1]);
        txtAnswer3.setText(question.getAllChoices()[2]);
        txtAnswer4.setText(question.getAllChoices()[3]);
        //set question counter
        question_counter.setText((level.numberOfAnswers + 1) + "/" + level.numberOfQuestions);
    }

    public void updateJocker() {
        jockerIcon.setClickable(true);

        if (JackpotApplication.jokerAllowed == 0) {
            ((TextView) findViewById(R.id.txtJoker)).setText(""
                    + (JackpotApplication.jokerHas));
            jockerIcon.setClickable(false);
            jockerIconFrame.setAlpha(.5f);
            jockerIconFrame.setVisibility(View.VISIBLE);
            return;
        } else {
            //jockerIcon.setOnClickListener(jockerClickListener);
            jockerIconFrame.setAlpha(1f);
            ((TextView) findViewById(R.id.txtJoker)).setText(""
                    + (JackpotApplication.jokerHas));
            jockerIconFrame.setVisibility(View.VISIBLE);
            return;
        }

    }

    public void updateFloaty() {
        floatyIcon.setClickable(true);
        floatyIconFrame.setAlpha(1f);
        if (JackpotApplication.floatyAllowed == 0) {
            ((TextView) findViewById(R.id.txtFloaty)).setText(""
                    + JackpotApplication.floatyHas);
            floatyIcon.setClickable(false);
            floatyIconFrame.setAlpha(0.5f);
            floatyIconFrame.setVisibility(View.VISIBLE);
            return;
        } else {
            ((TextView) findViewById(R.id.txtFloaty)).setText(""
                    + JackpotApplication.floatyHas);
            floatyIconFrame.setVisibility(View.VISIBLE);
            return;
        }

    }

    public void jockerUpdateUIAfterGetAnswer() {
        //level.newScore = level.level * 5;
        //level.numberOfAnswers++;
        updateTotalScore();

        /*if (level.numberOfAnswers == level.numberOfQuestions) {
            level.oneMoreQuestion = true;
        }*/

        rightAnswer();
        //animN = ANIMATION_JOCKER;
        animN = ANIMATION_TRUE;

        deleteAllWrongAnswersAndClose();
//        imgTrue.setBackgroundResource(R.drawable.true_icon255);
//        imgTrue.startAnimation(trueAnimation);
//        imgTrue.setVisibility(View.VISIBLE);
    }

    private void minsTotalScoreAnimation(final int scoreMins) {
        Animation scoreMinsAnimtion = AnimationUtils.loadAnimation(Question.this, R.anim.zoom_in_and_disappear);
        scoreMinsAnimtion.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                txtScoreWord.setText("" + scoreMins);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                level.totalScore += scoreMins;
                txtScore.setText("" + level.totalScore);
                txtScoreWord.setText("Score");
            }
        });
        txtScoreWord.startAnimation(scoreMinsAnimtion);
    }

    private void startTimer() {
        // imgNiddle.setVisibility(View.VISIBLE);
        txtAlarm.setTextColor(Color.RED);
        LevelController.timer = 10;
        txtAlarm.setText("" + LevelController.timer);

        if (timer != null) {
            timer.cancel();
        }
        // re-schedule timer here
        // otherwise, IllegalStateException of
        // "TimerTask is scheduled already"
        // will be thrown
        timer = new Timer();
        startQTask = new StartQuestionTask();
        timer.schedule(startQTask, 2000);
    }

    public void stopTimer() {
        if (alarmTimer != null) {
            alarmTimer.cancel();
        }
        // SoundPlayer.stopSound(Question.this);
        if (mp != null) {
            mp.release();
        }

        txtAnswer1.setOnClickListener(null);
        txtAnswer2.setOnClickListener(null);
        txtAnswer3.setOnClickListener(null);
        txtAnswer4.setOnClickListener(null);
        jockerIcon.setOnClickListener(null);
        floatyIcon.setOnClickListener(null);

        imgNiddle.clearAnimation();
        imgNiddle.setVisibility(View.INVISIBLE);
    }

	/*private void calculateTotalScoreDependOnHelpUsed() {
		level.totalScore -= (JackpotApplication.numberOfjokerUsed * 25)
				- (JackpotApplication.numberOfFloatyUsed * 10);
	}*/

    private void showQuestionViewes() {
        updateCategoryBackground();
        // after roulette out
        Animation out = AnimationUtils.loadAnimation(this,
                AnimationRandom.getOutAnimation());
        out.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                rouletteLayout.setVisibility(View.INVISIBLE);
                rouletteDim.setVisibility(View.INVISIBLE);

                // start animation order 1- niddle. 2- choices. 3- question
                intiNiddleToRotate();
            }
        });
        rouletteLayout.startAnimation(out);
    }

    private void showNextQuestion() {
        level.category = getRandomNumber();
        question = nextQuestion();
        updateCategoryBackground();
        intiNiddleToRotate();
    }

    private void hideQuestionViewes() {
        imgTrue.clearAnimation();
        //make the right logo retirn to show the jackpot logo instead of true icon
        right_logo.clearAnimation();
        //show jackpot logo on right icon (Normal state)
        showJackpotLogoOnRightCircle();
        imgTrue.setVisibility(View.INVISIBLE);
        choicesFrame.setVisibility(View.INVISIBLE);
        questionBackground.setVisibility(View.INVISIBLE);
        bottomBar.setVisibility(View.INVISIBLE);
        floatyIconFrame.setVisibility(View.INVISIBLE);
        jockerIconFrame.setVisibility(View.INVISIBLE);
    }

    public void showRouletteView() {

        //calculateTotalScoreDependOnHelpUsed();
        hideQuestionViewes();

        new GetAdImageTask(this).execute();

        Animation inAnimation = AnimationUtils.loadAnimation(this,
                AnimationRandom.getInAnimation());
        // listener to show save game button
        inAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // show save game button after roulette animation
                Animation btnSaveAnimation = AnimationUtils.loadAnimation(Question.this, R.anim.btn_save_appear);
                btnSaveGame.setVisibility(View.VISIBLE);
                btnSaveGame.startAnimation(btnSaveAnimation);
                btnSaveGame.setOnClickListener(new SaveGameClickListener());
            }
        });
        rouletteLayout.setVisibility(View.VISIBLE);
        rouletteDim.setVisibility(View.VISIBLE);
        btnPushRoulette.setClickable(true);
        updateRouletteText();
        rouletteLayout.startAnimation(inAnimation);
    }

    private void updateRouletteText() {
        if (level.oneMoreQuestion) {
            txtLevelRoulette.setText("");
            txtLevelRouletteTitle.setText("CHEESE QUESTION");
            txtQuestionRouletteTitle.setText("LEVEL");
            txtQuestionRoulette.setText("  " + level.level);

            imgCheeseLeft.setVisibility(View.VISIBLE);
            imgCheeseRight.setVisibility(View.VISIBLE);
        } else {
            txtLevelRoulette.setText("" + level.level);
            txtQuestionRoulette.setText((level.numberOfAnswers + 1) + "/"
                    + level.numberOfQuestions);
        }

    }

    public void wineer() {
        mp = MediaPlayer.create(Question.this, R.raw.when_user_win_the_jackpot);
        mp.start();

        // call update score UI
        updateTotalScore();

        showGetDataScreen();
    }

    public void rightAnswer() {

        // one more Question at end of every level
        if (level.oneMoreQuestion) {
            level.isOneMoreQuestionSolved = true;
            // Save category number
            choisedCategory.add(level.category);
        } else {
            level.numberOfAnswers++;
        }
        updateTotalScore();
        if (level.numberOfAnswers == level.numberOfQuestions) {
            level.oneMoreQuestion = true;
        }

        rightAnswerAnimation();
    }

    public void wrongAnswer() {
        deleteTheSavedGame();
        wrongAnswerAnimation();
        wrongAnswerSound();
    }


    public void rightAnswerAnimation() {

        imgTrue.setImageResource(R.drawable.true_icon255);
        //show company logo in center
        //showCompanyLogoOnCenterAvomenetr();

        animN = ANIMATION_TRUE;
        imgTrue.setVisibility(View.VISIBLE);
        imgTrue.startAnimation(trueAnimation);
        //right_logo.startAnimation(trueAnimation);
    }

    public void updateTotalScore() {
        level.totalScore += level.newScore;
        txtScore.setText("" + level.totalScore);
    }

    public void deleteTheSavedGame() {
        if (isThisSavedGame) {
            ConnectionDetector cd = new ConnectionDetector(Question.this);
            if (cd.isConnectingToInternet()) {
                new SaveGameTask(Question.this, SaveGameTask.DELETE_TASK).execute(Integer.parseInt(JackpotApplication.JACKPOT_ID),
                        1, 0, 0);
            }
        }
    }

    public void wrongAnswerSound() {
        switch (level.level) {
            // switch doesn't accept '|' or operation
            case 1:
                mp = MediaPlayer.create(this, R.raw.when_user_comete_mistake_at_level_1_and_2);
                break;
            case 2:
                mp = MediaPlayer.create(this, R.raw.when_user_comete_mistake_at_level_1_and_2);
                break;
            case 3:
                mp = MediaPlayer.create(this, R.raw.when_user_comete_mistake_at_level_3_and_4);
                break;
            case 4:
                mp = MediaPlayer.create(this, R.raw.when_user_comete_mistake_at_level_3_and_4);
                break;
            case 5:
                mp = MediaPlayer.create(this, R.raw.when_user_comete_mistake_at_level_5_and_6);
                break;
            default:
                mp = MediaPlayer.create(this, R.raw.when_user_comete_mistake_at_level_5_and_6);
                break;
        }
        mp.start();
    }

    public void wrongAnswerAnimation() {
        imgTrue.setImageResource(R.drawable.false_icon);
        //show company logo in center
        //showCompanyLogoOnCenterAvomenetr();

        animN = ANIMATION_FALSE;
        imgTrue.setVisibility(View.VISIBLE);
        imgTrue.startAnimation(trueAnimation);
        //right_logo.startAnimation(trueAnimation);
    }

    private void intiNiddleToRotate() {
        // elsawaf
        progress = 0;
        RotateAnimation rotate = new RotateAnimation((float) (progress),
                (float) (progress) + 2, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(100);
        rotate.setStartOffset(400);
        // rotate.setRepeatCount(Animation.INFINITE);
        // rotate.setRepeatMode(Animation.REVERSE);
        rotate.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SoundPlayer.playSound(Question.this, SoundPlayer.needleSound);
                rotateNiddle(imgNiddle, level.level);
            }
        });
        imgNiddle.startAnimation(rotate);

    }

    private void rotateNiddle(final ImageView niddle, int level) {
        newProgress = level * 50;
        rotation = new RotateAnimation((float) (progress),
                (float) (newProgress), Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotation.setDuration(600);
        rotation.setFillAfter(true);

        rotation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RotateAnimation rotate2 = new RotateAnimation(
                        (float) (progress), (float) (progress) + 2,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotate2.setDuration(100);
                rotate2.setRepeatCount(Animation.INFINITE);
                rotate2.setRepeatMode(Animation.REVERSE);

                niddle.startAnimation(rotate2);

                // *** to continue the animation order
                // if this is n't the levelCompleteScreen
                if (!Question.this.level.isOneMoreQuestionSolved) {
                    SoundPlayer.playSound(Question.this,
                            SoundPlayer.answerSound);
                    choicesFrame.setVisibility(View.VISIBLE);
                    choicesFrame.startAnimation(choicesAnimation);
                    animN = ANIMATION_CHOICES;

                    bottomBar.setVisibility(View.VISIBLE);
                }
            }
        });
        niddle.startAnimation(rotation);
        progress = newProgress;
    }

    public void clearQuestionData() {
        txtQuestion.setText("");
        txtAnswer1.setText("");
        txtAnswer2.setText("");
        txtAnswer3.setText("");
        txtAnswer4.setText("");
    }

    private void deleteAllWrongAnswersAndClose() {
        Thread timer = new Thread() {
            public void run() {
                // delete first wrong answer instantly
                //messageHandler.sendEmptyMessage(0);
                // now loop 3 times to delete the other two wrong answers
                Log.d("elswaf" + getClass().getSimpleName(), "" + question.getAllWrongAnswers().size());
                // we need this count because the size with decrease every loop
                int count = question.getAllWrongAnswers().size();
                for (int i = 0; i <= count; i++) {
                    try {
                        sleep(700);
                        // reach to end so wait until user see the right answer
                        // then complete
						/*if (i == 2)
							sleep(500);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        messageHandler.sendEmptyMessage(0);
                    }
                }
            }
        };
        timer.start();
    }

//    private boolean deleteOneWrongAnswer() {
//        Log.d("elswaf" + getClass().getSimpleName(), "delete one answer");
//        if (!TextUtils.isEmpty(txtAnswer1.getText())
//                && !txtAnswer1.getText().equals(question.getAnswer())) {
//            question.getAllWrongAnswers().remove(txtAnswer1.getText());
//            txtAnswer1.setText("");
//            return true;
//        } else if (!TextUtils.isEmpty(txtAnswer2.getText())
//                && !txtAnswer2.getText().equals(question.getAnswer())) {
//            question.getAllWrongAnswers().remove(txtAnswer2.getText());
//            txtAnswer2.setText("");
//            return true;
//        } else if (!TextUtils.isEmpty(txtAnswer3.getText())
//                && !txtAnswer3.getText().equals(question.getAnswer())) {
//            question.getAllWrongAnswers().remove(txtAnswer3.getText());
//            txtAnswer3.setText("");
//            return true;
//        } else if (!TextUtils.isEmpty(txtAnswer4.getText())
//                && !txtAnswer4.getText().equals(question.getAnswer())) {
//            question.getAllWrongAnswers().remove(txtAnswer4.getText());
//            txtAnswer4.setText("");
//            return true;
//        } else {
//            return false;
//        }
//    }

    public boolean deleteRandomWrongAnswer() {
        if (question.getAllWrongAnswers().size() < 1) {
            return false;
        } else {
            String wrongAnswer = question.getAllWrongAnswers().get(0);
            if (!TextUtils.isEmpty(txtAnswer1.getText())
                    && txtAnswer1.getText().equals(wrongAnswer)) {
                txtAnswer1.setText("");
                question.getAllWrongAnswers().remove(wrongAnswer);
            } else if (!TextUtils.isEmpty(txtAnswer2.getText())
                    && txtAnswer2.getText().equals(wrongAnswer)) {
                txtAnswer2.setText("");
                question.getAllWrongAnswers().remove(wrongAnswer);
            } else if (!TextUtils.isEmpty(txtAnswer3.getText())
                    && txtAnswer3.getText().equals(wrongAnswer)) {
                txtAnswer3.setText("");
                question.getAllWrongAnswers().remove(wrongAnswer);
            } else if (!TextUtils.isEmpty(txtAnswer4.getText())
                    && txtAnswer4.getText().equals(wrongAnswer)) {
                txtAnswer4.setText("");
                question.getAllWrongAnswers().remove(wrongAnswer);
            }
            Log.d("elswaf" + getClass().getSimpleName(), "" + question.getAllWrongAnswers().size());
            return true;
        }
    }

    @Override
    public void onAnimationEnd(Animation arg0) {
        // animation question after showing choices (answer boxes)
        if (animN == ANIMATION_CHOICES) {
            SoundPlayer.playSound(Question.this, SoundPlayer.questionSound);
            questionBackground.startAnimation(qAnimation);
            questionBackground.setVisibility(View.VISIBLE);
            animN = ANIMATION_Question;
        }
        // after question views
        else if (animN == ANIMATION_Question) {
            // call it from onPost task at QuestionDetails
            isQuestionViewsShow = true;
            if (isGetQuestionTaskEnd) {
                setQuestionDetailsOnScreen();
                isQuestionViewsShow = false;
                isGetQuestionTaskEnd = false;
            }
        }
        // after right answer
        else if (animN == ANIMATION_TRUE) { //FIXME I was put here ||animN == ANIMATION_TRUE to solve bug but I can't remember ???
            clearQuestionData();
            // check to show cheese screen before Roulette
            if (level.oneMoreQuestion) {
                // if he answered the one more Q
                if (level.isOneMoreQuestionSolved)
                    showLevelCompleteScreen();
                    // this is the final question
                else if (level.level == 6)
                    showLastCheeseScreen();
                    // this is cheese question
                else
                    showCheeseLevelScreen();
            } else {
                // >>>> this is normal question
                hideQuestionViewes();
                showNextQuestion();
            }
        } /*else if (animN == ANIMATION_JOCKER) {
            //Do the same as right answer for now !!!!
            clearQuestionData();
            // check to show cheese screen before Roulette
            if (level.oneMoreQuestion) {
                // if he answered the one more Q
                if (level.isOneMoreQuestionSolved)
                    showLevelCompleteScreen();
                    // this is the final question
                else if (level.level == 6)
                    showLastCheeseScreen();
                    // this is cheese question
                else
                    showCheeseLevelScreen();
            } else {
                // >>>> this is normal question
                hideQuestionViewes();
                showNextQuestion();
            }
            //Warning
            //deleteAllWrongAnswersAndClose();
        }*/
        // after wrong answer
        else if (animN == ANIMATION_FALSE) {
            deleteAllWrongAnswersAndClose();
        }

    }

    @Override
    public void onAnimationRepeat(Animation arg0) {
    }

    @Override
    public void onAnimationStart(Animation arg0) {
        // after right answer
        if (animN == ANIMATION_TRUE || animN == ANIMATION_JOCKER) {
            txtStar.setText("" + level.newScore);

            SoundPlayer.playSound(Question.this, SoundPlayer.starSound);
            starLayout.setVisibility(View.VISIBLE);
            isAnimationEnd = false;
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator animationScaleXBig = ObjectAnimator.ofFloat(
                    starLayout, "scaleX", 0, 1).setDuration(500);
            ObjectAnimator animationScaleYBig = ObjectAnimator.ofFloat(
                    starLayout, "scaleY", 0, 1).setDuration(500);

            ObjectAnimator animationRotation = ObjectAnimator.ofFloat(
                    starLayout, "rotation", 0, -75, 0).setDuration(700);

            set.play(animationScaleXBig).with(animationScaleYBig)
                    .before(animationRotation);
            set.start();

            set.addListener(new StarAnimatorListener());

        }
    }

    class HideScreenTask extends TimerTask {
        @Override
        public void run() {
            hideGetReadyOrTimeIsUpScreen();
        }

    }

    class TimeIsUpTask extends TimerTask {

        @Override
        public void run() {
            hideGetReadyOrTimeIsUpScreen();
            deleteAllWrongAnswersAndClose();
        }
    }

    class JokerClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            JackpotApplication.jokerAllowed--;
            JackpotApplication.numberOfjokerUsed++;
            //minsTotalScoreAnimation(-25);

            ConnectionDetector cd = new ConnectionDetector(Question.this);
            if (cd.isConnectingToInternet()) {
                if (JackpotApplication.jokerHas > 0) {
                    JackpotApplication.jokerHas--;
                    new HelpTask(Question.this).execute("0", "1");
                }

            }

            updateJocker();

            stopTimer();
            MediaPlayer jockerMP = MediaPlayer.create(Question.this, R.raw.when_user_press_the_joker_or_float);
            jockerMP.start();

        }
    }

    class FloatyClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (JackpotApplication.numberOfFloatyUsed < 3) {
                //minsTotalScoreAnimation(-10);
                MediaPlayer floatMP = MediaPlayer.create(Question.this, R.raw.when_user_press_the_joker_or_float);
                floatMP.start();
                /*// this mean I have the right answer
                if (JackpotApplication.numberOfFloatyUsed > 0) {
                    deleteRandomWrongAnswer();
                } else {*/
                    ConnectionDetector cd = new ConnectionDetector(Question.this);
                    if (cd.isConnectingToInternet()) {
                        //new DecrementFloatsTask().execute(1);
                        new HelpTask(Question.this).execute("1", "0");
                    }
                //}
                JackpotApplication.floatyAllowed--;
                JackpotApplication.floatyHas--;
                JackpotApplication.numberOfFloatyUsed++;

                updateFloaty();
            }
        }
    }

    class StartQuestionTask extends TimerTask {

        @Override
        public void run() {
            // SoundPlayer.playSound(Question.this, SoundPlayer.clockSound);
            mp = MediaPlayer.create(Question.this, R.raw.clock);
            mp.start();

            alarmTimer.start();

            txtAnswer1.setOnClickListener(answerClickListener);
            txtAnswer2.setOnClickListener(answerClickListener);
            txtAnswer3.setOnClickListener(answerClickListener);
            txtAnswer4.setOnClickListener(answerClickListener);
            if (JackpotApplication.jokerAllowed > 0)
                jockerIcon.setOnClickListener(jockerClickListener);
            if (JackpotApplication.floatyAllowed > 0)
                floatyIcon.setOnClickListener(floatyClickListener);
        }

    }

    class PushRouletteClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            int categoryNumber = getRandomNumber();
            getCategory(categoryNumber);
            btnPushRoulette.setClickable(false);

            Animation btnSaveOutAnimation = AnimationUtils.loadAnimation(Question.this, R.anim.btn_save_disappear);
            btnSaveGame.startAnimation(btnSaveOutAnimation);
            btnSaveGame.setVisibility(View.INVISIBLE);
        }

    }

    class SaveGameClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            ConnectionDetector cd = new ConnectionDetector(Question.this);
            if (cd.isConnectingToInternet()) {
                new SaveGameTask(Question.this, SaveGameTask.SAVE_TASK).execute(Integer.parseInt(JackpotApplication.JACKPOT_ID),
                        level.level, level.numberOfAnswers, level.totalScore);
            }
        }
    }

    // Interaction with user answer
    class AnswerClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            stopTimer();
            TextView txtV = (TextView) v;

            // the last question to win
            if (level.level == 6 && level.oneMoreQuestion)
                isLast = "1";

            // run answer task (send 3 params)
            // get status and call suitable method
            ConnectionDetector cd = new ConnectionDetector(Question.this);
            if (cd.isConnectingToInternet()) {
                new AnswerTask(Question.this).execute(txtV.getText().toString(), "" + JackpotApplication.numberOfFloatyUsed,
                        "" + JackpotApplication.numberOfjokerUsed, "" + level.level);
            }

        }
    }

    class StarAnimatorListener implements AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (isAnimationEnd) {
                ObjectAnimator.ofFloat(starLayout, "translationY", -200, 0)
                        .setDuration(1).start();
                starLayout.setVisibility(View.GONE);
            } else {
                // complete animation
                AnimatorSet set = new AnimatorSet();

                ObjectAnimator animationScaleXSmall = ObjectAnimator.ofFloat(
                        starLayout, "scaleX", 1, 0).setDuration(500);
                ObjectAnimator animationScaleYSmall = ObjectAnimator.ofFloat(
                        starLayout, "scaleY", 1, 0).setDuration(500);
                ObjectAnimator animationTransationY = ObjectAnimator.ofFloat(
                        starLayout, "translationY", 0, -200).setDuration(500);

                set.play(animationScaleXSmall).with(animationScaleYSmall)
                        .with(animationTransationY);
                set.addListener(new StarAnimatorListener());
                set.start();
                isAnimationEnd = true;
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }
    }

}