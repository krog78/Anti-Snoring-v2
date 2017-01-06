package fr.snoring.anti_snoring.sound;

/**
 * Sound file to play
 *
 */
public class SoundFile {

	/**
	 * This sound is based on a URL and is not a resource inside the raw dir of
	 * this project
	 */
	public static final int ID_IS_NOT_A_RESOURCE = -1;

	/**
	 * The name of the file as it will be printed
	 */
	private final String filename;

	/**
	 * The resource id in the raw dir
	 */
	private final int resourceId;

	private final String url;

	public SoundFile(String filename, int resourceId, String url) {
		super();
		if (resourceId == ID_IS_NOT_A_RESOURCE && (url == null || url.isEmpty())) {
			// Bad formatted sound
			throw new IllegalArgumentException("Either the resourceId or the url should be defined.");
		}
		this.filename = filename;
		this.resourceId = resourceId;
		this.url = url;
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
		return !(getResourceId() == ID_IS_NOT_A_RESOURCE);

	}
}
