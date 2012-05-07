package org.jmc.entities;

import org.jmc.BlockMaterial;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;

public interface EntityModel {
	
	public void addEntity(OBJOutputFile obj, Transform transform);
	public void setMaterials(BlockMaterial val);
}
