package org.jmc;

import java.io.File;
import java.util.HashMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.models.BlockModel;
import org.jmc.models.Cube;
import org.jmc.models.Mesh;
import org.jmc.util.Filesystem;
import org.jmc.util.Log;
import org.jmc.util.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * This class reads the block definition file (blocks.conf) and makes the information
 * available to the program.
 * It also creates the appropriate model handlers for each block type.
 */
public class BlockTypes
{
	private static final String CONFIG_FILE = "conf/blocks.conf";


	private static HashMap<Short, BlockInfo> blockTable;

	private static BlockInfo unknownBlock;


	private static void readConfig(HashMap<Short, BlockInfo> blockTable) throws Exception
	{
		File confFile = new File(Filesystem.getDatafilesDir(), CONFIG_FILE);
		if (!confFile.canRead())
			throw new Exception("Cannot open configuration file " + CONFIG_FILE);

		Document doc = Xml.loadDocument(confFile);
		XPath xpath = XPathFactory.newInstance().newXPath();

		NodeList blockNodes = (NodeList)xpath.evaluate("/blocks/block", doc, XPathConstants.NODESET);
		for (int i = 0; i < blockNodes.getLength(); i++)
		{
			Node blockNode = blockNodes.item(i);

			short id = Short.parseShort(Xml.getAttribute(blockNode, "id", "0"), 10);
			if (id < 1)
			{
				Log.info("Skipping block with invalid id");
				continue;
			}

			String name = Xml.getAttribute(blockNode, "name", "");
			String modelName = "Cube";
			BlockInfo.Occlusion occlusion = BlockInfo.Occlusion.FULL; 
			BlockMaterial materials = new BlockMaterial();

			String aux;
			aux = (String)xpath.evaluate("model", blockNode, XPathConstants.STRING);
			if (aux != null && aux.length() > 0)
			{
				modelName = aux;
			}

			aux = (String)xpath.evaluate("occlusion", blockNode, XPathConstants.STRING);
			if (aux != null && aux.length() > 0)
			{
				try {
					occlusion = BlockInfo.Occlusion.valueOf(aux.toUpperCase());
				} catch (Exception e) {
					Log.info("Block " + id + " has invalid occlusion. Using default.");
				}
			}

			boolean hasMtl = false;
			NodeList matNodes = (NodeList)xpath.evaluate("materials", blockNode, XPathConstants.NODESET);
			for (int j = 0; j < matNodes.getLength(); j++)
			{
				Node matNode = matNodes.item(j);

				int data = Integer.parseInt(Xml.getAttribute(matNode, "data", "-1"), 10);
				int mask = Integer.parseInt(Xml.getAttribute(matNode, "mask", "-1"), 10);
				int biome = Integer.parseInt(Xml.getAttribute(matNode, "biome", "-1"), 10);
				String mats = matNode.getTextContent();
				if (data < -1 || data > 15 || mats.trim().isEmpty() || biome < -1 || biome > 255 )
				{
					Log.info("Block " + id + " has invalid material. Ignoring.");
					continue;
				}

				if (mask >= 0)
					materials.setDataMask((byte)mask);

				if(biome >= 0)
				{
					materials.put((byte)biome, (byte)data, mats.split("\\s*,\\s*"));
				}
				else
				{
					if (data >= 0)
						materials.put((byte)data, mats.split("\\s*,\\s*"));
					else
						materials.put(mats.split("\\s*,\\s*"));
				}

				hasMtl = true;
			}

			if (!hasMtl)
			{
				Log.info("Block " + id + " has no materials. Using default.");
				materials.put(new String[] { "unknown" });
			}

			BlockModel model;
			try {
				Class<?> modelClass = Class.forName("org.jmc.models." + modelName);
				model = (BlockModel)modelClass.getConstructor().newInstance();
			}
			catch (Exception e) {
				Log.info("Block " + id + " has invalid model. Using default.");
				model = new Cube();
			}
			model.setBlockId(id);
			model.setMaterials(materials);

			if(modelName.equals("Mesh"))
			{
				Mesh mesh=(Mesh)model;

				NodeList meshNodes = (NodeList)xpath.evaluate("mesh", blockNode, XPathConstants.NODESET);			
				for (int j = 0; j < meshNodes.getLength(); j++)
				{
					Node meshNode = meshNodes.item(j);
					try {
						parseMeshNode(meshNode,mesh);
					}catch (Exception e) {
						Log.info("Block " + id + " has invalid mesh definition. Ignoring.");
						continue;
					}					
				}

				NodeList trasNodes = (NodeList)xpath.evaluate("translate", blockNode, XPathConstants.NODESET);			
				for (int j = 0; j < trasNodes.getLength(); j++)
				{
					Node transNode = trasNodes.item(j);					
					try {
						parseTransNode(transNode,mesh);
					}catch (RuntimeException e) {
						Log.info("Block " + id + " has invalid mesh definition. Ignoring.");
						continue;
					}					
				}

				NodeList rotNodes = (NodeList)xpath.evaluate("rotate", blockNode, XPathConstants.NODESET);			
				for (int j = 0; j < rotNodes.getLength(); j++)
				{
					Node transNode = rotNodes.item(j);					
					try {
						parseTransNode(transNode,mesh);
					}catch (RuntimeException e) {
						Log.info("Block " + id + " has invalid mesh definition. Ignoring.");
						continue;
					}					
				}

				NodeList scaleNodes = (NodeList)xpath.evaluate("scale", blockNode, XPathConstants.NODESET);			
				for (int j = 0; j < scaleNodes.getLength(); j++)
				{
					Node transNode = scaleNodes.item(j);					
					try {
						parseTransNode(transNode,mesh);
					}catch (RuntimeException e) {
						Log.info("Block " + id + " has invalid mesh definition. Ignoring.");
						continue;
					}					
				}
			}

			blockTable.put(id, new BlockInfo(id, name, materials, occlusion, model)); 
		}
	}

