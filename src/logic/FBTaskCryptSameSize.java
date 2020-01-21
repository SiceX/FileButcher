package logic;

public class FBTaskCryptSameSize extends FBTask {
	
	private final String fileExtension = ".crypar";
	
	public FBTaskCryptSameSize(String path, String n){
		super(path, n, TaskMode.CRYPT_SAME_SIZE);
	}

	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}

}
