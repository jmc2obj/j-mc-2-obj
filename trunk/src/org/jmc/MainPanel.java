package org.jmc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
				int player_x=(int)((TAG_Double)pos.getElement(0)).value;
				int player_z=(int)((TAG_Double)pos.getElement(2)).value;
				
				int spawn_x=levelDat.getSpawnX();
				int spawn_z=levelDat.getSpawnZ();
				
				preview.clearImages();
				preview.setPosition(player_x*4,player_z*4);
				preview.addMarker(player_x*4,player_z*4,Color.red);
				preview.addMarker(spawn_x*4,spawn_z*4,Color.green);

				(new ChunkLoaderThread(preview, savepath)).start();
			}
		});

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
