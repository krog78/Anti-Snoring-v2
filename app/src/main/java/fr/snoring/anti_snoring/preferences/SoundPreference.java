package fr.snoring.anti_snoring.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.SparseArray;
import android.widget.TextView;

import fr.snoring.anti_snoring.R;
import fr.snoring.anti_snoring.sound.SoundFile;

/**
 * User sound preferences
 *
 */
public class SoundPreference {

	private final SharedPreferences preferences;

    private final SparseArray<SoundFile> internalSounds;

    private final Activity activity;

    private final SoundFile defaultSound;

	private enum Fields {
        SOUND_NAME, RESOURCE_ID, SOUND_URL
    }

	public SoundPreference(Activity activity) {
		super();
        this.activity = activity;
        preferences = activity.getPreferences(Context.MODE_PRIVATE);
        internalSounds = new SparseArray<>();
        defaultSound = loadDefaultSounds();
    }

    public final SoundFile getPreference() {
        SoundFile prefSoundFile;
        String soundName = preferences.getString(Fields.SOUND_NAME.name(), "");
        String soundUrl = preferences.getString(Fields.SOUND_URL.name(), "");
        int soundId = preferences.getInt(Fields.RESOURCE_ID.name(), 0);
        if (soundId == SoundFile.RESOURCE_ID_EXT_FILE) {
            prefSoundFile = new SoundFile(soundName, soundUrl);
        } else {
            prefSoundFile = internalSounds.get(soundId, defaultSound);
        }
        updateSoundText(prefSoundFile);
        return prefSoundFile;
    }

    public final void savePreference(SoundFile soundFile) {
        clearPreferences();
        preferences.edit().putString(Fields.SOUND_NAME.name(), soundFile.getFilename()).apply();
        preferences.edit().putString(Fields.SOUND_URL.name(), soundFile.getUrl()).apply();
        preferences.edit().putInt(Fields.RESOURCE_ID.name(), soundFile.getResourceId()).apply();
        updateSoundText(soundFile);
    }

	/**
	 * Reinitializes all the sound preferences
	 */
    private void clearPreferences() {
        for (Fields field : Fields.values()) {
            preferences.edit().remove(field.name()).apply();
        }
    }

    private SoundFile loadDefaultSounds() {
        String[] sonsListe = activity.getResources().getStringArray(R.array.sounds_texts);
        TypedArray raws = activity.getResources().obtainTypedArray(R.array.sounds_ids);
        if (sonsListe.length != raws.length()) {
            throw new IllegalArgumentException(
                    String.format("The size %s of the sound names should be equal to the files resources %s.",
                            sonsListe.length, raws.length()));
        }
        SoundFile defaultSoundFile = null;
        for (int i = 0; i < sonsListe.length; i++) {
            int resourceId = raws.getResourceId(i, 0);
            if (resourceId > 0) {
                SoundFile soundFile = new SoundFile(sonsListe[i], resourceId);
                if (defaultSoundFile == null) {
                    defaultSoundFile = soundFile;
                }
                internalSounds.put(resourceId, soundFile);
            }
        }
        raws.recycle();
        return defaultSoundFile;
    }

    public SparseArray<SoundFile> getInternalSounds() {
        return internalSounds;
    }

    private void updateSoundText(SoundFile soundFile) {
        TextView text = (TextView) activity.findViewById(R.id.son_selectionne);
        text.setText(soundFile.getFilename());
    }

    public SoundFile getDefaultSound() {
        return defaultSound;
    }
}
