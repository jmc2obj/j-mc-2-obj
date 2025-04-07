package org.jmc.entities;

import org.jmc.BlockMaterial;
import org.jmc.NBT.*;
import org.jmc.geom.BlockPos;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.util.Log;


/**
 * Model for paintings.
 * TODO correctly render paintings larger than 1x1
 */
public class Painting extends Entity
{
	public Painting(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addEntity(ChunkProcessor obj, TAG_Compound entity)
	{
		/*
		Example of a painting entity as of MC 1.8:
		
		TAG_Compound(""): count=18
			TAG_Byte("Facing"): dec=1
			TAG_Long("UUIDLeast"): val=-6055910250618088878
			TAG_List("Motion"): count=3 type=6
				TAG_Double(""): val=0.0
				TAG_Double(""): val=0.0
				TAG_Double(""): val=0.0
			ENDOF TAG_List("Motion")
			TAG_Byte("OnGround"): dec=0
			TAG_Long("UUIDMost"): val=-8729761873231721078
			TAG_Int("TileY"): val=102
			TAG_Int("Dimension"): val=0
			TAG_Int("TileX"): val=-1265
			TAG_Short("Air"): val=300
			TAG_Int("TileZ"): val=-3932
			TAG_String("Motive"): val=Match
			TAG_String("id"): val=Painting
			TAG_List("Pos"): count=3 type=6
				TAG_Double(""): val=-1264.03125
				TAG_Double(""): val=103.0
				TAG_Double(""): val=-3931.0
			ENDOF TAG_List("Pos")
			TAG_Int("PortalCooldown"): val=0
			TAG_Short("Fire"): val=0
			TAG_Float("FallDistance"): val=0.0
			TAG_List("Rotation"): count=2 type=5
				TAG_Float(""): val=90.0
				TAG_Float(""): val=0.0
			ENDOF TAG_List("Rotation")
			TAG_Byte("Invulnerable"): dec=0
		ENDOF TAG_Compound("")
		*/

		BlockPos pos = getBlockPosition(entity);
		
		NBT_Tag facingTag = entity.getElement("facing");
		byte facing;
		if (facingTag != null) {
			facing = ((TAG_Byte) facingTag).value;
		} else {
			facing = ((TAG_Byte) entity.getElement("Facing")).value;
		}
		
		NBT_Tag variantTag = entity.getElement("variant");
		String motiv = null;
		if (variantTag != null) {
			if (variantTag instanceof TAG_String) {
				motiv = ((TAG_String) variantTag).value;
			} else if (variantTag instanceof TAG_Compound) {
				motiv = ((TAG_Compound) variantTag).getElement("asset_id").toString();
			}
		} else {
			motiv = ((TAG_String) entity.getElement("Motive")).value;
		}
		
		if (motiv == null) {
			Log.debug(String.format("Painting at %s has no 'Motive'", pos.toString()));
			return;
		}
		NamespaceID motivID = NamespaceID.fromString(motiv);
		
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		switch (facing)
		{
			case 0:
				break;
			case 1:
				rotate = Transform.rotation(0, 90, 0);
				break;
			case 2:
				rotate = Transform.rotation(0, 180, 0);
				break;
			case 3:
				rotate = Transform.rotation(0, -90, 0);
				break;
		}
		
		translate = Transform.translation(pos.x, pos.y, pos.z);
		
		rt = translate.multiply(rotate);
		
		BlockMaterial materials=new BlockMaterial();
		NamespaceID[] matname={new NamespaceID(motivID.namespace, "painting/" + motivID.path)};
		materials.put(matname);
		model.setMaterials(materials);
		
		model.addEntity(obj, rt);
	}

	private BlockPos getBlockPosition(TAG_Compound entity) {
		int x, y, z;
		TAG_Int_Array blockPosList = (TAG_Int_Array) entity.getElement("block_pos");
		if (blockPosList != null) {
			x = blockPosList.data[0];
			y = blockPosList.data[1];
			z = blockPosList.data[2];
		} else {
			x = ((TAG_Int) entity.getElement("TileX")).value;
			y = ((TAG_Int) entity.getElement("TileY")).value;
			z = ((TAG_Int) entity.getElement("TileZ")).value;
		}
		return new BlockPos(x, y, z);
	}

	@Override
	public Vertex getPosition(TAG_Compound entity) {
		BlockPos pos = getBlockPosition(entity);
		return new Vertex(pos.x, pos.y, pos.z);
	}

}
