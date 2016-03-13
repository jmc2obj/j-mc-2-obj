package org.jmc.models;

import java.util.HashMap;
import java.util.HashSet;

import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for pipe-like blocks, such as BC Pipes or IC2 Wires.
 * Can be configured for different thicknesses with "<thickness data='3'>6</thickness>", an even number up to 14
 * Will connect to other pipes of the same ID and IDs listed with "<connect>1,2,3,4</connect>"
 */
public class Pipe extends BlockModel
{
	/** Blocks to which pipes will connect */
	private static HashMap<Short, HashSet<Short>> connectable;

	private void setupConnectable() {
		if (connectable == null || connectable.isEmpty()) {
			connectable = new HashMap<Short, HashSet<Short>>();
		}
		if (connectable.get(this.blockId) == null) {
			connectable.put(this.blockId, new HashSet<Short>());
		}
		connectable.get(this.blockId).add(this.blockId);
//		String connections = getConfigNodeValue("connect", 0);
	}	
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, byte data, byte biome)
	{
		setupConnectable();
		HashSet<Short> blockConnectable = connectable.get(this.blockId);

		int thickness = 2;
		String thicknessstr = getConfigNodeValue("thickness", data);
		if (thicknessstr != null && !thicknessstr.isEmpty())
			thickness = Integer.parseInt(thicknessstr);
		if ((thickness % 2) != 0) // odd thicknesses would be weird
			thickness += 1;
		if (thickness >= 16) // assume we're not making a cube
			thickness = 14;
		float offset = thickness / 2 / 16f;

		Vertex[] vertices = new Vertex[4];
		String[] mtls = getMtlSides(data, biome);
		boolean[] drawSides;
		UV[][] uvSides;
		UV[] uvCenter = new UV[] {
			new UV(0.5f - offset, 0.5f - offset),
			new UV(0.5f - offset, 0.5f + offset),
			new UV(0.5f + offset, 0.5f + offset),
			new UV(0.5f + offset, 0.5f - offset)
		};
		UV[] uvUp = new UV[] {
			new UV(0.5f - offset, 0.5f + offset),
			new UV(0.5f - offset, 1),
			new UV(0.5f + offset, 1),
			new UV(0.5f + offset, 0.5f + offset)
		};
		UV[] uvDown = new UV[] {
			new UV(0.5f - offset, 0),
			new UV(0.5f - offset, 0.5f - offset),
			new UV(0.5f + offset, 0.5f - offset),
			new UV(0.5f + offset, 0)
		};
		UV[] uvLeft = new UV[] {
			new UV(0, 0.5f - offset),
			new UV(0, 0.5f + offset),
			new UV(0.5f - offset, 0.5f + offset),
			new UV(0.5f - offset, 0.5f - offset)
		};
		UV[] uvRight = new UV[] {
			new UV(0.5f + offset, 0.5f - offset),
			new UV(0.5f + offset, 0.5f + offset),
			new UV(1, 0.5f + offset),
			new UV(1, 0.5f - offset)
		};

		// top connector
		if (blockConnectable.contains(chunks.getBlockID(x, y+1, z)))
		{
			if (this.blockId == chunks.getBlockID(x, y+1, z)) {
				uvSides = new UV[][] { null, uvLeft, uvLeft, uvLeft, uvLeft, null };
				drawSides = new boolean[] { false, true, true, true, true, false };
			} else {
				uvSides = new UV[][] { uvCenter, uvLeft, uvLeft, uvLeft, uvLeft, null };
				drawSides = new boolean[] { true, true, true, true, true, false };
			}
			addBox(obj,
				x - offset,
				y + offset,
				z - offset,
				x + offset,
				y + 0.5f,
				z + offset,
				null, mtls, uvSides, drawSides);
		} else {
			vertices[0] = new Vertex(x - offset, y + offset, z - offset);
			vertices[1] = new Vertex(x - offset, y + offset, z + offset);
			vertices[2] = new Vertex(x + offset, y + offset, z + offset);
			vertices[3] = new Vertex(x + offset, y + offset, z - offset);
			obj.addFace(vertices, uvCenter, null, mtls[0]);
		}

		// bottom connector
		if (blockConnectable.contains(chunks.getBlockID(x, y-1, z)))
		{
			if (this.blockId == chunks.getBlockID(x, y-1, z)) {
				uvSides = new UV[][] { null, uvRight, uvRight, uvRight, uvRight, null };
				drawSides = new boolean[] { false, true, true, true, true, false };
			} else {
				uvSides = new UV[][] { null, uvRight, uvRight, uvRight, uvRight, uvCenter };
				drawSides = new boolean[] { false, true, true, true, true, true };
			}
			addBox(obj,
				x - offset,
				y - 0.5f,
				z - offset,
				x + offset,
				y - offset,
				z + offset,
				null, mtls, uvSides, drawSides);
		} else {
			vertices[0] = new Vertex(x - offset, y - offset, z - offset);
			vertices[1] = new Vertex(x + offset, y - offset, z - offset);
			vertices[2] = new Vertex(x + offset, y - offset, z + offset);
			vertices[3] = new Vertex(x - offset, y - offset, z + offset);
			obj.addFace(vertices, uvCenter, null, mtls[0]);
		}

		// front connector
		if (blockConnectable.contains(chunks.getBlockID(x, y, z-1)))
		{
			if (this.blockId == chunks.getBlockID(x, y, z-1)) {
				uvSides = new UV[][] { uvLeft, null, null, uvUp, uvUp, uvLeft };
				drawSides = new boolean[] { true, false, false, true, true, true };
			} else {
				uvSides = new UV[][] { uvLeft, uvCenter, null, uvUp, uvUp, uvLeft };
				drawSides = new boolean[] { true, true, false, true, true, true };
			}
			addBox(obj,
				x - offset,
				y - offset,
				z - 0.5f,
				x + offset,
				y + offset,
				z - offset,
				null, mtls, uvSides, drawSides);
		} else {
			vertices[0] = new Vertex(x - offset, y - offset, z - offset);
			vertices[1] = new Vertex(x - offset, y + offset, z - offset);
			vertices[2] = new Vertex(x + offset, y + offset, z - offset);
			vertices[3] = new Vertex(x + offset, y - offset, z - offset);
			obj.addFace(vertices, uvCenter, null, mtls[0]);
		}

		// back connector
		if (blockConnectable.contains(chunks.getBlockID(x, y, z+1)))
		{
			if (this.blockId == chunks.getBlockID(x, y, z-+1)) {
				uvSides = new UV[][] { uvRight, null, null, uvDown, uvDown, uvRight };
				drawSides = new boolean[] { true, false, false, true, true, true };
			} else {
				uvSides = new UV[][] { uvRight, null, uvCenter, uvDown, uvDown, uvRight };
				drawSides = new boolean[] { true, false, true, true, true, true };
			}
			addBox(obj,
				x - offset,
				y - offset,
				z + offset,
				x + offset,
				y + offset,
				z + 0.5f,
				null, mtls, uvSides, drawSides);
		} else {
			vertices[0] = new Vertex(x - offset, y - offset, z + offset);
			vertices[1] = new Vertex(x + offset, y - offset, z + offset);
			vertices[2] = new Vertex(x + offset, y + offset, z + offset);
			vertices[3] = new Vertex(x - offset, y + offset, z + offset);
			obj.addFace(vertices, uvCenter, null, mtls[0]);
		}

		// left connector
		if (blockConnectable.contains(chunks.getBlockID(x-1, y, z)))
		{
			if (this.blockId == chunks.getBlockID(x-1, y, z)) {
				uvSides = new UV[][] { uvUp, uvUp, uvUp, null, null, uvUp };
				drawSides = new boolean[] { true, true, true, false, false, true };
			} else {
				uvSides = new UV[][] { uvUp, uvUp, uvUp, uvCenter, null, uvUp };
				drawSides = new boolean[] { true, true, true, true, false, true };
			}
			addBox(obj,
				x - 0.5f,
				y - offset,
				z - offset,
				x - offset,
				y + offset,
				z + offset,
				null, mtls, uvSides, drawSides);
		} else {
			vertices[0] = new Vertex(x - offset, y - offset, z - offset);
			vertices[1] = new Vertex(x - offset, y - offset, z + offset);
			vertices[2] = new Vertex(x - offset, y + offset, z + offset);
			vertices[3] = new Vertex(x - offset, y + offset, z - offset);
			obj.addFace(vertices, uvCenter, null, mtls[0]);
		}

		// right connector
		if (blockConnectable.contains(chunks.getBlockID(x+1, y, z)))
		{
			if (this.blockId == chunks.getBlockID(x+1, y, z)) {
				uvSides = new UV[][] { uvDown, uvDown, uvDown, null, null, uvDown };
				drawSides = new boolean[] { true, true, true, false, false, true };
			} else {
				uvSides = new UV[][] { uvDown, uvDown, uvDown, null, uvCenter, uvDown };
				drawSides = new boolean[] { true, true, true, false, true, true };
			}
			addBox(obj,
				x + offset,
				y - offset,
				z - offset,
				x + 0.5f,
				y + offset,
				z + offset,
				null, mtls, uvSides, drawSides);
		} else {
			vertices[0] = new Vertex(x + offset, y - offset, z - offset);
			vertices[1] = new Vertex(x + offset, y + offset, z - offset);
			vertices[2] = new Vertex(x + offset, y + offset, z + offset);
			vertices[3] = new Vertex(x + offset, y - offset, z + offset);
			obj.addFace(vertices, uvCenter, null, mtls[0]);
		}
	}

}
