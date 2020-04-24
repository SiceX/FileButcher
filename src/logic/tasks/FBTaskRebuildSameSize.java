package logic.tasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class FBTaskRebuildSameSize extends FBTask {

	private String currentDir;
	private String originalFileName;
	private boolean doCrypt;
	private Cipher cipher;
	private byte encodedKey[];
	private byte iv[];
	private long rebuiltFileSize;

	/**
	 * Rebuild Task
	 * @param path		Nome completo di indirizzo del file
	 * @param name		Solo il nome del file, senza il path
	 * @param fileSize	Grandezza del file
	 * @param crypt		Se criptare o no
	 */
	public FBTaskRebuildSameSize(String path, String name, boolean crypt, long fileSize) {
		super(path, name, crypt ? TaskMode.REBUILD_CRYPT_SAME_SIZE : TaskMode.REBUILD_SAME_SIZE, fileSize);
		doCrypt = crypt;
		currentDir = super.getPathName().substring(0, super.getPathName().length() - super.getFileName().length());
		String tokens[] = super.getFileName().split("\\.");
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<tokens.length-2; i++) {
			sb.append(tokens[i]).append(".");
		}
		sb.deleteCharAt(sb.length()-1);
		originalFileName = sb.toString();
		encodedKey = new byte[16];
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
			
			OutputStream oStream;
			InputStream iStream;
			
			for(int i=0; i<matchingFiles.length; i++) {
				oStream = new BufferedOutputStream(new FileOutputStream(currentDir+originalFileName, true));
				iStream = getProperStream(i+1, matchingFiles[i].getPath());
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
				
				byte[] bytes;
//				if(doCrypt) {
//					bytes = new byte[(int)currentFileSize - (48)];
//				}
//				else {
					bytes = new byte[(int)currentFileSize];
				//}
				iStream.read(bytes);
				
				oStream.write(bytes);
				oStream.close();
				iStream.close();
				setProcessed(processed + currentFileSize);
			}
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
	private void initCipher(){
		try {
			SecretKey key = new SecretKeySpec(encodedKey, "AES");

			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Ritorna l'OutputStream appropriato, normale o criptato
	 * @param fileCount Counter delle parti, impostare -1 se non si vuole aggiungere
	 * @param append	Se aprire o no lo stream in modalità append
	 * @return L'OutputStream appropriato, normale o criptato
	 * @throws FileNotFoundException
	 */
	private InputStream getProperStream(int fileCount, String path) throws FileNotFoundException {
		if(doCrypt) {
			if(fileCount == 1) {
				try {
					BufferedInputStream is = new BufferedInputStream(new FileInputStream(path));
					is.read(encodedKey);
					is.read(iv);
					is.close();
					initCipher();
					CipherInputStream cis = new CipherInputStream(new FileInputStream(path), cipher);
					byte[] skip = new byte[encodedKey.length + iv.length];
					cis.read(skip);
					//cis.skip(encodedKey.length + iv.length);
					return cis;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				return new CipherInputStream(new FileInputStream(path), cipher);
			}
		}
		return new BufferedInputStream(new FileInputStream(path));
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
	public String getParameters() {
		return "Ricomposizione";
	}

	@Override
	public double getProcessedPercentage() {
		double progress = ((double)getProcessed() / rebuiltFileSize) * 100;
		return progress;
	}

}
