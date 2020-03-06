package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import logic.Unit;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

@SuppressWarnings("serial")
public class FBDefineParts extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JComboBox<Unit> cmbUnit = new JComboBox<Unit>();
	private JLabel lblSizeLeft;
	private JTextField partSizeField;
	//private NumberFormatter numberFormatter;
	//private PartSizeFilter partSizeFilter;
	private double remainingSize;
	private double newPartSize;

	/**
	 * Create the dialog.
	 */
	public FBDefineParts(Window owner, double size) {
		super(owner, "Definisci dimensioni parti", ModalityType.DOCUMENT_MODAL);
		
		remainingSize = size;
		
//		NumberFormat longFormat = NumberFormat.getIntegerInstance();
//		NumberFormatter numberFormatter = new NumberFormatter(longFormat);
//		numberFormatter.setValueClass(Long.class); //optional, ensures you will always get a long value
//		numberFormatter.setAllowsInvalid(true); //this is the key!!
		
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
			//partSizeFilter = new PartSizeFilter(remainingSize/1);
			
			partSizeField = new JTextField();
			
			Document doc = partSizeField.getDocument();
			if (doc instanceof AbstractDocument) {
			    //AbstractDocument abDoc = (AbstractDocument)doc;
			    //abDoc.setDocumentFilter(partSizeFilter);
			} 
			
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
						//partSizeFilter.setMaxSize(remainingSize/getUnit());
						//Quando viene cambiato il massimo, effettuo un replace con la stessa stringa. Se questa non passa il filtro, svuoto il campo.
						partSizeField.setText(partSizeField.getText());
						
						DecimalFormat df = new DecimalFormat("#.##");
						lblSizeLeft.setText("Rimangono ancora " + df.format(remainingSize/getUnit()) + cmbUnit.getSelectedItem());
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
						if(partSizeField.getText().equals("")) {
							partSizeField.setBorder(new LineBorder(Color.RED, 2));
							Toolkit.getDefaultToolkit().beep();
						}
						else {
							newPartSize = Long.parseLong(partSizeField.getText());
							dispose();
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						newPartSize = -1;
						dispose();
					}
				});
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
	public double getNewPartSize() {
		return newPartSize*getUnit();
	}

}
