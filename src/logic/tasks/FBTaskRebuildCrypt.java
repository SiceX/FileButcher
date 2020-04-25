package logic.tasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FBTaskRebuildCrypt extends FBTask {

	private String currentDir;
	private String originalFileName;
	private Cipher cipher;
	private byte encodedKey[];
	private byte iv[];
	private long rebuiltFileSize;

	/**
	 * Rebuild Task
	 * @param path		Nome completo di indirizzo del file
	 * @param name		Solo il nome del file, senza il path
	 * @param fileSize	Grandezza del file
	 */
	public FBTaskRebuildCrypt(String path, String name, long fileSize) {
		super(path, name, TaskMode.REBUILD_CRYPT_SAME_SIZE, fileSize);
		currentDir = super.getPathName().substring(0, super.getPathName().length() - super.getFileName().length());
		String tokens[] = super.getFileName().split("\\.");
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<tokens.length-2; i++) {
			sb.append(tokens[i]).append(".");
		}
		sb.deleteCharAt(sb.length()-1);
		originalFileName = sb.toString();
		encodedKey = new byte[32];
		iv = new byte[16];
		rebuiltFileSize = 0;
	}
	
	/**
	 * Ricostruzione del file dalle sue parti
	 */
	@Override
	public void run() {
		File currentDirectory = new File(super.getPathName().substring(0, super.getPathName().length() - super.getFileName().length()));
		File matchingFiles[] = getMatchingFiles(currentDirectory);
		
		try {
			long currentFileSize;
			setProcessed(0);
			
			initCipher(matchingFiles[0].getPath());
			
			OutputStream oStream;
			InputStream iStream;
			
			for(int i=0; i<matchingFiles.length; i++) {
				oStream = new BufferedOutputStream(new FileOutputStream(currentDir+originalFileName, true));
				iStream = new FileInputStream(matchingFiles[i].getPath());
				currentFileSize = matchingFiles[i].length();
				if(i==0) {
					currentFileSize -= (encodedKey.length + iv.length);
				}

				while(currentFileSize > BLOCK_MAX_SIZE) {
					byte[] bytes = new byte[BLOCK_MAX_SIZE];
					iStream.read(bytes);
					oStream.write(cipher.doFinal(bytes));
					oStream.close();
					oStream = new BufferedOutputStream(new FileOutputStream(currentDir+originalFileName, true));
					currentFileSize -= BLOCK_MAX_SIZE;
					setProcessed(processed + BLOCK_MAX_SIZE);
				}
				
				byte[] bytes = new byte[(int)currentFileSize];
				iStream.read(bytes);
				
				oStream.write(cipher.doFinal(bytes));
				oStream.close();
				iStream.close();
				setProcessed(processed + currentFileSize);
			}
			setCompleted(true);
		}
		catch(Throwable e) {
			System.out.println(e.getMessage());
		}
	}
	
	/** Inizializzazione del Cipher con la password fornita
	 * Algoritmo AES con padding e metodo CBC (instead of EBC). 
	 * Mentre EBC cifra i blocchi indipendentemente l'uno dall'altro,
	 * CBC usa uno XOR tra i blocchi per rendere la codifica nel complesso più sicura.
	 * @throws Exception
	 */
	private void initCipher(String path){
		try {
			RandomAccessFile raf = new RandomAccessFile(path, "rw");
			System.out.println(raf.length());
			System.out.println((int)(raf.length()-iv.length));
			raf.seek((int)(raf.length()-iv.length));
			raf.read(iv);
			raf.seek((int)(raf.length()-encodedKey.length-iv.length));
			raf.read(encodedKey);
			raf.close();
			SecretKey key = new SecretKeySpec(encodedKey, "AES");

			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		} catch (Exception e) {
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
		rebuiltFileSize -= (encodedKey.length + iv.length);
		return matchingFiles;
	}
	
	@Override
	public String getParameters() {
		return "Ricomposizione";
	}

	@Override
	public double getProcessedPercentage() {
		double progress = ((double)getProcessed() / rebuiltFileSize) * 100;
		return progress;
	}

}
