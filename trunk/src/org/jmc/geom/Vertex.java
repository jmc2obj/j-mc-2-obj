package org.jmc.geom;


/**
 * Small class for describing Vertices in a sortable fashion.
 * @author danijel
 *
 */
public class Vertex implements Comparable<Vertex>
{
	public float x,y,z;

	/**
	 * Vertex constructor.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public Vertex(float x, float y, float z)
	{
		this.x=x;
		this.y=y;
		this.z=z;
	}

	/**
	 * Copy constructor
	 * @param v
	 */
	public Vertex(Vertex v)
	{
		this.x=v.x;
		this.y=v.y;
		this.z=v.z;
	}
	
	
	public String toString()
	{
		return "("+x+","+y+","+z+")";
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
		Vertex other = (Vertex)obj;
		return this.x == other.x && this.y == other.y && this.z == other.z;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	/**
	 * Comparator that sorts vertices first along the X, then Y and finally Z axis.
	 */
	@Override
	public int compareTo(Vertex o) {
		if(this.x>o.x) return 1;
		if(this.x<o.x) return -1;
		if(this.y>o.y) return 1;
		if(this.y<o.y) return -1;
		if(this.z>o.z) return 1;
		if(this.z<o.z) return -1;
		return 0;
	}
	
	public static Vertex midpoint(Vertex v1, Vertex v2)
	{
		float x=(v1.x+v2.x)/2.0f;
		float y=(v1.y+v2.y)/2.0f;
		float z=(v1.z+v2.z)/2.0f;
		return new Vertex(x, y, z);
	}
}