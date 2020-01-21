package logic;

public class FBTaskSameSize extends FBTask {
	
	private final String fileExtension = ".par";
	
	public FBTaskSameSize(String path, String n){
		super(path, n, TaskMode.SAME_SIZE);
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
