package org.jmc.models;

import java.util.HashMap;

import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Generic model for cube blocks.
 */
public class ChorusPlant extends BlockModel
{
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		boolean[] drawSides;
		
		boolean connUp = data.get("up").equals("true");
		boolean connNorth = data.get("north").equals("true");
		boolean connSouth = data.get("south").equals("true");
		boolean connWest = data.get("west").equals("true");
		boolean connEast = data.get("east").equals("true");
		boolean connDown = data.get("down").equals("true");
				
		float coreTop; // How far up the core needs to raise.
		float coreUvTop; // Corresponding UV height to maintain texture.
		float coreBottom; // How far down the core needs to lower.
		float coreUvBottom; // Corresponding UV height to maintain texture.
		
		UV[] uvTop;
		UV[] uvSide;
		UV[][] uvSides;
		
		// The top of the core is always an 8x8, as we'll be adding the sides on later if required.
		uvTop = new UV[] { new UV(0.25f,0.25f), new UV(0.75f,0.25f), new UV(0.75f,0.75f), new UV(0.25f,0.75f) };
		
		if (connUp) { // If it connects upwards, it will need the UVs to extend to the top
			coreTop = 8/16f;
			coreUvTop = 1;
		} else { // otherwise it will need to cut the top 4 pixels off
			coreTop = 4/16f;
			coreUvTop = 12/16f;
		}
		if (connDown) { // If it connects downwards, it will need UVs to extend to the bottom
			coreBottom = 8/16f;
			coreUvBottom = 0;
		} else { // otherwise it will need to cut the bottom 4 pixels off
			coreBottom = 4/16f;
			coreUvBottom = 4/16f;
		}
		
		// The UVs for the core tube, factoring in whether it extends up or downwards.
		uvSide = new UV[] { new UV(0.25f,coreUvBottom), new UV(0.75f,coreUvBottom), new UV(0.75f,coreUvTop), new UV(0.25f,coreUvTop) };
		uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };		
		
		addBox(obj,
				x - 0.25f, y - coreBottom, z - 0.25f,
				x + 0.25f, y + coreTop, z + 0.25f, 
				null, 
				getMtlSides(data,biome), 
				uvSides, 
				drawSides(chunks, x, y, z));
		
	    //top, north, south, west, east, bottom
	    
		
		// ========== North-facing Connector ==========
		if (connNorth) {
			uvSides = new UV[][] { 
				new UV[] { new UV(0.25f,1), new UV(0.75f,1), new UV(0.75f,0.75f), new UV(0.25f,0.75f) }, // top face
				uvSide, // North face doesn't matter, it's connected
				uvSide, // South face doesn't matter, it's connected
				new UV[] { new UV(0.25f,0.25f), new UV(0,0.25f), new UV(0,0.75f), new UV(0.25f,0.75f) }, //west face 
				new UV[] { new UV(1,0.25f), new UV(0.75f,0.25f), new UV(0.75f,0.75f), new UV(1,0.75f) }, //east face
				new UV[] { new UV(0.75f,0), new UV(0.25f,0), new UV(0.25f,0.25f), new UV(0.75f,0.25f) }}; //bottom face
			
			// Drawing the 8x8 extension to the north, it won't need the north/south faces
		    //                         top  south north
			drawSides = new boolean[] {true,false,false,true,true,true};
			
			addBox(obj,
					x - 0.25f, y - 0.25f, z - 0.25f,
					x + 0.25f, y + 0.25f, z - 0.5f, 
					null, 
					getMtlSides(data,biome), 
					uvSides, 
					drawSides);
		} else //no north-facing connection
		{
			
		}
		
		// ========== South-facing Connector ==========
		if (connSouth) {
			uvSides = new UV[][] { 
				new UV[] { new UV(0.25f,0), new UV(0.75f,0), new UV(0.75f,0.25f), new UV(0.25f,0.25f) },  // top face
				uvSide, // north face doesn't matter, it won't even be drawn in a moment
				uvSide, // south face doesn't matter, it won't even be drawn in a moment
				new UV[] { new UV(0.75f,0.25f), new UV(1,0.25f), new UV(1,0.75f), new UV(0.75f,0.75f) },  //west face 
				new UV[] { new UV(0,0.25f), new UV(0.25f,0.25f), new UV(0.25f,0.75f), new UV(0,0.75f) },  //east face
				new UV[] { new UV(0.75f,1), new UV(0.25f,1), new UV(0.25f,0.75f), new UV(0.75f,0.75f) }}; //bottom face
			
			// Drawing the 8x8 extension to the south, it won't need the north/south faces
			drawSides = new boolean[] {true,false,false,true,true,true};
			
			addBox(obj,
					x - 0.25f, y - 0.25f, z + 0.25f,
					x + 0.25f, y + 0.25f, z + 0.5f, 
					null, 
					getMtlSides(data,biome), 
					uvSides, 
					drawSides);
		} else //no south-facing connection
		{

		}
		
		// ========== West-facing Connector ==========
		if (connWest) {
			uvSides = new UV[][] { 
				new UV[] { new UV(0,0.25f), new UV(0.25f,0.25f), new UV(0.25f,0.75f), new UV(0,0.75f) }, // top face
				new UV[] { new UV(0.75f,0.25f), new UV(1,0.25f), new UV(1,0.75f), new UV(0.75f,0.75f) }, // north face
				new UV[] { new UV(0,0.25f), new UV(0.25f,0.25f), new UV(0.25f,0.75f), new UV(0,0.75f) }, // south face
				uvSide, // west face doesn't matter, it's connected
				uvSide, // east face doesn't matter, it's connected
				new UV[] { new UV(0.25f,0.75f), new UV(0,0.75f), new UV(0,0.25f), new UV(0.25f,0.25f) }}; // bottom face
			
			// Drawing the 8x8 extension to the west, it won't need the west/east faces
			drawSides = new boolean[] {true,true,true,false,false,true};
			
			addBox(obj,
					x - 0.50f, y - 0.25f, z - 0.25f,
					x - 0.25f, y + 0.25f, z + 0.25f, 
					null, 
					getMtlSides(data,biome), 
					uvSides, 
					drawSides);
		} else //no west-facing connection
		{
			
		}		
		
		// ========== East-facing Connector ==========
		if (connEast) {
			uvSides = new UV[][] { 
				new UV[] { new UV(1,0.25f), new UV(0.75f,0.25f), new UV(0.75f,0.75f), new UV(1,0.75f) }, // top face
				new UV[] { new UV(0.25f,0.25f), new UV(0,0.25f), new UV(0,0.75f), new UV(0.25f,0.75f) }, // north face
				new UV[] { new UV(1,0.25f), new UV(0.75f,0.25f), new UV(0.75f,0.75f), new UV(1,0.75f) }, // south face
				uvSide, // west face doesn't matter, it's connected
				uvSide, // east face doesn't matter, it's connected
				new UV[] { new UV(0.75f,0.75f), new UV(1,0.75f), new UV(1,0.25f), new UV(0.75f,0.25f) }}; // bottom face
			
			// Drawing the 8x8 extension to the east, it won't need the west/east faces
			drawSides = new boolean[] {true,true,true,false,false,true};
			
			addBox(obj,
					x + 0.50f, y - 0.25f, z - 0.25f,
					x + 0.25f, y + 0.25f, z + 0.25f, 
					null, 
					getMtlSides(data,biome), 
					uvSides, 
					drawSides);
		} else //no east-facing connection
		{
			
		}	
	}
}
