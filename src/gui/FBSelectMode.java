package gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import logic.TaskMode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
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

@SuppressWarnings("serial")
public class FBSelectMode extends JDialog {

	private final Window thisDialogReference = this;
	private final JPanel sizePanel = new JPanel();
	private final JComboBox<TaskMode> cmbModeSelect = new JComboBox<TaskMode>();
	private final JButton okButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");
	private final JLabel lblFileName = new JLabel("");
	private final JPanel modePanel = new JPanel();
	private JFormattedTextField sizeField;
	private final JComboBox cmbUnit = new JComboBox();
	private final JPanel numberPanel = new JPanel();
	private JFormattedTextField numberField;
	private final JLabel lblNewLabel = new JLabel("parti");
	private final JPanel customPanel = new JPanel();
	private final JButton btnAddPartSize = new JButton("Definisci parti");
	private final JTable tblParts = new JTable();
	
	/**
	 * Grandezza della parte in byte
	 */
	private long partsSize;

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
		numberFormatter.setMinimum(0l); //Optional

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

		setBounds(100, 100, 380, 373);
		getContentPane().setLayout(new MigLayout("", "[358px,grow]", "[54.00px][top][top][100px,grow,fill][39px]"));
		sizePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(sizePanel, "cell 0 1,grow");
		sizePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
		
		sizePanel.add(sizeField);
		cmbUnit.setMaximumRowCount(4);
		cmbUnit.setModel(new DefaultComboBoxModel(new String[] {"B", "KB", "MB", "GB"}));
		cmbUnit.setSelectedIndex(0);
		
		sizePanel.add(cmbUnit);
		FlowLayout flowLayout = (FlowLayout) numberPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		numberPanel.setBorder(null);
		
		getContentPane().add(numberPanel, "cell 0 2,grow");
		
		numberPanel.add(numberField);
		
		numberPanel.add(lblNewLabel);
		FlowLayout flowLayout_1 = (FlowLayout) customPanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		
		getContentPane().add(customPanel, "cell 0 3,grow");
		btnAddPartSize.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				FBDefineParts dialog;
				long newPartSize = 0;
				long remainingSize = file.length();
				while(remainingSize > 0) {
					dialog = new FBDefineParts(thisDialogReference, remainingSize);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
					newPartSize = dialog.getNewPartSize();
					remainingSize -= newPartSize;
					
					//TODO: AGGIUNGI A TABELLA NUOVA PARTE
				}
			}
		});
		
		customPanel.add(btnAddPartSize);
		
		customPanel.add(tblParts);
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
		
		getContentPane().add(modePanel, "cell 0 0,growx,aligny top");
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
		switch(cmbUnit.getSelectedIndex()) {
			case 0:	return 1;
			case 1: return 1024;
			case 2:	return 1024*1024;
			case 3: return 1024*1024*1024;
			default: throw new NullPointerException();
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
