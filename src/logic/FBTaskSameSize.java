package logic;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

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
			long currentSize = 0;
			int bt;
			InputStream iStream = new BufferedInputStream(new FileInputStream(getPathName()));
			OutputStream oStream = new BufferedOutputStream(new FileOutputStream(String.format("%s.%d%s", getResultDirectory()+getFileName(), fileCount, getFileExtension())));
						
			while((bt = iStream.read()) != -1) {
				if(currentSize >= partSize) {
					oStream.close();
					fileCount++;
					currentSize = 0;
					oStream = new BufferedOutputStream(new FileOutputStream(String.format("%s.%d%s", getResultDirectory()+getFileName(), fileCount, getFileExtension())));
				}
				oStream.write(bt);
				currentSize++;
			}
			
			iStream.close();
			oStream.close();
			Desktop.getDesktop().open(new File(getResultDirectory()));
		
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
