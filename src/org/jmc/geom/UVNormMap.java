package org.jmc.geom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class mapping UV and normal configuration for individual faces.
 * 
 * @author danijel
 *
 */
public class UVNormMap {

	List<UV> uvlist;
	List<Vertex> normlist;	
	Map<UV,Integer> uvmap;
	Map<Vertex,Integer> normmap;
	
	
	public UVNormMap() 
	{
		uvlist=new ArrayList<UV>(); 
		normlist=new ArrayList<Vertex>();
		uvmap=new HashMap<UV, Integer>();
		normmap=new HashMap<Vertex, Integer>();
		
		//adding default values
		uvlist.add(new UV(1,0));
		uvlist.add(new UV(1,1));
		uvlist.add(new UV(0,1));
		uvlist.add(new UV(0,0));
		normlist.add(new Vertex(0,-1,0));
		normlist.add(new Vertex(0,1,0));
		normlist.add(new Vertex(1,0,0));
		normlist.add(new Vertex(-1,0,0));
		normlist.add(new Vertex(0,0,1));
		normlist.add(new Vertex(0,0,-1));
		
		int i=1;
		for(UV s:uvlist)
		{
			uvmap.put(s, i);
			i++;
		}
		i=1;
		for(Vertex s:normlist)
		{
			normmap.put(s, i);
			i++;
		}
	}

	public int getUVId(UV uv)
	{
		if(uvmap.containsKey(uv))
		{
			return uvmap.get(uv);
		}
		
		uvlist.add(uv);
		uvmap.put(uv, uvlist.size());
		return uvlist.size();
	}
	
	public int getNormId(Vertex norm)
	{
		if(normmap.containsKey(norm))
		{
			return normmap.get(norm);
		}
		
		normlist.add(norm);
		normmap.put(norm, normlist.size());
		return normlist.size();
	}
	
	public UV getUV(int id)
	{
		return uvlist.get(id-1);
	}
	
	public Vertex getNorm(int id)
	{
		return normlist.get(id-1);
	}
	
}
