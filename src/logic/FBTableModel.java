package logic;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class FBTableModel extends AbstractTableModel {
	
	private final String[] columnNames = {"File", "Modalità", "Specifiche"};
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
			FBTask editedTask = createTask(data.get(rowIndex).getPathname(), data.get(rowIndex).getName(), mode);
			
			data.set(rowIndex, editedTask);
		}
		else if(columnIndex == 2) { //Modifica parametri del task
			//TODO DOCUMENT FILTER E CAZZIAMMAZZI
		}
	 	fireTableRowsUpdated(rowIndex, rowIndex);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(rowIndex < data.size()) {
			switch(columnIndex) {
				case 0:	return data.get(rowIndex).getName();
				case 1:	return data.get(rowIndex).getMode();
				case 2: return data.get(rowIndex).getSpecs();
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
	 * @return the data
	 */
	public ArrayList<FBTask> getData() {
		return data;
	}
	
	private FBTask createTask(String path, String name, TaskMode mode) {
		switch(mode) {
			case SAME_SIZE:			return new FBTaskSameSize(path, name);
			
			case CRYPT_SAME_SIZE:	return new FBTaskCryptSameSize(path, name);
			
			case ZIP_CUSTOM_SIZE:	return new FBTaskZipCustomSize(path, name);
			
			case CUSTOM_NUMBER:		return new FBTaskCustomNumber(path, name);
			
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