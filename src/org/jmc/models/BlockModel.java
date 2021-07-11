package org.jmc.models;

import java.awt.Rectangle;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jmc.BlockData;
import org.jmc.BlockMaterial;
import org.jmc.BlockTypes;
import org.jmc.Options;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Log;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Base class for the block model handlers. These handlers are responsible for
 * rendering the geometry that represents the blocks.
 */
@ParametersAreNonnullByDefault
public abstract class BlockModel {
	public String blockId = "";
	@CheckForNull
	protected Node configNode = null;
	@Nonnull
	protected BlockMaterial materials = new BlockMaterial();

	/**
	 * Id of the block this model will be rendering. This information may
	 * influence the behavior of the model.
	 */
	public void setBlockId(String id) {
		this.blockId = id;
	}

	/**
	 * Set the materials for this block.
	 */
	public void setMaterials(BlockMaterial val) {
		this.materials = val;
	}

	/**
	 * Nodes of config file of this block
	 */
	public void setConfigNodes(@CheckForNull Node blockNode) { this.configNode = blockNode; }

	/**
	 * returns a config Value
	 * @param tagName
	 * @param index
	 * @return String
	 */
	public String getConfigNodeValue(String tagName, int index) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String configValue = "";
		try {
			NodeList configNode = (NodeList) xpath.evaluate(tagName, this.configNode, XPathConstants.NODESET);
			Node currentItem = configNode.item(index);
			configValue = currentItem.getTextContent();
		} catch (Exception e) {
			Log.error("Cant read config Node", e, true);
		}
		return configValue;
	}


	/**
	 * Expand the materials to the full 6 side definition used by addBox
	 */
	@Nonnull
	protected String[] getMtlSides(BlockData data, int biome) {
		String[] abbrMtls = materials.get(data.state, biome);

		String[] mtlSides = new String[6];
		if (abbrMtls.length < 2) {
			mtlSides[0] = abbrMtls[0];
			mtlSides[1] = abbrMtls[0];
			mtlSides[2] = abbrMtls[0];
			mtlSides[3] = abbrMtls[0];
			mtlSides[4] = abbrMtls[0];
			mtlSides[5] = abbrMtls[0];
		} else if (abbrMtls.length < 3) {
			mtlSides[0] = abbrMtls[0];
			mtlSides[1] = abbrMtls[1];
			mtlSides[2] = abbrMtls[1];
			mtlSides[3] = abbrMtls[1];
			mtlSides[4] = abbrMtls[1];
			mtlSides[5] = abbrMtls[0];
		} else if (abbrMtls.length < 6) {
			mtlSides[0] = abbrMtls[0];
			mtlSides[1] = abbrMtls[1];
			mtlSides[2] = abbrMtls[1];
			mtlSides[3] = abbrMtls[1];
			mtlSides[4] = abbrMtls[1];
			mtlSides[5] = abbrMtls[2];
		} else {
			mtlSides[0] = abbrMtls[0];
			mtlSides[1] = abbrMtls[1];
			mtlSides[2] = abbrMtls[2];
			mtlSides[3] = abbrMtls[3];
			mtlSides[4] = abbrMtls[4];
			mtlSides[5] = abbrMtls[5];
		}

		return mtlSides;
	}
	
	/**
	 * If Occlusion is set to custom this will be called from a neighbouring block
	 * to know if a face should be drawn
	 * 
	 * @param side the side the neighbouring block is at.
	 * @param data the data for this block.
	 * @return true if this block occludes side.
	 */
	protected boolean getCustomOcclusion(Direction side, BlockData neighbourData, BlockData data) {
		return true;
	}

	/**
	 * Helper method to check if the side of a cube needs to be drawn, based on
	 * the occlusion type of the neighbouring block and whether or not the block
	 * is at the world (or selection) edge.
	 * 
	 * @param neighbourId
	 *            The neighbouring block, or null if there is no neighbour
	 *            (because the block is at the world edge)
	 * @param side
	 *            Side to check
	 * @return true if side needs to be drawn
	 */
	protected boolean drawSide(Direction side, BlockData data, @CheckForNull BlockData neighbourData) {
		if (Options.objectPerBlock)
			return true;

		if (neighbourData == null || neighbourData.id.equals(""))
			return Options.renderSides;

		if (neighbourData.id.endsWith("air") || Options.excludeBlocks.contains(neighbourData.id))
			return true;

		if (Options.objectPerMaterial && Options.objectPerMaterialOcclusionBarrier && (!neighbourData.id.equals(data.id)))
			return true;

		switch (BlockTypes.get(neighbourData).getOcclusion()) {
		case FULL:
			return false;
		case NONE:
			return true;
		case TRANSPARENT:
		case VOLUME:
			return !neighbourData.id.equals(data.id);
		case BOTTOM:
			return side != Direction.UP;
		case CUSTOM:
			return !BlockTypes.get(neighbourData).getModel().getCustomOcclusion(side.getOpposite(), data, neighbourData);
		default:
			return false;
		}
	}

	/**
	 * Helper method to check which sides of a cube need to be drawn, based on
	 * the occlusion type of the neighbouring blocks and whether or not the block
	 * is at the world (or selection) edge.
	 * 
	 * @param chunks
	 *            World chunk data
	 * @param x
	 *            Block x coordinate
	 * @param y
	 *            Block y coordinate
	 * @param z
	 *            Block z coordinate
	 * @return Whether to draw each side, in order UP, NORTH, SOUTH, WEST,
	 *         EAST, DOWN
	 */
	protected boolean[] drawSides(ThreadChunkDeligate chunks, int x, int y, int z, BlockData data) {
		int xmin, xmax, ymin, ymax, zmin, zmax;
		Rectangle xy, xz;
		xy = chunks.getXYBoundaries();
		xz = chunks.getXZBoundaries();
		xmin = xy.x;
		xmax = xmin + xy.width - 1;
		ymin = xy.y;
		ymax = ymin + xy.height - 1;
		zmin = xz.y;
		zmax = zmin + xz.height - 1;

		boolean sides[] = new boolean[6];

		sides[0] = drawSide(Direction.UP, data, y == ymax ? null : chunks.getBlockData(x, y + 1, z));
		sides[1] = drawSide(Direction.NORTH, data, z == zmin ? null : chunks.getBlockData(x, y, z - 1));
		sides[2] = drawSide(Direction.SOUTH, data, z == zmax ? null : chunks.getBlockData(x, y, z + 1));
		sides[3] = drawSide(Direction.WEST, data, x == xmin ? null : chunks.getBlockData(x - 1, y, z));
		sides[4] = drawSide(Direction.EAST, data, x == xmax ? null : chunks.getBlockData(x + 1, y, z));
		sides[5] = drawSide(Direction.DOWN, data, y == ymin ? null : chunks.getBlockData(x, y - 1, z));

		return sides;
	}

	/**
	 * Helper method to add a box to given OBJFile.
	 * 
	 * @param obj
	 *            OBJFile to add to
	 * @param xs
	 *            Start x coordinate
	 * @param ys
	 *            Start y coordinate
	 * @param zs
	 *            Start z coordinate
	 * @param xe
	 *            End x coordinate
	 * @param ye
	 *            End y coordinate
	 * @param ze
	 *            End z coordinate
	 * @param trans
	 *            Transform to apply to the vertex coordinates. If null, no
	 *            transform is applied
	 * @param mtlSides
	 *            Material for each side, in order UP, NORTH, SOUTH, WEST,
	 *         EAST, DOWN
	 * @param uvSides
	 *            Texture coordinates for each side, in order UP, NORTH, SOUTH,
	 *         	  WEST, EAST, DOWN. If null, uses default coordinates for all
	 *            sides. If an individual side is null, uses default coordinates
	 *            for that side.
	 * @param drawSides
	 *            Whether to draw each side, in order UP, NORTH, SOUTH, WEST,
	 *            EAST, DOWN. If null, draws all sides.
	 */
	protected void addBox(ChunkProcessor obj, float xs, float ys, float zs, float xe, float ye, float ze,
			@CheckForNull Transform trans, String[] mtlSides, @CheckForNull UV[][] uvSides, @CheckForNull boolean[] drawSides) {
		Vertex[] vertices = new Vertex[4];

		if (drawSides == null || drawSides[0]) { // top
			vertices[0] = new Vertex(xs, ye, ze);
			vertices[1] = new Vertex(xe, ye, ze);
			vertices[2] = new Vertex(xe, ye, zs);
			vertices[3] = new Vertex(xs, ye, zs);
			obj.addFace(vertices, uvSides == null ? null : uvSides[0], trans, mtlSides[0]);
		}
		if (drawSides == null || drawSides[1]) { // front
			vertices[0] = new Vertex(xe, ys, zs);
			vertices[1] = new Vertex(xs, ys, zs);
			vertices[2] = new Vertex(xs, ye, zs);
			vertices[3] = new Vertex(xe, ye, zs);
			obj.addFace(vertices, uvSides == null ? null : uvSides[1], trans, mtlSides[1]);
		}
		if (drawSides == null || drawSides[2]) { // back
			vertices[0] = new Vertex(xs, ys, ze);
			vertices[1] = new Vertex(xe, ys, ze);
			vertices[2] = new Vertex(xe, ye, ze);
			vertices[3] = new Vertex(xs, ye, ze);
			obj.addFace(vertices, uvSides == null ? null : uvSides[2], trans, mtlSides[2]);
		}
		if (drawSides == null || drawSides[3]) { // left
			vertices[0] = new Vertex(xs, ys, zs);
			vertices[1] = new Vertex(xs, ys, ze);
			vertices[2] = new Vertex(xs, ye, ze);
			vertices[3] = new Vertex(xs, ye, zs);
			obj.addFace(vertices, uvSides == null ? null : uvSides[3], trans, mtlSides[3]);
		}
		if (drawSides == null || drawSides[4]) { // right
			vertices[0] = new Vertex(xe, ys, ze);
			vertices[1] = new Vertex(xe, ys, zs);
			vertices[2] = new Vertex(xe, ye, zs);
			vertices[3] = new Vertex(xe, ye, ze);
			obj.addFace(vertices, uvSides == null ? null : uvSides[4], trans, mtlSides[4]);
		}
		if (drawSides == null || drawSides[5]) { // bottom
			vertices[0] = new Vertex(xe, ys, ze);
			vertices[1] = new Vertex(xs, ys, ze);
			vertices[2] = new Vertex(xs, ys, zs);
			vertices[3] = new Vertex(xe, ys, zs);
			obj.addFace(vertices, uvSides == null ? null : uvSides[5], trans, mtlSides[5]);
		}
	}

	/**
	 * Helper method to add a box to given OBJFile.
	 * 
	 * @param obj
	 *            OBJFile to add to
	 * @param x
	 *            Block x coordinate
	 * @param y
	 *            Block y coordinate
	 * @param z
	 *            Block z coordinate
	 * @param xs
	 *            Start x coordinate
	 * @param ys
	 *            Start y coordinate
	 * @param zs
	 *            Start z coordinate
	 * @param xe
	 *            End x coordinate
	 * @param ye
	 *            End y coordinate
	 * @param ze
	 *            End z coordinate
	 * @param trans
	 *            Transform to apply to the vertex coordinates. If null, no
	 *            transform is applied
	 * @param mtlSides
	 *            Material for each side, in order UP, NORTH, SOUTH, WEST,
	 *         EAST, DOWN
	 * @param drawSides
	 *            Whether to draw each side, in order UP, NORTH, SOUTH, WEST,
	 *            EAST, DOWN. If null, draws all sides.
	 */
	protected void addBoxCubeUV(ChunkProcessor obj, float xs, float ys, float zs, float xe, float ye, float ze,
			Transform trans, String[] mtlSides, @CheckForNull boolean[] drawSides) {
		UV[] uvU = new UV[] { new UV(xs+0.5f, -ze+0.5f), new UV(xe+0.5f, -ze+0.5f), new UV(xe+0.5f, -zs+0.5f), new UV(xs+0.5f, -zs+0.5f) };
		UV[] uvN = new UV[] { new UV(-xe+0.5f, ys+0.5f), new UV(-xs+0.5f, ys+0.5f), new UV(-xs+0.5f, ye+0.5f), new UV(-xe+0.5f, ye+0.5f) };
		UV[] uvS = new UV[] { new UV(xs+0.5f, ys+0.5f), new UV(xe+0.5f, ys+0.5f), new UV(xe+0.5f, ye+0.5f), new UV(xs+0.5f, ye+0.5f) };
		UV[] uvW = new UV[] { new UV(zs+0.5f, ys+0.5f), new UV(ze+0.5f, ys+0.5f), new UV(ze+0.5f, ye+0.5f), new UV(zs+0.5f, ye+0.5f) };
		UV[] uvE = new UV[] { new UV(-ze+0.5f, ys+0.5f), new UV(-zs+0.5f, ys+0.5f), new UV(-zs+0.5f, ye+0.5f), new UV(-ze+0.5f, ye+0.5f) };
		UV[] uvD = new UV[] { new UV(xe+0.5f, ze+0.5f), new UV(xs+0.5f, ze+0.5f), new UV(xs+0.5f, zs+0.5f), new UV(xe+0.5f, zs+0.5f) };
		UV[][] uvSides = new UV[][] { uvU, uvN, uvS, uvW, uvE, uvD };
		addBox(obj, xs, ys, zs, xe, ye, ze, trans, mtlSides, uvSides, drawSides);
	}

	/**
	 * Adds the block to the given OBJFile.
	 * 
	 * @param obj
	 *            OBJFile to add the model to.
	 * @param chunks
	 *            World chunk data
	 * @param x
	 *            Block x coordinate
	 * @param y
	 *            Block y coordinate
	 * @param z
	 *            Block z coordinate
	 * @param data
	 *            Block data value
	 */
	public abstract void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome);

}
