/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Int;
import org.jmc.NBT.TAG_List;

/**
 * Class used for loading the level.dat file from the world save.
 * This file contains useful information about the save like the
 * player position and state in the game, his inventory etc.
 * @author danijel
 *
 */
public class LevelDat {
	
	/**
	 * Path to the save.
	 */
	private File levelDir;
	/**
	 * The root of the NBT structure for this file. 
	 */
	private TAG_Compound root;
	
	/**
	 * Main constructor.
	 * @param levelDir path to the save
	 */
	public LevelDat(File levelDir)
	{
		this.levelDir=levelDir;
	}
	
	/**
	 * Opens the file.
	 * @return returns true if the operation was successful or false if the file doesn't exist or there is another error
	 */
	public boolean open()
	{
		File levelFile=new File(levelDir.getAbsolutePath()+"/level.dat");
		
		if(!levelFile.exists()) return false;
			
		try {
			
			GZIPInputStream stream = new GZIPInputStream(new FileInputStream(levelFile));
			
			root=(TAG_Compound) NBT_Tag.make(stream);
			
			stream.close();
			
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}			
	
		return true;
	}
	
	/**
	 * Gets the position of the player.
	 * @return returns a list of X,Y,Z NBT_Float values, or null if the information is not available
	 */
	public TAG_List getPosition()
	{
		TAG_Compound data=(TAG_Compound) root.getElement("Data");
		if (data==null) return null;
		TAG_Compound player=(TAG_Compound) data.getElement("Player");
		if (player==null) return null;
		return (TAG_List)player.getElement("Pos");
	}
	
	/**
	 * Gets the X location of the spawn. 
	 * @return x coordinate, or 0 if the information is not available
	 */
	public int getSpawnX()
	{
		TAG_Compound data=(TAG_Compound) root.getElement("Data");
		if (data==null) return 0;
		TAG_Int pos = (TAG_Int)data.getElement("SpawnX");
		if (pos==null) return 0;
		return pos.value;
	}
	
	/**
	 * Gets the Z location of the spawn.
	 * @return z coordinate, or 0 if the information is not available
	 */
	public int getSpawnZ()
	{
		TAG_Compound data=(TAG_Compound) root.getElement("Data");
		if (data==null) return 0;
		TAG_Int pos = (TAG_Int)data.getElement("SpawnZ");
		if (pos==null) return 0;
		return pos.value;
	}
	
	/**
	 * Prints the description and content of the file into a String.
	 */
	public String toString()
	{
		return "DAT file "+levelDir.getAbsolutePath()+"/level.dat:\n"+root;
	}
}
