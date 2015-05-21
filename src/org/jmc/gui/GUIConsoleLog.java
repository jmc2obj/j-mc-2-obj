package org.jmc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
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
		setBackground(Color.BLACK);
		setSize(600, 300);
		
		JPanel contentPane = new JPanel();contentPane.setLayout(new BorderLayout());
		contentPane.setBackground(Color.BLACK);
		setContentPane(contentPane);
		
		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBackground(Color.BLACK);
		
		taLog = new JTextArea(5, 1);
		taLog.setLineWrap(true);
		taLog.setEditable(false);
		taLog.setFont(new Font("Courier", 0, 14));
		taLog.setBackground(Color.BLACK);
		taLog.setForeground(Color.WHITE);

		spPane = new JScrollPane(taLog);
		
		contentPane.add(spPane);
		//add(mainPanel);
		
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
	public void log(String msg) {
		taLog.append(msg + "\n");
		try {
			taLog.setCaretPosition(taLog.getLineEndOffset(taLog.getLineCount() - 1));
		} catch (BadLocationException e) { /* don't care */
		}
	}
	
}
