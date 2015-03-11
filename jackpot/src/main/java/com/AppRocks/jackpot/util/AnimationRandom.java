package com.AppRocks.jackpot.util;

import com.AppRocks.jackpot.R;

import java.util.Random;

public class AnimationRandom {

    // from 0 to 3
    private static int getRandomNumber() {
        // TODO Auto-generated method stub
        Random rGenerator = new Random();
        return rGenerator.nextInt(4);
    }

    public static int getInAnimation() {
        int n = getRandomNumber();
        switch (n) {
            case 0:
                return R.anim.rail_in_from_bottom;
            case 1:
                return R.anim.rail_in_from_left;
            case 2:
                return R.anim.rail_in_from_right;
            case 3:
                return R.anim.rail_in_from_up;

            default:
                return R.anim.rail_in_from_bottom;
        }
    }

    public static int getOutAnimation() {
        int n = getRandomNumber();
        switch (n) {
            case 0:
                return R.anim.rail_out_to_bottom;
            case 1:
                return R.anim.rail_out_to_left;
            case 2:
                return R.anim.rail_out_to_right;
            case 3:
                return R.anim.rail_out_to_up;

            default:
                return R.anim.rail_out_to_bottom;
        }
    }
}
