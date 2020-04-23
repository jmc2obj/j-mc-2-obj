/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.jmc.Chunk;
import org.jmc.ChunkLoaderThread;
import org.jmc.Options;
import org.jmc.Region;
import org.jmc.gui.PreviewPanel.ChunkImage;
import org.jmc.threading.ThreadInputQueue;
import org.jmc.util.Hilbert.HilbertComparator;

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
	private File worldPath;
	/**
	 * Dimension id
	 */
	private int dimension;
	/**
	 * Collection of chunk images from the preview panel.
	 */
	private Vector<ChunkImage> chunkImages;

	/**
	 * Frequency of repainting in ms.
	 */
	private final int REPAINT_FREQUENCY=100;

	/**
	 * A collection of loaded chunk IDs.
	 */
	Set<Point> loadedChunks;
	
	// Chunks that we checked do not exist in this world.
	Set<Point> emptyChunks;
	
	private ThreadInputQueue chunkQueue;
	private AtomicInteger chunksToDo;

	/**
	 * Variables defining the Y-axis boundaries of the current preview. 
	 */
	private int floor, ceiling;
	private boolean yBoundsChanged;

	/**
	 * Main constructor.
	 * @param preview reference to the preview panel
	 * @param worldDir path to the world save
	 * @param dimension Dimension to load chunks from.
	 */
	public ViewChunkLoaderThread(PreviewPanel preview) {
		this.preview = preview;
		this.worldPath = Options.worldDir;
		this.dimension = Options.dimension;
		
		chunkImages = preview.getChunkImages();
		
		loadedChunks = Collections.synchronizedSet(new HashSet<Point>());
		emptyChunks = Collections.synchronizedSet(new HashSet<Point>());
		chunkQueue = new ThreadInputQueue();
		chunksToDo = new AtomicInteger();
		
		floor = 0;
		ceiling = Integer.MAX_VALUE;
		yBoundsChanged = false;
	}

	/**
	 * Main thread method.
	 */
	@Override
	public void run() {

		running = true;

		Rectangle prevBounds = new Rectangle();

		loadedChunks.clear();
		emptyChunks.clear();
		
		int threads = MainWindow.settings.getPreferences().getInt("PREVIEW_THREADS", 8);
		for (int i = 0; i < threads; i++) {
			Thread t = new Thread(new ChunkImager(chunkQueue));
			t.setName("ChunkImager - " + i);
			t.setPriority(Thread.MIN_PRIORITY);
			t.start();
		}

		while (running)
		{
			
			boolean drawn = false;
			while (prevBounds.equals(preview.getChunkBounds()) && !yBoundsChanged) {
				if (!running) {
					return;
				}
				if (chunksToDo.get() == 0 && !drawn) {
					preview.redraw(preview.fastrendermode);
					drawn = true;
				}
				preview.repaint();
				try {
					Thread.sleep(REPAINT_FREQUENCY);
				} catch (InterruptedException e) {}
			}

			Rectangle bounds = preview.getChunkBounds();
			boolean stopIter = false;
			chunkQueue.clear();
			chunksToDo = new AtomicInteger();
			
			int cxs = bounds.x;
			int czs = bounds.y;
			int cxe = bounds.x + bounds.width;
			int cze = bounds.y + bounds.height;

			if (yBoundsChanged)
			{
				yBoundsChanged = false;
				loadedChunks.clear();
				chunkImages.clear();
			}

			synchronized (chunkImages) {
				Iterator<ChunkImage> iter = chunkImages.iterator();
				while (iter.hasNext())
				{
					ChunkImage chunk_image = iter.next();

					int cx = chunk_image.x/64;
					int cz = chunk_image.y/64;

					if (cx<cxs || cx>cxe || cz<czs || cz>cze)
					{
						loadedChunks.remove(new Point(cx,cz));
						iter.remove();
					}

					Rectangle new_bounds = preview.getChunkBounds();
					if (!bounds.equals(new_bounds) || yBoundsChanged)
					{
						stopIter = true;
						break;
					}

					if (!running) return;
				}
			}

			preview.redraw(true);
			preview.repaint();
			

			ArrayList<Point> chunkList = new ArrayList<Point>();
			for (int cx=cxs; cx<=cxe && !stopIter; cx++)
			{
				for (int cz=czs; cz<=cze && !stopIter; cz++)
				{
					Point p = new Point(cx, cz);

					if (loadedChunks.contains(p) || emptyChunks.contains(p))
						continue;
					chunkList.add(p);
				}
			}
			
			chunkList.sort(new HilbertComparator(8));
			
			for (Point p : chunkList) {
				Rectangle new_bounds=preview.getChunkBounds();
				if (!bounds.equals(new_bounds) || yBoundsChanged)
					stopIter = true;
				
				if (stopIter) break;
				
				chunksToDo.addAndGet(1);
				chunkQueue.add(p);
			
				if (!running) return;
			}

			prevBounds = bounds;
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
		chunkQueue.finish();
		running=false;
	}

	/**
	 * Change the Y-axis bounds needed for drawing. 
	 * @param floor
	 * @param ceiling
	 */
	public void setYBounds(int floor, int ceiling)
	{
		this.floor=floor;
		this.ceiling=ceiling;
		yBoundsChanged=true;
	}
	
	private class ChunkImager implements Runnable {
		private ThreadInputQueue queue;
		
		ChunkImager(ThreadInputQueue chunkQueue) {
			this.queue = chunkQueue;
		}
		
		@Override
		public void run() {
			while (running) {
				Point p;
				AtomicInteger ctd;
				try {
					p = queue.getNext();
					ctd = chunksToDo;
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
				if (p == null)
					break;
				loadedChunks.add(p);
				Chunk chunk;
				Region region;
				try {
					region = Region.findRegion(worldPath, dimension, p.x, p.y);
					chunk = region.getChunk(p.x, p.y);
				} catch (Exception e) {
					emptyChunks.add(p);
					ctd.addAndGet(-1);
					continue;
				}
			
				if (chunk == null) {
					ctd.addAndGet(-1);
					continue;
				}
				
				chunk.renderImages(floor,ceiling,preview.fastrendermode);
				BufferedImage heightImg = null;
				if (!preview.fastrendermode)
					heightImg=chunk.getHeightImage();
				BufferedImage img=chunk.getBlockImage();
			
				preview.addImage(img, heightImg, p.x*64, p.y*64);
				ctd.addAndGet(-1);
			}
		}
		
	}

}
