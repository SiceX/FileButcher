package logic;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class FBTaskCryptSameSize extends FBTask {
	
	private long partSize;
	private String password;
	
	public FBTaskCryptSameSize(String path, String name, long fileSize, long pSize){
		super(path, name, TaskMode.CRYPT_SAME_SIZE, fileSize);
		partSize = pSize;
	}
	
	//Default
	public FBTaskCryptSameSize(String path, String name, long fileSize) {
		this(path, name, fileSize, 500);
	}

	@Override
	public void run() {
		try {
			int fileCount = 1;
			long currentSize = 0;
			byte[] bytes = new byte[(int)partSize];
			
			InputStream iStream = new BufferedInputStream(new FileInputStream(getPathName()));
			OutputStream oStream = new BufferedOutputStream(new FileOutputStream(String.format("%s.%d%s", getResultDirectory()+getFileName(), fileCount, getFileExtension())));
			
//			SecureRandom sr = SecureRandom.getInstanceStrong();
//		    byte[] salt = new byte[16];
//		    sr.nextBytes(salt);
//		    
//		    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//		    keyGenerator.init
//		    
//		    PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1000, 128 * 8);
//		    SecretKey key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec);
//		    Cipher cipher = Cipher.getInstance("AES");
//		    cipher.init(Cipher.ENCRYPT_MODE, key);
						
		    
		    
		    byte[] key = password.getBytes("UTF-8");
		    MessageDigest sha = MessageDigest.getInstance("SHA-1");
		    key = sha.digest(key);
		    key = Arrays.copyOf(key, 16); // use only first 128 bit

		    SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		    Cipher cipher = Cipher.getInstance("AES");
		    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		    
		    
			while(iStream.read(bytes, 0, (int)partSize) != -1) {
				if(currentSize >= partSize) {
					oStream.close();
					fileCount++;
					currentSize = 0;
					oStream = new BufferedOutputStream(new FileOutputStream(String.format("%s.%d%s", getResultDirectory()+getFileName(), fileCount, getFileExtension())));
				}
				oStream.write(cipher.doFinal(bytes));
				currentSize += partSize;
			}
			
			iStream.close();
			oStream.close();
			Desktop.getDesktop().open(new File(getResultDirectory()));
		
		}
		catch(Throwable e) {
			System.err.println(e.getMessage());
			//throw e;
			//TODO
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

	public void setPassword(String cryptKey) {
		this.password = cryptKey;
	}

}
