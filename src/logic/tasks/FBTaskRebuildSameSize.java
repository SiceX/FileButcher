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
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class FBTaskRebuildSameSize extends FBTask {

	private String currentDir;
	private String originalFileName;
	private boolean doCrypt;
	private Cipher cipher;

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
	}
	
	/**
	 * Ricostruzione del file dalle sue parti
	 */
	@Override
	public void run() {
		File currentDirectory = new File(super.getPathName().substring(0, super.getPathName().length() - super.getFileName().length()));
		File matchingFiles[] = getMatchingFiles(currentDirectory);
		
		
		
//		OutputStream oStream = 
		//TODO
	}
	
	/** Inizializzazione del Cipher con la password fornita
	 * @throws Exception
	 */
	private void initCipher() throws Exception {
		byte[] key = password.getBytes("UTF-8");
	    MessageDigest sha = MessageDigest.getInstance("SHA-1");
	    key = sha.digest(key);
	    key = Arrays.copyOf(key, 16); // use only first 128 bit

	    SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
	    cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
	}
	
	/** Ritorna l'OutputStream appropriato, normale o criptato
	 * @param fileCount Counter delle parti, impostare -1 se non si vuole aggiungere
	 * @param append	Se aprire o no lo stream in modalità append
	 * @return L'OutputStream appropriato, normale o criptato
	 * @throws FileNotFoundException
	 */
	private OutputStream getProperStream(int fileCount, boolean append) throws FileNotFoundException {
		if(doCrypt) {
			return new CipherOutputStream(new FileOutputStream(String.format("%s.%d%s", RESULT_DIR+getFileName(), fileCount, getFileExtension()), append), cipher);
		}
		else {
			return new BufferedOutputStream(new FileOutputStream(String.format("%s.%d%s", RESULT_DIR+getFileName(), fileCount, getFileExtension()), append));
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
		
		return matchingFiles;
	}
	
	@Override
	public String getParameters() {
		return "Ricomposizione";
	}

}
