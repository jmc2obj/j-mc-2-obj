/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.jmc.MTLFile.Side;

/**
 * OBJ file class.
 * This file contains the geometry of the whole world we are trying to export.
 * It also contains the links to the materials saved in the MTL file.
 * @author danijel
 *
 */
public class OBJFile {

	/**
	 * Small enum describing the UV location of the texture for simple rectangular
	 * textures.
	 * @author danijel
	 *
	 */
	public enum TexCoordinate
	{
		TOPLEFT,
		TOPRIGHT,
		BOTTOMRIGHT,
		BOTTOMLEFT
	}

	/**
	 * Small enum describing the mesh types.
	 * Currently only blocks are supported.
	 * @author danijel
	 *
	 */
	public enum MeshType
	{
		BLOCK,
		STAIRS,
		HALFBLOCK,
		CROSS,
		SNOW,
		LIQUID,
		TORCH
		//TODO: complete this
	}

	/**
	 * Small internal class for describing a simple rectangular face of an object.
	 * @author danijel
	 *
	 */
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

	/**
	 * Identifier of the file.
	 * Since many OBJ class objects are created by different chunks,
	 * this helps differentiate them and assign them a name. It's
	 * usually just the coordinates of the chunk.
	 */
	String identifier;
	/**
	 * Reference to the MTL file used in this OBJ.
	 */
	MTLFile material;
	/**
	 * List of vertices in the file.
	 */
	List<Vertex> vertices;
	/**
	 * Map of vertices to their respective IDs used in the faces of the mesh.
	 */
	Map<Vertex, Integer> vertex_map;
	/**
	 * List of faces in the file.
	 */
	List<Face> faces;

	/**
	 * Offsets of the file. Used to position the chunk in its right location.
	 */
	float x_offset, y_offset, z_offset;
	
	float file_scale;

