package gui;

import javax.swing.*;

import logic.FBQueue;
import logic.FBTask;
import logic.TaskType;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.table.DefaultTableModel;

public class FBMainWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JFileChooser fileChooser = new JFileChooser();
	private final FBQueue fbQueue = new FBQueue();
	private final JTable tbQueue = new JTable();
	

	@SuppressWarnings("serial")
	public FBMainWindow() {
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("FileButcher");
		setSize(450, 300);
		getContentPane().setLayout(null);
		
		tbQueue.setModel(new DefaultTableModel(
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
		tbQueue.getColumnModel().getColumn(1).setPreferredWidth(107);
		tbQueue.setBounds(200, 16, 213, 212);
		getContentPane().add(tbQueue);
		
		JButton btnChooseFile = new JButton("Seleziona file(s)");
		btnChooseFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				fileChooser.showOpenDialog(arg0.getComponent());
				File file = fileChooser.getSelectedFile();
				FBTask newTask = new FBTask(file.getPath(), file.getName(), TaskType.SAME_SIZE);
				
				fbQueue.add(newTask);
				DefaultTableModel model = (DefaultTableModel) tbQueue.getModel();
			    model.addRow(new String[] {newTask.getName(), newTask.getTypeDescription()} );
			}
		});
		btnChooseFile.setBounds(15, 16, 143, 29);
		getContentPane().add(btnChooseFile);
				
	}
}

