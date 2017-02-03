package fr.snoring.anti_snoring.sound;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import fr.snoring.anti_snoring.R;
import fr.snoring.anti_snoring.activity.preferences.SoundPreference;
import fr.snoring.anti_snoring.utils.Logger;
import fr.snoring.anti_snoring.utils.PollTask;

public class Magneto {

    private final Activity activity;
    private final Context context;
    private final AudioPlayer audioPlayer;
    private State state;
    private File recordFile;
    private final Handler handler;
    private final PollTask pollTask;
    private final SoundPreference soundPreference;

    public Magneto(Activity activity, AudioPlayer audioPlayer, PollTask pollTask, SoundPreference soundPreference) {
        this.activity = activity;
        this.context = activity.getBaseContext();
        initStopButton(initRecordButton(), initPlayButton());
        this.state = State.STOPPED;
        this.pollTask = pollTask;
        this.soundPreference = soundPreference;
        try {
            recordFile = File.createTempFile("record", ".audio", context.getCacheDir());
        } catch (IOException e) {
            Logger.error("Can't create output file " + recordFile.getPath());
        }
        this.audioPlayer = audioPlayer;
        handler = new Handler();
    }

    private ImageView initRecordButton() {
        final ImageView recordButtonView = (ImageView) activity.findViewById(R.id.record);
        final Animation myFadeInAnimation = AnimationUtils.loadAnimation(context,
                R.anim.fade);
        recordButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordButtonView.startAnimation(myFadeInAnimation);
                if (recordFile.exists()) {
                    pollTask.release();
                    pollTask.start(recordFile.getAbsolutePath());
                    state = State.RECORDING;
                } else {
                    Logger.error(recordFile.getPath() + "does not exists!");
                }
            }
        });
        return recordButtonView;
    }

    private ImageView initPlayButton() {
        final ImageView playButtonView = (ImageView) activity.findViewById(R.id.play);
        playButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state == State.STOPPED || state == State.PAUSED) {
                    if (recordFile.exists() && recordFile.length() > 0) {
                        pollTask.release();
                        playButtonView.setImageResource(R.drawable.pause);
                        state = State.PLAYING;
                        audioPlayer.create(context, recordFile);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                audioPlayer.play();
                            }
                        });
                    } else {
                        Logger.error(recordFile.getPath() + "does not exists or is empty!");
                    }
                } else if (state == State.PLAYING) {
                    playButtonView.setImageResource(R.drawable.play);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            audioPlayer.pause();
                        }
                    });
                    state = State.PAUSED;
                }
            }
        });
        return playButtonView;
    }

    private void initStopButton(final ImageView recordButtonView, final ImageView playButtonView) {
        ImageView stopButtonView = (ImageView) activity.findViewById(R.id.stop);
        stopButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state == State.RECORDING) {
                    pollTask.release();
                    pollTask.start();
                    recordButtonView.clearAnimation();
                } else if (state == State.PLAYING) {
                    playButtonView.setImageResource(R.drawable.play);
                    audioPlayer.reset();
                    audioPlayer.create(context, soundPreference.getCurrentSound());
                    pollTask.start();
                }
                state = State.STOPPED;
            }
        });
    }

    private enum State {RECORDING, PLAYING, PAUSED, STOPPED}

}
