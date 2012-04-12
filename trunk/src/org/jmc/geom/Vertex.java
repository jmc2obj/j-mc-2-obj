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
}