/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * MainWindow class.
 * 
 * Used to start the program and set up initial program behavior.  
 * 
 * @author danijel
 *
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame
{
	/**
	 * Panel containing the window's content.
	 */
	private MainPanel panel;
	
	/**
	 * Window contructor.
	 */
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
	
	/**
	 * Global main window reference. 
	 */
	public static MainWindow main=null;
	
	
	/**
	 * Start of program.
	 * 
	 * @param args program arguments (currently unused)
	 */
	public static void main(String[] args)
	{
		main = new MainWindow();
	}
	
	/**
	 * Global logging method called by using MainWindow.log("test")
	 * 
	 * @param msg string to be logged
	 */
	public static void log(String msg)
	{
		if(main!=null) main.panel.log(msg);
	}
}
