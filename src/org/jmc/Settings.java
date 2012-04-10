package org.jmc;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Settings extends JFrame implements WindowListener, ChangeListener {

	private static final long serialVersionUID = -5546934145954405065L;

	private Preferences prefs;

	JComboBox<String> cbMove,cbSelect;

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

		String actions[]={"left mouse button",
				"right mouse button",
				"middle mouse button",
				"shift + left mouse button",
				"shift + right mouse button",
		"shift + middle mouse button"};

		JPanel pMove=new JPanel();
		pMove.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		pMove.setLayout(new BoxLayout(pMove, BoxLayout.LINE_AXIS));
		JLabel lMove=new JLabel("Drag map using: ");
		cbMove=new JComboBox<String>(actions);
		pMove.add(lMove);
		pMove.add(cbMove);

		JPanel pSelect=new JPanel();
		pSelect.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		pSelect.setLayout(new BoxLayout(pSelect, BoxLayout.LINE_AXIS));
		JLabel lSelect=new JLabel("Select using: ");
		cbSelect=new JComboBox<String>(actions);
		pSelect.add(lSelect);
		pSelect.add(cbSelect);

		JButton reset=new JButton("Restore to factory settings");
		reset.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				int retval=JOptionPane.showConfirmDialog(Settings.this, "Are you sure?");
				if(retval==JOptionPane.YES_OPTION)
					resetSettings();
			}
		});	

		ActionListener saveAction=new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(cbMove.getSelectedIndex()==cbSelect.getSelectedIndex())
				{
					if(e.getSource().equals(cbMove))
					{
						if(cbMove.getSelectedIndex()==0)
							cbSelect.setSelectedIndex(1);
						else
							cbSelect.setSelectedIndex(0);
					}
					else
					{
						if(cbSelect.getSelectedIndex()==0)
							cbMove.setSelectedIndex(1);
						else
							cbMove.setSelectedIndex(0);
					}
				}
				saveSettings();				
			}
		};

		pMove.setAlignmentX(Component.LEFT_ALIGNMENT);
		pSelect.setAlignmentX(Component.LEFT_ALIGNMENT);
		reset.setAlignmentX(Component.LEFT_ALIGNMENT);

		mp.add(pMove);
		mp.add(pSelect);
		mp.add(Box.createVerticalGlue());
		mp.add(reset);		

		mp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		loadSettingsAfter();

		addWindowListener(this);
		cbMove.addActionListener(saveAction);
		cbSelect.addActionListener(saveAction);
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

	public int getMoveAction()
	{
		return cbMove.getSelectedIndex();
	}
	
	public int getSelectAction()
	{
		return cbSelect.getSelectedIndex();
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
		cbMove.setSelectedIndex(prefs.getInt("MOVE_ACTION", 1));
		cbSelect.setSelectedIndex(prefs.getInt("SELECT_ACTION", 0));
	}

	private void saveSettings()
	{
		prefs.putInt("MOVE_ACTION", cbMove.getSelectedIndex());
		prefs.putInt("SELECT_ACTION", cbSelect.getSelectedIndex());
	}

	private void resetSettings()
	{
		try {
			prefs.clear();
			cbMove.setSelectedIndex(1);
			cbSelect.setSelectedIndex(0);
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
