package org.jmc.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jmc.BlockData;
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

public class Registry extends BlockModel {

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome) {
		Transform posTrans = Transform.translation(x, y, z);
		
		BlockstateEntry bsEntry = Registries.getBlockstate(NamespaceID.fromString(data.id));
		List<ModelListWeighted> modelParts = bsEntry.getModelsFor(data.state);
		
		for (ModelListWeighted modelList : modelParts) {
			ModelInfo modelInfo = modelList.getRandomModel();
			ModelEntry modelEntry = Registries.getModel(modelInfo.id);
			RegistryModel model = modelEntry.generateModel();
			if (model.elements != null) {
				for (ModelElement element : model.elements) {
					addElement(obj, modelInfo, model, element, new Transform(posTrans));
				}
			}
		}
		//Log.debug(String.format("Models for %s: %s", data.toString(), String.valueOf(models)));
	}

	private void addElement(ChunkProcessor obj, ModelInfo modelInfo, RegistryModel model, ModelElement element, Transform baseTrans) {
		Transform stateTrans = getStateTrans(modelInfo);
		String[] textures = getFaceTextureArray(element.faces, model.textures);
		UV[][] uvs = getFaceUvs(element, modelInfo);
		boolean[] drawSides = getSidesCulling(element.faces);
		Transform elementTrans = getElemTrans(element);
		addBox(obj, element.from[0]/16, element.from[1]/16, element.from[2]/16, element.to[0]/16, element.to[1]/16, element.to[2]/16, baseTrans.multiply(stateTrans.multiply(elementTrans)), textures, uvs, drawSides);
	}

	private Transform getStateTrans(ModelInfo modelInfo) {
		Transform t = Transform.translation(-0.5f, -0.5f, -0.5f);// offset cor to middle of block
		t = Transform.rotation(-modelInfo.x, 0, 0).multiply(t);// rotate -x ???
		t = Transform.rotation(0, modelInfo.y, 0).multiply(t);// then y
		return t;
	}

	// Calculate the element transformation
	private Transform getElemTrans(ModelElement element) {
		ElementRotation rot = element.rotation;
		Transform t = new Transform();
		if (rot == null) {
			return t;
		}
		if (rot.origin != null) {
			t = t.multiply(Transform.translation(rot.origin[0]/16, rot.origin[1]/16, rot.origin[2]/16));
		}
		float scale = 1;
		if (rot.rescale) {
			scale = (float) (0.5-Math.abs(Math.cos(Math.toRadians(2*rot.angle)))*0.5+1);
		}
		switch (rot.axis) {
		case "x":
			t = t.multiply(Transform.rotation(rot.angle, 0, 0));
			t = t.multiply(Transform.scale(1, scale, scale));
			break;
		case "y":
			t = t.multiply(Transform.rotation(0, -rot.angle, 0));
			t = t.multiply(Transform.scale(scale, 1, scale));
			break;
		case "z":
			t = t.multiply(Transform.rotation(0, 0, rot.angle));
			t = t.multiply(Transform.scale(scale, scale, 1));
			break;
		default:
			Log.debug(String.format("Model for %s had invalid rotation axis!", blockId));
			break;
		}
		if (rot.origin != null) {
			t = t.multiply(Transform.translation(-rot.origin[0]/16, -rot.origin[1]/16, -rot.origin[2]/16));
		}
		return t;
	}

	// Get the textures for each face of the cuboid
	private String[] getFaceTextureArray(Map<String, ElementFace> faces, Map<String, String> textures) {
		String[] array = new String[] {"unknown", "unknown", "unknown", "unknown", "unknown", "unknown"};
		for (Entry<String, ElementFace> faceEntry : faces.entrySet()) {
			String tex = faceEntry.getValue().texture;
			if (tex != null && tex.startsWith("#")) {
				tex = NamespaceID.fromString(textures.get(tex.substring(1))).path.split("/")[1];//TODO tex.substring(1);
			}
			switch (faceEntry.getKey()) {
			case "up":
				array[0] = tex; break;
			case "north":
				array[1] = tex; break;
			case "south":
				array[2] = tex; break;
			case "west":
				array[3] = tex; break;
			case "east":
				array[4] = tex; break;
			case "down":
				array[5] = tex; break;
			default:
				Log.debug(String.format("Model for %s had invalid face direction!", blockId));
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
			float xs = elem.from[0]/16;
			float ys = elem.from[1]/16;
			float zs = elem.from[2]/16;
			float xe = elem.to[0]/16;
			float ye = elem.to[1]/16;
			float ze = elem.to[2]/16;
			
			int rot = faceEntry.getValue().rotation;
			
			switch (faceEntry.getKey()) {
			case "up":
				if (uvs == null) uvs = new UV[] { new UV(xs, -ze), new UV(xe, -ze), new UV(xe, -zs), new UV(xs, -zs) };
				rotateUVOrder(uvs, rot);
				array[0] = uvs; break;
			case "north":
				if (uvs == null) uvs = new UV[] { new UV(-xe, ys), new UV(-xs, ys), new UV(-xs, ye), new UV(-xe, ye) };
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
				if (uvs == null) uvs = new UV[] { new UV(-ze, ys), new UV(-zs, ys), new UV(-zs, ye), new UV(-ze, ye) };
				rotateUVOrder(uvs, rot);
				array[4] = uvs; break;
			case "down":
				if (uvs == null) uvs = new UV[] { new UV(xe, ze), new UV(xs, ze), new UV(xs, zs), new UV(xe, zs) };
				rotateUVOrder(uvs, 180);//bottom is 180 rotated
				rotateUVOrder(uvs, rot);
				array[5] = uvs; break;
			default:
				Log.debug(String.format("Model for %s had invalid face direction!", blockId));
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
				rotateFaceUVs(array[1], 180);
				rotateFaceUVs(array[2], 180);
				rotateFaceUVs(array[3], 180);
				rotateFaceUVs(array[4], 180);
				break;
			case "180-90":
				rotateFaceUVs(array[0], 90);
				rotateFaceUVs(array[1], 180);
				rotateFaceUVs(array[2], 180);
				rotateFaceUVs(array[3], 180);
				rotateFaceUVs(array[4], 180);
				rotateFaceUVs(array[5], -90);
				break;
			case "180-180":
				rotateFaceUVs(array[0], 180);
				rotateFaceUVs(array[1], 180);
				rotateFaceUVs(array[2], 180);
				rotateFaceUVs(array[3], 180);
				rotateFaceUVs(array[4], 180);
				rotateFaceUVs(array[5], 180);
				break;
			case "180-270":
				rotateFaceUVs(array[0], -90);
				rotateFaceUVs(array[1], 180);
				rotateFaceUVs(array[2], 180);
				rotateFaceUVs(array[3], 180);
				rotateFaceUVs(array[4], 180);
				rotateFaceUVs(array[5], 90);
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
				Log.debug("Bad UV lock rotation!");
				break;
			}
		}
		return array;
	}

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
	
	private void rotateFaceUVs(UV[] uvs, float rot) {
		if (uvs != null) {
			for (UV uv : uvs) {
				Transform t = Transform.translation(-0.5f, -0.5f, 0);
				t = t.multiply(Transform.rotation(0, 0, rot));
				t = t.multiply(Transform.translation(0.5f, 0.5f, 0));
				UV newUV = t.multiply(uv);
				uv.u = newUV.u;
				uv.v = newUV.v;
			}
		}
	}

	// Calculate the culling of each face
	private boolean[] getSidesCulling(Map<String, ElementFace> faces) {
		boolean[] array = new boolean[6];
		for (Entry<String, ElementFace> faceEntry : faces.entrySet()) {
			switch (faceEntry.getKey()) {
			case "up":
				array[0] = true; break;
			case "north":
				array[1] = true; break;
			case "south":
				array[2] = true; break;
			case "west":
				array[3] = true; break;
			case "east":
				array[4] = true; break;
			case "down":
				array[5] = true; break;
			default:
				Log.debug(String.format("Model for %s had invalid face direction!", blockId));
				break;
			}
		}
		return array;
	}

}
