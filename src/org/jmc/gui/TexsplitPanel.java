package org.jmc.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.jmc.Options;
import org.jmc.Texsplit;
import org.jmc.util.Log;
import org.jmc.util.Messages;

@SuppressWarnings("serial")
public class TexsplitPanel extends JPanel
{
	
	private Preferences prefs;
	
	private File destination;
	private boolean alphas;
	
	private JTextField tfDest;
	private JComboBox<String> cScale;
	private JCheckBox cbAlpha;
	private JCheckBox cbMerge;

	private OBJExportWindow parent;
	
	public TexsplitPanel(OBJExportWindow parent)
	{
		this.parent=parent;
		
		prefs=MainWindow.settings.getPreferences();
		
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		JPanel pDest=new JPanel();
		pDest.setLayout(new BoxLayout(pDest, BoxLayout.LINE_AXIS));
		pDest.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lDest=new JLabel(Messages.getString("TexsplitDialog.DESTINATION"));
		tfDest=new JTextField(MainWindow.settings.getLastExportPath()+"/tex");
		pDest.add(lDest);
		pDest.add(tfDest);

		JPanel pScale = new JPanel();
		pScale.setLayout(new BoxLayout(pScale, BoxLayout.LINE_AXIS));
		pScale.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lScale=new JLabel(Messages.getString("TexsplitDialog.PRESCALE"));
		cScale=new JComboBox<String>(new String[] {"1x","2x","4x","8x","16x"});
		pScale.add(lScale);
		pScale.add(cScale);

		JPanel pAlpha = new JPanel();
		pAlpha.setLayout(new BoxLayout(pAlpha, BoxLayout.LINE_AXIS));
		pAlpha.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbAlpha=new JCheckBox(Messages.getString("TexsplitDialog.EXP_ALPHA"), true);
		pAlpha.add(cbAlpha);

		JPanel pMerge = new JPanel();
		pMerge.setLayout(new BoxLayout(pMerge, BoxLayout.LINE_AXIS));
		pMerge.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbMerge=new JCheckBox(Messages.getString("TexsplitDialog.EXP_SINGLE"), Options.textureMerge);
		pMerge.add(cbMerge);

		JPanel pQuest=new JPanel();
		pQuest.setLayout(new BoxLayout(pQuest, BoxLayout.LINE_AXIS));
		JLabel lQuest=new JLabel(Messages.getString("TexsplitDialog.TEX_LOC"));
		pQuest.add(lQuest);

		JPanel pButtons=new JPanel();
		pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.LINE_AXIS));
		JButton bMinecraft=new JButton(Messages.getString("TexsplitDialog.MINECRAFT"));
		JButton bCustom=new JButton(Messages.getString("TexsplitDialog.CUSTOM"));
		pButtons.add(bMinecraft);
		pButtons.add(bCustom);	

		loadSettings();
		
		bMinecraft.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				destination=new File(tfDest.getText());
				alphas=cbAlpha.isSelected();
				Options.texturePack=null;
				Options.textureScale=Double.parseDouble(cScale.getSelectedItem().toString().replace("x",""));
				Options.textureMerge=cbMerge.isSelected();

				doExport();
			}
		});

		bCustom.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alphas=cbAlpha.isSelected();
				destination=new File(tfDest.getText());
				JFileChooser jfc=new JFileChooser();
				jfc.setDialogType(JFileChooser.OPEN_DIALOG);
				int retval=jfc.showDialog(TexsplitPanel.this,Messages.getString("TexsplitDialog.SEL_PACK"));
				if(retval!=JFileChooser.APPROVE_OPTION) return;
				Options.texturePack=jfc.getSelectedFile();
				Options.textureScale=Double.parseDouble(cScale.getSelectedItem().toString().replace("x",""));
				Options.textureMerge=cbMerge.isSelected();

				doExport();
			}
		});
		
		AbstractAction genericSaveAction=new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveSettings();
			}	
		};
		
		cScale.addActionListener(genericSaveAction);
		cbAlpha.addActionListener(genericSaveAction);
		cbMerge.addActionListener(genericSaveAction);

		add(pDest);
		add(pScale);
		add(pAlpha);
		add(pMerge);
		add(Box.createVerticalStrut(10));
		add(pQuest);
		add(pButtons);
		add(Box.createVerticalStrut(10));
	}
	
	public void setSaveDir(File dir)
	{
		tfDest.setText(dir.getAbsolutePath()+"/tex");
	}

	private void doExport()
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(Options.textureMerge)
					{
						Texsplit.mergeTextures(
								destination, Options.texturePack, Options.textureScale, alphas,
								parent);
					}
					else
					{
						Texsplit.splitTextures(
								destination, Options.texturePack, Options.textureScale, alphas,
								parent);
					}
				}
				catch (Exception e) {
					Log.error(Messages.getString("TexsplitDialog.ERR_EXP"), e);
				}			
			}
		}).start();								
	}


	private void loadSettings()
	{				
		cScale.setSelectedItem(""+prefs.getDouble("TEXTURE_SCALE_ID", 1.0));		
		cbAlpha.setSelected(prefs.getBoolean("TEXTURE_ALPHA", false));
		cbMerge.setSelected(prefs.getBoolean("TEXTURE_MERGE", false));
	}
	
	
	private void saveSettings()
	{
		updateSettings();
		
		prefs.putDouble("TEXTURE_SCALE_ID", Options.textureScale);
		prefs.putBoolean("TEXTURE_ALPHA", Options.textureAlpha);
		prefs.putBoolean("TEXTURE_MERGE", Options.textureMerge);
	}
	
	private void updateSettings()
	{
		String txt=cScale.getSelectedItem().toString();
		if(!txt.isEmpty())
		{
			if(txt.endsWith("x")) txt=txt.substring(0,txt.length()-1);
			
			try{
				Options.textureScale=Double.parseDouble(txt);
			}catch (NumberFormatException e) {
				Log.error(Messages.getString("TexsplitDialog.ERR_SCALE"), e,false);
			}
		}
		
		Options.textureAlpha=cbAlpha.isSelected();
		Options.textureMerge=cbMerge.isSelected();
	}
	
}