	/**
	 * Main constructor.
	 * @param ident identifier of the OBJ
	 * @param mtl reference to the MTL
	 */
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
		file_scale=1.0f;
	}

	/**
	 * Offset all the vertices by these amounts.
	 * Used to position the chunk in its right location.
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 */
	public void setOffset(int x, int y, int z)
	{
		x_offset=x;
		y_offset=y;
		z_offset=z;
	}
	
	public void setScale(float scale)
	{
		file_scale=scale;
	}

	/**
	 * Add a cube at the given location.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @param id block id
	 * @param data
	 * @param drawside 6-element array describing which sides are to be drawn
	 * @param sx scale in x axis
	 * @param sy scale in y axis
	 * @param sz scale in z axis
	 */
	public void addCube(float x, float y, float z, int id, byte data, boolean [] drawside, Transform trans)
	{
		Vertex vertices[]=new Vertex[4];
		
		Transform move=new Transform();
		move.translate(x, y, z);
		
		if(drawside[0])
		{
			vertices[0]=new Vertex(-0.5f,+0.5f,-0.5f);
			vertices[1]=new Vertex(-0.5f,+0.5f,+0.5f);
			vertices[2]=new Vertex(+0.5f,+0.5f,+0.5f);
			vertices[3]=new Vertex(+0.5f,+0.5f,-0.5f);			
			addFace(vertices,move.multiply(trans),Side.TOP,id,data);
		}
		if(drawside[1])
		{
			vertices[0]=new Vertex(+0.5f,-0.5f,-0.5f);
			vertices[1]=new Vertex(+0.5f,-0.5f,+0.5f);
			vertices[2]=new Vertex(-0.5f,-0.5f,+0.5f);
			vertices[3]=new Vertex(-0.5f,-0.5f,-0.5f);
			addFace(vertices,move.multiply(trans),Side.BOTTOM,id,data);
		}
		if(drawside[2])
		{
			vertices[0]=new Vertex(-0.5f,-0.5f,+0.5f);
			vertices[1]=new Vertex(-0.5f,+0.5f,+0.5f);
			vertices[2]=new Vertex(-0.5f,+0.5f,-0.5f);
			vertices[3]=new Vertex(-0.5f,-0.5f,-0.5f);
			addFace(vertices,move.multiply(trans),Side.LEFT,id,data);
		}
		if(drawside[3])
		{
			vertices[0]=new Vertex(+0.5f,-0.5f,-0.5f);
			vertices[1]=new Vertex(+0.5f,+0.5f,-0.5f);
			vertices[2]=new Vertex(+0.5f,+0.5f,+0.5f);
			vertices[3]=new Vertex(+0.5f,-0.5f,+0.5f);
			addFace(vertices,move.multiply(trans),Side.RIGHT,id,data);
		}
		if(drawside[4])
		{
			vertices[0]=new Vertex(-0.5f,-0.5f,-0.5f);
			vertices[1]=new Vertex(-0.5f,+0.5f,-0.5f);
			vertices[2]=new Vertex(+0.5f,+0.5f,-0.5f);
			vertices[3]=new Vertex(+0.5f,-0.5f,-0.5f);
			addFace(vertices,move.multiply(trans),Side.FRONT,id,data);
		}
		if(drawside[5])
		{
			vertices[0]=new Vertex(+0.5f,-0.5f,+0.5f);
			vertices[1]=new Vertex(+0.5f,+0.5f,+0.5f);
			vertices[2]=new Vertex(-0.5f,+0.5f,+0.5f);
			vertices[3]=new Vertex(-0.5f,-0.5f,+0.5f);
			addFace(vertices,move.multiply(trans),Side.BACK,id,data);
		}
	}

	/**
	 * Adds two crossed faces
	 * @param x
	 * @param y
	 * @param z
	 * @param id
	 * @param data
	 */
	public void addCross(float x, float y, float z, int id, byte data, Transform trans)
	{
		Transform move=new Transform();
		move.translate(x, y, z);
		
		Vertex vertices[]=new Vertex[4];
		vertices[0]=new Vertex(-0.5f,-0.5f,0.0f);
		vertices[1]=new Vertex(-0.5f,+0.5f,0.0f);
		vertices[2]=new Vertex(+0.5f,+0.5f,0.0f);
		vertices[3]=new Vertex(+0.5f,-0.5f,0.0f);
		addFace(vertices,move.multiply(trans),Side.FRONT,id,data);
		
		vertices[0]=new Vertex(0.0f,-0.5f,-0.5f);
		vertices[1]=new Vertex(0.0f,+0.5f,-0.5f);
		vertices[2]=new Vertex(0.0f,+0.5f,+0.5f);
		vertices[3]=new Vertex(0.0f,-0.5f,+0.5f);
		addFace(vertices,move.multiply(trans),Side.LEFT,id,data);						
	}
	
	/**
	 * Adds a halfblock at the given location 
	 * @param x
	 * @param y
	 * @param z
	 * @param id
	 * @param data
	 * @param drawside
	 */
	public void addHalfblock(float x, float y, float z, int id, byte data, boolean [] drawside)
	{
		Transform trans=new Transform();
		trans.scale(1.0f, 0.5f, 1.0f);
		drawside[0]=true;
		addCube(x,y,z,id,data,drawside,trans);
	}
	
	/**
	 * Adds a torch
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param id
	 * @param data
	 * @param drawside
	 */
	public void addTorch(float x, float y, float z, int id, byte data, boolean [] drawside)
	{
		Transform trans=new Transform();
		trans.scale(0.2f, 0.9f, 0.2f);
		
		Transform rotate=new Transform();
		
		switch(data)
		{
		case 1:
			rotate.rotate(0, 0, -30);
			break;
		case 2:
			rotate.rotate(0, 0, 30);
			break;
		case 3:
			rotate.rotate(30, 0, 0);
			break;
		case 4:
			rotate.rotate(-30, 0, 0);
			break;			
		}
		
		for(int i=0; i<6; i++) drawside[i]=true;		
		addCube(x,y,z,id,data,drawside,rotate.multiply(trans));
	}
	
	/**
	 * Adds a liquid block at the given location
	 * This is still a work in progress! 
	 * @param x
	 * @param y
	 * @param z
	 * @param id
	 * @param data
	 * @param drawside
	 */
	public void addLiquid(float x, float y, float z, int id, byte data, boolean [] drawside)
	{
		//TODO: complete this
		Transform trans=new Transform();
		trans.scale(1.0f,data/8.0f,1.0f);
		drawside[0]=true;
		if(data>8) data-=8;
		if(data==0) data=8;
		addCube(x,y,z,id,data,drawside,trans);
	}
	
	/**
	 * Adds a snow layer at the given location
	 * @param x
	 * @param y
	 * @param z
	 * @param id
	 * @param data
	 * @param drawside
	 */
	public void addSnow(float x, float y, float z, int id, byte data, boolean [] drawside)
	{
		Transform trans=new Transform();
		trans.scale(1.0f,(1.0f+data)/8.0f,1.0f);
		drawside[0]=true;
		addCube(x,y,z,id,data,drawside,trans);
	}
	
	/**
	 * Add stairs at the given location
	 * @param x
	 * @param y
	 * @param z
	 * @param id
	 * @param data
	 * @param drawside
	 */
	public void addStairs(float x, float y, float z, int id, byte data, boolean [] drawside)
	{
		int dir=data&3;
		int up=data&4;
		
		Transform trans_bottom=new Transform();
		trans_bottom.scale(1.0f, 0.5f, 1.0f);
		Transform trans_top_h=new Transform();
		trans_top_h.scale(0.5f, 0.5f, 1.0f);
		Transform trans_top_v=new Transform();
		trans_top_v.scale(1.0f, 0.5f, 0.5f);
		
		switch(dir)
		{
		case 0:
			if(up==0)
			{
				boolean b=drawside[0]; drawside[0]=true;
				addCube(x, y-0.25f, z, id, data, drawside,trans_bottom);
				drawside[0]=b; drawside[2]=true;
				addCube(x+0.25f, y+0.25f, z, id, data, drawside, trans_top_h);
			}
			else
			{
				boolean b=drawside[1]; drawside[1]=true;
				addCube(x, y+0.25f, z, id, data, drawside, trans_bottom);
				drawside[1]=b; drawside[2]=true;
				addCube(x+0.25f, y-0.25f, z, id, data, drawside, trans_top_h);
			}
			break;
		case 1:
			if(up==0)
			{
				boolean b=drawside[0]; drawside[0]=true;
				addCube(x, y-0.25f, z, id, data, drawside, trans_bottom);
				drawside[0]=b; drawside[3]=true;
				addCube(x-0.25f, y+0.25f, z, id, data, drawside, trans_top_h);
			}
			else
			{
				boolean b=drawside[1]; drawside[1]=true;
				addCube(x, y+0.25f, z, id, data, drawside, trans_bottom);
				drawside[1]=b; drawside[3]=true;
				addCube(x-0.25f, y-0.25f, z, id, data, drawside, trans_top_h);
			}
			break;
		case 2:
			if(up==0)
			{
				boolean b=drawside[0]; drawside[0]=true;
				addCube(x, y-0.25f, z, id, data, drawside, trans_bottom);
				drawside[0]=b; drawside[4]=true;
				addCube(x, y+0.25f, z+0.25f, id, data, drawside,trans_top_v);
			}
			else
			{
				boolean b=drawside[1]; drawside[1]=true;
				addCube(x, y+0.25f, z, id, data, drawside, trans_bottom);
				drawside[1]=b; drawside[4]=true;
				addCube(x, y-0.25f, z+0.25f, id, data, drawside, trans_top_v);
			}
			break;
		case 3:
			if(up==0)
			{
				boolean b=drawside[0]; drawside[0]=true;
				addCube(x, y-0.25f, z, id, data, drawside, trans_bottom);
				drawside[0]=b; drawside[5]=true;
				addCube(x, y+0.25f, z-0.25f, id, data, drawside, trans_top_v);
			}
			else
			{
				boolean b=drawside[1]; drawside[1]=true;
				addCube(x, y+0.25f, z, id, data, drawside, trans_bottom);
				drawside[1]=b; drawside[5]=true;
				addCube(x, y-0.25f, z-0.25f, id, data, drawside, trans_top_v);
			}
			break;		
		}
	}

	/**
	 * Append this object to the OBJ file.
	 * @param out writer of the OBJ file
	 */
	public void append(PrintWriter out)
	{
		Locale l=null;
		out.println("g "+identifier);
		out.println();

		for(Vertex vertex:vertices)
		{
			out.format(l,"v %2.2f %2.2f %2.2f",(vertex.x+x_offset)*file_scale,(vertex.y+y_offset)*file_scale,(vertex.z+z_offset)*file_scale);
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
			out.format(l,"%d/-4/%d ",(-vertices_num+f.vertices[0]),normal_idx);
			out.format(l,"%d/-3/%d ",(-vertices_num+f.vertices[1]),normal_idx);
			out.format(l,"%d/-2/%d ",(-vertices_num+f.vertices[2]),normal_idx);
			out.format(l,"%d/-1/%d ",(-vertices_num+f.vertices[3]),normal_idx);
			out.println();
		}				
	}

	/**
	 * Write texture coordinates and normals. These are usually the same for all chunks.
	 * @param out writer of the OBJ file
	 */
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

	/**
	 * Converts the side to the normal index as printed by the printTexturesAndNormals method
	 * @param side side of the block
	 * @return index of the normal
	 */
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

	/**
	 * Add a face with the given vertices to the appropriate lists.
	 * Also create vertices if necessary.
	 * @param verts vertices of the face
	 * @param side side of the object
	 * @param id block id
	 */
	private void addFace(Vertex [] verts, Transform trans, Side side, int id, byte data)
	{
		Face face=new Face();
		face.side=side;
		face.mtl_id=material.getMaterialId(id, data, side);		
		face.vertices=new int[4];
		Vertex vert;
		for(int i=0; i<4; i++)
		{
			vert=trans.multiply(verts[i]);
			
			if(!vertex_map.containsKey(vert))				
			{
				vertices.add(vert);
				vertex_map.put(vert, vertices.size()-1);				
			}
			face.vertices[i]=vertex_map.get(vert);
		}

		faces.add(face);
	}

}

