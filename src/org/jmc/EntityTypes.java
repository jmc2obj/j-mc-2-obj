package org.jmc;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_String;
import org.jmc.entities.Entity;
import org.jmc.entities.models.EntityModel;
import org.jmc.entities.models.Mesh;
import org.jmc.registry.NamespaceID;
import org.jmc.registry.Registries;
import org.jmc.util.Filesystem.JmcConfFile;
import org.jmc.util.Log;
import org.jmc.util.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EntityTypes {

	private static final String CONFIG_FILE = "conf/entities.conf";

	private static Map<String, Entity> entities;

	private static void readConfig() throws Exception {
		Document doc;
		try (JmcConfFile confFile = new JmcConfFile(CONFIG_FILE)) {
			if (!confFile.hasStream())
				throw new Exception("Cannot open configuration file " + CONFIG_FILE);
			
			 doc = Xml.loadDocument(confFile.getInputStream());
		}
		XPath xpath = XPathFactory.newInstance().newXPath();

		NodeList entityNodes = (NodeList) xpath.evaluate("/entities/entity", doc, XPathConstants.NODESET);
		for (int i = 0; i < entityNodes.getLength(); i++) {
			Node entityNode = entityNodes.item(i);

			String id = Xml.getAttribute(entityNode, "id", "");
			if (id == null || id.length() == 0) {
				Log.info("Skipping entity with invalid id");
				continue;
			}

			String handlerName = (String) xpath.evaluate("handler", entityNode, XPathConstants.STRING);
			if (handlerName == null || handlerName.length() == 0) {
				Log.info("Entity " + id + " has invalid handler. Skipping.");
				continue;
			}

			String modelName = (String) xpath.evaluate("model", entityNode, XPathConstants.STRING);
			if (modelName == null || modelName.length() == 0) {
				Log.info("Entity " + id + " has invalid model. Skipping.");
				continue;
			}

			BlockMaterial materials = new BlockMaterial();
			boolean hasMtl = false;
			NodeList matNodes = (NodeList) xpath.evaluate("materials", entityNode, XPathConstants.NODESET);
			for (int j = 0; j < matNodes.getLength(); j++) {
				Node matNode = matNodes.item(j);

				String mats = matNode.getTextContent();
				String[] splitmats = mats.split("\\s*,\\s*");
				NamespaceID[] nsMats = new NamespaceID[splitmats.length];
				for (int k = 0; k < splitmats.length; k++) {
					String mat = splitmats[k];
					if (mat != null)
						nsMats[k] = NamespaceID.fromString(mat);
				}

				materials.put(nsMats);

				hasMtl = true;
			}

			if (!hasMtl) {
				// Log.info("Entity " + id +
				// " has no materials. Using default.");
				materials.put(new NamespaceID[] { Registries.UNKNOWN_TEX_ID });
			}

			EntityModel entityModel;
			try {
				Class<?> entityModelClass = Class.forName("org.jmc.entities.models." + modelName);
				entityModel = (EntityModel) entityModelClass.getConstructor().newInstance();
			} catch (Exception e) {
				Log.info("Entity " + id + " has invalid model. Skipping.");
				continue;
			}

			Entity entity;
			try {
				Class<?> entityClass = Class.forName("org.jmc.entities." + handlerName);
				entity = (Entity) entityClass.getConstructor(String.class).newInstance(id);
			} catch (Exception e) {
				Log.info("Entity " + id + " has invalid handler. Skipping.");
				continue;
			}

			if (modelName.equals("Mesh")) {
				Mesh mesh = (Mesh) entityModel;

				NodeList meshNodes = (NodeList) xpath.evaluate("mesh", entityNode, XPathConstants.NODESET);

				for (int j = 0; j < meshNodes.getLength(); j++) {
					Node meshNode = meshNodes.item(j);

					int data = Integer.parseInt(Xml.getAttribute(meshNode, "data", "-1"), 10);
					int mask = Integer.parseInt(Xml.getAttribute(meshNode, "mask", "-1"), 10);

					String meshstr = meshNode.getTextContent();
					if (data < -1 || data > 15 || mask < -1 || mask > 15 || meshstr.trim().isEmpty()) {
						Log.info("Block " + id + " has invalid mesh definition. Ignoring.");
						continue;
					}

					mesh.addMesh(meshstr);

				}
			}

			entityModel.setMaterials(materials);
			entity.useModel(entityModel);
			entities.put(id, entity);
		}
	}

	public static void initialize() throws Exception {
		Log.info("Reading entities configuration file...");

		entities = new HashMap<String, Entity>();
		readConfig();

		Log.info("Loaded " + entities.size() + " entity definitions.");
	}

	public static Entity getEntity(TAG_Compound entity) {
		TAG_String id = (TAG_String) entity.getElement("id");
		if (id == null)
			return null;
		if (!entities.containsKey(id.value))
			return null;
		return entities.get(id.value);
	}

}
