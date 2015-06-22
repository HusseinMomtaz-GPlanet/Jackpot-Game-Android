package com.AppRocks.jackpot.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.JackpotParameters;
import com.AppRocks.jackpot.R;
import com.AppRocks.jackpot.services.MusicService;
import com.AppRocks.jackpot.util.ConnectionDetector;
import com.AppRocks.jackpot.util.SoundPlayer;
import com.AppRocks.jackpot.view.InfinitePagerAdapter;
import com.AppRocks.jackpot.webservice.GetAllJackpotsTask;
import com.AppRocks.jackpot.webservice.GetJackpotDetailsForWinnerTask;
import com.AppRocks.jackpot.webservice.GetJackpotDetailsTask;
import com.AppRocks.jackpot.webservice.JackpotServicesClient;
import com.AppRocks.jackpot.webservice.PlayJackpotTask;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

@EActivity
public class Main extends BaseActivity {

    public JackpotParameters p;
    @ViewById(R.id.viewPager)
    public ViewPager vp; // Reference to class to swipe views

    /*
     * @ViewById RoundedImageView imgJAckBot;
     */
    public PagerAdapter pagesAdapter;
    public ArrayList<HashMap<String, String>> jackpotsList;
    @ViewById
    LinearLayout Rlcontainer;
    @ViewById
    ImageView img3StageProgress;
    @ViewById
    Button btnPlay;
    // @ViewById
    ImageButton btnRight;
    // @ViewById
    ImageButton btnLeft;
    @ViewById
    ImageButton btn_continue_game;
    /*
     * String[] IDs; String[] imgURLs; int[] difficulty;
     */
    @ViewById
    RatingBar ratingBar1;
    @ViewById
    TextView txtPlayFree;
    Bitmap bitmap;
    LayoutInflater inflater; // Used to create individual pages
    DisplayImageOptions options;
    boolean playMusicContinue;
    ImageLoaderConfiguration config;
    private View page;
    private ImageLoader imageLoader;
    private boolean isJackpotFree = true;
    private int jackpotDifficulty;
    private ImageButton btnPlayMusic;


    // Scale the given view, its contents, and all of its children by the given
    // factor.
    public static void scaleViewAndChildren(View root, float scale) {
        // Retrieve the view's layout information
        ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
        // Scale the view itself
        if (layoutParams.width != ViewGroup.LayoutParams.FILL_PARENT
                && layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
            layoutParams.width *= scale;
        }
        if (layoutParams.height != ViewGroup.LayoutParams.FILL_PARENT
                && layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            layoutParams.height *= scale;
        }
        // If this view has margins, scale those too
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginParams.leftMargin *= scale;
            marginParams.rightMargin *= scale;
            marginParams.topMargin *= scale;
            marginParams.bottomMargin *= scale;
        }
        // Set the layout information back into the view
        root.setLayoutParams(layoutParams);
        // Scale the view's padding
        root.setPadding((int) (root.getPaddingLeft() * scale),
                (int) (root.getPaddingTop() * scale),
                (int) (root.getPaddingRight() * scale),
                (int) (root.getPaddingBottom() * scale));
        // If the root view is a TextView, scale the size of its text
        if (root instanceof TextView) {
            TextView textView = (TextView) root;
            textView.setTextSize(textView.getTextSize() * scale);
        }
        // If the root view is a ViewGroup, scale all of its children
        // recursively
        if (root instanceof ViewGroup) {
            ViewGroup groupView = (ViewGroup) root;
            for (int cnt = 0; cnt < groupView.getChildCount(); ++cnt)
                scaleViewAndChildren(groupView.getChildAt(cnt), scale);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // overridePendingTransition(R.anim.hold, R.anim.push_out_to_left);
        setContentView(R.layout.main);
        p = new JackpotParameters(this);
        SoundPlayer.initSounds(getApplicationContext());
    }

