package gui;

import javax.swing.*;

import logic.FBQueue;
import logic.FBTask;
import logic.TaskMode;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;
import java.io.File;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;

public class FBMainWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JFileChooser fileChooser = new JFileChooser();
	private final FBQueue fbQueue = new FBQueue();
	private final JTable tblQueue = new JTable();
	

	@SuppressWarnings("serial")
	public FBMainWindow() {
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("FileButcher");
		setSize(450, 300);
		getContentPane().setLayout(new MigLayout("", "[143px][60.00][111.00px,grow]", "[212px,grow]"));
		
		JButton btnChooseFile = new JButton("Seleziona file(s)");
		btnChooseFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				fileChooser.showOpenDialog(arg0.getComponent());
				File file = fileChooser.getSelectedFile();
				
				FBSelectMode dialog = new FBSelectMode();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				
				dialog.setVisible(true);
				
				TaskMode choice = dialog.getChoice();
								
				FBTask newTask = new FBTask(file.getPath(), file.getName(), choice);
				
				fbQueue.add(newTask);
				DefaultTableModel model = (DefaultTableModel) tblQueue.getModel();
			    model.addRow(new String[] {newTask.getName(), newTask.getModeDescription()} );
			}
		});
		getContentPane().add(btnChooseFile, "cell 0 0,growx,aligny top");
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 2 0,grow");
		scrollPane.setViewportView(tblQueue);
		
		tblQueue.setModel(new DefaultTableModel(
			fbQueue.getData(),
			new String[] {
				"File", "Tipo Lavoro"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tblQueue.getColumnModel().getColumn(1).setPreferredWidth(107);
				
	}
}

