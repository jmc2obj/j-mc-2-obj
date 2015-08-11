package org.jmc.gui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import org.jmc.Version;
import org.jmc.util.Messages;


public class About {
	
	private static boolean initialized=false; 
	
	private static void init()
	{
		if(initialized)
			return;
		
		String logo=About.class.getResource("logo.png").toExternalForm();
		
		// define the about box with a width of 300px
        String msg="<html><div style='width:300px'><div style='text-align:center;'><img src=\""+logo+"\"></div>";
        msg+="<div style='text-align:center;font-weight:bold; font-size:20pt;'>jMC2Obj</div>";
        msg+="<div style='text-align:center;font-weight:bold; font-size:16pt;'>"+Messages.getString("About.PROG_DESC")+"</div><p>";
        msg+="<table width=\"100%\" border=\"0\"><tr><td width=\"100px\">"+Messages.getString("About.VERSION")+":</td><td>"+Version.VERSION+Version.REVISION()+"</td></tr>"
                +"<tr><td width=\"100px\">"+Messages.getString("About.BUILD")+":</td><td>"+Version.DATE().toString()+"</td></tr>"
                + "</table><p>";
        List<String> developer_list=new LinkedList<String>();
        
        developer_list.add("<td>Max Tangelder</td><td>programming</td>");
        developer_list.add("<td>Danijel Kor&#x017e;inek</td><td>programming</td>");             
        developer_list.add("<td>Pedro Lopes</td><td>programming</td>");
        developer_list.add("<td>Kenneth Zhou</td><td>programming</td>");
        developer_list.add("<td>Moonlight63</td><td>programming</td>");
        developer_list.add("<td>Benjamin Egner</td><td>programming</td>");

        Collections.shuffle(developer_list);
        
        msg+="<u>"+Messages.getString("About.AUTHORS")+"</u><br>";
        
        msg+="<table width=\"100%\" border=\"0\">";        
        
        for(String s:developer_list)
                msg+="<tr>"+s+"</tr>";
        
        msg+="</table><br><p>";
        
        List<String> donations=new LinkedList<String>();
        
        donations.add("Hilum");
        donations.add("The Pixel Artist");
        donations.add("Justin \"Rip_Shot\" Goran");
        donations.add("Slamacow Creations");
        donations.add("darkinnit");
        
        Collections.shuffle(donations);
        
        msg+="<u>"+Messages.getString("About.THANKS")+"</u><br><p>";
        msg+="<table width=\"100%\" border=\"0\">";
        for(String s:donations)
                msg+="<tr><td>"+s+"</td></tr>";
        
        msg+="</table></p>";
        msg+="<p>"+Messages.getString("About.ALSO")+"<p>";
        msg+="<div style='text-align:center;'>"+Messages.getString("About.URL")+"<br>" +
        		"https://github.com/jmc2obj/j-mc-2-obj<br>" +
        		"<a href=\"http://www.jmc2obj.net/\">http://www.jmc2obj.net/</a></div>";
        msg+="</div><p><br></html>";
        about_msg=msg;
		initialized=true;
	}
	
	private static String about_msg="";
	
	public static void show()
	{
		init();
		
		JOptionPane.showMessageDialog(MainWindow.main, about_msg,Messages.getString("MainPanel.ABOUT_BUTTON"),JOptionPane.PLAIN_MESSAGE);
	}
}
