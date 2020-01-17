package gui;

import javax.swing.*;

import logic.FBQueue;
import logic.FBTableModel;
import logic.FBTask;
import logic.FBTaskCryptSameSize;
import logic.FBTaskCustomNumber;
import logic.FBTaskSameSize;
import logic.FBTaskZipCustomSize;
import logic.TaskMode;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;
import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.miginfocom.swing.MigLayout;

public class FBMainWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JFileChooser fileChooser = new JFileChooser();
	//private final ConcurrentLinkedQueue<FBTask> taskQueue = new ConcurrentLinkedQueue<FBTask>();
	private final FBTableModel tblModel = new FBTableModel(new ConcurrentLinkedQueue<FBTask>());
	private final JTable tblQueue = new JTable();
	private final JButton btnChooseFile = new JButton("Seleziona file(s)");
	private final Window mainWindowReference = this;
	
		

	@SuppressWarnings("serial")
	public FBMainWindow() {
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("FileButcher");
		setSize(450, 300);
		getContentPane().setLayout(new MigLayout("", "[143px][60.00][111.00px,grow]", "[212px,grow]"));
		
		btnChooseFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int res = fileChooser.showOpenDialog(mainWindowReference);
				if(res == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					
					FBSelectMode dialog = new FBSelectMode(mainWindowReference);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					
					dialog.setVisible(true);
					
					TaskMode choice = dialog.getChoice();
					
					if(choice != null) {
						FBTask newTask = createTask(file.getPath(), file.getName(), choice);
						
						//taskQueue.add(newTask);
						FBTableModel model = (FBTableModel)tblQueue.getModel();
					    model.addTask(newTask);
					    
					}
				}
			}
		});
		getContentPane().add(btnChooseFile, "cell 0 0,growx,aligny top");
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 2 0,grow");
		scrollPane.setViewportView(tblQueue);
		
		tblQueue.setModel(tblModel);
		tblQueue.getColumnModel().getColumn(1).setPreferredWidth(107);
				
	}
	
	private FBTask createTask(String path, String name, TaskMode choice) {
		switch(choice) {
			case SAME_SIZE:			return new FBTaskSameSize(path, name);
			
			case CRYPT_SAME_SIZE:	return new FBTaskCryptSameSize(path, name);
			
			case ZIP_CUSTOM_SIZE:	return new FBTaskZipCustomSize(path, name);
			
			case CUSTOM_NUMBER:		return new FBTaskCustomNumber(path, name);
			
			default:				return null;
		}
	}
}

