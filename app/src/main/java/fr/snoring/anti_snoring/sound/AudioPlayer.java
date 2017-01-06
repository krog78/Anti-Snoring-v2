package fr.snoring.anti_snoring.sound;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

import fr.snoring.anti_snoring.utils.FileUtils;

public class AudioPlayer {

	private MediaPlayer mp;

	public void create(Context ctx, String urlFichierSon) {
		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			// Gets the real path on the storage
			Uri realStoragePath = Uri.parse(FileUtils.getPath(ctx, Uri.parse(urlFichierSon)));
			mp.setDataSource(ctx, realStoragePath);
		} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
			throw new RuntimeException(e);
		}
		mp.prepareAsync();

	}

	private void create(Context ctx, int resid) {
		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		AssetFileDescriptor afd = ctx.getResources().openRawResourceFd(resid);
		if (afd == null)
			return;
		try {
			mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
		} catch (IllegalArgumentException | IllegalStateException | IOException e) {
			throw new RuntimeException(e);
		}
		mp.prepareAsync();
		try {
			afd.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void changerSon(Activity activity, int resid) {
		mp.release();
		create(activity, resid);
	}

	public void play() {
		mp.start();

	}

	public void pause() {
		if (mp != null && mp.isPlaying())
			mp.pause();
	}

	public void release() {
		if (mp != null) {
			mp.release();
		}
	}

	public void init(SoundFile soundFile, Context ctx) {
		// Creation et initialisation de l'audioplayer
		if (!soundFile.isAResource()) { // Une preference de fichier
										// externe existe
			create(ctx, soundFile.getUrl());
		} else { // Internal sound used
			create(ctx, soundFile.getResourceId());
		}
	}

}
