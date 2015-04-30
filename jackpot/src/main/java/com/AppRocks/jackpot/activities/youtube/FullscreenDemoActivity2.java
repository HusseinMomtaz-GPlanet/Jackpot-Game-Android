/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.AppRocks.jackpot.activities.youtube;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.AppRocks.jackpot.R;
import com.AppRocks.jackpot.services.ServiceTutorials;
import com.AppRocks.jackpot.util.ConnectionDetector;
import com.AppRocks.jackpot.webservice.IncrementVideoViewsTask;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Sample activity showing how to properly enable custom fullscreen behavior.
 * <p/>
 * This is the preferred way of handling fullscreen because the default
 * fullscreen implementation will cause re-buffering of the video.
 */
public class FullscreenDemoActivity2 extends YouTubeBaseActivity implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @SuppressLint("InlinedApi")
    private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9 ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
    // initialize from getJackpotDetailsTask
    public static String videoID;//= "wGCetsl-srk";
    private YouTubePlayerView playerView;
    private YouTubePlayer player;
    private final BroadcastReceiver videoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (player != null)
                player.release();
            Toast.makeText(FullscreenDemoActivity2.this,
                    "We are sorry. You didn't see the video correctly.\nYou must click over the company logo.",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    };
    private boolean fullscreen;
    private boolean isFirstTimeToPlay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_demo2);
        registerReceiver(videoReceiver, new IntentFilter("close.video.elsawaf"));
        playerView = (YouTubePlayerView) findViewById(R.id.player);

        if (videoID != null) {
            //videoID = "wGCetsl-srk";
            playerView.initialize(DeveloperKey.DEVELOPER_KEY, new YoutubeInitializedListener());
        } else {
            Toast.makeText(FullscreenDemoActivity2.this,
                    "No Video, please try again later.",
                    Toast.LENGTH_LONG).show();
        }

        // Web service get video
        // and get videoID from v parameter at URL

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(videoReceiver);
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return playerView;
    }

    @Override
    public void onClick(View v) {
        player.setFullscreen(!fullscreen);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        stopService(new Intent(FullscreenDemoActivity2.this,
                ServiceTutorials.class));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int controlFlags = player.getFullscreenControlFlags();
        if (isChecked) {
            // If you use the FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE,
            // your activity's normal UI
            // should never be laid out in landscape mode (since the video will
            // be fullscreen whenever the
            // activity is in landscape orientation). Therefore you should set
            // the activity's requested
            // orientation to portrait. Typically you would do this in your
            // AndroidManifest.xml, we do it
            // programmatically here since this activity demos fullscreen
            // behavior both with and without
            // this flag).
            setRequestedOrientation(PORTRAIT_ORIENTATION);
            controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            controlFlags &= ~YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
        }
        player.setFullscreenControlFlags(controlFlags);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // doLayout();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        //very important
        if (player != null)
            player.release();
    }

    public class YoutubeFullscreenListener implements OnFullscreenListener {
        @Override
        public void onFullscreen(boolean isFullscreen) {
            fullscreen = isFullscreen;
            // doLayout();
        }
    }

    public class YoutubeInitializedListener implements OnInitializedListener {

        private void closeVideo() {
            // TODO Auto-generated method stub
            stopService(new Intent(FullscreenDemoActivity2.this,
                    ServiceTutorials.class));
            Toast.makeText(FullscreenDemoActivity2.this,
                    "You should to watch the complete video",
                    Toast.LENGTH_LONG).show();
            finish();

        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                            YouTubePlayer newPlayer, boolean wasRestored) {
            player = newPlayer;
            // setControlsEnabled();
            // Specify that we want to handle fullscreen behavior ourselves.
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
            player.setOnFullscreenListener(new YoutubeFullscreenListener());
            if (!wasRestored) {
                // play automatically
                player.loadVideo(videoID);
                // don't play automatically - just prepare video
                //player.cueVideo(videoID);
                player.setShowFullscreenButton(false);
                //player.play();
                // player.setPlayerStyle(PlayerStyle.MINIMAL);
                player.setPlaybackEventListener(new PlaybackEventListener() {

                    @Override
                    public void onStopped() {
                        // TODO Auto-generated method stub
                        stopService(new Intent(FullscreenDemoActivity2.this,
                                ServiceTutorials.class));

                        // detect if user reach to the end of the video
                        if (player.getCurrentTimeMillis() == player
                                .getDurationMillis()) {
                            Toast.makeText(FullscreenDemoActivity2.this,
                                    "Successful watching", Toast.LENGTH_LONG)
                                    .show();
                            // web service increament
                            ConnectionDetector cd = new ConnectionDetector(FullscreenDemoActivity2.this);
                            if (cd.isConnectingToInternet()) {
                                new IncrementVideoViewsTask().execute();
                            }
                            finish();
                        }
                    }

                    @Override
                    public void onSeekTo(int newTime) {
                        Log.w("elsawaf", "seek");
                        // TODO Auto-generated method stub
                        //if (newTime > player.getCurrentTimeMillis())
                        closeVideo();

                        // Toast.makeText(FullscreenDemoActivity2.this,
                        // "elapsed: " + player.getCurrentTimeMillis()/1000 +
                        // " -Duration: " + player.getDurationMillis()/1000,
                        // Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPlaying() {
                        Log.w("elsawaf", "play");
                        if (isFirstTimeToPlay && player.getCurrentTimeMillis() > 1000) {
                            closeVideo();
                            isFirstTimeToPlay = false;
                        }
                        startService(new Intent(FullscreenDemoActivity2.this,
                                ServiceTutorials.class));

                    }

                    @Override
                    public void onPaused() {
                        // TODO Auto-generated method stub
                        stopService(new Intent(FullscreenDemoActivity2.this,
                                ServiceTutorials.class));
                        isFirstTimeToPlay = false;
                    }

                    @Override
                    public void onBuffering(boolean arg0) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        }

        @Override
        public void onInitializationFailure(Provider provider,
                                            YouTubeInitializationResult errorReason) {
            // TODO Auto-generated method stub
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(FullscreenDemoActivity2.this, 1)
                        .show();
            } else {
                String errorMessage = String.format(
                        "There was an error initializing the YouTubePlayer",
                        errorReason.toString());
                Toast.makeText(FullscreenDemoActivity2.this, errorMessage,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
