package logic;

public class FBTaskCustomNumber extends FBTask {
	
	private final String fileExtension = ".parn";

	public FBTaskCustomNumber(String path, String n){
		super(path, n, TaskMode.CUSTOM_NUMBER);
	}

	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}

}
