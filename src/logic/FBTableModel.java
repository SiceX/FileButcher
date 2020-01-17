package logic;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class FBTableModel extends AbstractTableModel {
	
	private final String[] columnNames = {"File", "Tipo Lavoro"};
	private ConcurrentLinkedQueue<FBTask> queueData;
	
	public FBTableModel(ConcurrentLinkedQueue<FBTask> queue) {
		setQueueData(queue);
	}

	@Override
    public String getColumnName(int columnIndex){
        return columnNames[columnIndex];
    }
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		getQueueData().size();
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		FBTask[] taskArray = getQueueData().toArray(new FBTask[0]);
		
		if(rowIndex < taskArray.length) {
			switch(columnIndex) {
				case 0:	return taskArray[rowIndex].getName();
				case 1:	return taskArray[rowIndex].getModeDescription();
				default:	throw new ArrayIndexOutOfBoundsException();
			}
		}
		else {
			throw new ArrayIndexOutOfBoundsException();
		}
		
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex){
		switch(columnIndex) {
			case 0:	return String.class;
			case 1:	return String.class;
			default:	throw new ArrayIndexOutOfBoundsException();
		}
	}
	
	public void addTask(FBTask task) {
		queueData.add(task);
		fireTableRowsInserted(queueData.size(), queueData.size());
	}

	/**
	 * @return the queueData
	 */
	public ConcurrentLinkedQueue<FBTask> getQueueData() {
		return queueData;
	}

	/**
	 * @param queueData the queueData to set
	 */
	public void setQueueData(ConcurrentLinkedQueue<FBTask> queueData) {
		this.queueData = queueData;
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