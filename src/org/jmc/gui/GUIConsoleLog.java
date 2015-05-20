package org.jmc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

@SuppressWarnings("serial")
public class GUIConsoleLog extends JFrame{
	
	private JScrollPane spPane;
	private JTextArea taLog;

	public GUIConsoleLog(){
		setLayout(new BorderLayout());
		setSize(600, 300);
		
		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		
		taLog = new JTextArea(5, 1);
		taLog.setLineWrap(true);
		taLog.setEditable(false);
		taLog.setFont(new Font("Courier", 0, 14));
		taLog.setBackground(Color.BLACK);
		taLog.setForeground(Color.WHITE);

		spPane = new JScrollPane(taLog);
		
		mainPanel.add(spPane);
		add(mainPanel);
	}
	
	/**
	 * Main log method. Adds the string to the log at the bottom of the window.
	 * 
	 * @param msg
	 *            line to be added to the log
	 */
	public void log(String msg) {
		taLog.append(msg + "\n");
		try {
			taLog.setCaretPosition(taLog.getLineEndOffset(taLog.getLineCount() - 1));
		} catch (BadLocationException e) { /* don't care */
		}
	}
	
}
