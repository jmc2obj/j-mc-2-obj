package org.jmc.Updater;

public class Options {

	
	public static String download_url=null;
	
	public static String dest_folder=null;
	
	public static String remove_file=null;
	
	public static boolean gui=false;
	
	public static boolean help=false;
	
	public static void processArgs(String [] args) throws CmdLineException
	{
		for(int i=0; i<args.length; i++)
		{
			String a = args[i];
			
			try{
				if(a.equals("-d"))
				{
					download_url=args[i+1];
					i++;					
				}
				else if(a.equals("-f"))
				{
					dest_folder=args[i+1];
					i++;
				}
				else if(a.equals("-r"))
				{
					remove_file=args[i+1];
					i++;
				}
				else if(a.equals("-g"))
				{
					gui=true;
				}
				else if(a.equals("-h"))
				{
					help=true;
				}
				else
				{
					throw new CmdLineException("Unexpected command line token: "+a);
				}
			}catch (CmdLineException ex) {
				throw ex;
			}
			catch (IndexOutOfBoundsException ex) {
				throw new CmdLineException("Missing argument to option " + a);
			}
			catch (Exception ex) {
				throw new CmdLineException("Invalid option: " + a);
			}
		}
	}
	
	public static void printUsage()
	{
		Log.info("Updater - program for updating applications");
		Log.info("usage:");
		Log.info("java -jar Updater.jar [options]");
		Log.info("options:");
		Log.info("  -d - program download link (mandatory)");
		Log.info("  -f - destination folder (mandatory)");
		Log.info("  -r - remove file after download is succesful");
		Log.info("  -g - display progress in a GUI application");
		Log.info("  -h - display this help");
	}
	
	@SuppressWarnings("serial")
	public static class CmdLineException extends Exception
	{
		public CmdLineException(String message) {
			super(message);
		}
	}
	
}
