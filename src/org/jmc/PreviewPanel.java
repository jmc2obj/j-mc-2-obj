/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;



@SuppressWarnings("serial")
public class PreviewPanel extends JPanel implements MouseMotionListener, MouseWheelListener, MouseInputListener {

	private final int MAX_WIDTH=1920;
	private final int MAX_HEIGHT=1080;

	private int selection_start_x=0, selection_start_z=0,selection_end_x=0, selection_end_z=0;
	private int shift_x,shift_y;
	private float zoom_level;

	private BufferedImage main_img,base_img,height_img;

	private Font gui_font;
	private Color gui_color,gui_bg_color;
	private float gui_bg_alpha;

	class ChunkImage
	{
		public BufferedImage image;
		public BufferedImage height_map;
		public int x, y;
	}

	class MapMarker
	{
		int x, z;
		Color color;
	}

	private Vector<ChunkImage> chunks;
	private Vector<MapMarker> markers;

	public PreviewPanel() {

		main_img=new BufferedImage(MAX_WIDTH, MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);
		base_img=new BufferedImage(MAX_WIDTH, MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);
		height_img=new BufferedImage(MAX_WIDTH, MAX_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);

		setMaximumSize(new Dimension(MAX_WIDTH,MAX_HEIGHT));

		chunks=new Vector<ChunkImage>();
		markers=new Vector<MapMarker>();

		shift_x=0;
		shift_y=0;
		zoom_level=1;

		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		gui_font=new Font("Courier",Font.BOLD,14);
		gui_color=Color.white;
		gui_bg_color=Color.black;
		gui_bg_alpha=0.3f;		

	}

