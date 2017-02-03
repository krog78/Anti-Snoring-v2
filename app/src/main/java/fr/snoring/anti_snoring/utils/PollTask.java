package fr.snoring.anti_snoring.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;

import fr.snoring.anti_snoring.R;
import fr.snoring.anti_snoring.activity.AntiSnoringActivity;
import fr.snoring.anti_snoring.sound.AudioPlayer;
import fr.snoring.anti_snoring.sound.AudioRecorder;
import fr.snoring.anti_snoring.view.SoundLevelView;

public class PollTask {

    private static final String NO_RECORD_FILE = "/dev/null";

    /* constants */
    private static final int POLL_INTERVAL = 500;

    private final AudioRecorder audioRecorder;

    /**
     * running state
     **/
    private int mHitCount = 0;
    private int mLowCount = 0;

    /**
     * config state
     **/
    private int mThreshold;

    private final Activity activity;

    private final Handler mHandler;
    private final SoundLevelView soundLevelView;

    // Get instance of Vibrator from current Context
    private final Vibrator v;

    private final AudioPlayer audioPlayer;

    private final AudioManager audioManager;

    /**
     * Set it to true to test this part.
     */
    private boolean testing = false;

    public PollTask(Activity activity, AudioPlayer audioPlayer, AudioRecorder audioRecorder) throws IllegalStateException, IOException {
        super();
        readPollPreferences(activity);
        this.mHandler = new Handler();
        this.activity = activity;
        this.audioRecorder = audioRecorder;
        v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

        // Used to get media volume (and not ringtone volume)
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Init audio player
        this.audioPlayer = audioPlayer;
        soundLevelView = (SoundLevelView) activity.findViewById(R.id.volume);

        soundLevelView.setLevel(0, mThreshold);
        start(NO_RECORD_FILE);

    }

    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = audioRecorder.getAmplitude();
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

    public void start(String recordFile) {
        mHitCount = 0;
        audioRecorder.start(recordFile);
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    public void start() {
        start(NO_RECORD_FILE);
    }

    private void updateDisplay(double signalEMA) {
        soundLevelView.setLevel((int) signalEMA, mThreshold);
    }

    private void changeHeadPicture(final Activity activity, final int id) {
        final ImageView headView = ((ImageView) activity.findViewById(R.id.sleeping));
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
        int mPollDelay;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        mThreshold = prefs.getInt("threshold", 3);
        Log.i(AntiSnoringActivity.TAG, "threshold=" + mThreshold);
        mPollDelay = Integer.parseInt(prefs.getString("sleep", "5"));
        Log.i(AntiSnoringActivity.TAG, "sleep=" + mPollDelay);
    }

    public final void changeThreshold(Activity activity, int threshold) {
        int value = threshold / 10;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        prefs.edit().putInt("threshold", value).apply();
        mThreshold = value;
    }

    public void release() {
        mHandler.removeCallbacks(mPollTask);
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
