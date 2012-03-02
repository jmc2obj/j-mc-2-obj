package org.jmc;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class MemoryMonitor extends JPanel implements Runnable {

	JLabel label;
	JProgressBar bar1,bar2;
	
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
