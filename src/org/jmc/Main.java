package org.jmc;

import java.io.File;

import javax.swing.UIManager;

import org.jmc.Options.UIMode;
import org.jmc.gui.MainWindow;
import org.jmc.util.Log;


public class Main
{
	/**
	 * Start of program.
	 * 
	 * @param args program arguments
	 */
	public static void main(String[] args)
	{
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {			
			@Override
			public void uncaughtException(Thread t, Throwable e) { 
				Log.error("Uncaught exception in thread: "+t.getName(), e);
			}
		});

		System.out.println("jmc2obj "+Version.VERSION+" ("+Version.REVISION+")");

		if (args.length == 0) {
			Options.uiMode = UIMode.GUI;
			runGUI();
		}
		else {
			Options.uiMode = UIMode.CONSOLE;
			runConsole(args);
		}
	}

	
	private static void runGUI()
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		new MainWindow();

		try {
			Configuration.initialize();
			Materials.initialize();
			BlockTypes.initialize();
			EntityTypes.initialize();
		}
		catch (Exception e) {
			Log.error("Error reading configuration file:", e);
		}
	}
	

	private static void runConsole(String[] args)
	{
		try {
			CmdLineParser.parse(args);
		}
		catch (CmdLineParser.CmdLineException e) {
			System.out.println("Error: " + e.getMessage());
			CmdLineParser.printUsage();
			System.exit(-1);
		}
		
		try {
			Configuration.initialize();
			Materials.initialize();
			BlockTypes.initialize();
		}
		catch (Exception e) {
			Log.error("Error reading configuration file:", e);
			System.exit(-2);
		}
		
		ObjExporter.export(new ConsoleProgress(), null, Options.exportObj, Options.exportMtl);

		if (Options.exportTex) {
			System.out.println("Exporting textures...");
			try {
				Texsplit.splitTextures(new File(Options.outputDir, "tex"), Options.texturePack, true, new ConsoleProgress());
			}
			catch (Exception e) {
				Log.error("Error saving textures:", e);
			}
		}
	}

}

/** Simple console progress bar */
class ConsoleProgress implements ProgressCallback
{
	private static final int SIZE = 78;
	private int last = Integer.MAX_VALUE;
	
	@Override
	public void setProgress(float value) {
		int curr = Math.min((int)(SIZE * value), SIZE);
		if (curr < last) {
			System.out.print("[");
			last = 0;
		}
		while (curr > last) {
			System.out.print(".");
			last++;
			if (last == SIZE) System.out.println("]");
		}
	}
}
