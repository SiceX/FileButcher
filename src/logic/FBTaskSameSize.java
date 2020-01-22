package logic;

public class FBTaskSameSize extends FBTask {
	
	private final String fileExtension = ".par";
	private long partSize;
	
	public FBTaskSameSize(String path, String n, long pSize){
		super(path, n, TaskMode.SAME_SIZE);
		partSize = pSize;
	}
	
	public void execute() {
		
	}

	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}

}
