package org.jmc;

import java.awt.Rectangle;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jmc.geom.UV;
import org.jmc.util.Log;
import org.jmc.util.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class UVRecalculate {

	private static int width, height;
	private static Map<String,Rectangle> uv_map=null;
	
	public static void load(File uvFile) throws Exception
	{
		uv_map=new HashMap<String, Rectangle>();
		
		Document doc = Xml.loadDocument(uvFile);
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		String name;
		int u,v,w,h;
		
		Element root=(Element)xpath.evaluate("/textures", doc, XPathConstants.NODE);
		width=Integer.parseInt(root.getAttribute("width"));
		height=Integer.parseInt(root.getAttribute("width"));
		
		NodeList textures = (NodeList)xpath.evaluate("/textures/texture", doc, XPathConstants.NODESET);
		for(int i=0; i<textures.getLength(); i++)
		{
			Element texture=(Element)textures.item(i);
			name=texture.getTextContent().trim();
			u=Integer.parseInt(texture.getAttribute("u"));
			v=Integer.parseInt(texture.getAttribute("v"));
			w=Integer.parseInt(texture.getAttribute("w"));
			h=Integer.parseInt(texture.getAttribute("h"));
			uv_map.put(name, new Rectangle(u,v,w,h));
		}		
	}
	
	
	public static UV[] recalculate(UV[] uvs, String mtl_name)
	{		
		if(uv_map==null) return uvs;
		
		Rectangle rect=uv_map.get(mtl_name);
		
		if(rect==null)
		{
			Log.info("WARNING: cannot recalculate material: "+mtl_name);
			return uvs;
		}
		
		float sx=rect.x/(float)width;
		float sy=1.0f-((rect.y+rect.height)/(float)height);
		float sw=rect.width/(float)width;
		float sh=rect.height/(float)height;
		
		for(UV uv:uvs)
		{
			if(uv.recalculated) continue;

			uv.u=uv.u*sw+sx;
			uv.v=uv.v*sh+sy;
			uv.recalculated=true;
		}
		
		return uvs;
	}
}
