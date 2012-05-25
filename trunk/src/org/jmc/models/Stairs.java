package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;


/**
 * Model for stairs.
 */
public class Stairs extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		String[] mtls = getMtlSides(data);
		boolean[] drawSides = drawSides(chunks, x, y, z);
		UV[] uvSide, uvFront, uvTop;

		int dir = data & 3;
		int up = data & 4;

		switch (dir)
		{
		case 0:
			if (up == 0)
			{
				/* base */
				uvTop = new UV[] { new UV(0,0), new UV(0.5f,0), new UV(0.5f,1), new UV(0,1) };
				uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };

				// top
				obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z+0.5f), new Vertex(x,y,z+0.5f), new Vertex(x,y,z-0.5f), new Vertex(x-0.5f,y,z-0.5f)}, uvTop, null, mtls[0]);
				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x+0.5f,y,z-0.5f)}, uvSide, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z+0.5f)}, uvSide, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z-0.5f)}, uvSide, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y-0.5f,z-0.5f), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x+0.5f,y,z+0.5f)}, uvSide, null, mtls[4]);
				// bottom
				if (drawSides[5])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x+0.5f,y-0.5f,z-0.5f)}, uvTop, null, mtls[5]);

				/* step */
				uvTop = new UV[] { new UV(0.5f,0), new UV(1,0), new UV(1,1), new UV(0.5f,1) };
				uvFront = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSide = new UV[] { new UV(0.5f,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0.5f,1) };

				// top
				if (drawSides[0])
					obj.addFace(new Vertex[] {new Vertex(x,y+0.5f,z+0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f), new Vertex(x,y+0.5f,z-0.5f)}, uvTop, null, mtls[0]);
				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z-0.5f), new Vertex(x,y,z-0.5f), new Vertex(x,y+0.5f,z-0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f)}, uvSide, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x,y,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f), new Vertex(x,y+0.5f,z+0.5f)}, uvSide, null, mtls[2]);
				// left
				obj.addFace(new Vertex[] {new Vertex(x,y,z-0.5f), new Vertex(x,y,z+0.5f), new Vertex(x,y+0.5f,z+0.5f), new Vertex(x,y+0.5f,z-0.5f)}, uvFront, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f)}, uvFront, null, mtls[4]);
			}
			else
			{
				/* base */
				uvTop = new UV[] { new UV(0,0), new UV(0.5f,0), new UV(0.5f,1), new UV(0,1) };
				uvSide = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };

				// top
				if (drawSides[0])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y+0.5f,z+0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f)}, null, null, mtls[0]);
				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z-0.5f), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f)}, uvSide, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f), new Vertex(x-0.5f,y+0.5f,z+0.5f)}, uvSide, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z-0.5f), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y+0.5f,z+0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f)}, uvSide, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f)}, uvSide, null, mtls[4]);
				// bottom
				obj.addFace(new Vertex[] {new Vertex(x,y,z+0.5f), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x,y,z-0.5f)}, uvTop, null, mtls[5]);

				/* step */
				uvTop = new UV[] { new UV(0.5f,0), new UV(1,0), new UV(1,1), new UV(0.5f,1) };
				uvFront = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSide = new UV[] { new UV(0.5f,0), new UV(1,0), new UV(1,0.5f), new UV(0.5f,0.5f) };

				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z-0.5f), new Vertex(x,y-0.5f,z-0.5f), new Vertex(x,y,z-0.5f), new Vertex(x+0.5f,y,z-0.5f)}, uvSide, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x,y-0.5f,z+0.5f), new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x,y,z+0.5f)}, uvSide, null, mtls[2]);
				// left
				obj.addFace(new Vertex[] {new Vertex(x,y-0.5f,z-0.5f), new Vertex(x,y-0.5f,z+0.5f), new Vertex(x,y,z+0.5f), new Vertex(x,y,z-0.5f)}, uvFront, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y-0.5f,z-0.5f), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x+0.5f,y,z+0.5f)}, uvFront, null, mtls[4]);
				// bottom
				if (drawSides[5])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x,y-0.5f,z+0.5f), new Vertex(x,y-0.5f,z-0.5f), new Vertex(x+0.5f,y-0.5f,z-0.5f)}, uvTop, null, mtls[5]);
			}
			break;
		case 1:
			if (up == 0)
			{
				/* base */
				uvTop = new UV[] { new UV(0.5f,0), new UV(1,0), new UV(1,1), new UV(0.5f,1) };
				uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };

				// top
				obj.addFace(new Vertex[] {new Vertex(x,y,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x,y,z-0.5f)}, uvTop, null, mtls[0]);
				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x+0.5f,y,z-0.5f)}, uvSide, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z+0.5f)}, uvSide, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z-0.5f)}, uvSide, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y-0.5f,z-0.5f), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x+0.5f,y,z+0.5f)}, uvSide, null, mtls[4]);
				// bottom
				if (drawSides[5])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x+0.5f,y-0.5f,z-0.5f)}, null, null, mtls[5]);

				/* step */
				uvTop = new UV[] { new UV(0,0), new UV(0.5f,0), new UV(0.5f,1), new UV(0,1) };
				uvFront = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSide = new UV[] { new UV(0,0.5f), new UV(0.5f,0.5f), new UV(0.5f,1), new UV(0,1) };

				// top
				if (drawSides[0])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y+0.5f,z+0.5f), new Vertex(x,y+0.5f,z+0.5f), new Vertex(x,y+0.5f,z-0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f)}, uvTop, null, mtls[0]);
				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x,y,z-0.5f), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f), new Vertex(x,y+0.5f,z-0.5f)}, uvSide, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z+0.5f), new Vertex(x,y,z+0.5f), new Vertex(x,y+0.5f,z+0.5f), new Vertex(x-0.5f,y+0.5f,z+0.5f)}, uvSide, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z-0.5f), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y+0.5f,z+0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f)}, uvFront, null, mtls[3]);
				// right
				obj.addFace(new Vertex[] {new Vertex(x,y,z+0.5f), new Vertex(x,y,z-0.5f), new Vertex(x,y+0.5f,z-0.5f), new Vertex(x,y+0.5f,z+0.5f)}, uvFront, null, mtls[4]);
			}
			else
			{
				/* base */
				uvTop = new UV[] { new UV(0.5f,0), new UV(1,0), new UV(1,1), new UV(0.5f,1) };
				uvSide = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				
				// top
				if (drawSides[0])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y+0.5f,z+0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f)}, null, null, mtls[0]);
				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z-0.5f), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f)}, uvSide, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f), new Vertex(x-0.5f,y+0.5f,z+0.5f)}, uvSide, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z-0.5f), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y+0.5f,z+0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f)}, uvSide, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f)}, uvSide, null, mtls[4]);
				// bottom
				obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z+0.5f), new Vertex(x,y,z+0.5f), new Vertex(x,y,z-0.5f), new Vertex(x+0.5f,y,z-0.5f)}, uvTop, null, mtls[5]);

				/* step */
				uvTop = new UV[] { new UV(0,0), new UV(0.5f,0), new UV(0.5f,1), new UV(0,1) };
				uvFront = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSide = new UV[] { new UV(0,0), new UV(0.5f,0), new UV(0.5f,0.5f), new UV(0,0.5f) };

				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x,y-0.5f,z-0.5f), new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x,y,z-0.5f)}, uvSide, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x,y-0.5f,z+0.5f), new Vertex(x,y,z+0.5f), new Vertex(x-0.5f,y,z+0.5f)}, uvSide, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z-0.5f)}, uvFront, null, mtls[3]);
				// right
				obj.addFace(new Vertex[] {new Vertex(x,y-0.5f,z+0.5f), new Vertex(x,y-0.5f,z-0.5f), new Vertex(x,y,z-0.5f), new Vertex(x,y,z+0.5f)}, uvFront, null, mtls[4]);
				// bottom
				if (drawSides[5])
					obj.addFace(new Vertex[] {new Vertex(x,y-0.5f,z+0.5f), new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x,y-0.5f,z-0.5f)}, uvTop, null, mtls[5]);
			}
			break;
		case 2:
			if (up == 0)
			{
				/* base */
				uvTop = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };

				// top
				obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z), new Vertex(x+0.5f,y,z), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x-0.5f,y,z-0.5f)}, uvTop, null, mtls[0]);
				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x+0.5f,y,z-0.5f)}, uvSide, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z+0.5f)}, uvSide, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z-0.5f)}, uvSide, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y-0.5f,z-0.5f), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x+0.5f,y,z+0.5f)}, uvSide, null, mtls[4]);
				// bottom
				if (drawSides[5])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x+0.5f,y-0.5f,z-0.5f)}, null, null, mtls[5]);

				/* step */
				uvTop = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvFront = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSide = new UV[] { new UV(0.5f,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0.5f,1) };

				// top
				if (drawSides[0])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y+0.5f,z+0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f), new Vertex(x+0.5f,y+0.5f,z), new Vertex(x-0.5f,y+0.5f,z)}, uvTop, null, mtls[0]);
				// front
				obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z), new Vertex(x-0.5f,y,z), new Vertex(x-0.5f,y+0.5f,z), new Vertex(x+0.5f,y+0.5f,z)}, uvFront, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f), new Vertex(x-0.5f,y+0.5f,z+0.5f)}, uvFront, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y+0.5f,z+0.5f), new Vertex(x-0.5f,y+0.5f,z)}, uvSide, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z), new Vertex(x+0.5f,y+0.5f,z), new Vertex(x+0.5f,y+0.5f,z+0.5f)}, uvSide, null, mtls[4]);
			}
			else
			{
				/* base */
				uvTop = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSide = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };

				// top
				if (drawSides[0])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y+0.5f,z+0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f)}, null, null, mtls[0]);
				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z-0.5f), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f)}, uvSide, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f), new Vertex(x-0.5f,y+0.5f,z+0.5f)}, uvSide, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z-0.5f), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y+0.5f,z+0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f)}, uvSide, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f)}, uvSide, null, mtls[4]);
				// bottom
				obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z), new Vertex(x-0.5f,y,z), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x+0.5f,y,z-0.5f)}, uvTop, null, mtls[5]);

				/* step */
				uvTop = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvFront = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSide = new UV[] { new UV(0.5f,0), new UV(1,0), new UV(1,0.5f), new UV(0.5f,0.5f) };

				// front
				obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z), new Vertex(x-0.5f,y-0.5f,z), new Vertex(x-0.5f,y,z), new Vertex(x+0.5f,y,z)}, uvFront, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z+0.5f)}, uvFront, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z), new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z)}, uvSide, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y-0.5f,z), new Vertex(x+0.5f,y,z), new Vertex(x+0.5f,y,z+0.5f)}, uvSide, null, mtls[4]);
				// bottom
				if (drawSides[5])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y-0.5f,z), new Vertex(x+0.5f,y-0.5f,z)}, uvTop, null, mtls[5]);
			}
			break;
		case 3:
			if (up == 0)
			{
				/* base */
				uvTop = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };

				// top
				obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z), new Vertex(x-0.5f,y,z)}, uvTop, null, mtls[0]);
				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x+0.5f,y,z-0.5f)}, uvSide, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z+0.5f)}, uvSide, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z-0.5f)}, uvSide, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x+0.5f,y-0.5f,z-0.5f), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x+0.5f,y,z+0.5f)}, uvSide, null, mtls[4]);
				// bottom
				if (drawSides[5])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y-0.5f,z+0.5f), new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x+0.5f,y-0.5f,z-0.5f)}, null, null, mtls[5]);

				/* step */
				uvTop = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvFront = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSide = new UV[] { new UV(0,0.5f), new UV(0.5f,0.5f), new UV(0.5f,1), new UV(0,1) };

				// top
				if (drawSides[0])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y+0.5f,z), new Vertex(x+0.5f,y+0.5f,z), new Vertex(x+0.5f,y+0.5f,z-0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f)}, uvTop, null, mtls[0]);
				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z-0.5f), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f)}, uvFront, null, mtls[1]);
				// back
				obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z), new Vertex(x+0.5f,y,z), new Vertex(x+0.5f,y+0.5f,z), new Vertex(x-0.5f,y+0.5f,z)}, uvFront, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z-0.5f), new Vertex(x-0.5f,y,z), new Vertex(x-0.5f,y+0.5f,z), new Vertex(x-0.5f,y+0.5f,z-0.5f)}, uvSide, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f), new Vertex(x+0.5f,y+0.5f,z)}, uvSide, null, mtls[4]);
			}
			else
			{
				/* base */
				uvTop = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSide = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };

				// top
				if (drawSides[0])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y+0.5f,z+0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f)}, null, null, mtls[0]);
				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z-0.5f), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f)}, uvSide, null, mtls[1]);
				// back
				if (drawSides[2])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f), new Vertex(x-0.5f,y+0.5f,z+0.5f)}, uvSide, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y,z-0.5f), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y+0.5f,z+0.5f), new Vertex(x-0.5f,y+0.5f,z-0.5f)}, uvSide, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z+0.5f), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x+0.5f,y+0.5f,z-0.5f), new Vertex(x+0.5f,y+0.5f,z+0.5f)}, uvSide, null, mtls[4]);
				// bottom
				obj.addFace(new Vertex[] {new Vertex(x+0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z+0.5f), new Vertex(x-0.5f,y,z), new Vertex(x+0.5f,y,z)}, uvTop, null, mtls[5]);
				
				/* step */
				uvTop = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvFront = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSide = new UV[] { new UV(0,0), new UV(0.5f,0), new UV(0.5f,0.5f), new UV(0,0.5f) };

				// front
				if (drawSides[1])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y,z-0.5f), new Vertex(x+0.5f,y,z-0.5f)}, uvFront, null, mtls[1]);
				// back
				obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z), new Vertex(x+0.5f,y-0.5f,z), new Vertex(x+0.5f,y,z), new Vertex(x-0.5f,y,z)}, uvFront, null, mtls[2]);
				// left
				if (drawSides[3])
					obj.addFace(new Vertex[] {new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x-0.5f,y-0.5f,z), new Vertex(x-0.5f,y,z), new Vertex(x-0.5f,y,z-0.5f)}, uvSide, null, mtls[3]);
				// right
				if (drawSides[4])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z), new Vertex(x+0.5f,y-0.5f,z-0.5f), new Vertex(x+0.5f,y,z-0.5f), new Vertex(x+0.5f,y,z)}, uvSide, null, mtls[4]);
				// bottom
				if (drawSides[5])
					obj.addFace(new Vertex[] {new Vertex(x+0.5f,y-0.5f,z), new Vertex(x-0.5f,y-0.5f,z), new Vertex(x-0.5f,y-0.5f,z-0.5f), new Vertex(x+0.5f,y-0.5f,z-0.5f)}, uvTop, null, mtls[5]);
			}
			break;		
		}
	}

}
