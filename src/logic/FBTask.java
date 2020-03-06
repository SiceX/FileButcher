package logic;

import java.text.DecimalFormat;

//import java.io.File;

public abstract class FBTask {
	//private File file;
	private String pathname;
	private String name;
	private long fileSize;
	private TaskMode mode;
	
	public FBTask(String path, String name, TaskMode tMode, long fSize) {
		setPathname(path);
		setName(name);
		setMode(tMode);
		fileSize = fSize;
	}
	
	public FBTask(String path, String name, long fileSize){
		this(path, name, TaskMode.SAME_SIZE, fileSize);
	}
	
	public TaskMode getMode() {
		return mode;
	}
	
	public String getModeDescription() {
		switch(mode) {
		case SAME_SIZE:
			return "Divisione in parti di dimensioni uguali";
		case CRYPT_SAME_SIZE:
			return "Divisione in parti di dimensioni uguali con crittografia";
		case ZIP_CUSTOM_SIZE:
			return "Divisione e compresione in parti di dimensione specificata";
		case CUSTOM_NUMBER:
			return "Divisione in N parti";
		default:
			throw new NullPointerException();
		}
	}
	
	public void setMode(TaskMode type) {
		this.mode = type;
	}

	public String getPathname() {
		return pathname;
	}

	public void setPathname(String pathname) {
		this.pathname = pathname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getParameters() {
		return null;
	}
	
	public void setParameters(Object param) {}

	public long getFileSize() {
		return fileSize;
	}

	public String getFileSizeFormatted() {
		DecimalFormat df = new DecimalFormat("#.##");
		if(fileSize < 1000) {
			return fileSize + " B";
		}
		else if(fileSize < 1000000) {
			return df.format(((double)fileSize)/1000) + " KB";
		}
		else if(fileSize < 1000000000) {
			return df.format(((double)fileSize)/1000000) + " MB";
		}
		else {
			return df.format(((double)fileSize)/1000000000) + " GB";
		}
	}
	
}
