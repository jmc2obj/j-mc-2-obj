package org.jmc;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
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
import javax.swing.JCheckBox;
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

	private String last_loaded_map;	
	
	public Colors minecraft_colors; 
	public Textures minecraft_textures;


	private Preferences prefs;

	private JCheckBox cbTexAlpha;

	@SuppressWarnings("serial")
	public Settings()
	{				
		prefs = Preferences.userNodeForPackage(getClass());		

		loadSettings();

		minecraft_colors=new Colors();
		minecraft_textures=new Textures();

		setTitle("Settings");

		setSize(400,300);

		JPanel mp = new JPanel();
		add(mp);

		mp.setLayout(new BoxLayout(mp, BoxLayout.PAGE_AXIS));
				
		JPanel pTexAlpha=new JPanel();
		pTexAlpha.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		pTexAlpha.setLayout(new BoxLayout(pTexAlpha, BoxLayout.LINE_AXIS));
		cbTexAlpha=new JCheckBox("Export alpha textures");
		cbTexAlpha.addChangeListener(this);
		pTexAlpha.add(cbTexAlpha);

		JButton tex_mc=new JButton("Split textures from minecraft");		
		tex_mc.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Texsplit split = new Texsplit();
				split.splitTextures(chooseTextureDestination(),cbTexAlpha.isSelected());
			}
		});
		JButton tex_custom=new JButton("Split custom textures");		
		tex_custom.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc=new JFileChooser();
				jfc.setDialogType(JFileChooser.OPEN_DIALOG);
				int retval=jfc.showDialog(Settings.this,"Choose your terrain.png file");
				if(retval==JFileChooser.APPROVE_OPTION)
				{					
					Texsplit split = new Texsplit(jfc.getSelectedFile());
					split.splitTextures(chooseTextureDestination(),cbTexAlpha.isSelected());
				}
			}
		});
		
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
		tex_mc.setAlignmentX(Component.LEFT_ALIGNMENT);
		tex_custom.setAlignmentX(Component.LEFT_ALIGNMENT);
		reset.setAlignmentX(Component.LEFT_ALIGNMENT);
				
		mp.add(pTexAlpha);
		mp.add(Box.createRigidArea(new Dimension(0, 10)));		
		mp.add(tex_mc);
		mp.add(tex_custom);
		mp.add(Box.createVerticalGlue());
		mp.add(reset);		
		
		mp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		loadSettingsAfter();
		
		addWindowListener(this);
	}

	public void setLastLoadedMap(String path)
	{
		last_loaded_map=path;
		saveSettings();
	}
	
	public String getLastLoadedMap()
	{
		return last_loaded_map;
	}
	
	
	public void setDefaultScale(float val)
	{		
		prefs.putFloat("DEFAULT_SCALE", val);
	}
	
	public float getDefaultScale()
	{
		return prefs.getFloat("DEFAULT_SCALE",1.0f);
	}
	
	public void setOffset(int type, int x, int z)
	{
		prefs.putInt("OFFSET_TYPE", type);
		prefs.putInt("OFFSET_X", x);
		prefs.putInt("OFFSET_Z", z);
	}
	
	public int getOffsetType()
	{
		return prefs.getInt("OFFSET_TYPE", 0);		
	}
	
	public Point getOffset()
	{
		return new Point(prefs.getInt("OFFSET_X", 0),prefs.getInt("OFFSET_Z", 0));
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
		last_loaded_map=prefs.get("LAST_MAP", "");	
	}
	
	private void loadSettingsAfter()
	{
		cbTexAlpha.setSelected(prefs.getBoolean("TEX_ALPHA", true));//?????	
	}

	private void saveSettings()
	{
		prefs.put("LAST_MAP", last_loaded_map);		
		prefs.putBoolean("TEX_ALPHA", cbTexAlpha.isSelected());
	}
	
	private void resetSettings()
	{
		try {
			prefs.clear();
		} catch (BackingStoreException e) {}
		loadSettings();
		setFields();
	}
	
	private File chooseTextureDestination()
	{
		JFileChooser jfc=new JFileChooser();
		jfc.setDialogType(JFileChooser.SAVE_DIALOG);
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int retval=jfc.showDialog(Settings.this,"Choose texture files' destination");
		if(retval==JFileChooser.APPROVE_OPTION)
			return jfc.getSelectedFile();
		else
			return null;
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
