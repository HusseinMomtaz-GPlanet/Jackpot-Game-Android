package com.AppRocks.jackpot.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.AppRocks.jackpot.JackpotParameters;
import com.AppRocks.jackpot.R;
import com.AppRocks.jackpot.services.MusicService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity
public class BaseActivity extends Activity {

    @ViewById
    ImageButton bottom_btnMoney;
    @ViewById
    ImageButton bottom_btnStar;
    @ViewById
    ImageButton bottom_btnHome;
    @ViewById
    ImageButton bottom_btnVedio;
    private JackpotParameters p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        p = new JackpotParameters(this);
    }

    @AfterViews
    void playMusic() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (p.getBoolean("playMusic", true)) {
            startService(new Intent(getBaseContext(), MusicService.class));
        } else {
            bottom_btnVedio.setImageResource(R.drawable.stop_music_off);
        }
    }

    // Using this button to launch activity invitations
    @Click
    void bottom_btnMoney() {
        startActivity(new Intent(this, FaceBookInvitations.class));
        overridePendingTransition(R.anim.rail_in_from_up, R.anim.disapear);
    }

    @Click
    void bottom_btnStar() {
        startActivity(new Intent(this, Scoreboard.class));
    }

    @Click
    void bottom_btnHome() {
        Intent i = new Intent(this, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //i.putExtra("login", true);
        startActivity(i);
    }

    @Click
    void bottom_btnVedio() {
        if (p.getBoolean("playMusic", true)) {
            stopService(new Intent(getBaseContext(), MusicService.class));
            bottom_btnVedio.setImageResource(R.drawable.stop_music_off);
            p.setBoolean(false, "playMusic");
        } else {
            startService(new Intent(getBaseContext(), MusicService.class));
            bottom_btnVedio.setImageResource(R.drawable.stop_music);
            p.setBoolean(true, "playMusic");
        }
    }
}
