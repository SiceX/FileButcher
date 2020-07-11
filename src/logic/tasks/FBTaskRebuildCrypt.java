package logic.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Definisce il procedimento per ricostruire un file dalle sue parti uguali cifrate, estende la classe FBTask
 * @see FBTask
 * @see TaskMode
 * @author Sice
 */
public class FBTaskRebuildCrypt extends FBTask {

	/**
	 * Durante la lettura dei blocchi bisogna accontare anche per i possibili 16 byte in più di padding generati dall'operazione di cifratura
	 */
	private final int PADDING_SURPLUS = 16;
	/**
	 * La cartella dove si trova la prima parte, sarà anche dove sarà messo il file ricostruito
	 */
	private String currentDir;
	/**
	 * Nome originale del file, ricavato dal nome della prima parte
	 */
	private String originalFileName;
	/**
	 * Cifrario utilizzato per cifrare il file
	 */
	private Cipher cipher;
	/**
	 * Chiave di codifica per la cifratura
	 */
	private byte encodedKey[];
	/**
	 * Vettore di inizializzazione per la cifratura
	 */
	private byte iv[];
	/**
	 * Array con i puntamenti alle altre parti da cui ricostruire il file originario
	 */
	private File matchingFiles[];
	/**
	 * Dimensione calcolata che avrà il file ricostruito
	 */
	private long rebuiltFileSize;

	/**
	 * Crea un FBTaskRebuildCrypt con i valori forniti.
	 * <br>Vengono salvati anche:
	 * <br>- La cartella che contiene la prima parte;
	 * <br>- Il nome del file originario (ottenuto dal nome della parte);
	 * <br>- L'Array delle altre parti trovate nella stessa cartella.
	 * @param path	Indirizzo completo della prima parte, compresa la parte stessa
	 * @param name	Il nome completo della prima parte
	 * @param fileSize	La dimensione in Byte della prima parte
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
		File currentDirectory = new File(super.getPathName().substring(0, super.getPathName().length() - super.getFileName().length()));
		matchingFiles = getMatchingFiles(currentDirectory);
	}
	
	/**
	 * Esegue la ricostruzione del file originario dalle sue parti uguali e cifrate.
	 * <br>Questo metodo si occupa di:
	 * <br>- Inizializzare il cifrario con la chiave e il vettore iniziale ottenuti leggendo gli ultimi 32+16 byte della prima parte;
	 * <br>- Leggere da ogni parte e scrivere i dati decifrati nel nuovo file ricostruito;
	 * <br>- Se la grandezza della parte è maggiore della costante definita nella superclasse BLOCK_MAX_SIZE (50 MB + 16 Byte, per tenere conto del padding dovuto alla cifratura), 
	 * 		 la lettura e scrittura viene suddivisa in più scritture separate di grandezza BLOCK_MAX_SIZE per evitare di trattenere un eccessiva quantità di dati in memoria;
	 * <br>- Aggiornare il valore del progresso.
	 * @see BLOCK_MAX_SIZE
	 * @see Cipher
	 */
	@Override
	public void run() {
		try {
			long currentFileSize;
			setProcessed(0);
			
			initCipher(matchingFiles[0].getPath());
			
			OutputStream oStream;
			InputStream iStream;
			
			for(int i=0; i<matchingFiles.length; i++) {
				oStream = getOutputStream(currentDir, originalFileName);
				iStream = new FileInputStream(matchingFiles[i].getPath());
				currentFileSize = matchingFiles[i].length();
				if(i==0) {
					currentFileSize -= (encodedKey.length + iv.length);
				}

				while(currentFileSize > BLOCK_MAX_SIZE+PADDING_SURPLUS) {
					byte[] bytes = new byte[BLOCK_MAX_SIZE+PADDING_SURPLUS];
					iStream.read(bytes);
					writeBytes(oStream, bytes);
					oStream.close();
					oStream = getOutputStream(currentDir, originalFileName);
					currentFileSize -= BLOCK_MAX_SIZE+PADDING_SURPLUS;
					setProcessed(processed + BLOCK_MAX_SIZE+PADDING_SURPLUS);
				}
				
				byte[] bytes = new byte[(int)currentFileSize];
				iStream.read(bytes);
				
				writeBytes(oStream, bytes);
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
	
	/** Inizializzazione del Cipher con la chiave e il vettore iniziale letti dal fondo della prima parte
	 * Algoritmo AES con PKCS5Padding e metodo CBC.
	 * CBC usa uno XOR tra i blocchi per rendere la codifica nel complesso più sicura.
	 * @param path	Indirizzo della prima parte da cui leggere i dati
	 */
	private void initCipher(String path){
		try {
			RandomAccessFile raf = new RandomAccessFile(path, "rw");
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
	
	/**
	 * Sovrascrittura della funzione di base per scrivere dei byte su file
	 * con gestione della decifratura dei dati tramite il Cipher
	 * @see Cipher
	 * @param oStream	OutputStream su cui scrivere
	 * @param bytes		Bytes di dati da scrivere
	 * @throws IOException
	 */
	@Override
	protected void writeBytes(OutputStream oStream, byte[] bytes) throws IOException {
		try {
			oStream.write(cipher.doFinal(bytes));
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
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
	
	/**
	 * Formatta la dimensione che avrà il file originario (calcolata in numero di Byte) in una versione più umanamente leggibile
	 * @return	Stringa con la dimensione espressa in:
	 * <br>- Byte, se è minore di 10^3;
	 * <br>- KB, se è maggiore di 10^3 e minore di 10^6;
	 * <br>- MB, se è maggiore di 10^6 e minore di 10^9;
	 * <br>- GB, altrimenti;
	 */
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

	/**
	 * @return Una stringa contenente la quantità di parti che sono state trovate
	 */
	@Override
	public String getParameters() {
		return matchingFiles.length + " parti";
	}
	
	/**
	 * Non è possibile cambiare i parametri di un Task di ricostruzione
	 */
	@Override
	public void setParameters(Object param) {}

	/**
	 * Calcola il progresso nell'esecuzione del Task come numero di byte processati divisa la dimensione che avrà il file ricostruito, per 100.
	 */
	@Override
	public double getProcessedPercentage() {
		double progress = ((double)getProcessed() / rebuiltFileSize) * 100;
		return progress;
	}

}
