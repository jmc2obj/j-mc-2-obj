/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import org.jmc.util.Messages;



/**
 * Panel used for displaying the preview of the map.
 * @author danijel
 *
 */
@SuppressWarnings("serial")
public class PreviewPanel extends JPanel implements MouseMotionListener, MouseWheelListener, MouseInputListener {

	//Might want to up these later to support larger monitors...
	/**
	 * Maximum width of the panel.
	 */
	private final int MAX_WIDTH=1920;
	/**
	 * Maximum height of the panel.
	 */
	private final int MAX_HEIGHT=1080;

	/**
	 * Selection boundaries.
	 */
	public int selection_start_x=0, selection_start_z=0,selection_end_x=0, selection_end_z=0;
	private int screen_sx=-1, screen_sz=-1, screen_ex=-1, screen_ez=-1;
	/**
	 * Altitude ranges.
	 */
	@SuppressWarnings("unused")
	private int alt_floor, alt_ceil;
	/**
	 * Offset of the map, as set by dragging the map around.
	 */
	private int shift_x,shift_y;
	/**
	 * Zoom level of the map.
	 */
	private float zoom_level;

	/**
	 * Back buffers used for drawing the preview.
	 */
	private BufferedImage main_img,base_img,height_img;

	/**
	 * Font used in the preview.
	 */
	private Font gui_font;
	/**
	 * Foreground and background colors for the UI.
	 */
	private Color gui_color,gui_bg_color;
	/**
	 * Alpha of the background for the UI.
	 */
	private float gui_bg_alpha;
	/**
	 * Buffer of text lines drawn in the GUI.
	 */
	private Vector<String> gui_text;
	
	public boolean fastrendermode;
	public boolean showchunks;
	public boolean selectchunks;

	/**
	 * Small internal class describing an image of a single chunk this preview is comprised of.
	 * @author danijel
	 *
	 */
	class ChunkImage
	{
		public BufferedImage image;
		public BufferedImage height_map;
		public int x, y;
	}

	/**
	 * Small internal class for map markers.
	 * @author danijel
	 *
	 */
	class MapMarker
	{
		int x, z;
		Color color;
	}

	/**
	 * Collection of chunks.
	 */
	private Vector<ChunkImage> chunks;
	/**
	 * Collection of markers.
	 */
	private Vector<MapMarker> markers;

	/**
	 * Main constructor.
	 */
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

