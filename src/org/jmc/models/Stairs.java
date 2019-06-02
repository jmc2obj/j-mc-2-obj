package org.jmc.models;

import java.util.Vector;

import org.jmc.BlockData;
import org.jmc.geom.FaceUtils;
import org.jmc.geom.FaceUtils.Face;
import org.jmc.geom.FaceUtils.Half;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Log;

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
		face = FaceUtils.getOuterBlockFace(Direction.DOWN);
		face.mtl_idx = 5;
		original_shape.add(face);
		small_stair.add(face);
		large_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Direction.NORTH);
		face = FaceUtils.getHalf(face, Half.BOTTOM);
		face.mtl_idx = 1;
		original_shape.add(face);
		small_stair.add(face);
		large_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Direction.SOUTH);
		face = FaceUtils.getHalf(face, Half.BOTTOM);
		face.mtl_idx = 2;
		original_shape.add(face);
		small_stair.add(face);
		large_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Direction.WEST);
		face = FaceUtils.getHalf(face, Half.BOTTOM);
		face.mtl_idx = 3;
		original_shape.add(face);
		small_stair.add(face);
		large_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Direction.EAST);
		face = FaceUtils.getHalf(face, Half.BOTTOM);
		face.mtl_idx = 4;
		original_shape.add(face);
		small_stair.add(face);
		large_stair.add(face);

		// stair top
		face = FaceUtils.getOuterBlockFace(Direction.UP);
		face = FaceUtils.getHalf(face, Half.RIGHT);
		face.mtl_idx = 0;
		original_shape.add(face);
		large_stair.add(face);

		face = FaceUtils.getHalf(face, Half.TOP);
		small_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Direction.UP);
		face = FaceUtils.getHalf(face, Half.LEFT);
		face = FaceUtils.getHalf(face, Half.TOP);
		face.mtl_idx = 0;
		large_stair.add(face);

		// stair step
		face = FaceUtils.getOuterBlockFace(Direction.UP);
		face = FaceUtils.getHalf(face, Half.LEFT);
		face = FaceUtils.translate(face, 0, -0.5f, 0);
		face.mtl_idx = 0;
		original_shape.add(face);
		small_stair.add(face);

		face = FaceUtils.getHalf(face, Half.BOTTOM);
		large_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Direction.UP);
		face = FaceUtils.getHalf(face, Half.RIGHT);
		face = FaceUtils.getHalf(face, Half.BOTTOM);
		face = FaceUtils.translate(face, 0, -0.5f, 0);
		face.mtl_idx = 0;
		small_stair.add(face);

		// front
		face = FaceUtils.getOuterBlockFace(Direction.WEST);
		face = FaceUtils.getHalf(face, Half.TOP);
		face = FaceUtils.translate(face, 0.5f, 0, 0);
		face.mtl_idx = 1;
		original_shape.add(face);

		face = FaceUtils.getHalf(face, Half.RIGHT);
		large_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Direction.WEST);
		face = FaceUtils.getHalf(face, Half.TOP);
		face = FaceUtils.getHalf(face, Half.LEFT);
		face.mtl_idx = 1;
		large_stair.add(face);

		face = FaceUtils.translate(face, 0.5f, 0, 0);
		small_stair.add(face);

		// back
		face = FaceUtils.getOuterBlockFace(Direction.EAST);
		face = FaceUtils.getHalf(face, Half.TOP);
		face.mtl_idx = 2;
		original_shape.add(face);
		large_stair.add(face);

		face = FaceUtils.getHalf(face, Half.RIGHT);
		small_stair.add(face);

		// left
		face = FaceUtils.getOuterBlockFace(Direction.SOUTH);
		face = FaceUtils.getHalf(face, Half.TOP);
		face.mtl_idx = 3;
		large_stair.add(face);

		face = FaceUtils.getHalf(face, Half.LEFT);
		original_shape.add(face);
		small_stair.add(face);

		// right
		face = FaceUtils.getOuterBlockFace(Direction.NORTH);
		face = FaceUtils.getHalf(face, Half.TOP);
		face = FaceUtils.getHalf(face, Half.RIGHT);
		face.mtl_idx = 4;
		original_shape.add(face);
		large_stair.add(face);

		face = FaceUtils.translate(face, 0, 0, 0.5f);
		small_stair.add(face);

		face = FaceUtils.getOuterBlockFace(Direction.NORTH);
		face = FaceUtils.getHalf(face, Half.TOP);
		face = FaceUtils.getHalf(face, Half.LEFT);
		face = FaceUtils.translate(face, 0, 0, 0.5f);
		face.mtl_idx = 4;
		large_stair.add(face);

	}

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome) {

		String[] mtls = getMtlSides(data, biome);
		boolean[] drawSides = drawSides(chunks, x, y, z);

		int dir = getFacingDir(data); // 0-east; 1-west; 2-south; 3-north
		int up = data.get("half").equals("top") ? 1 : 0;
		
		/*
		 *  -1 for no change in shape (straight)
		 *  0 for small block left (outer_right)
		 *  1 small block right (outer_left)
		 *  2 big block left (inner_right)
		 *  3 big block right (inner_left)
		 */
		int shape;
		switch (data.get("shape"))
		{
			case ("outer_right"):
				shape = 0;
				break;
			case ("outer_left"):
				shape = 1;
				break;
			case ("inner_right"):
				shape = 2;
				break;
			case ("inner_left"):
				shape = 3;
				break;
			default: // Straight
				shape = -1;
				break;
		}
		
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
	
	@Override
	protected boolean getCustomOcclusion(Direction side, BlockData neighbourData, BlockData data) {
		switch (side) {//top / bottom are always solid
		case UP:
			return data.get("half").equals("top");
		case DOWN:
			return data.get("half").equals("bottom");
		default:
			if (data.get("shape").startsWith("outer_"))
				return false;
			break;
		}
		
		Direction facing = Direction.valueOf(data.get("facing").toUpperCase());
		
		if (data.get("shape").equals("straight") && neighbourData.equalData(data) && side != facing && side != facing.getOpposite()) {
			return true;
		}
		
		switch(facing) {//blocks facing direction and left or right if corner piece
		case NORTH:
			switch(side) {
			case NORTH:
				return true;
			case WEST:
				return data.get("shape").equals("inner_left");
			case EAST:
				return data.get("shape").equals("inner_right");
			default:
				return false;
			}
		case EAST:
			switch(side) {
			case EAST:
				return true;
			case NORTH:
				return data.get("shape").equals("inner_left");
			case SOUTH:
				return data.get("shape").equals("inner_right");
			default:
				return false;
			}
		case SOUTH:
			switch(side) {
			case SOUTH:
				return true;
			case EAST:
				return data.get("shape").equals("inner_left");
			case WEST:
				return data.get("shape").equals("inner_right");
			default:
				return false;
			}
		case WEST:
			switch(side) {
			case WEST:
				return true;
			case SOUTH:
				return data.get("shape").equals("inner_left");
			case NORTH:
				return data.get("shape").equals("inner_right");
			default:
				return false;
			}
		default:
			return false;
		}
	}

	private int getFacingDir(BlockData data) {
		int dir;
		switch (data.get("facing")) {
		case "north":
			dir = 3;
			break;
		case "east":
			dir = 0;
			break;
		case "south":
			dir = 2;
			break;
		case "west":
			dir = 1;
			break;

		default:
			Log.error("Unknown stair facing value! " + data.get("facing"), null, false);
			dir = 0;
		}
		return dir;
	}

}
