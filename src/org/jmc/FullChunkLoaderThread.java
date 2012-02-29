package org.jmc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;

public class FullChunkLoaderThread implements ChunkLoaderThread {

	private PreviewPanel preview;
	private File savepath;
	
	private final int REPAINT_FREQUENCY=4;
	
	boolean running;
	
	public FullChunkLoaderThread(PreviewPanel preview, File savepath) {
		this.preview=preview;
		this.savepath=savepath;
	}
	
	@Override
	public void run() {
	
		running=true;
		
		Vector<Region> regions=null;
		try {
			regions=Region.loadAllRegions(savepath);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Couldn't load regions: "+e1);
			return;
		}				

		int repaint_counter=REPAINT_FREQUENCY;
		for(Region region:regions)
		{
			for(Chunk chunk:region)
			{											
				if(chunk==null)
				{
					MainWindow.log("Chunk couldn't be loaded.");
					return;
				}

				BufferedImage height_img=chunk.getHeightImage();
				BufferedImage img=chunk.getBlockImage();										

				int ix=chunk.getPosX();
				int iy=chunk.getPosZ();
				
				preview.addImage(img, height_img, ix*64, iy*64);
				
				repaint_counter--;
				if(repaint_counter<=0)
				{
					preview.repaint();
					repaint_counter=REPAINT_FREQUENCY;
				}
				
				if(!running) return;
			}

		}
		
		running=false;
		
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void stopRunning() {
		running=false;		
	}

}
