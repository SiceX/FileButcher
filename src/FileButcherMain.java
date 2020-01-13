import java.awt.*;
import javax.swing.*;

public class FileButcherMain {

	private JFrame frmFileButcher;
	private String FileQueue[];

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		FileButcherMain window = new FileButcherMain();
		//frmFileButcher = new JFrame();
		window.frmFileButcher.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public FileButcherMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmFileButcher = new JFrame();
		frmFileButcher.setTitle("FileCutter");
		frmFileButcher.setBounds(100, 100, 450, 300);
		frmFileButcher.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmFileButcher.getContentPane().setLayout(null);
		
		JButton btnSelectFile = new JButton("Seleziona File");
		btnSelectFile.setBounds(10, 11, 142, 23);
		frmFileButcher.getContentPane().add(btnSelectFile);
		
		JPanel panel = new JPanel();
		panel.setBounds(282, 11, 131, 217);
		frmFileButcher.getContentPane().add(panel);
		
		JList FCQueue = new JList(FileQueue);
		panel.add(FCQueue);
	}
	
	
}
