package logic;

public class FBTaskCryptSameSize extends FBTask {
	
	private final String fileExtension = ".crypar";
	private long partSize;
	
	public FBTaskCryptSameSize(String path, String name, long pSize){
		super(path, name, TaskMode.CRYPT_SAME_SIZE);
		partSize = pSize;
	}
	
	//Default
	public FBTaskCryptSameSize(String path, String name) {
		this(path, name, 500);
	}

	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}
	
	@Override
	public String getSpecs() {
		if(partSize < 1000) {
			return "Parti da: " + partSize + " B";
		}
		else if(partSize < 1000000) {
			return "Parti da: " + ((double)partSize)/1000 + " KB";
		}
		else if(partSize < 1000000000) {
			return "Parti da: " + ((double)partSize)/1000000 + " MB";
		}
		else {
			return "Parti da: " + ((double)partSize)/1000000000 + " GB";
		}
	}

}
