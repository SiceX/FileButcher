package logic.tasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Observable;

import javax.swing.filechooser.FileSystemView;

/**
 * Classe padre di tutti i Task.
 * <br>	Estende Observable per permettere alla tabella nella finestra principale di aggiornare le opportune barre di progresso
 * 		man mano che il task viene completato.
 * <br> Implementa Runnable per poter permettere l'esecuzione in parallelo dei Task.
 * @author Nicola Ferrari
 */
public abstract class FBTask extends Observable implements Runnable{
	
	/**
	 * Cartella dove vengono riposti i risultati delle operazioni
	 */
	protected static final String TASKS_DIR = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\File Splitter\\";
	/**
	 * Massima grandezza di un singolo blocco da tenere in memoria prima che venga scritto su disco
	 */
	protected static final int BLOCK_MAX_SIZE = 50000000;
	/**
	 * Cartella dove vengono generati i file processati
	 */
	private String splittedDir;
	/**
	 * L'indirizzo completo del file
	 */
	private String pathName;
	/**
	 * Il nome del file senza estensione
	 */
	private String fileNameNoExt;
	/**
	 * Il nome completo del file
	 */
	private String fileName;
	/**
	 * Dimensione in Byte del file
	 */
	private long fileSize;
	/**
	 * La quantità di Byte processati finora
	 */
	protected long processed;
	/**
	 * True: Il task è stato completato
	 * <br>False: Il task non è ancora stato completato
	 */
	private boolean completed;
	/**
	 * La modalità con cui viene processato il Task.
	 * @see TaskMode
	 */
	private TaskMode mode;
	
	/**
	 * Crea un FBTask con i valori forniti
	 * @see FBTask 
	 * @param path	Indirizzo completo del file, compreso il file stesso
	 * @param name	Il nome completo del file
	 * @param tMode	La modalità con cui viene processato il Task.
	 * @see TaskMode
	 * @param fSize La dimensione in Byte del file
	 */
	public FBTask(String path, String name, TaskMode tMode, long fSize) {
		setPathName(path);
		setFileName(name);
		setFileNameNoExt(name.split("\\.")[0]);
		setMode(tMode);
		setSplittedDir(TASKS_DIR + getFileNameNoExt() + "\\");
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
	 * Metodo astratto per l'esecuzione del task, lasciato da implementare alle classi figlie
	 */
	@Override
	public abstract void run();
	
	/**
	 * Crea la cartella dentro cui verranno generati le parti del file, con il nome del file originario (meno l'estensione).
	 * <br>Viene chiamata prima di iniziare il "butchering" vero e proprio.
	 */
	protected void createButcheringResultDir() {
		File resultDir = new File(getSplittedDir());
	    if (!resultDir.exists()){
	    	resultDir.mkdir();
	    }
	}

	/**
	 * Formatta la dimensione del file (memorizzata in numero di Byte) in una versione più umanamente leggibile
	 * @return	Stringa con la dimensione espressa in:
	 * <br>- Byte, se è minore di 10^3;
	 * <br>- KB, se è maggiore di 10^3 e minore di 10^6;
	 * <br>- MB, se è maggiore di 10^6 e minore di 10^9;
	 * <br>- GB, altrimenti;
	 */
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
	
	/**
	 * Definizione base della funzione per scrivere dei byte su file
	 * @param oStream	OutputStream su cui scrivere
	 * @param bytes		Bytes di dati da scrivere
	 * @throws IOException
	 */
	protected void writeBytes(OutputStream oStream, byte[] bytes) throws IOException {
		oStream.write(bytes);
	}
	
	/** Crea l'OutputStream per una nuova parte di file, col nome costruito dal nome del file originale, il contatore delle parti e l'estensione del metodo
	 * @param fileCount	Counter delle parti, impostare -1 se non si vuole aggiungere
	 * @param append	Se aprire o no lo stream in modalità append
	 * @return	OutputStream
	 * @throws FileNotFoundException
	 */
	protected OutputStream getOutputStream(int fileCount, boolean append) throws FileNotFoundException {
		return new BufferedOutputStream(new FileOutputStream(String.format("%s.%d%s", getSplittedDir()+getFileName(), fileCount, getFileExtension()), append));
	}
	
	/** Crea l'OutputStream per un file ricostruito, nella cartella da cui è stata presa la prima parte e col nome del file originale
	 * @param currentDir	La cartella da cui è stata selezionata la prima parte del file da ricostruire
	 * @param originalFileName	Il nome originale del file ottenuto dal nome della parte
	 * @return	OutputStream
	 * @throws FileNotFoundException
	 */
	protected OutputStream getOutputStream(String currentDir, String originalFileName) throws FileNotFoundException {
		return new BufferedOutputStream(new FileOutputStream(currentDir+originalFileName, true));
	}
	
	/**
	 * @return La modalità con cui viene processato il Task.
	 * @see TaskMode
	 */
	public TaskMode getMode() {
		return mode;
	}
	
	/**
	 * @param type La modalità con cui viene processato il Task.
	 * @see TaskMode
	 */
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
	
	/**
	 * Metodo astratto lasciato da implementare alle classi figlie
	 * @return Una stringa contenente la rappresentazione dei parametri corrispondente alla classe figlia.
	 */
	public abstract String getParameters();
	
	/**
	 * Metodo astratto lasciato da implementare alle classi figlie
	 * @param param Un oggetto contenente la rappresentazione dei parametri corrispondente alla classe figlia.
	 */
	public abstract void setParameters(Object param);

	/**
	 * @return La dimensione in Byte del file
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @return La quantità di Byte processati finora
	 */
	public long getProcessed() {
		return processed;
	}

	/**
	 * @param proc Quantità di Byte processati aggiornata
	 */
	protected void setProcessed(long proc) {
		processed = proc;
		setChanged();
		notifyObservers(this); 
	}
	
	/**
	 * Metodo astratto lasciato implementato da ogni tipo di Task
	 * @return	Un decimale da 0 a 100 che rappresenta la percentuale di completamento del task
	 */
	public abstract double getProcessedPercentage();
	
	/**
	 * @return L'estensione del file corrispondente
	 * @see TaskMode
	 */
	public String getFileExtension() {
		return mode.getFileExtension();
	}

	/**
	 * @return - True se il task è stato completato;
	 * <br>- False se il task non è ancora stato completato
	 */
	public boolean isCompleted() {
		return completed;
	}

	/**
	 * @param completed Lo stato di completamento del task aggiornato
	 */
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	/**
	 * @return La cartella dove vengono generati i file processati
	 */
	public String getSplittedDir() {
		return splittedDir;
	}

	/**
	 * @param splittedDir La cartella dove vengono generati i file processati
	 */
	public void setSplittedDir(String splittedDir) {
		this.splittedDir = splittedDir;
	}

	
}
