package gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import logic.TaskMode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class FBSelectMode extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JComboBox cmbModeSelect = new JComboBox();

	/**
	 * Create the dialog.
	 */
	@SuppressWarnings("unchecked")
	public FBSelectMode(Window owner) {
		super(owner, "Seleziona modalità di esecuzione", ModalityType.DOCUMENT_MODAL);
		
		setTitle("Seleziona azione da eseguire");

		setBounds(100, 100, 372, 143);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			cmbModeSelect.setModel(new DefaultComboBoxModel(TaskMode.values()));
			contentPanel.add(cmbModeSelect);
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
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						cmbModeSelect.setSelectedIndex(-1);
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public TaskMode getChoice() {
		return (TaskMode)cmbModeSelect.getSelectedItem();
	}

}
