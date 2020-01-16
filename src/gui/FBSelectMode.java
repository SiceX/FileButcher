package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import logic.TaskMode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FBSelectMode extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JComboBox cmbModeSelect = new JComboBox();

	/**
	 * Create the dialog.
	 */
	@SuppressWarnings("unchecked")
	public FBSelectMode() {
		
		setTitle("Seleziona azione da eseguire");

		setBounds(100, 100, 146, 175);
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
