/**
 * 
 */
package logic.tasks;

/**
 * @author nicola.ferrari
 *
 */
public enum TaskMode {
	SAME_SIZE(".par", "Parti uguali"),
	CRYPT_SAME_SIZE(".crypar", "Parti uguali sicuro"),
	ZIP_CUSTOM_SIZE(".zipar", "Parti custom"),
	CUSTOM_NUMBER(".parn", "N parti");
	
	private String fileExt;
	private String displayName;

	TaskMode(String fileExt, String displayName) {
		this.fileExt = fileExt;
		this.displayName = displayName;
	}
	
	public String getFileExtension() {
       return fileExt;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}

}
