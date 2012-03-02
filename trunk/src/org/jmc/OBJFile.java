package org.jmc;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jmc.MTLFile.Side;

public class OBJFile {
	
	public enum TexCoordinate
	{
		TOPLEFT,
		TOPRIGHT,
		BOTTOMRIGHT,
		BOTTOMLEFT
	}
	
	public enum MeshType
	{
		BLOCK,
		IMAGE,
		TORCH,
		FENCE
		//TODO: complete this
	}
	
	private class Vertex implements Comparable<Vertex>
	{
		float x,y,z;
		
		Vertex(float x, float y, float z)
		{
			this.x=x;
			this.y=y;
			this.z=z;
		}

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
	
	private class Face implements Comparable<Face>
	{
		int [] vertices;
		Side side;
		int mtl_id;
		@Override
		public int compareTo(Face o) {
			return this.mtl_id-o.mtl_id;
		}
	}
	
	String identifier;
	MTLFile material;
	List<Vertex> vertices;
	Map<Vertex, Integer> vertex_map;
	List<Face> faces;
	
	float x_offset, y_offset, z_offset;
	
	public OBJFile(String ident, MTLFile mtl)
	{
		identifier=ident;
		material=mtl;
		vertices=new LinkedList<Vertex>();
		vertex_map=new TreeMap<Vertex, Integer>();
		faces=new LinkedList<OBJFile.Face>();
		x_offset=0;
		y_offset=0;
		z_offset=0;
	}
	
	public void setOffset(int x, int y, int z)
	{
		x_offset=x;
		y_offset=y;
		z_offset=z;
	}
	
	public void addCube(float x, float y, float z, int id, boolean [] drawside)
	{
		Vertex vertices[]=new Vertex[4];
		
		if(drawside[0])
		{
			vertices[0]=new Vertex(x-0.5f,y+0.5f,z-0.5f);
			vertices[1]=new Vertex(x-0.5f,y+0.5f,z+0.5f);
			vertices[2]=new Vertex(x+0.5f,y+0.5f,z+0.5f);
			vertices[3]=new Vertex(x+0.5f,y+0.5f,z-0.5f);
			addFace(vertices,Side.TOP,id);
		}
		if(drawside[1])
		{
			vertices[0]=new Vertex(x+0.5f,y-0.5f,z-0.5f);
			vertices[1]=new Vertex(x+0.5f,y-0.5f,z+0.5f);
			vertices[2]=new Vertex(x-0.5f,y-0.5f,z+0.5f);
			vertices[3]=new Vertex(x-0.5f,y-0.5f,z-0.5f);
			addFace(vertices,Side.BOTTOM,id);
		}
		if(drawside[2])
		{
			vertices[0]=new Vertex(x-0.5f,y-0.5f,z+0.5f);
			vertices[1]=new Vertex(x-0.5f,y+0.5f,z+0.5f);
			vertices[2]=new Vertex(x-0.5f,y+0.5f,z-0.5f);
			vertices[3]=new Vertex(x-0.5f,y-0.5f,z-0.5f);
			addFace(vertices,Side.LEFT,id);
		}
		if(drawside[3])
		{
			vertices[0]=new Vertex(x+0.5f,y-0.5f,z-0.5f);
			vertices[1]=new Vertex(x+0.5f,y+0.5f,z-0.5f);
			vertices[2]=new Vertex(x+0.5f,y+0.5f,z+0.5f);
			vertices[3]=new Vertex(x+0.5f,y-0.5f,z+0.5f);
			addFace(vertices,Side.RIGHT,id);
		}
		if(drawside[4])
		{
			vertices[0]=new Vertex(x-0.5f,y-0.5f,z-0.5f);
			vertices[1]=new Vertex(x-0.5f,y+0.5f,z-0.5f);
			vertices[2]=new Vertex(x+0.5f,y+0.5f,z-0.5f);
			vertices[3]=new Vertex(x+0.5f,y-0.5f,z-0.5f);
			addFace(vertices,Side.FRONT,id);
		}
		if(drawside[5])
		{
			vertices[0]=new Vertex(x+0.5f,y-0.5f,z+0.5f);
			vertices[1]=new Vertex(x+0.5f,y+0.5f,z+0.5f);
			vertices[2]=new Vertex(x-0.5f,y+0.5f,z+0.5f);
			vertices[3]=new Vertex(x-0.5f,y-0.5f,z+0.5f);
			addFace(vertices,Side.BACK,id);
		}
	}

	
	public void append(PrintWriter out)
	{
		out.println("g "+identifier);
		out.println();
		
		for(Vertex vertex:vertices)
		{
			out.format("v %2.2f %2.2f %2.2f",vertex.x+x_offset,vertex.y+y_offset,vertex.z+z_offset);
			out.println();
		}
		
		printTexturesAndNormals(out);
		
		Collections.sort(faces);
		int last_id=-2;	
		int normal_idx;
		int vertices_num=vertices.size();
		for(Face f:faces)
		{
			if(f.mtl_id<0) continue; //TODO: temporary modification - skip unknown materials  
			
			if(f.mtl_id!=last_id)
			{
				out.println();
				out.println("usemtl "+material.getMaterial(f.mtl_id));
				last_id=f.mtl_id;
			}
			
			normal_idx=sideToNormalIndex(f.side);
			
			out.print("f ");
			out.print((-vertices_num+f.vertices[0])+"/-4/"+normal_idx+" ");
			out.print((-vertices_num+f.vertices[1])+"/-3/"+normal_idx+" ");
			out.print((-vertices_num+f.vertices[2])+"/-2/"+normal_idx+" ");
			out.println((-vertices_num+f.vertices[3])+"/-1/"+normal_idx+" ");
		}				
	}
	
	
	private void printTexturesAndNormals(PrintWriter out)
	{
		out.println("vt 0 0");
		out.println("vt 1 0");
		out.println("vt 1 1");
		out.println("vt 0 1");
		out.println("vn 0 -1 0");
		out.println("vn 0 1 0");
		out.println("vn 1 0 0");
		out.println("vn -1 0 0");
		out.println("vn 0 0 1");
		out.println("vn 0 1 0");
		//TODO: finish printing normals
		
	}
	
	
	private int sideToNormalIndex(Side side)
	{
		switch(side)
		{
		case BOTTOM: return -6;
		case TOP: return -5;
		case RIGHT: return -4;
		case LEFT: return -3;
		case BACK: return -2;
		case FRONT: return -1;
		default: return -1;
		}
	}
	
	private void addFace(Vertex [] verts, Side side, int id)
	{
		Face face=new Face();
		face.side=side;
		face.mtl_id=material.getMaterialId(id, side);
		face.vertices=new int[4];
		for(int i=0; i<4; i++)
		{
			if(!vertex_map.containsKey(verts[i]))				
			{
				vertices.add(verts[i]);
				vertex_map.put(verts[i], vertices.size()-1);				
			}
			face.vertices[i]=vertex_map.get(verts[i]);
		}
		
		faces.add(face);
	}

}
