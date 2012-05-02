package org.jmc;

import javax.swing.UIManager;

import org.jmc.gui.MainWindow;
import org.jmc.util.Log;


public class Main
{

	private static void initGUI()
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		new MainWindow();
	}
	
	
	/**
	 * Start of program.
	 * 
	 * @param args program arguments
	 */
	public static void main(String[] args)
	{
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {			
			@Override
			public void uncaughtException(Thread t, Throwable e) { 
				Log.error("Uncaught exception in thread: "+t.getName(), e);
			}
		});
		
		initGUI();
		
		try {
			Configuration.initialize();
			Materials.initialize();
			BlockTypes.initialize();
		} catch (Exception e) {
			Log.error("Error reading configuration file:", e);
			return;
		}
	}

}
