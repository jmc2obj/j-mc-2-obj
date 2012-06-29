package org.jmc;


/**
 * Simple callback to indicate progress of an operation.
 */
public interface ProgressCallback
{
	/**
	 * Sets the current level of progress.
	 * @param value Progress, in the interval [0,1]
	 */
	public void setProgress(float value);
}
