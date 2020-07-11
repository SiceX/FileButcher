package logic.tasks;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Definisce il procedimento per scomporre un file in parti uguali cifrate, estende la classe FBTask
 * @see FBTask
 * @see TaskMode
 * @author Nicola Ferrari
 */
public class FBTaskButcherCrypt extends FBTask {
	
	/**
	 * Dimensione delle parti specificata dall'utente, utilizzata all'interno della classe
	 */
	private long partSize;
	/**
	 * Cifrario utilizzato per cifrare il file
	 */
	private Cipher cipher;
	/**
	 * Chiave di codifica per la cifratura
	 */
	private byte[] encodedKey;
	/**
	 * Vettore di inizializzazione per la cifratura
	 */
	private byte[] iv;
	
	/**
	 * Crea un FBTaskButcherCrypt con i valori forniti.
	 * Se pSize fosse più grande della dimensione effettiva del file, viene preso come pSize fileSize, 
	 * creando quindi solo una versione cifrata del file originario. 
	 * @param path	Indirizzo completo del file, compreso il file stesso
	 * @param name	Il nome completo del file
	 * @param fileSize	La dimensione in Byte del file
	 * @param pSize	La dimensione delle parti specificata dall'utente
	 */
	public FBTaskButcherCrypt(String path, String name, long fileSize, long pSize){
		super(path, name, TaskMode.BUTCHER_CRYPT_SAME_SIZE, fileSize);
		partSize = pSize < fileSize ? pSize : fileSize;
	}

	/** 
	 * Crea un FBTaskButcherCrypt di default con i valori forniti e pSize uguale a 100 KB.
	 * Se la dimensione effettiva del file fosse minore di 100 KB, viene presa come dimensione delle parti fileSize, 
	 * creando quindi solo una versione cifrata del file originario. 
	 * @param path		Nome completo di indirizzo del file
	 * @param name		Solo il nome del file, senza il path
	 * @param fileSize	Grandezza del file
	 */
	public FBTaskButcherCrypt(String path, String name, long fileSize) {
		this(path, name, fileSize, 100*1000);
	}
	
	/**
	 * Esegue la scomposizione in parti uguali e la cifratura.
	 * <br>Questo metodo si occupa di:
	 * <br>- Creare la cartella dove verranno generate le parti (Documents/Splitted Files/[nome file da dividere]);
	 * <br>- Inizializzare il cifrario;
	 * <br>- Leggere dal file originario e scrivere i dati cifrati nei file .crypar, nominandoli con un contatore di parte;
	 * <br>- Se la grandezza della parte è maggiore della costante definita nella superclasse BLOCK_MAX_SIZE (50 MB), la lettura e scrittura della parte viene suddivisa in più scritture separate
	 * 		 di grandezza BLOCK_MAX_SIZE per evitare di trattenere un eccessiva quantità di dati in memoria;
	 * <br>- Aggiornare il valore del progresso;
	 * <br>- Salvare in fondo alla prima parte la chiave generata e il vettore iniziale.
	 * @see encodedKey
	 * @see iv
	 * @see BLOCK_MAX_SIZE
	 * @see Cipher
	 */
	@Override
	public void run() {
		super.createButcheringResultDir();
		try {
			initCipher();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
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
				// Salvo la chiave codificata nel primo file
				if(fileCount == 1) { 
					oStream = getOutputStream(fileCount, true);
					super.writeBytes(oStream, encodedKey);
					super.writeBytes(oStream, iv);
					oStream.close();
				}
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
	
	/** Inizializzazione del Cipher con una chiave generata sul momento
	 * Algoritmo AES con PKCS5Padding e metodo CBC.
	 * CBC usa uno XOR tra i blocchi per rendere la codifica nel complesso più sicura.
	 */
	private void initCipher(){
		KeyGenerator keyGen;
		try {
			keyGen = KeyGenerator.getInstance("AES");
		
			keyGen.init(256); // Key size
			SecretKey key = keyGen.generateKey();
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			encodedKey = key.getEncoded();
			iv = cipher.getIV();
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			// Non davvero possibile che vengano lanciate queste eccezioni
			e.printStackTrace();
		}
	}
	
	/**
	 * Sovrascrittura della funzione di base per scrivere dei byte su file
	 * con gestione della cifratura dei dati tramite il Cipher
	 * @see Cipher
	 * @param oStream	OutputStream su cui scrivere
	 * @param bytes		Bytes di dati da scrivere
	 * @throws IOException
	 */
	@Override
	protected void writeBytes(OutputStream oStream, byte[] bytes) throws IOException {
		try {
			oStream.write(cipher.doFinal(bytes));
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
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
