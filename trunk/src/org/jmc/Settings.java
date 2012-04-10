package org.jmc;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Settings extends JFrame implements WindowListener, ChangeListener {

	private static final long serialVersionUID = -5546934145954405065L;
	

	private Preferences prefs;

	@SuppressWarnings("serial")
	public Settings()
	{				
		prefs = Preferences.userNodeForPackage(getClass());		

		loadSettings();

		setTitle("Settings");

		setSize(400,300);

		JPanel mp = new JPanel();
		add(mp);

		mp.setLayout(new BoxLayout(mp, BoxLayout.PAGE_AXIS));
				
		JPanel pTexAlpha=new JPanel();
		pTexAlpha.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		pTexAlpha.setLayout(new BoxLayout(pTexAlpha, BoxLayout.LINE_AXIS));		
		
		JButton reset=new JButton("Restore to factory settings");
		reset.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				int retval=JOptionPane.showConfirmDialog(Settings.this, "Are you sure?");
				if(retval==JOptionPane.YES_OPTION)
					resetSettings();
			}
		});

		pTexAlpha.setAlignmentX(Component.LEFT_ALIGNMENT);
		reset.setAlignmentX(Component.LEFT_ALIGNMENT);
				
		mp.add(pTexAlpha);
		mp.add(Box.createRigidArea(new Dimension(0, 10)));		
		mp.add(Box.createVerticalGlue());
		mp.add(reset);		
		
		mp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		loadSettingsAfter();
		
		addWindowListener(this);
	}
	
	public Preferences getPreferences()
	{
		return prefs;
	}

	public void setLastLoadedMap(String path)
	{
		prefs.put("LAST_MAP", path);	
	}
	
	public String getLastLoadedMap()
	{
		return prefs.get("LAST_MAP", "");
	}
	
	public void setLastExportPath(String path)
	{
		prefs.put("LAST_EXPORT_PATH", path);	
	}
	
	public String getLastExportPath()
	{
		File cwd=new File(".");
		String str="";
		try{
		str=cwd.getCanonicalPath();
		}catch(Exception e){}
		return prefs.get("LAST_EXPORT_PATH", str);
	}
	
	private void getFields()
	{
		saveSettings();
	}
	
	private void setFields()
	{		
	}

	private void loadSettings()
	{		
	}
	
	private void loadSettingsAfter()
	{
	}

	private void saveSettings()
	{		
	}
	
	private void resetSettings()
	{
		try {
			prefs.clear();
		} catch (BackingStoreException e) {}
		loadSettings();
		setFields();
	}
		
	private DocumentListener document_listener=new DocumentListener() {	
		@Override
		public void removeUpdate(DocumentEvent e) {
			getFields();
		}		
		@Override
		public void insertUpdate(DocumentEvent e) {
			getFields();
		}		
		@Override
		public void changedUpdate(DocumentEvent e) {
			getFields();
		}
	};

	@Override
	public void windowActivated(WindowEvent e) {
		loadSettings();
		setFields();
	}

	@Override
	public void windowClosed(WindowEvent e) {		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		saveSettings();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		saveSettings();
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {					
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		saveSettings();
		
	}
}
