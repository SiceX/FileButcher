package gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import logic.TaskMode;
import logic.Unit;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import net.miginfocom.swing.MigLayout;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.beans.PropertyChangeEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
@Deprecated
public class FBSelectMode extends JDialog {

	private final JPanel sizePanel = new JPanel();
	private final JComboBox<TaskMode> cmbModeSelect = new JComboBox<TaskMode>();
	private final JButton okButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");
	private final JLabel lblFileName = new JLabel("");
	private final JPanel modePanel = new JPanel();
	private final JComboBox<Unit> cmbUnit = new JComboBox<Unit>();
	private final JPanel numberPanel = new JPanel();
	private final JLabel lblNewLabel = new JLabel("parti");
	private final JPanel customPanel = new JPanel();
	private final JButton btnAddPartSize = new JButton("Definisci parti");
	private final JTable tblParts = new JTable();
	private final Window thisDialogReference = this;

	/**
	 * Grandezza della parte in byte
	 */
	private long partsSize;
	private JFormattedTextField numberField;
	private JFormattedTextField sizeField;
	private DefaultTableModel customPartsModel = new DefaultTableModel(
		new Double[][] {},
		new String[] {
			"Dimensione"
		}
	) {
		Class[] columnTypes = new Class[] {
			Integer.class
		};
		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}
	};
	
	
	
	/*
	 * 
	 * TODO
	 * MAKE SAME SIZE TEXT FIELD HAVE A MAX WITH PARTSIZEFILTER
	 * TODO
	 * 
	 */

	/**
	 * Create the dialog.
	 * @param fileName 
	 */
	public FBSelectMode(Window owner, File file) {
		super(owner, "Seleziona modalità di esecuzione", ModalityType.DOCUMENT_MODAL);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setLabelFor(numberField);

		NumberFormat longFormat = NumberFormat.getIntegerInstance();

		NumberFormatter numberFormatter = new NumberFormatter(longFormat);
		numberFormatter.setValueClass(Long.class); //optional, ensures you will always get a long value
		numberFormatter.setAllowsInvalid(false); //this is the key!!

		sizeField = new JFormattedTextField(numberFormatter);
		sizeField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				partsSize = Integer.parseUnsignedInt(sizeField.getText()) * getUnit();
			}
		});
		sizeField.setColumns(10);
		
		numberField = new JFormattedTextField(numberFormatter);
		numberField.setEnabled(false);
		numberField.setColumns(10);
		
		setTitle("Seleziona azione da eseguire");

		setBounds(100, 100, 557, 373);
		getContentPane().setLayout(new MigLayout("", "[358px,trailing][grow]", "[54.00px][top][grow,top][100px,grow,fill][39px]"));
		sizePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(sizePanel, "cell 0 1,grow");
		sizePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
		
		sizePanel.add(sizeField);
		cmbUnit.setMaximumRowCount(4);
		cmbUnit.setModel(new DefaultComboBoxModel<Unit>(Unit.values()));
		cmbUnit.setSelectedIndex(0);
		
		sizePanel.add(cmbUnit);
		FlowLayout flowLayout = (FlowLayout) numberPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		numberPanel.setBorder(null);
		
		getContentPane().add(numberPanel, "cell 0 2,grow");
		
		numberPanel.add(numberField);
		
		numberPanel.add(lblNewLabel);
		
		tblParts.setModel(customPartsModel);
		tblParts.getColumnModel().getColumn(0).setResizable(false);
		
		getContentPane().add(tblParts, "cell 1 1 1 3,grow");
		FlowLayout flowLayout_1 = (FlowLayout) customPanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		
		getContentPane().add(customPanel, "cell 0 3,grow");
		btnAddPartSize.setEnabled(false);
		btnAddPartSize.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				FBDefineParts dialog;
				double newPartSize = 0;
				double remainingSize = file.length();
				while(remainingSize > 0 && newPartSize >= 0) {
					dialog = new FBDefineParts(thisDialogReference, remainingSize);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
					newPartSize = dialog.getNewPartSize();
					if(newPartSize >= 0) {
						remainingSize -= newPartSize;
						customPartsModel.addRow(new Double[] {newPartSize});
					}
				}
			}
		});
		
		
		customPanel.add(btnAddPartSize);
		{
			JPanel buttonPanel = new JPanel();
			getContentPane().add(buttonPanel, "cell 0 4,growx,aligny top");
			buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
			buttonPanel.add(okButton);
			okButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					dispose();
				}
			});
			okButton.setActionCommand("OK");
			getRootPane().setDefaultButton(okButton);
			{
				buttonPanel.add(cancelButton);
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						cmbModeSelect.setSelectedIndex(-1);
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
			}
		}
		modePanel.setBorder(null);
		
		getContentPane().add(modePanel, "cell 0 0 2 1,growx,aligny top");
		{
			modePanel.add(lblFileName);
			lblFileName.setText(file.getName());
			{
				lblFileName.setHorizontalAlignment(SwingConstants.LEFT);
			}
		}
		lblFileName.setLabelFor(cmbModeSelect);
		modePanel.add(cmbModeSelect);
		
		cmbModeSelect.setModel(new DefaultComboBoxModel<TaskMode>(TaskMode.values()));
		cmbModeSelect.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					enableFields((TaskMode)cmbModeSelect.getSelectedItem());
				}
			}
		});
		cmbModeSelect.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					dispose();
					okButton.doClick();
				}
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					cmbModeSelect.setSelectedIndex(-1);
					dispose();
					cancelButton.doClick();
				}
			}
		});
	}
	
	public TaskMode getChoice() {
		return (TaskMode)cmbModeSelect.getSelectedItem();
	}
	
	private int getUnit() {
		switch((Unit)cmbUnit.getSelectedItem()) {
			case B:		return 1;
			case KB:	return 1024;
			case MB:	return 1024*1024;
			case GB:	return 1024*1024*1024;
			default: 	throw new NullPointerException();
		}
	}
	
	private void enableFields(TaskMode mode) {
		switch(mode) {
			case SAME_SIZE:	
				sizeField.setEnabled(true);
				cmbUnit.setEnabled(true);
				numberField.setEnabled(false);
				btnAddPartSize.setEnabled(false);
				break;
			case CRYPT_SAME_SIZE:
				sizeField.setEnabled(true);
				cmbUnit.setEnabled(true);
				numberField.setEnabled(false);
				btnAddPartSize.setEnabled(false);
				break;
			case CUSTOM_NUMBER:
				sizeField.setEnabled(false);
				cmbUnit.setEnabled(false);
				numberField.setEnabled(true);
				btnAddPartSize.setEnabled(false);
				break;
			case ZIP_CUSTOM_SIZE:
				sizeField.setEnabled(false);
				cmbUnit.setEnabled(false);
				numberField.setEnabled(false);
				btnAddPartSize.setEnabled(true);
				break;
		}
	}

	/**
	 * @return the partsSize
	 */
	public long getPartsSize() {
		return partsSize;
	}

}