		gui_font=new Font("Verdana",Font.BOLD,10); 
		gui_color=Color.white;
		gui_bg_color=Color.black;
		gui_bg_alpha=0.3f;		
		gui_text=new Vector<String>();

	}

	/**
	 * Main repaint procedure (run as much as possible).
	 */
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
			screen_sx=(int) ((shift_x+selection_start_x*4)*zoom_level);
			screen_sz=(int) ((shift_y+selection_start_z*4)*zoom_level);
			screen_ex=(int) ((shift_x+selection_end_x*4)*zoom_level);
			screen_ez=(int) ((shift_y+selection_end_z*4)*zoom_level);
			int t;

			if(screen_ex<screen_sx) {t=screen_sx;screen_sx=screen_ex;screen_ex=t;} 
			if(screen_ez<screen_sz) {t=screen_sz;screen_sz=screen_ez;screen_ez=t;}

			g2d.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER,gui_bg_alpha));
			g2d.setColor(Color.red);			
			g2d.fillRect(screen_sx, screen_sz, screen_ex-screen_sx, screen_ez-screen_sz);

			g2d.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER,1));
			g2d.setColor(Color.black);			
			g2d.drawRect(screen_sx, screen_sz, screen_ex-screen_sx, screen_ez-screen_sz);

			g2d.setColor(Color.white);
			g2d.fillRect(screen_sx-2, screen_sz-2, 4, 4);
			g2d.fillRect(screen_sx-2, screen_ez-2, 4, 4);
			g2d.fillRect(screen_ex-2, screen_sz-2, 4, 4);
			g2d.fillRect(screen_ex-2, screen_ez-2, 4, 4);

			g2d.setColor(Color.black);
			g2d.drawRect(screen_sx-2, screen_sz-2, 4, 4);
			g2d.drawRect(screen_sx-2, screen_ez-2, 4, 4);
			g2d.drawRect(screen_ex-2, screen_sz-2, 4, 4);
			g2d.drawRect(screen_ex-2, screen_ez-2, 4, 4);
		}
		else
		{
			screen_sx=-1;
			screen_ex=-1;
			screen_sz=-1;
			screen_ez=-1;
		}


		g2d.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER,gui_bg_alpha));
		gui_text.clear();
		gui_text.add(zoom_level+"x"); 
		gui_text.add("("+px+","+py+")");  //$NON-NLS-2$ //$NON-NLS-3$
		gui_text.add(Messages.getString("PreviewPanel.SELECTION")); 
		gui_text.add("("+selection_start_x+","+selection_start_z+")");  //$NON-NLS-2$ //$NON-NLS-3$
		gui_text.add("("+selection_end_x+","+selection_end_z+")");  //$NON-NLS-2$ //$NON-NLS-3$
		
		//Commented these out because this information is now in a JSpinner on MainPanel
		//gui_text.add(Messages.getString("PreviewPanel.FLOOR")+alt_floor); 
		//gui_text.add(Messages.getString("PreviewPanel.CEILING")+alt_ceil); 



		g2d.setColor(gui_bg_color);
		g2d.fillRect(0, 0, 100, 130+gui_text.size()*(fh+5));
		g2d.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER,1));
		g2d.setColor(gui_color);
		g2d.drawRect(40, 20, 20, 100);
		g2d.drawRect(37, 18+z_p, 26, 5);
		int start=125+fh;
		for(String s:gui_text)
		{
			fw=metrics.stringWidth(s);
			g2d.drawString(s, 50-fw/2, start);
			start+=5+fh;
		}
	}

	/**
	 * Main redraw procedure (run only when something changes).
	 * @param fast if true use only block images; if false also draw height map
	 */
	void redraw(boolean fast)
	{
		int win_w=getWidth();
		int win_h=getHeight();
		
		synchronized (main_img) {
			BufferedImage ckln=new BufferedImage(MAX_WIDTH, MAX_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D bg=base_img.createGraphics();	
			if(!fast)
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
					
					if(showchunks){
						ckln.createGraphics().drawLine(x, y, x+w, y);
						ckln.createGraphics().drawLine(x+w, y, x+w, y+h);
					}

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
			mg.drawImage(base_img,0,0,null);
			if(!fast)
			{
				mg.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER,(float) (0.6)));
				mg.drawImage(height_img,0,0,null);
			}
			if(showchunks)
				mg.drawImage(ckln, 0, 0, null);

		}
	}

	/**
	 * Draws a single chunk. Does not draw height map.
	 */
	private void redrawChunk(ChunkImage chunk)
	{
		int win_w=getWidth();
		int win_h=getHeight();

		int x=(int) ((chunk.x+shift_x)*zoom_level);
		int y=(int) ((chunk.y+shift_y)*zoom_level);
		int w=(int) (chunk.image.getWidth()*zoom_level);
		int h=(int) (chunk.image.getHeight()*zoom_level);

		if(x>win_w || y>win_h) return;
		if(x+w<0 || y+h<0) return;

		synchronized (main_img) {
			Graphics2D mg=main_img.createGraphics();
			mg.drawImage(chunk.image, x, y, w, h, null);
		}
	}
	
	/**
	 * Add a chunk image to the preview.
	 * @param img image of the individual blocks
	 * @param height height map
	 * @param x x location of the chunk on the screen
	 * @param y y location of the chunk on the screen
	 */
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
		redrawChunk(chunk);
	}

	/**
	 * Get the collection of chunks. Allows its manipulation by external loaders.
	 * @return collection of chunks
	 */
	public Vector<ChunkImage> getChunkImages()
	{
		return chunks;
	}

	/**
	 * Clears all the images in the preview.
	 */
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
	
	public void clearChunks(){
		chunks.clear();
		redraw(true);
		repaint();
	}

	/**
	 * Sets the offset.
	 * @param x x position
	 * @param z z position
	 */
	public void setPosition(int x, int z)
	{
		shift_x=getWidth()/2-(x*4);
		shift_y=getHeight()/2-(z*4);
	}

	/**
	 * Adds a marker to the map.
	 * @param x x position
	 * @param z z position
	 * @param color color of the marker
	 */
	public void addMarker(int x, int z, Color color)
	{
		MapMarker marker=new MapMarker();
		marker.x=x;
		marker.z=z;
		marker.color=color;
		markers.add(marker);
	}

	/**
	 * Retrieves the coordinate boundaries of chunks that are visible on the preview.
	 * Used by some loaders to determine which chunks to load. 
	 * @return bounds of drawn chunks
	 */
	public Rectangle getChunkBounds()
	{
		Rectangle ret=new Rectangle();

		ret.x=(-shift_x/64)-1;
		ret.y=(-shift_y/64)-1;
		ret.width=(int) Math.ceil((getWidth()/zoom_level)/64.0)+1;
		ret.height=(int) Math.ceil((getHeight()/zoom_level)/64.0)+1;

		return ret;
	}

	private int zoom_level_pos=11;
	private final float zoom_levels[]={0.015625f, 0.03125f, 0.0625f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 10.0f}; 

	/**
	 * Event fired when mouse is scrolled inside the preview. 
	 */
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

		if(!fastrendermode)
			redraw(false);
		else
			redraw(true);
		repaint();

	}

	enum CursorSelectionPosition
	{
		INSIDE,
		NE_CORNER,
		NW_CORNER,
		SE_CORNER,
		SW_CORNER,
		N_SIDE,
		E_SIDE,
		S_SIDE,
		W_SIDE,
		OUTSIDE
	}

	private CursorSelectionPosition getCursorSelectionPosition(int x, int y)
	{
		if(x>screen_sx+4 && x<screen_ex-4 && y>screen_sz+4 && y<screen_ez-4)
		{			
			return CursorSelectionPosition.INSIDE;
		}

		if(x>=screen_sx-4 && x<=screen_sx+4 && y>=screen_sz-4 && y<=screen_sz+4)
		{
			return CursorSelectionPosition.NW_CORNER;
		}

		if(x>=screen_ex-4 && x<=screen_ex+4 && y>=screen_sz-4 && y<=screen_sz+4)
		{
			return CursorSelectionPosition.NE_CORNER;
		}

		if(x>=screen_sx-4 && x<=screen_sx+4 && y>=screen_ez-4 && y<=screen_ez+4)
		{
			return CursorSelectionPosition.SW_CORNER;
		}

		if(x>=screen_ex-4 && x<=screen_ex+4 && y>=screen_ez-4 && y<=screen_ez+4)
		{
			return CursorSelectionPosition.SE_CORNER;
		}

		if(x>=screen_sx-4 && x<=screen_sx+4 && y>screen_sz+4 && y<screen_ez-4)
		{
			return CursorSelectionPosition.W_SIDE;
		}

		if(x>=screen_ex-4 && x<=screen_ex+4 && y>screen_sz+4 && y<screen_ez-4)
		{
			return CursorSelectionPosition.E_SIDE;
		}

		if(x>screen_sx+4 && x<screen_ex-4 && y>=screen_sz-4 && y<=screen_sz+4)
		{
			return CursorSelectionPosition.N_SIDE;
		}

		if(x>screen_sx+4 && x<screen_ex-4 && y>=screen_ez-4 && y<=screen_ez+4)
		{
			return CursorSelectionPosition.S_SIDE;
		}

		return CursorSelectionPosition.OUTSIDE;
	}

	
	private boolean isPerformingAction(MouseEvent e, int action_index)
	{		
		switch(action_index)
		{
		case 0:
			return e.getButton()==MouseEvent.BUTTON1;
		case 1:
			return e.getButton()==MouseEvent.BUTTON3;
		case 2:
			return e.getButton()==MouseEvent.BUTTON2;
		case 3:
			return e.getButton()==MouseEvent.BUTTON1 && ((e.getModifiersEx()&MouseEvent.SHIFT_DOWN_MASK)!=0);
		case 4:
			return e.getButton()==MouseEvent.BUTTON3 && ((e.getModifiersEx()&MouseEvent.SHIFT_DOWN_MASK)!=0);
		case 5:
			return e.getButton()==MouseEvent.BUTTON2 && ((e.getModifiersEx()&MouseEvent.SHIFT_DOWN_MASK)!=0);
		default:
				return false;
		}
	}

	private boolean selecting_area=false;
	private boolean moving_map=false;
	private boolean shaping_selection=false;
	private CursorSelectionPosition shaping_action;
	private int last_x,last_y;
	private int origin_x,origin_y;
	private int ssx,ssz,sex,sez;

	/**
	 * Event fired when mouse button is pressed down inside the preview. 
	 */
	@Override
	public void mousePressed(MouseEvent e) {

		int x=e.getX();
		int y=e.getY();

		if(isPerformingAction(e, MainWindow.settings.getSelectAction()))
		{
			selecting_area=true;

			shaping_action=getCursorSelectionPosition(x, y);
			if(shaping_action!=CursorSelectionPosition.OUTSIDE)
			{
				shaping_selection=true;
				origin_x=x;
				origin_y=y;
				ssx=selection_start_x;
				ssz=selection_start_z;
				sex=selection_end_x;
				sez=selection_end_z;				
				return;
			}

			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			selection_start_x=(int) Math.floor((e.getX()/zoom_level-shift_x)/4);
			selection_start_z=(int) Math.floor((e.getY()/zoom_level-shift_y)/4);
			
			if(selectchunks){
			
				if(selection_start_x % 16 != 0){
					selection_start_x = Math.round(selection_start_x / 16) * 16;
				}
				
				if(selection_start_z % 16 != 0){
					selection_start_z = Math.round(selection_start_z / 16) * 16;
				}
			
			}

			return;

		}

		if(isPerformingAction(e, MainWindow.settings.getMoveAction()))
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			last_x=e.getX();
			last_y=e.getY();			
			moving_map=true;

		}
	}

	/**
	 * Event fired when mouse button is released inside the preview. 
	 */
	@Override
	public void mouseReleased(MouseEvent e) {

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		if(selecting_area && !shaping_selection)
		{			
			selection_end_x=(int) Math.floor((e.getX()/zoom_level-shift_x)/4);
			selection_end_z=(int) Math.floor((e.getY()/zoom_level-shift_y)/4);
			int t;
			if(selection_end_x<selection_start_x)
			{
				t=selection_end_x;
				selection_end_x=selection_start_x;
				selection_start_x=t;
			}
			
			if(selection_end_z<selection_start_z)
			{
				t=selection_end_z;
				selection_end_z=selection_start_z;
				selection_start_z=t;
			}
		}
		
		if(selectchunks){
			if(selection_start_x % 16 != 0){
				selection_start_x = Math.round(selection_start_x / 16) * 16;
			}
			
			if(selection_start_z % 16 != 0){
				selection_start_z = Math.round(selection_start_z / 16) * 16;
			}
			
			if(selection_end_x % 16 != 0){
				selection_end_x = Math.round(selection_end_x / 16) * 16;
			}
			
			if(selection_end_z % 16 != 0){
				selection_end_z = Math.round(selection_end_z / 16) * 16;
			}
		}
		
		MainPanel.modelPos1X.setValue(selection_start_x);
		MainPanel.modelPos1Z.setValue(selection_start_z);
		MainPanel.modelPos2X.setValue(selection_end_x);
		MainPanel.modelPos2Z.setValue(selection_end_z);

		selecting_area=false;
		moving_map=false;
		shaping_selection=false;
		if(!fastrendermode)
			redraw(false);
		else
			redraw(true);
		repaint();
	}

	/**
	 * Event fired when mouse is moved with the button pressed inside the preview. 
	 */
	@Override
	public void mouseDragged(MouseEvent e) {

		int x=e.getX();
		int y=e.getY();

		if(selecting_area)
		{
			if(shaping_selection)
			{				
				int dx=(x-origin_x)/4;
				int dy=(y-origin_y)/4;


				if(shaping_action==CursorSelectionPosition.INSIDE 
						|| shaping_action==CursorSelectionPosition.W_SIDE 
						|| shaping_action==CursorSelectionPosition.NW_CORNER 
						|| shaping_action==CursorSelectionPosition.SW_CORNER)
					selection_start_x=(int) (ssx+dx/zoom_level);
				if(shaping_action==CursorSelectionPosition.INSIDE 
						|| shaping_action==CursorSelectionPosition.N_SIDE 
						|| shaping_action==CursorSelectionPosition.NW_CORNER 
						|| shaping_action==CursorSelectionPosition.NE_CORNER)
					selection_start_z=(int) (ssz+dy/zoom_level);
				if(shaping_action==CursorSelectionPosition.INSIDE 
						|| shaping_action==CursorSelectionPosition.E_SIDE 
						|| shaping_action==CursorSelectionPosition.NE_CORNER 
						|| shaping_action==CursorSelectionPosition.SE_CORNER)
					selection_end_x=(int) (sex+dx/zoom_level);
				if(shaping_action==CursorSelectionPosition.INSIDE 
						|| shaping_action==CursorSelectionPosition.S_SIDE 
						|| shaping_action==CursorSelectionPosition.SW_CORNER 
						|| shaping_action==CursorSelectionPosition.SE_CORNER)
					selection_end_z=(int) (sez+dy/zoom_level);												



				repaint();		
				return;
			}

			selection_end_x=(int) Math.floor((e.getX()/zoom_level-shift_x)/4);
			selection_end_z=(int) Math.floor((e.getY()/zoom_level-shift_y)/4);
			
			if(selectchunks){
				if(selection_start_x % 16 != 0){
					selection_start_x = Math.round(selection_start_x / 16) * 16;
				}
				
				if(selection_start_z % 16 != 0){
					selection_start_z = Math.round(selection_start_z / 16) * 16;
				}
				
				if(selection_end_x % 16 != 0){
					selection_end_x = Math.round(selection_end_x / 16) * 16;
				}
				
				if(selection_end_z % 16 != 0){
					selection_end_z = Math.round(selection_end_z / 16) * 16;
				}
			}
			
			repaint();		

			return;
		}	

		if(moving_map)
		{		
			shift_x+=(x-last_x)/zoom_level;
			shift_y+=(y-last_y)/zoom_level;

			last_x=x;
			last_y=y;

			redraw(true);
			repaint();		
		}
	}

	/**
	 * Unused.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {

		int x=e.getX();
		int y=e.getY();

		switch(getCursorSelectionPosition(x, y))
		{
		case INSIDE:
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			break;
		case NW_CORNER:
			setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			break;
		case NE_CORNER:
			setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
			break;
		case SW_CORNER:
			setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			break;
		case SE_CORNER:
			setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			break;
		case N_SIDE:
			setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			break;
		case W_SIDE:
			setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			break;
		case S_SIDE:
			setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
			break;
		case E_SIDE:
			setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			break;
		default:
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}
	/**
	 * Unused.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {	
	}
	/**
	 * Unused.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {		
	}
	/**
	 * Unused.
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Get the boundaries selected with the left mouse click.
	 * @return selection boundaries
	 */
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

	/**
	 * Sets the altitude ranges that are to be painted in the GUI.
	 * @param floor altitude floor
	 * @param ceil altitude ceiling
	 */
	public void setAltitudes(int floor, int ceil)
	{
		alt_ceil=ceil;
		alt_floor=floor;
	}
}
