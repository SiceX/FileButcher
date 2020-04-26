package logic.tasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class FBTaskRebuildSameSize extends FBTask {

	private String currentDir;
	private String originalFileName;
	private File matchingFiles[];
	private long rebuiltFileSize;

	/**
	 * Rebuild Task
	 * @param path		Nome completo di indirizzo del file
	 * @param name		Solo il nome del file, senza il path
	 * @param fileSize	Grandezza del file
	 */
	public FBTaskRebuildSameSize(String path, String name, long fileSize) {
		super(path, name, TaskMode.REBUILD_SAME_SIZE, fileSize);
		currentDir = super.getPathName().substring(0, super.getPathName().length() - super.getFileName().length());
		String tokens[] = super.getFileName().split("\\.");
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<tokens.length-2; i++) {
			sb.append(tokens[i]).append(".");
		}
		sb.deleteCharAt(sb.length()-1);
		originalFileName = sb.toString();

		rebuiltFileSize = 0;
		File currentDirectory = new File(super.getPathName().substring(0, super.getPathName().length() - super.getFileName().length()));
		matchingFiles = getMatchingFiles(currentDirectory);
	}
	
	/**
	 * Ricostruzione del file dalle sue parti
	 */
	@Override
	public void run() {
		try {
			long currentFileSize;
			setProcessed(0);
			
			OutputStream oStream;
			InputStream iStream;
			
			for(int i=0; i<matchingFiles.length; i++) {
				oStream = new BufferedOutputStream(new FileOutputStream(currentDir+originalFileName, true));
				iStream = new BufferedInputStream(new FileInputStream(matchingFiles[i].getPath()));
				currentFileSize = matchingFiles[i].length();

				while(currentFileSize > BLOCK_MAX_SIZE) {
					byte[] bytes = new byte[BLOCK_MAX_SIZE];
					iStream.read(bytes);
					oStream.write(bytes);
					oStream.close();
					oStream = new BufferedOutputStream(new FileOutputStream(currentDir+originalFileName, true));
					currentFileSize -= BLOCK_MAX_SIZE;
					setProcessed(processed + BLOCK_MAX_SIZE);
				}
				
				byte[] bytes = new byte[(int)currentFileSize];
				iStream.read(bytes);
				
				oStream.write(bytes);
				oStream.close();
				iStream.close();
				setProcessed(processed + currentFileSize);
			}
			setCompleted(true);
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	/** Ritrovamento delle altre parti del file dalla stessa directory
	 * @param currentDirectory	Directory dove si trova la prima parte
	 * @return	Array di File contenente le parti successive
	 */
	private File[] getMatchingFiles(File currentDirectory) {
		String extension = super.getFileExtension();
		String matchName = originalFileName;
		
		File[] matchingFiles = currentDirectory.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		    	Pattern regex = Pattern.compile(matchName.replaceAll("\\.", "\\\\.") + "\\.\\d+" + extension.replaceAll("\\.", "\\\\."));
		        return name.matches(regex.toString());
		    }
		});
		for(int i=0; i<matchingFiles.length; i++) {
			rebuiltFileSize += matchingFiles[i].length();
		}
		return matchingFiles;
	}
	
	@Override
	public String getFileSizeFormatted() {
		DecimalFormat df = new DecimalFormat("#.##");
		if(rebuiltFileSize < 1000) {
			return rebuiltFileSize + " B";
		}
		else if(rebuiltFileSize < 1000000) {
			return df.format(((double)rebuiltFileSize)/1000) + " KB";
		}
		else if(rebuiltFileSize < 1000000000) {
			return df.format(((double)rebuiltFileSize)/1000000) + " MB";
		}
		else {
			return df.format(((double)rebuiltFileSize)/1000000000) + " GB";
		}
	}
	
	@Override
	public String getParameters() {
		return matchingFiles.length + " parti";
	}

	@Override
	public double getProcessedPercentage() {
		double progress = ((double)getProcessed() / rebuiltFileSize) * 100;
		return progress;
	}

}
