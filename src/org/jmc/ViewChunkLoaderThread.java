/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.jmc.PreviewPanel.ChunkImage;

/**
 * Chunk loader that loads only the chunks visible on the screen and
 * removes the chunks that go off screen. 
 * @author danijel
 *
 */
public class ViewChunkLoaderThread implements ChunkLoaderThread {

	/**
	 * Used by isRunning and stopRunning methods.
	 */
	private boolean running;

	/**
	 * Reference to preview panel so we can change the preview.
	 */
	private PreviewPanel preview;
	/**
	 * Path to world save.
	 */
	private File savepath;
	/**
	 * Collection of chunk images from the preview panel.
	 */
	private Vector<ChunkImage> chunk_images;

	/**
	 * Frequency of repainting in ms.
	 */
	private final int REPAINT_FREQUENCY=100;

	/**
	 * Maximum number of chunks loaded.
	 */
	public final int MAX_CHUNK_NUM=32768;

	/**
	 * A collection of loaded chunk IDs.
	 */
	Set<Integer> loaded_chunks;

	/**
	 * Variables defining the Y-axis boundaries of the current preview. 
	 */
	private int floor, ceiling;
	private boolean y_bounds_changed;
	private Object y_bounds_sync;

	/**
	 * Main constructor.
	 * @param preview reference to the preview panel
	 * @param savepath path to the world save
	 */
	public ViewChunkLoaderThread(PreviewPanel preview, File savepath) {
		this.preview=preview;
		this.savepath=savepath;

		chunk_images=preview.getChunkImages();

		loaded_chunks=new HashSet<Integer>();
		floor=0;
		ceiling=Integer.MAX_VALUE;
		y_bounds_changed=false;
		y_bounds_sync=new Object();
	}

	/**
	 * Main thread method.
	 */
	@Override
	public void run() {		

		running=true;

		Region region=null;
		Chunk chunk=null;

		Rectangle prev_bounds=new Rectangle();

		long last_time=System.currentTimeMillis(),this_time;

		loaded_chunks.clear();

		while(running)
		{
			Rectangle bounds=preview.getChunkBounds();
			int floor,ceiling;
			synchronized (y_bounds_sync) {
				floor=this.floor;
				ceiling=this.ceiling;				
			}

			boolean stop_iter=false;
			if(!bounds.equals(prev_bounds) || y_bounds_changed)
			{
				int cxs=bounds.x;
				int czs=bounds.y;
				int cxe=bounds.x+bounds.width;
				int cze=bounds.y+bounds.height;

				if(y_bounds_changed)
				{
					y_bounds_changed=false;
					loaded_chunks.clear();
					chunk_images.clear();
				}

				Iterator<ChunkImage> iter=chunk_images.iterator();
				while(iter.hasNext())
				{
					ChunkImage chunk_image=iter.next();

					int cx=chunk_image.x/64;
					int cz=chunk_image.y/64;

					if(cx<cxs || cx>cxe || cz<czs || cz>cze)
					{
						loaded_chunks.remove(cx*MAX_CHUNK_NUM+cz);
						iter.remove();
					}

					Rectangle new_bounds=preview.getChunkBounds();
					if(!bounds.equals(new_bounds) || y_bounds_changed)
					{						
						stop_iter=true;
						break;
					}

					if(!running) return;
				}	

				preview.redraw(true);			

				for(int cx=cxs; cx<=cxe && !stop_iter; cx++)
				{
					for(int cz=czs; cz<=cze && !stop_iter; cz++)
					{										
						if(loaded_chunks.contains(cx*MAX_CHUNK_NUM+cz)) continue;

						try {
							region=Region.findRegion(savepath, cx, cz);
							chunk=region.getChunk(cx, cz);
						} catch (Exception e) {
							continue;
						}

						if(chunk==null) continue;					


						int ix=chunk.getPosX();
						int iy=chunk.getPosZ();

						chunk.renderImages(floor,ceiling);
						BufferedImage height_img=chunk.getHeightImage();
						BufferedImage img=chunk.getBlockImage();											

						preview.addImage(img, height_img, ix*64, iy*64);
						loaded_chunks.add(cx*MAX_CHUNK_NUM+cz);			


						this_time=System.currentTimeMillis();
						if(this_time-last_time>REPAINT_FREQUENCY)
						{
							preview.repaint();
							last_time=this_time;
						}

						Rectangle new_bounds=preview.getChunkBounds();
						if(!bounds.equals(new_bounds) || y_bounds_changed)
							stop_iter=true;

						if(!running) return;
					}
				}			

				preview.redraw(false);
				preview.repaint();
			}

			prev_bounds=bounds;

			if(!stop_iter)
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
			}
		}

	}

	/**
	 * Interface override.
	 */
	@Override
	public boolean isRunning() {
		return running;
	}

	/**
	 * Interface override.
	 */
	@Override
	public void stopRunning() {
		running=false;

	}

	/**
	 * Change the Y-axis bounds needed for drawing. 
	 * @param floor
	 * @param ceiling
	 */
	public void setYBounds(int floor, int ceiling)
	{
		synchronized (y_bounds_sync) 
		{
			this.floor=floor;
			this.ceiling=ceiling;	
			y_bounds_changed=true;
		}		
	}

}
