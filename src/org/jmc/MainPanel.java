package org.jmc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.BoxLayout;
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
	private JButton bLoad,bSave;
	private JComboBox cbPath;
	private JTextArea taLog;
	private JScrollPane spPane;
	private PreviewPanel preview;
	private MemoryMonitor memory_monitor;

	private ChunkLoaderThread chunk_loader=null;

	public MainPanel()
	{
		setLayout(new BorderLayout());
		JPanel buttons = new JPanel();
		preview = new PreviewPanel();
		preview.setBackground(new Color(110,150,100));
		bLoad = new JButton("Load");
		bSave = new JButton("Save chunk");
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

				if(chunk_loader!=null && chunk_loader.isRunning())
					chunk_loader.stopRunning();

				//chunk_loader=new FullChunkLoaderThread(preview, savepath);
				chunk_loader=new ViewChunkLoaderThread(preview, savepath);
				(new Thread(chunk_loader)).start();
			}
		});

		bSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				File savepath=new File((String)cbPath.getSelectedItem());
				if(!savepath.exists() || !savepath.isDirectory())
				{
					JOptionPane.showMessageDialog(null, "Enter correct dir!");
					return;
				}

				try {
					FileWriter file=new FileWriter("out.obj");
					PrintWriter writer=new PrintWriter(file);

					MTLFile mtl=new MTLFile();
					
					mtl.saveMTLFile(new File("minecraft.mtl"));

					mtl.header(writer);

					int x=preview.getSelectedChunkX();
					int z=preview.getSelectedChunkZ();

					for(int cx=x-1,ox=-1; cx<=x+1; cx++,ox++)
						for(int cz=z-1,oz=-1; cz<=z+1; cz++,oz++)
						{
							Region region=Region.findRegion(savepath, cx, cz);					
							Chunk chunk=region.getChunk(cx, cz);

							OBJFile obj=chunk.getOBJ(mtl);
							obj.setOffset(ox*16, oz*16);

							obj.append(writer);
						}

					writer.close();


				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});


		(new Thread(memory_monitor)).start();

	}

	public void log(String msg)
	{
		taLog.append(msg+"\n");
		//System.out.println(msg);
	}

	class PopulateLoadListThread extends Thread
	{
		public void run()
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

	private void populateLoadList()
	{
		(new PopulateLoadListThread()).start();
	}
}
