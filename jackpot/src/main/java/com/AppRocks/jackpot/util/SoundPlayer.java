package com.AppRocks.jackpot.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import com.AppRocks.jackpot.R;

public class SoundPlayer {

    //public static final int clockSound = R.raw.clock;
    public static final int needleSound = R.raw.needle_sound;
    public static final int answerSound = R.raw.answers_go_down;
    public static final int questionSound = R.raw.questions_appear;
    public static final int starSound = R.raw.new_when_user_answer_a_correct_question_with_star_animation;
    static int clockSoundLoading;
    private static SoundPool soundPool;
    private static int streamID;
    private static SparseIntArray soundsList;

    /**
     * Populate the SoundPool
     */
    public static void initSounds(Context context) {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
        soundsList = new SparseIntArray();
        /*for (int i=0 ; i<5; i++){
            clockSoundLoading = soundPool.load(context, fileFromResources, 1);
		}*/
        //soundsList.put(clockSound, soundPool.load(context, clockSound, 1));
        soundsList.put(needleSound, soundPool.load(context, needleSound, 1));
        soundsList.put(answerSound, soundPool.load(context, answerSound, 1));
        soundsList.put(questionSound, soundPool.load(context, questionSound, 1));
        soundsList.put(starSound, soundPool.load(context, starSound, 1));

        // soundPool.setOnLoadCompleteListener(listener);
    }

    /**
     * Play a given sound in the soundPool
     */
    public static void playSound(Context context, int fileFromResources) {
		/*if (soundPool == null) {
			initSounds(context);
		}*/
        float volume = 0.8f;// whatever in the range = 0.0 to 1.0

        // play sound with same right and left volume, with a priority of 1,
        // zero repeats (i.e play once), and a playback rate of 1f
        streamID = soundPool.play(soundsList.get(fileFromResources), volume, volume, 1, 0, 1f);
    }

    public static void stopSound(Context context) {
        if (soundPool != null) {
            //initSounds(context);
            soundPool.stop(streamID);
            soundPool.release();
        }
    }
}
