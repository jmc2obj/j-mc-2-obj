package org.jmc.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.jmc.Options;
import org.jmc.Options.OffsetType;
import org.jmc.Options.OverwriteAction;
import org.jmc.util.Messages;


@SuppressWarnings("serial")
public class OBJExportOptions extends JPanel
{
	private Preferences prefs;
	private JTextField tfScale;
	private JRadioButton rbNoOffset,rbCenterOffset,rbCustomOffset;
	private JTextField tfXOffset,tfZOffset;
	private JCheckBox cbRenderSides;
	private JCheckBox cbRenderBiomes;
	private JCheckBox cbRenderEntities;
	private JCheckBox cbObjPerMat;
	private JCheckBox cbObjPerChunk;
	private JCheckBox cbRemoveDuplicates;
	private JRadioButton rbOBJAlways, rbOBJNever, rbOBJAsk;
	private JRadioButton rbMTLAlways, rbMTLNever, rbMTLAsk;

	public OBJExportOptions() {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		prefs=MainWindow.settings.getPreferences();

		JPanel pScale=new JPanel();		
		pScale.setLayout(new BoxLayout(pScale,BoxLayout.LINE_AXIS));
		pScale.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lScale=new JLabel(Messages.getString("OBJExportOptions.MAP_SCALE")); 
		tfScale=new JTextField(""); 
		pScale.add(lScale);
		pScale.add(tfScale);

		JPanel pOffset=new JPanel();
		pOffset.setLayout(new BoxLayout(pOffset,BoxLayout.LINE_AXIS));
		pOffset.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lOffset=new JLabel(Messages.getString("OBJExportOptions.OFFSET")); 
		rbNoOffset=new JRadioButton(Messages.getString("OBJExportOptions.NONE")); 
		rbCenterOffset=new JRadioButton(Messages.getString("OBJExportOptions.CENTER")); 
		rbCustomOffset=new JRadioButton(Messages.getString("OBJExportOptions.CUSTOM")); 
		tfXOffset=new JTextField("0"); 
		tfZOffset=new JTextField("0"); 
		pOffset.add(lOffset);
		pOffset.add(rbNoOffset);
		pOffset.add(rbCenterOffset);
		pOffset.add(rbCustomOffset);
		pOffset.add(tfXOffset);
		pOffset.add(tfZOffset);

		ButtonGroup gOffset=new ButtonGroup();
		gOffset.add(rbNoOffset);
		gOffset.add(rbCenterOffset);
		gOffset.add(rbCustomOffset);
		rbNoOffset.setActionCommand("none"); 
		rbCenterOffset.setActionCommand("center"); 
		rbCustomOffset.setActionCommand("custom"); 

		JPanel pSides=new JPanel();
		pSides.setLayout(new BoxLayout(pSides, BoxLayout.LINE_AXIS));
		pSides.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbRenderSides=new JCheckBox(Messages.getString("OBJExportOptions.R_SIDES")); 
		pSides.add(cbRenderSides);
		
		JPanel pBiomes=new JPanel();
		pBiomes.setLayout(new BoxLayout(pBiomes, BoxLayout.LINE_AXIS));
		pBiomes.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbRenderBiomes=new JCheckBox(Messages.getString("OBJExportOptions.R_BIOMES")); 
		pBiomes.add(cbRenderBiomes);
		
		JPanel pEntities=new JPanel();
		pEntities.setLayout(new BoxLayout(pEntities, BoxLayout.LINE_AXIS));
		pEntities.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbRenderEntities=new JCheckBox(Messages.getString("OBJExportOptions.R_ENTITIES")); 
		pEntities.add(cbRenderEntities);
		
		JPanel pObjPerMat=new JPanel();
		pObjPerMat.setLayout(new BoxLayout(pObjPerMat, BoxLayout.LINE_AXIS));
		pObjPerMat.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbObjPerMat=new JCheckBox(Messages.getString("OBJExportOptions.SEP_OBJ_MTL")); 
		pObjPerMat.add(cbObjPerMat);
		
		JPanel pObjPerChunk=new JPanel();
		pObjPerChunk.setLayout(new BoxLayout(pObjPerChunk, BoxLayout.LINE_AXIS));
		pObjPerChunk.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbObjPerChunk=new JCheckBox(Messages.getString("OBJExportOptions.SEP_OBJ_CUNK")); 
		pObjPerChunk.add(cbObjPerChunk);
		
		JPanel pRemoveDuplicates=new JPanel();
		pRemoveDuplicates.setLayout(new BoxLayout(pRemoveDuplicates, BoxLayout.LINE_AXIS));
		pRemoveDuplicates.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbRemoveDuplicates=new JCheckBox(Messages.getString("OBJExportOptions.DUPL_VERT")); 
		pRemoveDuplicates.add(cbRemoveDuplicates);
		
		JPanel pOBJOver = new JPanel();
		pOBJOver.setLayout(new BoxLayout(pOBJOver, BoxLayout.LINE_AXIS));
		pOBJOver.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lOBJOver=new JLabel(Messages.getString("OBJExportOptions.OVER_OBJ")); 
		rbOBJAsk=new JRadioButton(Messages.getString("OBJExportOptions.ASK")); 
		rbOBJAlways=new JRadioButton(Messages.getString("OBJExportOptions.ALWAYS")); 
		rbOBJNever=new JRadioButton(Messages.getString("OBJExportOptions.NEVER"));		 
		pOBJOver.add(lOBJOver);
		pOBJOver.add(rbOBJAsk);
		pOBJOver.add(rbOBJAlways);
		pOBJOver.add(rbOBJNever);
		
		ButtonGroup gOBJOver=new ButtonGroup();
		gOBJOver.add(rbOBJAsk);
		gOBJOver.add(rbOBJAlways);
		gOBJOver.add(rbOBJNever);
		rbOBJAsk.setActionCommand("ask"); 
		rbOBJAlways.setActionCommand("always"); 
		rbOBJNever.setActionCommand("never"); 
		
		JPanel pMTLOver = new JPanel();
		pMTLOver.setLayout(new BoxLayout(pMTLOver, BoxLayout.LINE_AXIS));
		pMTLOver.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lMTLOver=new JLabel(Messages.getString("OBJExportOptions.OVER_MTL")); 
		rbMTLAsk=new JRadioButton(Messages.getString("OBJExportOptions.ASK")); 
		rbMTLAlways=new JRadioButton(Messages.getString("OBJExportOptions.ALWAYS")); 
		rbMTLNever=new JRadioButton(Messages.getString("OBJExportOptions.NEVER"));		 
		pMTLOver.add(lMTLOver);
		pMTLOver.add(rbMTLAsk);
		pMTLOver.add(rbMTLAlways);
		pMTLOver.add(rbMTLNever);
		
		JPanel pNames=new JPanel();
		pNames.setLayout(new BoxLayout(pNames, BoxLayout.LINE_AXIS));
		pNames.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JButton bNames=new JButton(Messages.getString("OBJExportOptions.RENAME")); 
		pNames.add(bNames);
		
		bNames.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {	
				MainWindow.file_names.display();
			}
		});
		
		ButtonGroup gMTLOver=new ButtonGroup();
		gMTLOver.add(rbMTLAsk);
		gMTLOver.add(rbMTLAlways);
		gMTLOver.add(rbMTLNever);
		rbMTLAsk.setActionCommand("ask"); 
		rbMTLAlways.setActionCommand("always"); 
		rbMTLNever.setActionCommand("never"); 

		AbstractAction offsetSaveAction=new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent ev) {
				if(ev.getActionCommand().equals("custom")) 
				{
					tfXOffset.setEnabled(true);
					tfZOffset.setEnabled(true);
				}
				else
				{
					tfXOffset.setEnabled(false);
					tfZOffset.setEnabled(false);
				}
				saveSettings();
			}
		};

		rbNoOffset.addActionListener(offsetSaveAction);
		rbCenterOffset.addActionListener(offsetSaveAction);
		rbCustomOffset.addActionListener(offsetSaveAction);
		
		AbstractAction genericSaveAction=new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveSettings();
			}	
		};
		
		rbOBJAsk.addActionListener(genericSaveAction);
		rbOBJAlways.addActionListener(genericSaveAction);
		rbOBJNever.addActionListener(genericSaveAction);
		rbMTLAsk.addActionListener(genericSaveAction);
		rbMTLAlways.addActionListener(genericSaveAction);
		rbMTLNever.addActionListener(genericSaveAction);
		cbRenderSides.addActionListener(genericSaveAction);
		cbRenderBiomes.addActionListener(genericSaveAction);
		cbRenderEntities.addActionListener(genericSaveAction);
		cbObjPerMat.addActionListener(genericSaveAction);
		cbObjPerChunk.addActionListener(genericSaveAction);
		cbRemoveDuplicates.addActionListener(genericSaveAction);

		loadSettings();

		add(pScale);
		add(pOffset);
		add(pSides);
		add(pBiomes);
		add(pEntities);
		add(pObjPerMat);
		add(pObjPerChunk);
		add(pRemoveDuplicates);
		add(pOBJOver);
		add(pMTLOver);
		add(pNames);
	}


	/**
	 * Loads the options from the prefs store. Updates the UI controls and the global Options.
	 */
	private void loadSettings()
	{
		tfScale.setText(""+prefs.getFloat("DEFAULT_SCALE", 1.0f));  //$NON-NLS-2$
		
		switch(prefs.getInt("OFFSET_TYPE", 0)) 
		{
		case 0:
			rbNoOffset.setSelected(true);
			tfXOffset.setEnabled(false);
			tfZOffset.setEnabled(false);
			break;
		case 1:
			rbCenterOffset.setSelected(true);
			tfXOffset.setEnabled(false);
			tfZOffset.setEnabled(false);
			break;
		case 2:
			rbCustomOffset.setSelected(true);
			tfXOffset.setEnabled(true);
			tfZOffset.setEnabled(true);
			break;
		}
		tfXOffset.setText(""+prefs.getInt("OFFSET_X", 0));  //$NON-NLS-2$
		tfZOffset.setText(""+prefs.getInt("OFFSET_Z", 0));  //$NON-NLS-2$
		
		switch(prefs.getInt("OBJ_OVERWRITE", 0)) 
		{
		case 0:
			rbOBJAsk.setSelected(true);
			break;
		case 1:
			rbOBJAlways.setSelected(true);
			break;
		case 2:
			rbOBJNever.setSelected(true);
			break;
		}
		
		switch(prefs.getInt("MTL_OVERWRITE", 0)) 
		{
		case 0:
			rbMTLAsk.setSelected(true);
			break;
		case 1:
			rbMTLAlways.setSelected(true);
			break;
		case 2:
			rbMTLNever.setSelected(true);
			break;
		}
		
		cbRenderSides.setSelected(prefs.getBoolean("RENDER_SIDES", false)); 
		cbRenderBiomes.setSelected(prefs.getBoolean("RENDER_BIOMES", true)); 
		cbRenderEntities.setSelected(prefs.getBoolean("RENDER_ENTITIES", false)); 
		cbObjPerMat.setSelected(prefs.getBoolean("OBJ_PER_MTL", false)); 
		cbObjPerChunk.setSelected(prefs.getBoolean("OBJ_PER_CHUNK", false)); 
		cbRemoveDuplicates.setSelected(prefs.getBoolean("REMOVE_DUPLICATES", false)); 


		updateOptions();
	}

	/**
	 * Saves the options to the prefs store. Also updates the global Options.
	 */
	private void saveSettings()
	{
		updateOptions();


		prefs.putFloat("DEFAULT_SCALE", Options.scale); 

		prefs.putInt("OFFSET_X", Options.offsetX); 
		prefs.putInt("OFFSET_Z", Options.offsetZ); 
		switch(Options.offsetType)
		{
		case NONE:
			prefs.putInt("OFFSET_TYPE", 0); 
			break;
		case CENTER:
			prefs.putInt("OFFSET_TYPE", 1); 
			break;
		case CUSTOM:
			prefs.putInt("OFFSET_TYPE", 2); 
			break;
		}
		
		switch(Options.objOverwriteAction)
		{
		case ASK:
			prefs.putInt("OBJ_OVERWRITE", 0); 
			break;
		case ALWAYS:
			prefs.putInt("OBJ_OVERWRITE", 1); 
			break;
		case NEVER:
			prefs.putInt("OBJ_OVERWRITE", 2); 
			break;
		}
		
		switch(Options.mtlOverwriteAction)
		{
		case ASK:
			prefs.putInt("MTL_OVERWRITE", 0); 
			break;
		case ALWAYS:
			prefs.putInt("MTL_OVERWRITE", 1); 
			break;
		case NEVER:
			prefs.putInt("MTL_OVERWRITE", 2); 
			break;
		}
		
		prefs.putBoolean("RENDER_SIDES", Options.renderSides); 
		prefs.putBoolean("RENDER_BIOMES", Options.renderBiomes); 
		prefs.putBoolean("RENDER_ENTITIES", Options.renderEntities); 
		prefs.putBoolean("OBJ_PER_MTL", Options.objectPerMaterial); 
		prefs.putBoolean("OBJ_PER_CHUNK", Options.objectPerChunk); 
		prefs.putBoolean("REMOVE_DUPLICATES", Options.removeDuplicates); 
	}

	/**
	 * Updates the global Options values from the UI.
	 */
	private void updateOptions()
	{
		Options.scale = getScale();
		
		Options.offsetX = Integer.parseInt(tfXOffset.getText());
		Options.offsetZ = Integer.parseInt(tfZOffset.getText());

		if(rbCenterOffset.isSelected())
			Options.offsetType = OffsetType.CENTER;
		else if(rbCustomOffset.isSelected())
			Options.offsetType = OffsetType.CUSTOM;
		else
			Options.offsetType = OffsetType.NONE;
		
		if(rbOBJAlways.isSelected())
			Options.objOverwriteAction = OverwriteAction.ALWAYS;
		else if(rbOBJNever.isSelected())
			Options.objOverwriteAction = OverwriteAction.NEVER;
		else
			Options.objOverwriteAction = OverwriteAction.ASK;

		if(rbMTLAlways.isSelected())
			Options.mtlOverwriteAction = OverwriteAction.ALWAYS;
		else if(rbMTLNever.isSelected())
			Options.mtlOverwriteAction = OverwriteAction.NEVER;
		else
			Options.mtlOverwriteAction = OverwriteAction.ASK;
		
		Options.renderSides = cbRenderSides.isSelected();
		Options.renderBiomes = cbRenderBiomes.isSelected();
		Options.renderEntities = cbRenderEntities.isSelected();
		Options.objectPerMaterial = cbObjPerMat.isSelected();
		Options.objectPerChunk = cbObjPerChunk.isSelected();
		Options.removeDuplicates = cbRemoveDuplicates.isSelected();
	}
	
	private float getScale()
	{
		float ret=1;

		try
		{
			ret=Float.parseFloat(tfScale.getText());
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(OBJExportOptions.this, Messages.getString("OBJExportOptions.SCALE_NUM_ERR")); 
			return 1.0f;
		}

		return ret;
	}

}
