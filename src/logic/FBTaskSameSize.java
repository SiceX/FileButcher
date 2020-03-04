package logic;

public class FBTaskSameSize extends FBTask {
	
	private final String fileExtension = ".par";
	private long partSize;
	
	public FBTaskSameSize(String path, String name, long pSize){
		super(path, name, TaskMode.SAME_SIZE);
		partSize = pSize;
	}
	
	//Default
	public FBTaskSameSize(String path, String name) {
		this(path, name, 500);
	}
	
	public void execute() {
		
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