/**
 * Small class for describing Vertices in a sortable fashion.
 * @author danijel
 *
 */
class Vertex implements Comparable<Vertex>
{
	float x,y,z;

	/**
	 * Vertex constructor.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	Vertex(float x, float y, float z)
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

/**
 * A class to perform simple affine transformations.
 * @author danijel
 *
 */
class Transform
{
	float matrix[][];
	public Transform()
	{
		matrix=new float[4][4];
		identity();
	}
	
	private void identity()
	{
		for(int i=0; i<4; i++)
			for(int j=0; j<4; j++)
			{
				if(i==j) matrix[i][j]=1;
				else matrix[i][j]=0;
			}
	}
	
	public Transform multiply(Transform a)
	{
		Transform ret=new Transform();
		for(int i=0; i<4; i++)
			for(int j=0; j<4; j++)
			{
				ret.matrix[i][j]=0;
				for(int k=0; k<4; k++)
					ret.matrix[i][j]+=matrix[i][k]*a.matrix[k][j];
			}
		return ret;
	}
	
	public Vertex multiply(Vertex vertex)
	{
		Vertex ret=new Vertex(0,0,0);
		
		ret.x=vertex.x*matrix[0][0]+vertex.y*matrix[0][1]+vertex.z*matrix[0][2]+matrix[0][3];		
		ret.y=vertex.x*matrix[1][0]+vertex.y*matrix[1][1]+vertex.z*matrix[1][2]+matrix[1][3];
		ret.z=vertex.x*matrix[2][0]+vertex.y*matrix[2][1]+vertex.z*matrix[2][2]+matrix[2][3];
		if(matrix[3][0]+matrix[3][1]+matrix[3][2]+matrix[3][3]!=1)
		{
			System.out.println("matrix multiply error: last row doesn't add to 1");
		}
		
		return ret;
	}
	
