package gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import logic.*;
import logic.tasks.FBTask;
import logic.tasks.FBTaskRebuildCustomNumber;
import logic.tasks.FBTaskRebuildCrypt;
import logic.tasks.FBTaskRebuildSameSize;
import logic.tasks.FBTaskButcherSameSize;
import logic.tasks.TaskMode;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import net.miginfocom.swing.MigLayout;


public class FBMainWindow extends JFrame{
	
	/**
	 * Indirizzo della cartella di lavoro dell'applicazione, dove vengono generati i file processati
	 */
	public static final String RESULT_DIR = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\File Butcher\\";
	/**
	 * Indirizzo del file con l'ultima locazione visitata
	 */
	private File lastDirectoryUsedFile;
	private final JFileChooser fileChooser;
	private final JTable tblQueue = new JTable();
	private final Window mainWindowReference = this;

	/**
	 * Creazione della finestra principale, con tabella dei Tasks e pulsanti delle funzioni
	 * @param lastDirectoryUsedFile	Indirizzo del file con l'ultima locazione visitata, utilizzato per inizializzare JFileChooser
	 * @see JFileChooser
	 */
	public FBMainWindow(File lastDirectoryUsedFile) {
		super();
		
		//Lettura della locazione precedentemente visitata
		this.lastDirectoryUsedFile = lastDirectoryUsedFile;
		fileChooser = initFileChoiceDirectory(lastDirectoryUsedFile);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("FileButcher");
		setSize(600, 300);
		getContentPane().setLayout(new MigLayout("", "[::145px,grow][45.00,center][111.00px,grow]", "[212px][grow]"));
		JPanel panel = new JPanel();
		panel.setBorder(null);
		
		fileChooser.setMultiSelectionEnabled(true);
		
		getContentPane().add(panel, "cell 0 0,growx,aligny top");
		panel.setLayout(new GridLayout(5, 1, 0, 10));
		
		JButton btnChooseFile = new JButton("Seleziona file(s)");
		btnChooseFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				chooseToButcher();
			}
		});
		panel.add(btnChooseFile);
		
		JButton btnRebuild = new JButton("Ricomponi file");
		btnRebuild.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseToRebuild();
			}
		});
		panel.add(btnRebuild);
		
		JButton btnRemoveSelected = new JButton("Rimuovi selezionati");
		btnRemoveSelected.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				removeSelected();
			}
		});
		panel.add(btnRemoveSelected);
		
		JButton btnButcher = new JButton("Esegui lavori");
		btnButcher.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				doButchering();
			}
		});
		panel.add(btnButcher);
		
		JButton btnOpenResDirectory = new JButton("Apri cartella risultati");
		btnOpenResDirectory.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				openResDir();
			}
		});
		panel.add(btnOpenResDirectory);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 2 0 1 2,grow");
		tblQueue.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_DELETE) {
					removeSelected();
				}
			}
		});
		scrollPane.setViewportView(tblQueue);
		
		tblQueue.setModel(new FBTableModel());
		tblQueue.getTableHeader().setReorderingAllowed(false);

		JPanel passwordPanel = new JPanel();
		getContentPane().add(passwordPanel, "cell 0 1,growx,aligny center");
		passwordPanel.setLayout(new GridLayout(2, 0, 0, 3));
		tblQueue.getColumnModel().getColumn(0).setPreferredWidth(92);
		JComboBox<TaskMode> cmbxCellEditor = new JComboBox<TaskMode>(TaskMode.selectableValues());
		DefaultCellEditor editor = new DefaultCellEditor(cmbxCellEditor);
		tblQueue.getColumnModel().getColumn(1).setCellEditor(editor);
		tblQueue.getColumnModel().getColumn(1).setPreferredWidth(100);
		
		ProgressBarRenderer pbr = new ProgressBarRenderer(0, 100);
		pbr.setStringPainted(true);
		tblQueue.setDefaultRenderer(JProgressBar.class, pbr);
	}
	
	/**
	 * Scelta dei file da dividere e creazione nuovo task (dimensioni uguali di default)
	 */
	private void chooseToButcher() {
		int res = fileChooser.showOpenDialog(mainWindowReference);
		if(res == JFileChooser.APPROVE_OPTION) {
			try (PrintWriter out = new PrintWriter(lastDirectoryUsedFile)){
				out.println(fileChooser.getCurrentDirectory());
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
			File[] files = fileChooser.getSelectedFiles();
			
			for(int i=0; i<files.length; i++) {
				FBTask newTask = createTask(files[i].getPath(), files[i].getName(), files[i].length());
				
				FBTableModel model = (FBTableModel) tblQueue.getModel();
			    model.addTask(newTask);
			}
		}
	}
	
	/**
	 * Scelta dei file da ricostruire, creazione del task di ricostruzione
	 */
	private void chooseToRebuild() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Parti di file", "par", "crypar", "parn");
		fileChooser.setFileFilter(filter);
		int res = fileChooser.showOpenDialog(mainWindowReference);
		if(res == JFileChooser.APPROVE_OPTION) {
			try (PrintWriter out = new PrintWriter(lastDirectoryUsedFile)){
				out.println(fileChooser.getCurrentDirectory());
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
			File file = fileChooser.getSelectedFile();
			String tokens[] = file.getName().split("\\.");
			
			FBTask newTask = createRebuildTask(file.getPath(), file.getName(), "."+tokens[tokens.length-1], file.length());
				
			FBTableModel model = (FBTableModel) tblQueue.getModel();
		    model.addTask(newTask);
		}
		fileChooser.resetChoosableFileFilters();
	}
	
	/**
	 * Rimuove le righe (tasks) selezionate dalla tabella (e dalla coda), ascolta anche per la pressione del tasto DEL
	 */
	private void removeSelected() {
		FBTableModel model = (FBTableModel)tblQueue.getModel();
		model.removeSelectedRows(tblQueue.getSelectedRows());
	}
	
	/**
	 * Avvia i task di scomposizione/ricostruzione
	 */
	private void doButchering() {
		FBTableModel model = (FBTableModel)tblQueue.getModel();
		Butcher.executeOrder66(model.getData());
	}
	
	/**
	 * Apre la cartella dove vengono generati i risultati delle scomposizioni in system explorer
	 */
	private void openResDir() {
		try {
			Desktop.getDesktop().open(new File(RESULT_DIR));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Questa funzione legge il file con l'ultima locazione utilizzata
	 * @param lastDirectoryUsedFile File con l'ultima cartella utilizzata
	 * @return JFileChooser sull'ultima cartella utilizzata
	 */
	private JFileChooser initFileChoiceDirectory(File lastDirectoryUsedFile) {
		String line = null;
        try (BufferedReader br = new BufferedReader(new FileReader(lastDirectoryUsedFile))) {
            line = br.readLine();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
		return new JFileChooser(line);
	}

	/** Crea un Task di scomposizione di default, un SAME_SIZE con parti da 500 KB
	 * @param path		Nome completo di indirizzo del file
	 * @param name		Solo il nome del file, senza il path
	 * @param fileSize 	Grandezza del file
	 * @return FBTask	un FBTask di tipo SAME_SIZE con parti da 500 KB
	 */
	private FBTask createTask(String path, String name, long fileSize) {
		return new FBTaskButcherSameSize(path, name, fileSize);
	}
	
	/** Crea un Task di ricostruzione di un file appropriato all'estenzione della prima parte
	 * @param path	Nome completo di indirizzo del file
	 * @param name	Solo il nome del file, senza il path
	 * @param ext	Estensione del file
	 * @param fileSize	Dimensione del file scelto
	 * @return FBTask	un FBTask del tipo corrispondente alla estensione della parte
	 */
	private FBTask createRebuildTask(String path, String name, String ext, long fileSize) {
		switch(ext) {
			case ".par":			return new FBTaskRebuildSameSize(path, name, fileSize);
			
			case ".crypar":			return new FBTaskRebuildCrypt(path, name, fileSize);
			
			case ".parn":			return new FBTaskRebuildCustomNumber(path, name, fileSize);
			
			default:				return null;
		}
	}
}

