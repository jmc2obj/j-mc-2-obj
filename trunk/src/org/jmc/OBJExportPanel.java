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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

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
		
		File cwd=new File(".");
		try{
		tfSavePath.setText(cwd.getCanonicalPath());
		}catch(Exception e){}
		
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
			mtlfile.createNewFile();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(this, "Cannot write to the chosen location!");
			return;
		}

		running=true;
		
		
		float scale=options.getScale();
		
		try {
			FileWriter file=new FileWriter(objfile);
			PrintWriter writer=new PrintWriter(file);

			MTLFile mtl=new MTLFile(mtlfile);
			
			mtl.saveMTLFile();
			
			MainWindow.log("Saved materials to "+mtlfile.getAbsolutePath());

			mtl.header(writer,objfile);

			int cxs=(int)Math.floor(bounds.x/16.0f);
			int czs=(int)Math.floor(bounds.y/16.0f);
			int cxe=(int)Math.ceil((bounds.x+bounds.width)/16.0f);
			int cze=(int)Math.ceil((bounds.y+bounds.height)/16.0f);
			int oxs=(cxe-cxs)/-2;
			int ozs=(cze-czs)/-2;

			progress.setMaximum((cxe-cxs)*(cze-czs));

			int progress_count=0;
			for(int cx=cxs,ox=oxs; cx<=cxe && running; cx++,ox++)
				for(int cz=czs,oz=ozs; cz<=cze && running; cz++,oz++,progress_count++)
				{
					progress.setValue(progress_count);

					Chunk chunk=null;
					try{
						Region region=Region.findRegion(savepath, cx, cz);							
						if(region==null) continue;					
						chunk=region.getChunk(cx, cz);
						if(chunk==null) continue;
					}catch (Exception e) {
						continue;
					}

					OBJFile obj=chunk.getOBJ(mtl,bounds,ymin, ymax);

					if(obj==null) continue;

					obj.setOffset(ox*16, -ymin, oz*16);
					obj.setScale(scale);

					obj.append(writer);
				}

			writer.close();
			
			MainWindow.log("Saved model to "+objfile.getAbsolutePath());

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
		
		add(pScale);
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
