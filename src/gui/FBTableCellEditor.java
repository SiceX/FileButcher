package gui;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

@SuppressWarnings("serial")
public class FBTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	
	String currentValue;

	public FBTableCellEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getCellEditorValue() {
		return currentValue;
	}

	@Override
	public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, int arg3, int arg4) {
//		if(arg0.getModel().getValueAt(arg3, 1) == TaskMode.ZIP_CUSTOM_SIZE) {
//			//TODO 
//		}
//		else {
//			JTextField eComponent = new JTextField();
//			return 
//		}
		return null;
	}

}
