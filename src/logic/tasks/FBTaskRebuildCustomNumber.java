package logic.tasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.regex.Pattern;

public class FBTaskRebuildCustomNumber extends FBTask {
	
	private String currentDir;
	private String originalFileName;

	/**
	 * Rebuild
	 * @param path
	 * @param name
	 * @param doRebuild
	 * @param fileSize
	 */
	public FBTaskRebuildCustomNumber(String path, String name, long fileSize){
		super(path, name, TaskMode.REBUILD_CUSTOM_NUMBER, fileSize);
		currentDir = super.getPathName().substring(0, super.getPathName().length() - super.getFileName().length());
		String tokens[] = super.getFileName().split("\\.");
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<tokens.length-2; i++) {
			sb.append(tokens[i]).append(".");
		}
		sb.deleteCharAt(sb.length()-1);
		originalFileName = sb.toString();
	}
	
	/**
	 * Ricostruzione del file dalle sue parti
	 */
	@Override
	public void run() {
		File currentDirectory = new File(super.getPathName().substring(0, super.getPathName().length() - super.getFileName().length()));
		File matchingFiles[] = getMatchingFiles(currentDirectory);
		
		//TODO
	}
	
	private File[] getMatchingFiles(File currentDirectory) {
		String extension = super.getFileExtension();
		String matchName = originalFileName;
		
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
		return "Ricomposizione";
	}
}
