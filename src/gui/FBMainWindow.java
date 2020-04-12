package gui;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import logic.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class FBMainWindow extends JFrame{
	/**
	 * "F:\\Event\\Download" per Event,
	 * System.getenv("HOMEPATH") + "\\Download" per altri pc
	 */
	public static final String RESULT_DIR = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\Splitted Files\\";
	private final JFileChooser fileChooser = new JFileChooser("F:\\Event\\Download");
	private final JTable tblQueue = new JTable();
	private final JButton btnChooseFile = new JButton("Seleziona file(s)");
	private final Window mainWindowReference = this;
	private final JPanel panel = new JPanel();
	private final JButton btnRemoveSelected = new JButton("Rimuovi selezionati");
	private final JButton btnButcher = new JButton("Esegui scomposizioni");
	private final JButton btnRebuild = new JButton("Ricomponi file");
	private final JPanel passwordPanel = new JPanel();
	private final JPasswordField cryptKeyField = new JPasswordField();
	private final JLabel lblCryptKeyField = new JLabel("Password");
	private final JButton btnOpenResDirectory = new JButton("Apri cartella risultati");
	
	

	public FBMainWindow() {
		super();
		lblCryptKeyField.setHorizontalAlignment(SwingConstants.CENTER);
		lblCryptKeyField.setLabelFor(cryptKeyField);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("FileButcher");
		setSize(600, 300);
		getContentPane().setLayout(new MigLayout("", "[::145px,grow][45.00,center][111.00px,grow]", "[212px][grow]"));
		panel.setBorder(null);
		
		fileChooser.setMultiSelectionEnabled(true);
		
		getContentPane().add(panel, "cell 0 0,growx,aligny top");
		panel.setLayout(new GridLayout(5, 1, 0, 10));
		panel.add(btnChooseFile);
		btnRemoveSelected.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				FBTableModel model = (FBTableModel)tblQueue.getModel();
				model.removeSelectedRows(tblQueue.getSelectedRows());
			}
		});
		
		panel.add(btnRemoveSelected);
		btnButcher.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				FBTableModel model = (FBTableModel)tblQueue.getModel();
				model.setPassword(cryptKeyField.getPassword());
				Butcher.executeOrder66(model.getData());
			}
		});
		
		panel.add(btnButcher);
		
		panel.add(btnRebuild);
		
		btnOpenResDirectory.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					Desktop.getDesktop().open(new File(RESULT_DIR));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		panel.add(btnOpenResDirectory);
		
		btnChooseFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int res = fileChooser.showOpenDialog(mainWindowReference);
				if(res == JFileChooser.APPROVE_OPTION) {
					File[] files = fileChooser.getSelectedFiles();
					
					for(int i=0; i<files.length; i++) {
						FBTask newTask = createTask(files[i].getPath(), files[i].getName(), files[i].length());
						
						FBTableModel model = (FBTableModel) tblQueue.getModel();
					    model.addTask(newTask);
					}
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 2 0 1 2,grow");
		tblQueue.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_DELETE) {
					FBTableModel model = (FBTableModel)tblQueue.getModel();
					model.removeSelectedRows(tblQueue.getSelectedRows());
				}
			}
		});
		scrollPane.setViewportView(tblQueue);
		
		tblQueue.setModel(new FBTableModel());
		
		getContentPane().add(passwordPanel, "cell 0 1,growx,aligny center");
		passwordPanel.setLayout(new GridLayout(2, 0, 0, 3));
		
		passwordPanel.add(lblCryptKeyField);
		cryptKeyField.setToolTipText("Chiave usata per la crittazione");
		
		passwordPanel.add(cryptKeyField);
		tblQueue.getColumnModel().getColumn(0).setPreferredWidth(92);
		JComboBox<TaskMode> cmbxCellEditor = new JComboBox<TaskMode>(TaskMode.values());
		DefaultCellEditor editor = new DefaultCellEditor(cmbxCellEditor);
		tblQueue.getColumnModel().getColumn(1).setCellEditor(editor);
		
		ProgressBarRenderer pbr = new ProgressBarRenderer(0, 100);
		pbr.setStringPainted(true);
		tblQueue.setDefaultRenderer(JProgressBar.class, pbr);
				
	}
	
	private FBTask createTask(String path, String name, long fileSize) {
		return new FBTaskSameSize(path, name, fileSize, 100*1000, false);
	}
	
//	private FBTask createTask(String path, String name, FBSelectMode dialog) {
//		switch(dialog.getChoice()) {
//			case SAME_SIZE:			return new FBTaskSameSize(path, name, dialog.getPartsSize());
//			
//			case CRYPT_SAME_SIZE:	return new FBTaskCryptSameSize(path, name, dialog.getPartsSize());
//			
//			case ZIP_CUSTOM_SIZE:	return new FBTaskZipCustomSize(path, name);
//			
//			case CUSTOM_NUMBER:		return new FBTaskCustomNumber(path, name, dialo);
//			
//			default:				return null;
//		}
//	}
}

