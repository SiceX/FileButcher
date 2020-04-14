package logic.tasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FBTaskCustomNumber extends FBTask {
	
	private int numberOfParts;

	public FBTaskCustomNumber(String path, String name, boolean doRebuild, long fileSize, int nParts){
		super(path, name, TaskMode.CUSTOM_NUMBER, doRebuild, fileSize);
		numberOfParts = nParts;
	}
	
	//Default
	public FBTaskCustomNumber(String path, String name, long fileSize){
		this(path, name, false, fileSize, 2);
	}
	
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
			long partSize = getFileSize()/numberOfParts;
			long carryBytes = getFileSize()-(partSize*numberOfParts);
			long currentPartSize = 0;
			processed = 0;
			
			InputStream iStream = new BufferedInputStream(new FileInputStream(getPathName()));
			BufferedOutputStream oStream;
			
			for(int i=0; i<numberOfParts; i++) {
				oStream = new BufferedOutputStream(new FileOutputStream(String.format("%s.%d%s", RESULT_DIR+getFileName(), i+1, getFileExtension())));
				//Questo è per assicurarsi che l'ultima parte non si perda eventuali byte "di riporto"
 				currentPartSize = i != numberOfParts-1 ? partSize : partSize+carryBytes;
				
				while(currentPartSize > BLOCK_MAX_SIZE) {
					byte[] bytes = new byte[BLOCK_MAX_SIZE];
					iStream.read(bytes, 0, BLOCK_MAX_SIZE);
					oStream.write(bytes);
					oStream.close();
					oStream = new BufferedOutputStream(new FileOutputStream(String.format("%s.%d%s", RESULT_DIR+getFileName(), i+1, getFileExtension()), true));
					currentPartSize -= BLOCK_MAX_SIZE;
					setProcessed(processed + BLOCK_MAX_SIZE);
				}
				
				byte[] bytes = new byte[(int)currentPartSize];
				iStream.read(bytes, 0, (int)currentPartSize);
				
				oStream.write(bytes);
				oStream.close();
				setProcessed(processed + currentPartSize);
			}
			
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

	@Override
	public String getParameters() {
		return Integer.toString(numberOfParts);
	}
	
	@Override
	public void setParameters(Object param) {
		if(param.getClass() == Integer.class) {
			numberOfParts = (int)param;
		}
	}
}
