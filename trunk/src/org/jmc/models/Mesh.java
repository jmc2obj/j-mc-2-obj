package org.jmc.models;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJInputFile;
import org.jmc.OBJInputFile.OBJGroup;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.util.Log;

public class Mesh extends BlockModel 
{
	
	private static Map<String,OBJInputFile> files=null;
	
	private class MeshObject
	{
		OBJInputFile file;
		OBJGroup group;		
	}
	
	private MeshObject objects[];
	
	public Mesh()
	{
		if(files==null) files=new HashMap<String, OBJInputFile>();
		objects=new MeshObject[16];
	}
	
	public void addMesh(String objectstr, int data)
	{
		String [] tok=objectstr.trim().split("#");
		String filename=tok[0];
				
		OBJInputFile objin=null;
		
		
		if(files.containsKey(filename))
			objin=files.get(filename);
		else
		{
			objin=new OBJInputFile();

			try {
				objin.loadFile(new File("conf",filename));
			} catch (IOException e) {
				Log.error("Cannot load mesh file!", e);
				return;
			}
			
			files.put(filename, objin);
		}
		
		OBJGroup group=null;
		
		if(tok.length>1)
			group=objin.getObject(tok[1]);
		else
			group=objin.getDefaultObject();
			
		if(group==null)
		{
			Log.info("Cannot find "+objectstr+" object!");
			return;
		}
		
		MeshObject object=new MeshObject();
		object.group=group;
		object.file=objin;
		
		if(data<0)
		{
			for(int i=0; i<16; i++)
				objects[i]=object;
		}
		else
			objects[data]=object;
		
	}

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		if(objects[data]==null) return; 
		
		MeshObject object=objects[data];

		Transform translate = new Transform();
		translate.translate(x, y, z);		

		object.file.addObject(object.group, translate, obj);
	}

}
