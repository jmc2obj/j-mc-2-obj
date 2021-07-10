package org.jmc;

import java.util.HashMap;
import java.util.HashSet;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.models.BlockModel;
import org.jmc.models.Cube;
import org.jmc.models.Mesh;
import org.jmc.registry.BlockstateEntry;
import org.jmc.registry.NamespaceID;
import org.jmc.registry.Registries;
import org.jmc.util.Filesystem.JmcConfFile;
import org.jmc.util.Log;
import org.jmc.util.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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


	private static HashMap<String, BlockInfo> blockTable;

	private static HashSet<String> unknownBlockIds;
	
	private static BlockInfo unknownBlock;


	private static void readConfig(HashMap<String, BlockInfo> blockTable) throws Exception
	{
		Document doc;
		try (JmcConfFile confFile = new JmcConfFile(CONFIG_FILE)) {
			if (!confFile.hasStream())
				throw new Exception("Cannot open configuration file " + CONFIG_FILE);
			
			doc = Xml.loadDocument(confFile.getInputStream());
		}
		XPath xpath = XPathFactory.newInstance().newXPath();

		NodeList blockNodes = (NodeList)xpath.evaluate("/blocks/block", doc, XPathConstants.NODESET);
		for (int i = 0; i < blockNodes.getLength(); i++)
		{
			Node blockNode = blockNodes.item(i);

			String id = Xml.getAttribute(blockNode, "id", "");

			String name = Xml.getAttribute(blockNode, "name", "");
			String modelName = "Registry";
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
			
			Boolean waterlogged = (Boolean)xpath.evaluate("waterlogged", blockNode, XPathConstants.BOOLEAN);
			if (waterlogged == null) {
				waterlogged = false;
			}

			boolean hasMtl = false;
			NodeList matNodes = (NodeList)xpath.evaluate("materials", blockNode, XPathConstants.NODESET);
			for (int j = 0; j < matNodes.getLength(); j++)
			{
				Node matNode = matNodes.item(j);
				
				Blockstate state = new Blockstate();
				int biome = -1;
				
				NamedNodeMap matAttribs = matNode.getAttributes();
				
				for (int k = 0; k < matAttribs.getLength(); k++) {
					Node attrib = matAttribs.item(k);
					String attrName = attrib.getNodeName();
					String attrVal = attrib.getNodeValue();
					if (attrName.equalsIgnoreCase("jmc_biome")) 
						biome = Integer.parseInt(attrVal, 10);
					else {
						state.put(attrName, attrVal);
					}
				}
				
				BlockData data = new BlockData(id, state);
				
				String mats = matNode.getTextContent();
				if (mats.trim().isEmpty() || biome < -1 || biome > 255 )//TODO biome 255 id limit needed?
				{
					Log.info("Block " + id + " has invalid material. Ignoring.");
					continue;
				}
				
				if(biome >= 0)
				{
					materials.put(biome, data.state, mats.split("\\s*,\\s*"));
				}
				else
				{
					if (!data.state.isEmpty())
						materials.put(data.state, mats.split("\\s*,\\s*"));
					else
						materials.put(mats.split("\\s*,\\s*"));
				}

				hasMtl = true;
			}

			if (!hasMtl)
			{
				Log.debug("Block " + id + " has no materials. Using default.");
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
			model.setConfigNodes(blockNode);

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
				
				mesh.propagateMaterials();
			}

			blockTable.put(id, new BlockInfo(id, name, materials, occlusion, model, waterlogged)); 
		}
	}

	private static void parseAttributes(Node meshNode, Mesh mesh) throws RuntimeException
	{
		Blockstate state = new Blockstate();
		NamedNodeMap meshAttribs = meshNode.getAttributes();
		
		if (meshAttribs != null) {
			for (int k = 0; k < meshAttribs.getLength(); k++) {
				Node attrib = meshAttribs.item(k);
				String attrName = attrib.getNodeName();
				String attrVal = attrib.getNodeValue();
				if (attrName.equalsIgnoreCase("id")) {
					mesh.mesh_data.id  = attrVal;
				}
				else if (attrName.equalsIgnoreCase("jmc_offset")) {
					if(attrVal.length()>0)
					{
						String [] tok=attrVal.split(",");
						if(tok.length!=3)
						{
							Log.info("Error parsing offset string: offset=\""+attrVal+"\"");
						}
						else
						{
							int x=Integer.parseInt(tok[0],10);
							int y=Integer.parseInt(tok[1],10);
							int z=Integer.parseInt(tok[2],10);
							mesh.mesh_data.offset = new Vertex(x,y,z);
						}
					}
				}
				else if (attrName.equalsIgnoreCase("jmc_fallthrough")) {
					mesh.mesh_data.fallthrough = Boolean.parseBoolean(attrVal);
				}
				else if (attrName.equalsIgnoreCase("jmc_material")) {
					BlockMaterial mat = new BlockMaterial();
					mat.put(new String[] {attrVal});
					mesh.setMaterials(mat);
				}
				else {
					//transform nodes have other attributes.
					if (meshNode.getNodeName().equalsIgnoreCase("mesh"))
						state.put(attrName, attrVal);
				}
			}
		}
		
		mesh.mesh_data.state = state;
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
				String name=child.getNodeName();
				Mesh new_mesh=new Mesh();				
				
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

		Transform newtransform;

		if(type.equals("translate"))
		{
			newtransform = Transform.translation(x, y, z);
		}
		else if(type.equals("scale"))
		{
			newtransform = Transform.scale(x, y, z);
		}
		else if(type.equals("rotate"))
		{
			newtransform = Transform.rotation(x, y, z);
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
		unknownBlock = new UnknownBlockInfo();

		// create table to keep track of unknown block ids found
		unknownBlockIds = new HashSet<String>();
		
		// create the blocks table
		Log.info("Reading blocks configuration file...");

		blockTable = new HashMap<String, BlockInfo>();
		readConfig(blockTable);

		Log.info("Loaded " + blockTable.size() + " block definitions.");
	}


	/**
	 * Gets the block information for the given block id.
	 * If the block id is not found, returns a default BlockInfo structure for 
	 * "unknown" blocks. The block id of the unknown block is always "". 
	 * 
	 * @param block Block data
	 * @return BlockInfo structure
	 */
	public static BlockInfo get(BlockData block)
	{
		BlockInfo bi = blockTable.get(block.id);
		if (bi == null && !block.id.isEmpty() && !unknownBlockIds.contains(block.id)) {
			Log.info("Found unknown block id: " + block.id);
			BlockstateEntry bs = Registries.getBlockstate(NamespaceID.fromString(block.id));
			
			unknownBlockIds.add(block.id);
		}

		return bi != null ? bi : unknownBlock;
	}
	
	public static HashMap<String, BlockInfo> getAll()
	{
		return blockTable;
	}

}
