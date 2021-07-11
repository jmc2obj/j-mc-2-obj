package org.jmc.geom;

import java.util.Arrays;

import org.jmc.util.Log;

/**
 * Class describing some standard faces used in models, to simplify their
 * implementation.
 * 
 * @author danijel
 * 
 */
public class FaceUtils {

	public static class Face {
		public Vertex[] vertices;
		public UV[] uvs;
		public Vertex[] norms;
		public int mtl_idx;
		public String material;
		public boolean remove = false;
		public int chunk_idx = -1;
		
		public Face() {
		}
		
		public Face(Vertex[] vert, UV[] uv, Vertex[] norm, String mat) {
			vertices = vert;
			uvs = uv;
			norms = norm;
			material = mat;
		}

		/**
		 * @return 0 if x, 1 if y, 2 if z, 3 if none.
		 */
		public int isPlanar(){
			if (vertices[0].x == vertices[1].x && vertices[0].x == vertices[2].x && vertices[0].x == vertices[3].x){
				return 0;
			}
			else if (vertices[0].y == vertices[1].y && vertices[0].y == vertices[2].y && vertices[0].y == vertices[3].y){
				return 1;
			}
			else if (vertices[0].z == vertices[1].z && vertices[0].z == vertices[2].z && vertices[0].z == vertices[3].z){
				return 2;
			}
			else {
				return 3;
			}
		}

		public boolean isAnticlockwise() {
			int a;
			int b;
			switch(isPlanar()){
			case 0: a = 1; b = 2; break;
			case 1: a = 0; b = 2; break;
			case 2: a = 0; b = 1; break;
			default: Log.debug("isPlanar returned an unknown value!"); a = b = 0; break;
			}
			if (vertices[1].getByInt(a) > vertices[0].getByInt(a)){//increasing a
		        if (vertices[2].getByInt(b) > vertices[1].getByInt(b)){//increasing b: anticlockwise
	                return true;
		        }
			}
			if (vertices[1].getByInt(a) < vertices[0].getByInt(a)){//decreasing a
		        if (vertices[2].getByInt(b) < vertices[1].getByInt(b)){//decreasing b: anticlockwise
	                return true;
		        }
			}
			if (vertices[1].getByInt(b) > vertices[0].getByInt(b)){//increasing b
		        if (vertices[2].getByInt(a) < vertices[1].getByInt(a)){//decreasing a: anticlockwise
	                return true;
		        }
			}
			if (vertices[1].getByInt(b) < vertices[0].getByInt(b)){//decreasing b
		        if (vertices[2].getByInt(a) > vertices[1].getByInt(a)){//increasing a: anticlockwise
	                return true;
		        }
			}
            return false;
		}

		public boolean isUVAnticlockwise() {
			if (uvs[1].u > uvs[0].u){//increasing u
		        if (uvs[2].v > uvs[1].v){//increasing v: anticlockwise
	                return true;
		        }
			}
			if (uvs[1].u < uvs[0].u){//decreasing u
		        if (uvs[2].v < uvs[1].v){//decreasing v: anticlockwise
	                return true;
		        }
			}
			if (uvs[1].v > uvs[0].v){//increasing v
		        if (uvs[2].u < uvs[1].u){//decreasing u: anticlockwise
	                return true;
		        }
			}
			if (uvs[1].v < uvs[0].v){//decreasing v
		        if (uvs[2].u > uvs[1].u){//increasing u: anticlockwise
	                return true;
		        }
			}
            return false;
		}

		@Override
		public String toString() {
			return "Mat:'" + material + "' Verts:" + Arrays.toString(vertices) + " UVs:" + Arrays.toString(uvs);
		}
	}

	/**
	 * Describes a face in the OBJ file.
	 * Faces can be sorted by material.
	 */
	public static class OBJFace implements Comparable<OBJFace>
	{
		public int[] vertices;
		public int[] normals;
		public int[] uv;
		public String mtl;
		public Long obj_idx;
		
		public OBJFace(int sides)
		{
			obj_idx=Long.valueOf(-1);
			vertices=new int[sides];
			normals=new int[sides];
			uv=new int[sides];
		}
		
		public OBJFace(OBJFace f) {
			obj_idx = f.obj_idx;
			vertices = f.vertices.clone();
			normals = f.normals != null ? f.normals.clone() : null;
			uv = f.uv != null ? f.uv.clone() : null;
			mtl = f.mtl;
		}
		
		@Override
		public int compareTo(OBJFace o) {
			if(!this.obj_idx.equals(o.obj_idx))
				return this.obj_idx.compareTo(o.obj_idx);
			return this.mtl.compareTo(o.mtl);
		}
	}

	// Outer vertices:
	// Bottom/Top Front/Back Left/Right
	// BFL,BFR,BBL,BBR,TFL,TFR,TBL,TBR
	// 0 1 2 3 4 5 6 7
	private static Vertex OV[] = new Vertex[] { new Vertex(-0.5f, -0.5f, -0.5f), new Vertex(0.5f, -0.5f, -0.5f),
			new Vertex(-0.5f, -0.5f, 0.5f), new Vertex(0.5f, -0.5f, 0.5f), new Vertex(-0.5f, 0.5f, -0.5f),
			new Vertex(0.5f, 0.5f, -0.5f), new Vertex(-0.5f, 0.5f, 0.5f), new Vertex(0.5f, 0.5f, 0.5f) };

