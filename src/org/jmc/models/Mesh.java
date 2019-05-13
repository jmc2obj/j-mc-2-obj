package org.jmc.models;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jmc.BlockData;
import org.jmc.OBJInputFile;
import org.jmc.OBJInputFile.OBJGroup;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Log;

public class Mesh extends BlockModel
{

	private static Map<String,OBJInputFile> files=null;

	public static class MeshData
	{
		public BlockData data;
		public Vertex offset;
		public String id;		
		public Transform transform;
		public boolean fallthrough;

		public MeshData()
		{
			data=new BlockData();
			id="";
			offset=null;
			transform=null;
			fallthrough=false;
		}

		public boolean matches(ThreadChunkDeligate chunks, int x, int y, int z, BlockData data2)
		{			
			if(offset==null || offset==new Vertex(0,0,0))
			{
				if(!data.isEmpty() && !data2.matchesMask(data)) return false;
				return true;
			}
			else
			{
				BlockData d=chunks.getBlockData(x+(int)offset.x, y+(int)offset.y, z+(int)offset.z);
				String i=d.id;

				if(!data.isEmpty() && !d.matchesMask(data)) return false;

				if(!i.isEmpty() && !i.equals(id)) return false;

				return true;
			}
		}

	}

	//@SuppressWarnings("unused")
	private String obj_str;
	private OBJInputFile objin_file;
	private OBJGroup group;

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

	public void loadObjFile(String objectstr)
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

	public void propagateMaterials() {
		for (Mesh mesh : objects) {
			if (materials != null && mesh.materials == null && !materials.get(null, 0)[0].equals("unknown"))
				mesh.setMaterials(materials);
			mesh.propagateMaterials();
		}
	}
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		//What did this do? if(data<0) data=(byte) (16+data);

		Transform translate = new Transform();
		translate.translate(x, y, z);

		addModel(obj,chunks,x,y,z,data,biome,translate);
	}

	private void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z , BlockData data, int biome, Transform trans)
	{
		boolean match=mesh_data.matches(chunks, x, y, z, data);

		if(match)
		{
			if(mesh_data.transform!=null)
			{
				trans=trans.multiply(mesh_data.transform);
			}

			if(group!=null && objin_file!=null)
			{
				if (materials != null) {
					String[] mats = materials.get(data, biome);
					if (mats != null) {
						objin_file.overwriteMaterial(group, mats[0]);
					}
				}
				objin_file.addObjectToOutput(group, trans, obj);
			}
		}


		if(mesh_data.fallthrough || match)
		{
			for(Mesh object:objects)
			{
				object.addModel(obj,chunks,x,y,z,data,biome,trans);
			}
		}
	}

	//DEBUG
	@Override
	public String toString()
	{
		return toString(0);
	}
	
	public String toString(int depth)
	{
		String ret = "";
		for (int i = 0; i<depth;i++)
			ret += '\t';

		ret+="MESH ("+this.hashCode()+"), ";
		ret+="STR "+obj_str+", ";
		if (materials != null)
			ret+="MAT "+Arrays.toString(materials.get(null, 0))+", ";
		ret+="DATA "+mesh_data.data+", ";
		if (mesh_data.transform != null)
			ret+="TRANS "+mesh_data.transform.toString()+", ";
		ret+="OBJECTS "+objects.size()+":";
		for(Mesh object:objects)
			ret+= "\n" + object.toString(depth + 1);

		return ret;
	}
}
