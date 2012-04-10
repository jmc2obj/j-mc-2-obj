/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.jmc.OBJExportOptions.OffsetType;
import org.jmc.OBJExportOptions.OverwriteAction;

/**
 * A thread used and UI for saving an OBJ file.
 * When run it saves the file and shows a small window that shows the progress of
 * saving the file.
 * @author danijel
 *
 */
@SuppressWarnings("serial")
public class OBJExportPanel extends JFrame implements Runnable {

	/**
	 * Path to the world save.
	 */
	File savepath;
	/**
	 * Region of the map to be saved.
	 */
	Rectangle bounds;
	/**
	 * Altitude above which we are saving.
	 */
	int ymin,ymax;

	boolean running;

	//UI elements (not described)
	OBJExportOptions options;
	JProgressBar progress;
	JTextField tfSavePath;
	JButton bRun,bStop;
	JButton bOptions;
	JButton bTex;


	/**
	 * Main constructor.
	 * @param objfile path to OBJ file
	 * @param savepath path to world save
	 * @param bounds region being saved
	 * @param ymin minimum altitude being saved
	 */
	public OBJExportPanel(File savepath, Rectangle bounds, int ymin, int ymax) 
	{		
		super("Export selection");		

		this.savepath=savepath;
		this.bounds=bounds;
		this.ymin=ymin;
		this.ymax=ymax;

		setSize(400,200);
		setMinimumSize(new Dimension(400,0));

		JPanel main=new JPanel();
		add(main);

		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));

		JPanel pSavePath=new JPanel();
		pSavePath.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lSavePath=new JLabel("Save folder: ");
		pSavePath.setLayout(new BoxLayout(pSavePath, BoxLayout.LINE_AXIS));		
		tfSavePath = new JTextField();		
		JButton bObj=new JButton("Browse");
		pSavePath.add(lSavePath);
		pSavePath.add(tfSavePath);
		pSavePath.add(bObj);
		main.add(pSavePath);

		tfSavePath.setText(MainWindow.settings.getLastExportPath());

		JPanel pOptions=new JPanel();
		pOptions.setLayout(new BoxLayout(pOptions, BoxLayout.LINE_AXIS));
		bOptions=new JButton("Show more options...");
		bOptions.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		pOptions.add(bOptions);
		main.add(pOptions);

		options=new OBJExportOptions();
		options.setVisible(false);
		main.add(options);

		main.add(Box.createVerticalGlue());

		JPanel pRun=new JPanel();
		pRun.setLayout(new BoxLayout(pRun, BoxLayout.LINE_AXIS));
		bRun=new JButton("Export");
		bRun.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		Font fRun=bRun.getFont();
		bRun.setFont(new Font(fRun.getFamily(),fRun.getStyle()+Font.BOLD,fRun.getSize()));
		bRun.setForeground(Color.red);
		bStop=new JButton("Stop");
		bStop.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		bStop.setEnabled(false);
		pRun.add(bRun);
		pRun.add(bStop);
		main.add(pRun);
		
		JPanel pTex=new JPanel();
		pTex.setLayout(new BoxLayout(pTex, BoxLayout.LINE_AXIS));
		bTex=new JButton("Export Textures");
		bTex.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		pTex.add(bTex);
		main.add(pTex);

		progress=new JProgressBar();
		progress.setStringPainted(true);
		main.add(progress);

		bObj.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser jfcSave=new JFileChooser();
				jfcSave.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfcSave.setDialogTitle("Save folder");
				if(jfcSave.showSaveDialog(OBJExportPanel.this)!=JFileChooser.APPROVE_OPTION)
				{
					return;
				}

				File save_path=jfcSave.getSelectedFile();													
				tfSavePath.setText(save_path.getAbsolutePath());
			}
		});

		bRun.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bRun.setEnabled(false);
				bStop.setEnabled(true);
				(new Thread(OBJExportPanel.this)).start();

			}
		});

		bStop.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				running=false;				
			}
		});

		bOptions.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!options.isVisible())
				{
					options.setVisible(true);
					bOptions.setText("Hide options...");		
					pack();
				}
				else
				{
					options.setVisible(false);
					bOptions.setText("Show options...");
					pack();
				}
			}
		});
		
		bTex.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				TexsplitDialog tsdiag=new TexsplitDialog(tfSavePath.getText()+"/tex");
				Point p=getLocation();
				p.x+=(getWidth()-tsdiag.getWidth())/2;
				p.y+=(getHeight()-tsdiag.getHeight())/2;
				tsdiag.setLocation(p);
			}
		});

		pack();
		setVisible(true);
	}

	/**
	 * Main thread method.
	 */
	@Override
	public void run() {

		File exportpath=new File(tfSavePath.getText());

		File objfile=new File(exportpath.getAbsolutePath()+"/minecraft.obj");
		File mtlfile=new File(exportpath.getAbsolutePath()+"/minecraft.mtl");

		boolean write_obj=true;
		boolean write_mtl=true;

		if(objfile.exists())
		{
			if(options.getOBJOverwriteAction()==OverwriteAction.ALWAYS)
			{
				write_obj=true;
			}
			else if(options.getOBJOverwriteAction()==OverwriteAction.NEVER)
			{
				write_obj=false;
			}
			else if(JOptionPane.showConfirmDialog(OBJExportPanel.this, "OBJ file already exists. Do you want to overwrite?")!=JOptionPane.YES_OPTION)
			{												
				write_obj=false;
			}
		}

		if(mtlfile.exists())
		{
			if(options.getMTLOverwriteAction()==OverwriteAction.ALWAYS)
			{
				write_mtl=true;
			}
			else if(options.getMTLOverwriteAction()==OverwriteAction.NEVER)
			{
				write_mtl=false;
			}
			else if(JOptionPane.showConfirmDialog(OBJExportPanel.this, "MTL file already exists. Do you want to overwrite?")!=JOptionPane.YES_OPTION)
			{												
				write_mtl=false;
			}
		}

		try {
			objfile.createNewFile();
			mtlfile.createNewFile();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(this, "Cannot write to the chosen location!");
			bRun.setEnabled(true);
			bStop.setEnabled(false);
			return;
		}

		OffsetType offset_type=options.getOffsetType();
		Point offset_point=options.getCustomOffset();
		MainWindow.settings.setLastExportPath(tfSavePath.getText());

		running=true;


		float scale=options.getScale();
		boolean obj_per_mat=options.getObjPerMat();

		options.saveSettings();

		try
		{
			if(write_mtl)
			{
				Materials.copyMTLFile(mtlfile);
				MainWindow.log("Saved materials to "+mtlfile.getAbsolutePath());
			}

			PrintWriter obj_writer=new PrintWriter(new FileWriter(objfile));

			if(write_obj)
			{
				int cxs=(int)Math.floor(bounds.x/16.0f);
				int czs=(int)Math.floor(bounds.y/16.0f);
				int cxe=(int)Math.ceil((bounds.x+bounds.width)/16.0f);
				int cze=(int)Math.ceil((bounds.y+bounds.height)/16.0f);
				int oxs=0,ozs=0;

				if(offset_type==OffsetType.NO_OFFSET)
				{
					oxs=0;
					ozs=0;
				}
				else if(offset_type==OffsetType.CENTER_OFFSET)
				{
					oxs=cxs+(cxe-cxs)/2;
					ozs=czs+(cze-czs)/2;
				}
				else if(offset_type==OffsetType.CUSTOM_OFFSET)
				{
					oxs=offset_point.x;
					ozs=offset_point.y;
				}


				int progress_count=0;
				int progress_max=(cxe-cxs)*(cze-czs);

				progress.setMaximum(progress_max);

				ChunkDataBuffer chunk_buffer=new ChunkDataBuffer(bounds, ymin, ymax);

				OBJFile obj=new OBJFile("minecraft");
				obj.setOffset(-oxs*16, -ymin, -ozs*16);
				obj.setScale(scale);

				obj.appendMtl(obj_writer, "minecraft.mtl");
				if(!obj_per_mat) obj.appendObjectname(obj_writer);
				obj.printTexturesAndNormals(obj_writer);


				MainWindow.log("Processing chunks...");

				for(int cx=cxs; cx<=cxe && running; cx++)
				{
					for(int cz=czs; cz<=cze && running; cz++,progress_count++)
					{
						progress.setValue(progress_count);					

						for(int lx=cx-1; lx<=cx+1; lx++)
							for(int lz=cz-1; lz<=cz+1; lz++)
							{
								if(lx<cxs || lx>cxe || lz<czs || lz>cze) continue;

								if(chunk_buffer.hasChunk(lx, lz)) continue;

								Chunk chunk=null;
								try{
									Region region=Region.findRegion(savepath, lx, lz);							
									if(region==null) continue;					
									chunk=region.getChunk(lx, lz);
									if(chunk==null) continue;
								}catch (Exception e) {
									continue;
								}

								chunk_buffer.addChunk(chunk);		
								obj.appendVertices(obj_writer);
								obj.appendFaces(obj_writer,obj_per_mat);
								obj.clearData();
							}

						obj.addChunkBuffer(chunk_buffer,cx,cz);

						for(int lx=cx-1; lx<=cx+1; lx++)
							chunk_buffer.removeChunk(lx,cz-1);
					}

					chunk_buffer.removeAllChunks();
				}

				obj_writer.close();

				MainWindow.log("Saved model to "+objfile.getAbsolutePath());
			}
			
			MainWindow.log("Done!");

		}
		catch (Exception e) {
			Utility.logError("Error while exporting OBJ:", e);
		}

		bRun.setEnabled(true);
		bStop.setEnabled(false);
	}

}

