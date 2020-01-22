package logic;

public class FBTaskCryptSameSize extends FBTask {
	
	private final String fileExtension = ".crypar";
	private long partSize;
	
	public FBTaskCryptSameSize(String path, String n, long pSize){
		super(path, n, TaskMode.CRYPT_SAME_SIZE);
		partSize = pSize;
	}

	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}

}
