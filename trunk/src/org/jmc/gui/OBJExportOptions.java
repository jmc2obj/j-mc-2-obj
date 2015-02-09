package org.jmc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jmc.Options;
import org.jmc.Options.OffsetType;
import org.jmc.Options.OverwriteAction;
import org.jmc.util.Log;
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
	private JCheckBox cbRenderUnknown;
	private JCheckBox cbObjPerMat;
	private JCheckBox cbObjPerChunk;
	private JCheckBox cbObjPerBlock;
	private JCheckBox cbSingleBlock;
	private JTextField tfSingleBlockID;
	private JCheckBox cbSingleMaterial;
	private JCheckBox cbRemoveDuplicates;
	private JCheckBox cbUseUV;
	private JTextField tfUVFile;
	private JRadioButton rbOBJAlways, rbOBJNever, rbOBJAsk;
	private JRadioButton rbMTLAlways, rbMTLNever, rbMTLAsk;

	public OBJExportOptions() {
		
		ToolTipManager.sharedInstance().setInitialDelay(100);
		
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
		JLabel lOffset=new JLabel(Messages.getString("OBJExportOptions.OFFSET")); 
		rbNoOffset=new JRadioButton(Messages.getString("OBJExportOptions.NONE")); 
		rbCenterOffset=new JRadioButton(Messages.getString("OBJExportOptions.CENTER")); 
		rbCustomOffset=new JRadioButton(Messages.getString("OBJExportOptions.CUSTOM")); 		
		pOffset.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		tfXOffset=new JTextField();
		tfZOffset=new JTextField();
		JLabel lUnit=new JLabel(Messages.getString("OBJExportOptions.BLOCKS"));
		pOffset.add(lOffset);
		pOffset.add(rbNoOffset);
		pOffset.add(rbCenterOffset);
		pOffset.add(rbCustomOffset);
		pOffset.add(tfXOffset);
		pOffset.add(tfZOffset);
		pOffset.add(lUnit);

		ButtonGroup gOffset=new ButtonGroup();
		gOffset.add(rbNoOffset);
		gOffset.add(rbCenterOffset);
		gOffset.add(rbCustomOffset);
		rbNoOffset.setActionCommand(Messages.getString("OBJExportOptions.NONE"));
		rbCenterOffset.setActionCommand(Messages.getString("OBJExportOptions.CENTER"));
		rbCustomOffset.setActionCommand(Messages.getString("OBJExportOptions.CUSTOM"));

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

		JPanel pUnknown=new JPanel();
		pUnknown.setLayout(new BoxLayout(pUnknown, BoxLayout.LINE_AXIS));
		pUnknown.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbRenderUnknown=new JCheckBox(Messages.getString("OBJExportOptions.R_UNKNOWN"));
		pUnknown.add(cbRenderUnknown);
		
		JPanel pObjPerMat=new JPanel();
		pObjPerMat.setLayout(new BoxLayout(pObjPerMat, BoxLayout.LINE_AXIS));
		pObjPerMat.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbObjPerMat=new JCheckBox(Messages.getString("OBJExportOptions.SEP_OBJ_MTL"));
		pObjPerMat.add(cbObjPerMat);

		JPanel pObjPerChunk=new JPanel();
		pObjPerChunk.setLayout(new BoxLayout(pObjPerChunk, BoxLayout.LINE_AXIS));
		pObjPerChunk.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbObjPerChunk=new JCheckBox(Messages.getString("OBJExportOptions.SEP_OBJ_CHUNK"));
		pObjPerChunk.add(cbObjPerChunk);

		JPanel pObjPerBlock=new JPanel();
		pObjPerBlock.setLayout(new BoxLayout(pObjPerBlock, BoxLayout.LINE_AXIS));
		pObjPerBlock.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbObjPerBlock=new JCheckBox(Messages.getString("OBJExportOptions.SEP_OBJ_BLOCK"));
		JLabel lObjPerBlockWarn=new JLabel("[!]");		
		lObjPerBlockWarn.setToolTipText(Messages.getString("OBJExportOptions.SEP_OBJ_BLOCK_WARNING"));
		lObjPerBlockWarn.setForeground(Color.red);
		pObjPerBlock.add(cbObjPerBlock);
		pObjPerBlock.add(lObjPerBlockWarn);
		
		JPanel pSingleBlock=new JPanel();
		pSingleBlock.setLayout(new BoxLayout(pSingleBlock, BoxLayout.LINE_AXIS));
		pSingleBlock.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbSingleBlock=new JCheckBox(Messages.getString("OBJExportOptions.SINGLEBLOCK"));
		tfSingleBlockID=new JTextField("0");
//		I've tried every way I know to change the width of the JTextField, but nothing is working, so this is just for looks
		JLabel blankLabel = new JLabel("                                                                     ");
		pSingleBlock.add(cbSingleBlock);
		pSingleBlock.add(tfSingleBlockID);
		pSingleBlock.add(blankLabel);
		
		JPanel pSingleMaterial=new JPanel();
		pSingleMaterial.setLayout(new BoxLayout(pSingleMaterial, BoxLayout.LINE_AXIS));
		pSingleMaterial.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbSingleMaterial=new JCheckBox(Messages.getString("OBJExportOptions.SINGLE_MTL"));
		pSingleMaterial.add(cbSingleMaterial);

		JPanel pRemoveDuplicates=new JPanel();
		pRemoveDuplicates.setLayout(new BoxLayout(pRemoveDuplicates, BoxLayout.LINE_AXIS));
		pRemoveDuplicates.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbRemoveDuplicates=new JCheckBox(Messages.getString("OBJExportOptions.DUPL_VERT"));
		pRemoveDuplicates.add(cbRemoveDuplicates);

		JPanel pUseUV=new JPanel();
		pUseUV.setLayout(new BoxLayout(pUseUV, BoxLayout.LINE_AXIS));
		pUseUV.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbUseUV=new JCheckBox(Messages.getString("OBJExportOptions.SINGLE_TEX"));
		tfUVFile=new JTextField();
		JButton bUVFile=new JButton(Messages.getString("OBJExportPanel.BROWSE"));
		pUseUV.add(cbUseUV);
		pUseUV.add(tfUVFile);
		pUseUV.add(bUVFile);

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

		bUVFile.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser jfcFile=new JFileChooser();
				jfcFile.setDialogTitle("UV File");
				if(jfcFile.showSaveDialog(OBJExportOptions.this)!=JFileChooser.APPROVE_OPTION)
				{
					return;
				}

				File save_path=jfcFile.getSelectedFile();													
				tfUVFile.setText(save_path.getAbsolutePath());
			}
		});

		ButtonGroup gMTLOver=new ButtonGroup();
		gMTLOver.add(rbMTLAsk);
		gMTLOver.add(rbMTLAlways);
		gMTLOver.add(rbMTLNever);
		rbMTLAsk.setActionCommand("ask");
		rbMTLAlways.setActionCommand("always");
		rbMTLNever.setActionCommand("never");

		loadSettings();

		DocumentListener tf_listener=new DocumentListener() {			
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

		tfScale.getDocument().addDocumentListener(tf_listener);
		tfXOffset.getDocument().addDocumentListener(tf_listener);
		tfZOffset.getDocument().addDocumentListener(tf_listener);
		tfUVFile.getDocument().addDocumentListener(tf_listener);
		tfSingleBlockID.getDocument().addDocumentListener(tf_listener);

		AbstractAction offsetSaveAction=new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent ev) {
				if(ev.getSource() == rbCustomOffset)
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
		
		AbstractAction singelBlockAction=new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent ev) {
				if(cbSingleBlock.isSelected()){
					tfSingleBlockID.setEnabled(true);
				}else{
					tfSingleBlockID.setEnabled(false);
				}
				saveSettings();
			}
		};		

		rbNoOffset.addActionListener(offsetSaveAction);
		rbCenterOffset.addActionListener(offsetSaveAction);
		rbCustomOffset.addActionListener(offsetSaveAction);
		cbSingleBlock.addActionListener(singelBlockAction);

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
		cbRenderUnknown.addActionListener(genericSaveAction);
		cbObjPerMat.addActionListener(genericSaveAction);
		cbObjPerChunk.addActionListener(genericSaveAction);
		cbObjPerBlock.addActionListener(genericSaveAction);
		cbSingleBlock.addActionListener(genericSaveAction);
		cbRemoveDuplicates.addActionListener(genericSaveAction);
		cbUseUV.addActionListener(genericSaveAction);
		cbSingleMaterial.addActionListener(genericSaveAction);
		
		add(pScale);
		add(pOffset);
		add(pSides);
		add(pBiomes);
		add(pEntities);
		add(pUnknown);
		add(pObjPerMat);
		add(pObjPerChunk);
		add(pObjPerBlock);
		add(pSingleBlock);
		add(pSingleMaterial);
		add(pRemoveDuplicates);
		add(pUseUV);
		add(pOBJOver);
		add(pMTLOver);
		add(pNames);
	}


	/**
	 * Loads the options from the prefs store. Updates the UI controls and the global Options.
	 */
	private void loadSettings()
	{
		tfScale.setText(""+prefs.getFloat("DEFAULT_SCALE", 1.0f));

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
		tfXOffset.setText(""+prefs.getInt("OFFSET_X", 0));
		tfZOffset.setText(""+prefs.getInt("OFFSET_Z", 0));

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
		
		if(cbSingleBlock.isSelected()){
			tfSingleBlockID.setEnabled(true);
		}else{
			tfSingleBlockID.setEnabled(false);
		}

		cbRenderSides.setSelected(prefs.getBoolean("RENDER_SIDES", false));
		cbRenderBiomes.setSelected(prefs.getBoolean("RENDER_BIOMES", true));
		cbRenderEntities.setSelected(prefs.getBoolean("RENDER_ENTITIES", false));
		cbRenderUnknown.setSelected(prefs.getBoolean("RENDER_UNKNOWN", false));
		cbObjPerMat.setSelected(prefs.getBoolean("OBJ_PER_MTL", false));
		cbObjPerChunk.setSelected(prefs.getBoolean("OBJ_PER_CHUNK", false));
		cbObjPerBlock.setSelected(prefs.getBoolean("OBJ_PER_BLOCK", false));
		cbSingleBlock.setSelected(prefs.getBoolean("SINGLE_BLOCK", false));
		cbSingleMaterial.setSelected(prefs.getBoolean("SINGLE_MTL", false));
		cbRemoveDuplicates.setSelected(prefs.getBoolean("REMOVE_DUPLICATES", false));
		cbUseUV.setSelected(prefs.getBoolean("USE_UV_FILE", false));
		tfUVFile.setText(prefs.get("UV_FILE", ""));

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
		prefs.putBoolean("RENDER_UNKNOWN", Options.renderUnknown);
		prefs.putBoolean("OBJ_PER_MTL", Options.objectPerMaterial);
		prefs.putBoolean("OBJ_PER_CHUNK", Options.objectPerChunk);
		prefs.putBoolean("OBJ_PER_BLOCK", Options.objectPerBlock);
		prefs.putBoolean("SINGLE_BLOCK", Options.singleBlock);
		prefs.putBoolean("SINGLE_MTL", Options.singleMaterial);
		prefs.putBoolean("REMOVE_DUPLICATES", Options.removeDuplicates);
		prefs.putBoolean("USE_UV_FILE", Options.useUVFile);
		prefs.put("UV_FILE", Options.UVFile.getAbsolutePath());
	}

	/**
	 * Updates the global Options values from the UI.
	 */
	private void updateOptions()
	{
		Options.scale = getScale();

		try{
			String txt=tfXOffset.getText();
			if(!txt.isEmpty() && !txt.equals("-"))
				Options.offsetX = Integer.parseInt(txt);
			txt=tfZOffset.getText();
			if(!txt.isEmpty() && !txt.equals("-"))
				Options.offsetZ = Integer.parseInt(txt);

		}catch (NumberFormatException e) {
			Log.error("Offset number format error!", e, false);
		}

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
		
		try{
			String txt=tfSingleBlockID.getText();
			if(!txt.isEmpty() && !txt.equals("-"))
				Options.blockid = Integer.parseInt(txt);
		}catch (NumberFormatException e) {
			Log.error("ID number format error!", e, false);
		}

		Options.renderSides = cbRenderSides.isSelected();
		Options.renderBiomes = cbRenderBiomes.isSelected();
		Options.renderEntities = cbRenderEntities.isSelected();
		Options.renderUnknown = cbRenderUnknown.isSelected();
		Options.objectPerMaterial = cbObjPerMat.isSelected();
		Options.objectPerChunk = cbObjPerChunk.isSelected();
		Options.objectPerBlock = cbObjPerBlock.isSelected();
		Options.singleBlock = cbSingleBlock.isSelected();
		Options.singleMaterial = cbSingleMaterial.isSelected();
		Options.removeDuplicates = cbRemoveDuplicates.isSelected();
		Options.useUVFile=cbUseUV.isSelected();
		Options.UVFile=new File(tfUVFile.getText());
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
