package org.jmc.threading;

import java.awt.Point;
import java.util.ArrayList;

import org.jmc.Chunk;
import org.jmc.ChunkDataBuffer;
import org.jmc.Options;
import org.jmc.Region;
import org.jmc.StopCallback;
import org.jmc.geom.FaceUtils.Face;
import org.jmc.threading.ThreadOutputQueue.ChunkOutput;

public class ReaderRunnable implements Runnable {
	private ChunkDataBuffer chunkBuffer;
	private ThreadChunkDeligate chunkDeligate;
	private Point chunkStart;
	private Point chunkEnd;
	private ThreadInputQueue inputQueue;
	private ThreadOutputQueue outputQueue;
	private StopCallback stop;
	
	public ReaderRunnable(ChunkDataBuffer chunk_buffer, Point chunkStart, Point chunkEnd, ThreadInputQueue inQueue, ThreadOutputQueue outQueue, StopCallback stop) {
		super();
		this.chunkBuffer = chunk_buffer;
		this.chunkDeligate = new ThreadChunkDeligate(chunk_buffer);
		this.chunkStart = chunkStart;
		this.chunkEnd = chunkEnd;
		this.inputQueue = inQueue;
		this.outputQueue = outQueue;
		this.stop = stop;
	}

	@Override
	public void run() {
		Point chunkCoord;
		while (true) {
			if (stop != null && stop.stopRequested())
				break;
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
			outputQueue.add(output);
		}
	}
	
	private ChunkOutput exportChunk(Point chunkCoord){
		int chunkX = chunkCoord.x;
		int chunkZ = chunkCoord.y;

		// load chunk being processed to the buffer
		if (!addChunkIfExists(chunkBuffer, chunkX, chunkZ))
			return null;

		// also load chunks from x-1 to x+1 and z-1 to z+1
		for (int lx = chunkX - 1; lx <= chunkX + 1; lx++) {
			for (int lz = chunkZ - 1; lz <= chunkZ + 1; lz++) {
				if (lx < chunkStart.x || lx > chunkEnd.x || lz < chunkStart.y || lz > chunkEnd.y)
					continue;

				if (lx == chunkX && lz == chunkZ)
					continue;

				addChunkIfExists(chunkBuffer, lx, lz);
			}
		}
		
		chunkDeligate.setCurrentChunk(new Point(chunkX, chunkZ));

		// export the chunk to the OBJ
		ChunkProcessor proc = new ChunkProcessor();
		ArrayList<Face> faces = proc.process(chunkDeligate, chunkX, chunkZ);
		
		// remove the chunks we won't need anymore from the buffer 
		for (int lx = chunkX - 1; lx <= chunkX + 1; lx++) {
			for (int lz = chunkZ - 1; lz <= chunkZ + 1; lz++) {
				chunkBuffer.removeChunk(lx, lz);
			}
		}
		
		ChunkOutput output = new ChunkOutput(chunkCoord, faces);
		return output;
	}
	
	private static boolean addChunkIfExists(ChunkDataBuffer chunk_buffer, int x, int z) {
		if (chunk_buffer.hasChunk(x, z))
			return true;

		try {
			Region region = Region.findRegion(Options.worldDir, Options.dimension, x, z);
			if (region == null)
				return false;

			Chunk chunk = region.getChunk(x, z);
			if (chunk == null)
				return false;

			chunk_buffer.addChunk(chunk);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
