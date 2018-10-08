package org.jmc.models;

import java.util.HashMap;
import java.util.Vector;

import org.jmc.BlockTypes;
import org.jmc.geom.FaceUtils;
import org.jmc.geom.FaceUtils.Face;
import org.jmc.geom.FaceUtils.Half;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.geom.Side;
import org.jmc.geom.Transform;

/**
 * Model for stairs.
 */
public class Stairs extends BlockModel {

	private static Transform transforms[][];
	private static boolean invert_normals[] = new boolean[] { false, true, true, false };

	private static Vector<Face> original_shape = new Vector<Face>();
	private static Vector<Face> small_stair = new Vector<Face>();
	private static Vector<Face> large_stair = new Vector<Face>();

	static {
		transforms = new Transform[4][];
		for (int i = 0; i < 4; i++) {
			transforms[i] = new Transform[4];
			for (int j = 0; j < 4; j++) {
				transforms[i][j] = new Transform();
			}
		}

		transforms[0][1].rotate(0, 180, 0);
		transforms[0][2].rotate(0, 90, 0);
		transforms[0][3].rotate(0, -90, 0);

		Transform invert1 = new Transform();
		invert1.scale(1, -1, 1);
		Transform invert2 = new Transform();
		invert2.scale(1, 1, -1);

		for (int i = 0; i < 4; i++) {
			transforms[1][i] = transforms[0][i].multiply(invert1);
			transforms[2][i] = transforms[0][i].multiply(invert2);
			transforms[3][i] = transforms[0][i].multiply(invert1);
			transforms[3][i] = transforms[3][i].multiply(invert2);
		}

		Face face;

		// base
		face = FaceUtils.getOuterBlockFace(Side.BOTTOM);
		face.mtl_idx = 5;
		original_shape.add(face);
		small_stair.add(face);
		large_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Side.FRONT);
		face = FaceUtils.getHalf(face, Half.BOTTOM);
		face.mtl_idx = 1;
		original_shape.add(face);
		small_stair.add(face);
		large_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Side.BACK);
		face = FaceUtils.getHalf(face, Half.BOTTOM);
		face.mtl_idx = 2;
		original_shape.add(face);
		small_stair.add(face);
		large_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Side.LEFT);
		face = FaceUtils.getHalf(face, Half.BOTTOM);
		face.mtl_idx = 3;
		original_shape.add(face);
		small_stair.add(face);
		large_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Side.RIGHT);
		face = FaceUtils.getHalf(face, Half.BOTTOM);
		face.mtl_idx = 4;
		original_shape.add(face);
		small_stair.add(face);
		large_stair.add(face);

		// stair top
		face = FaceUtils.getOuterBlockFace(Side.TOP);
		face = FaceUtils.getHalf(face, Half.RIGHT);
		face.mtl_idx = 0;
		original_shape.add(face);
		large_stair.add(face);

		face = FaceUtils.getHalf(face, Half.TOP);
		small_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Side.TOP);
		face = FaceUtils.getHalf(face, Half.LEFT);
		face = FaceUtils.getHalf(face, Half.TOP);
		face.mtl_idx = 0;
		large_stair.add(face);

		// stair step
		face = FaceUtils.getOuterBlockFace(Side.TOP);
		face = FaceUtils.getHalf(face, Half.LEFT);
		face = FaceUtils.translate(face, 0, -0.5f, 0);
		face.mtl_idx = 0;
		original_shape.add(face);
		small_stair.add(face);

		face = FaceUtils.getHalf(face, Half.BOTTOM);
		large_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Side.TOP);
		face = FaceUtils.getHalf(face, Half.RIGHT);
		face = FaceUtils.getHalf(face, Half.BOTTOM);
		face = FaceUtils.translate(face, 0, -0.5f, 0);
		face.mtl_idx = 0;
		small_stair.add(face);

		// front
		face = FaceUtils.getOuterBlockFace(Side.LEFT);
		face = FaceUtils.getHalf(face, Half.TOP);
		face = FaceUtils.translate(face, 0.5f, 0, 0);
		face.mtl_idx = 1;
		original_shape.add(face);

		face = FaceUtils.getHalf(face, Half.RIGHT);
		large_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Side.LEFT);
		face = FaceUtils.getHalf(face, Half.TOP);
		face = FaceUtils.getHalf(face, Half.LEFT);
		face.mtl_idx = 1;
		large_stair.add(face);

		face = FaceUtils.translate(face, 0.5f, 0, 0);
		small_stair.add(face);

		// back
		face = FaceUtils.getOuterBlockFace(Side.RIGHT);
		face = FaceUtils.getHalf(face, Half.TOP);
		face.mtl_idx = 2;
		original_shape.add(face);
		large_stair.add(face);

		face = FaceUtils.getHalf(face, Half.RIGHT);
		small_stair.add(face);

		// left
		face = FaceUtils.getOuterBlockFace(Side.BACK);
		face = FaceUtils.getHalf(face, Half.TOP);
		face.mtl_idx = 3;
		large_stair.add(face);

		face = FaceUtils.getHalf(face, Half.LEFT);
		original_shape.add(face);
		small_stair.add(face);

		// right
		face = FaceUtils.getOuterBlockFace(Side.FRONT);
		face = FaceUtils.getHalf(face, Half.TOP);
		face = FaceUtils.getHalf(face, Half.RIGHT);
		face.mtl_idx = 4;
		original_shape.add(face);
		large_stair.add(face);

		face = FaceUtils.translate(face, 0, 0, 0.5f);
		small_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Side.FRONT);
		face = FaceUtils.getHalf(face, Half.TOP);
		face = FaceUtils.getHalf(face, Half.LEFT);
		face = FaceUtils.translate(face, 0, 0, 0.5f);
		face.mtl_idx = 4;
		large_stair.add(face);

	}

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome) {

		String[] mtls = getMtlSides(data, biome);
		boolean[] drawSides = drawSides(chunks, x, y, z);

		int dir = data & 3; // 0-east; 1-west; 2-south; 3-north
		int up = data & 4; // 0-regular ; 1-upside down
		if (up != 0)
			up = 1;
		int shape = getStairModification(chunks, x, y, z, data);
		int invert = 0;
		if (shape == 1 || shape == 3)
			invert = 2;

		Transform trans = transforms[invert + up][dir];
		
		Transform shift = new Transform();
		shift.translate(x, y, z);		

		Vector<Face> face_list;

		if (shape == 0 || shape == 1)
			face_list = small_stair;
		else if (shape == 2 || shape == 3)
			face_list = large_stair;
		else
			face_list = original_shape;

		for (Face face : face_list) {			
			
			if (invert_normals[invert + up])
				face = FaceUtils.invertNormals(face);			
			
			Face transface=trans.multiply(face);
			
			if(!FaceUtils.checkOcclusion(transface, drawSides))
			{
				FaceUtils.UVprojectFromView(transface, 0.5f);
				obj.addFace(transface.vertices, transface.uvs, shift, mtls[transface.mtl_idx]);
			}
		}

	}

	/**
	 * Retrieves if the stair requires any shape modification
	 * 
	 * @param chunks
	 *            object containing the data from neighboring blocks
	 * @param x
	 *            x location
	 * @param y
	 *            y location
	 * @param z
	 *            z location
	 * @param data
	 *            data of current block containing rotation of current stair
	 * @return -1 for no change in shape, 0 for small block left, 1 small block
	 *         right, 2 big block left, 3 big block right
	 */
	private int getStairModification(ThreadChunkDeligate chunks, int x, int y, int z, byte data) {
		int dir = data & 3; // 0-east; 1-west; 2-south; 3-north
		int up = data & 4; // 0-regular ; 1-upside down
		int ndir = -1;
		int nup = -1;

		switch (dir) {
		case 0:
			// get the block behind
			if (BlockTypes.get(chunks.getBlockID(x + 1, y, z)).getModel() instanceof Stairs) {
				// get the direction of the block behind
				ndir = chunks.getBlockData(x + 1, y, z) & 3;

				// check if both are up or down
				nup = chunks.getBlockData(x + 1, y, z) & 4;
				if (up != nup)
					break;

				if (ndir == 2)// if the direction is perpendicular to this one
					return 0;
				if (ndir == 3)
					return 1;
			}
			if (BlockTypes.get(chunks.getBlockID(x - 1, y, z)).getModel() instanceof Stairs) {
				ndir = chunks.getBlockData(x - 1, y, z) & 3;
				nup = chunks.getBlockData(x - 1, y, z) & 4;
				if (up != nup)
					break;
				if (ndir == 2)
					return 2;
				if (ndir == 3)
					return 3;
			}
			break;
		case 1:
			if (BlockTypes.get(chunks.getBlockID(x - 1, y, z)).getModel() instanceof Stairs) {
				ndir = chunks.getBlockData(x - 1, y, z) & 3;
				nup = chunks.getBlockData(x - 1, y, z) & 4;
				if (up != nup)
					break;
				if (ndir == 2)
					return 1;
				if (ndir == 3)
					return 0;
			}
			if (BlockTypes.get(chunks.getBlockID(x + 1, y, z)).getModel() instanceof Stairs) {
				ndir = chunks.getBlockData(x + 1, y, z) & 3;
				nup = chunks.getBlockData(x + 1, y, z) & 4;
				if (up != nup)
					break;
				if (ndir == 2)
					return 3;
				if (ndir == 3)
					return 2;
			}
			break;
		case 2:
			if (BlockTypes.get(chunks.getBlockID(x, y, z + 1)).getModel() instanceof Stairs) {
				ndir = chunks.getBlockData(x, y, z + 1) & 3;
				nup = chunks.getBlockData(x, y, z + 1) & 4;
				if (up != nup)
					break;
				if (ndir == 1)
					return 0;
				if (ndir == 0)
					return 1;
			}
			if (BlockTypes.get(chunks.getBlockID(x, y, z - 1)).getModel() instanceof Stairs) {
				ndir = chunks.getBlockData(x, y, z - 1) & 3;
				nup = chunks.getBlockData(x, y, z - 1) & 4;
				if (up != nup)
					break;
				if (ndir == 1)
					return 2;
				if (ndir == 0)
					return 3;
			}
		case 3:
		default:
			if (BlockTypes.get(chunks.getBlockID(x, y, z - 1)).getModel() instanceof Stairs) {
				ndir = chunks.getBlockData(x, y, z - 1) & 3;
				nup = chunks.getBlockData(x, y, z - 1) & 4;
				if (up != nup)
					break;
				if (ndir == 1)
					return 1;
				if (ndir == 0)
					return 0;
			}
			if (BlockTypes.get(chunks.getBlockID(x, y, z + 1)).getModel() instanceof Stairs) {
				ndir = chunks.getBlockData(x, y, z + 1) & 3;
				nup = chunks.getBlockData(x, y, z + 1) & 4;
				if (up != nup)
					break;
				if (ndir == 1)
					return 3;
				if (ndir == 0)
					return 2;
			}
			break;
		}

		return -1;
	}

}