@SuppressWarnings("serial")
class OBJExportOptions extends JPanel
{
	private Preferences prefs;
	private JTextField tfScale;
	private JRadioButton rbNoOffset,rbCenterOffset,rbCustomOffset;
	private JTextField tfXOffset,tfZOffset;
	private JCheckBox cbObjPerMat;
	private JRadioButton rbOBJAlways, rbOBJNever, rbOBJAsk;
	private JRadioButton rbMTLAlways, rbMTLNever, rbMTLAsk;

	public OBJExportOptions() {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		prefs=MainWindow.settings.getPreferences();

		JPanel pScale=new JPanel();		
		pScale.setLayout(new BoxLayout(pScale,BoxLayout.LINE_AXIS));
		pScale.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lScale=new JLabel("Map Scale: ");
		tfScale=new JTextField(""+prefs.getFloat("DEFAULT_SCALE", 1.0f));
		pScale.add(lScale);
		pScale.add(tfScale);

		JPanel pOffset=new JPanel();
		pOffset.setLayout(new BoxLayout(pOffset,BoxLayout.LINE_AXIS));
		pOffset.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lOffset=new JLabel("Offset: ");
		rbNoOffset=new JRadioButton("None");
		rbCenterOffset=new JRadioButton("Center");
		rbCustomOffset=new JRadioButton("Custom");
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

		JPanel pObjPerMat=new JPanel();
		pObjPerMat.setLayout(new BoxLayout(pObjPerMat, BoxLayout.LINE_AXIS));
		pObjPerMat.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbObjPerMat=new JCheckBox("Create a separate object for each material");
		pObjPerMat.add(cbObjPerMat);
		
		JPanel pOBJOver = new JPanel();
		pOBJOver.setLayout(new BoxLayout(pOBJOver, BoxLayout.LINE_AXIS));
		pOBJOver.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lOBJOver=new JLabel("Overwrite OBJ: ");
		rbOBJAsk=new JRadioButton("Ask");
		rbOBJAlways=new JRadioButton("Always");
		rbOBJNever=new JRadioButton("Never");		
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
		JLabel lMTLOver=new JLabel("Overwrite MTL: ");
		rbMTLAsk=new JRadioButton("Ask");
		rbMTLAlways=new JRadioButton("Always");
		rbMTLNever=new JRadioButton("Never");		
		pMTLOver.add(lMTLOver);
		pMTLOver.add(rbMTLAsk);
		pMTLOver.add(rbMTLAlways);
		pMTLOver.add(rbMTLNever);
		
		ButtonGroup gMTLOver=new ButtonGroup();
		gMTLOver.add(rbMTLAsk);
		gMTLOver.add(rbMTLAlways);
		gMTLOver.add(rbMTLNever);
		rbMTLAsk.setActionCommand("ask");
		rbMTLAlways.setActionCommand("always");
		rbMTLNever.setActionCommand("never");

		AbstractAction rbOffsetAction=new AbstractAction() {			
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

				int x=Integer.parseInt(tfXOffset.getText());
				int z=Integer.parseInt(tfZOffset.getText());

				prefs.putInt("OFFSET_X", x);
				prefs.putInt("OFFSET_Z", z);

				if(ev.getActionCommand().equals("none"))
					prefs.putInt("OFFSET_TYPE", 0);
				else if(ev.getActionCommand().equals("center"))
					prefs.putInt("OFFSET_TYPE", 1);
				else if(ev.getActionCommand().equals("custom"))
					prefs.putInt("OFFSET_TYPE", 2);
			}
		};

