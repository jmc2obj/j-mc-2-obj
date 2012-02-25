package org.jmc;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;



@SuppressWarnings("serial")
public class PreviewPanel extends JPanel implements MouseMotionListener, MouseWheelListener, MouseInputListener {

	private final int MAX_WIDTH=1920;
	private final int MAX_HEIGHT=1080;
	
	private int shift_x,shift_y;
	private float zoom_level;

	private BufferedImage main_img;
	
	class ChunkImage
	{
		public BufferedImage image;
		public int x, y;
	}
	
	Vector<ChunkImage> chunks;

	public PreviewPanel() {
		
		main_img=new BufferedImage(MAX_WIDTH, MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);

		setMaximumSize(new Dimension(MAX_WIDTH,MAX_HEIGHT));
		
		chunks=new Vector<ChunkImage>();
		
		shift_x=0;
		shift_y=0;
		zoom_level=1;
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
	}

	@Override
	public void paint(Graphics g) {
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(main_img, 0, 0, null);
	}

	void redraw()
	{
		int win_w=getWidth();
		int win_h=getHeight();
		
		Graphics2D g=main_img.createGraphics();	
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setColor(Color.black);
		g.clearRect(0, 0, main_img.getWidth(), main_img.getHeight());
		for(ChunkImage chunk:chunks)
		{
			int x=(int) ((chunk.x+shift_x)*zoom_level);
			int y=(int) ((chunk.y+shift_y)*zoom_level);
			int w=(int) (chunk.image.getWidth()*zoom_level);
			int h=(int) (chunk.image.getHeight()*zoom_level);
			
			if(x>win_w || y>win_h) continue;
			if(x+w<0 || y+h<0) continue;
			
			g.drawImage(chunk.image, x, y, w, h, null);
		}		
	}

	void addImage(BufferedImage img, int x, int y)
	{
		ChunkImage chunk=new ChunkImage();
		chunk.image=img;
		chunk.x=x;
		chunk.y=y;		
		chunks.add(chunk);
		redraw();
	}
	
	void clearImages()
	{
		chunks.clear();
		redraw();
		
		shift_x=0;
		shift_y=0;
		zoom_level_pos=7;
		zoom_level=1;

	}

	private int zoom_level_pos=7;
	private final float zoom_levels[]={0.1f, 0.25f, 0.35f, 0.5f, 0.65f, 0.75f, 0.85f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f}; 
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int z=e.getWheelRotation();
		zoom_level_pos-=z;
		if(zoom_level_pos<0) zoom_level_pos=0;
		if(zoom_level_pos>=zoom_levels.length) zoom_level_pos=zoom_levels.length-1;
		
		zoom_level=zoom_levels[zoom_level_pos];
				
		redraw();
		repaint();
			
	}

	
	private boolean pressed=false;
	private int last_x,last_y;
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1)
		{
			last_x=e.getX();
			last_y=e.getY();
			pressed=true;
//			System.out.println("SET "+last_x+"/"+last_y);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		pressed=false;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {		
		
		if(pressed)
		{
			int x,y;
			
			x=e.getX();
			y=e.getY();
			
			shift_x+=(x-last_x)/zoom_level;
			shift_y+=(y-last_y)/zoom_level;
			
			last_x=x;
			last_y=y;
			
			redraw();
			repaint();
			
			//System.out.println("DRAG "+last_x+"/"+last_y+" -> "+shift_x+"/"+shift_y);
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
