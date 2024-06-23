/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jmc.*;
import org.jmc.geom.BlockPos;
import org.jmc.gui.PreviewPanel.ChunkImage;
import org.jmc.models.None;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ThreadInputQueue;
import org.jmc.util.Hilbert.HilbertComparator;

/**
 * Chunk loader that loads only the chunks visible on the screen and
 * removes the chunks that go off screen. 
 * @author danijel
 *
 */
public class ViewChunkLoaderRunner implements ChunkLoaderRunner {

	/**
	 * Reference to preview panel so we can change the preview.
	 */
	private final PreviewPanel preview;
	/**
	 * Path to world save.
	 */
	private final File worldPath;
	/**
	 * Dimension id
	 */
	private final NamespaceID dimension;
	/**
	 * Collection of chunk images from the preview panel.
	 */
	private final Vector<ChunkImage> chunkImages;

	/**
	 * Frequency of repainting in ms.
	 */
	private final int REPAINT_FREQUENCY=100;

	/**
	 * A collection of loaded chunk IDs.
	 */
	Set<Point> loadedChunks;
	
	private final ThreadInputQueue chunkQueue;
	private AtomicInteger chunksToDo;

	private final AtomicBoolean paused = new AtomicBoolean(false);

	/**
	 * Variables defining the Y-axis boundaries of the current preview. 
	 */
	private int viewFloor, viewCeiling;
	private boolean yBoundsChanged;
	
	private final ArrayList<Thread> imagerThreads = new ArrayList<Thread>();
	
	private final ChunkDataBuffer chunkBuffer = new ChunkDataBuffer(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);

	/**
	 * Main constructor.
	 * @param preview reference to the preview panel
	 */
	public ViewChunkLoaderRunner(PreviewPanel preview, File worldPath, NamespaceID dimension) {
		this.preview = preview;
		this.worldPath = worldPath;
		this.dimension = dimension;
		
		chunkImages = preview.getChunkImages();
		
		loadedChunks = Collections.synchronizedSet(new HashSet<Point>());
		chunkQueue = new ThreadInputQueue();
		chunksToDo = new AtomicInteger();
		
		viewFloor = Integer.MIN_VALUE;
		viewCeiling = Integer.MAX_VALUE;
		yBoundsChanged = false;
	}

	/**
	 * Main thread method.
	 */
	@Override
	public void run() {

		Rectangle prevBounds = new Rectangle();

		loadedChunks.clear();
		
		int threads = MainWindow.settings.getPreferences().getInt("PREVIEW_THREADS", 8);
		for (int i = 0; i < threads; i++) {
			Thread t = new Thread(new ChunkImager(chunkQueue));
			imagerThreads.add(t);
			t.setName("ChunkImager - " + i);
			t.setPriority(Thread.MIN_PRIORITY);
			t.start();
		}

		try {
			while (!Thread.interrupted())
			{
				
				boolean drawn = false;
				while (prevBounds.equals(preview.getChunkBounds()) && !yBoundsChanged) {
					if (Thread.interrupted()) {
						return;
					}
					if (chunksToDo.get() == 0 && !drawn) {
						preview.redraw(preview.fastrendermode);
						drawn = true;
					}
					preview.repaint();
					try {
						Thread.sleep(REPAINT_FREQUENCY);
						waitIfPaused();
					} catch (InterruptedException e) {
						return;
					}
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
						
						int cx = chunk_image.x;
						int cz = chunk_image.y;
						
						if ((cx<cxs || cx>cxe || cz<czs || cz>cze) && !preview.keepChunks) {
							loadedChunks.remove(new Point(cx,cz));
							iter.remove();
						}
						
						Rectangle new_bounds = preview.getChunkBounds();
						if (!bounds.equals(new_bounds) || yBoundsChanged)
						{
							stopIter = true;
							break;
						}
						
						if (Thread.interrupted()) return;
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
						
						if (loadedChunks.contains(p))
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
					
					if (Thread.interrupted()) return;
				}
				
				prevBounds = bounds;
			}
		} finally {
			for (Thread t : imagerThreads) {
				t.interrupt();
				try {
					t.join();
				} catch (InterruptedException ignored) {}
			}
			imagerThreads.clear();
		}

	}

