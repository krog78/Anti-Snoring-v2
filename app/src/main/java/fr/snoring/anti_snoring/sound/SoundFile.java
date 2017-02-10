package fr.snoring.anti_snoring.sound;

/**
 * Sound file to play
 *
 */
public class SoundFile {

    public static final int RESOURCE_ID_EXT_FILE = -1;

	/**
	 * The name of the file as it will be printed
	 */
	private final String filename;

	/**
	 * The resource id in the raw dir
	 */
	private final int resourceId;

	private final String url;

    public SoundFile(String filename, String url) {
        super();
        this.resourceId = RESOURCE_ID_EXT_FILE;
        this.filename = filename;
        this.url = url;
	}

    public SoundFile(String filename, int resourceId) {
        super();
        this.filename = filename;
        this.resourceId = resourceId;
        this.url = null;
    }

	public String getFilename() {
		return filename;
	}

	public int getResourceId() {
		return resourceId;
	}

	public String getUrl() {
		return url;
	}

	final boolean isAResource() {
		return resourceId != RESOURCE_ID_EXT_FILE;
	}
}
