package gui;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;
import javax.swing.JProgressBar;

import logic.tasks.FBTask;
import logic.tasks.FBTaskButcherCrypt;
import logic.tasks.FBTaskButcherCustomNumber;
import logic.tasks.FBTaskButcherSameSize;
import logic.tasks.TaskMode;

/**
 * Modello custom della tabella
 * @author Sice
 */
public class FBTableModel extends AbstractTableModel implements Observer{
	
	private final String[] columnNames = {"File", "Modalità", "Parametri", "Dimensione", "Progresso"};
	/**
	 * Lista dei FBTask
	 */
	private final ArrayList<FBTask> data = new ArrayList<FBTask>();
	
	public FBTableModel() {	}

	@Override
    public String getColumnName(int columnIndex){
        return columnNames[columnIndex];
    }
	
	@Override
	public Class<?> getColumnClass(int columnIndex){
		switch(columnIndex) {
			case 0:	return String.class;
			case 1:	return String.class;
			case 2: return String.class;
			case 3: return String.class;
			case 4: return JProgressBar.class;
			default:	throw new ArrayIndexOutOfBoundsException();
		}
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}
		
	/**
	 * Gestisce la modifica di due colonne:
	 * <br>- tipo di task (in caso sia un task di scomposizione);
	 * <br>- parametri del task (che possono essere la grandezza delle parti o il numero)
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if(columnIndex == 1) {	//Modifica tipo di task
			TaskMode mode = (TaskMode)value;
			FBTask editedTask = createTask(data.get(rowIndex).getPathName(), data.get(rowIndex).getFileName(), mode, data.get(rowIndex).getFileSize());
			
			data.set(rowIndex, editedTask);
			editedTask.addObserver(this);
		}
		else if(columnIndex == 2) { //Modifica parametri del task
			String newStr = (String)value;
			TaskMode mode = data.get(rowIndex).getMode();
			if(mode == TaskMode.BUTCHER_SAME_SIZE || mode == TaskMode.BUTCHER_CRYPT_SAME_SIZE) {
				long partSize = validateAndParse(newStr);
				if(partSize > 0) {
					if( partSize <= data.get(rowIndex).getFileSize() ) {
						data.get(rowIndex).setParameters(partSize);
					}
					else {
						data.get(rowIndex).setParameters(data.get(rowIndex).getFileSize());
					}
				}
			}
			else if(mode == TaskMode.BUTCHER_CUSTOM_NUMBER) {
				try {
					int nParts = Integer.parseInt(newStr);
					if(nParts >= 1) {
						data.get(rowIndex).setParameters(nParts);
					}
				}
				catch(NumberFormatException e) {} //Non modificare il valore
			}
		}
	 	fireTableRowsUpdated(rowIndex, rowIndex);
	}

	/**
	 * Valida la stringa immessa dall'utente in base ad una regex:
	 * <br>accetta un numero arbitrario di cifre, anche diviso da un punto o una virgola per i decimali,
	 * che può essere seguito dall'unità di misura (B, KB, MB, GB, maiuscoli o minuscoli).
	 * <br>- Se viene usata la virgola come divisore per il decimale, viene sostituita da un punto e "parsato" a double;
	 * <br>- Se si scrive un decimale e non si specifica una misura (quindi viene assunto Byte), viene troncato ad intero;
	 * <br>- Se non è specificata un'unità di misura, viene assunto sia Byte;
	 * <br>- Per le altre unità di misura, il dato viene convertito a Byte.
	 * @param newStr Nuova stringa immessa dall'utente
	 * @return long: Dimensione specificata in byte se la validazione ha successo, -1 altrimenti
	 */
	private long validateAndParse(String newStr) {
		if(newStr.matches("^[0-9]+[.,]?[0-9]*(\\s[KMGkmg]?[Bb])?$")) {		//numero con possibili decimali e possibile unità di misura
			String[] splits = newStr.split("\\s");
			double size = Double.parseDouble(splits[0].replaceAll(",", "."));	//parte col numero
			if(splits.length > 1) {
				switch(splits[1].toUpperCase()) {		//switch sulla parte con l'unità di misura
					case "B": return (long)size;
					case "KB": return (long)(size *= 1000);
					case "MB": return (long)(size *= 1000000);
					case "GB": return (long)(size *= 1000000000);
					default: return (long)size;
				}
			}
			return (long)size;
		}
		else {
			return -1;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(rowIndex < data.size()) {
			FBTask task = data.get(rowIndex);
			switch(columnIndex) {
				case 0:		return task.getFileName();
				case 1:		return task.getMode();
				case 2: 	return task.getParameters();
				case 3: 	return task.getFileSizeFormatted();
				case 4: 	return task.getProcessedPercentage();
				default:	throw new ArrayIndexOutOfBoundsException();
			}
		}
		else {
			throw new ArrayIndexOutOfBoundsException();
		}
		
	}
	
	/**
	 * Gestisce la modificabilità della colonna del tipo di Task.
	 * Se il tipo di task della riga è di tipo Rebuild, la cella non è modificabile
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		TaskMode mode = data.get(row).getMode();
		if(mode == TaskMode.BUTCHER_SAME_SIZE || mode == TaskMode.BUTCHER_CRYPT_SAME_SIZE || mode == TaskMode.BUTCHER_CUSTOM_NUMBER) {
	        if(col == 1) return true;
	        if(col == 2) return true;
		}
        return false;
     }
	
	public void addTask(FBTask task) {
		data.add(task);
		task.addObserver(this);
		purgeCompleted();
		fireTableRowsInserted(data.size(), data.size());
	}

	public void removeTask(FBTask task) {
        if (data.contains(task)) {
            int row = data.indexOf(task);
            data.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }
	
	public void removeSelectedRows(int[] selection) {
		for(int i=selection.length-1; i>=0; i--) {
			removeTask(data.get(selection[i]));
		}
	}

	/** 
	 * Se ci sono task completati, li rimuove dalla coda 
	 */
	private void purgeCompleted() {
		for(int i=data.size()-1; i>=0; i--) {
			if(data.get(i).isCompleted()) {
				removeTask(data.get(i));
			}
		}
	}

	/**
	 * Ritorna la coda di Task
	 * @return ArrayList di FBTask
	 */
	public ArrayList<FBTask> getData() {
		return data;
	}
	
	/**
	 * Utility interna al modello per creare un task dati indirizzo, nome e dimensione del file e la sua modalità
	 * @param path		Indirizzo file
	 * @param name		Nome completo di estensione file
	 * @param mode		Tipo di Task da eseguire
	 * @param fileSize	Dimensione del file
	 * @return	FBTask	un nuovo FBTask
	 */
	private FBTask createTask(String path, String name, TaskMode mode, long fileSize) {
		switch(mode) {
			case BUTCHER_SAME_SIZE:			return new FBTaskButcherSameSize(path, name, fileSize);
			
			case BUTCHER_CRYPT_SAME_SIZE:	return new FBTaskButcherCrypt(path, name, fileSize);
			
			case BUTCHER_CUSTOM_NUMBER:		return new FBTaskButcherCustomNumber(path, name, fileSize);
			
			default: return null;
		}
	}

	/**
	 * Questa funzione viene chiamata quando la percentuale di completamento di un task cambia
	 * per aggiornare le barre di progresso
	 */
	@Override
	public void update(Observable o, Object arg) {
		int index = data.indexOf(arg);
		if(index != -1) {
			fireTableRowsUpdated(index, index);
		}
	}

}