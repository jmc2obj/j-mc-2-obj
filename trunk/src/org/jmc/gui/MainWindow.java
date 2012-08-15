/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.gui;

import java.awt.Dimension;

import javax.swing.JFrame;



/**
 * Main program window.
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame
{
	/**
	 * Panel containing the window's content.
	 */
	private MainPanel panel;
	

	/**
	 * Global main window reference. 
	 */
	public static MainWindow main;
	
	/**
	 * Global main settings and settings window reference.
	 */
	public static Settings settings;
	
	public static UpdateWindow update;
	
	public static FileNames file_names;
	
	
	/**
	 * Window contructor.
	 */
	public MainWindow()
	{
		super("Main Window");

		settings = new Settings();
		update = new UpdateWindow();
		file_names = new FileNames();
		
		main = this;
		
		setSize(800,600);
		setMinimumSize(new Dimension(400,400));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("jMC2Obj");
		setLocationRelativeTo(null);
		
		panel = new MainPanel();		
		add(panel);
		
		setVisible(true);
	}
	
	public void loadingFinished()
	{
		panel.loadingFinished();
	}
	
	public void highlightUpdateButton()
	{
		panel.highlightUpdateButton();
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
