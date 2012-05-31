package org.jmc.Updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.jmc.Updater.Options.CmdLineException;

public class UpdaterMain {

	public static Window window=null;

	public static void main(String[] args) {

		try {
			Options.processArgs(args);
		} catch (CmdLineException e) {
			Log.info("ERROR: "+e.getMessage());
			Options.printUsage();
			return;
		}

		if(Options.download_url==null || Options.dest_folder==null)
		{
			Log.info("ERROR: Missing download URL or destination folder!");
			Options.printUsage();
			return;
		}

		if(Options.help)
		{
			Options.printUsage();
			return;
		}

		if(Options.gui)
		{
			window=new Window();
		}

		Log.info("File URL: "+Options.download_url);
		Log.info("Destination folder: "+Options.dest_folder);

		String jarfile="";
		try {

			Log.info("Downloading...");

			jarfile=downloadFile(Options.download_url,Options.dest_folder);

			Log.info("Done!");

		} catch (IOException e) {
			Log.info("Error downloading file: "+e.getMessage());
			return;
		}		


		try {

			File file=new File(jarfile);
			if(file.exists())
			{
				Log.info("Running application...");			
				Runtime.getRuntime().exec("java -jar "+jarfile);
			}
			else
			{
				Log.info("Cannot run application because I can't find it!");
				Log.info("Path: "+jarfile);
			}


		} catch (IOException e) {
			Log.info("Error running application: "+e.getMessage());
			return;
		}

		if(Options.remove_file!=null)
		{
			File a=new File(Options.remove_file);
			File b=new File(jarfile);
			if(a.equals(b))
			{
				Log.info("Skipping removal of same file as the program I'm trying to run!");
			}
			else
			{
				Log.info("Removing old program: "+Options.remove_file);
				File file=new File(Options.remove_file);
				file.delete();
			}
		}

		Log.info("Shutting down.");

		System.exit(0);
	}


	private static String downloadFile(String download_url, String dest_folder) throws IOException
	{
		String filename=download_url;
		int p=filename.indexOf("?");
		if(p>0) filename=filename.substring(0,p);
		p=filename.lastIndexOf('/');
		if(p>0) filename=filename.substring(p);
		if(filename.isEmpty())
		{			
			throw new IOException("cannot parse file name from url: "+download_url);
		}

		URL url=new URL(Options.download_url);			
		URLConnection conn=url.openConnection();
		InputStream istream=url.openStream();						

		FileOutputStream ostream = new FileOutputStream(dest_folder+"/"+filename);

		ReadableByteChannel reader=Channels.newChannel(istream);
		WritableByteChannel writer=Channels.newChannel(ostream);

		ByteBuffer buffer=ByteBuffer.allocateDirect(32*1024);

		Log.setProgressMax(conn.getContentLength());

		int read=0,ret;
		while((ret=reader.read(buffer))!=-1)
		{
			read+=ret;
			Log.setProgress(read);

			buffer.flip();
			writer.write(buffer);
			buffer.compact();
		}

		buffer.flip();
		while(buffer.hasRemaining())
		{
			writer.write(buffer);
		}


		writer.close();
		reader.close();

		return dest_folder+"/"+filename;
	}
}
