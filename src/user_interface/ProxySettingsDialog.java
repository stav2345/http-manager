package user_interface;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import config.ProxyConfig;
import proxy.ProxyMode;

public class ProxySettingsDialog extends JFrame {

	private static final long serialVersionUID = 8417106752066127434L;

	private static final int SAVE_ID = 1;
	public static final String SAVE_ACTION = "save";
	private static final int TEST_CONN_ID = 2;
	public static final String TEST_CONN_ACTION = "testConn";
	
	private static final String TEST_CONN_URL = "http://www.google.com";
	
	private Collection<ActionListener> listeners;
	
	public ProxySettingsDialog() {
		this.listeners = new ArrayList<>();
		createContents();
	}
	
	public void createContents() {
		
		JPanel panel = new JPanel();
		Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		panel.setBorder(padding);
		
		panel.setLayout(new GridLayout(4, 2));
		
		JLabel proxyModeLabel = new JLabel("Proxy:");
		JComboBox<ProxyMode> proxyMode = new JComboBox<>(ProxyMode.values());
		
		JLabel hostnameLabel = new JLabel("Hostname:");
		TextField hostnameText = new TextField();
		
		JLabel portLabel = new JLabel("Port:");
		TextField portText = new TextField();
		
		JButton testConnectionBtn = new JButton("Test connection");
		JButton saveBtn = new JButton("Save");
		
		panel.add(proxyModeLabel);
		panel.add(proxyMode);
		panel.add(hostnameLabel);
		panel.add(hostnameText);
		panel.add(portLabel);
		panel.add(portText);
		panel.add(testConnectionBtn);
		panel.add(saveBtn);
		
		this.setContentPane(panel);
		this.pack();
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		
		proxyMode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ProxyMode mode = (ProxyMode) proxyMode.getSelectedItem();
				
				boolean manualMode = mode == ProxyMode.MANUAL;
				
				hostnameText.setEnabled(manualMode);
				portText.setEnabled(manualMode);
			}
		});
		
		testConnectionBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				ProxySettingsDialog.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				Actions actions = new Actions();
				
				// save the configuration before testing
				// otherwise not working
				actions.save((ProxyMode) proxyMode.getSelectedItem(), 
						hostnameText.getText(), portText.getText());
				
				boolean success = actions.testConnection(TEST_CONN_URL);
				
				String message = null;
				int icon;
				
				if (success) {
					message = "Test connection: success!";
					icon = JOptionPane.INFORMATION_MESSAGE;
				}
				else {
					message = "Test connection failed!";
					icon = JOptionPane.ERROR_MESSAGE;
				}
				
				ProxySettingsDialog.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
				JOptionPane.showMessageDialog(ProxySettingsDialog.this, message, "", icon);
				
				notifyListeners(TEST_CONN_ID, TEST_CONN_ACTION);
			}
		});
		
		saveBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				Actions actions = new Actions();
				actions.save((ProxyMode) proxyMode.getSelectedItem(), 
						hostnameText.getText(), portText.getText());
				
				dispatchEvent(new WindowEvent(ProxySettingsDialog.this, 
						WindowEvent.WINDOW_CLOSING));
				
				notifyListeners(SAVE_ID, SAVE_ACTION);
			}
		});
		
		this.setTitle("Proxy settings");
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				dispose();
			}
		});
		
		// set default values contained in the file
		ProxyConfig config = new ProxyConfig();
		hostnameText.setText(config.getProxyHostname());
		portText.setText(config.getProxyPort());
		proxyMode.setSelectedItem(config.getProxyMode());
	}
	
	/**
	 * Notify all the listeners of the dialog
	 * @param id
	 * @param action
	 */
	private void notifyListeners(int id, String action) {
		for (ActionListener listener: listeners)
			listener.actionPerformed(new ActionEvent(this, id, action));
	}
	
	/**
	 * Add a listener for the dialog
	 * @param listener
	 */
	public void addActionListener(ActionListener listener) {
		this.listeners.add(listener);
	}
	
	public static void main(String[] args) {
		
		ProxySettingsDialog frame = new ProxySettingsDialog();
		frame.setVisible(true);
	}
}
