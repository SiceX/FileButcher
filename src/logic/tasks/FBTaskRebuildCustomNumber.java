package logic.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * Definisce il procedimento per ricostruire un file dalle sue N parti, estende la classe FBTask
 * @see FBTask
 * @see TaskMode
 * @author Nicola Ferrari
 */
public class FBTaskRebuildCustomNumber extends FBTask {
	
	/**
	 * La cartella dove si trova la prima parte, sarà anche dove sarà messo il file ricostruito
	 */
	private String currentDir;
	/**
	 * Nome originale del file, ricavato dal nome della prima parte
	 */
	private String originalFileName;
	/**
	 * Array con i puntamenti alle altre parti da cui ricostruire il file originario
	 */
	private File matchingFiles[];
	/**
	 * Dimensione calcolata che avrà il file ricostruito
	 */
	private long rebuiltFileSize;

	/**
	 * Crea un FBTaskRebuildCustomNumber con i valori forniti.
	 * <br>Vengono salvati anche:
	 * <br>- La cartella che contiene la prima parte;
	 * <br>- Il nome del file originario (ottenuto dal nome della parte);
	 * <br>- L'Array delle altre parti trovate nella stessa cartella.
	 * @param path	Indirizzo completo della prima parte, compresa la parte stessa
	 * @param name	Il nome completo della prima parte
	 * @param fileSize	La dimensione in Byte della prima parte
	 */
	public FBTaskRebuildCustomNumber(String path, String name, long fileSize){
		super(path, name, TaskMode.REBUILD_CUSTOM_NUMBER, fileSize);
		currentDir = super.getPathName().substring(0, super.getPathName().length() - super.getFileName().length());
		String tokens[] = super.getFileName().split("\\.");
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<tokens.length-2; i++) {
			sb.append(tokens[i]).append(".");
		}
		sb.deleteCharAt(sb.length()-1);
		originalFileName = sb.toString();
		
		rebuiltFileSize = 0;
		File currentDirectory = new File(super.getPathName().substring(0, super.getPathName().length() - super.getFileName().length()));
		matchingFiles = getMatchingFiles(currentDirectory);
	}
	
	/**
	 * Esegue la ricostruzione del file originario dalle sue parti N parti.
	 * <br>Questo metodo si occupa di:
	 * <br>- Leggere da ogni parte e ricopiarne i dati nel nuovo file ricostruito;
	 * <br>- Se la grandezza della parte è maggiore della costante definita nella superclasse BLOCK_MAX_SIZE (50 MB), 
	 * 		 la lettura e scrittura viene suddivisa in più scritture separate di grandezza BLOCK_MAX_SIZE per evitare di trattenere un eccessiva quantità di dati in memoria;
	 * <br>- Aggiornare il valore del progresso.
	 * @see BLOCK_MAX_SIZE
	 */
	@Override
	public void run() {		
		try {
			long currentFileSize;
			setProcessed(0);
			
			OutputStream oStream;
			InputStream iStream;
			
			for(int i=0; i<matchingFiles.length; i++) {
				oStream = getOutputStream(currentDir, originalFileName);
				iStream = new BufferedInputStream(new FileInputStream(matchingFiles[i].getPath()));
				currentFileSize = matchingFiles[i].length();

				while(currentFileSize > BLOCK_MAX_SIZE) {
					byte[] bytes = new byte[BLOCK_MAX_SIZE];
					iStream.read(bytes);
					writeBytes(oStream, bytes);
					oStream.close();
					oStream = getOutputStream(currentDir, originalFileName);
					currentFileSize -= BLOCK_MAX_SIZE;
					setProcessed(processed + BLOCK_MAX_SIZE);
				}
				
				byte[] bytes = new byte[(int)currentFileSize];
				iStream.read(bytes);
				
				writeBytes(oStream, bytes);
				oStream.close();
				iStream.close();
				setProcessed(processed + currentFileSize);
			}
			setCompleted(true);
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	/** Ritrovamento delle altre parti del file dalla stessa directory
	 * @param currentDirectory	Directory dove si trova la prima parte
	 * @return Array di File contenente le parti successive
	 */
	private File[] getMatchingFiles(File currentDirectory) {
		String extension = super.getFileExtension();
		String matchName = originalFileName;
		
		File[] matchingFiles = currentDirectory.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		    	Pattern regex = Pattern.compile(matchName.replaceAll("\\.", "\\\\.") + "\\.\\d+" + extension.replaceAll("\\.", "\\\\."));
		        return name.matches(regex.toString());
		    }
		});

		for(int i=0; i<matchingFiles.length; i++) {
			rebuiltFileSize += matchingFiles[i].length();
		}
		return matchingFiles;
	}

	/**
	 * Formatta la dimensione che avrà il file originario (calcolata in numero di Byte) in una versione più umanamente leggibile
	 * @return	Stringa con la dimensione espressa in:
	 * <br>- Byte, se è minore di 10^3;
	 * <br>- KB, se è maggiore di 10^3 e minore di 10^6;
	 * <br>- MB, se è maggiore di 10^6 e minore di 10^9;
	 * <br>- GB, altrimenti;
	 */
	@Override
	public String getFileSizeFormatted() {
		DecimalFormat df = new DecimalFormat("#.##");
		if(rebuiltFileSize < 1000) {
			return rebuiltFileSize + " B";
		}
		else if(rebuiltFileSize < 1000000) {
			return df.format(((double)rebuiltFileSize)/1000) + " KB";
		}
		else if(rebuiltFileSize < 1000000000) {
			return df.format(((double)rebuiltFileSize)/1000000) + " MB";
		}
		else {
			return df.format(((double)rebuiltFileSize)/1000000000) + " GB";
		}
	}
	
	/**
	 * @return Una stringa contenente la quantità di parti che sono state trovate
	 */
	@Override
	public String getParameters() {
		return matchingFiles.length + " parti";
	}

	/**
	 * Non è possibile cambiare i parametri di un Task di ricostruzione
	 */
	@Override
	public void setParameters(Object param) {}
	
	/**
	 * Calcola il progresso nell'esecuzione del Task come numero di byte processati divisa la dimensione che avrà il file ricostruito, per 100.
	 */
	@Override
	public double getProcessedPercentage() {
		double progress = ((double)getProcessed() / rebuiltFileSize) * 100;
		return progress;
	}
}
