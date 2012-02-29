package org.jmc;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;



@SuppressWarnings("serial")
public class PreviewPanel extends JPanel implements MouseMotionListener, MouseWheelListener, MouseInputListener {

	private final int MAX_WIDTH=1920;
	private final int MAX_HEIGHT=1080;

	private int selected_chunk_x=0, selected_chunk_z=0;
	private int shift_x,shift_y;
	private float zoom_level;

	private BufferedImage main_img,base_img,height_img;

	private Font gui_font;
	private Color gui_color;

	class ChunkImage
	{
		public BufferedImage image;
		public BufferedImage height_map;
		public int x, y;
	}

	class MapMarker
	{
		int x, y;
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

		setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

		gui_font=new Font("Courier",Font.BOLD,14);
		gui_color=new Color(150,145,215);

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
			int x=(int) ((shift_x+marker.x)*zoom_level);
			int y=(int) ((shift_y+marker.y)*zoom_level);
			g2d.setColor(marker.color);
			g2d.fillOval(x-2, y-2, 4, 4);
		}

		int z_p=100-(zoom_level_pos*100/(zoom_levels.length-1));

		int mx=getWidth()/2;
		int my=getHeight()/2;
		int px=(int) ((mx/zoom_level-shift_x)/4);
		int py=(int) ((my/zoom_level-shift_y)/4);

		g2d.setColor(gui_color);
		g2d.drawRect(20, 20, 20, 100);
		g2d.drawRect(17, 18+z_p, 26, 5);
		g2d.drawString(zoom_level+"x", 20, 135);
		g2d.drawString("("+px+","+py+")", 20, 155);

	}

	void redraw(boolean fast)
	{

		int win_w=getWidth();
		int win_h=getHeight();

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


				if(chunk.x/64==selected_chunk_x && chunk.y/64==selected_chunk_z)
				{
					bg.setColor(Color.red);
					bg.drawRect(x, y, w-1, h-1);
				}
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

		synchronized (main_img) {

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
		shift_x=getWidth()/2-x;
		shift_y=getHeight()/2-z;
	}

	public void addMarker(int x, int z, Color color)
	{
		MapMarker marker=new MapMarker();
		marker.x=x;
		marker.y=z;
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

		zoom_level=zoom_levels[zoom_level_pos];

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
			last_x=e.getX();
			last_y=e.getY();
			left_pressed=true;
			//			System.out.println("SET "+last_x+"/"+last_y);
		}

		if(e.getButton()==MouseEvent.BUTTON3)
		{
			right_pressed=true;
			selected_chunk_x=(int) Math.floor((e.getX()/zoom_level-shift_x)/64);
			selected_chunk_z=(int) Math.floor((e.getY()/zoom_level-shift_y)/64);
			redraw(true);
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		left_pressed=false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {		

		if(left_pressed)
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

	public int getSelectedChunkX() { return selected_chunk_x; }
	public int getSelectedChunkZ() { return selected_chunk_z; }
}
