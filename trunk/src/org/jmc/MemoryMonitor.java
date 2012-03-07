/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

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
	JLabel label;
	JProgressBar bar1,bar2;
	
	/**
	 * Main constructor.
	 */
	public MemoryMonitor() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
		
		label=new JLabel("MEM: ?");
		bar1=new JProgressBar();
		bar2=new JProgressBar();
		
		bar1.setStringPainted(true);
		bar2.setStringPainted(true);
		
		label.setPreferredSize(new Dimension(100, 20));
		label.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		add(label);
		add(bar1);
		add(bar2);
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
	
	/**
	 * Main thread method.
	 */
	@Override
	public void run() {
		
		while(true)
		{
			long total=Runtime.getRuntime().totalMemory();
			long free=Runtime.getRuntime().freeMemory();
			long max=Runtime.getRuntime().maxMemory();
			
			label.setText("T:"+toSize(total)+" F:"+toSize(free));		
			
			bar1.setMaximum((int) total);
			bar1.setValue((int) (total-free));
			
			bar2.setMaximum((int) max);
			bar2.setValue((int) total);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}

	}

}
