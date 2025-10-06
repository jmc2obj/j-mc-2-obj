package org.jmc.geom;

import java.util.Objects;

/**
 * A class representing a Quaternion, used for 3D rotations.
 * Quaternions provide a more robust way to represent rotations than Euler angles,
 * avoiding issues like gimbal lock.
 */
public class Quaternion {
	public double x, y, z, w;

	/**
	 * Default constructor. Creates an identity quaternion (0, 0, 0, 1), representing no rotation.
	 */
	public Quaternion() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
		this.w = 1.0;
	}

	/**
	 * Creates a quaternion with the given components.
	 *
	 * @param x The first imaginary part.
	 * @param y The second imaginary part.
	 * @param z The third imaginary part.
	 * @param w The real part.
	 */
	public Quaternion(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * Copy constructor.
	 *
	 * @param other The quaternion to copy.
	 */
	public Quaternion(Quaternion other) {
		this.w = other.w;
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}

	/**
	 * Creates a rotation quaternion from an axis and an angle.
	 *
	 * @param axis The axis of rotation. Must be a unit vector.
	 * @param angle The angle of rotation in degrees.
	 * @return A new Quaternion representing the rotation.
	 */
	public static Quaternion fromAxisAngle(Vertex axis, double angle) {
		double halfAngle = Math.toRadians(angle) / 2.0;
		double sinHalfAngle = Math.sin(halfAngle);

		return new Quaternion(
			axis.x * sinHalfAngle,
			axis.y * sinHalfAngle,
			axis.z * sinHalfAngle,
			Math.cos(halfAngle)
		);
	}

	/**
	 * Normalizes this quaternion to have a length of 1.
	 * Rotations should be represented by unit quaternions.
	 *
	 * @return This quaternion, normalized.
	 */
	public Quaternion normalize() {
		double len = Math.sqrt(w * w + x * x + y * y + z * z);
		if (len > 0) {
			w /= len;
			x /= len;
			y /= len;
			z /= len;
		}
		return this;
	}

	/**
	 * Multiplies this quaternion by another quaternion.
	 * This is equivalent to composing the rotations they represent.
	 *
	 * @param other The quaternion to multiply by.
	 * @return A new quaternion that is the result of the multiplication.
	 */
	public Quaternion multiply(Quaternion other) {
		return new Quaternion(
			w * other.x + x * other.w + y * other.z - z * other.y,
			w * other.y - x * other.z + y * other.w + z * other.x,
			w * other.z + x * other.y - y * other.x + z * other.w,
			w * other.w - x * other.x - y * other.y - z * other.z
		);
	}

	/**
	 * Converts this quaternion into a 4x4 rotation matrix.
	 *
	 * @return A {@link Transform} object representing the rotation.
	 */
	public Transform toTransform() {
		Transform t = new Transform();
		
		// Ensure it's a unit quaternion
		normalize();

		double xx = x * x;
		double xy = x * y;
		double xz = x * z;
		double xw = x * w;

		double yy = y * y;
		double yz = y * z;
		double yw = y * w;

		double zz = z * z;
		double zw = z * w;

		t.matrix[0][0] = 1 - 2 * (yy + zz);
		t.matrix[0][1] = 2 * (xy - zw);
		t.matrix[0][2] = 2 * (xz + yw);

		t.matrix[1][0] = 2 * (xy + zw);
		t.matrix[1][1] = 1 - 2 * (xx + zz);
		t.matrix[1][2] = 2 * (yz - xw);

		t.matrix[2][0] = 2 * (xz - yw);
		t.matrix[2][1] = 2 * (yz + xw);
		t.matrix[2][2] = 1 - 2 * (xx + yy);

		return t;
	}

	@Override
	public String toString() {
		return String.format("Quaternion[w=%.3f, x=%.3f, y=%.3f, z=%.3f]", w, x, y, z);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Quaternion that = (Quaternion) o;
		return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0 && Double.compare(that.w, w) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z, w);
	}
}