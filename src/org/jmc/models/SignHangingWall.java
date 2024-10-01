package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for sign posts.
 */
public class SignHangingWall extends SignHanging {
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome) {
		NamespaceID texture = getMtlSides(data,biome)[0];
		
		Transform rotate = new Transform();
		switch (data.state.get("facing")) {
			case "north":
				rotate = Transform.rotation(0, 180, 0);
				break;
			case "west":
				rotate = Transform.rotation(0, 90, 0);
				break;
			case "south":
				rotate = Transform.rotation(0, 0, 0);
				break;
			case "east":
				rotate = Transform.rotation(0, -90, 0);
				break;
		}
		Transform translate = Transform.translation(x, y, z);
		Transform rt = translate.multiply(rotate);
		
		hangingObj.addObjectToOutput(getObjPart("sign", texture), rt, obj, false);
		hangingObj.addObjectToOutput(getObjPart("chains", texture), rt, obj, false);
		hangingObj.addObjectToOutput(getObjPart("top_bar", texture), rt, obj, false);
	}
}
