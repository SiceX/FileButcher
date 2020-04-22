package logic.tasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class FBTaskButcherSameSize extends FBTask {
	
	private long partSize;
	private boolean doCrypt;
	private Cipher cipher;
	
	public FBTaskButcherSameSize(String path, String name, long fileSize, long pSize, boolean crypt){
		super(path, name, crypt ? TaskMode.BUTCHER_CRYPT_SAME_SIZE : TaskMode.BUTCHER_SAME_SIZE, fileSize);
		partSize = pSize < fileSize ? pSize : fileSize;
		doCrypt = crypt;
	}

	/** 
	 * Default
	 * @param path		Nome completo di indirizzo del file
	 * @param name		Solo il nome del file, senza il path
	 * @param fileSize	Grandezza del file
	 * @param crypt		Se criptare o no
	 */
	public FBTaskButcherSameSize(String path, String name, long fileSize, boolean crypt) {
		this(path, name, fileSize, 100*1000, crypt);
	}
	
	/**
	 * Scomposizione in parti uguali
	 */
	@Override
	public void run() {
		try {
			int fileCount = 1;
			long currentPartSize = 0;
			processed = 0;
			
			if(doCrypt) {
				initCipher();
			}
			
			InputStream iStream = new BufferedInputStream(new FileInputStream(getPathName()));
			OutputStream oStream;
			
			do {
 				oStream = getProperStream(fileCount, false);
				long remainingBytes = getFileSize() - processed;
 				currentPartSize = ( partSize < remainingBytes ) ? partSize : remainingBytes;
				
				while(currentPartSize > BLOCK_MAX_SIZE) {
					byte[] bytes = new byte[BLOCK_MAX_SIZE];
					iStream.read(bytes, 0, BLOCK_MAX_SIZE);
					oStream.write(bytes);
					oStream.close();
					oStream = getProperStream(fileCount, true);
					currentPartSize -= BLOCK_MAX_SIZE;
					setProcessed(processed + BLOCK_MAX_SIZE);
				}
				
				byte[] bytes = new byte[(int)currentPartSize];
				iStream.read(bytes, 0, (int)currentPartSize);
				
				oStream.write(bytes);
				oStream.close();
				fileCount++;
				setProcessed(processed + currentPartSize);
				
			}while(iStream.available() > 0);
			
			iStream.close();
		}
		catch(Throwable e) {
			//throw e;
			//TODO
		}
	}
	
	/** Inizializzazione del Cipher con la password fornita
	 * @throws Exception
	 */
	private void initCipher() throws Exception {
		byte[] key = password.getBytes("UTF-8");
	    MessageDigest sha = MessageDigest.getInstance("SHA-1");
	    key = sha.digest(key);
	    key = Arrays.copyOf(key, 16); // use only first 128 bit

	    SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
	    cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
	}
	
	/** Ritorna l'OutputStream appropriato, normale o criptato
	 * @param fileCount Counter delle parti, impostare -1 se non si vuole aggiungere
	 * @param append	Se aprire o no lo stream in modalità append
	 * @return L'OutputStream appropriato, normale o criptato
	 * @throws FileNotFoundException
	 */
	private OutputStream getProperStream(int fileCount, boolean append) throws FileNotFoundException {
		if(doCrypt) {
			return new CipherOutputStream(new FileOutputStream(String.format("%s.%d%s", RESULT_DIR+getFileName(), fileCount, getFileExtension()), append), cipher);
		}
		else {
			return new BufferedOutputStream(new FileOutputStream(String.format("%s.%d%s", RESULT_DIR+getFileName(), fileCount, getFileExtension()), append));
		}
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
