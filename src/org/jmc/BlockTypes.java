package org.jmc;

import java.io.File;
import java.util.HashMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jmc.models.BlockModel;
import org.jmc.models.Cube;
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
	private static final String CONFIG_FILE = "blocks.conf";
	
	
	private static HashMap<Short, BlockInfo> blockTable;
	
	private static BlockInfo unknownBlock;
	
	
	private static void readConfig(HashMap<Short, BlockInfo> blockTable) throws Exception
	{
		File confFile = new File(Utility.getDatafilesDir(), CONFIG_FILE);
		if (!confFile.canRead())
			throw new Exception("Cannot open configuration file " + CONFIG_FILE);
		
		Document doc = XmlUtil.loadDocument(confFile);
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		NodeList blockNodes = (NodeList)xpath.evaluate("/blocks/block", doc, XPathConstants.NODESET);
		for (int i = 0; i < blockNodes.getLength(); i++)
		{
			Node blockNode = blockNodes.item(i);
			
			short id = Short.parseShort(XmlUtil.getAttribute(blockNode, "id", "0"), 10);
			if (id < 1)
			{
				Utility.logInfo("Skipping block with invalid id");
				continue;
			}

			String name = XmlUtil.getAttribute(blockNode, "name", "");
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
					Utility.logInfo("Block " + id + " has invalid occlusion. Using default.");
				}
			}

			boolean hasMtl = false;
			NodeList matNodes = (NodeList)xpath.evaluate("materials", blockNode, XPathConstants.NODESET);
			for (int j = 0; j < matNodes.getLength(); j++)
			{
				Node matNode = matNodes.item(j);

				int data = Integer.parseInt(XmlUtil.getAttribute(matNode, "data", "-1"), 10);
				int mask = Integer.parseInt(XmlUtil.getAttribute(matNode, "mask", "-1"), 10);
				String mats = matNode.getTextContent();
				if (data < -1 || data > 15 || mats.trim().isEmpty())
				{
					Utility.logInfo("Block " + id + " has invalid material. Ignoring.");
					continue;
				}
				
				if (mask >= 0)
					materials.setDataMask((byte)mask);
				
				if (data >= 0)
					materials.put((byte)data, mats.split("\\s*,\\s*"));
				else
					materials.put(mats.split("\\s*,\\s*"));
				
				hasMtl = true;
			}
			
			if (!hasMtl)
			{
				Utility.logInfo("Block " + id + " has no materials. Using default.");
				materials.put(new String[] { "unknown" });
			}
			
			BlockModel model;
			try {
				Class<?> modelClass = Class.forName("org.jmc.models." + modelName);
				model = (BlockModel)modelClass.getConstructor().newInstance();
			}
			catch (Exception e) {
				Utility.logInfo("Block " + id + " has invalid model. Using default.");
				model = new Cube();
			}
			model.setBlockId(id);
			model.setMaterials(materials);
			
			blockTable.put(id, new BlockInfo(id, name, materials, occlusion, model)); 
		}
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
		Utility.logInfo("Reading blocks configuration file...");

		blockTable = new HashMap<Short, BlockInfo>();
		readConfig(blockTable);

		Utility.logInfo("Loaded " + blockTable.size() + " block definitions.");
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
		return bi != null ? bi : unknownBlock;
	}
	
}
