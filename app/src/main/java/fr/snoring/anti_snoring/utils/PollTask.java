package fr.snoring.anti_snoring.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;

import fr.snoring.anti_snoring.R;
import fr.snoring.anti_snoring.activity.AntiSnoringActivity;
import fr.snoring.anti_snoring.sound.AudioPlayer;
import fr.snoring.anti_snoring.sound.SoundFile;
import fr.snoring.anti_snoring.sound.SoundMeter;
import fr.snoring.anti_snoring.view.SoundLevelView;

public class PollTask {

    /* constants */
    private static final int POLL_INTERVAL = 500;

    private SoundMeter soundMeter;

    /**
     * running state
     **/
    private int mHitCount = 0;
    private int mLowCount = 0;

    /**
     * config state
     **/
    private int mThreshold;

    private Handler mHandler;
    private SoundLevelView soundLevelView;

    // Get instance of Vibrator from current Context
    private Vibrator v;

    private AudioPlayer audioPlayer;

    private AudioManager audioManager;

    private Runnable mPollTask;

    /**
     * Set it to true to test this part.
     */
    private boolean testing = false;
    public PollTask(final Activity activity, final SoundFile soundFile)
    {
        if(mPollTask == null) {
            readPollPreferences(activity);
            this.mHandler = new Handler();
            this.soundMeter = new SoundMeter();
            v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

            // Used to get media volume (and not ringtone volume)
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

            // Init audio player
            audioPlayer = new AudioPlayer();
            audioPlayer.init(soundFile, activity);
            soundLevelView = activity.findViewById(R.id.volume);

            soundLevelView.setLevel(0, mThreshold);
            mHitCount = 0;
            soundMeter.start(activity.getApplicationContext());

            mPollTask = new Runnable() {
                public void run() {
                    double amp = soundMeter.getAmplitude();
                    updateDisplay(amp);

                    // Get the current volume
                    int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                    // For tests purpose, force the values to enter the if conditions
                    if (testing) {
                        amp = mThreshold + 1;
                        mHitCount = 6;
                    }

                    if (amp > mThreshold) {
                        mHitCount++;
                        if (mHitCount > 5) {
                            changeHeadPicture(activity, R.drawable.angry);
                            if (currentVolume == 0) {
                                if (v.hasVibrator()) {
                                    v.vibrate(300);
                                }
                            } else {
                                startAudioPlayer();
                            }
                            if (testing) {
                                // Goes to the else clause next time
                                mLowCount = 6;
                                testing = false;
                            }
                            mHitCount = 0;
                        }
                    } else {
                        mLowCount++;
                        if (mLowCount > 5) {
                            changeHeadPicture(activity, R.drawable.sleeping);
                            mLowCount = 0;
                            try {
                                audioPlayer.pause();
                            } catch (IllegalStateException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    mHandler.postDelayed(mPollTask, POLL_INTERVAL);
                }
            };
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    }

    private void updateDisplay(double signalEMA) {
        soundLevelView.setLevel((int) signalEMA, mThreshold);
    }

    private void changeHeadPicture(final Activity activity, final int id) {
        final ImageView headView = activity.findViewById(R.id.sleeping);
        headView.post(new Runnable() {
            @Override
            public void run() {
                headView.setImageDrawable(ContextCompat.getDrawable(activity.getApplicationContext(), id));
            }
        });
    }

    public int getmThreshold() {
        return mThreshold;
    }

    private void readPollPreferences(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        mThreshold = prefs.getInt("threshold", 3);
    }

    public final void changeThreshold(Activity activity, int threshold) {
        int value = threshold / 10 + 1;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        prefs.edit().putInt("threshold", value).apply();
        mThreshold = value;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public void release() {
        mHandler.removeCallbacks(mPollTask);
        audioPlayer.release();
        soundMeter.release();
    }

    private void startAudioPlayer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                audioPlayer.play();
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                audioPlayer.pause();
            }
        }, 5000);
    }
}