	public void translate(float x, float y, float z)
	{
		identity();
		
		matrix[0][3]=x;
		matrix[1][3]=y;
		matrix[2][3]=z;				
	}
	
	public void scale(float x, float y, float z)
	{
		identity();
		
		matrix[0][0]=x;
		matrix[1][1]=y;
		matrix[2][2]=z;				
	}
	
	public void rotate(float a, float b, float g)
	{
		//convert to rad
		a=(float) (a*Math.PI/180.0);
		b=(float) (b*Math.PI/180.0);
		g=(float) (g*Math.PI/180.0);
	
		identity();
		Transform ret;
		Transform trans=new Transform();
		
		trans.matrix[1][1]=(float) Math.cos(a);
		trans.matrix[1][2]=(float) -Math.sin(a);
		trans.matrix[2][1]=(float) Math.sin(a);
		trans.matrix[2][2]=(float) Math.cos(a);
		
		ret=multiply(trans);
		matrix=ret.matrix;
		
		trans.identity();
		trans.matrix[0][0]=(float) Math.cos(b);
		trans.matrix[0][2]=(float) -Math.sin(b);
		trans.matrix[2][0]=(float) Math.sin(b);
		trans.matrix[2][2]=(float) Math.cos(b);
		
		ret=multiply(trans);
		matrix=ret.matrix;
		
		trans.identity();
		trans.matrix[0][0]=(float) Math.cos(g);
		trans.matrix[0][1]=(float) -Math.sin(g);
		trans.matrix[1][0]=(float) Math.sin(g);
		trans.matrix[1][1]=(float) Math.cos(g);
		
		ret=multiply(trans);
		matrix=ret.matrix;
	}
}