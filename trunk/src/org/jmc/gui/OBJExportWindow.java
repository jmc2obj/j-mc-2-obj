/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
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

import org.jmc.ObjExporter;
import org.jmc.Options;
import org.jmc.Options.OverwriteAction;
import org.jmc.ProgressCallback;
import org.jmc.StopCallback;
import org.jmc.util.Messages;


/**
 * A thread used and UI for saving an OBJ file.
 * When run it saves the file and shows a small window that shows the progress of
 * saving the file.
 * @author danijel
 *
 */
@SuppressWarnings("serial")
public class OBJExportWindow extends JFrame implements ProgressCallback
{

	private boolean stop;

	//UI elements (not described)
	OBJExportOptions options;
	TexsplitPanel texsplit;
	JProgressBar progress;
	JTextField tfSavePath;
	JButton bRun,bStop;
	JButton bOptions;
	JButton bTex;


	/**
	 * Main constructor.
	 * @param objfile path to OBJ file
	 * @param savepath path to world save
	 * @param dimension Dimension to load chunks from
	 * @param bounds region being saved
	 * @param ymin minimum altitude being saved
	 */
	public OBJExportWindow() 
	{		
		super(Messages.getString("OBJExportPanel.WIN_TITLE"));		

		setSize(400,200);
		setMinimumSize(new Dimension(400,200));

		JPanel main=new JPanel();
		add(main);

		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));

		JPanel pSavePath=new JPanel();
		pSavePath.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lSavePath=new JLabel(Messages.getString("OBJExportPanel.SAVEDIR"));
		pSavePath.setLayout(new BoxLayout(pSavePath, BoxLayout.LINE_AXIS));		
		tfSavePath = new JTextField();		
		JButton bObj=new JButton(Messages.getString("OBJExportPanel.BROWSE"));
		pSavePath.add(lSavePath);
		pSavePath.add(tfSavePath);
		pSavePath.add(bObj);
		main.add(pSavePath);

		tfSavePath.setText(MainWindow.settings.getLastExportPath());

		JPanel pOptions=new JPanel();
		pOptions.setLayout(new BoxLayout(pOptions, BoxLayout.LINE_AXIS));
		bOptions=new JButton(Messages.getString("OBJExportPanel.SHOW"));
		bOptions.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		pOptions.add(bOptions);
		main.add(pOptions);

		options=new OBJExportOptions();
		options.setVisible(false);
		main.add(options);
		
		JPanel pTex=new JPanel();
		pTex.setLayout(new BoxLayout(pTex, BoxLayout.LINE_AXIS));
		bTex=new JButton(Messages.getString("OBJExportPanel.SHOW_TEX"));
		bTex.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		pTex.add(bTex);
		main.add(pTex);
		
		texsplit=new TexsplitPanel(this);
		texsplit.setVisible(false);
		main.add(texsplit);
		
		main.add(Box.createVerticalGlue());

		JPanel pRun=new JPanel();
		pRun.setLayout(new BoxLayout(pRun, BoxLayout.LINE_AXIS));
		bRun=new JButton(Messages.getString("OBJExportPanel.EXPORT_BTN"));
		bRun.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		Font fRun=bRun.getFont();
		bRun.setFont(new Font(fRun.getFamily(),fRun.getStyle()+Font.BOLD,fRun.getSize()));
		bRun.setForeground(Color.red);
		bStop=new JButton(Messages.getString("OBJExportPanel.STOP_BTN"));
		bStop.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		bStop.setEnabled(false);
		pRun.add(bRun);
		pRun.add(bStop);
		main.add(pRun);

		progress=new JProgressBar();
		progress.setMaximum(100);
		progress.setStringPainted(true);
		main.add(progress);

		bObj.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser jfcSave=new JFileChooser();
				jfcSave.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfcSave.setDialogTitle(Messages.getString("OBJExportPanel.Save folder"));
				if(jfcSave.showSaveDialog(OBJExportWindow.this)!=JFileChooser.APPROVE_OPTION)
				{
					bRun.setEnabled(true);
					bStop.setEnabled(false);
					return;
				}

				File save_path=jfcSave.getSelectedFile();													
				tfSavePath.setText(save_path.getAbsolutePath());
				texsplit.setSaveDir(save_path);
			}
		});

		bRun.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				File savePath = new File(tfSavePath.getText()).getAbsoluteFile();
				File objfile=new File(savePath, Options.objFileName);
				File mtlfile=new File(savePath, Options.mtlFileName);

				final boolean write_obj;
				final boolean write_mtl;

				if(objfile.exists())
				{
					if(Options.objOverwriteAction==OverwriteAction.NEVER)
						write_obj=false;
					else if(Options.objOverwriteAction==OverwriteAction.ASK && 
							JOptionPane.showConfirmDialog(OBJExportWindow.this, 
									Messages.getString("OBJExportPanel.OBJ_ERR"), 
									Messages.getString("OBJExportPanel.WIN_TITLE"),
									JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)
						write_obj=false;
					else
						write_obj=true;
				}
				else {
					write_obj=true;
				}

				if(mtlfile.exists())
				{
					if(Options.mtlOverwriteAction==OverwriteAction.NEVER)
						write_mtl=false;
					else if(Options.mtlOverwriteAction==OverwriteAction.ASK &&
							JOptionPane.showConfirmDialog(OBJExportWindow.this, 
									Messages.getString("OBJExportPanel.MTL_ERR"), 
									Messages.getString("OBJExportPanel.WIN_TITLE"),
									JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)
						write_mtl=false;
					else
						write_mtl=true;
				}
				else {
					write_mtl=true;
				}
				
				Options.outputDir = savePath;
				MainWindow.settings.setLastExportPath(tfSavePath.getText());
				MainWindow.updateSelectionOptions();
								
				bRun.setEnabled(false);
				bStop.setEnabled(true);
				
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						stop=false;

						ObjExporter.export(OBJExportWindow.this,
							new StopCallback() {
								@Override
								public boolean stopRequested() {
									return stop;
								}
							}, 
							write_obj,
							write_mtl);
						
						bRun.setEnabled(true);
						bStop.setEnabled(false);
					}
				});
				t.start();
			}
		});

		bStop.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				stop=true;				
			}
		});

		bOptions.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!options.isVisible())
				{
					options.setVisible(true);
					bOptions.setText(Messages.getString("OBJExportPanel.HIDE_BTN"));		
					pack();
				}
				else
				{
					options.setVisible(false);
					bOptions.setText(Messages.getString("OBJExportPanel.SHOW_BTN"));
					pack();
				}
			}
		});

		bTex.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				if(!texsplit.isVisible())
				{
					texsplit.setVisible(true);
					bTex.setText(Messages.getString("OBJExportPanel.HIDE_BTN"));		
					pack();
				}
				else
				{
					texsplit.setVisible(false);
					bTex.setText(Messages.getString("OBJExportPanel.SHOW_TEX"));
					pack();
				}
			}
		});

		pack();
	}


	@Override
	public void setProgress(float value) {
		progress.setValue((int)(value*100f));		
	}

}

