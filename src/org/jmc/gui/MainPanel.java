/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.BadLocationException;

import org.jmc.ChunkLoaderThread;
import org.jmc.LevelDat;
import org.jmc.Options;
import org.jmc.NBT.TAG_Double;
import org.jmc.NBT.TAG_List;
import org.jmc.util.Filesystem;


/**
 * Main Panel containing all the content of the window.
 * 
 * @author max, danijel
 *
 */
@SuppressWarnings("serial")
public class MainPanel extends JPanel
{
	//UI elements (not described separately)
	private JButton bLoad,bExport,bSettings,bAbout;
	//this suppression is for compatibility with java 1.6
	private JComboBox<String> cbPath;
	private JComboBox<Integer> cbDimension;
	private JTextArea taLog;
	private JScrollPane spPane;
	private JSlider sFloor,sCeil;
	
	/**
	 * Main map preview panel. 
	 */
	private PreviewPanel preview;

	/**
	 * Panel containing the memory state information.
	 * Also a thread constantly making memory measurements.
	 */
	private MemoryMonitor memory_monitor;
	
	/**
	 * Thread object used for monitoring the state of the chunk loading thread.
	 * Necessary for restarting the thread when loading a new map.
	 */
	private ChunkLoaderThread chunk_loader=null;

	private boolean slider_pressed=false;