		rbNoOffset.addActionListener(rbOffsetAction);
		rbCenterOffset.addActionListener(rbOffsetAction);
		rbCustomOffset.addActionListener(rbOffsetAction);
		
		AbstractAction SaveAction=new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveSettings();
			}	
		};
		
		rbOBJAsk.addActionListener(SaveAction);
		rbOBJAlways.addActionListener(SaveAction);
		rbOBJNever.addActionListener(SaveAction);
		rbMTLAsk.addActionListener(SaveAction);
		rbMTLAlways.addActionListener(SaveAction);
		rbMTLNever.addActionListener(SaveAction);
		cbObjPerMat.addActionListener(SaveAction);

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
		
		cbObjPerMat.setSelected(prefs.getBoolean("OBJ_PER_MTL", false));
		
		add(pScale);
		add(pOffset);
		add(pObjPerMat);
		add(pOBJOver);
		add(pMTLOver);
	}

	enum OffsetType{ NO_OFFSET, CENTER_OFFSET, CUSTOM_OFFSET };

	public OffsetType getOffsetType()
	{
		if(rbNoOffset.isSelected())
			return OffsetType.NO_OFFSET;

		if(rbCenterOffset.isSelected())
			return OffsetType.CENTER_OFFSET;

		if(rbCustomOffset.isSelected())
			return OffsetType.CUSTOM_OFFSET;

		return OffsetType.NO_OFFSET;
	}
	
	enum OverwriteAction { ASK, ALWAYS, NEVER };
	
	public OverwriteAction getOBJOverwriteAction()
	{
		if(rbOBJAsk.isSelected())
			return OverwriteAction.ASK;
		
		if(rbOBJAlways.isSelected())
			return OverwriteAction.ALWAYS;
		
		if(rbOBJNever.isSelected())
			return OverwriteAction.NEVER;
		
		return OverwriteAction.ASK;
	}
	
	public OverwriteAction getMTLOverwriteAction()
	{
		if(rbMTLAsk.isSelected())
			return OverwriteAction.ASK;
		
		if(rbMTLAlways.isSelected())
			return OverwriteAction.ALWAYS;
		
		if(rbMTLNever.isSelected())
			return OverwriteAction.NEVER;
		
		return OverwriteAction.ASK;
	}

	public void saveSettings()
	{
		prefs.putFloat("DEFAULT_SCALE", getScale());
		
		switch(getOBJOverwriteAction())
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
		
		switch(getMTLOverwriteAction())
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
		
		prefs.putBoolean("OBJ_PER_MTL", cbObjPerMat.isSelected());
	}

	public Point getCustomOffset()
	{
		int x=Integer.parseInt(tfXOffset.getText());
		int z=Integer.parseInt(tfZOffset.getText());
		return new Point(x,z);
	}

	public float getScale()
	{
		float ret=1;

		try
		{
			ret=Float.parseFloat(tfScale.getText());
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(OBJExportOptions.this, "Cannot parse the scale value! Assuming 1!");
			return 1.0f;
		}

		return ret;
	}

	public boolean getObjPerMat()
	{
		return cbObjPerMat.isSelected();
	}
}
