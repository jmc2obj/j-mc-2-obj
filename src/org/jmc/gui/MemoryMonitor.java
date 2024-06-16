/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.gui;

import org.jmc.util.Messages;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

/**
 * Memory monitor panel and thread.
 * This class describes a thread and a GUI component that monitors the memory usage
 * of the Java program.
 * @author danijel
 *
 */
@SuppressWarnings("serial")
public class MemoryMonitor extends JPanel implements Runnable {

	//UI elements (not described)
	JLabel lblTextStats;
	JProgressBar barUsed, barMax;
	
	/**
	 * Main constructor.
	 */
	public MemoryMonitor() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
		
		lblTextStats =new JLabel("MEM: ?");
		barUsed =new JProgressBar();
		barMax =new JProgressBar();
		
		barUsed.setStringPainted(true);
		barMax.setStringPainted(true);
		
		lblTextStats.setPreferredSize(new Dimension(150, 20));
		lblTextStats.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		JLabel lblMem = new JLabel(Messages.getString("MemoryMonitor.MEMORY"));
		lblMem.setBorder(new EmptyBorder(0, 4, 0, 4));
		add(lblMem);
		add(lblTextStats);
		JLabel lblUsed = new JLabel(Messages.getString("MemoryMonitor.TOTAL_USED"));
		lblUsed.setBorder(new EmptyBorder(0, 4, 0, 4));
		add(lblUsed);
		add(barUsed);
		JLabel lblMax = new JLabel(Messages.getString("MemoryMonitor.MAX_USED"));
		lblMax.setBorder(new EmptyBorder(0, 4, 0, 4));
		add(lblMax);
		add(barMax);
	}
	
	/**
	 * Method used to convert size in bytes into a descriptive string in KB, MB or GB.
	 * @param mem size in bytes
	 * @return string in KB, MB or GB
	 */
	private String toSize(long mem)
	{
		if(mem>=1024)
		{			
			if(mem>=1045504)
			{
				if(mem>=1070596096)
				{
					return (mem/1070596096)+"G";
				}
				return (mem/1045504)+"M";
			}
			return (mem/1024)+"K";
		}
		
		return ""+mem;
	}
	
	private int scaleLong(long val, long max) {
		double pos = (double)val / (double)max;
		return (int) Math.floor((double)Integer.MAX_VALUE * pos);
	}
	
	/**
	 * Main thread method.
	 */
	@Override
	public void run() {
		while(true) {
			long total=Runtime.getRuntime().totalMemory();
			long free=Runtime.getRuntime().freeMemory();
			long max=Runtime.getRuntime().maxMemory();
			
			lblTextStats.setText("T:"+toSize(total)+" F:"+toSize(free)+" M:"+toSize(max));
			
			barUsed.setMaximum(scaleLong(total, total));
			barUsed.setValue(scaleLong(total-free, total));
			
			barMax.setMaximum(scaleLong(max, max));
			barMax.setValue(scaleLong(total, max));
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				return;
			}
		}

	}

}
