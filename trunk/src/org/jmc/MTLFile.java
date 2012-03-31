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

/**
 * Material file class.
 * This class describes the file which contains a library of all materials in the
 * model. It is saved into a .MTL file that is linked from within the .OBJ file. 
 * @author danijel
 *
 */
public class MTLFile {

	/**
	 * A small enum for describing the sides of a cube.
	 * @author danijel
	 *
	 */
	public enum Side
	{
		TOP,
		BOTTOM,
		LEFT,
		RIGHT,
		FRONT,
		BACK,
		BACKRIGHT,
		FRONTRIGHT
	}

	/**
	 * Reference to a colors object.
	 */
	private Colors colors;
	
	private File mtl_file;

	private Textures textures;//added
	/**
	 * Main constructor.
	 */
	public MTLFile(File mtl_file)
	{
		colors=MainWindow.settings.minecraft_colors;
		textures = MainWindow.settings.minecraft_textures;
		this.mtl_file=mtl_file;
	}

	/**
	 * Returns the material ID for a given side of a block.
	 * @param id ID of the block
	 * @param side side of the block
	 * @return material ID
	 */
	public int getMaterialId(int id, byte data, Side side)
	{
		if(colors.getColor(id,data)!=null)
		{
			return (((int)data)&0xff)<<16|id;
		}
		return -1;
	}

	/**
	 * Returns a name for a material with the given ID.
	 * @param id material ID
	 * @return name of material
	 */
	public String getMaterial(int mtl_id)
	{
		int id=mtl_id&0xFFFF;
		if(colors.hasData(id))
		{
			byte data=(byte) (mtl_id>>16);
			int dataval=(mtl_id>>16)&0x0F;
			if(colors.getColor(id,data)!=null)
				return "material-"+id+"_"+dataval;
		}
		else if(colors.getColor(id,(byte) 0)!=null)
			return "material-"+id;

		return "unknown";
	}

	/**
	 * Prints a header with the name of the MTL file. This is used by the OBJ class
	 * to print a header in the OBJ file.
	 * @param out OBJ file writer
	 */
	public void header(PrintWriter out, File objfile)
	{
		out.println("mtllib "+mtl_file.getName());		
		out.println();
	}

	/**
	 * Writes a line with the diffuse color.
	 * @param writer writer of the MTL file
	 * @param c color to write
	 */
	private void writeDiffuse(PrintWriter writer, Color c)
	{
		Locale l=null;
		float r=c.getRed()/256.0f;
		float g=c.getGreen()/256.0f;				
		float b=c.getBlue()/256.0f;
		writer.format(l,"Kd %2.2f %2.2f %2.2f",r,g,b);
		writer.println();
		writer.format(l,"Ks 0 0 0");
		writer.println();
	}
	
	private void writeDiffuseTexture(PrintWriter writer, String s)//added
	{
		Locale l = null;
		String path = s;
		if(s != null)
		{
			writer.format(l , "map_Kd %s", path);
			writer.println();
		}
		
	}
	
	private void writeAlphaTexture(PrintWriter writer, String s)//added
	{
		Locale l = null;
		String path = s;
		if(s != null)
		{
			writer.format(l , "map_d %s", path);
			writer.println();
		}
		
	}
	
	/**
	 * Saves the MTL file.
	 * @param file destination of the file
	 * @throws IOException exception if an error occurs during writing
	 */
	public void saveMTLFile() throws IOException
	{		
		PrintWriter writer=new PrintWriter(new FileWriter(mtl_file));		

		writer.println("newmtl unknown");
		writer.println("Kd 1 0 1");
		writer.println();

		Color c;
		for(int i=0; i<256; i++)
		{
			if(colors.hasData(i))
			{
				for(int j=0; j<16; j++)
				{
					c=colors.getColor(i,(byte) j);
					if(c!=null)
					{						
						writer.println("newmtl material-"+i+"_"+j);
						writeDiffuse(writer, c);
						writer.println();
					}
				}
			}

			c=colors.getColor(i,(byte) 0);

			if(c!=null)
			{
				writer.println("newmtl material-"+i);
				writeDiffuse(writer, c);
				writeDiffuseTexture(writer, textures.getTexture(i));
				if(textures.hasAlpha(i))
				{
					//for Blender renderer use same texture for alpha
					writeAlphaTexture(writer, textures.getTexture(i));
				}
				writer.println();
			}

		}

		writer.close();
	}

}
