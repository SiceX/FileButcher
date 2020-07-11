package logic.tasks;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;

/**
 * Definisce il procedimento per scomporre un file in numero N di parti specificato dall'utente, estende la classe FBTask
 * @see FBTask
 * @see TaskMode
 * @author Nicola Ferrari
 */
public class FBTaskButcherCustomNumber extends FBTask {
	
	/**
	 * Numero di parti in cui scomporre il file
	 */
	private int numberOfParts;

	/**
	 * Crea un FBTaskButcherCustomNumber con i valori forniti.
	 * @param path	Indirizzo completo del file, compreso il file stesso
	 * @param name	Il nome completo del file
	 * @param fileSize	La dimensione in Byte del file
	 * @param nParts	Numero di parti in cui scomporre il file
	 */
	public FBTaskButcherCustomNumber(String path, String name, long fileSize, int nParts){
		super(path, name, TaskMode.BUTCHER_CUSTOM_NUMBER, fileSize);
		numberOfParts = nParts;
	}
	
	/** 
	 * Crea un FBTaskButcherCustomNumber di default con i valori forniti e nParts uguale a 2.
	 * @param path	Indirizzo completo del file, compreso il file stesso
	 * @param name	Il nome completo del file
	 * @param fileSize	La dimensione in Byte del file
	 */
	public FBTaskButcherCustomNumber(String path, String name, long fileSize){
		this(path, name, fileSize, 2);
	}
	
	/**
	 * Esegue la scomposizione in N parti.
	 * <br>Questo metodo si occupa di:
	 * <br>- Creare la cartella dove verranno generate le parti (Documents/Splitted Files/[nome file da dividere]);
	 * <br>- Leggere dal file originario e scrivere i dati nei file .parn, di dimensione uguale alla dimensione del file da dividere fratto N e nominandoli con un contatore di parte;
	 * <br>- Se la grandezza della parte è maggiore della costante definita nella superclasse BLOCK_MAX_SIZE (50 MB), la lettura e scrittura della parte viene suddivisa in più scritture separate
	 * 		 di grandezza BLOCK_MAX_SIZE per evitare di trattenere un eccessiva quantità di dati in memoria;
	 * <br>- Aggiornare il valore del progresso.
	 * @see BLOCK_MAX_SIZE
	 * @see Cipher
	 */
	@Override
	public void run() {
		super.createButcheringResultDir();
		
		try {
			long partSize = getFileSize()/numberOfParts;
			long carryBytes = getFileSize()-(partSize*numberOfParts);
			long currentPartSize = 0;
			processed = 0;
			
			InputStream iStream = new FileInputStream(getPathName());
			OutputStream oStream;
			
			for(int i=0; i<numberOfParts; i++) {
				oStream = getOutputStream(i+1, false);
				//Questo è per assicurarsi che l'ultima parte non si perda eventuali byte "di riporto"
 				currentPartSize = i != numberOfParts-1 ? partSize : partSize+carryBytes;
				
				while(currentPartSize > BLOCK_MAX_SIZE) {
					byte[] bytes = new byte[BLOCK_MAX_SIZE];
					iStream.read(bytes, 0, BLOCK_MAX_SIZE);
					writeBytes(oStream, bytes);
					oStream.close();
					oStream = getOutputStream(i+1, true);
					currentPartSize -= BLOCK_MAX_SIZE;
					setProcessed(processed + BLOCK_MAX_SIZE);
				}
				
				byte[] bytes = new byte[(int)currentPartSize];
				iStream.read(bytes, 0, (int)currentPartSize);
				
				writeBytes(oStream, bytes);
				oStream.close();
				setProcessed(processed + currentPartSize);
			}
			setCompleted(true);
			iStream.close();
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Una stringa contenente la quantità di parti in cui dividere il file
	 */
	@Override
	public String getParameters() {
		return Integer.toString(numberOfParts);
	}
	
	/**
	 * @param param	Un int contenente la quantità di parti in cui dividere il file
	 */
	@Override
	public void setParameters(Object param) {
		if(param.getClass() == Integer.class) {
			numberOfParts = (int)param;
		}
	}
	
	/**
	 * Calcola il progresso nell'esecuzione del Task come numero di byte processati divisa la dimensione del file intero, per 100.
	 */
	@Override
	public double getProcessedPercentage() {
		double progress = ((double)getProcessed() / getFileSize()) * 100;
		return progress;
	}

}
