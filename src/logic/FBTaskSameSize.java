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
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class FBTaskSameSize extends FBTask {
	
	private long partSize;
	
	public FBTaskSameSize(String path, String name, long fileSize, long pSize){
		super(path, name, TaskMode.SAME_SIZE, fileSize);
		partSize = pSize;
	}
	
	//Default
	public FBTaskSameSize(String path, String name, long fileSize) {
		this(path, name, fileSize, 500);
	}
	
	/**
	 * Esecuzione della divisione in parti uguali
	 */
	@Override
	public void run() {
		try {
			int fileCount = 1;
			long currentPartSize = 0;
			long bytesRead = 0;
			
			InputStream iStream = new BufferedInputStream(new FileInputStream(getPathName()));
			BufferedOutputStream coStream;
			
			do {
 				coStream = new BufferedOutputStream(new FileOutputStream(String.format("%s.%d%s", RESULT_DIR+getFileName(), fileCount, getFileExtension())));
				long remainingBytes = getFileSize() - bytesRead;
 				currentPartSize = ( partSize < remainingBytes ) ? partSize : remainingBytes;
				
				while(currentPartSize > BLOCK_MAX_SIZE) {
					byte[] bytes = new byte[BLOCK_MAX_SIZE];
					iStream.read(bytes, 0, BLOCK_MAX_SIZE);
					coStream.write(bytes);
					coStream.close();
					coStream = new BufferedOutputStream(new FileOutputStream(String.format("%s.%d%s", RESULT_DIR+getFileName(), fileCount, getFileExtension()), true));
					currentPartSize -= BLOCK_MAX_SIZE;
					bytesRead += BLOCK_MAX_SIZE;
				}
				
				byte[] bytes = new byte[(int)currentPartSize];
				iStream.read(bytes, 0, (int)currentPartSize);
				
				coStream.write(bytes);
				coStream.close();
				fileCount++;
				bytesRead += currentPartSize;
				
			}while(iStream.available() > 0);
			
			iStream.close();
			Desktop.getDesktop().open(new File(RESULT_DIR));
		
		}
		catch(Throwable e) {
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

}
