package logic;

public class FBTaskZipCustomSize extends FBTask {
	
	private final String fileExtension = ".zipar";
	
	public FBTaskZipCustomSize(String path, String n){
		super(path, n, TaskMode.ZIP_CUSTOM_SIZE);
	}

	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}

}
