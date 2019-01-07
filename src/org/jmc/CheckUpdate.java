package org.jmc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jmc.Options.UIMode;
import org.jmc.gui.MainWindow;
import org.jmc.util.Log;

public class CheckUpdate {

	public static void asyncCheck()
	{
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {
				if(isAvailable())
				{
					Log.info("New version of the program is available!");
					if (Options.uiMode == UIMode.GUI)
						MainWindow.main.highlightUpdateButton();
				}
				else
				{
					Log.info("No update available...");
				}				
			}
		});
		t.setDaemon(true);
		t.start();		
	}
	
	public static boolean isAvailable()
	{
		try{
			int newVer = getVersion();
			
			int currVer = Version.VERSION();
			
			return newVer > currVer;
			
		}catch (Exception e) {
			Log.error("Cannot check update ", e);
			return false;
		}
	}
	
	public static int getVersion()
	{
		try{

			URL updateLink=new URL("https://api.github.com/repos/jmc2obj/j-mc-2-obj/releases/latest");
			String latestData = getHTML(updateLink);
			
			Pattern pattern = Pattern.compile("\"tag_name\"\\s*?:\\s*?\"([\\d\\w-:]+?)\"");
			Matcher matcher = pattern.matcher(latestData);
			if (!matcher.find()) {
				throw new Exception("Failed to find tag_name!");
			}
			
			String tagStr = matcher.group(1);
			int newVer = Integer.parseInt(tagStr);
			
			return newVer;
			
		}catch (Exception e) {
			Log.error("Cannot check update ", e);
			return 0;
		}
	}
	
	public static String getUrl()
	{
		try{
			URL updateLink=new URL("https://api.github.com/repos/jmc2obj/j-mc-2-obj/releases/latest");
			String latestData = getHTML(updateLink);
			
			Pattern pattern = Pattern.compile("\"browser_download_url\"\\s*?:\\s*?\"(.+?)\"");//BAD, there can be multiple browser_download_url.
			Matcher matcher = pattern.matcher(latestData);
			if (!matcher.find()) {
				throw new Exception("Failed to find html_url!");
			}
			
			String url = matcher.group(1);
			
			return url;
			
		}catch (Exception e) {
			Log.error("Cannot check update ", e);
			return "";
		}
	}
	
	private static String getHTML(URL url) throws Exception {
	      StringBuilder result = new StringBuilder();
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod("GET");
	      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	      String line;
	      while ((line = rd.readLine()) != null) {
	         result.append(line);
	      }
	      rd.close();
	      return result.toString();
	   }
	
}
