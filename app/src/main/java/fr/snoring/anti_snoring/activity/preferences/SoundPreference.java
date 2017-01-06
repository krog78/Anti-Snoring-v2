package fr.snoring.anti_snoring.activity.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.snoring.anti_snoring.R;
import fr.snoring.anti_snoring.sound.SoundFile;

/**
 * User sound preferences
 *
 */
public class SoundPreference {

	private final SharedPreferences preferences;

	private final List<SoundFile> defaultSounds;

	private SoundFile currentSound;

	private enum Fields {
		SOUND_NAME, RESOURCE_ID, SOUND_URL
	}

	public SoundPreference(Activity activity) {
		super();
		preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		defaultSounds = new ArrayList<SoundFile>();
	}

	private final String getName() {
		return preferences.getString(Fields.SOUND_NAME.name(), "");
	}

	private final void setName(String name) {
		preferences.edit().putString(Fields.SOUND_NAME.name(), name).commit();
	}

	private final String getUrl() {
		return preferences.getString(Fields.SOUND_URL.name(), "");
	}

	private final void setUrl(String url) {
		preferences.edit().putString(Fields.SOUND_URL.name(), url).commit();
	}

	private final int getResourceId() {
		return preferences.getInt(Fields.RESOURCE_ID.name(), SoundFile.ID_IS_NOT_A_RESOURCE);
	}

	private final void setResourceId(int resourceId) {
		preferences.edit().putInt(Fields.RESOURCE_ID.name(), resourceId).commit();
	}

	public final void savePreference(Activity activity, SoundFile soundFile) {
		init();
		setName(soundFile.getFilename());
		setResourceId(soundFile.getResourceId());
		setUrl(soundFile.getUrl());
		currentSound = soundFile;
		updateSoundText(activity);
	}

	public final SoundFile getCurrentSound() {
		if (currentSound != null)
			return currentSound;
		int resourceId = getResourceId();
		String url = getUrl();
		if (resourceId != SoundFile.ID_IS_NOT_A_RESOURCE || (url != null && !url.isEmpty())) {
			return new SoundFile(getName(), resourceId, url);
		} else {
			// Return the default sound file
			return getDefaultSounds().get(0);
		}
	}

	/**
	 * Reinitializes all the sound preferences
	 */
	private final void init() {
		for (Fields field : Fields.values()) {
			preferences.edit().remove(field.name()).commit();
		}
	}

	public void loadDefaultSounds(Resources resources) {
		TypedArray sonsListe = resources.obtainTypedArray(R.array.sons_liste);
		TypedArray raws = resources.obtainTypedArray(R.array.sons_fichier);
		if (sonsListe.length() != raws.length()) {
			throw new IllegalArgumentException(
					String.format("The size %s of the sound names should be equal to the files resources %s.",
							sonsListe.length(), raws.length()));
		}

		for (int i = 0; i < sonsListe.length(); i++) {
			defaultSounds.add(new SoundFile(sonsListe.getText(i).toString(), raws.getResourceId(i, 0), ""));
		}
		sonsListe.recycle();
		raws.recycle();
	}

	public List<SoundFile> getDefaultSounds() {
		return defaultSounds;
	}

	public void updateSoundText(Activity activity) {
		TextView text = (TextView) activity.findViewById(R.id.son_selectionne);
		text.setText(getCurrentSound().getFilename());
	}
}
