package org.jmc.threading;

import java.awt.Point;
import java.util.ArrayList;

import org.jmc.ChunkDataBuffer;
import org.jmc.geom.FaceUtils.Face;
import org.jmc.threading.ThreadObjOutputQueue.ChunkOutput;
import org.jmc.util.Log;

public class ObjReaderRunnable implements Runnable {
	private ChunkDataBuffer chunkBuffer;
	private ThreadChunkDeligate chunkDeligate;
	private Point chunkStart;
	private Point chunkEnd;
	private ThreadInputQueue inputQueue;
	private ThreadObjOutputQueue outputQueue;
	
	public ObjReaderRunnable(ChunkDataBuffer chunk_buffer, Point chunkStart, Point chunkEnd, ThreadInputQueue inQueue, ThreadObjOutputQueue outQueue) {
		super();
		this.chunkBuffer = chunk_buffer;
		this.chunkDeligate = new ThreadChunkDeligate(chunk_buffer);
		this.chunkStart = chunkStart;
		this.chunkEnd = chunkEnd;
		this.inputQueue = inQueue;
		this.outputQueue = outQueue;
	}

	@Override
	public void run() {
		Point chunkCoord;
		while (!Thread.interrupted()) {
			try {
				chunkCoord = inputQueue.getNext();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			if (chunkCoord == null) {
				break;
			}
			ChunkOutput output = exportChunk(chunkCoord);
			if (output == null){
				continue;
			}
			try {
				outputQueue.put(output);
			} catch (InterruptedException e) {
				Log.debug(String.format("Reader %s interrupted!", Thread.currentThread().getName()));
				break;
			}
		}
	}
	
	private ChunkOutput exportChunk(Point chunkCoord){
		int chunkX = chunkCoord.x;
		int chunkZ = chunkCoord.y;
		
		chunkDeligate.setCurrentChunk(chunkCoord);

		// export the chunk to the OBJ
		ObjChunkProcessor proc = new ObjChunkProcessor();
		ArrayList<Face> faces = proc.process(chunkDeligate, chunkX, chunkZ);
		
		ChunkOutput output = new ChunkOutput(chunkCoord, faces);
		return output;
	}
}