	/**
	 * Change the Y-axis bounds needed for drawing. 
	 * @param floor
	 * @param ceiling
	 */
	@Override
	public void setYBounds(int floor, int ceiling)
	{
		this.viewFloor = floor;
		this.viewCeiling = ceiling;
		yBoundsChanged = true;
	}

	@Override
	public void pause(boolean p) {
		paused.set(p);
		if (!p) {
			synchronized (paused) {
				paused.notifyAll();
			}
		}
	}

	private void waitIfPaused() throws InterruptedException {
		if (paused.get()) {
			synchronized (paused) {
				while (paused.get()) {
					paused.wait();
				}
			}
		}
	}
	
	private class ChunkImager implements Runnable {
		private final ThreadInputQueue queue;
		
		ChunkImager(ThreadInputQueue chunkQueue) {
			this.queue = chunkQueue;
		}
		
		@Override
		public void run() {
			while (!Thread.interrupted()) {
				Point p;
				AtomicInteger ctd;
				try {
					waitIfPaused();
					p = queue.getNext();
					ctd = chunksToDo;
				} catch (InterruptedException e) {
					break;
				}
				if (p == null)
					break;
				loadedChunks.add(p);
				
				Chunk.Blocks blocks = chunkBuffer.getBlocks(p);
				
				if (blocks == null) {
					ctd.addAndGet(-1);
					continue;
				}
				
				int width = 4 * 16;
				int height = 4 * 16;
				BufferedImage blockImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				BufferedImage heightImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
				Graphics2D blockGraphics = blockImage.createGraphics();
				blockGraphics.setColor(Color.black);
				blockGraphics.fillRect(0, 0, width, height);
				
				Graphics2D heightGraphics = heightImage.createGraphics();
				heightGraphics.setColor(Color.black);
				heightGraphics.fillRect(0, 0, width, height);
				
				BlockDataPos[] topBlocks = renderImages(blocks, blockGraphics, heightGraphics);
				if (topBlocks == null) {
					ctd.addAndGet(-1);
					continue;
				}
				
				preview.addImage(blockImage, heightImage, p.x, p.y, topBlocks);
				ctd.addAndGet(-1);
			}
		}
		
		/**
		 * Renders the block and height images.
		 */
		private BlockDataPos[] renderImages(Chunk.Blocks bd, Graphics2D block_graphics, Graphics2D height_graphics) {
			
			int chunkFloor = viewFloor;
			int chunkCeiling = viewCeiling;
			if (chunkFloor > bd.ymax || chunkCeiling < bd.ymin)
				return null;
			if (chunkFloor < bd.ymin)
				chunkFloor = bd.ymin;
			if (chunkCeiling > bd.ymax + 1)
				chunkCeiling = bd.ymax + 1;
			if(chunkFloor >= chunkCeiling)
				chunkFloor = chunkCeiling - 1;
			
			
			BlockDataPos[] topBlocks = new BlockDataPos[16*16];
			
			for(int z = 0; z < 16; z++) {
				for(int x = 0; x < 16; x++) {
					for(int y = chunkCeiling; y >= chunkFloor; y--) {
						NamespaceID blockBiome = bd.getBiome(x, y, z);
						BlockData blockData = bd.getBlockData(x, y, z);
						
						if(blockData != null && !blockData.getInfo().getOcclusion().equals(BlockInfo.Occlusion.NONE)) {
							topBlocks[z*16+x] = new BlockDataPos(new BlockPos(x, y, z), blockData, blockBiome);
							
							BlockInfo type = blockData.getInfo();
							if (type.getModel().getClass() != None.class) {
								block_graphics.setColor(type.getPreviewColor(blockData, blockBiome));
								block_graphics.fillRect(x*4, z*4, 4, 4);
							}
							
							if(!preview.fastrendermode) {
								float a = y - viewFloor;
								float b = viewCeiling - viewFloor;
								float r = Math.max(0, Math.min(1, a / b));
								height_graphics.setColor(new Color(r, r, r));
								height_graphics.fillRect(x * 4, z * 4, 4, 4);
							}
							
							break;
						}
					}
					if (Thread.currentThread().isInterrupted())
						return null;
				}
			}
			
			return topBlocks;
		}
	}

}
