package org.jmc.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import org.jmc.Options;
import org.jmc.Options.UIMode;
import org.jmc.gui.MainWindow;


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
		System.out.println(msg);//TODO add debug checkbox to message window?
	}

	/**
	 * Logs an informational message to stdout.
	 * If the GUI is up, it will also be shown in the messages area of the main window.
	 * 
	 * @param msg string to be logged
	 */
	public static void info(String msg)
	{
		System.out.println(msg);
		if (Options.uiMode == UIMode.GUI)
			MainWindow.log(msg, false);
	}

	/**
	 * Logs an error message to stderr.
	 * If the GUI is up, it will also be shown in a popup window, and the stack trace 
	 * written to the messages area of the main window.
	 * 
	 * @param msg string to be logged
	 * @param ex (optional) exception that caused the error
	 * @param popup pop a message
	 */
	public static void error(String msg, Throwable ex, boolean popup)
	{
		System.err.println(msg);
		if (ex != null)
			ex.printStackTrace();
		
		if (Options.uiMode == UIMode.GUI )
		{
			MainWindow.log("ERROR: "+msg, true);	
			if (ex != null)
			{
				if(popup == true)
					JOptionPane.showMessageDialog(MainWindow.main, msg + "\n" + ex.getClass().getSimpleName() + ": " + ex.getMessage());
				
				// write the full stack trace to the message area
				final StringWriter sw = new StringWriter();
			    ex.printStackTrace(new PrintWriter(sw));
				MainWindow.log(sw.toString(), true);
			}
			else
			{
				if(popup == true)
					JOptionPane.showMessageDialog(MainWindow.main, msg);
			}
		}
	}
	
	/**
	 * Version that automatically pops a message.
	 * @param msg
	 * @param ex
	 */
	public static void error(String msg, Throwable ex)
	{
		error(msg,ex,true);
	}

}
