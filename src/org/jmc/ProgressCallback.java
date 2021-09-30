package org.jmc;

import javax.annotation.CheckForNull;

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

	/**
	 * Sets a message to describe currently running task.
	 * @param message The current message, null for no message.
	 */
	public void setMessage(@CheckForNull String message);
}