	private final static int ovBFL = 0;
	private final static int ovBFR = 1;
	private final static int ovBBL = 2;
	private final static int ovBBR = 3;
	private final static int ovTFL = 4;
	private final static int ovTFR = 5;
	private final static int ovTBL = 6;
	private final static int ovTBR = 7;

	// Outer UVs:
	// Bottm/Top Left/Right
	// BL, BR, TL, TR
	// 0 1 2 3
	private static UV OU[] = new UV[] { new UV(0, 0), new UV(1, 0), new UV(0, 1), new UV(1, 1) };

	private final static int ouBL = 0;
	private final static int ouBR = 1;
	private final static int ouTL = 2;
	private final static int ouTR = 3;

	// outer block faces:
	private static Vertex[][] OBF = new Vertex[][] { { OV[ovTFL], OV[ovTBL], OV[ovTBR], OV[ovTFR] },// top
			{ OV[ovBBL], OV[ovBFL], OV[ovBFR], OV[ovBBR] },// bottom
			{ OV[ovBFL], OV[ovTFL], OV[ovTFR], OV[ovBFR] },// front
			{ OV[ovBBR], OV[ovTBR], OV[ovTBL], OV[ovBBL] },// back
			{ OV[ovBBL], OV[ovTBL], OV[ovTFL], OV[ovBFL] },// left
			{ OV[ovBFR], OV[ovTFR], OV[ovTBR], OV[ovBBR] } };// right

	/**
	 * Gets the outer face of a standard minecraft block.
	 * 
	 * @param sides
	 * @return
	 */
	public static Face getOuterBlockFace(Direction side) {
		Face ret = new Face();
		ret.uvs = new UV[] { OU[ouBL], OU[ouTL], OU[ouTR], OU[ouBR] };
		switch (side) {
		case UP:
			ret.vertices = OBF[0];
			break;
		case DOWN:
			ret.vertices = OBF[1];
			break;
		case NORTH:
			ret.vertices = OBF[2];
			break;
		case SOUTH:
			ret.vertices = OBF[3];
			break;
		case WEST:
			ret.vertices = OBF[4];
			break;
		case EAST:
		default:
			ret.vertices = OBF[5];
			break;
		}
		return ret;
	}

	public static enum Half {
		TOP, BOTTOM, LEFT, RIGHT
	}

	/**
	 * Get half of a given face.
	 * 
	 * @param face
	 * @param side
	 *            0 - top, 1 - bottom, 2 - left, 3 - right
	 * @return
	 */
	public static Face getHalf(Face face, Half half) {
		Face ret = new Face();

		ret.mtl_idx = face.mtl_idx;

		Vertex v1 = face.vertices[0];
		Vertex v2 = face.vertices[1];
		Vertex v3 = face.vertices[2];
		Vertex v4 = face.vertices[3];

		Vertex m12 = Vertex.midpoint(v1, v2);
		Vertex m23 = Vertex.midpoint(v2, v3);
		Vertex m34 = Vertex.midpoint(v3, v4);
		Vertex m41 = Vertex.midpoint(v4, v1);

		UV uv1 = face.uvs[0];
		UV uv2 = face.uvs[1];
		UV uv3 = face.uvs[2];
		UV uv4 = face.uvs[3];

		UV um12 = UV.midpoint(uv1, uv2);
		UV um23 = UV.midpoint(uv2, uv3);
		UV um34 = UV.midpoint(uv3, uv4);
		UV um41 = UV.midpoint(uv4, uv1);

		switch (half) {
		case TOP:
			ret.vertices = new Vertex[] { m12, v2, v3, m34 };
			ret.uvs = new UV[] { um12, uv2, uv3, um34 };
			break;
		case BOTTOM:
			ret.vertices = new Vertex[] { v1, m12, m34, v4 };
			ret.uvs = new UV[] { uv1, um12, um34, uv4 };
			break;
		case LEFT:
			ret.vertices = new Vertex[] { v1, v2, m23, m41 };
			ret.uvs = new UV[] { uv1, uv2, um23, um41 };
			break;
		case RIGHT:
		default:
			ret.vertices = new Vertex[] { m41, m23, v3, v4 };
			ret.uvs = new UV[] { um41, um23, uv3, uv4 };
			break;
		}

		return ret;
	}

	public static Face translate(Face face, float x, float y, float z) {
		Face ret = new Face();

		ret.mtl_idx = face.mtl_idx;
		ret.uvs = face.uvs;

		Vertex v1 = face.vertices[0];
		Vertex v2 = face.vertices[1];
		Vertex v3 = face.vertices[2];
		Vertex v4 = face.vertices[3];

		ret.vertices = new Vertex[] { new Vertex(v1.x + x, v1.y + y, v1.z + z),
				new Vertex(v2.x + x, v2.y + y, v2.z + z), new Vertex(v3.x + x, v3.y + y, v3.z + z),
				new Vertex(v4.x + x, v4.y + y, v4.z + z) };

		return ret;
	}

