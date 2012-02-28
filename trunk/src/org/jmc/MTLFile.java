package org.jmc;

import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class MTLFile {
	
	public enum Side
	{
		TOP,
		BOTTOM,
		LEFT,
		RIGHT,
		FRONT,
		BACK
	}
	
	Map<Integer,String> materials;
	
	public MTLFile()//default texture
	{
		materials=new TreeMap<Integer, String>();
		
		materials.put(1, "stone");
		materials.put(2, "grass-top");
		materials.put(3, "grass-bottom");
		materials.put(4, "grass-side");
		materials.put(5, "dirt");
	}
	
	public int getMaterialId(int id, Side side)
	{
		switch(id)
		{
		case 1:
			return 1;
		case 2:
			if(side==Side.TOP)
				return 2;
			else if(side==Side.BOTTOM)
				return 3;
			else return 4;
		case 3:
			return 5;
		default:
			return -1;
		}
	}
	
	
	public String getMaterial(int id)
	{
		if(!materials.containsKey(id))
			return "unknown";
		else return materials.get(id);
	}
	
	public void header(PrintWriter out)
	{
		out.println("mtllib minecraft.mtl");
		out.println();
	}

}
