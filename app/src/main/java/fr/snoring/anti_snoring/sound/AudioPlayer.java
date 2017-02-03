package fr.snoring.anti_snoring.sound;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

import fr.snoring.anti_snoring.utils.FileUtils;
import fr.snoring.anti_snoring.utils.Logger;

public class AudioPlayer {

	private MediaPlayer mp;

	private State t;

	private enum State {
		INITIALIZED, STARTED, PAUSED, STOPPED, RESET, RELEASED
	}

	public AudioPlayer() {
		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

	void create(Context ctx, SoundFile soundFile) {
		// Creation et initialisation de l'audioplayer
		if (!soundFile.isAResource()) { // Une preference de fichier
			// externe existe
			create(ctx, soundFile);
		} else { // Internal sound used
			create(ctx, soundFile.getResourceId());
		}
	}

    void create(Context ctx, File soundFile) {
        reset();
        try {
            // Gets the real path on the storage
            mp.setDataSource(ctx, Uri.fromFile(soundFile));
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            Logger.error("Unable to set the data source" + e.getMessage());
        }
        mp.prepareAsync();
        t = State.INITIALIZED;
    }

	public void create(Context ctx, Uri soundFileUri) {
		reset();
		try {
			// Gets the real path on the storage
			mp.setDataSource(ctx, soundFileUri);
		} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
			Logger.error("Unable to set the data source" + e.getMessage());
		}
		mp.prepareAsync();
		t = State.INITIALIZED;
	}

	private void create(Context ctx, int resid) {
		AssetFileDescriptor afd = ctx.getResources().openRawResourceFd(resid);
		if (afd == null)
			return;
		try {
			mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
		} catch (IllegalArgumentException | IllegalStateException | IOException e) {
			Logger.error("Unable to set the data source" + e.getMessage());
		}
		mp.prepareAsync();
		try {
			afd.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		t = State.INITIALIZED;
	}

	public void changerSon(Activity activity, int resid) {
		mp.release();
		create(activity, resid);
	}

	public void play() {
		mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				mp.start();
				t = State.STARTED;
			}
		});
	}

	public void pause() {
		if (t == State.STARTED) {
			mp.pause();
		}
		t = State.PAUSED;
	}

	public void stop() {
		if (t == State.STARTED) {
			mp.stop();
		}
		t = State.STOPPED;
	}

	public void release() {
		if (mp != null) {
			mp.release();
		}
		t = State.RELEASED;
	}

	void reset() {
		if (mp != null) {

            mp.reset();
		}
		t = State.RESET;
	}
}
