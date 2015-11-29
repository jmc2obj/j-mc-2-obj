package org.jmc.threading;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.jmc.geom.FaceUtils.Face;

public class ThreadOutputQueue {
	private Queue<ChunkOutput> outputQueue = new LinkedList<ChunkOutput>();
	private boolean finished = false;
	
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
	
	public synchronized void add(ChunkOutput outChunk){
		outputQueue.add(outChunk);
		notify();
	}

	public synchronized ChunkOutput getNext() throws InterruptedException {
		while (true) {
			if (!outputQueue.isEmpty()) {
				return outputQueue.remove();
			} else if (finished) {
				return null;
			}
			wait();
		}
	}
	
	public synchronized void finish(){
		finished = true;
		notifyAll();
	}
}
