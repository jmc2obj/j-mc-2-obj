package org.jmc.gui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import org.jmc.Version;


public class About {
	
	private static boolean initialized=false; 
	
	private static void init()
	{
		if(initialized)
			return;
		
		String msg="<html><div style=\"width:200px\">";		
		
		msg+="<div style=\"font-size:24pt\">jMC2Obj</div>";
		msg+="<div style=\"font-size:14pt\">A Java Minecraft to OBJ converter</div>";
		msg+="<div style=\"font-size:12pt;margin-bottom:20px\">version: "+Version.VERSION+" build "+Version.REVISION()+"</div>";
		
		List<String> developer_list=new LinkedList<String>();
		
		developer_list.add("Max Tangelder - programming");
		developer_list.add("Danijel Kor&#x017e;inek - programming");		
		developer_list.add("Pedro Lopes - programming");
		
		//I hate ordering people so I used this trick...
		Collections.shuffle(developer_list);
		
		msg+="Made by:";
		msg+="<ul style=\"list-style-type:none\">";
		for(String s:developer_list)
			msg+="<li>"+s+"</li>";
		msg+="</ul>";
				
				
		List<String> donations=new LinkedList<String>();
		
		donations.add("Hilum");
		donations.add("The Pixel Artist");
		donations.add("Justin \"Rip_Shot\" Goran");
		donations.add("Slamacow Creations");
		
		Collections.shuffle(donations);
		
		msg+="We would like to thank these people for their donations:";
		msg+="<ul style=\"list-style-type:none\">";
		for(String s:donations)
			msg+="<li>"+s+"</li>";
		msg+="</ul>";
		
		msg+="</div></html>";
		
		about_msg=msg;
		initialized=true;
	}
	
	private static String about_msg="";
	
	public static void show()
	{
		init();
		
		JOptionPane.showMessageDialog(MainWindow.main, about_msg,"About...",JOptionPane.PLAIN_MESSAGE);
	}
}
