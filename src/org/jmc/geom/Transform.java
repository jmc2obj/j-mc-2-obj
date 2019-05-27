package org.jmc.geom;

import java.util.Arrays;

import org.jmc.geom.FaceUtils.Face;

/**
 * A class to perform simple affine transformations.
 * 
 * @author danijel
 * 
 */
public class Transform {
	double matrix[][];

	public Transform() {
		matrix = new double[4][4];
		identity();
	}

	private void identity() {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				if (i == j)
					matrix[i][j] = 1;
				else
					matrix[i][j] = 0;
			}
	}

	public Transform multiply(Transform a) {
		Transform ret = new Transform();
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				ret.matrix[i][j] = 0;
				for (int k = 0; k < 4; k++)
					ret.matrix[i][j] += matrix[i][k] * a.matrix[k][j];
			}
		return ret;
	}

	public Vertex multiply(Vertex vertex) {
		if (matrix[3][0] + matrix[3][1] + matrix[3][2] + matrix[3][3] != 1)
			throw new RuntimeException("matrix multiply error: last row doesn't add to 1");

		Vertex ret = new Vertex(0, 0, 0);
		ret.x = (float) (vertex.x * matrix[0][0] + vertex.y * matrix[0][1] + vertex.z * matrix[0][2] + matrix[0][3]);
		ret.y = (float) (vertex.x * matrix[1][0] + vertex.y * matrix[1][1] + vertex.z * matrix[1][2] + matrix[1][3]);
		ret.z = (float) (vertex.x * matrix[2][0] + vertex.y * matrix[2][1] + vertex.z * matrix[2][2] + matrix[2][3]);
		return ret;
	}


	public Face multiply(Face face) {
		Face ret = new Face();
		ret.mtl_idx = face.mtl_idx;
		ret.material = face.material;
		ret.uvs=face.uvs;
		ret.vertices = new Vertex[face.vertices.length];
		for (int i = 0; i < ret.vertices.length; i++)
			ret.vertices[i] = multiply(face.vertices[i]);

		return ret;
	}

	public Vertex applyToNormal(Vertex norm) {
		double[][] invt = new double[3][3]; // inverse transpose

		double a = matrix[0][0];
		double b = matrix[0][1];
		double c = matrix[0][2];
		double d = matrix[1][0];
		double e = matrix[1][1];
		double f = matrix[1][2];
		double g = matrix[2][0];
		double h = matrix[2][1];
		double k = matrix[2][2];

		double det = a * (e * k - f * h) + b * (f * g - d * k) + c * (d * h - e * g);

		invt[0][0] = (e * k - f * h) / det;
		invt[0][1] = (f * g - d * k) / det;
		invt[0][2] = (d * h - e * g) / det;
		invt[1][0] = (c * h - b * k) / det;
		invt[1][1] = (a * k - c * g) / det;
		invt[1][2] = (b * g - a * h) / det;
		invt[2][0] = (b * f - c * e) / det;
		invt[2][1] = (c * d - a * f) / det;
		invt[2][2] = (a * e - b * d) / det;

		return new Vertex((float) (norm.x * invt[0][0] + norm.y * invt[0][1] + norm.z * invt[0][2]),(float) (norm.x * invt[1][0] + norm.y
				* invt[1][1] + norm.z * invt[1][2]),(float) (norm.x * invt[2][0] + norm.y * invt[2][1] + norm.z * invt[2][2]));
	}

	public void translate(float x, float y, float z) {
		identity();

		matrix[0][3] = x;
		matrix[1][3] = y;
		matrix[2][3] = z;
	}

	public void scale(float x, float y, float z) {
		identity();

		matrix[0][0] = x;
		matrix[1][1] = y;
		matrix[2][2] = z;
	}

	public void rotate(double a, double b, double g) {
		// convert to rad
		a = Math.toRadians(a);
		b = Math.toRadians(b);
		g = Math.toRadians(g);

		identity();
		Transform ret;
		Transform trans = new Transform();

		trans.matrix[1][1] = Math.cos(a);
		trans.matrix[1][2] = -Math.sin(a);
		trans.matrix[2][1] = Math.sin(a);
		trans.matrix[2][2] = Math.cos(a);

		ret = multiply(trans);
		matrix = ret.matrix;

		trans.identity();
		trans.matrix[0][0] = Math.cos(b);
		trans.matrix[0][2] = -Math.sin(b);
		trans.matrix[2][0] = Math.sin(b);
		trans.matrix[2][2] = Math.cos(b);

		ret = multiply(trans);
		matrix = ret.matrix;

		trans.identity();
		trans.matrix[0][0] = Math.cos(g);
		trans.matrix[0][1] = -Math.sin(g);
		trans.matrix[1][0] = Math.sin(g);
		trans.matrix[1][1] = Math.cos(g);

		ret = multiply(trans);
		matrix = ret.matrix;
	}

	public void rotate2(double yaw, double pitch, double roll) {
		// TODO: check if this works correctly
		// convert to rad
		roll = Math.toRadians(roll);
		pitch = Math.toRadians(pitch);
		yaw = Math.toRadians(yaw);

		identity();
		Transform ret;
		Transform trans = new Transform();

		trans.matrix[0][0] = Math.cos(yaw);
		trans.matrix[0][2] = -Math.sin(yaw);
		trans.matrix[2][0] = Math.sin(yaw);
		trans.matrix[2][2] = Math.cos(yaw);
		ret = multiply(trans);
		matrix = ret.matrix;

		trans.identity();
		trans.matrix[1][1] = Math.cos(pitch);
		trans.matrix[1][2] = -Math.sin(pitch);
		trans.matrix[2][1] = Math.sin(pitch);
		trans.matrix[2][2] = Math.cos(pitch);

		ret = multiply(trans);
		matrix = ret.matrix;

		trans.identity();
		trans.matrix[0][0] = Math.cos(roll);
		trans.matrix[0][1] = -Math.sin(roll);
		trans.matrix[1][0] = Math.sin(roll);
		trans.matrix[1][1] = Math.cos(roll);

		ret = multiply(trans);
		matrix = ret.matrix;
	}
	
	@Override
	public String toString() {
		return Arrays.deepToString(matrix);
	}
}