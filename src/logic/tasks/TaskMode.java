/**
 * 
 */
package logic.tasks;

/**
 * @author nicola.ferrari
 *
 */
public enum TaskMode {
	BUTCHER_SAME_SIZE(".par", "Parti uguali"),
	BUTCHER_CRYPT_SAME_SIZE(".crypar", "Parti uguali sicuro"),
	BUTCHER_ZIP_CUSTOM_SIZE(".zipar", "Parti custom"),
	BUTCHER_CUSTOM_NUMBER(".parn", "N parti"),
	REBUILD_SAME_SIZE(".par", "Parti uguali"),
	REBUILD_CRYPT_SAME_SIZE(".crypar", "Parti uguali sicuro"),
	REBUILD_ZIP_CUSTOM_SIZE(".zipar", "Parti custom"),
	REBUILD_CUSTOM_NUMBER(".parn", "N parti");
	
	private String fileExt;
	private String displayName;

	TaskMode(String fileExt, String displayName) {
		this.fileExt = fileExt;
		this.displayName = displayName;
	}
	
	public static TaskMode[] selectableValues() {
		TaskMode values[] = {
				TaskMode.BUTCHER_SAME_SIZE,
				TaskMode.BUTCHER_CRYPT_SAME_SIZE,
				TaskMode.BUTCHER_ZIP_CUSTOM_SIZE,
				TaskMode.BUTCHER_CUSTOM_NUMBER
		};
		return values;
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
