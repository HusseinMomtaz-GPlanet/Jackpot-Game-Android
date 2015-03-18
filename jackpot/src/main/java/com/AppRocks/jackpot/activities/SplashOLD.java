package com.AppRocks.jackpot.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Window;

import com.AppRocks.jackpot.R;

import java.lang.ref.WeakReference;

public class SplashOLD extends Activity {

    private SleepAsyncTask task;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_from_left, R.anim.hold);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.splash);

        //LinearLayout test = (LinearLayout) findViewById(R.id.testL);
        /*test.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				startActivity(new Intent(Splash.this, Main_.class));
				return false;
			}
		});*/

        //new GetAllJackpotsTask(Splash.this).execute();
		
		/*Thread timer = new Thread() {
			public void run() {
				try {
					sleep(2500);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					Intent openHome = new Intent(Splash.this, Main_.class);
					//openHome.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(openHome);
					//overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);				
				}
			}
		};
		timer.start();*/
		
		/*new MyThread(this).start();
		sleepThreed = new MyThread(this);
		sleepThreed.start();*/

        task = new SleepAsyncTask(this);
        task.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        if ((task != null) && isFinishing()) {
            task.cancel(false);
        }
    }
	
	/*private class MyThread extends Thread {
		Context context;
		public MyThread (Context c){
			context = c;
		}
		public void run() {
			try {
				sleep(2500);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Intent openHome = new Intent(context, Main_.class);
				//openHome.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				//context.startActivity(openHome);
				interrupt();
				//overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);				
			}
		}
	}*/

    private static class SleepAsyncTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<Context> context;

        public SleepAsyncTask(Context c) {
            context = new WeakReference<Context>(c);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.w("SleepTask", "I will sleep");
            SystemClock.sleep(2500);
            Log.w("SleepTask", "I'm running");
            return null;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onPostExecute(Void result) {
            Context mContext = context.get();
            Intent openHome = new Intent(mContext, Main_.class);
            openHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mContext.startActivity(openHome);
            Log.w("SleepTask", "Goodbye");
            super.onPostExecute(result);
        }

    }

}