	public static Face invertNormals(Face face) {
		Face ret = new Face();
		ret.mtl_idx = face.mtl_idx;
		ret.vertices = new Vertex[] { face.vertices[0], face.vertices[3], face.vertices[2], face.vertices[1] };
		ret.uvs = new UV[] { face.uvs[0], face.uvs[3], face.uvs[2], face.uvs[1] };
		return ret;
	}

	private final static float e = 0.001f;

	public static boolean similar(float a, float b) {
		if (Math.abs(a - b) < e)
			return true;
		else
			return false;
	}

	/**
	 * Checks if the face is occluded.
	 * 
	 * @param face
	 *            the face that needs to be checked
	 * @param drawSides
	 *            array with values if sides should be drawn (is not occluded by
	 *            block)
	 * @return true if occluded (so don't render); false if not occluded (so do
	 *         render)
	 */
	public static boolean checkOcclusion(Face face, boolean[] drawSides) {
		boolean x = true, y = true, z = true;
		for (int i = 1; i < 4; i++) {
			if (!similar(face.vertices[i].x, face.vertices[0].x))
				x = false;
			if (!similar(face.vertices[i].y, face.vertices[0].y))
				y = false;
			if (!similar(face.vertices[i].z, face.vertices[0].z))
				z = false;
		}

		float val;

		// occluded order TOP, FRONT, BACK, LEFT, RIGHT, BOTTOM
		// 0 1 2 3 4 5

		if (x) {
			val = face.vertices[0].x;
			if (similar(val, -0.5f) && !drawSides[3])
				return true;
			if (similar(val, 0.5f) && !drawSides[4])
				return true;
			return false;
		} else if (y) {
			val = face.vertices[0].y;
			if (similar(val, -0.5f) && !drawSides[5])
				return true;
			if (similar(val, 0.5f) && !drawSides[0])
				return true;
			return false;
		} else if (z) {
			val = face.vertices[0].z;
			if (similar(val, -0.5f) && !drawSides[1])
				return true;
			if (similar(val, 0.5f) && !drawSides[2])
				return true;
			return false;
		}

		return false;
	}

	/**
	 * Maps the UV coordinates to be proportional to the world coordinates.
	 * 
	 * @param face
	 *            Face to fix the UVs for.
	 * @param bounds
	 *            Bounds in 3D that limit the size of this object (so this value
	 *            in every direction around 0,0,0).
	 */
	public static void UVprojectFromView(Face face, float bounds) {

		// calculate normal
		Vertex U, V, N;

		U = Vertex.subtract(face.vertices[1], face.vertices[0]);
		V = Vertex.subtract(face.vertices[2], face.vertices[0]);

		N = new Vertex(0, 0, 0);

		N.x = U.y * V.z - U.z * V.y;
		N.y = U.z * V.x - U.x * V.z;
		N.z = U.x * V.y - U.y * V.x;

		float u, v;

		if (similar(N.y, 0) && similar(N.z, 0)) {
			if (N.x > 0) {
				face.uvs = new UV[face.vertices.length];
				for (int i = 0; i < face.uvs.length; i++) {
					u = bounds - face.vertices[i].z;
					v = face.vertices[i].y + bounds;
					face.uvs[i] = new UV(u, v);
				}
			} else {
				face.uvs = new UV[face.vertices.length];
				for (int i = 0; i < face.uvs.length; i++) {
					u = face.vertices[i].z + bounds;
					v = face.vertices[i].y + bounds;
					face.uvs[i] = new UV(u, v);
				}
			}
		} else if (similar(N.x, 0) && similar(N.z, 0)) {
			if (N.y > 0) {
				face.uvs = new UV[face.vertices.length];
				for (int i = 0; i < face.uvs.length; i++) {
					u = face.vertices[i].x + bounds;
					v = bounds - face.vertices[i].z;
					face.uvs[i] = new UV(u, v);
				}
			} else {
				face.uvs = new UV[face.vertices.length];
				for (int i = 0; i < face.uvs.length; i++) {
					u = bounds - face.vertices[i].x;
					v = bounds - face.vertices[i].z;
					face.uvs[i] = new UV(u, v);
				}
			}
		} else if (similar(N.x, 0) && similar(N.y, 0)) {
			if (N.z > 0) {
				face.uvs = new UV[face.vertices.length];
				for (int i = 0; i < face.uvs.length; i++) {
					u = face.vertices[i].x + bounds;
					v = face.vertices[i].y + bounds;
					face.uvs[i] = new UV(u, v);
				}
			} else {
				face.uvs = new UV[face.vertices.length];
				for (int i = 0; i < face.uvs.length; i++) {
					u = bounds - face.vertices[i].x;
					v = face.vertices[i].y + bounds;
					face.uvs[i] = new UV(u, v);
				}
			}
		}

		// in other cases, the normal isn't perpendicular, so simply skip fixing
		// UVs

	}

}
