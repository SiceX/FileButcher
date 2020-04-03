package gui;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

import logic.FBTask;
import logic.FBTaskCryptSameSize;
import logic.FBTaskCustomNumber;
import logic.FBTaskSameSize;
import logic.FBTaskZipCustomSize;
import logic.TaskMode;

@SuppressWarnings("serial")
public class FBTableModel extends AbstractTableModel {
	
	private final String[] columnNames = {"File", "Modalità", "Parametri", "Dimensione"};
	private final ArrayList<FBTask> data = new ArrayList<FBTask>();
	
	public FBTableModel() {
		//
	}

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
		
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if(columnIndex == 1) {	//Modifica tipo di task
			TaskMode mode = (TaskMode)value;
			FBTask editedTask = createTask(data.get(rowIndex).getPathName(), data.get(rowIndex).getFileName(), mode, data.get(rowIndex).getFileSize());
			
			data.set(rowIndex, editedTask);
		}
		else if(columnIndex == 2) { //Modifica parametri del task
			String newStr = (String)value;
			TaskMode mode = data.get(rowIndex).getMode();
			if(mode == TaskMode.SAME_SIZE || mode == TaskMode.CRYPT_SAME_SIZE) {
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
			else if(mode == TaskMode.CUSTOM_NUMBER) {
				try {
					int nParts = Integer.parseInt(newStr);
					if(nParts >= 1) {
						data.get(rowIndex).setParameters(nParts);
					}
				}
				catch(NumberFormatException e) {} //Do nothing
			}
			else if(mode == TaskMode.ZIP_CUSTOM_SIZE) {
				//TODO DEFINIZIONE PARTI
			}
		}
	 	fireTableRowsUpdated(rowIndex, rowIndex);
	}

	/**
	 * @param newStr
	 * @return specified size in bytes if validation succeeds, -1 otherwise
	 */
	private long validateAndParse(String newStr) {
		if(newStr.matches("^[0-9]+[.,]?[0-9]*(\\s[KMGkmg]?[Bb])?$")) {		//numero con possibili decimali e possibile unità
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
			switch(columnIndex) {
				case 0:	return data.get(rowIndex).getFileName();
				case 1:	return data.get(rowIndex).getMode();
				case 2: return data.get(rowIndex).getParameters();
				case 3: return data.get(rowIndex).getFileSizeFormatted();
				default:	throw new ArrayIndexOutOfBoundsException();
			}
		}
		else {
			throw new ArrayIndexOutOfBoundsException();
		}
		
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
        if(col == 1) return true ;
        if(col == 2) return true ;
        return false;
     }
	
	public void addTask(FBTask task) {
		data.add(task);
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
	 * Se ci sono dei task che richiedono la crittazione, ne imposta la chiave di crittazione
	 * @param cryptKey chiave di crittazione, password
	 */
	public void setPassword(char[] charKey) {
		for(int i=0; i<data.size(); i++) {
			if(data.get(i).getMode() == TaskMode.CRYPT_SAME_SIZE) {
				FBTaskCryptSameSize task = (FBTaskCryptSameSize)data.get(i);
				task.setPassword(new String(charKey));
			}
		}
	}

	/**
	 * @return the data
	 */
	public ArrayList<FBTask> getData() {
		return data;
	}
	
	private FBTask createTask(String path, String name, TaskMode mode, long fileSize) {
		switch(mode) {
			case SAME_SIZE:			return new FBTaskSameSize(path, name, fileSize);
			
			case CRYPT_SAME_SIZE:	return new FBTaskCryptSameSize(path, name, fileSize);
			
			case ZIP_CUSTOM_SIZE:	return new FBTaskZipCustomSize(path, name, fileSize);
			
			case CUSTOM_NUMBER:		return new FBTaskCustomNumber(path, name, fileSize);
			
			default:				return null;
		}
	}

}


//Iterator<FBTask> it = queueData.iterator();
//
//int i=0;
//while(it.hasNext() && i<rowIndex) {
//	
//}
//
//while(it.hasNext()) {
//	data[i][0] = it.next().getName();
//	data[i][1] = it.next().getModeDescription();
//	i++;
//}
//
//return data;
//return null;