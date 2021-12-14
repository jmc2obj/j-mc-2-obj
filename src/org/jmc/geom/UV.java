package org.jmc.geom;


/**
 * Small class to represent a texture coordinate.
 */
public class UV
{
	public float u,v;
	public boolean recalculated;

	/**
	 * Constructor.
	 * @param u u coordinate
	 * @param v v coordinate
	 */
	public UV(float u, float v)
	{
		this.u = u;
		this.v = v;
		recalculated=false;
	}

	/**
	 * Convenience constructor, casts to float.
	 * @param u u coordinate
	 * @param v v coordinate
	 */
	public UV(double u, double v)
	{
		this.u = (float) u;
		this.v = (float) v;
		recalculated=false;
	}

	/**
	 * Copy constructor
	 * @param other
	 */
	public UV(UV other)
	{
		this.u = other.u;
		this.v = other.v;
		this.recalculated=other.recalculated;
	}
	
	@Override
	public String toString()
	{
		return "("+u+","+v+")";
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UV other = (UV)obj;
		return this.u == other.u && this.v == other.v;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(u);
		result = prime * result + Float.floatToIntBits(v);
		return result;
	}
	
	public boolean similar(UV other)
	{
		if (this == other)
			return true;
		if (other == null)
			return false;
		return FaceUtils.similar(this.u, other.u) &&
				FaceUtils.similar(this.v, other.v);
	}
	
	public static UV midpoint(UV uv1, UV uv2)
	{
		float u = (uv1.u+uv2.u)/2.0f;
		float v = (uv1.v+uv2.v)/2.0f;
		return new UV(u,v);
	}
	
	public static UV subtract(UV uv1, UV uv2) {
		float u = uv1.u - uv2.u;
		float v = uv1.v - uv2.v;
		return new UV(u, v);
	}
	
	public static UV add(UV uv1, UV uv2) {
		float u = uv1.u + uv2.u;
		float v = uv1.v + uv2.v;
		return new UV(u, v);
	}
	
	public static double distance(UV a, UV b)
	{
		UV c = subtract(a, b);
		return Math.sqrt(c.u * c.u + c.v * c.v);
	}
}