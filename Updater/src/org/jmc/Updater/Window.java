package org.jmc.Updater;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class Window extends JFrame {
	
	private JTextArea taLog;
	private JProgressBar progress;
	
	public Window()
	{
		super("Updating application");
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setSize(400,400);
		
		JPanel cp=new JPanel();
		cp.setLayout(new BoxLayout(cp, BoxLayout.PAGE_AXIS));
		cp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		taLog=new JTextArea();
		taLog.setEditable(false);
		taLog.setFont(new Font("Verdana", 0, 12));
		taLog.setPreferredSize(new Dimension(300, 200));
		
		JScrollPane spLog=new JScrollPane(taLog);
		cp.add(spLog);
		
		progress=new JProgressBar();
		cp.add(progress);
		
		add(cp);
		
		pack();
		setVisible(true);
		
		log("Close this window to cancel update.");
	}
	
	public void log(String msg)
	{
		taLog.append(msg+"\n");
	}
	
	public void setProgressMax(int val)
	{
		progress.setMaximum(val);
	}
	
	public void setProgress(int val)
	{
		progress.setValue(val);
	}

}
