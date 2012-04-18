package org.jmc.util;

import java.io.PrintWriter;
import java.io.StringWriter;

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
	 * If the UI is up, it will also be shown in a popup window, and the stack trace 
	 * written to the messages area of the main window.
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
			JOptionPane.showMessageDialog(MainWindow.main, msg + "\n" + ex.getClass().getSimpleName() + ": " + ex.getMessage());
			
			// write the full stack trace to the message area
			final StringWriter sw = new StringWriter();
		    ex.printStackTrace(new PrintWriter(sw));
			MainWindow.log(sw.toString());
		}
		else
		{
			JOptionPane.showMessageDialog(MainWindow.main, msg);
		}
	}

}
