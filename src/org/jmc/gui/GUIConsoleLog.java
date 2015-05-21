package org.jmc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

@SuppressWarnings("serial")
public class GUIConsoleLog extends JFrame{
	
	private JScrollPane spPane;
	private JTextPane taLog;

	public GUIConsoleLog(){
		setLayout(new BorderLayout());
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
		
		final JCheckBox openOnStart = new JCheckBox("Open Console On Startup", MainWindow.settings.getPreferences().getBoolean("OPEN_CONSOLE_ON_START", true));
		openOnStart.setForeground(Color.WHITE);
		openOnStart.setBackground(Color.BLACK);
		openOnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow.settings.getPreferences().putBoolean("OPEN_CONSOLE_ON_START", openOnStart.isSelected());
			}
		});
		
		contentPane.add(openOnStart, BorderLayout.SOUTH);
	}
	
	/**
	 * Main log method. Adds the string to the log at the bottom of the window.
	 * 
	 * @param msg
	 *            line to be added to the log
	 */
	public void log(String msg, boolean isError) {		
		try {
			Style color = taLog.addStyle("color", null);
			StyleConstants.setForeground(color, isError ? Color.RED : Color.WHITE);
			taLog.getStyledDocument().insertString(taLog.getDocument().getLength(), msg + "\n", color);
			taLog.setCaretPosition(taLog.getDocument().getLength());
		} catch (BadLocationException e) { /* don't care */	}
	}
	
}
