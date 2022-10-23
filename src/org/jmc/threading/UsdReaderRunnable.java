package org.jmc.threading;

import org.jmc.BlockDataPos;
import org.jmc.ChunkDataBuffer;
import org.jmc.util.Log;

import java.awt.*;
import java.util.ArrayList;

public class UsdReaderRunnable implements Runnable {
	private ChunkDataBuffer chunkBuffer;
	private ThreadChunkDeligate chunkDeligate;
	private Point chunkStart;
	private Point chunkEnd;
	private ThreadInputQueue inputQueue;
	private ThreadUsdOutputQueue outputQueue;
	
	public UsdReaderRunnable(ChunkDataBuffer chunk_buffer, Point chunkStart, Point chunkEnd, ThreadInputQueue inQueue, ThreadUsdOutputQueue outQueue) {
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
			ThreadUsdOutputQueue.ChunkOutput output = exportChunk(chunkCoord);
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
	
	private ThreadUsdOutputQueue.ChunkOutput exportChunk(Point chunkCoord){
		
		chunkDeligate.setCurrentChunk(chunkCoord);

		// export the chunk to the OBJ
		UsdChunkProcessor proc = new UsdChunkProcessor();
		return proc.process(chunkDeligate, chunkCoord);
	}
}
