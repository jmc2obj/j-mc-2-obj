package org.jmc.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jmc.BlockData;
import org.jmc.geom.BlockPos;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.registry.BlockstateEntry;
import org.jmc.registry.BlockstateEntry.ModelInfo;
import org.jmc.registry.BlockstateEntry.ModelListWeighted;
import org.jmc.registry.ModelEntry;
import org.jmc.registry.ModelEntry.RegistryModel;
import org.jmc.registry.ModelEntry.RegistryModel.ModelElement;
import org.jmc.registry.ModelEntry.RegistryModel.ModelElement.ElementFace;
import org.jmc.registry.ModelEntry.RegistryModel.ModelElement.ElementRotation;
import org.jmc.registry.NamespaceID;
import org.jmc.registry.Registries;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Log;

@ParametersAreNonnullByDefault
public class Registry extends BlockModel {

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome) {
		BlockPos pos = new BlockPos(x, y, z);
		
		BlockstateEntry bsEntry = Registries.getBlockstate(data.id);
		if (bsEntry == null) {
			Log.debugOnce(String.format("Couldn't get blockstate to export %s", data.id.toString()));
			return;
		}
		List<ModelListWeighted> modelParts = bsEntry.getModelsFor(data.state);
		
		List<AddedElem> addedElems = new ArrayList<>();
		
		for (ModelListWeighted modelList : modelParts) {
			ModelInfo modelInfo = modelList.getRandomModel(pos);
			ModelEntry modelEntry = Registries.getModel(modelInfo.id);
			if (modelEntry == null) {
				Log.debugOnce(String.format("Couldn't get model %s to export %s", modelInfo.id, bsEntry.id));
				continue;
			}
			RegistryModel model = modelEntry.generateModel();
			if (model.elements != null) {
				for (ModelElement element : model.elements) {
					if (element != null)
						addElement(obj, chunks, pos, data, modelInfo, model, element, addedElems);
				}
			}
		}
		//Log.debug(String.format("Models for %s: %s", data.toString(), String.valueOf(models)));
	}
	
	// Add the element 
	private void addElement(ChunkProcessor obj, ThreadChunkDeligate chunks, BlockPos pos, BlockData data, ModelInfo modelInfo, RegistryModel model, ModelElement element, List<AddedElem> prevElems) {
		Transform baseTrans = pos.getTransform();
		Transform stateTrans = getStateTrans(modelInfo);
		NamespaceID[] textures = getFaceTextureArray(element.faces, model.textures);
		UV[][] uvs = getFaceUvs(element, modelInfo);
		boolean[] drawSides = getSidesCulling(chunks, pos, data, element.faces, stateTrans);
		Transform elementTrans = getElemTrans(element);
		
		// if element in the same position was added before then don't add another on top (grass_block overlay)
		AddedElem elem = new AddedElem(stateTrans, elementTrans, element, drawSides);
		boolean add = true;
		for (@Nonnull AddedElem prevElem : prevElems) {
			if (elem.matches(prevElem)) {
				if (elem.hasNew(prevElem)) {// same elem but new faces that weren't added before
					drawSides = elem.getNew(prevElem);
					prevElem.addSides(elem);
					add = false;
				} else {
					//Log.debug(String.format("Skipping double element on %s", data.id));
					return;
				}
			}
		}
		if (add) prevElems.add(elem);
		addBox(obj, element.from.x/16, element.from.y/16, element.from.z/16, element.to.x/16, element.to.y/16, element.to.z/16, baseTrans.multiply(stateTrans.multiply(elementTrans)), textures, uvs, drawSides);
	}
	
	// Get the transform for the blockstate
	@Nonnull
	private Transform getStateTrans(ModelInfo modelInfo) {
		Transform t = Transform.translation(-0.5d, -0.5d, -0.5d);// offset cor to middle of block
		t = Transform.rotation(-modelInfo.x, 0, 0).multiply(t);// rotate -x ???
		t = Transform.rotation(0, modelInfo.y, 0).multiply(t);// then y
		return t;
	}
	
	// Calculate the element transformation
	@Nonnull
	private Transform getElemTrans(ModelElement element) {
		ElementRotation rot = element.rotation;
		Transform t = new Transform();
		if (rot == null) {
			return t;
		}
		if (rot.origin != null) {
			t = Transform.translation(-rot.origin.x/16, -rot.origin.y/16, -rot.origin.z/16).multiply(t);
		}
		float scale = 1;
		if (rot.rescale) {
			scale = (float) (0.5-Math.abs(Math.cos(Math.toRadians(2*rot.angle)))*0.5+1);
		}
		switch (rot.axis) {
		case "x":
			t = Transform.rotation(rot.angle, 0, 0).multiply(t);
			t = rot.rescale ? Transform.scale(1, scale, scale).multiply(t) : t;
			break;
		case "y":
			t = Transform.rotation(0, -rot.angle, 0).multiply(t);
			t = rot.rescale ? Transform.scale(scale, 1, scale).multiply(t) : t;
			break;
		case "z":
			t = Transform.rotation(0, 0, rot.angle).multiply(t);
			t = rot.rescale ? Transform.scale(scale, scale, 1).multiply(t) : t;
			break;
		default:
			Log.debugOnce(String.format("Model for %s had invalid rotation axis!", blockId));
			break;
		}
		if (rot.origin != null) {
			t = Transform.translation(rot.origin.x/16, rot.origin.y/16, rot.origin.z/16).multiply(t);
		}
		return t;
	}
	
	// Get the textures for each face of the cuboid
	@Nonnull
	private NamespaceID[] getFaceTextureArray(Map<String, ElementFace> faces, Map<String, String> textures) {
		NamespaceID[] array = new NamespaceID[6];
		Arrays.fill(array, Registries.UNKNOWN_TEX_ID);
		for (Entry<String, ElementFace> faceEntry : faces.entrySet()) {
			String tex = faceEntry.getValue().texture;
			if (tex != null && tex.startsWith("#")) {
				tex = textures.get(tex.substring(1));
			}
			if (tex == null) break;
			NamespaceID texNs = NamespaceID.fromString(tex);
			
			switch (faceEntry.getKey()) {
			case "up":
				array[0] = texNs; break;
			case "north":
				array[1] = texNs; break;
			case "south":
				array[2] = texNs; break;
			case "west":
				array[3] = texNs; break;
			case "east":
				array[4] = texNs; break;
			case "down":
				array[5] = texNs; break;
			default:
				Log.debugOnce(String.format("Model for %s had invalid face direction!", blockId));
				break;
			}
		}
		return array;
	}
	
	// Calculate the UVs for each face
	private UV[][] getFaceUvs(ModelElement elem, ModelInfo modelInfo) {
		Map<String, ElementFace> faces = elem.faces;
		UV[][] array = new UV[6][];
		for (Entry<String, ElementFace> faceEntry : faces.entrySet()) {
			float[] faceUv = faceEntry.getValue().uv;
			UV[] uvs = null;
			if (faceUv != null) {
				UV a = new UV(faceUv[0]/16, 1-(faceUv[1]/16));
				UV b = new UV(faceUv[0]/16, 1-(faceUv[3]/16));
				UV c = new UV(faceUv[2]/16, 1-(faceUv[3]/16));
				UV d = new UV(faceUv[2]/16, 1-(faceUv[1]/16));
				uvs = new UV[] {b, c, d, a};
			}
			double xs = elem.from.x/16;
			double ys = elem.from.y/16;
			double zs = elem.from.z/16;
			double xe = elem.to.x/16;
			double ye = elem.to.y/16;
			double ze = elem.to.z/16;
			
			int rot = faceEntry.getValue().rotation;
			
			switch (faceEntry.getKey()) {
			case "up":
				if (uvs == null) uvs = new UV[] { new UV(xs, 1-ze), new UV(xe, 1-ze), new UV(xe, 1-zs), new UV(xs, 1-zs) };
				rotateUVOrder(uvs, rot);
				array[0] = uvs; break;
			case "north":
				if (uvs == null) uvs = new UV[] { new UV(1-xe, ys), new UV(1-xs, ys), new UV(1-xs, ye), new UV(1-xe, ye) };
				rotateUVOrder(uvs, rot);
				array[1] = uvs; break;
			case "south":
				if (uvs == null) uvs = new UV[] { new UV(xs, ys), new UV(xe, ys), new UV(xe, ye), new UV(xs, ye) };
				rotateUVOrder(uvs, rot);
				array[2] = uvs; break;
			case "west":
				if (uvs == null) uvs = new UV[] { new UV(zs, ys), new UV(ze, ys), new UV(ze, ye), new UV(zs, ye) };
				rotateUVOrder(uvs, rot);
				array[3] = uvs; break;
			case "east":
				if (uvs == null) uvs = new UV[] { new UV(1-ze, ys), new UV(1-zs, ys), new UV(1-zs, ye), new UV(1-ze, ye) };
				rotateUVOrder(uvs, rot);
				array[4] = uvs; break;
			case "down":
				if (uvs == null) uvs = new UV[] { new UV(xe, ze), new UV(xs, ze), new UV(xs, zs), new UV(xe, zs) };
				rotateUVOrder(uvs, 180);//bottom is 180 rotated
				rotateUVOrder(uvs, rot);
				array[5] = uvs; break;
			default:
				Log.debugOnce(String.format("Model for %s had invalid face direction!", blockId));
				break;
			}
		}
		if (modelInfo.uvlock) {
			switch (modelInfo.x + "-" + modelInfo.y) {
			case "0-0":
				break;
			case "0-90":
			case "0-180":
			case "0-270":
				rotateFaceUVs(array[0], -modelInfo.y);
				rotateFaceUVs(array[5], modelInfo.y);
				break;
			case "90-0":
				rotateFaceUVs(array[0], 180);
				rotateFaceUVs(array[1], 180);
				rotateFaceUVs(array[3], 90);
				rotateFaceUVs(array[4], -90);
				break;
			case "90-90":
				rotateFaceUVs(array[0], 180);
				rotateFaceUVs(array[1], -90);
				rotateFaceUVs(array[2], -90);
				rotateFaceUVs(array[3], 90);
				rotateFaceUVs(array[4], -90);
				break;
			case "90-180":
				rotateFaceUVs(array[0], 180);
				rotateFaceUVs(array[2], 180);
				rotateFaceUVs(array[3], 90);
				rotateFaceUVs(array[4], -90);
				break;
			case "90-270":
				rotateFaceUVs(array[0], 180);
				rotateFaceUVs(array[1], 90);
				rotateFaceUVs(array[2], 90);
				rotateFaceUVs(array[3], 90);
				rotateFaceUVs(array[4], -90);
				break;
			case "180-0":
			case "180-90":
			case "180-180":
			case "180-270":
				rotateFaceUVs(array[0], modelInfo.y);
				rotateFaceUVs(array[1], 180);
				rotateFaceUVs(array[2], 180);
				rotateFaceUVs(array[3], 180);
				rotateFaceUVs(array[4], 180);
				rotateFaceUVs(array[5], -modelInfo.y);
				break;
			case "270-0":
				rotateFaceUVs(array[1], 180);
				rotateFaceUVs(array[3], -90);
				rotateFaceUVs(array[4], 90);
				rotateFaceUVs(array[5], 180);
				break;
			case "270-90":
				rotateFaceUVs(array[1], 90);
				rotateFaceUVs(array[2], 90);
				rotateFaceUVs(array[3], -90);
				rotateFaceUVs(array[4], 90);
				rotateFaceUVs(array[5], 180);
				break;
			case "270-180":
				rotateFaceUVs(array[2], 180);
				rotateFaceUVs(array[3], -90);
				rotateFaceUVs(array[4], 90);
				rotateFaceUVs(array[5], 180);
				break;
			case "270-270":
				rotateFaceUVs(array[1], -90);
				rotateFaceUVs(array[2], -90);
				rotateFaceUVs(array[3], -90);
				rotateFaceUVs(array[4], 90);
				rotateFaceUVs(array[5], 180);
				break;
			default:
				Log.debugOnce(String.format("Bad UV lock rotation in model for %s", blockId));
				break;
			}
		}
		return array;
	}
	
	// Rotate the UV array
	private void rotateUVOrder(UV[] uvs, int rot) {
		switch (rot) {
		case 0:
			break;
		case 90:
			Collections.rotate(Arrays.asList(uvs), -1);
			break;
		case 180:
			Collections.rotate(Arrays.asList(uvs), -2);
			break;
		case 270:
			Collections.rotate(Arrays.asList(uvs), -3);
			break;
		}
	}
	
	// Rotate the UV coordinates around 0,0
	private void rotateFaceUVs(@CheckForNull UV[] uvs, float rot) {
		if (uvs != null) {
			for (UV uv : uvs) {
				Transform t = Transform.translation(-0.5f, -0.5f, 0);
				t = Transform.rotation(0, 0, rot).multiply(t);
				t = Transform.translation(0.5f, 0.5f, 0).multiply(t);
				UV newUV = t.multiply(uv);
				uv.u = newUV.u;
				uv.v = newUV.v;
			}
		}
	}
	
	// Calculate the culling of each face
	@Nonnull
	private boolean[] getSidesCulling(ThreadChunkDeligate chunks, BlockPos pos, BlockData data, Map<String, ElementFace> faces, Transform stateTrans) {
		boolean[] array = new boolean[6];
		boolean[] ds = drawSides(chunks, pos.x, pos.y, pos.z, data);
		Transform rotation = stateTrans.multiply(Transform.translation(0.5f, 0.5f, 0.5f));
		for (Entry<String, ElementFace> faceEntry : faces.entrySet()) {
			int faceIndex;
			try {
				 faceIndex = Direction.valueOf(faceEntry.getKey().toUpperCase()).getArrIndex();
			} catch (IllegalArgumentException e) {
				Log.debugOnce(String.format("Model for %s had invalid face direction '%s'!", blockId, faceEntry.getKey()));
				continue;
			}
			
			array[faceIndex] = true;
			
			if (faceEntry.getValue().cullface == null) {
				continue;
			}
			Direction dir;
			switch (faceEntry.getValue().cullface) {
			case "up":
				dir = Direction.UP;
				break;
			case "north":
				dir = Direction.NORTH;
				break;
			case "south":
				dir = Direction.SOUTH;
				break;
			case "west":
				dir = Direction.WEST;
				break;
			case "east":
				dir = Direction.EAST;
				break;
			case "down":
			case "bottom":
				dir = Direction.DOWN;
				break;
			default:
				Log.debugOnce(String.format("Model for %s had invalid cullface direction!", blockId));
				continue;
			}
			dir = dir.rotate(rotation);
			array[faceIndex] = ds[dir.getArrIndex()];
		}
		return array;
	}
	
	private class AddedElem {
		private Transform stateTrans;
		private Transform elemTrans;
		private ModelElement elem;
		private boolean[] drawnSides;
		
		private AddedElem(Transform stateTrans, Transform elemTrans, ModelElement elem, boolean[] drawnSides) {
			this.stateTrans = stateTrans;
			this.elemTrans = elemTrans;
			this.elem = elem;
			this.drawnSides = drawnSides;
		}
		
		public boolean hasNew(AddedElem prevElem) {
			for (boolean side : getNew(prevElem)) {
				if (side) {
					return true;
				}
			}
			return false;
		}
		
		public boolean[] getNew(AddedElem prevElem) {
			if (prevElem.drawnSides.length != drawnSides.length) {
				throw new IllegalArgumentException("Sides array length mismatch!");
			}
			boolean[] newSides = new boolean[drawnSides.length];
			for (int i = 0; i < drawnSides.length; i++) {
				newSides[i] = !prevElem.drawnSides[i] && drawnSides[i];
			}
			return newSides;
		}
		
		public boolean matches(@CheckForNull AddedElem obj) {
			if (obj instanceof AddedElem) {
				AddedElem otherElem = (AddedElem)obj;
				boolean equal = stateTrans.equals(otherElem.stateTrans);
				equal &= elemTrans.equals(otherElem.elemTrans);
				equal &= elem.from.equals(otherElem.elem.from);
				equal &= elem.to.equals(otherElem.elem.to);
				return equal;
			}
			return false;
		}
		
		public void addSides(AddedElem elem) {
			if (elem.drawnSides.length != drawnSides.length) {
				throw new IllegalArgumentException("Sides array length mismatch!");
			}
			for (int i = 0; i < drawnSides.length; i++) {
				drawnSides[i] |= elem.drawnSides[i];
			}
		}
	}
}