	/**
	 * Panel constructor.
	 */	
	public MainPanel()
	{
		setLayout(new BorderLayout());
		
		
		JPanel pToolbar = new JPanel();
		pToolbar.setLayout(new BoxLayout(pToolbar, BoxLayout.PAGE_AXIS));
		
		JPanel pPath=new JPanel();
		pPath.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));
		pPath.setLayout(new BoxLayout(pPath, BoxLayout.LINE_AXIS));
		cbPath = new JComboBox<String>();
		//cbPath.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
		cbPath.setEditable(true);
		cbDimension = new JComboBox<Integer>();
		JButton bPath=new JButton("...");
		pPath.add(bPath);
		pPath.add(cbPath);
		pPath.add(cbDimension);
		
		JScrollPane spPath=new JScrollPane(pPath);
		spPath.setBorder(BorderFactory.createEmptyBorder());
		
		JPanel pButtons = new JPanel();
		pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.LINE_AXIS));
		pButtons.setBorder(BorderFactory.createEmptyBorder(0,5,10,5));
		
		bLoad = new JButton("Load");
		bExport = new JButton("Export selection");
		bSettings = new JButton("Settings");
		bAbout = new JButton("About");
		bAbout.setForeground(Color.red);
		Font f=bAbout.getFont();
		bAbout.setFont(new Font(f.getFamily(),Font.BOLD,f.getSize()));
				
		pButtons.add(bLoad);
		pButtons.add(bExport);
		pButtons.add(bSettings);
		pButtons.add(bAbout);						

		pToolbar.add(spPath);
		pToolbar.add(pButtons);
		
		preview = new PreviewPanel();
		preview.setBackground(new Color(110,150,100));
		
		JPanel preview_alts=new JPanel();
		preview_alts.setLayout(new BorderLayout());
		JPanel alts=new JPanel();
		alts.setLayout(new BoxLayout(alts, BoxLayout.PAGE_AXIS));
		sFloor=new JSlider();
		sFloor.setOrientation(JSlider.VERTICAL);
		sFloor.setToolTipText("Export floor");
		sFloor.setMinimum(0);
		sFloor.setMaximum(256);//TODO: this should really be read from the file, IMO
		sFloor.setValue(0);
		sCeil=new JSlider();
		sCeil.setOrientation(JSlider.VERTICAL);
		sCeil.setToolTipText("Export ceiling");
		sCeil.setMinimum(0);
		sCeil.setMaximum(256);
		sCeil.setValue(256);
			
		taLog = new JTextArea(5,1);
		taLog.setLineWrap(true);
		taLog.setEditable(false);
		taLog.setFont(new Font("Courier", 0, 14));
				
		spPane = new JScrollPane(taLog);
		memory_monitor=new MemoryMonitor();


		populateLoadList();		
		
		JSplitPane spMainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, preview_alts, spPane);
		spMainSplit.setDividerLocation(400);
		spMainSplit.setResizeWeight(1);
		
		
		
		alts.add(sCeil);
		alts.add(sFloor);
		
		preview_alts.add(preview);
		preview_alts.add(alts,BorderLayout.EAST);
		
		add(pToolbar, BorderLayout.NORTH);		
		add(spMainSplit);		
		add(memory_monitor, BorderLayout.SOUTH);	
		
		
		ChangeListener slider_listener=new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(e.getSource().equals(sCeil))
				{
					if(sFloor.getValue()>=sCeil.getValue())
						sFloor.setValue(sCeil.getValue()-1);
				}
				else
				{
					if(sCeil.getValue()<=sFloor.getValue())
						sCeil.setValue(sFloor.getValue()+1);
				}
				if(!slider_pressed) 
					chunk_loader.setYBounds(sFloor.getValue(), sCeil.getValue());
				preview.setAltitudes(sFloor.getValue(), sCeil.getValue());
				preview.repaint();
			}
		};
		MouseInputAdapter slider_adapter=new MouseInputAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				slider_pressed=true;
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				slider_pressed=false;
				chunk_loader.setYBounds(sFloor.getValue(), sCeil.getValue());
			}
		};
		
		sCeil.addChangeListener(slider_listener);
		sCeil.addMouseListener(slider_adapter);
		sFloor.addChangeListener(slider_listener);
		sFloor.addMouseListener(slider_adapter);
		preview.setAltitudes(sFloor.getValue(), sCeil.getValue());
		
		bPath.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc=new JFileChooser(MainWindow.settings.getLastVisitedDir());
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(jfc.showDialog(MainPanel.this, "Choose save folder")==JFileChooser.APPROVE_OPTION)
				{
					String path=jfc.getSelectedFile().getAbsolutePath();
					addPathToList(path);
					MainWindow.settings.setLastVisitedDir(path);
				}
			}
		});
		
		cbPath.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fillDimensionList();
			}
		});
		
		bLoad.addActionListener(new ActionListener()
		{			
			public void actionPerformed(ActionEvent e)
			{
				Options.worldDir=new File(cbPath.getSelectedItem().toString());
				if(!Options.worldDir.exists() || !Options.worldDir.isDirectory())
				{
					JOptionPane.showMessageDialog(null, "Enter correct dir!");
					Options.worldDir=null;
					return;
				}
				Options.dimension=(Integer) cbDimension.getSelectedItem();

				LevelDat levelDat=new LevelDat(Options.worldDir);

				if(!levelDat.open())
				{
					JOptionPane.showMessageDialog(null, "Cannot open level.dat in folder!");
					return;
				}				

				log(levelDat.toString());

				int player_x=0;
				int player_z=0;
				TAG_List pos=levelDat.getPosition();
				if (pos!=null)
				{
					player_x=(int)((TAG_Double)pos.getElement(0)).value;
					player_z=(int)((TAG_Double)pos.getElement(2)).value;
				}

				int spawn_x=levelDat.getSpawnX();
				int spawn_z=levelDat.getSpawnZ();

				preview.clearImages();
				preview.setPosition(player_x,player_z);
				preview.addMarker(player_x,player_z,Color.red);
				preview.addMarker(spawn_x,spawn_z,Color.green);

				if(chunk_loader!=null && chunk_loader.isRunning())
					chunk_loader.stopRunning();

				//chunk_loader=new FullChunkLoaderThread(preview);
				chunk_loader=new ViewChunkLoaderThread(preview);
				(new Thread(chunk_loader)).start();
				
				MainWindow.settings.setLastLoadedMap(Options.worldDir.toString());
			}
		});

		bExport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if(Options.worldDir==null)
				{
					JOptionPane.showMessageDialog(null, "You have to load a file first!");
					return;
				}
				Options.dimension=(Integer) cbDimension.getSelectedItem();

				Rectangle rect=preview.getSelectionBounds();
				if(rect.width==0 || rect.height==0)
				{
					JOptionPane.showMessageDialog(null, "Click and drag the left mouse button to make a selection first!");
					return;
				}
				Options.minX = rect.x;
				Options.maxX = rect.x + rect.width;
				Options.minZ = rect.y;
				Options.maxZ = rect.y + rect.height;

				Options.minY = sFloor.getValue();
				Options.maxY = sCeil.getValue();

				OBJExportPanel export_thread = new OBJExportPanel();

				Rectangle win_bounds=MainWindow.main.getBounds();
				int mx=win_bounds.x+win_bounds.width/2;
				int my=win_bounds.y+win_bounds.height/2;
				int xw=export_thread.getWidth();
				int xh=export_thread.getHeight();
				export_thread.setBounds(mx-xw/2, my-xh/2, xw, xh);
			}
		});
		
		bSettings.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				Point p=MainWindow.main.getLocation();
				p.x+=(MainWindow.main.getWidth()-MainWindow.settings.getWidth())/2;
				p.y+=(MainWindow.main.getHeight()-MainWindow.settings.getHeight())/2;
				MainWindow.settings.setLocation(p);
				MainWindow.settings.setVisible(true);
				
			}
		});
		
		bAbout.addActionListener(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				About.show();
			}
		});


		(new Thread(memory_monitor)).start();

	}
	
	private void addPathToList(String path)
	{
		for(int i=0; i<cbPath.getItemCount(); i++)
		{
			if(cbPath.getItemAt(i).equals(path))
			{
				cbPath.setSelectedIndex(i);
				return;
			}
		}
		
		cbPath.addItem(path);
		cbPath.setSelectedItem(path);
	}
	
	private void fillDimensionList()
	{
		File save_dir = new File((String)cbPath.getSelectedItem());
		if (!save_dir.isDirectory())
			return;

		cbDimension.removeAllItems();

		cbDimension.addItem(0);
		for (File f : save_dir.listFiles()) {
			if (f.isDirectory())
			{
				String dirname = f.getName();
				if (dirname.startsWith("DIM"))
				{
					int dim_id = Integer.parseInt(dirname.substring(3));
					cbDimension.addItem(dim_id);
				}
			}
		}
	}

	/**
	 * Main log method.
	 * Adds the string to the log at the bottom of the window.
	 * @param msg line to be added to the log
	 */
	public void log(String msg)
	{
		taLog.append(msg+"\n");
		try {
			taLog.setCaretPosition(taLog.getLineEndOffset(taLog.getLineCount()-1));
		} catch (BadLocationException e) {}
		//System.out.println(msg);
	}

	/**
	 * A small thread to speed up window startup.
	 * It is used to find saves in the minecraft save directory.
	 * @author danijel
	 *
	 */
	private class PopulateLoadListThread extends Thread
	{
		public void run()
		{
			File minecraft_dir=Filesystem.getMinecraftDir();
			if(minecraft_dir==null) return;
			File save_dir=new File(minecraft_dir.getAbsolutePath()+"/saves");

			if(!save_dir.exists())
				return;

			String last_map=MainWindow.settings.getLastLoadedMap();
			boolean found_last_save=false;
			
			File [] saves=save_dir.listFiles();

			String p;
			for(File f:saves)
			{
				if(f.isDirectory())
				{
					p=f.getAbsolutePath();
					cbPath.addItem(p);
					if(p.equals(last_map)) found_last_save=true;
				}
			}
			
			if(found_last_save)
				cbPath.setSelectedItem(last_map);
			else
				addPathToList(last_map);
			
			fillDimensionList();

			try{
				ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(Filesystem.getMinecraftDir(), "bin/minecraft.jar")));

				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null)
				{
					if (entry.getName().equals("title/splashes.txt"))
						break;
				}
				if (entry != null)
				{
					BufferedReader in=new BufferedReader(new InputStreamReader(zis));
					List<String> splashes=new LinkedList<String>();
					String line;
					while((line=in.readLine())!=null)
						splashes.add(line);
					in.close();
					int r=(int)(Math.random()*(double)splashes.size());
					MainWindow.main.setTitle("jMc2Obj - "+splashes.get(r));
				}								
				zis.close();
			}catch (Exception e) {/* don't care */}
		}
	}

	/**
	 * Runs the populate list thread.
	 */
	private void populateLoadList()
	{
		(new PopulateLoadListThread()).start();
	}

}
