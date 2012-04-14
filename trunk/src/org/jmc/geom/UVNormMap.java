package org.jmc.geom;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class mapping UV and normal configuration for individual faces.
 * 
 * @author danijel
 *
 */
public class UVNormMap {

	List<String> uv,normal;	
	Map<String,Integer> uvmap,normmap;
	
	
	public UVNormMap() 
	{
		uv=new LinkedList<String>(); 
		normal=new LinkedList<String>();
		uvmap=new HashMap<String, Integer>();
		normmap=new HashMap<String, Integer>();
		
		//adding default values
		uv.add("1 0");
		uv.add("1 1");
		uv.add("0 1");
		uv.add("0 0");
		normal.add("0 -1 0");
		normal.add("0 1 0");
		normal.add("1 0 0");
		normal.add("-1 0 0");
		normal.add("0 0 1");
		normal.add("0 0 -1");
		normal.add("0.7 0 0.7");
		normal.add("-0.7 0 0.7");
		
		int i=1;
		for(String s:uv)
		{
			uvmap.put(s, i);
			i++;
		}
		i=1;
		for(String s:normal)
		{
			normmap.put(s, i);
			i++;
		}
	}

	public void calculate(Side side, Face face)
	{
		int norm_idx=6;		
		switch(side)
		{
		case BOTTOM: norm_idx=1; break;
		case TOP: norm_idx=2; break;
		case RIGHT: norm_idx=3; break;
		case LEFT: norm_idx=4; break;
		case BACK: norm_idx=5; break;
		case FRONT: norm_idx=6; break;
		case BACKRIGHT: norm_idx=7; break;
		case FRONTRIGHT: norm_idx=8; break;
		default: norm_idx=6;
		}
		
		for(int i=0; i<4; i++)
		{
			face.normals[i]=norm_idx;
			face.uv[i]=i+1;
		}
	}
	
	public int getUVId(String str)
	{
		if(uvmap.containsKey(str))
		{
			return uvmap.get(str);
		}
		
		uv.add(str);
		uvmap.put(str, uv.size());
		return uv.size();
	}
	
	public int getNormId(String str)
	{
		if(normmap.containsKey(str))
		{
			return normmap.get(str);
		}
		
		normal.add(str);
		normmap.put(str, normal.size());
		return normal.size();
	}
	
	public String getUV(int id)
	{
		return uv.get(id-1);
	}
	
	public String getNorm(int id)
	{
		return normal.get(id-1);
	}
	
	public void print(PrintWriter out)
	{
		for(String s:uv)
		{
			out.println("vt "+s);
		}
		
		for(String s:normal)
		{
			out.println("vn "+s);
		}
	}
}
