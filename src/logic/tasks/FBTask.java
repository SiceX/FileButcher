package logic.tasks;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Observable;

import javax.swing.filechooser.FileSystemView;

/**
 * @author Sice
 *
 */
public abstract class FBTask extends Observable implements Runnable{
	
	/**
	 * Cartella dove vengono riposti i risultati delle operazioni
	 */
	public static final String TASKS_DIR = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\File Splitter\\";
	protected static final int BLOCK_MAX_SIZE = 50000000;
	/**
	 * Cartella dove vengono generati i file macellati
	 */
	public String splittedDir;
	private String pathName;
	private String fileNameNoExt;
	private String fileName;
	protected String password;
	private long fileSize;
	protected long processed;
	private boolean completed;
	private TaskMode mode;
	
	public FBTask(String path, String name, TaskMode tMode, long fSize) {
		setPathName(path);
		setFileName(name);
		setFileNameNoExt(name.split("\\.")[0]);
		setMode(tMode);
		splittedDir = TASKS_DIR + getFileNameNoExt() + "\\";
		fileSize = fSize;
		processed = 0;
		setCompleted(false);
	}
	
	/**
	 * Crea un FBTask generico (default SAME_SIZE)
	 * @param path indirizzo del file
	 * @param name nome del file
	 * @param fileSize dimensione del file
	 */
	public FBTask(String path, String name, long fileSize){
		this(path, name, TaskMode.BUTCHER_SAME_SIZE, fileSize);
	}
	
	/**
	 * L'esecuzione del task
	 */
	@Override
	public void run() {
		
	}
	
	protected void createButcheringResultDir() {
		File resultDir = new File(splittedDir);
	    if (!resultDir.exists()){
	    	resultDir.mkdir();
	    }
	}
	
	public String getModeDescription() {
		switch(mode) {
		case BUTCHER_SAME_SIZE:
			return "Divisione in parti di dimensioni uguali";
		case BUTCHER_CRYPT_SAME_SIZE:
			return "Divisione in parti di dimensioni uguali con crittografia";
		case BUTCHER_CUSTOM_NUMBER:
			return "Divisione in N parti";
		default:
			throw new NullPointerException();
		}
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

	public TaskMode getMode() {
		return mode;
	}
	
	public void setMode(TaskMode type) {
		this.mode = type;
	}

	/**
	 * @return indirizzo completo del file, compreso il file stesso
	 */
	public String getPathName() {
		return pathName;
	}

	/**
	 * @param pathname indirizzo completo del file, compreso il file stesso
	 */
	public void setPathName(String pathname) {
		this.pathName = pathname;
	}

	/**
	 * @return il nome del file senza estensione
	 */
	public String getFileNameNoExt() {
		return fileNameNoExt;
	}

	/**
	 * @param name il nome del file senza estensione
	 */
	public void setFileNameNoExt(String name) {
		this.fileNameNoExt = name;
	}
	
	/**
	 * @return il nome completo del file
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName il nome completo del file
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setPassword(String cryptKey) {
		this.password = cryptKey;
	}
	
	public String getParameters() {
		return null;
	}
	
	public void setParameters(Object param) {}

	public long getFileSize() {
		return fileSize;
	}

	public long getProcessed() {
		return processed;
	}

	protected void setProcessed(long proc) {
		processed = proc;
		setChanged();
		notifyObservers(this); 
	}
	
	public abstract double getProcessedPercentage();
	
	public String getFileExtension() {
		return mode.getFileExtension();
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	
}
