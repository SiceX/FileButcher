package logic;

import java.text.DecimalFormat;

import javax.swing.filechooser.FileSystemView;

//import java.io.File;

public abstract class FBTask extends Thread{
	//private File file;
	public static final String RESULT_DIR = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\Splitted Files\\";
	protected static final int BLOCK_MAX_SIZE = 50000000;
	private String pathName;
	private String fileName;
	private long fileSize;
	private TaskMode mode;
	
	public FBTask(String path, String name, TaskMode tMode, long fSize) {
		setPathName(path);
		setFileName(name);
		setMode(tMode);
		fileSize = fSize;
	}
	
	/**
	 * Crea un FBTask generico (default SAME_SIZE)
	 * @param path indizirro del file
	 * @param name nome del file
	 * @param fileSize dimensione del file
	 */
	public FBTask(String path, String name, long fileSize){
		this(path, name, TaskMode.SAME_SIZE, fileSize);
	}
	
	/**
	 * L'esecuzione del task
	 */
	@Override
	public void run() {
		
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

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathname) {
		this.pathName = pathname;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String name) {
		this.fileName = name;
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
	
	public String getFileExtension() {
		switch(mode) {
		case SAME_SIZE:
			return ".par";
		case CRYPT_SAME_SIZE:
			return ".crypar";
		case ZIP_CUSTOM_SIZE:
			return ".parn";
		case CUSTOM_NUMBER:
			return ".zipar";
		default:
			throw new NullPointerException();
		}
	}
	
}
