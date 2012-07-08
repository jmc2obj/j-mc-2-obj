package org.jmc.gui;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jmc.Options;
import org.jmc.util.Messages;




@SuppressWarnings("serial")
public class FileNames extends JFrame {
	
	Preferences pref;
	JTextField tfObj,tfMtl;
	
	public FileNames()
	{
		pref=MainWindow.settings.getPreferences();
		
		JPanel mp=new JPanel();
		mp.setLayout(new BoxLayout(mp, BoxLayout.PAGE_AXIS));				
		add(mp);
		
		JPanel pObj=new JPanel();
		pObj.setLayout(new BoxLayout(pObj, BoxLayout.LINE_AXIS));
		pObj.setMaximumSize(new Dimension(Short.MAX_VALUE,20));		
		JLabel lObj=new JLabel(Messages.getString("FileNames.FNAME_OBJ"));
		tfObj=new JTextField();
		pObj.add(lObj);
		pObj.add(tfObj);
		
		JPanel pMtl=new JPanel();
		pMtl.setLayout(new BoxLayout(pMtl, BoxLayout.LINE_AXIS));
		pMtl.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		JLabel lMtl=new JLabel(Messages.getString("FileNames.FNAME_MTL"));
		tfMtl=new JTextField();		
		pMtl.add(lMtl);
		pMtl.add(tfMtl);

		loadSettings();
		
		DocumentListener save_listener=new DocumentListener() {			
			@Override
			public void removeUpdate(DocumentEvent e) {
				saveSettings();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				saveSettings();
			}			
			@Override
			public void changedUpdate(DocumentEvent e) {
				saveSettings();				
			}
		};
		
		tfObj.getDocument().addDocumentListener(save_listener);
		tfMtl.getDocument().addDocumentListener(save_listener);

		JPanel pClose=new JPanel();
		pClose.setLayout(new BoxLayout(pClose, BoxLayout.LINE_AXIS));
		pClose.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		JButton bClose=new JButton(Messages.getString("FileNames.CLOSE"));
		bClose.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		pClose.add(bClose);
		
		bClose.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		
		mp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		mp.add(pObj);
		mp.add(pMtl);
		mp.add(pClose);
		
		pack();
		
		//this hack is easiest since pack seems to ignore minimum sizes...
		if(getWidth()<400)
		{
			setSize(new Dimension(400,getHeight()));
		}
	}
	
	/**
	 * Loads the options from the prefs store. Updates the UI controls and the global Options.
	 */
	private void loadSettings()
	{
		Options.objFileName = pref.get("OBJ_FILE_NAME", "minecraft.obj");
		tfObj.setText(Options.objFileName);
		
		Options.mtlFileName = pref.get("MTL_FILE_NAME", "minecraft.mtl");
		tfMtl.setText(Options.mtlFileName);
	}
	
	/**
	 * Saves the options to the prefs store. Also updates the global Options.
	 */
	private void saveSettings()
	{
		String txt;
		
		txt = tfObj.getText().trim();
		if (txt.length() > 0)
		{
			Options.objFileName = txt;
			pref.put("OBJ_FILE_NAME", txt);
		}
		txt = tfMtl.getText().trim();
		if (txt.length() > 0)
		{
			Options.mtlFileName = txt;
			pref.put("MTL_FILE_NAME",txt);
		}
	}
	
	public void display()
	{
		Point p=MainWindow.main.getLocation();
		p.x+=(MainWindow.main.getWidth()-getWidth())/2;
		p.y+=(MainWindow.main.getHeight()-getHeight())/2;
		setLocation(p);
		setVisible(true);
	}
	
}
