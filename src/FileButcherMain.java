import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.filechooser.FileSystemView;

import gui.FBMainWindow;

public class FileButcherMain {

	/**
	 * Crea cartella dei risultati e cartella data (se non esistenti) e lancia l'applicazione.
	 * @param args Argomenti passabili dalla chiamata da linea di comando, non utilizzati.
	 */
	public static void main(String[] args) {
		String resultDirectory = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\File Butcher";
		File directory = new File(resultDirectory);
	    if (!directory.exists()){
	        directory.mkdir();
	    }
	    String currentDir = System.getProperty("user.dir");
		File dataDir = new File(currentDir + "\\data");
	    if (!dataDir.exists()){
	    	dataDir.mkdir();
	    }
	    File lastDirectoryUsed = new File(dataDir.getAbsolutePath() + "\\lastDir.data");
	    if (!lastDirectoryUsed.exists()){
	    	try (PrintWriter out = new PrintWriter(lastDirectoryUsed)){
				out.println(FileSystemView.getFileSystemView().getDefaultDirectory().getPath());
			} catch (FileNotFoundException e) {
				dataDir.mkdir();
			}
	    }
	    
		FBMainWindow mainWindow = new FBMainWindow(lastDirectoryUsed);
		mainWindow.setVisible(true);
	}
	
}
