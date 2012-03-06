/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class MTLFile {
	
	public enum Side
	{
		TOP,
		BOTTOM,
		LEFT,
		RIGHT,
		FRONT,
		BACK
	}
	
	Colors colors;
	
	public MTLFile()//default texture
	{
		colors=new Colors();
	}
	
	public int getMaterialId(int id, Side side)
	{
		if(colors.getColor(id)!=null)
		{
			return id;
		}
		return -1;
	}
	
	
	public String getMaterial(int id)
	{
		if(colors.getColor(id)!=null)
			return "material-"+id;
		else return "unknown";
	}
	
	public void header(PrintWriter out)
	{
		out.println("mtllib minecraft.mtl");
		out.println();
	}
	
	public void saveMTLFile(File file) throws IOException
	{		
		PrintWriter writer=new PrintWriter(new FileWriter(file));
		Locale l=null;
		
		writer.println("newmtl unknown");
		writer.println("Kd 1 0 1");
		writer.println();
		
		Color c;
		for(int i=0; i<256; i++)
		{
			c=colors.getColor(i);
			if(c!=null)
			{
				float r=c.getRed()/256.0f;
				float g=c.getGreen()/256.0f;				
				float b=c.getBlue()/256.0f;
				writer.println("newmtl material-"+i);
				writer.format(l,"Kd %2.2f %2.2f %2.2f",r,g,b);
				writer.println();
				writer.println();
			}
		}
		
		writer.close();
	}

}
