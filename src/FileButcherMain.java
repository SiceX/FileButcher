import java.awt.*;
import javax.swing.*;

public class FileButcherMain {

	private JFrame frmFilecutter;
	private String FileQueue[];

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		FileButcherMain window = new FileButcherMain();
		window.frmFilecutter.setVisible(true);
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
		frmFilecutter = new JFrame();
		frmFilecutter.setTitle("FileCutter");
		frmFilecutter.setBounds(100, 100, 450, 300);
		frmFilecutter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmFilecutter.getContentPane().setLayout(null);
		
		JButton btnSelectFile = new JButton("Seleziona File");
		btnSelectFile.setBounds(10, 11, 89, 23);
		frmFilecutter.getContentPane().add(btnSelectFile);
		
		JPanel panel = new JPanel();
		panel.setBounds(282, 11, 142, 239);
		frmFilecutter.getContentPane().add(panel);
		
		JList FCQueue = new JList(FileQueue);
		panel.add(FCQueue);
	}
	
	
}
