package org.jmc.threading;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jmc.geom.FaceUtils.Face;

public class ThreadOutputQueue{
	private final BlockingQueue<ChunkOutput> outputQueue;
	
	public static class ChunkOutput {
		private Point chunkCoord;
		private ArrayList<Face> faces;
		
		public ChunkOutput(Point chunkCoord, ArrayList<Face> faces) {
			this.chunkCoord = chunkCoord;
			this.faces = faces;
		}
		
		public Point getChunkCoord() {
			return chunkCoord;
		}

		public ArrayList<Face> getFaces() {
			return faces;
		}
	}
	
	public ThreadOutputQueue(int queueSize) {
		outputQueue = new LinkedBlockingQueue<ChunkOutput>(queueSize);
	}
	
	/**
	 * Calls {@link BlockingQueue#put()}
	 * @param outChunk the {@link ChunkOutput chunk} to put in the queue
	 * @throws InterruptedException
	 */
	public void put(ChunkOutput outChunk) throws InterruptedException {
		outputQueue.put(outChunk);
	}
	
	/**
	 * Calls {@link BlockingQueue#take()} and notifies {@link #waitUntilEmpty()}
	 * @throws InterruptedException
	 */
	public ChunkOutput take() throws InterruptedException {
		ChunkOutput outChunk = outputQueue.take();
		synchronized (this) {
			notifyAll();
		}
		return outChunk;
	}
	
	
	/**
	 * Waits on this until take is called and queue is emptied
	 * @throws InterruptedException
	 */
	public synchronized void waitUntilEmpty() throws InterruptedException {
		while (!outputQueue.isEmpty()) {
			wait();
		}
	}
}
