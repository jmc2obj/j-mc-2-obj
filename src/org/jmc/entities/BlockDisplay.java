package org.jmc.entities;

import org.jmc.BlockData;
import org.jmc.Blockstate;
import org.jmc.Chunk;
import org.jmc.ChunkDataBuffer;
import org.jmc.NBT.*;
import org.jmc.geom.FaceUtils;
import org.jmc.geom.Quaternion;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.models.BlockModel;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Log;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;

public class BlockDisplay extends Entity {
	public BlockDisplay(@Nonnull String id) {
		super(id);
	}

	@Override
	public void addEntity(ChunkProcessor obj, TAG_Compound entity) {
		TAG_Compound state = (TAG_Compound) entity.getElement("block_state");
		if (state == null) {
			Log.debug("BlockDisplay has no blockState!");
			return;
		}
		TAG_String name = (TAG_String) state.getElement("Name");
		if (name == null) {
			Log.debug("BlockDisplay block_state has no Name!");
			return;
		}
		BlockData blockData = new BlockData(NamespaceID.fromString(name.value));

		TAG_Compound propertiesTag = (TAG_Compound) state.getElement("Properties");
		if (propertiesTag != null) {
			for (NBT_Tag tag : propertiesTag.elements) {
				TAG_String propTag = (TAG_String) tag;
				blockData.state.put(propTag.getName(), propTag.value);
			}
		}

		BlockModel model = blockData.getInfo().getModel();
		ChunkDataBuffer fakeChunkData = new ChunkDataBuffer(-5, 5, -5, 5, -5, 5) {
			@CheckForNull
			@Override
			public Chunk getChunk(Point p) {
				return null;
			}

			@Override
			public Chunk.Blocks getBlocks(Point p) {
				return null;
			}
		};

		Transform compTrans = computeDisplayTransform(entity);

		ThreadChunkDeligate fakeChunkDeligate = new ThreadChunkDeligate(fakeChunkData);
		ChunkProcessor blockObj = new ChunkProcessor();
		model.addModel(blockObj, fakeChunkDeligate, 0, 0, 0, blockData, NamespaceID.NULL);
		ArrayList<FaceUtils.Face> faces = blockObj.getAllFaces();
		for (FaceUtils.Face face : faces) {
			obj.addFace(compTrans.multiply(face), false);
		}
	}

	private Transform computeDisplayTransform(TAG_Compound entity) {
		Vertex entityPos = this.getPosition(entity);
		Transform compTrans = new Transform();
		compTrans = Transform.translation(0.5, 0.5, 0.5);
		TAG_Compound transformation = (TAG_Compound) entity.getElement("transformation");
		if (transformation != null) {
			TAG_List rightRotationTag = (TAG_List) transformation.getElement("right_rotation");
			if (rightRotationTag != null) {
				Transform rightRot = new Quaternion(
					((TAG_Float) rightRotationTag.elements[0]).value,
					((TAG_Float) rightRotationTag.elements[1]).value,
					((TAG_Float) rightRotationTag.elements[2]).value,
					((TAG_Float) rightRotationTag.elements[3]).value
				).toTransform();
				compTrans = rightRot.multiply(compTrans);
			}

			TAG_List scaleTag = (TAG_List) transformation.getElement("scale");
			if (scaleTag != null) {
				Transform scale = Transform.scale(
					((TAG_Float) scaleTag.elements[0]).value,
					((TAG_Float) scaleTag.elements[1]).value,
					((TAG_Float) scaleTag.elements[2]).value
				);
				compTrans = scale.multiply(compTrans);
			}

			TAG_List leftRotationTag = (TAG_List) transformation.getElement("left_rotation");
			if (leftRotationTag != null) {
				Transform leftRot = new Quaternion(
					((TAG_Float) leftRotationTag.elements[0]).value,
					((TAG_Float) leftRotationTag.elements[1]).value,
					((TAG_Float) leftRotationTag.elements[2]).value,
					((TAG_Float) leftRotationTag.elements[3]).value
				).toTransform();
				compTrans = leftRot.multiply(compTrans);
			}

			TAG_List translationTag = (TAG_List) transformation.getElement("translation");
			if (translationTag != null) {
				Transform trans = Transform.translation(
					((TAG_Float) translationTag.elements[0]).value,
					((TAG_Float) translationTag.elements[1]).value,
					((TAG_Float) translationTag.elements[2]).value
				);
				compTrans = trans.multiply(compTrans);
			}
		}
		compTrans = Transform.translation(entityPos.x, entityPos.y, entityPos.z).multiply(compTrans);
		return compTrans;
	}
}
