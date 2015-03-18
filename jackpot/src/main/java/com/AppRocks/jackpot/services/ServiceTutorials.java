package com.AppRocks.jackpot.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.R;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceTutorials extends Service implements OnClickListener {
    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    Animation animEnterHand;
    Animation animEnterCircle;
    Animation animMoveHand;
    Animation animScaleLine;
    Animation animDisappear;
    ImageView imglblazkarType;
    ImageView imglblazkarIcon;
    View rowView;
    WindowManager localWindowManager;
    WindowManager.LayoutParams ll_lp;
    LayoutInflater inflater;
    Timer timer;
    MyTimerTask myTimerTask;
    private String TAG = "ServiceTutorials";
    private ImageView btnClickMe;
    private int count;
    private ObjectAnimator oX;
    private ObjectAnimator oAlpha;
    private ObjectAnimator oY;
    private boolean signOpposite;
    private boolean isIconClick = false;
    private Animation alphaA;

    private WindowManager.LayoutParams params;
    private Handler messageHandler = new Handler() {

        public void handleMessage(Message msg) {
            // update your view here
            int x = getRandomNumber(true);
            int y = getRandomNumber(false);
            params.x = x;
            params.y = y;
            btnClickMe.setVisibility(View.VISIBLE);
            localWindowManager.addView(rowView, params);
            startAnimations();
        }
    };
    private int xMax;
    private int yMax;

    @Override
    public void onCreate() {
        super.onCreate();

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        localWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //signOpposite = true;
        xMax = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        yMax = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay()
                .getHeight();
        Log.w("elsawafMAX", xMax + ":" + yMax);

        //Log.d(TAG, "service created");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //Log.d(TAG, "service Started");
        super.onStart(intent, startId);

        // s = new SideBarParameters(this);
        // if service is restarted
        try {
            localWindowManager.removeView(rowView);
        } catch (Exception e) {
            Log.d("Main", "ERROR :" + e.toString());
        }

        rowView = inflater.inflate(R.layout.tutrials_left, null, false);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        int x = getRandomNumber(true);
        int y = getRandomNumber(false);
        params.x = x;
        params.y = y;

		/*// Just a sample layout parameters.
        ll_lp = new WindowManager.LayoutParams();
		ll_lp.format = PixelFormat.TRANSLUCENT;
		ll_lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		ll_lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		ll_lp.gravity = Gravity.TOP | Gravity.LEFT;

		ll_lp.gravity = true ? (Gravity.RIGHT | Gravity.CENTER_VERTICAL)
				: (Gravity.LEFT | Gravity.CENTER_VERTICAL);

		int x = getRandomNumber(true);
		int y = getRandomNumber(false);
		ll_lp.x = x;
		ll_lp.y = y;
		Log.w("elsawafXY", x + ":" + y);

		// This one is necessary.
		ll_lp.type = WindowManager.LayoutParams.TYPE_PHONE;

		// Play around with these two.
		ll_lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		ll_lp.flags = ll_lp.flags
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		ll_lp.flags = ll_lp.flags
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

		// >>>>elasawf<<<<<   Have the system blur any windows behind this one.
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
	            WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	    ll_lp.flags = ll_lp.flags
				| WindowManager.LayoutParams.FLAG_BLUR_BEHIND;

		// if(s.getPostionUCB()=='u')
		// ll_lp.gravity = ll_lp.gravity|Gravity.TOP;
		// else if(s.getPostionUCB()=='c')
		//     ll_lp.gravity = ll_lp.gravity | Gravity.CENTER_VERTICAL;
		// else if(s.getPostionUCB()=='b')
		// ll_lp.gravity = ll_lp.gravity|Gravity.BOTTOM;
*/
        localWindowManager.addView(rowView, params);

        initUILConfig();
        findViews();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        try {
            localWindowManager.removeView(rowView);
        } catch (Exception e) {
            Log.d("ServiceTutorials", "ERROR :" + e.toString());
        }

        //Log.d(TAG, "ServiceTutorials destroyed");
    }

    private void initUILConfig() {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).threadPriority(Thread.NORM_PRIORITY - 2)
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
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                        //.imageScaleType(ImageScaleType.EXACTLY)
                        //.bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        imageLoader = ImageLoader.getInstance();
    }

    // generate random numbers 'y' from 100 to 250
    // 'x' from 150 to 350
    // NOW it's depend on screen size
    private int getRandomNumber(boolean xTransation) {
        // TODO Auto-generated method stub
        Random rGenerator = new Random();
        int n = xTransation ? rGenerator.nextInt(xMax) : rGenerator.nextInt(yMax);
            /*int n = xTransation? rGenerator.nextInt(200)+150 : rGenerator.nextInt(150)+100;
			if (n%2 == 0)
				signOpposite = !signOpposite;
			return signOpposite? n : -n;*/
        return n;
    }

    private void startAnimations() {
        //this flag to indicate if user clicks on button or not at animation end
        isIconClick = false;
        alphaA = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fly_alpha);
        alphaA.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
                // this mean user didn't click on logo so close video and
                // service
                // finish activity
                if (!isIconClick) {
                    sendBroadcast(new Intent("close.video.elsawaf"));
                    stopSelf();
                }
            }
        });
        btnClickMe.startAnimation(alphaA);

		/*
		int x = getRandomNumber(true);
		oX = ObjectAnimator.ofFloat(btnClickMe, "translationX", -x, x)
				.setDuration(3000);
		int y = getRandomNumber(false);
		oY = ObjectAnimator.ofFloat(btnClickMe, "translationY", -y, y)
				.setDuration(3000);

		oAlpha = ObjectAnimator.ofFloat(btnClickMe, "alpha", 1, 0.25f, 0)
				.setDuration(4500);

		oX.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				oAlpha.start();

			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});

		oAlpha.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// this mean user didn't click on logo so close video and
				// service
				// finish activity
				if (!isIconClick) {
					sendBroadcast(new Intent("close.video.elsawaf"));
					stopSelf();
				}

				// Log.w("elsawaf", "Animation END");

			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// Log.w("elsawaf", "Animation CANCEL");
			}
		});

		oX.start();
		oY.start();
		// next time will change start point
		signOpposite = !signOpposite;
*/
    }

    private void findViews() {

        btnClickMe = (ImageView) rowView.findViewById(R.id.btnClickMe);

        btnClickMe.setOnClickListener(this);

        imageLoader.displayImage(JackpotApplication.logoURL,
                btnClickMe, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        // spinner.setVisibility(View.VISIBLE);
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
                        Toast.makeText(
                                getApplicationContext(),
                                message, Toast.LENGTH_SHORT).show();

                        // spinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri,
                                                  View view, Bitmap loadedImage) {
                        // spinner.setVisibility(View.GONE);
                        startAnimations();
                    }
                });

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onClick(View arg0) {
        // btnClickMe.setText(""+count);
        isIconClick = true;
        btnClickMe.setVisibility(View.INVISIBLE);
/*		oX.cancel();
		oY.cancel();
		oAlpha.cancel();*/
        alphaA.cancel();
        btnClickMe.clearAnimation();
        localWindowManager.removeView(rowView);

        if (timer != null) {
            timer.cancel();
        }
        //re-schedule timer here
        //otherwise, IllegalStateException of
        //"TimerTask is scheduled already"
        //will be thrown
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 10000);
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            messageHandler.sendEmptyMessage(0);
			
			 /* runOnUiThread(new Runnable(){
			 * 
			 * @Override public void run() { textCounter.setText(strDate); }});
			 */
        }

    }

}