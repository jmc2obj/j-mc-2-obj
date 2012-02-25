package org.jmc;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PreviewPanel extends JPanel
{
	private BufferedImage main_img=null;

	public PreviewPanel() 
	{
		
	}

	public void paint(Graphics g) {

		if(main_img!=null)
		{
			int mx=(getWidth()-main_img.getWidth())/2;
			int my=(getHeight()-main_img.getHeight())/2;

			Graphics2D g2 = (Graphics2D) g;
			g2.drawImage(main_img, mx, my, null);
		}		
	}


	void addImage(BufferedImage img, int x, int y)
	{
		if(main_img==null || main_img.getWidth()<img.getWidth()+x || main_img.getHeight()<img.getHeight()+y)
		{
			int w=0,h=0;
			if(main_img!=null) 
			{
				w=main_img.getWidth();
				h=main_img.getHeight();
			}
			
			if(w<x+img.getWidth()) w=x+img.getWidth();
			if(h<y+img.getHeight()) h=y+img.getHeight();
			
			BufferedImage new_image=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g=new_image.createGraphics();
			g.drawImage(main_img, 0, 0, null);
			main_img=new_image;
		}
		Graphics2D g=main_img.createGraphics();
		g.drawImage(img, x, y, null);

	}
	
	public BufferedImage blend(BufferedImage blocks, BufferedImage alpha, double amount)
	{
		BufferedImage output = new BufferedImage(blocks.getWidth(), blocks.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = output.createGraphics();
		g.drawImage(blocks, null, 0, 0);
		g.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER,(float) (1.0-amount)));
		g.drawImage(alpha, null, 0, 0);
		g.dispose();
		
		return output;
	}
}
