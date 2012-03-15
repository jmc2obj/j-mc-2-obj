package org.jmc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Settings extends JFrame implements WindowListener {

	private static final long serialVersionUID = -5546934145954405065L;

	private int ground_level;
	private String last_loaded_map;
	
	public Colors minecraft_colors; 
	public Textures minecraft_textures;


	private Preferences prefs;

	private JTextField tfGround;
	private JLabel lGroundValidation;

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

		JPanel pGround=new JPanel();
		pGround.setLayout(new BoxLayout(pGround, BoxLayout.LINE_AXIS));
		pGround.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		JLabel lGround=new JLabel("Ground level: ");
		tfGround=new JTextField(""+ground_level);
		tfGround.getDocument().addDocumentListener(document_listener);
		lGroundValidation=new JLabel("Wrong value entered!");
		lGroundValidation.setForeground(Color.red);
		lGroundValidation.setVisible(false);
		pGround.add(lGround);
		pGround.add(tfGround);
		pGround.add(lGroundValidation);

		JButton tex=new JButton("Split textures");		
		tex.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Texsplit();

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

		pGround.setAlignmentX(Component.LEFT_ALIGNMENT);
		tex.setAlignmentX(Component.LEFT_ALIGNMENT);
		reset.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		mp.add(pGround);
		mp.add(Box.createRigidArea(new Dimension(0, 10)));
		mp.add(tex);
		mp.add(Box.createVerticalGlue());
		mp.add(reset);		
		
		mp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		addWindowListener(this);
	}

	
	public int getGroundLevel()
	{
		return ground_level;
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
	
	private void getFields()
	{
		if(tfGround.getText().length()>0)
		{
			try{			
				ground_level=Integer.parseInt(tfGround.getText());
				lGroundValidation.setVisible(false);
			}catch (NumberFormatException e) {
				lGroundValidation.setVisible(true);
				tfGround.requestFocusInWindow();
			}
		}

		saveSettings();
	}
	
	private void setFields()
	{
		tfGround.setText(""+ground_level);
	}

	private void loadSettings()
	{
		ground_level=prefs.getInt("GROUND_LEVEL", 0);	
		last_loaded_map=prefs.get("LAST_MAP", "");
	}

	private void saveSettings()
	{
		prefs.putInt("GROUND_LEVEL", ground_level);
		prefs.put("LAST_MAP", last_loaded_map);
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
}
