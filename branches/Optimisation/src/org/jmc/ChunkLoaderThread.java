/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;
/**
 * Chunk loader interface. Defines minimum methods required for a chunk loading thread.
 * @author danijel
 *
 */
public interface ChunkLoaderThread extends Runnable {

	/**
	 * Is the thread still running?
	 * @return if the thread is still running
	 */
	public boolean isRunning();
	/**
	 * Stops the thread.
	 */
	public void stopRunning();
	/**
	 * Changes the Y-axis boundaries.
	 * @param floor
	 * @param ceiling
	 */
	public void setYBounds(int floor, int ceiling);
}
