package org.jmc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

@SuppressWarnings("serial")
public class GUIConsoleLog extends JmcFrame{
	
	private JScrollPane spPane;
	private JTextPane taLog;

	public GUIConsoleLog(){
		super("Console log");
		getContentPane().setLayout(new BorderLayout());
		setBackground(Color.BLACK);
		setSize(600, 300);
		
		JPanel contentPane = new JPanel();contentPane.setLayout(new BorderLayout());
		contentPane.setBackground(Color.BLACK);
		setContentPane(contentPane);
		
		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBackground(Color.BLACK);
		
		taLog = new JTextPane();
		taLog.setEditable(false);
		taLog.setFont(new Font("Lucida Console", 0, 14));
		taLog.setBackground(Color.BLACK);

		spPane = new JScrollPane(taLog);
		
		contentPane.add(spPane);
		
		JPanel optionPanel = new JPanel();
		optionPanel.setBackground(Color.BLACK);
		optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.X_AXIS));
		contentPane.add(optionPanel, BorderLayout.SOUTH);
		
		final JCheckBox openOnStart = new JCheckBox("Open Console On Startup", MainWindow.settings.getPreferences().getBoolean("OPEN_CONSOLE_ON_START", true));
		openOnStart.setForeground(Color.WHITE);
		openOnStart.setBackground(Color.BLACK);
		openOnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow.settings.getPreferences().putBoolean("OPEN_CONSOLE_ON_START", openOnStart.isSelected());
			}
		});
		
		optionPanel.add(openOnStart);
		
		final JCheckBox showDebug = new JCheckBox("Show debug", MainWindow.settings.getPreferences().getBoolean("SHOW_DEBUG_LOG", false));
		showDebug.setForeground(Color.WHITE);
		showDebug.setBackground(Color.BLACK);
		showDebug.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow.settings.getPreferences().putBoolean("SHOW_DEBUG_LOG", showDebug.isSelected());
			}
		});
		optionPanel.add(showDebug);
	}
	
	/**
	 * Main log method. Adds the string to the log at the bottom of the window.
	 * 
	 * @param msg
	 *            line to be added to the log
	 */
	public void log(String msg, boolean isError, boolean isDebug) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Style color = taLog.addStyle("color", null);
					StyleConstants.setForeground(color, isError ? Color.RED : isDebug ? Color.GRAY : Color.WHITE);
					taLog.getStyledDocument().insertString(taLog.getDocument().getLength(), msg + "\n", color);
					taLog.setCaretPosition(taLog.getDocument().getLength());
				} catch (BadLocationException e) { /* don't care */	}
			}
		});
	}
	
}
