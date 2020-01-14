package gui;

import javax.swing.*;

import logic.FBQueue;
import logic.FBTask;
import logic.TaskType;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class FBMainWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JFileChooser fileChooser = new JFileChooser();
	private final FBQueue fbqueue = new FBQueue();
	private final JList<String> fileList = new JList<String>(fbqueue.getNameList());
	

	public FBMainWindow() {
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("FileButcher");
		setSize(450, 300);
		getContentPane().setLayout(null);
		
		JButton btnChooseFile = new JButton("Seleziona file(s)");
		btnChooseFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				fileChooser.showOpenDialog(arg0.getComponent());
				File file = fileChooser.getSelectedFile();
				fbqueue.add(new FBTask(file.getPath(), file.getName(), TaskType.SAME_SIZE));
				fileList.setListData(fbqueue.getNameList());
			}
		});
		btnChooseFile.setBounds(15, 16, 143, 29);
		getContentPane().add(btnChooseFile);
		
		fileList.setBounds(310, 16, 103, 212);
		getContentPane().add(fileList);
		
	}
}

