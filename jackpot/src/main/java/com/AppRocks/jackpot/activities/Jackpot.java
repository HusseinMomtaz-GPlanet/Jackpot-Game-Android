package com.AppRocks.jackpot.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.JackpotParameters;
import com.AppRocks.jackpot.R;
import com.AppRocks.jackpot.activities.youtube.FullscreenDemoActivity2;
import com.AppRocks.jackpot.services.MusicService;
import com.AppRocks.jackpot.util.ConnectionDetector;
import com.AppRocks.jackpot.webservice.GetJackpotDetailsTask;
import com.AppRocks.jackpot.webservice.PlayJackpotTask;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

@EActivity(R.layout.jackpot)
public class Jackpot extends BaseActivity {

    MediaPlayer mp;
	/*@ViewById
    TextView txtSignOut;*/
    private StartAppAd startAppAd = new StartAppAd(this);

    public String desc;
    @ViewById
    public ImageView imgLogo;
    @ViewById
    public RoundedImageView imgJAckBot;
    @ViewById
    public Button btnSeeTheVideo;
    @ViewById
    public ImageView imgProgrWorld;
    @ViewById
    public View viewBGWorld;
    /*@ViewById
    Button btnYouCanPlay;*/
    @ViewById
    public TextView txtCompanyName;
    @ViewById
    public TextView txtBrandName;
    @ViewById
    public TextView txtDesc;
    @ViewById
    public Button btnDescComplete;
    ImageButton btn_continue_game;
    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public ProgressBar spinner;
    public boolean playMusicContinue;
    public JSONObject jackpotJsonDetails;
    JackpotParameters jackParams;
    @ViewById
    ImageView img3StageProgress;
    int progressYou = 0;
    int progressWorld = 0;
    float newProgress = 0;
    ScaleAnimation animationProgress;
    int count;
    boolean isProgressYou100;
    boolean isProgressWorld100;
    @ViewById
    ImageView imgProgrYou;
    @ViewById
    View dimView;
    WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private View getReadyView;
    public static int LOGO_APPEARANCE;
    public  String reamining_winners;
    @Click
    void btnSeeTheVideo() {
        if (progressYou == 100) {
            if (progressWorld == 100) {

                new PlayJackpotTask(this).execute();

            } else {
                Toast.makeText(Jackpot.this, "You must wait until world watch reach to 100%", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (progressYou == 0) {
                showClickLogoAlertScreen();
            } else {
                startActivity(new Intent(getApplicationContext(), FullscreenDemoActivity2.class));
                playMusicContinue = false;
            }
        }
    }


    @Click
    void btnDescComplete() {
        showInfoScreen();
    }

    private void showInfoScreen() {
        dimView.setVisibility(View.VISIBLE);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);//context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getReadyView = inflator.inflate(R.layout.desc_screen, null);

        int windowHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.windowAnimations = R.style.screen_animation;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = windowHeight / 8;
        windowManager.addView(getReadyView, params);

        Button OkBtn = (Button) getReadyView.findViewById(R.id.btnTryAgain);
        TextView cheeseLevelNumberTxt = (TextView) getReadyView.findViewById(R.id.txt_you_lose);
        TextView cheeseLevelInfoTxt = (TextView) getReadyView.findViewById(R.id.txt_cheese_info);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "BigSoftie-Fat.otf");
        cheeseLevelNumberTxt.setTypeface(font);
        cheeseLevelInfoTxt.setTypeface(font);
        OkBtn.setTypeface(font);

        cheeseLevelInfoTxt.append(desc);

        OkBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // elsawaf
                if (getReadyView != null) {
                    windowManager.removeView(getReadyView);
                    getReadyView = null;
                    dimView.setVisibility(View.INVISIBLE);
                }
            }
        });


    }


    void btn_continue_game() {
        Intent continueGame = new Intent(this, Question.class);
        continueGame.putExtra("savedGame", true);
        startActivity(continueGame);
    }

    private void showClickLogoAlertScreen() {
        dimView.setVisibility(View.VISIBLE);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);//context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getReadyView = inflator.inflate(R.layout.click_logo_screen, null);

        int windowHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();

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
        TextView clickLogoTxt = (TextView) getReadyView.findViewById(R.id.click_logo_alert);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "BigSoftie-Fat.otf");
        clickLogoTxt.setTypeface(font);

        Button OkBtn = (Button) getReadyView.findViewById(R.id.btnTryAgain);

        OkBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // elsawaf
                if (getReadyView != null) {
                    windowManager.removeView(getReadyView);
                    getReadyView = null;
                    dimView.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(getApplicationContext(), FullscreenDemoActivity2.class));
                    playMusicContinue = false;

                }
            }
        });
    }

    //this method invoke automatically after init views
    @AfterViews
    void startAnimation() {
        Animation animation2 = new ScaleAnimation(1, 1, (float) ((100.0 - progressYou) / 100.0), (float) ((100.0 - progressWorld - 2) / 100.0), Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.0);
        animation2.setDuration(100);
        animation2.setRepeatCount(Animation.INFINITE);
        animation2.setRepeatMode(Animation.REVERSE);
        imgProgrYou.startAnimation(animation2);

        spinner = (ProgressBar) findViewById(R.id.loading);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "103654352", "206877549", true);
        initUILConfig();
        jackParams = new JackpotParameters(this);

    }


    @Override
    protected void onStart() {
        JackpotApplication.currentLanguage="sp";
        btn_continue_game=(ImageButton)findViewById(R.id.btn_continue_game);
        btn_continue_game.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_continue_game();
            }
        });

        if (TextUtils.isEmpty(JackpotApplication.JACKPOT_ID)) {
            JackpotApplication.JACKPOT_ID = jackParams.getString(JackpotApplication.PREF_LAST_JACKPOT_ID);
        }
        if (TextUtils.isEmpty(JackpotApplication.TOKEN_ID)) {
            JackpotApplication.TOKEN_ID = jackParams.getString(JackpotApplication.PREF_USER_TOKEN);
        }
        ConnectionDetector cd = new ConnectionDetector(Jackpot.this);
        if (cd.isConnectingToInternet()) {
            new GetJackpotDetailsTask(Jackpot.this).execute();
        }

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    Jackpot.this).threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                    .threadPoolSize(1)
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .writeDebugLogs() // Remove for release app
                    .build();
            // Initialize ImageLoader with configuration.
            ImageLoader.getInstance().init(config);
        }

        super.onStart();
    }


    private void showOwlPopup() {

        final Dialog pop_up = new Dialog(this,R.style.DialogSlideAnim);
        pop_up.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pop_up.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getReadyView = inflator.inflate(R.layout.owl_popup, null);
        pop_up.setContentView(getReadyView);

        Typeface font = Typeface.createFromAsset(getAssets(),
                "BigSoftie-Fat.otf");
        TextView remaining_textField = (TextView) getReadyView
                .findViewById(R.id.remaining_winners);
        remaining_textField.setTypeface(font);
        remaining_textField.setText(this.reamining_winners+"");
        Button exit_button = (Button) getReadyView
                .findViewById(R.id.exit_button);
        exit_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                pop_up.dismiss();
            }
        });

        pop_up.show();
        mp = MediaPlayer.create(this, R.raw.owl);
        mp.start();
    }



    @Override
    public void onResume() {
        super.onResume();
        startAppAd.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        startAppAd.onPause();
        if (!playMusicContinue) {
            stopService(new Intent(getBaseContext(), MusicService.class));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playMusicContinue = true;
        Intent i = new Intent(this, Main_.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void initUILConfig() {


        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        imageLoader = ImageLoader.getInstance();
    }


    public void animateThermometerYou(int n) {
        // to update img3stage
        if (n >= 100) {
            isProgressYou100 = true;
            n = 100;
        }
        // animation
        newProgress = (float) n / 100;
        animationProgress = new ScaleAnimation(1, 1, (float) ((100.0 - progressYou) / 100.0), 1.0f - newProgress, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.0);
        animationProgress.setDuration(500);
        animationProgress.setFillAfter(true);
        if (n != 100)
            animationProgress.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation animation2 = new ScaleAnimation(1, 1, (float) ((100.0 - progressYou) / 100.0), (float) ((100.0 - progressYou - 2) / 100.0), Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.0);
                    animation2.setDuration(100);
                    animation2.setRepeatCount(Animation.INFINITE);
                    animation2.setRepeatMode(Animation.REVERSE);

                    imgProgrYou.startAnimation(animation2);
                }
            });

        imgProgrYou.startAnimation(animationProgress);
        progressYou = n;
    }

    //I tried to make them one method but this affect on animation behavior
    // *** now this call only if you thermometer 100%
    public void animateThermometerWorld(int n) {
        //to update img3stage
        if (n >= 100) {
            isProgressWorld100 = true;
            n = 100;
        }
        // animation
        newProgress = (float) n / 100;
        animationProgress = new ScaleAnimation(1, 1, (float) ((100.0 - progressWorld) / 100.0), 1.0f - newProgress, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.0);
        animationProgress.setDuration(500);
        animationProgress.setFillAfter(true);
        if (n != 100)
            animationProgress.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation animation2 = new ScaleAnimation(1, 1, (float) ((100.0 - progressWorld) / 100.0), (float) ((100.0 - progressWorld - 2) / 100.0), Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.0);
                    animation2.setDuration(100);
                    animation2.setRepeatCount(Animation.INFINITE);
                    animation2.setRepeatMode(Animation.REVERSE);

                    imgProgrWorld.startAnimation(animation2);
                }
            });

        imgProgrWorld.startAnimation(animationProgress);
        progressWorld = n;

        updateImg3StageProgress();
    }

    private void updateImg3StageProgress() {
        if (isProgressYou100 && isProgressWorld100) {
            img3StageProgress.setBackgroundResource(R.drawable.traffic_bar_green);
            if(JackpotApplication.currentLanguage=="en")
                btnSeeTheVideo.setBackgroundResource(R.drawable.jackpot_play);
            else
                btnSeeTheVideo.setBackgroundResource(R.drawable.play_button_sp);
        } else {
            img3StageProgress.setBackgroundResource(R.drawable.traffic_bar_yellow);
            if(JackpotApplication.currentLanguage=="en")
                    btnSeeTheVideo.setBackgroundResource(R.drawable.jackpot_wait_video);
            else
                btnSeeTheVideo.setBackgroundResource(R.drawable.you_must_wait_sp);
        }
    }

    //Get jackpot data from get details service , we will need to pass it to questions details
    public void onJackpotDataRetrieved(JSONObject jackpotDetailsJSON) {
        jackpotJsonDetails = jackpotDetailsJSON;
        try {
            //set the count of company logo appearance to use in video screen
            LOGO_APPEARANCE=jackpotDetailsJSON.getInt("logo_videos");
            reamining_winners=jackpotDetailsJSON.getString("remaining_winners");

            //if(!reamining_winners.equalsIgnoreCase("0"))
               showOwlPopup();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
