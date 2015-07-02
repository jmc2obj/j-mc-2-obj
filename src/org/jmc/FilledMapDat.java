/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;

import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Byte_Array;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Int;
import org.jmc.NBT.TAG_List;
import org.jmc.util.Log;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Class used for loading the map_xxx.dat file from the world save.
 * This file contains useful information about the save like the
 * player position and state in the game, his inventory etc.
 * @author begner
 *
 */
public class FilledMapDat {
	
	/**
	 * Path to the save.
	 */
	private File levelDir;
	/**
	 * The root of the NBT structure for this file. 
	 */
	private TAG_Compound root;
	
	private String map_id;
	
	/**
	 * Main constructor.
	 * @param levelDir path to the save
	 */
	public FilledMapDat(File levelDir)
	{
		this.levelDir=levelDir;
	}
	
	/**
	 * Opens the file.
	 * @return returns true if the operation was successful or false if the file doesn't exist or there is another error
	 */
	public boolean open(String id)
	{
		map_id = id;
		File mapFile=new File(levelDir.getAbsolutePath()+"/data/map_" + map_id + ".dat");
		
		if(!mapFile.exists()) return false;
			
		try {
			
			GZIPInputStream stream = new GZIPInputStream(new FileInputStream(mapFile));
			
			root=(TAG_Compound) NBT_Tag.make(stream);
			
			stream.close();
			
		} catch (FileNotFoundException e) {
			return false;
		} catch (Exception e) {
			Log.error("Error reading map.dat", e, false);
			return false;
		}			
	
		return true;
	}
	
	
	/**
	 * Exports an MapImage and Append the material
	 * @throws IOException 
	 */
	public TAG_Byte_Array writePngTexture() throws IOException
	{
		int image_width = 128;
		int image_height = 128;
		
		TAG_Compound data=(TAG_Compound) root.getElement("data");
		if (data==null) return new TAG_Byte_Array(null);
		TAG_Byte_Array color_map =((TAG_Byte_Array)data.getElement("colors"));
		
		BufferedImage img = new BufferedImage(image_width, image_height, BufferedImage.TYPE_INT_RGB);

		for(int x=0; x<image_width; x++) {
			for(int y=0; y<image_height; y++) {
				int index = ((image_height-y-1)*image_height)+(image_width-1-x);
				byte colorIdx = color_map.data[index];
				img.setRGB(y, x, getColorByByte(colorIdx).getRGB());
			}
		}
		
		String materialName = "map_"+map_id+"_item_frame";
		String mapFilename = "map_"+map_id+"_item_frame.png"; 
				
		File texFile = new File(Options.outputDir+"/tex", mapFilename);
		if (!ImageIO.write(img, "PNG", texFile)) {
		  throw new RuntimeException("Unexpected error writing image");
		}

		File mtlfile = new File(Options.outputDir, Options.mtlFileName);
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(mtlfile, true)))) {
			out.println("");
			out.println("");
			out.println("newmtl "+materialName);
		    out.println("Kd 0.2500 0.2500 0.2500");
    		out.println("Ks 0.0000 0.0000 0.0000");
			out.print("map_Kd tex/"+mapFilename);
		} catch (IOException e) {
		    throw new RuntimeException("Unexpected error apending material file");
		}
		

		return color_map;
	}
	
	
	
	
	
	private Color getColorByByte(short colorIdx) {
		Color mappedColor = new Color(0,0,0);
		// Log.info("color idx: "+colorIdx);
		switch(colorIdx) {
			case 0: mappedColor = new Color(0, 0, 0); break;
			case 1: mappedColor = new Color(0, 0, 0); break;
			case 2: mappedColor = new Color(0, 0, 0); break;
			case 3: mappedColor = new Color(0, 0, 0); break;
			case 4: mappedColor = new Color(88, 124, 39); break;
			case 5: mappedColor = new Color(108, 151, 47); break;
			case 6: mappedColor = new Color(125, 176, 55); break;
			case 7: mappedColor = new Color(66, 93, 29); break;
			case 8: mappedColor = new Color(172, 162, 114); break;
			case 9: mappedColor = new Color(210, 199, 138); break;
			case 10: mappedColor = new Color(244, 230, 161); break;
			case 11: mappedColor = new Color(128, 122, 85); break;
			case 12: mappedColor = new Color(138, 138, 138); break;
			case 13: mappedColor = new Color(169, 169, 169); break;
			case 14: mappedColor = new Color(197, 197, 197); break;
			case 15: mappedColor = new Color(104, 104, 104); break;
			case 16: mappedColor = new Color(178, 0, 0); break;
			case 17: mappedColor = new Color(217, 0, 0); break;
			case 18: mappedColor = new Color(252, 0, 0); break;
			case 19: mappedColor = new Color(133, 0, 0); break;
			case 20: mappedColor = new Color(111, 111, 178); break;
			case 21: mappedColor = new Color(136, 136, 217); break;
			case 22: mappedColor = new Color(158, 158, 252); break;
			case 23: mappedColor = new Color(83, 83, 133); break;
			case 24: mappedColor = new Color(116, 116, 116); break;
			case 25: mappedColor = new Color(142, 142, 142); break;
			case 26: mappedColor = new Color(165, 165, 165); break;
			case 27: mappedColor = new Color(87, 87, 87); break;
			case 28: mappedColor = new Color(0, 86, 0); break;
			case 29: mappedColor = new Color(0, 105, 0); break;
			case 30: mappedColor = new Color(0, 123, 0); break;
			case 31: mappedColor = new Color(0, 64, 0); break;
			case 32: mappedColor = new Color(178, 178, 178); break;
			case 33: mappedColor = new Color(217, 217, 217); break;
			case 34: mappedColor = new Color(252, 252, 252); break;
			case 35: mappedColor = new Color(133, 133, 133); break;
			case 36: mappedColor = new Color(114, 117, 127); break;
			case 37: mappedColor = new Color(139, 142, 156); break;
			case 38: mappedColor = new Color(162, 166, 182); break;
			case 39: mappedColor = new Color(85, 87, 96); break;
			case 40: mappedColor = new Color(105, 75, 53); break;
			case 41: mappedColor = new Color(128, 93, 65); break;
			case 42: mappedColor = new Color(149, 108, 76); break;
			case 43: mappedColor = new Color(78, 56, 39); break;
			case 44: mappedColor = new Color(78, 78, 78); break;
			case 45: mappedColor = new Color(95, 95, 95); break;
			case 46: mappedColor = new Color(111, 111, 111); break;
			case 47: mappedColor = new Color(58, 58, 58); break;
			case 48: mappedColor = new Color(44, 44, 178); break;
			case 49: mappedColor = new Color(54, 54, 217); break;
			case 50: mappedColor = new Color(63, 63, 252); break;
			case 51: mappedColor = new Color(33, 33, 133); break;
			case 52: mappedColor = new Color(99, 83, 49); break;
			case 53: mappedColor = new Color(122, 101, 61); break;
			case 54: mappedColor = new Color(141, 118, 71); break;
			case 55: mappedColor = new Color(74, 62, 38); break;
			case 56: mappedColor = new Color(178, 175, 170); break;
			case 57: mappedColor = new Color(217, 214, 208); break;
			case 58: mappedColor = new Color(252, 249, 242); break;
			case 59: mappedColor = new Color(133, 131, 127); break;
			case 60: mappedColor = new Color(150, 88, 36); break;
			case 61: mappedColor = new Color(184, 108, 43); break;
			case 62: mappedColor = new Color(213, 125, 50); break;
			case 63: mappedColor = new Color(113, 66, 27); break;
			case 64: mappedColor = new Color(124, 52, 150); break;
			case 65: mappedColor = new Color(151, 64, 184); break;
			case 66: mappedColor = new Color(176, 75, 213); break;
			case 67: mappedColor = new Color(93, 39, 113); break;
			case 68: mappedColor = new Color(71, 107, 150); break;
			case 69: mappedColor = new Color(87, 130, 184); break;
			case 70: mappedColor = new Color(101, 151, 213); break;
			case 71: mappedColor = new Color(53, 80, 113); break;
			case 72: mappedColor = new Color(159, 159, 36); break;
			case 73: mappedColor = new Color(195, 195, 43); break;
			case 74: mappedColor = new Color(226, 226, 50); break;
			case 75: mappedColor = new Color(120, 120, 27); break;
			case 76: mappedColor = new Color(88, 142, 17); break;
			case 77: mappedColor = new Color(108, 174, 21); break;
			case 78: mappedColor = new Color(125, 202, 25); break;
			case 79: mappedColor = new Color(66, 107, 13); break;
			case 80: mappedColor = new Color(168, 88, 115); break;
			case 81: mappedColor = new Color(206, 108, 140); break;
			case 82: mappedColor = new Color(239, 125, 163); break;
			case 83: mappedColor = new Color(126, 66, 86); break;
			case 84: mappedColor = new Color(52, 52, 52); break;
			case 85: mappedColor = new Color(64, 64, 64); break;
			case 86: mappedColor = new Color(75, 75, 75); break;
			case 87: mappedColor = new Color(39, 39, 39); break;
			case 88: mappedColor = new Color(107, 107, 107); break;
			case 89: mappedColor = new Color(130, 130, 130); break;
			case 90: mappedColor = new Color(151, 151, 151); break;
			case 91: mappedColor = new Color(80, 80, 80); break;
			case 92: mappedColor = new Color(52, 88, 107); break;
			case 93: mappedColor = new Color(64, 108, 130); break;
			case 94: mappedColor = new Color(75, 125, 151); break;
			case 95: mappedColor = new Color(39, 66, 80); break;
			case 96: mappedColor = new Color(88, 43, 124); break;
			case 97: mappedColor = new Color(108, 53, 151); break;
			case 98: mappedColor = new Color(125, 62, 176); break;
			case 99: mappedColor = new Color(66, 33, 93); break;
			case 100: mappedColor = new Color(36, 52, 124); break;
			case 101: mappedColor = new Color(43, 64, 151); break;
			case 102: mappedColor = new Color(50, 75, 176); break;
			case 103: mappedColor = new Color(27, 39, 93); break;
			case 104: mappedColor = new Color(71, 52, 36); break;
			case 105: mappedColor = new Color(87, 64, 43); break;
			case 106: mappedColor = new Color(101, 75, 50); break;
			case 107: mappedColor = new Color(53, 39, 27); break;
			case 108: mappedColor = new Color(71, 88, 36); break;
			case 109: mappedColor = new Color(87, 108, 43); break;
			case 110: mappedColor = new Color(101, 125, 50); break;
			case 111: mappedColor = new Color(53, 66, 27); break;
			case 112: mappedColor = new Color(107, 36, 36); break;
			case 113: mappedColor = new Color(130, 43, 43); break;
			case 114: mappedColor = new Color(151, 50, 50); break;
			case 115: mappedColor = new Color(80, 27, 27); break;
			case 116: mappedColor = new Color(17, 17, 17); break;
			case 117: mappedColor = new Color(21, 21, 21); break;
			case 118: mappedColor = new Color(25, 25, 25); break;
			case 119: mappedColor = new Color(13, 13, 13); break;
			case 120: mappedColor = new Color(174, 166, 53); break;
			case 121: mappedColor = new Color(212, 203, 65); break;
			case 122: mappedColor = new Color(247, 235, 76); break;
			case 123: mappedColor = new Color(130, 125, 39); break;
			case 124: mappedColor = new Color(63, 152, 148); break;
			case 125: mappedColor = new Color(78, 186, 181); break;
			case 126: mappedColor = new Color(91, 216, 210); break;
			case 127: mappedColor = new Color(47, 114, 111); break;
			case 128: mappedColor = new Color(51, 89, 178); break;
			case 129: mappedColor = new Color(62, 109, 217); break;
			case 130: mappedColor = new Color(73, 129, 252); break;
			case 131: mappedColor = new Color(39, 66, 133); break;
			case 132: mappedColor = new Color(0, 151, 39); break;
			case 133: mappedColor = new Color(0, 185, 49); break;
			case 134: mappedColor = new Color(0, 214, 57); break;
			case 135: mappedColor = new Color(0, 113, 30); break;
			case 136: mappedColor = new Color(90, 59, 34); break;
			case 137: mappedColor = new Color(110, 73, 41); break;
			case 138: mappedColor = new Color(127, 85, 48); break;
			case 139: mappedColor = new Color(67, 44, 25); break;
			case 140: mappedColor = new Color(78, 1, 0); break;
			case 141: mappedColor = new Color(95, 1, 0); break;
			case 142: mappedColor = new Color(111, 2, 0); break;
			case 143: mappedColor = new Color(58,1,0); break;
		}
	
		return mappedColor; 
	}
	
	/**
	 * Prints the description and content of the file into a String.
	 */
	public String toString()
	{
		return "MAP file:\n"+root;
	}
}
