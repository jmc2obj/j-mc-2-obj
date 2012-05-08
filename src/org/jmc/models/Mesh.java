package org.jmc.models;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJInputFile;
import org.jmc.OBJInputFile.OBJGroup;
import org.jmc.OBJOutputFile;
import org.jmc.entities.EntityModel;
import org.jmc.geom.Transform;
import org.jmc.util.Log;

public class Mesh extends BlockModel implements EntityModel
{
	
	private static Map<String,OBJInputFile> files=null;
	
	private class MeshObject
	{
		byte data;
		byte mask;
		
		Transform transform;
		
		OBJInputFile file;
		OBJGroup group;		
	}
	
	private List<MeshObject> objects;
	
	public Mesh()
	{
		if(files==null) files=new HashMap<String, OBJInputFile>();
		objects=new LinkedList<MeshObject>();
	}
	
	public void addMesh(String objectstr)
	{
		addMesh(objectstr,(byte)-1,(byte)0,null);
	}
	
	public void addMesh(String objectstr, byte data, byte mask, Transform transform)
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
		object.mask=mask;
		object.data=data;
		object.transform=transform;
		
		objects.add(object);
		
	}

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		if(data<0) data=(byte) (16+data);
		
		for(MeshObject object:objects)
		{
			if(object.data>=0)
			{
				if(object.mask>0)
				{
					if((data&object.mask)!=object.data) continue;
				}
				else
				{
					if(object.data!=data) continue;
				}
			}
			
			Transform translate = new Transform();
			translate.translate(x, y, z);		
			
			if(object.transform!=null)
			{
				translate=translate.multiply(object.transform);
			}
			
			object.file.addObject(object.group, translate, obj);
		}
	}

	@Override
	public void addEntity(OBJOutputFile obj, Transform transform) 
	{
		if(objects.size()==0) return;
		MeshObject object=objects.get(0);
		object.file.addObject(object.group, transform, obj);
	}

}
