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

public class FBTaskSameSize extends FBTask {
	
	private long partSize;
	private boolean doCrypt;
	private Cipher cipher;
	
	public FBTaskSameSize(String path, String name, boolean doRebuild, long fileSize, long pSize, boolean crypt){
		super(path, name, crypt ? TaskMode.CRYPT_SAME_SIZE : TaskMode.SAME_SIZE, false, fileSize);
		partSize = pSize < fileSize ? pSize : fileSize;
		doCrypt = crypt;
	}

	/** Default
	 * @param path
	 * @param name
	 * @param fileSize
	 * @param crypt
	 */
	public FBTaskSameSize(String path, String name, long fileSize, boolean crypt) {
		this(path, name, false, fileSize, 100*1000, crypt);
	}
	
	/**
	 * Esecuzione della divisione in parti uguali
	 */
	@Override
	public void run() {
		if(!super.isRebuild) {
			doButchering();
		}
		else {
			doRebuilding();
		}
	}
	
	@Override
	protected void doButchering() {
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
	
	@Override
	protected void doRebuilding() {
		
	}
	
	private void initCipher() throws Exception {
		byte[] key = password.getBytes("UTF-8");
	    MessageDigest sha = MessageDigest.getInstance("SHA-1");
	    key = sha.digest(key);
	    key = Arrays.copyOf(key, 16); // use only first 128 bit

	    SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
	    cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
	}
	
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