	@Override
	public void paint(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(gui_font);

		synchronized (main_img) {
			g2d.drawImage(main_img, 0, 0, null);
		}		

		for(MapMarker marker:markers)
		{
			int x=(int) ((shift_x+marker.x*4)*zoom_level) + 2;
			int y=(int) ((shift_y+marker.z*4)*zoom_level) + 2;
			g2d.setColor(marker.color);
			g2d.drawLine(x-3, y-3, x+3, y+3);
			g2d.drawLine(x+3, y-3, x-3, y+3);
		}
		
		int z_p=100-(zoom_level_pos*100/(zoom_levels.length-1));

		int mx=getWidth()/2;
		int my=getHeight()/2;
		int px=(int) ((mx/zoom_level-shift_x)/4);
		int py=(int) ((my/zoom_level-shift_y)/4);

		FontMetrics metrics=g2d.getFontMetrics(gui_font);
		int fw;
		int fh=metrics.getHeight();
		
		g2d.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER,gui_bg_alpha));
		
		if(selection_start_x!=selection_end_x && selection_start_z!=selection_end_z)
		{
			int sx=(int) ((shift_x+selection_start_x*4)*zoom_level);
			int sz=(int) ((shift_y+selection_start_z*4)*zoom_level);
			int ex=(int) ((shift_x+selection_end_x*4)*zoom_level);
			int ez=(int) ((shift_y+selection_end_z*4)*zoom_level);
			int t;
			
			if(ex<sx) {t=sx;sx=ex;ex=t;} 
			if(ez<sz) {t=sz;sz=ez;ez=t;}
			
			g2d.setColor(Color.red);			
			g2d.fillRect(sx, sz, ex-sx, ez-sz);
		}
		
		
		g2d.setColor(gui_bg_color);
		g2d.fillRect(0, 0, 80, 140+2*fh);
		g2d.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER,1));
		g2d.setColor(gui_color);
		g2d.drawRect(30, 20, 20, 100);
		g2d.drawRect(27, 18+z_p, 26, 5);
		fw=metrics.stringWidth(zoom_level+"x");
		g2d.drawString(zoom_level+"x", 40-fw/2, 125+fh);
		fw=metrics.stringWidth("("+px+","+py+")");
		g2d.drawString("("+px+","+py+")", 40-fw/2, 130+2*fh);

	}

	void redraw(boolean fast)
	{

		int win_w=getWidth();
		int win_h=getHeight();

		synchronized (main_img) {
			Graphics2D bg=base_img.createGraphics();	
			bg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);			
			bg.setColor(Color.black);			
			bg.clearRect(0, 0, win_w, win_h);			

			Graphics2D hg=null;
			if(!fast)
			{
				hg=height_img.createGraphics();
				hg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				hg.setColor(Color.black);
				hg.clearRect(0, 0, win_w, win_h);
			}

			synchronized (chunks) {
				for(ChunkImage chunk:chunks)
				{
					int x=(int) ((chunk.x+shift_x)*zoom_level);
					int y=(int) ((chunk.y+shift_y)*zoom_level);
					int w=(int) (chunk.image.getWidth()*zoom_level);
					int h=(int) (chunk.image.getHeight()*zoom_level);

					if(x>win_w || y>win_h) continue;
					if(x+w<0 || y+h<0) continue;

					bg.drawImage(chunk.image, x, y, w, h, null);
					if(!fast)
						hg.drawImage(chunk.height_map, x, y, w, h, null);
				}		
			}					

			if(!fast)
			{
				WritableRaster height_raster = height_img.getRaster();
				int h,oh;
				for(int x=0; x<win_w; x++)
					for(int y=0; y<win_h; y++)
					{
						h=height_raster.getSample(x, y, 0);
						if(x<(win_w-1) && y<(win_h-1)) oh=height_raster.getSample(x+1, y+1, 0);
						else oh=h;

						h=h+50+(oh-h)*20;
						if(h<0) h=0;
						if (h>255) h=255;

						height_raster.setSample(x, y, 0, h);
					}
			}


			Graphics2D mg=main_img.createGraphics();	
			mg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);			
			mg.setColor(Color.black);			
			mg.clearRect(0, 0, win_w, win_h);			

			mg.drawImage(base_img,0,0,null);
			if(!fast)
			{
				mg.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER,(float) (0.6)));
				mg.drawImage(height_img,0,0,null);
			}

		}
	}

	public void addImage(BufferedImage img, BufferedImage height, int x, int y)
	{		
		ChunkImage chunk=new ChunkImage();
		chunk.image=img;
		chunk.height_map=height;
		chunk.x=x;
		chunk.y=y;
		synchronized (chunks) {			
			chunks.add(chunk);
		}
		redraw(true);	
	}

	public Vector<ChunkImage> getChunkImages()
	{
		return chunks;
	}

	public void clearImages()
	{
		chunks.clear();
		markers.clear();
		redraw(true);

		shift_x=0;
		shift_y=0;
		zoom_level_pos=7;
		zoom_level=1;

	}

	public void setPosition(int x, int z)
	{
		shift_x=getWidth()/2-(x*4);
		shift_y=getHeight()/2-(z*4);
	}

	public void addMarker(int x, int z, Color color)
	{
		MapMarker marker=new MapMarker();
		marker.x=x;
		marker.z=z;
		marker.color=color;
		markers.add(marker);
	}

	public Rectangle getChunkBounds()
	{
		Rectangle ret=new Rectangle();

		ret.x=(-shift_x/64)-1;
		ret.y=(-shift_y/64)-1;
		ret.width=(int) Math.ceil((getWidth()/zoom_level)/64.0)+1;
		ret.height=(int) Math.ceil((getHeight()/zoom_level)/64.0)+1;

		return ret;
	}

	private int zoom_level_pos=7;
	private final float zoom_levels[]={0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f}; 

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int z=e.getWheelRotation();
		zoom_level_pos-=z;
		if(zoom_level_pos<0) zoom_level_pos=0;
		if(zoom_level_pos>=zoom_levels.length) zoom_level_pos=zoom_levels.length-1;
		
		int x=e.getX();
		int y=e.getY();
		
		float old_zoom_level=zoom_level;
		
		zoom_level=zoom_levels[zoom_level_pos];
		
		float ratio=zoom_level/old_zoom_level;

		shift_x-=(x-x/ratio)/old_zoom_level;
		shift_y-=(y-y/ratio)/old_zoom_level;			
		
		redraw(false);
		repaint();

	}


	private boolean left_pressed=false;
	private boolean right_pressed=false;
	private int last_x,last_y;

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			selection_start_x=(int) Math.floor((e.getX()/zoom_level-shift_x)/4);
			selection_start_z=(int) Math.floor((e.getY()/zoom_level-shift_y)/4);
			left_pressed=true;
		}

		if(e.getButton()==MouseEvent.BUTTON3)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			last_x=e.getX();
			last_y=e.getY();			
			right_pressed=true;
			
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		if(left_pressed)
		{
			
			selection_end_x=(int) Math.floor((e.getX()/zoom_level-shift_x)/4);
			selection_end_z=(int) Math.floor((e.getY()/zoom_level-shift_y)/4);		
		}
		
		left_pressed=false;
		right_pressed=false;
		redraw(false);
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {		

		if(left_pressed)
		{
			selection_end_x=(int) Math.floor((e.getX()/zoom_level-shift_x)/4);
			selection_end_z=(int) Math.floor((e.getY()/zoom_level-shift_y)/4);
			repaint();			
		}	
		
		if(right_pressed)
		{
			int x,y;

			x=e.getX();
			y=e.getY();

			shift_x+=(x-last_x)/zoom_level;
			shift_y+=(y-last_y)/zoom_level;

			last_x=x;
			last_y=y;

			redraw(true);
			repaint();		
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
	@Override
	public void mouseClicked(MouseEvent e) {	
	}
	@Override
	public void mouseEntered(MouseEvent e) {		
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}

	public Rectangle getSelectionBounds()
	{
		Rectangle rect=new Rectangle();
		int sx=selection_start_x;
		int sz=selection_start_z;
		int ex=selection_end_x;
		int ez=selection_end_z;
		int t;
		
		if(ex<sx) {t=sx;sx=ex;ex=t;} 
		if(ez<sz) {t=sz;sz=ez;ez=t;}
		
		rect.x=sx;
		rect.y=sz;
		rect.width=ex-sx;
		rect.height=ez-sz;
		
		return rect;
	}
}
