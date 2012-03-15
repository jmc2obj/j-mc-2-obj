/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jmc.NBT.TAG_Double;
import org.jmc.NBT.TAG_List;


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
	private JButton bLoad,bSave,bSettings;
	private JComboBox cbPath;
	private JTextArea taLog;
	private JScrollPane spPane;
	
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

	/**
	 * Reference to the loaded file used by the export function to
	 * be able to save the last loaded file.
	 */
	private File loaded_file=null;

	/**
	 * Panel contructor.
	 */
	public MainPanel()
	{
		setLayout(new BorderLayout());
		JPanel buttons = new JPanel();
		preview = new PreviewPanel();
		preview.setBackground(new Color(110,150,100));
		bLoad = new JButton("Load");
		bSave = new JButton("Export selection");
		bSettings = new JButton("Settings");
		cbPath = new JComboBox();	
		JPanel jpBottom = new JPanel();
		taLog = new JTextArea(5,1);
		spPane = new JScrollPane(taLog);
		memory_monitor=new MemoryMonitor();

		cbPath.setEditable(true);
		jpBottom.setLayout(new BoxLayout(jpBottom, BoxLayout.Y_AXIS));

		populateLoadList();		
		
		buttons.add(cbPath);
		buttons.add(bLoad);
		buttons.add(bSave);
		buttons.add(bSettings);
		add(buttons, BorderLayout.NORTH);
		add(preview);
		jpBottom.add(spPane);
		jpBottom.add(memory_monitor);
		add(jpBottom, BorderLayout.SOUTH);
		taLog.setLineWrap(true);
		bLoad.addActionListener(new ActionListener()
		{			
			public void actionPerformed(ActionEvent e)
			{
				String map=(String) cbPath.getSelectedItem();				
				loaded_file=new File(map);
				if(!loaded_file.exists() || !loaded_file.isDirectory())
				{
					JOptionPane.showMessageDialog(null, "Enter correct dir!");
					return;
				}

				LevelDat levelDat=new LevelDat(loaded_file);

				if(!levelDat.open())
				{
					JOptionPane.showMessageDialog(null, "Cannot open level.dat in folder!");
					return;
				}				

				log(levelDat.toString());

				TAG_List pos=levelDat.getPosition();
				int player_x=(int)((TAG_Double)pos.getElement(0)).value;
				int player_z=(int)((TAG_Double)pos.getElement(2)).value;

				int spawn_x=levelDat.getSpawnX();
				int spawn_z=levelDat.getSpawnZ();

				preview.clearImages();
				preview.setPosition(player_x,player_z);
				preview.addMarker(player_x,player_z,Color.red);
				preview.addMarker(spawn_x,spawn_z,Color.green);

				if(chunk_loader!=null && chunk_loader.isRunning())
					chunk_loader.stopRunning();

				//chunk_loader=new FullChunkLoaderThread(preview, savepath);
				chunk_loader=new ViewChunkLoaderThread(preview, loaded_file);
				(new Thread(chunk_loader)).start();
				
				MainWindow.settings.setLastLoadedMap(map);
			}
		});

		bSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if(loaded_file==null)
				{
					JOptionPane.showMessageDialog(null, "You have to load a file first!");
					return;
				}

				JFileChooser jfcSave=new JFileChooser();
				jfcSave.setDialogTitle("Save OBJ file");
				if(jfcSave.showSaveDialog(MainWindow.main)!=JFileChooser.APPROVE_OPTION)
				{
					return;
				}

				File objfile=jfcSave.getSelectedFile();
				if(!objfile.getName().endsWith(".obj"))
				{
					objfile=new File(objfile.getAbsolutePath()+".obj");
				}

				if(objfile.exists())
				{
					if(JOptionPane.showConfirmDialog(MainWindow.main, "File exists. Do you want to overwrite?")!=JOptionPane.YES_OPTION)
					{
						return;
					}
				}

				int ymin=0;
				Rectangle rect=preview.getSelectionBounds();

				if(rect.width==0 || rect.height==0)
				{
					JOptionPane.showMessageDialog(null, "Click and drag the right mouse button to make a selection first!");
					return;
				}

				ymin=MainWindow.settings.getGroundLevel();

				OBJExportThread export_thread = new OBJExportThread(objfile,loaded_file, rect, ymin);

				Rectangle win_bounds=MainWindow.main.getBounds();
				int mx=win_bounds.x+win_bounds.width/2;
				int my=win_bounds.y+win_bounds.height/2;
				export_thread.setBounds(mx-100, my-25, 200, 50);

				(new Thread(export_thread)).start();

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


		(new Thread(memory_monitor)).start();

	}

	/**
	 * Main log method.
	 * Adds the string to the log at the bottom of the window.
	 * @param msg line to be added to the log
	 */
	public void log(String msg)
	{
		taLog.append(msg+"\n");
		//System.out.println(msg);
	}

	/**
	 * A small thread to speed up window startup.
	 * It is used to find saves in the minecraft save directory.
	 * @author danijel
	 *
	 */
	class PopulateLoadListThread extends Thread
	{
		public void run()
		{
			File minecraft_dir=getMinecraftDir();
			if(minecraft_dir==null) return;
			File save_dir=new File(minecraft_dir.getAbsolutePath()+"/saves");

			if(!save_dir.exists())
				return;

			String last_map=MainWindow.settings.getLastLoadedMap();
			boolean use_last_map=false;
			
			File [] saves=save_dir.listFiles();

			String p;
			for(File f:saves)
			{
				if(f.isDirectory())
				{
					p=f.getAbsolutePath();
					cbPath.addItem(p);
					if(p.equals(last_map)) use_last_map=true;
				}
			}
			
			if(use_last_map)
				cbPath.setSelectedItem(last_map);
		}
	}

	/**
	 * Runs the populate list thread.
	 */
	private void populateLoadList()
	{
		(new PopulateLoadListThread()).start();
	}

	/**
	 * Gets the directory that Minecraft keeps its save files in.
	 * It works on all systems that Minecraft 1.2 works in.
	 * @return path to the Minecraft dir
	 */
	public static File getMinecraftDir()
	{
		String minecraft="minecraft";
		String osname = System.getProperty("os.name").toLowerCase();
		String default_home = System.getProperty("user.home", ".");
		if(osname.contains("solaris") || osname.contains("sunos") || osname.contains("linux") || osname.contains("unix"))
		{
			return new File(default_home, (new StringBuilder()).append('.').append(minecraft).append('/').toString());
		}

		if(osname.contains("win"))
		{
			String win_home = System.getenv("APPDATA");
			if(win_home != null)
			{
				return new File(win_home, (new StringBuilder()).append(".").append(minecraft).append('/').toString());
			} else
			{
				return new File(default_home, (new StringBuilder()).append('.').append(minecraft).append('/').toString());
			}
		}

		if(osname.contains("mac"))
		{
			return new File(default_home, (new StringBuilder()).append("Library/Application Support/").append(minecraft).toString());
		}

		return null;
	}

}
