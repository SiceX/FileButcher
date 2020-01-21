package gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import logic.TaskMode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@SuppressWarnings("serial")
public class FBSelectMode extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JComboBox<TaskMode> cmbModeSelect = new JComboBox<TaskMode>();
	private final JButton okButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");
	private final JLabel lblFileName = new JLabel("");

	/**
	 * Create the dialog.
	 * @param fileName 
	 */
	@SuppressWarnings("unchecked")
	public FBSelectMode(Window owner, String fileName) {
		super(owner, "Seleziona modalità di esecuzione", ModalityType.DOCUMENT_MODAL);
		lblFileName.setLabelFor(cmbModeSelect);
		
		setTitle("Seleziona azione da eseguire");

		setBounds(100, 100, 380, 278);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
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
			contentPanel.add(lblFileName);
			lblFileName.setText(fileName);
			{
				lblFileName.setHorizontalAlignment(SwingConstants.LEFT);
			}
			cmbModeSelect.setModel(new DefaultComboBoxModel<TaskMode>(TaskMode.values()));
			contentPanel.add(cmbModeSelect);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
			buttonPane.add(okButton);
			okButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					dispose();
				}
			});
			okButton.setActionCommand("OK");
			getRootPane().setDefaultButton(okButton);
			{
				buttonPane.add(cancelButton);
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
	}
	
	public TaskMode getChoice() {
		return (TaskMode)cmbModeSelect.getSelectedItem();
	}

}
