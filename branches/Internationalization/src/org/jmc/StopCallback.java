package org.jmc;


/**
 * Simple callback to check if a long-running operation should be stopped.
 */
public interface StopCallback
{
	/**
	 * Indicates to the caller whether a stop was requested.
	 * @return true if the operation should stop, false otherwise.
	 */
	public boolean stopRequested();
}
