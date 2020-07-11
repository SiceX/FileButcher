package gui;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renderer custom per la barra di progresso dei Task nella tabella
 * @author Sice
 */
public class ProgressBarRenderer extends JProgressBar implements TableCellRenderer {

	public ProgressBarRenderer(int min, int max) {
		super(min, max);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		setValue((int) ((Double) value).floatValue());
		return this;
	}

}
