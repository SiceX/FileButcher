package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import logic.TaskMode;
import logic.Unit;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class FBDefineParts extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JComboBox<Unit> cmbUnit = new JComboBox<Unit>();
	private JLabel lblSizeLeft;
	private JFormattedTextField partSizeField;
	private NumberFormatter numberFormatter;
	private long remainingSize;
	private long newPartSize;

	/**
	 * Create the dialog.
	 */
	public FBDefineParts(Window owner, long size) {
		super(owner, "Definisci dimensioni parti", ModalityType.DOCUMENT_MODAL);
		
		remainingSize = size;
		
		NumberFormat longFormat = NumberFormat.getIntegerInstance();
		NumberFormatter numberFormatter = new NumberFormatter(longFormat);
		numberFormatter.setValueClass(Long.class); //optional, ensures you will always get a long value
		numberFormatter.setAllowsInvalid(false); //this is the key!!
		
		setBounds(100, 100, 265, 223);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			//Impostato in base all'unità di misura selezionata, che all'inizio è B(Byte)
			lblSizeLeft = new JLabel("Rimangono ancora " + remainingSize/1 + " B");
			contentPanel.add(lblSizeLeft);
		}
		{
			//Il massimo deve essere impostato in base all'unità di misura selezionata, che all'inizio è B(Byte)
			numberFormatter.setMaximum(remainingSize/1);	
			
			partSizeField = new JFormattedTextField(numberFormatter);
			partSizeField.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					newPartSize = Integer.parseUnsignedInt(partSizeField.getText()) * getUnit();
				}
			});
			partSizeField.setColumns(10);
			contentPanel.add(partSizeField);
		}
		{
			cmbUnit.setModel(new DefaultComboBoxModel<Unit>(Unit.values()));
			cmbUnit.setSelectedIndex(0);
			cmbUnit.setMaximumRowCount(4);
			cmbUnit.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					if (arg0.getStateChange() == ItemEvent.SELECTED) {
						//Il massimo deve essere impostato in base all'unità di misura selezionata
						numberFormatter.setMaximum(remainingSize/getUnit());	
						
						partSizeField = new JFormattedTextField(numberFormatter);
						
						lblSizeLeft.setText("Rimangono ancora " + remainingSize/getUnit() + cmbUnit.getSelectedItem());
					}
				}
			});
			contentPanel.add(cmbUnit);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
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

	/**
	 * @return the newPartSize
	 */
	public long getNewPartSize() {
		return newPartSize;
	}

}
