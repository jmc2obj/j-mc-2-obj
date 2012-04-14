package org.jmc.models;

import java.io.File;
import java.io.IOException;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJInputFile;
import org.jmc.OBJOutputFile;

public class Mesh extends BlockModel 
{
	
	OBJInputFile objin;
	
	public Mesh()
	{
		objin=new OBJInputFile();

		try {
			objin.loadFile(new File("object.obj"));
		} catch (IOException e) {
			e.printStackTrace();
			objin=null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x,
			int y, int z, byte data) {
		
		if(objin==null) return; 
			
		objin.addObject("object.obj#Cake", x, y, z, null, obj);
		
	}

}
