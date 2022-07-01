package org.jmc.geom;

import java.lang.reflect.Type;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

/**
 * Small class for describing Vertices in a sortable fashion.
 * @author danijel
 *
 */
@JsonAdapter(Vertex.VertexAdapter.class)
public class Vertex implements Comparable<Vertex>
{
	public double x,y,z;

	/**
	 * Vertex constructor.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public Vertex(double x, double y, double z)
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
	
	/**
	 * Returns the x/y/z from 0/1/2
	 * @param i should be 0, 1 or 2
	 * @return The axis co-ordinate
	 */
	public double getByInt(int i)
	{
		if (i == 0) return x;
		if (i == 1) return y;
		if (i == 2) return z;
		return 0;
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
		return Objects.hash(x, y, z);
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
	
	public boolean similar(Vertex v)
	{
		if (this == v)
			return true;
		if (v == null)
			return false;
		return FaceUtils.similar(this.x, v.x) &&
				FaceUtils.similar(this.y, v.y) &&
				FaceUtils.similar(this.z, v.z);
	}
	
	public static Vertex midpoint(Vertex v1, Vertex v2)
	{
		double x=(v1.x+v2.x)/2.0f;
		double y=(v1.y+v2.y)/2.0f;
		double z=(v1.z+v2.z)/2.0f;
		return new Vertex(x, y, z);
	}
	
	public static Vertex add(Vertex a, Vertex b)
	{
		double x=a.x+b.x;
		double y=a.y+b.y;
		double z=a.z+b.z;
		return new Vertex(x, y, z);
	}
	
	public static Vertex subtract(Vertex a, Vertex b)
	{
		double x=a.x-b.x;
		double y=a.y-b.y;
		double z=a.z-b.z;
		return new Vertex(x, y, z);
	}
	
	public static double distance(Vertex a, Vertex b)
	{
		Vertex c = subtract(a, b);
		return Math.sqrt(c.x * c.x + c.y * c.y + c.z * c.z);
	}
	
	public static double dot(Vertex a, Vertex b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}
	
	static class VertexAdapter implements JsonDeserializer<Vertex>, JsonSerializer<Vertex> {

		@Override
		public Vertex deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			try {
				JsonArray array = json.getAsJsonArray();
				double x = array.get(0).getAsDouble();
				double y = array.get(1).getAsDouble();
				double z = array.get(2).getAsDouble();
				return new Vertex(x, y, z);
			} catch (ClassCastException | IllegalStateException e) {
				throw new JsonParseException("Tried to parse invalid json as Vertex", e);
			}
		}

		@Override
		public JsonElement serialize(Vertex src, Type typeOfSrc, JsonSerializationContext context) {
			JsonArray array = new JsonArray(3);
			array.add(src.x);
			array.add(src.y);
			array.add(src.z);
			return array;
		}
		
	}
}