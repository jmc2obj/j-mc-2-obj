/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.threading;

import com.google.gson.JsonObject;
import org.jmc.*;
import org.jmc.NBT.TAG_Compound;
import org.jmc.entities.Entity;
import org.jmc.geom.*;
import org.jmc.geom.FaceUtils.Face;
import org.jmc.models.None;
import org.jmc.registry.NamespaceID;
import org.jmc.util.Log;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * ChunkProcessor reads through the given chunk and outputs faces
 */
public class JsonChunkProcessor
{
	/**
	 * Returns all blocks from the given chunk buffer into the output.
	 * @param chunk
	 * @param chunk_x
	 * @param chunk_z
	 */
	public ArrayList<BlockDataPos> process(ThreadChunkDeligate chunk, int chunk_x, int chunk_z)
	{
		ArrayList<BlockDataPos> blocks = new ArrayList<>();
		int xmin,xmax,ymin,ymax,zmin,zmax;
		Rectangle xy,xz;
		xy=chunk.getXYBoundaries();
		xz=chunk.getXZBoundaries();
		xmin=xy.x;
		xmax=xmin+xy.width;
		ymin=xy.y;
		ymax=ymin+xy.height;
		zmin=xz.y;
		zmax=zmin+xz.height;

		int xs=chunk_x*16;
		int zs=chunk_z*16;
		int xe=xs+16;
		int ze=zs+16;

		if(xs<xmin) xs=xmin;
		if(xe>xmax) xe=xmax;
		if(zs<zmin) zs=zmin;
		if(ze>zmax) ze=zmax;

		for(int z = zs; z < ze; z++)
		{
			for(int x = xs; x < xe; x++)
			{
				for(int y = ymin; y < ymax; y++)
				{
					BlockData block=chunk.getBlockData(x, y, z);
					NamespaceID blockBiome=chunk.getBlockBiome(x, y, z);
					
					if(block == null || block.id == NamespaceID.NULL)
						continue;
					
					if(Options.excludeBlocks.contains(block.id))
						continue;
					
					BlockInfo blockInfo = BlockTypes.get(block);
					
					if(Options.convertOres) {
						NamespaceID oreBase = blockInfo.getOreBase();
						if (oreBase != null) {
							block.id = oreBase;
						}
					}
					if (blockInfo.getModel().getClass() != None.class) {
						blocks.add(new BlockDataPos(new BlockPos(x, y, z), block, blockBiome));
					}
				}
			}
		}
		
//
//		if (Options.renderEntities) {
//			for (TAG_Compound entity:chunk.getEntities(chunk_x, chunk_z)) {
//				if (entity == null) continue;
//				Entity handler=EntityTypes.getEntity(entity);
//				if (handler!=null) {
//					try {
//						Vertex pos = handler.getPosition(entity);
//						if (pos.x > xmin && pos.y > ymin && pos.z > zmin && pos.x < xmax && pos.y < ymax && pos.z < zmax) {
//							handler.addEntity(this, entity);
//						}
//					} catch (Exception ex) {
//						Log.error(String.format("Error rendering entity %s, skipping.", handler.id), ex);
//					}
//				}
//			}
//
//			for (TAG_Compound entity:chunk.getTileEntities(chunk_x, chunk_z)) {
//				if (entity == null) continue;
//				Entity handler=EntityTypes.getEntity(entity);
//				if(handler!=null) {
//					try {
//						Vertex pos = handler.getPosition(entity);
//						if (pos.x > xmin && pos.y > ymin && pos.z > zmin && pos.x < xmax && pos.y < ymax && pos.z < zmax) {
//							handler.addEntity(this, entity);
//						}
//					}
//					catch (Exception ex) {
//						Log.error("Error rendering tile entity, skipping.", ex);
//					}
//				}
//			}
//		}
		
		return blocks;
	}
}