    @Override
    protected void onStart() {
        initUILConfig();
        super.onStart();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!playMusicContinue) {
            stopService(new Intent(getBaseContext(), MusicService.class));
        }

    }

    @AfterViews
    void getAllJackPotsInfo() {
        jackpotsList = new ArrayList<HashMap<String, String>>();
        ConnectionDetector cd = new ConnectionDetector(Main.this);
        if (cd.isConnectingToInternet()) {
            new GetAllJackpotsTask(this).execute();
            preparePageViewer();
        }
    }

    private void preparePageViewer() {
        initUILConfig();
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pagesAdapter = new InfinitePagerAdapter(new MyPagesAdapter());

        btnRight = (ImageButton) findViewById(R.id.btnRight);
        btnLeft = (ImageButton) findViewById(R.id.btnLeft);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vp.getCurrentItem()+1<jackpotsList.size())
                   vp.setCurrentItem(vp.getCurrentItem()+1);
            }
        });
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vp.getCurrentItem()>0)
                    vp.setCurrentItem(vp.getCurrentItem()-1);
            }
        });
        vp.setOnTouchListener(new ButtonsTouchListener());

        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String has_winner = jackpotsList.get(vp.getCurrentItem()).get(
                        JackpotApplication.TAG_HASWINNER);
                String is_blocked = jackpotsList.get(vp.getCurrentItem()).get(
                        JackpotApplication.TAG_Bloked);
                ImageView status_image=(ImageView)page.findViewById(R.id.status_image);

                if(Integer.parseInt(has_winner)!=0){
                    status_image.setVisibility(View.VISIBLE);
                    status_image.setImageResource(R.drawable.trophy);
                    btnPlay.setText("Winner");
                    btnPlay.setClickable(true);
                    btnPlay.setAlpha(1.0f);
                }else  if(Integer.parseInt(is_blocked)!=0){
                    status_image.setVisibility(View.VISIBLE);
                    status_image.setImageResource(R.drawable.lock);
                    btnPlay.setText("Locked");
                    btnPlay.setClickable(false);
                    btnPlay.setAlpha(0.7f);
                }else{
                    //clear image
                    status_image.setImageDrawable(null);
                    status_image.setVisibility(View.GONE);
                    btnPlay.setText("Play");
                    btnPlay.setClickable(true);
                    btnPlay.setAlpha(1.0f);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initUILConfig() {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.

        if (config != null) {
            return;
        }
        config = new ImageLoaderConfiguration.Builder(
                Main.this).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .threadPoolSize(1)
                .memoryCache(new WeakMemoryCache())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                // .showImageForEmptyUri(R.drawable.ic_empty)
                // .showImageOnFail(R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        imageLoader = ImageLoader.getInstance();
    }

    @Click
    void btn_continue_game() {
        Intent continueGame = new Intent(this, Question.class);
        continueGame.putExtra("savedGame", true);
        startActivity(continueGame);
    }

    @Click
    void btnPlay() {
        playMusicContinue = true;
        ConnectionDetector cd = new ConnectionDetector(Main.this);
        if (cd.isConnectingToInternet()) {

            JackpotApplication.JACKPOT_ID = jackpotsList.get(
                    vp.getCurrentItem()).get(JackpotApplication.TAG_ID);
            JackpotApplication.JACKPOT_DIFFICULTY = jackpotDifficulty;

            String has_winner = jackpotsList.get(vp.getCurrentItem()).get(
                    JackpotApplication.TAG_HASWINNER);
            String is_blocked = jackpotsList.get(vp.getCurrentItem()).get(
                    JackpotApplication.TAG_Bloked);

            if(Integer.parseInt(has_winner)!=0){
                //Get winner details from server and show winner screen
                new GetJackpotDetailsForWinnerTask(Main.this).execute();

                return;
             }else  if(Integer.parseInt(is_blocked)!=0){
                //show user he is locked
                Toast.makeText(Main.this,"This jackpot is locked",Toast.LENGTH_LONG).show();
                return;
            }

            // save last jackpot id and difficulty to use them in jackpot activity at onStart()
            //and in Question onCreate()
            p.setString(JackpotApplication.JACKPOT_ID, JackpotApplication.PREF_LAST_JACKPOT_ID);
            p.setInt(JackpotApplication.JACKPOT_DIFFICULTY, JackpotApplication.PREF_LAST_JACKPOT_DIFFICULTY);


            new PlayJackpotTask(this).execute();

            if (isJackpotFree) {
                JackpotApplication.isPlayFreeJackpot = true;
                boolean isRegisterForFree = p.getBoolean(
                        JackpotApplication.PREF_IS_REGISTER_FREE, false);
                //boolean isRegisterByFB = jackpotParams.getBoolean("isUserRegisterOnService", false);
                if (isRegisterForFree) {    //|| isRegisterByFB) {
                    // First call playRegister task
                    // TODO moved it to jackpot onStart
                    // new PlayJackpotTesk().execute();

                    // then go to play
                    goToPlay();
                } else {
                    askNicknameToRegister();
                }
            } else {
                // this jackpot is premium (need to login with FB)
                JackpotApplication.isPlayFreeJackpot = false;

				/*
				 * LevelController.PREF_JACKPOT_ID = IDs[vp.getCurrentItem()];
				 * jackpotParams.setInt(difficulty[vp.getCurrentItem()],
				 * LevelController.PREF_DIFFICULTY +
				 * LevelController.PREF_JACKPOT_ID);
				 */

                // previously was need to go login screen
                Intent i = new Intent(Main.this, Jackpot_.class);
                i.putExtra("jackpot", true);
                // i.putExtra("id",
                // jackpotsList.get(vp.getCurrentItem()).get(JackpotApplication.TAG_ID));
                startActivity(i);
            }
        }
    }

    private void askNicknameToRegister() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        // alert.setTitle("Register");
        alert.setMessage("Your Nickname:");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String nickname = input.getText().toString();
                // Do something with value!
                saveUserNicknameAndRegister(nickname);
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

        alert.show();
    }

    protected void saveUserNicknameAndRegister(String nickname) {
        // save name at preferences
        p.setString(nickname, JackpotApplication.PREF_NICKNAME);
        // register user online with NickName & device ID
        ConnectionDetector cd = new ConnectionDetector(Main.this);
        if (cd.isConnectingToInternet()) {
            new RegisterFreeTask().execute(nickname);

            // then call playRegister task
            // TODO moved it to jackpot onStart
            // new PlayJackpotTesk().execute();
        }

        // then go to play
        goToPlay();
    }

    private void goToPlay() {
        startActivity(new Intent(Main.this, Question.class));
    }

    @Override
    protected void onResume() {
        overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            // scaleContents(Rlcontainer);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Click
    void imgJAckBot() {
        // startActivity(new Intent(this, FullscreenDemoActivity.class));
    }

    private void scaleContents(View rootView) {
        // Compute the scaling ratio

        // float xScale = (float) container.getWidth() / rootView.getWidth();
        // float yScale = (float) container.getHeight() / rootView.getHeight();

        float xScale = (float) getResources().getDisplayMetrics().widthPixels
                / rootView.getWidth();
        float yScale = (float) getResources().getDisplayMetrics().heightPixels
                / rootView.getHeight();

        float scale = Math.min(xScale, yScale);
        // Scale our contents
        scaleViewAndChildren(rootView, scale);
    }

    class MyPagesAdapter extends PagerAdapter {

        // 1 is free
        // 1 is active
        @Override
        public int getCount() {
            // Return total pages, here one for each data item
            return jackpotsList.size();
        }

        // Create the given page (indicated by position)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            page = inflater.inflate(R.layout.page, null);
            // ((TextView)page.findViewById(R.id.textMessage)).setText(jackpotsList.get(position).get(JackpotApplication.TAG_ID));
            RoundedImageView imgView = (RoundedImageView) page
                    .findViewById(R.id.imgJAckBot);
            final ProgressBar spinner = (ProgressBar) page
                    .findViewById(R.id.loading);


            String img = jackpotsList.get(position).get(
                    JackpotApplication.TAG_IMAGE);
            String imgURL = JackpotApplication.BASE_URL
                    + img.substring(img.indexOf("uploads"));
            if (ImageLoader.getInstance() != null) {
                imageLoader.displayImage(imgURL, imgView, options,
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
                                Toast.makeText(Main.this, message,
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
            // Add the page to the front of the queue
            ((ViewPager) container).addView(page, 0);
            return page;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // See if object from instantiateItem is related to the given view
            // required by API
            return arg0 == (View) arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
            object = null;
        }


        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            String difficulty = jackpotsList.get(vp.getCurrentItem()).get(
                    JackpotApplication.TAG_DIFFICULTY);
            jackpotDifficulty = Integer.parseInt(difficulty);
            ratingBar1.setRating(jackpotDifficulty);
            isJackpotFree = jackpotsList.get(vp.getCurrentItem())
                    .get(JackpotApplication.TAG_FREE).contains("1");
            if (isJackpotFree) {
                txtPlayFree.setVisibility(View.VISIBLE);
            } else {
                txtPlayFree.setVisibility(View.INVISIBLE);
            }


        }
    }

    class ButtonsTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideButtons();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                showButtons();
            }
            return false;
        }

        private void showButtons() {
            btnRight.setVisibility(View.VISIBLE);
            btnLeft.setVisibility(View.VISIBLE);
        }

        private void hideButtons() {
            btnRight.setVisibility(View.INVISIBLE);
            btnLeft.setVisibility(View.INVISIBLE);
        }

    }

    class RegisterFreeTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            JSONObject j = JackpotServicesClient.getInstance().registerFree(
                    params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            p.setBoolean(true, JackpotApplication.PREF_IS_REGISTER_FREE);

        }

    }

}
