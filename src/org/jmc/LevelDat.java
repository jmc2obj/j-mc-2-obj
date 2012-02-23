package org.jmc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Compound;

public class LevelDat {
	
	private File levelDir;
	TAG_Compound root;
	
	public LevelDat(File levelDir)
	{
		this.levelDir=levelDir;
	}
	
	public boolean open()
	{
		File levelFile=new File(levelDir.getAbsoluteFile()+"/level.dat");
		
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
	
	public String toString()
	{
		return "DAT file "+levelDir.getAbsolutePath()+"/level.dat:\n"+root;
	}
}