	private static void parseAttributes(Node meshNode, Mesh mesh) throws RuntimeException
	{
		mesh.mesh_data.data = (byte) Integer.parseInt(Xml.getAttribute(meshNode, "data", "-1"), 10);
		mesh.mesh_data.mask = (byte) Integer.parseInt(Xml.getAttribute(meshNode, "mask", "-1"), 10);
		mesh.mesh_data.id  = (short) Integer.parseInt(Xml.getAttribute(meshNode, "id", "-1"), 10);
		String offset_str = Xml.getAttribute(meshNode, "offset", "");
		if(offset_str.length()>0)
		{
			String [] tok=offset_str.split(",");
			if(tok.length!=3)
			{
				Log.info("Error parsing offset string: offset=\""+offset_str+"\"");
			}
			else
			{
				int x=Integer.parseInt(tok[0],10);
				int y=Integer.parseInt(tok[1],10);
				int z=Integer.parseInt(tok[2],10);
				mesh.mesh_data.offset = new Vertex(x,y,z);
			}
		}
		
		if(Xml.getAttribute(meshNode, "fallthrough", "").toLowerCase().equals("true"))
			mesh.mesh_data.fallthrough=true;
	}
	
	private static void recurseChildren(NodeList children, Mesh mesh)
	{
		for(int i=0; i<children.getLength(); i++)
		{
			Node child=children.item(i);

			if(child.getNodeType()==Node.TEXT_NODE)
			{			
				String meshstr = child.getTextContent().trim();

				if(meshstr.isEmpty()) continue;

				Mesh new_mesh=new Mesh();
				new_mesh.loadObjFile(meshstr);
				parseAttributes(child, new_mesh);
				mesh.addMesh(new_mesh);
			}
			else if(child.getNodeType()==Node.ELEMENT_NODE)
			{
				Mesh new_mesh=new Mesh();				
				
				String name=child.getNodeName();
				if(name.equals("mesh"))
					parseMeshNode(child,new_mesh);
				else if(name.equals("translate") || name.equals("rotate") || name.equals("scale"))
					parseTransNode(child, new_mesh);
				
				mesh.addMesh(new_mesh);
			}
		}
	}
	
