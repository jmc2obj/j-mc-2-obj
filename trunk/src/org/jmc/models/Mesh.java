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
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.util.Log;

public class Mesh extends BlockModel
{

	private static Map<String,OBJInputFile> files=null;

	public static class MeshData
	{
		public byte data;
		public byte mask;		
		public Vertex offset;
		public short id;		
		public Transform transform;
		public boolean fallthrough;

		public MeshData()
		{
			data=-1;
			mask=-1;
			id=-1;
			offset=null;
			transform=null;
			fallthrough=false;
		}

		public boolean matches(ChunkDataBuffer chunks, int x, int y, int z, byte block_data)
		{			
			if(offset==null || offset==new Vertex(0,0,0))
			{
				byte d=block_data;
				if(mask>0) d=(byte)(block_data&mask);
				if(data>=0 && data!=d) return false;
				return true;
			}
			else
			{
				byte d=chunks.getBlockData(x+(int)offset.x, y+(int)offset.y, z+(int)offset.z);
				short i=chunks.getBlockID(x+(int)offset.x, y+(int)offset.y, z+(int)offset.z);

				if(mask>0) d=(byte)(d&mask);

				if(data>=0 && data!=d) return false;

				if(id>=0 && i!=id) return false;

				return true;
			}
		}

	}

	@SuppressWarnings("unused")
	private String obj_str;
	public OBJInputFile objin_file;
	public OBJGroup group;

	private List<Mesh> objects;
	public MeshData mesh_data;

	public Mesh()
	{
		if(files==null) files=new HashMap<String, OBJInputFile>();
		objects=new LinkedList<Mesh>();
		mesh_data=new MeshData();

		objin_file=null;
		group=null;
		obj_str="";
	}

	public void parseString(String objectstr)
	{
		obj_str=objectstr;
		String [] tok=objectstr.trim().split("#");
		String filename=tok[0];

		if(files.containsKey(filename))
			objin_file=files.get(filename);
		else
		{
			objin_file=new OBJInputFile();

			try {
				objin_file.loadFile(new File("conf",filename));
			} catch (IOException e) {
				Log.error("Cannot load mesh file!", e);
				return;
			}

			files.put(filename, objin_file);
		}

		if(tok.length>1)
			group=objin_file.getObject(tok[1]);
		else
			group=objin_file.getDefaultObject();

		if(group==null)
		{
			Log.info("Cannot find "+objectstr+" object!");
			return;
		}
	}

	public void addMesh(Mesh mesh)
	{
		objects.add(mesh);
	}

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte block_data, byte biome)
	{
		if(block_data<0) block_data=(byte) (16+block_data);

		Transform translate = new Transform();
		translate.translate(x, y, z);

		addModel(obj,chunks,x,y,z,block_data,biome,translate);
	}

	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z , byte block_data, byte biome, Transform trans)
	{

		boolean match=mesh_data.matches(chunks, x, y, z, block_data);

		if(match)
		{
			if(mesh_data.transform!=null)
			{
				trans=trans.multiply(mesh_data.transform);
			}

			if(group!=null && objin_file!=null)
			{
				objin_file.addObject(group, trans, obj);
				Log.info("ADDING");
			}
		}


		if(mesh_data.fallthrough || match)
		{
			for(Mesh object:objects)
			{
				object.addModel(obj,chunks,x,y,z,block_data,biome,trans);
			}
		}
	}

	//DEBUG
	/*
	public String toString()
	{
		String ret;

		ret="MESH ("+this.hashCode()+")\n";
		ret+="STR "+obj_str+"\n";
		ret+="DATA "+mesh_data.data+" & "+mesh_data.mask+"\n";
		ret+="OBJECTS "+objects.size()+":\n";
		for(Mesh object:objects)
			ret+=object.toString();
		ret+="------\n";

		return ret;
	}
	*/
}
