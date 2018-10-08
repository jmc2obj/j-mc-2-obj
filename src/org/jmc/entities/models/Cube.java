package org.jmc.entities.models;

import org.jmc.BlockMaterial;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;

public class Cube extends EntityModel
{
	protected BlockMaterial materials = null;

	/**
	 * Set the materials for this block.
	 */
	public void setMaterials(BlockMaterial val)
	{
		this.materials = val;
	}


	/**
	 * Expand the materials to the full 6 side definition used by addBox
	 */
	protected String[] getMtlSides()
	{
		String[] abbrMtls = materials.get(null, -1);

		String[] mtlSides = new String[6];
		if (abbrMtls.length < 2)
		{
			mtlSides[0] = abbrMtls[0];
			mtlSides[1] = abbrMtls[0];
			mtlSides[2] = abbrMtls[0];
			mtlSides[3] = abbrMtls[0];
			mtlSides[4] = abbrMtls[0];
			mtlSides[5] = abbrMtls[0];
		}
		else if (abbrMtls.length < 3)
		{
			mtlSides[0] = abbrMtls[0];
			mtlSides[1] = abbrMtls[1];
			mtlSides[2] = abbrMtls[1];
			mtlSides[3] = abbrMtls[1];
			mtlSides[4] = abbrMtls[1];
			mtlSides[5] = abbrMtls[0];
		}
		else if (abbrMtls.length < 6)
		{
			mtlSides[0] = abbrMtls[0];
			mtlSides[1] = abbrMtls[1];
			mtlSides[2] = abbrMtls[1];
			mtlSides[3] = abbrMtls[1];
			mtlSides[4] = abbrMtls[1];
			mtlSides[5] = abbrMtls[2];
		}
		else
		{
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
	 * Helper method to add a box to given OBJFile.
	 * 
	 * @param obj OBJFile to add to
	 * @param xs Start x coordinate
	 * @param ys Start y coordinate
	 * @param zs Start z coordinate
	 * @param xe End x coordinate
	 * @param ye End y coordinate
	 * @param ze End z coordinate
	 * @param trans Transform to apply to the vertex coordinates. If null, no transform is applied 
	 * @param mtlSides Material for each side, in order TOP, FRONT, BACK, LEFT, RIGHT, BOTTOM
	 * @param uvSides Texture coordinates for each side, in order TOP, FRONT, BACK, LEFT, RIGHT, BOTTOM. If null, uses default
	 * coordinates for all sides. If an individual side is null, uses default coordinates for that side.
	 * @param drawSides Whether to draw each side, in order TOP, FRONT, BACK, LEFT, RIGHT, BOTTOM. If null, draws all sides.
	 */
	protected void addBox(ChunkProcessor obj, float xs, float ys, float zs, float xe, float ye, float ze, Transform trans, String[] mtlSides, UV[][] uvSides)
	{
		Vertex[] vertices = new Vertex[4];

		{	// top
			vertices[0] = new Vertex(xs,ye,ze);
			vertices[1] = new Vertex(xe,ye,ze);
			vertices[2] = new Vertex(xe,ye,zs);
			vertices[3] = new Vertex(xs,ye,zs);
			obj.addFace(vertices, uvSides == null ? null : uvSides[0], trans, mtlSides[0]);
		}
		{	// front
			vertices[0] = new Vertex(xe,ys,zs);
			vertices[1] = new Vertex(xs,ys,zs);
			vertices[2] = new Vertex(xs,ye,zs);
			vertices[3] = new Vertex(xe,ye,zs);
			obj.addFace(vertices, uvSides == null ? null : uvSides[1], trans, mtlSides[1]);
		}
		{	// back
			vertices[0] = new Vertex(xs,ys,ze);
			vertices[1] = new Vertex(xe,ys,ze);
			vertices[2] = new Vertex(xe,ye,ze);
			vertices[3] = new Vertex(xs,ye,ze);
			obj.addFace(vertices, uvSides == null ? null : uvSides[2], trans, mtlSides[2]);
		}
		{	// left
			vertices[0] = new Vertex(xs,ys,zs);
			vertices[1] = new Vertex(xs,ys,ze);
			vertices[2] = new Vertex(xs,ye,ze);
			vertices[3] = new Vertex(xs,ye,zs);
			obj.addFace(vertices, uvSides == null ? null : uvSides[3], trans, mtlSides[3]);
		}
		{	// right
			vertices[0] = new Vertex(xe,ys,ze);
			vertices[1] = new Vertex(xe,ys,zs);
			vertices[2] = new Vertex(xe,ye,zs);
			vertices[3] = new Vertex(xe,ye,ze);
			obj.addFace(vertices, uvSides == null ? null : uvSides[4], trans, mtlSides[4]);
		}
		{	// bottom
			vertices[0] = new Vertex(xe,ys,ze);
			vertices[1] = new Vertex(xs,ys,ze);
			vertices[2] = new Vertex(xs,ys,zs);
			vertices[3] = new Vertex(xe,ys,zs);
			obj.addFace(vertices, uvSides == null ? null : uvSides[5], trans, mtlSides[5]);
		}
	}


	@Override
	public void addEntity(ChunkProcessor obj, Transform transform) {
		
		addBox(obj,
				 - 0.5f,  - 0.5f,  - 0.5f,
				 + 0.5f,  + 0.5f,  + 0.5f, 
				transform, 
				getMtlSides(), 
				null);
		
	}

}
