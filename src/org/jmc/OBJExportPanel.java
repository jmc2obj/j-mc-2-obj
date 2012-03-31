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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
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
		File tmpfile=new File(exportpath.getAbsolutePath()+"/minecraft_faces.tmp");
		File mtlfile=new File(exportpath.getAbsolutePath()+"/minecraft.mtl");

		if(objfile.exists())
		{
			if(JOptionPane.showConfirmDialog(OBJExportPanel.this, "OBJ file already exists. Do you want to overwrite?")!=JOptionPane.YES_OPTION)
			{												
				return;
			}
		}

		if(mtlfile.exists())
		{
			if(JOptionPane.showConfirmDialog(OBJExportPanel.this, "MTL file already exists. Do you want to overwrite?")!=JOptionPane.YES_OPTION)
			{												
				return;
			}
		}

		try {
			objfile.createNewFile();
			tmpfile.createNewFile();
			mtlfile.createNewFile();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(this, "Cannot write to the chosen location!");
			return;
		}

		OffsetType offset_type=options.getOffsetType();
		Point offset_point=options.getCustomOffset();
		switch(offset_type)
		{
		case NO_OFFSET:
			MainWindow.settings.setOffset(0,offset_point.x,offset_point.y);
			break;
		case CENTER_OFFSET:
			MainWindow.settings.setOffset(1,offset_point.x,offset_point.y);
			break;
		case CUSTOM_OFFSET:
			MainWindow.settings.setOffset(2,offset_point.x,offset_point.y);
			break;
		}
		MainWindow.settings.setLastExportPath(tfSavePath.getText());

		running=true;


		float scale=options.getScale();

		try {
			PrintWriter vertex_writer=new PrintWriter(new FileWriter(objfile));
			PrintWriter faces_writer=new PrintWriter(new FileWriter(tmpfile));

			MTLFile mtl=new MTLFile(mtlfile);

			mtl.saveMTLFile();

			MainWindow.log("Saved materials to "+mtlfile.getAbsolutePath());

			mtl.header(vertex_writer,objfile);

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
			
			OBJFile obj=new OBJFile("minecraft", mtl);
			obj.setOffset(-oxs*16, -ymin, -ozs*16);
			obj.setScale(scale);
			
			obj.appendObjectname(vertex_writer);
			
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
							obj.appendVertices(vertex_writer);
							obj.appendFaces(faces_writer);
							obj.clearData();
							
						}
					
					obj.addChunkBuffer(chunk_buffer,cx,cz);
					
					for(int lx=cx-1; lx<=cx+1; lx++)
						chunk_buffer.removeChunk(lx,cz-1);

				}
				
				chunk_buffer.removeAllChunks();
			}
			
			
			obj.printTexturesAndNormals(vertex_writer);
			
			faces_writer.close();
			
			MainWindow.log("Merging temp into final...");
			
			BufferedReader tmpreader=new BufferedReader(new InputStreamReader(new FileInputStream(tmpfile)));
			
			String line;
			while((line=tmpreader.readLine())!=null)
			{
				vertex_writer.println(line);
			}
			
			tmpreader.close();
			vertex_writer.close();
			
			tmpfile.delete();
			
			MainWindow.log("Saved model to "+objfile.getAbsolutePath());
			MainWindow.log("Done!");

		} catch (Exception e) {
			e.printStackTrace();
		}

		bRun.setEnabled(true);
		bStop.setEnabled(false);

	}

}

@SuppressWarnings("serial")
class OBJExportOptions extends JPanel
{

	private JTextField tfScale;
	private JRadioButton rbNoOffset,rbCenterOffset,rbCustomOffset;
	private JTextField tfXOffset,tfZOffset;

	public OBJExportOptions() {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		JPanel pScale=new JPanel();		
		pScale.setLayout(new BoxLayout(pScale,BoxLayout.LINE_AXIS));
		pScale.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lScale=new JLabel("Map Scale: ");
		tfScale=new JTextField(""+MainWindow.settings.getDefaultScale());
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

				if(ev.getActionCommand().equals("none"))
					MainWindow.settings.setOffset(0,x,z);
				else if(ev.getActionCommand().equals("center"))
					MainWindow.settings.setOffset(1,x,z);
				else if(ev.getActionCommand().equals("custom"))
					MainWindow.settings.setOffset(2,x,z);
			}
		};

		rbNoOffset.addActionListener(rbOffsetAction);
		rbCenterOffset.addActionListener(rbOffsetAction);
		rbCustomOffset.addActionListener(rbOffsetAction);

		switch(MainWindow.settings.getOffsetType())
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
		Point p=MainWindow.settings.getOffset();
		tfXOffset.setText(""+p.x);
		tfZOffset.setText(""+p.y);

		add(pScale);
		add(pOffset);
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
}
