package org.jmc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jmc.NBT.TAG_Double;
import org.jmc.NBT.TAG_List;

@SuppressWarnings("serial")
public class MainPanel extends JPanel
{
	private JButton bLoad;
	private JComboBox cbPath;
	private JTextArea taLog;
	private JScrollPane spPane;
	private PreviewPanel preview;

	public MainPanel()
	{
		setLayout(new BorderLayout());
		JPanel buttons = new JPanel();
		preview = new PreviewPanel();
		preview.setBackground(new Color(110,150,100));
		bLoad = new JButton("Load");
		cbPath = new JComboBox();		
		taLog = new JTextArea(5,1);
		spPane = new JScrollPane(taLog);

		cbPath.setEditable(true);

		populateLoadList();

		buttons.add(cbPath);
		buttons.add(bLoad);
		add(buttons, BorderLayout.NORTH);
		add(preview);
		add(spPane, BorderLayout.SOUTH);
		taLog.setLineWrap(true);
		bLoad.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				File savepath=new File((String)cbPath.getSelectedItem());
				if(!savepath.exists() || !savepath.isDirectory())
				{
					JOptionPane.showMessageDialog(null, "Enter correct dir!");
					return;
				}


				LevelDat levelDat=new LevelDat(savepath);

				if(!levelDat.open())
				{
					JOptionPane.showMessageDialog(null, "Cannot open level.dat in folder!");
					return;
				}				

				log(levelDat.toString());

				TAG_List pos=levelDat.getPosition();
				int x=(int)((TAG_Double)pos.getElement(0)).value;
				int z=(int)((TAG_Double)pos.getElement(2)).value;

				//convert from player coords to chunk coords
				x=x/16;
				z=z/16;				

				for(int cx=x-3, ix=0; cx<=x+3; cx++, ix++)
					for(int cz=z-3, iy=0; cz<=z+3; cz++, iy++)
					{

						AnvilRegion region=null;
						Chunk chunk=null;
						try {
							region = AnvilRegion.findRegion(savepath, cx, cz);
							chunk=region.getChunk(cx, cz);				
						} catch (Exception ex) {
							ex.printStackTrace();
							log("Error: "+ex);
							return;
						}							

						if(chunk!=null) log(chunk.toString());
						else 
						{
							log("Chunk couldn't be loaded.");
							return;
						}

						//BufferedImage img=chunk.getHeightImage();
						BufferedImage img=chunk.getBlocks();
						
						preview.addImage(img, ix*64, iy*64);
						
						preview.repaint();
					
					}
			}
		});

	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		repaint();
	}

	public void log(String msg)
	{
		taLog.append(msg+"\n");
		//System.out.println(msg);
	}

	private void populateLoadList()
	{
		//TODO: this works in windows only! check other OSs and fix accordingly
		File save_dir=new File(System.getenv("appdata")+"\\.minecraft\\saves");

		if(!save_dir.exists())
			return;

		File [] saves=save_dir.listFiles();

		for(File f:saves)
		{
			if(f.isDirectory())
				cbPath.addItem(f.getAbsolutePath());
		}
	}
}
