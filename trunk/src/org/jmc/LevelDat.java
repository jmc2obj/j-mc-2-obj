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

public class LevelDat {
	
	private File levelDir;
	TAG_Compound root;
	
	public LevelDat(File levelDir)
	{
		this.levelDir=levelDir;
	}
	
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
	
	public TAG_List getPosition()
	{
		TAG_Compound data=(TAG_Compound) root.getElement("Data");
		TAG_Compound player=(TAG_Compound) data.getElement("Player");
		return (TAG_List)player.getElement("Pos");
	}
	
	public int getSpawnX()
	{
		TAG_Compound data=(TAG_Compound) root.getElement("Data");
		return ((TAG_Int)data.getElement("SpawnX")).value;
	}
	
	public int getSpawnZ()
	{
		TAG_Compound data=(TAG_Compound) root.getElement("Data");
		return ((TAG_Int)data.getElement("SpawnZ")).value;
	}
	
	public String toString()
	{
		return "DAT file "+levelDir.getAbsolutePath()+"/level.dat:\n"+root;
	}
}
