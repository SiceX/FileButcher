import java.io.File;

import javax.swing.filechooser.FileSystemView;

import gui.FBMainWindow;

public class FileButcherMain {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		String resultDirectory = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\Splitted Files";
		File directory = new File(resultDirectory);
	    if (!directory.exists()){
	        directory.mkdir();
	    }
		FBMainWindow mainWindow = new FBMainWindow();
		mainWindow.setVisible(true);
	}
	
}
