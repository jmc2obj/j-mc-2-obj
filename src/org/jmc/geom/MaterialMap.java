package org.jmc.geom;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Id map of material names to optimize the faces class.
 * @author danijel
 *
 */
public class MaterialMap {

	Vector<String> material_list;
	Map<String,Integer> material_map;
	
	public MaterialMap()
	{
		material_list=new Vector<String>();
		material_map=new HashMap<String, Integer>();

		material_map.put("unknown", -1);
	}
	
	public int getMaterialID(String mtl)
	{
		if(material_map.containsKey(mtl))
			return material_map.get(mtl);
		else
		{
			int ret=material_list.size();
			material_list.add(mtl);
			material_map.put(mtl, ret);
			return ret;
		}
	}
	
	public String getMaterialName(int id)
	{
		if(id==-1) return "unknown";
		
		if(id<0 || id>=material_list.size()) return null;
		
		return material_list.elementAt(id);
	}
	
}
