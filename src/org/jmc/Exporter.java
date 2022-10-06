package org.jmc;

import org.jmc.models.Banner;
import org.jmc.util.Log;

import javax.annotation.CheckForNull;

public abstract class Exporter {
	public abstract void export(@CheckForNull ProgressCallback progress);
	
	protected static void resetErrors() {
		Banner.resetReadError();
		Log.resetSingles();
	}
}