	private static void parseMeshNode(Node meshNode, Mesh mesh) throws RuntimeException
	{
		parseAttributes(meshNode, mesh);

		NodeList children=meshNode.getChildNodes();

		recurseChildren(children, mesh);
	}

	private static void parseTransNode(Node transNode, Mesh mesh) throws RuntimeException
	{
		parseAttributes(transNode, mesh);

		String type=transNode.getNodeName();
		String constraint=Xml.getAttribute(transNode, "const", "");
		String valstr=Xml.getAttribute(transNode, "value", "");
		String randval=Xml.getAttribute(transNode, "randval", "");

		float randmin=0,randmax=0;
		if(randval.length()>0)
		{
			String [] vals=randval.split(",");
			if(vals.length!=2)
			{
				Log.info("Wrong randval syntax.");
				throw new RuntimeException();
			}
			randmin=Float.parseFloat(vals[0]);
			randmax=Float.parseFloat(vals[1]);
			if(randmin>randmax)
			{
				float t=randmin;
				randmin=randmax;
				randmax=t;
			}
		}

		constraint=constraint.toLowerCase();

		float x=0,y=0,z=0;
		if(constraint.contains("x"))
		{
			if(valstr.length()>0)
				x=Float.parseFloat(valstr);
			else
				x=randmin+(float)Math.random()*(randmax-randmin);
		}
		if(constraint.contains("y"))
		{
			if(valstr.length()>0)
				y=Float.parseFloat(valstr);
			else
				y=randmin+(float)Math.random()*(randmax-randmin);
		}		
		if(constraint.contains("z"))
		{
			if(valstr.length()>0)
				z=Float.parseFloat(valstr);
			else
				z=randmin+(float)Math.random()*(randmax-randmin);
		}

		Transform newtransform=new Transform();

		if(type.equals("translate"))
		{
			newtransform.translate(x, y, z);
		}
		else if(type.equals("scale"))
		{
			newtransform.scale(x, y, z);
		}
		else if(type.equals("rotate"))
		{
			newtransform.rotate(x, y, z);
		}
		else
		{
			Log.info("Unknown transformation: "+type);
			throw new RuntimeException();
		}

		mesh.mesh_data.transform=newtransform;

		NodeList children=transNode.getChildNodes();
		
		recurseChildren(children, mesh);
	}

	/**
	 * Reads the configuration file.
	 * Must be called once at the start of the program.
	 * 
	 * @throws Exception if reading the configuration failed. In this case the program should abort.
	 */
	public static void initialize() throws Exception
	{
		// create a block to use when dealing with unknown block ids
		BlockMaterial materials = new BlockMaterial();
		materials.put(new String[] { "unknown" });

		BlockModel cube = new Cube();
		cube.setBlockId((short)-1);
		cube.setMaterials(materials);

		unknownBlock = new BlockInfo(-1, "unknown", materials, BlockInfo.Occlusion.FULL, cube);

		// create the blocks table
		Log.info("Reading blocks configuration file...");

		blockTable = new HashMap<Short, BlockInfo>();
		readConfig(blockTable);

		Log.info("Loaded " + blockTable.size() + " block definitions.");
	}


	/**
	 * Gets the block information for the given block id.
	 * If the block id is not found, returns a default BlockInfo structure for 
	 * "unknown" blocks
	 * 
	 * @param id Block id
	 * @return BlockInfo structure
	 */
	public static BlockInfo get(short id)
	{
		BlockInfo bi = blockTable.get(id);
		//if (bi == null)
		//	Log.debug("Unknow block id: " + id);

		return bi != null ? bi : unknownBlock;
	}

}
