package logic;

import java.text.DecimalFormat;

public class FBTaskSameSize extends FBTask {
	
	private final String fileExtension = ".par";
	private long partSize;
	
	public FBTaskSameSize(String path, String name, long fileSize, long pSize){
		super(path, name, TaskMode.SAME_SIZE, fileSize);
		partSize = pSize;
	}
	
	//Default
	public FBTaskSameSize(String path, String name, long fileSize) {
		this(path, name, fileSize, 500);
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
	public String getParameters() {
		DecimalFormat df = new DecimalFormat("#.##");
		if(partSize < 1000) {
			return partSize + " B";
		}
		else if(partSize < 1000000) {
			return df.format(((double)partSize)/1000) + " KB";
		}
		else if(partSize < 1000000000) {
			return df.format(((double)partSize)/1000000) + " MB";
		}
		else {
			return df.format(((double)partSize)/1000000000) + " GB";
		}
	}
	
	@Override
	public void setParameters(Object param) {
		if(param.getClass() == Long.class) {
			partSize = (long)param;
		}
	}

}
