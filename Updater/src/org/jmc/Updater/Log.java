package org.jmc.Updater;

public class Log {

	public static void info(String str)
	{
		if(UpdaterMain.window!=null)
			UpdaterMain.window.log(str);
		System.out.println(str);
	}
	
	public static void setProgressMax(int val)
	{
		if(UpdaterMain.window!=null)
			UpdaterMain.window.setProgressMax(val);
	}
	
	
	public static void setProgress(int val)
	{
		if(UpdaterMain.window!=null)
			UpdaterMain.window.setProgress(val);
	}
}
