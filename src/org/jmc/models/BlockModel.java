package org.jmc.models;

import java.awt.Rectangle;

import org.jmc.BlockMaterial;
import org.jmc.BlockTypes;
import org.jmc.ChunkDataBuffer;
import org.jmc.OBJFile;
import org.jmc.OBJFile.Side;
import org.jmc.Vertex;


/**
 * Base class for the block model handlers.
 * These handlers are responsible for rendering the geometry that represents the blocks.
 */
public abstract class BlockModel
{
	protected short blockId = -1;
	protected BlockMaterial materials = null;
	

	/**
	 * Id of the block this model will be rendering.
	 * This information may influence the behavior of the model.
	 */
	public void setBlockId(short val) {
		this.blockId = val;
	}
	
	/**
	 * Set the materials for this block.
	 */
	public void setMaterials(BlockMaterial val)
	{
		this.materials = val;
	}


	/**
	 * Helper method to check if the side of a cube needs to be drawn, based on 
	 * the occlusion type of the neighboring block.
	 *  
	 * @param neighborId Id of the neighboring block
	 * @param side Side to check
	 * @return true if side needs to be drawn
	 */
	protected boolean drawSide(Side side, short neighborId)
	{
		if (neighborId == 0)
			return true;
		
		switch(BlockTypes.get(neighborId).occlusion)
		{
			case FULL:
				return false;
			case NONE:
				return true;
			case TRANSPARENT:
				return neighborId != blockId;
			case BOTTOM:
				return side != Side.TOP;
			default:
				return false;
		}
	}

	
	/**
	 * Helper method to check which sides of a cube need to be drawn, based on 
	 * the occlusion type of the neighboring blocks and whether or not the block
	 * is at the world (or selection) edge.
	 * 
	 * @param chunks World chunk data
	 * @param x Block x coordinate
	 * @param y Block y coordinate
	 * @param z Block z coordinate
	 * @return Whether to draw each side, in order TOP, FRONT, BACK, LEFT, RIGHT, BOTTOM
	 */
	protected boolean[] drawSides(ChunkDataBuffer chunks, int x, int y, int z)
	{
		int xmin,xmax,ymin,ymax,zmin,zmax;
		Rectangle xy,xz;
		xy=chunks.getXYBoundaries();
		xz=chunks.getXZBoundaries();
		xmin=xy.x;
		xmax=xmin+xy.width-1;
		ymin=xy.y;
		ymax=ymin+xy.height-1;
		zmin=xz.y;
		zmax=zmin+xz.height-1;

		boolean sides[] = new boolean[6];

		sides[0] = y==ymax || drawSide(Side.TOP,    chunks.getBlockID(x, y+1, z));
		sides[1] = z==zmin || drawSide(Side.FRONT,  chunks.getBlockID(x, y, z-1));
		sides[2] = z==zmax || drawSide(Side.BACK,   chunks.getBlockID(x, y, z+1));
		sides[3] = x==xmin || drawSide(Side.LEFT,   chunks.getBlockID(x-1, y, z));
		sides[4] = x==xmax || drawSide(Side.RIGHT,  chunks.getBlockID(x+1, y, z));
		sides[5] = y==ymin || drawSide(Side.BOTTOM, chunks.getBlockID(x, y-1, z));
		
		return sides;
	}

	
	/**
	 * Helper method to add a box to given OBJFile.
	 * 
	 * @param obj OBJFile to add to
	 * @param xs Start x coordinate
	 * @param ys Start y coordinate
	 * @param zs Start z coordinate
	 * @param xe End x coordinate
	 * @param ye End y coordinate
	 * @param ze End z coordinate
	 * @param drawSides Whether to draw each side, in order TOP, FRONT, BACK, LEFT, RIGHT, BOTTOM
	 * @param mtlSides Material for each side, in order TOP, FRONT, BACK, LEFT, RIGHT, BOTTOM
	 */
	protected void addBox(OBJFile obj, float xs, float ys, float zs, float xe, float ye, float ze, boolean[] drawSides, String[] mtlSides)
	{
		Vertex[] vertices = new Vertex[4];

		if(drawSides[0])
		{
			vertices[0] = new Vertex(xe,ye,ze);
			vertices[1] = new Vertex(xe,ye,zs);			
			vertices[2] = new Vertex(xs,ye,zs);
			vertices[3] = new Vertex(xs,ye,ze);
			obj.addFace(vertices, null, Side.TOP, mtlSides[0]);
		}
		if(drawSides[1])
		{
			vertices[0] = new Vertex(xs,ys,zs);
			vertices[1] = new Vertex(xs,ye,zs);
			vertices[2] = new Vertex(xe,ye,zs);
			vertices[3] = new Vertex(xe,ys,zs);
			obj.addFace(vertices, null, Side.FRONT, mtlSides[1]);
		}
		if(drawSides[2])
		{
			vertices[0] = new Vertex(xe,ys,ze);
			vertices[1] = new Vertex(xe,ye,ze);
			vertices[2] = new Vertex(xs,ye,ze);
			vertices[3] = new Vertex(xs,ys,ze);
			obj.addFace(vertices, null ,Side.BACK, mtlSides[2]);
		}
		if(drawSides[3])
		{
			vertices[0] = new Vertex(xs,ys,ze);
			vertices[1] = new Vertex(xs,ye,ze);
			vertices[2] = new Vertex(xs,ye,zs);
			vertices[3] = new Vertex(xs,ys,zs);
			obj.addFace(vertices, null, Side.LEFT, mtlSides[3]);
		}
		if(drawSides[4])
		{
			vertices[0] = new Vertex(xe,ys,zs);
			vertices[1] = new Vertex(xe,ye,zs);
			vertices[2] = new Vertex(xe,ye,ze);
			vertices[3] = new Vertex(xe,ys,ze);
			obj.addFace(vertices, null, Side.RIGHT, mtlSides[4]);
		}
		if(drawSides[5])
		{
			vertices[0] = new Vertex(xs,ys,ze);
			vertices[1] = new Vertex(xs,ys,zs);
			vertices[2] = new Vertex(xe,ys,zs);
			vertices[3] = new Vertex(xe,ys,ze);
			obj.addFace(vertices, null, Side.BOTTOM, mtlSides[5]);
		}
	}
	
	
	/**
	 * Adds the block to the given OBJFile.
	 * 
	 * @param obj OBJFile to add the model to.
	 * @param chunks World chunk data
	 * @param x Block x coordinate
	 * @param y Block y coordinate
	 * @param z Block z coordinate
	 * @param data Block data value
	 */
	public abstract void addModel(OBJFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data);
	
}
