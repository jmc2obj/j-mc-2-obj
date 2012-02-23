package org.jmc;

import javax.swing.JFrame;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class MainWindow extends JFrame{


	MainWindow()
	{
		super("Main Window");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//TODO: remove this line
		System.out.println("Test!");

		setSize(640,480);

		setVisible(true);
	}

	public static MainWindow main;

	public static void main(String[] args) {

		main=new MainWindow();

	}

}
