package com.AppRocks.jackpot.util;

import android.os.CountDownTimer;

import com.AppRocks.jackpot.activities.Question;
import com.AppRocks.jackpot.models.LevelController;

public class AlarmTimer extends CountDownTimer {

    private Question qActivity;

    public AlarmTimer(long millisInFuture, long countDownInterval, Question q) {
        super(millisInFuture, countDownInterval);
        qActivity = q;
    }

    @Override
    public void onFinish() {
        qActivity.txtAlarm.setText("0");
        qActivity.showTimeIsUpScreen();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        LevelController.timer = ((int) (millisUntilFinished / 1000));
        qActivity.txtAlarm.setText("" + LevelController.timer);
    }

}
