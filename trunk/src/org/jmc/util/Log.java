package org.jmc.util;

import javax.swing.JOptionPane;

import org.jmc.MainWindow;


/**
 * Logging methods
 */
public class Log
{

	/**
	 * Logs a debug message to stdout.
	 * 
	 * @param msg string to be logged
	 */
	public static void debug(String msg)
	{
		System.out.println(msg);
	}

	/**
	 * Logs an informational message to stdout.
	 * If the UI is up, it will also be shown in the messages area of the main window.   
	 * 
	 * @param msg string to be logged
	 */
	public static void info(String msg)
	{
		System.out.println(msg);
		MainWindow.log(msg);
	}

	/**
	 * Logs an error message to stderr.
	 * If the UI is up, it will also be shown in a popup window.   
	 * 
	 * @param msg string to be logged
	 * @param ex (optional) exception that caused the error
	 */
	public static void error(String msg, Exception ex)
	{
		System.err.println(msg);
		if (ex != null)
			ex.printStackTrace();
		
		if (ex != null)
		{
			String exMsg = ex.getMessage();
			if (exMsg == null)
				exMsg = ex.getClass().getSimpleName();
			JOptionPane.showMessageDialog(MainWindow.main, msg + "\n" + exMsg);
		}
		else
		{
			JOptionPane.showMessageDialog(MainWindow.main, msg);
		}
	}

}
