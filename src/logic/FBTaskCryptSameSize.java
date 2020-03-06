package logic;

import java.text.DecimalFormat;

public class FBTaskCryptSameSize extends FBTask {
	
	private final String fileExtension = ".crypar";
	private long partSize;
	
	public FBTaskCryptSameSize(String path, String name, long fileSize, long pSize){
		super(path, name, TaskMode.CRYPT_SAME_SIZE, fileSize);
		partSize = pSize;
	}
	
	//Default
	public FBTaskCryptSameSize(String path, String name, long fileSize) {
		this(path, name, fileSize, 500);
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
