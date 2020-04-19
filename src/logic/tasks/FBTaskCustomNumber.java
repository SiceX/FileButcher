package logic.tasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.regex.Pattern;

public class FBTaskCustomNumber extends FBTask {
	
	private int numberOfParts;

	public FBTaskCustomNumber(String path, String name, boolean doRebuild, long fileSize, int nParts){
		super(path, name, TaskMode.CUSTOM_NUMBER, doRebuild, fileSize);
		numberOfParts = nParts;
	}
	
	/**
	 * Default
	 * @param path
	 * @param name
	 * @param fileSize
	 */
	public FBTaskCustomNumber(String path, String name, long fileSize){
		this(path, name, false, fileSize, 2);
	}
	
	/**
	 * Rebuild
	 * @param path
	 * @param name
	 * @param doRebuild
	 * @param fileSize
	 */
	public FBTaskCustomNumber(String path, String name){
		this(path, name, true, 0, 0);
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
		File currentDirectory = new File(super.getPathName().substring(0, super.getPathName().length() - super.getFileName().length()));
		File matchingFiles[] = getMatchingFiles(currentDirectory);
		
		//TODO
	}
	
	private File[] getMatchingFiles(File currentDirectory) {
		String tokens[] = super.getFileName().split("\\.");
		String extension = super.getFileExtension();
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<tokens.length-2; i++) {
			sb.append(tokens[i]).append(".");
		}
		sb.deleteCharAt(sb.length()-1);
		String matchName = sb.toString();
		
		File[] matchingFiles = currentDirectory.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		    	Pattern regex = Pattern.compile(matchName.replaceAll("\\.", "\\\\.") + "\\.\\d+" + extension.replaceAll("\\.", "\\\\."));
		        return name.matches(regex.toString());
		    }
		});
		
		return matchingFiles;
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
