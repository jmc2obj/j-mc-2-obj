/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.gui;

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jmc.util.Log;



/**
 * Main program window.
 */
@SuppressWarnings("serial")
public class MainWindow extends JmcFrame
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
	
	public static BlockListWindow blocksWindow;
	
	public static GUIConsoleLog consoleLog;
	
	public static ExportWindow export;
	
	
	/**
	 * Window contructor.
	 */
	public MainWindow()
	{
		super("Main Window");

		settings = new Settings();
		update = new UpdateWindow();
		blocksWindow = new BlockListWindow();
		consoleLog = new GUIConsoleLog();
		if(settings.getPreferences().getBoolean("OPEN_CONSOLE_ON_START", true)){
			consoleLog.setVisible(true);
		}
		export = new ExportWindow();
		
		main = this;
		
		setSize(1000,800);
		setMinimumSize(new Dimension(400,400));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("jMC2Obj");
		setLocationRelativeTo(null);
		
		panel = new MainPanel();		
		getContentPane().add(panel);
		
		setVisible(true);
	}
	
	public void loadingFinished()
	{
		panel.loadingFinished();
		blocksWindow.initialize();
	}
	
	public void highlightUpdateButton()
	{
		panel.highlightUpdateButton();
	}
	
	/**
	 * Global logging method called by using MainWindow.log("test")
	 * 
	 * @param msg string to be logged
	 * @param isError is this an error log, shows error pop-up.
	 */
	public static void log(String msg, boolean isError)
	{
		if(isError){
			try {
				SwingUtilities.invokeAndWait(new Runnable(){
					@Override
					public void run() {
						consoleLog.setVisible(true);
					}});
			} catch (InvocationTargetException e) {
				Log.error("Error erroring!", e);
			} catch (InterruptedException e) {
				Log.error("Error erroring!", e);
			}
		}
		consoleLog.log(msg, isError, false);
	}
	
	/**
	 * Global logging method called by using MainWindow.log("test")
	 * 
	 * @param msg string to be logged
	 */
	public static void logDebug(String msg)
	{
		if (settings.getPreferences().getBoolean("SHOW_DEBUG_LOG", false))
			consoleLog.log(msg, false, true);
	}
	
	/**
	 * Global method that syncs the GUI selection with options.
	 */
	public static void updateSelectionOptions()
	{
		if(main!=null) main.panel.updateSelectionOptions();
	}

	public void reloadPreviewLoader() {
		panel.reloadPreviewLoader();
	}
}
