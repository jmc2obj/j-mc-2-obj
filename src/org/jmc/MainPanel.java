package org.jmc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainPanel extends JPanel
{
	private JButton load;
	private JTextField path;
	private JTextArea log;
	private JScrollPane pane;
	
	public MainPanel()
	{
		setLayout(new BorderLayout());
		JPanel buttons = new JPanel();
		JPanel preview = new JPanel();
		preview.setBackground(new Color(110,150,100));
		load = new JButton("Load .dat");
		path = new JTextField(20);
		log = new JTextArea(5,1);
		pane = new JScrollPane(log);
		
		buttons.add(path);
		buttons.add(load);
		add(buttons, BorderLayout.NORTH);
		add(preview);
		add(pane, BorderLayout.SOUTH);
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
