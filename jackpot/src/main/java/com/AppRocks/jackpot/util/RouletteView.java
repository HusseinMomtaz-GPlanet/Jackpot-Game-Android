package com.AppRocks.jackpot.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.AppRocks.jackpot.R;

public class RouletteView extends RelativeLayout {

    ImageView rouletteImage;
    ImageView arrowImage;
    Animation rAnim;
    int previousCategory;
    private MediaPlayer mp;
    private Runnable startPlayerTask;

    public RouletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.roulette_view, this, true);
        rouletteImage = (ImageView) findViewById(R.id.rouletteImage);
        //arrowImage = (ImageView) findViewById(R.id.arrowImage);
        previousCategory = 0;
    }

    /**
     * 2-Sports: orange
     * 1-History: Yellow
     * 3-Geography: Blue
     * 0-Science: Green
     * 5-Literature: Violet
     * 4-Art: Pink
     *
     * @param category the category which animation should stop rotating at, its value from 0 to 5, where 0 stops at the same starting position (Green),
     *                 other numbers stop at following color (with current image): Yellow, Orange, Blue, Pink ,Violet in order
     * @param callback call back listener to call when rotation is completed
     */
    public void rotateTo(int category, final RotationEndCallBack callback) {

        mp = MediaPlayer.create(getContext(), R.raw.roulette_2_6_seconds);
        int numberOfRotations = 6; // it was 8 to make duration 4 S

        rAnim = new RotateAnimation(60 * previousCategory, 360 * numberOfRotations + 60 * category, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rAnim.setDuration(333 * numberOfRotations);//+ 100 * category);
        //rAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        //rAnim.setStartOffset(500);
        rAnim.setFillAfter(true);
        previousCategory = category;

        startPlayerTask = new Runnable() {

            @Override
            public void run() {
                //mp.start();
                callback.onRotationEnd();
            }
        };

        // Start animation
        rouletteImage.startAnimation(rAnim);
        mp.setLooping(true);

        mp.start();

        // Set on animation finish listener
        rAnim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                callback.onRotationStart();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mp != null) {
                    mp.stop();
                    mp.reset();
                    mp.release();
                }
                /*Animation myFadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
                arrowImage.startAnimation(myFadeInAnimation);*/

                // Actions to do after rotation finished
                Handler handler = new Handler();
                handler.postDelayed(startPlayerTask, 500);
            }
        });
    }

    public void stop() {
        rouletteImage.clearAnimation();
        rAnim.cancel();
        //mp.stop();
        //mp.release();
    }

    public static interface RotationEndCallBack {
        public void onRotationEnd();

        public void onRotationStart();
    }
}
