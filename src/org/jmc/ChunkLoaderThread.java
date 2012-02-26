package org.jmc;

public interface ChunkLoaderThread extends Runnable {

	public boolean isRunning();
	public void stopRunning();
}
