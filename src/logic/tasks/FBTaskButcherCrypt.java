package logic.tasks;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Definisce il procedimento per scomporre un file in parti uguali cifrate, estende la classe FBTask
 * @see FBTask
 * @see TaskMode
 * @author Sice
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
	 * 
	 */
	private byte[] encodedKey;
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
	 * Scomposizione in parti uguali e cifratura
	 */
	@Override
	public void run() {
		super.createButcheringResultDir();
		
		try {
			int fileCount = 1;
			long currentPartSize = 0;
			processed = 0;
			
			initCipher();
			
			InputStream iStream = new FileInputStream(getPathName());
			OutputStream oStream;
			
			do {
 				oStream = getStream(fileCount, false);
				long remainingBytes = getFileSize() - processed;
 				currentPartSize = ( partSize < remainingBytes ) ? partSize : remainingBytes;
				
				while(currentPartSize > BLOCK_MAX_SIZE) {
					byte[] bytes = new byte[BLOCK_MAX_SIZE];
					iStream.read(bytes, 0, BLOCK_MAX_SIZE);
					oStream.write(bytes);
					oStream.close();
					oStream = getStream(fileCount, true);
					currentPartSize -= BLOCK_MAX_SIZE;
					setProcessed(processed + BLOCK_MAX_SIZE);
				}
				
				byte[] bytes = new byte[(int)currentPartSize];
				iStream.read(bytes, 0, (int)currentPartSize);
				
				oStream.write(bytes);
				oStream.close();
				// Salvo la chiave codificata nel primo file
				if(fileCount == 1) { 
					oStream = new BufferedOutputStream(new FileOutputStream(String.format("%s.%d%s", getSplittedDir()+getFileName(), fileCount, getFileExtension()), true));
					oStream.write(encodedKey);
					oStream.write(iv);
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
	
	/** Inizializzazione del Cipher con la password fornita
	 * Algoritmo AES con padding e metodo CBC (instead of EBC). PKCS5Padding
	 * Mentre EBC critta i blocchi indipendentemente l'uno dall'altro,
	 * CBC usa uno XOR tra i blocchi per rendere la codifica nel complesso più sicura.
	 * @throws Exception
	 */
	private void initCipher() throws Exception {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256); // Key size
		SecretKey key = keyGen.generateKey();
		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
		encodedKey = key.getEncoded();
		iv = cipher.getIV();
	}
	
	/** Ritorna l'OutputStream appropriato, normale o criptato
	 * @param fileCount Counter delle parti, impostare -1 se non si vuole aggiungere
	 * @param append	Se aprire o no lo stream in modalità append
	 * @return L'OutputStream appropriato, normale o criptato
	 * @throws FileNotFoundException
	 */
	private OutputStream getStream(int fileCount, boolean append) throws FileNotFoundException { 
		return new CipherOutputStream(new FileOutputStream(String.format("%s.%d%s", getSplittedDir()+getFileName(), fileCount, getFileExtension()), append), cipher);
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

	@Override
	public double getProcessedPercentage() {
		double progress = ((double)getProcessed() / getFileSize()) * 100;
		return progress;
	}

}
