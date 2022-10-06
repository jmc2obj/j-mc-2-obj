package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ObjChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;

public class RegistryBell extends Registry{

	private final Mesh meshModel = new Mesh();

	{
		meshModel.loadObjFile("models/bell.obj");
	}

	@Override
	public void addModel(ObjChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome) {
		super.addModel(obj, chunks, x, y, z, data, biome);
		meshModel.addModel(obj, chunks, x, y, z, data, biome);
	}
}
