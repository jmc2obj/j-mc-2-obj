package org.jmc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jmc.Options;
import org.jmc.util.Filesystem;
import org.jmc.util.Log;
import org.jmc.util.Messages;

public class Settings extends JFrame implements WindowListener, ChangeListener {

	private static final long serialVersionUID = -5546934145954405065L;

	private Preferences prefs;

	JComboBox cbMove,cbSelect,cbLang;
	JTextArea taRestart;

	@SuppressWarnings("serial")
	public Settings()
	{				
		prefs = Preferences.userNodeForPackage(getClass());		

		loadSettings();

		setTitle(Messages.getString("Settings.SETTINGS")); 

		setSize(400,300);

		JPanel mp = new JPanel();
		add(mp);

		mp.setLayout(new BoxLayout(mp, BoxLayout.PAGE_AXIS));

		String actions[]={Messages.getString("Settings.LMB"), 
				Messages.getString("Settings.RMB"), 
				Messages.getString("Settings.MMB"), 
				Messages.getString("Settings.SLMB"), 
				Messages.getString("Settings.SRMB"), 
		Messages.getString("Settings.SMMB")}; 

		String languages[] = new String[Options.availableLocales.length];
		for(int i=0; i<languages.length; i++) 
		{
			languages[i]=Options.availableLocales[i].getDisplayLanguage(); 
		}

		JPanel pMove=new JPanel();
		pMove.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		pMove.setLayout(new BoxLayout(pMove, BoxLayout.LINE_AXIS));
		JLabel lMove=new JLabel(Messages.getString("Settings.DRAG")); 
		cbMove=new JComboBox(actions);
		pMove.add(lMove);
		pMove.add(cbMove);

		JPanel pSelect=new JPanel();
		pSelect.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		pSelect.setLayout(new BoxLayout(pSelect, BoxLayout.LINE_AXIS));
		JLabel lSelect=new JLabel(Messages.getString("Settings.SELECT")); 
		cbSelect=new JComboBox(actions);
		pSelect.add(lSelect);
		pSelect.add(cbSelect);

		JPanel pLang=new JPanel();
		pLang.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		pLang.setLayout(new BoxLayout(pLang, BoxLayout.LINE_AXIS));
		JLabel lLang=new JLabel(Messages.getString("Settings.LANGUAGE")); 
		cbLang=new JComboBox(languages);
		pLang.add(lLang);
		pLang.add(cbLang);

		JPanel pRestart=new JPanel();
		pRestart.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		pRestart.setLayout(new BoxLayout(pRestart, BoxLayout.LINE_AXIS));
		taRestart=new JTextArea(Messages.getString("Settings.RESTART_MSG")); 
		taRestart.setLineWrap(true);
		Font fRestart = taRestart.getFont();
		taRestart.setFont(new Font(fRestart.getFamily(),Font.BOLD,16));
		taRestart.setForeground(Color.red);
		taRestart.setBackground(getBackground());
		taRestart.setVisible(false);
		pRestart.add(taRestart);

		JPanel pButtons=new JPanel();
		pButtons.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.LINE_AXIS));
		JButton bReset=new JButton(Messages.getString("Settings.RESTORE")); 
		JButton bRestart=new JButton(Messages.getString("Settings.RESTART_BTN")); 
		pButtons.add(bReset);
		pButtons.add(bRestart);


		bReset.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				int retval=JOptionPane.showConfirmDialog(Settings.this, Messages.getString("Settings.ARE_YOU_SURE")); 
				if(retval==JOptionPane.YES_OPTION)
					resetSettings();
			}
		});	

		bRestart.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				File prog=Filesystem.getProgramExecutable();
				if(prog!=null)
				{
					final ArrayList<String> command = new ArrayList<String>();
					command.add("java"); 
					command.add("-jar"); 
					command.add(prog.getPath());

					final ProcessBuilder builder = new ProcessBuilder(command);
					try {
						builder.start();
					} catch (IOException e1) {
						Log.error(Messages.getString("Settings.RESTART_FAIL"), e1, true); 
					}
					System.exit(0);
				}
				else
				{
					Log.error(Messages.getString("Settings.RESTART_FAIL"), null, true); 
				}
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

		mp.add(pMove);
		mp.add(pSelect);
		mp.add(pLang);
		mp.add(Box.createVerticalGlue());
		mp.add(pRestart);
		mp.add(pButtons);		

		mp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		loadSettingsAfter();

		addWindowListener(this);
		cbMove.addActionListener(saveAction);
		cbSelect.addActionListener(saveAction);
		cbLang.addActionListener(saveAction);
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
		return prefs.get("LAST_MAP", "");  //$NON-NLS-2$
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

	public void setLastVisitedDir(String path)
	{
		prefs.put("LAST_VISITED_DIR", path); 
	}

	public String getLastVisitedDir()
	{
		return prefs.get("LAST_VISITED_DIR", getLastExportPath()); 
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
		cbLang.setSelectedIndex(prefs.getInt("LANGUAGE", 0)); 
	}

	private void saveSettings()
	{
		taRestart.setVisible(false);

		prefs.putInt("MOVE_ACTION", cbMove.getSelectedIndex()); 
		prefs.putInt("SELECT_ACTION", cbSelect.getSelectedIndex()); 

		int l=prefs.getInt("LANGUAGE", 0); 
		if(cbLang.getSelectedIndex()!=l)
		{
			prefs.putInt("LANGUAGE", cbLang.getSelectedIndex()); 
			taRestart.setVisible(true);
		}
	}

	private void resetSettings()
	{
		try {
			prefs.clear();
			cbMove.setSelectedIndex(1);
			cbSelect.setSelectedIndex(0);
			cbLang.setSelectedIndex(0);
		} catch (BackingStoreException e) {}
		loadSettings();
		setFields();
	}

	@SuppressWarnings("unused")
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
