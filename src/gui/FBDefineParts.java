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

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class FBDefineParts extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JComboBox cmbUnit = new JComboBox();
	private long remainingSize;
	private long newPartSize;

	/**
	 * Create the dialog.
	 */
	public FBDefineParts(Window owner, long size) {
		super(owner, "Definisci dimensioni parti", ModalityType.DOCUMENT_MODAL);
		
		remainingSize = size;
		
		setBounds(100, 100, 265, 223);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblSizeLeft = new JLabel("Rimangono ancora " + remainingSize);
			contentPanel.add(lblSizeLeft);
		}
		{
			NumberFormat longFormat = NumberFormat.getIntegerInstance();

			NumberFormatter numberFormatter = new NumberFormatter(longFormat);
			numberFormatter.setValueClass(Long.class); //optional, ensures you will always get a long value
			numberFormatter.setAllowsInvalid(false); //this is the key!!
			numberFormatter.setMinimum(0l); //Optional
			numberFormatter.setMaximum(remainingSize);
			
			JFormattedTextField partSizeField = new JFormattedTextField(numberFormatter);
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
			cmbUnit.setModel(new DefaultComboBoxModel(new String[] {"B", "KB", "MB", "GB"}));
			cmbUnit.setSelectedIndex(0);
			cmbUnit.setMaximumRowCount(4);
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
		switch(cmbUnit.getSelectedIndex()) {
			case 0:	return 1;
			case 1: return 1024;
			case 2:	return 1024*1024;
			case 3: return 1024*1024*1024;
			default: throw new NullPointerException();
		}
	}

	/**
	 * @return the newPartSize
	 */
	public long getNewPartSize() {
		return newPartSize;
	}

}
