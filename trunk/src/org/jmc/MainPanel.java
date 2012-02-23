package org.jmc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainPanel extends JPanel
{
	private JButton load;
	private JTextField path;
	private JTextArea log;
	
	public MainPanel()
	{
		load = new JButton("Load .dat");
		path = new JTextField(20);
		log = new JTextArea(20,70);
		
		add(path);
		add(load);
		add(log);
		log.setLineWrap(true);
		load.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				File savepath=new File(path.getText());
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
				
				log.append(levelDat.toString());				
			}
		});
		
	}
}
