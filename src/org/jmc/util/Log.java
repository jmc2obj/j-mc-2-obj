package org.jmc.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jmc.Options;
import org.jmc.Options.UIMode;
import org.jmc.gui.MainWindow;


/**
 * Logging methods
 */
public class Log
{
	private static Set<String> debugSingles = new HashSet<>();
	private static Set<String> infoSingles = new HashSet<>();
	private static Set<String> errorSingles = new HashSet<>();
	
	/**
	 * Logs a debug message to stdout.
	 * 
	 * @param msg string to be logged
	 */
	public static synchronized void debug(String msg)
	{
		System.out.println(msg);
		if (Options.uiMode == UIMode.GUI)
			MainWindow.logDebug(msg);
	}
	
	/**
	 * Logs a debug message to stdout only one time.
	 * Subsequent calls with the same message won't be printed until reset.
	 * 
	 * @param msg string to be logged
	 */
	public static synchronized void debugOnce(String msg)
	{
		if (debugSingles.add(msg))
			debug(msg);
	}

	/**
	 * Logs an informational message to stdout.
	 * If the GUI is up, it will also be shown in the messages area of the main window.
	 * 
	 * @param msg string to be logged
	 */
	public static synchronized void info(String msg)
	{
		System.out.println(msg);
		if (Options.uiMode == UIMode.GUI)
			MainWindow.log(msg, false);
	}

	/**
	 * Logs an informational message to stdout only one time.
	 * If the GUI is up, it will also be shown in the messages area of the main window.
	 * Subsequent calls with the same message won't be printed until reset.
	 * 
	 * @param msg string to be logged
	 */
	public static synchronized void infoOnce(String msg) {
		if (infoSingles.add(msg))
			info(msg);
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
	public static synchronized void error(String msg, Throwable ex, boolean popup)
	{
		System.err.println(msg);
		if (ex != null)
			ex.printStackTrace();
		
		if (Options.uiMode == UIMode.GUI ) {
			MainWindow.log("ERROR: "+msg, true);
			if (ex != null) {
				// write the full stack trace to the message area
				final StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				MainWindow.log(sw.toString(), true);
				
				if(popup == true) {
					Box panel = Box.createVerticalBox();
					Box msgBox = Box.createHorizontalBox();
					JLabel msgLabel = new JLabel(msg);
					msgBox.add(msgLabel);
					msgBox.add(Box.createGlue());
					panel.add(msgBox);
					JTextArea excArea = new JTextArea(sw.toString(), 5, 120);
					excArea.setWrapStyleWord(true);
					excArea.setLineWrap(true);
					excArea.setOpaque(false);
					excArea.setBorder(null);
					excArea.setEditable(false);
					excArea.setFocusable(false);
					JScrollPane scrollPane = new JScrollPane(excArea);
					panel.add(scrollPane);
					JOptionPane.showMessageDialog(MainWindow.main, panel, Messages.getString("Log.ERROR"), JOptionPane.ERROR_MESSAGE);
				}
			} else {
				if(popup == true)
					JOptionPane.showMessageDialog(MainWindow.main, msg, Messages.getString("Log.ERROR"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Logs an error message to stderr only one time.
	 * If the GUI is up, it will also be shown in a popup window, and the stack trace 
	 * written to the messages area of the main window.
	 * Subsequent calls with the same message won't be printed until reset.
	 * 
	 * @param msg string to be logged
	 * @param ex (optional) exception that caused the error
	 * @param popup pop a message
	 */
	public static synchronized void errorOnce(String msg, Throwable ex, boolean popup)
	{
		if (ex != null) {
			if (errorSingles.add(msg + ex.toString())) {
				error(msg, ex, popup);
			}
		} else {
			if (errorSingles.add(msg)) {
				error(msg, ex, popup);
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
	
	public static synchronized void resetSingles() {
		debugSingles.clear();
		infoSingles.clear();
		errorSingles.clear();
	}
}
