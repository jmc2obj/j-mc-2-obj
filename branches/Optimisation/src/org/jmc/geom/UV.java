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
	
	public static UV midpoint(UV uv1, UV uv2)
	{
		float u = (uv1.u+uv2.u)/2.0f;
		float v = (uv1.v+uv2.v)/2.0f;
		return new UV(u,v);
	}

}