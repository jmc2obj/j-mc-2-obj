/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;
/**
 * Chunk loader that loads all the chunks for the chosen save.
 * @author danijel
 *
 */
public class FullChunkLoaderThread implements ChunkLoaderThread {

	/**
	 * Reference to the preview panel used for image refreshing.
	 */
	private PreviewPanel preview;
	/**
	 * Path to save file.
	 */
	private File savepath;
	
	/**
	 * Frequency of repainting in ms.
	 */
	private final int REPAINT_FREQUENCY=100;
	
	/**
	 * Variable used by isRunning and stopRunning.  
	 */
	private boolean running;
	
	/**
	 * Constructor.
	 * @param preview reference to preview panel
	 * @param savepath path to the save
	 */
	public FullChunkLoaderThread(PreviewPanel preview, File savepath) {
		this.preview=preview;
		this.savepath=savepath;
	}
	
	/**
	 * Main thread method.
	 */
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

		long last_time=System.currentTimeMillis(),this_time;
		for(Region region:regions)
		{
			for(Chunk chunk:region)
			{											
				if(chunk==null)
				{
					MainWindow.log("Chunk couldn't be loaded.");
					return;
				}

				chunk.renderImages();
				BufferedImage height_img=chunk.getHeightImage();
				BufferedImage img=chunk.getBlockImage();										

				int ix=chunk.getPosX();
				int iy=chunk.getPosZ();
				
				preview.addImage(img, height_img, ix*64, iy*64);
				
				this_time=System.currentTimeMillis();
				if(this_time-last_time>REPAINT_FREQUENCY)
				{
					preview.repaint();
					last_time=this_time;
				}
				
				if(!running) return;
			}

		}
		
		preview.redraw(false);
		preview.repaint();
		
		running=false;
		
	}

	/**
	 * Implementation of interface method.
	 */
	@Override
	public boolean isRunning() {
		return running;
	}

	/**
	 * Implementation of interface method.
	 */
	@Override
	public void stopRunning() {
		running=false;		
	}

}
