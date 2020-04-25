package logic.tasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class FBTaskButcherSameSize extends FBTask {
	
	private long partSize;
	
	public FBTaskButcherSameSize(String path, String name, long fileSize, long pSize){
		super(path, name, TaskMode.BUTCHER_SAME_SIZE, fileSize);
		partSize = pSize < fileSize ? pSize : fileSize;
	}

	/** 
	 * Default
	 * @param path		Nome completo di indirizzo del file
	 * @param name		Solo il nome del file, senza il path
	 * @param fileSize	Grandezza del file
	 */
	public FBTaskButcherSameSize(String path, String name, long fileSize) {
		this(path, name, fileSize, 100*1000);
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
			
			InputStream iStream = new BufferedInputStream(new FileInputStream(getPathName()));
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
	
	/** Ritorna l'OutputStream appropriato
	 * @param fileCount Counter delle parti, impostare -1 se non si vuole aggiungere
	 * @param append	Se aprire o no lo stream in modalità append
	 * @return L'OutputStream appropriato
	 * @throws FileNotFoundException
	 */
	private OutputStream getStream(int fileCount, boolean append) throws FileNotFoundException { 
			return new BufferedOutputStream(new FileOutputStream(String.format("%s.%d%s", RESULT_DIR+getFileName(), fileCount, getFileExtension()), append));
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
