package com.AppRocks.jackpot.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.AppRocks.jackpot.R;

public class MusicService extends Service {

    private MediaPlayer mp;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        mp = MediaPlayer.create(this, R.raw.when_user_is_in_menu);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mp.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        mp = null;
        super.onDestroy();
    }

}
