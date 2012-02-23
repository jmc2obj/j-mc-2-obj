package org.jmc;

import javax.swing.JFrame;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class MainWindow extends JFrame
{
	public MainPanel panel;
	MainWindow()
	{
		super("Main Window");

		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		setSize(800,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("J-MC2OBJ - v0.1");
		setLocationRelativeTo(null);
		
		panel = new MainPanel();
		
		add(panel);
		
		setVisible(true);
		
	}
	public static MainWindow main;
	public static void main(String[] args)
	{
		main = new MainWindow();
	}
}
