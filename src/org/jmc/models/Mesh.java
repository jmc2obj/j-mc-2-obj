package org.jmc.models;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jmc.BlockData;
import org.jmc.Blockstate;
import org.jmc.OBJInputFile;
import org.jmc.OBJInputFile.OBJGroup;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.registry.Registries;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Filesystem.JmcConfFile;
import org.jmc.util.Log;

public class Mesh extends BlockModel
{

	private static Map<String,OBJInputFile> files=null;

	public static class MeshData
	{
		public Blockstate state;
		public Vertex offset;
		public NamespaceID id;
		public Transform transform;
		public boolean fallthrough;
		public boolean random;
		public float weight;

		public MeshData()
		{
			state=new Blockstate();
			id=NamespaceID.NULL;
			offset=null;
			transform=null;
			fallthrough=false;
			random=false;
			weight=1;
		}

		public boolean matches(ThreadChunkDeligate chunks, int x, int y, int z, Blockstate state2)
		{			
			if(offset == null || offset.equals(new Vertex(0,0,0)))
			{
				if(!state.isEmpty() && !state2.matchesMask(state)) return false;
				return true;
			}
			else
			{
				BlockData d=chunks.getBlockData(x+(int)offset.x, y+(int)offset.y, z+(int)offset.z);
				
				if(d==null) return false;

				if(!state.isEmpty() && !d.state.matchesMask(state)) return false;

				if(d.id != NamespaceID.NULL && !d.id.equals(id)) return false;

				return true;
			}
		}

	}

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

			try (JmcConfFile objFile = new JmcConfFile("conf/"+filename)) {
				objin_file.loadFile(objFile);
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
			if (!materials.isEmpty() && mesh.materials.isEmpty() && !materials.get(null, 0)[0].equals(Registries.UNKNOWN_TEX_ID))
				mesh.setMaterials(materials);
			mesh.propagateMaterials();
		}
	}
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		//What did this do? if(data<0) data=(byte) (16+data);

		Transform translate = Transform.translation(x, y, z);

		addModel(obj,chunks,x,y,z,data,biome,translate);
	}

	private void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z , BlockData data, int biome, Transform trans)
	{
		boolean match=mesh_data.matches(chunks, x, y, z, data.state);

		if(match)
		{
			if(mesh_data.transform!=null)
			{
				trans=trans.multiply(mesh_data.transform);
			}

			if(group!=null && objin_file!=null)
			{
				OBJGroup group_mod = group;
				if (!materials.isEmpty()) {
					NamespaceID[] mats = materials.get(data.state, biome);
					group_mod = objin_file.overwriteMaterial(group, mats[0]);
				}
				objin_file.addObjectToOutput(group_mod, trans, obj);
			}
		}


		if(mesh_data.fallthrough || match)
		{
			if (mesh_data.random) {
				float maxWeight = 0;
				for (Mesh object : objects) {
					maxWeight += object.mesh_data.weight;
				}
				long seed = new Random(x).nextLong() + new Random(y).nextLong() + new Random(z).nextLong();
				float rand = new Random(seed).nextFloat()*maxWeight;
				for (Mesh object : objects) {
					if (rand < object.mesh_data.weight) {
						object.addModel(obj, chunks, x, y, z, data, biome, trans);
						break;
					}
					rand -= object.mesh_data.weight;
				}
			} else {
				for(Mesh object:objects)
				{
					object.addModel(obj,chunks,x,y,z,data,biome,trans);
				}
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
		if (!materials.isEmpty())
			ret+="MAT "+Arrays.toString(materials.get(null, 0))+", ";
		ret+="STATE "+mesh_data.state+", ";
		if (mesh_data.transform != null)
			ret+="TRANS "+mesh_data.transform.toString()+", ";
		ret+="OBJECTS "+objects.size()+":";
		for(Mesh object:objects)
			ret+= "\n" + object.toString(depth + 1);

		return ret;
	}
}
