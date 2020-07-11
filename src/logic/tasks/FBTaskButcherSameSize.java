package logic.tasks;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import javax.crypto.Cipher;

/**
 * Definisce il procedimento per scomporre un file in parti uguali, estende la classe FBTask
 * @see FBTask
 * @see TaskMode
 * @author Nicola Ferrari
 */
public class FBTaskButcherSameSize extends FBTask {
	
	/**
	 * Dimensione delle parti specificata dall'utente, utilizzata all'interno della classe
	 */
	private long partSize;
	
	/**
	 * Crea un FBTaskButcherSameSize con i valori forniti.
	 * Se pSize fosse più grande della dimensione effettiva del file, viene preso come pSize fileSize, 
	 * creando quindi solo una versione cifrata del file originario. 
	 * @param path	Indirizzo completo del file, compreso il file stesso
	 * @param name	Il nome completo del file
	 * @param fileSize	La dimensione in Byte del file
	 * @param pSize	La dimensione delle parti specificata dall'utente
	 */
	public FBTaskButcherSameSize(String path, String name, long fileSize, long pSize){
		super(path, name, TaskMode.BUTCHER_SAME_SIZE, fileSize);
		partSize = pSize < fileSize ? pSize : fileSize;
	}

	/** 
	 * Crea un FBTaskButcherSameSize di default con i valori forniti e pSize uguale a 100 KB.
	 * Se la dimensione effettiva del file fosse minore di 100 KB, viene presa come dimensione delle parti fileSize, 
	 * creando quindi solo una versione cifrata del file originario. 
	 * @param path		Nome completo di indirizzo del file
	 * @param name		Solo il nome del file, senza il path
	 * @param fileSize	Grandezza del file
	 */
	public FBTaskButcherSameSize(String path, String name, long fileSize) {
		this(path, name, fileSize, 100*1000);
	}
	
	/**
	 * Esegue la scomposizione in parti uguali.
	 * <br>Questo metodo si occupa di:
	 * <br>- Creare la cartella dove verranno generate le parti (Documents/Splitted Files/[nome file da dividere]);
	 * <br>- Leggere dal file originario e scrivere i dati nei file .par, nominandoli con un contatore di parte;
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
			int fileCount = 1;
			long currentPartSize = 0;
			processed = 0;
			
			InputStream iStream = new FileInputStream(getPathName());
			OutputStream oStream;
			
			do {
 				oStream = getOutputStream(fileCount, false);
				long remainingBytes = getFileSize() - processed;
 				currentPartSize = ( partSize < remainingBytes ) ? partSize : remainingBytes;
				
				while(currentPartSize > BLOCK_MAX_SIZE) {
					byte[] bytes = new byte[BLOCK_MAX_SIZE];
					iStream.read(bytes, 0, BLOCK_MAX_SIZE);
					writeBytes(oStream, bytes);
					oStream.close();
					oStream = getOutputStream(fileCount, true);
					currentPartSize -= BLOCK_MAX_SIZE;
					setProcessed(processed + BLOCK_MAX_SIZE);
				}
				
				byte[] bytes = new byte[(int)currentPartSize];
				iStream.read(bytes, 0, (int)currentPartSize);
				
				writeBytes(oStream, bytes);
				oStream.close();
				fileCount++;
				setProcessed(processed + currentPartSize);
				
			}while(iStream.available() > 0);
			setCompleted(true);
			iStream.close();
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Una stringa contenente la dimensione massima di ogni parte in cui dividere il file
	 */
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
	
	/**
	 * @param param	Un long contenente la dimensione massima di ogni parte in cui dividere il file
	 */
	@Override
	public void setParameters(Object param) {
		if(param.getClass() == Long.class) {
			partSize = (long)param;
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
