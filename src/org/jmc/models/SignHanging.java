package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.OBJInputFile;
import org.jmc.geom.Transform;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Filesystem;
import org.jmc.util.Log;
import org.w3c.dom.Node;

import javax.annotation.CheckForNull;
import java.io.IOException;


/**
 * Model for sign posts.
 */
public class SignHanging extends BlockModel {
	
	protected OBJInputFile hangingObj = null;
	
	@Override
	public void setConfigNodes(@CheckForNull Node blockNode) {
		super.setConfigNodes(blockNode);
		hangingObj = new OBJInputFile();
		String filename = "conf/models/hanging_sign.obj";
		try (Filesystem.JmcConfFile objFile = new Filesystem.JmcConfFile(filename)) {
			hangingObj.loadFile(objFile);
		} catch (IOException e) {
			Log.error(String.format("Cannot load mesh file '%s'!", filename), e);
			return;
		}
	}
	
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome) {
		NamespaceID texture = getMtlSides(data,biome)[0];
		
		float r = 0;
		int rotation = Integer.parseInt(data.state.get("rotation"));
		switch (rotation) {
			case 0: r = 0f; break;
			case 1: r = 22.5f; break;
			case 2: r = 45f; break;
			case 3: r = 67.5f; break;
			case 4: r = 90f; break;
			case 5: r = 112.5f; break;
			case 6: r = 135f; break;
			case 7: r = 157.5f; break;
			case 8: r = -180f; break;
			case 9: r = -157.5f; break;
			case 10: r = -135f; break;
			case 11: r = -112.5f; break;
			case 12: r = -90f; break;
			case 13: r = -67.5f; break;
			case 14: r = -45f; break;
			case 15: r = -22.5f; break;
		}
		Transform rotate = Transform.rotation(0, r, 0);
		Transform translate = Transform.translation(x, y, z);
		Transform rt = translate.multiply(rotate);
		
		boolean attached = Boolean.parseBoolean(data.state.get("attached"));
		
		hangingObj.addObjectToOutput(getObjPart("sign", texture), rt, obj, false);
		hangingObj.addObjectToOutput(getObjPart(attached ? "chains_attached" : "chains", texture), rt, obj, false);
	}
	
	protected OBJInputFile.OBJGroup getObjPart(String name, @CheckForNull NamespaceID texOverride) {
		OBJInputFile.OBJGroup grp = hangingObj.getObject(name);
		if (texOverride != null) {
			grp = hangingObj.overwriteMaterial(grp, texOverride);
		}
		return grp;
	}
}
