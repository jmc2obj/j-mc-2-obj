/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jmc.Chunk.Blocks;
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
	 * Collection of IDs of transparent blocks.
	 * These blocks allow us to see what's behind them.
	 * Non-transparent blocks make the sides of neighboring blocks not render.
	 */
	private Set<Short> transparent_blocks;
	/**
	 * A map of mesh types for given block IDs.
	 */
	private Map<Short, MeshType> mesh_types;
	
	private Colors colors;
	
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
		transparent_blocks=new TreeSet<Short>();
		transparent_blocks.add((short) -1);
		transparent_blocks.add((short) 0);
		transparent_blocks.add((short) 8);
		transparent_blocks.add((short) 9);
		transparent_blocks.add((short) 20);
		transparent_blocks.add((short) 26);
		transparent_blocks.add((short) 27);
		transparent_blocks.add((short) 30);
		transparent_blocks.add((short) 31);
		transparent_blocks.add((short) 32);
		transparent_blocks.add((short) 34);
		transparent_blocks.add((short) 37);
		transparent_blocks.add((short) 38);
		transparent_blocks.add((short) 39);
		transparent_blocks.add((short) 40);
		transparent_blocks.add((short) 50);
		transparent_blocks.add((short) 51);
		transparent_blocks.add((short) 52);
		transparent_blocks.add((short) 53);
		transparent_blocks.add((short) 55);
		transparent_blocks.add((short) 59);
		transparent_blocks.add((short) 63);

		mesh_types=new TreeMap<Short, OBJFile.MeshType>();
		mesh_types.put((short) 1, MeshType.BLOCK);
		mesh_types.put((short) 2, MeshType.BLOCK);
		mesh_types.put((short) 3, MeshType.BLOCK);
		mesh_types.put((short) 4, MeshType.BLOCK);
		mesh_types.put((short) 5, MeshType.BLOCK);
		mesh_types.put((short) 7, MeshType.BLOCK);
		mesh_types.put((short) 8, MeshType.LIQUID);//WATER F
		mesh_types.put((short) 9, MeshType.BLOCK);//WATER S
		mesh_types.put((short) 10, MeshType.LIQUID);//LAVA F
		mesh_types.put((short) 11, MeshType.BLOCK);//LAVA S
		mesh_types.put((short) 12, MeshType.BLOCK);		
		mesh_types.put((short) 13, MeshType.BLOCK);
		mesh_types.put((short) 14, MeshType.BLOCK);
		mesh_types.put((short) 15, MeshType.BLOCK);
		mesh_types.put((short) 16, MeshType.BLOCK);
		mesh_types.put((short) 17, MeshType.BLOCK);
		mesh_types.put((short) 18, MeshType.BLOCK);//LEAVES(alpha)
		mesh_types.put((short) 19, MeshType.BLOCK);
		mesh_types.put((short) 20, MeshType.BLOCK);//GLASS(alpha)
		mesh_types.put((short) 21, MeshType.BLOCK);
		mesh_types.put((short) 22, MeshType.BLOCK);
		mesh_types.put((short) 23, MeshType.BLOCK);
		mesh_types.put((short) 24, MeshType.BLOCK);
		mesh_types.put((short) 25, MeshType.BLOCK);
		mesh_types.put((short) 30, MeshType.CROSS);//COBWEB
		mesh_types.put((short) 31, MeshType.CROSS);//TALL GRASS
		mesh_types.put((short) 32, MeshType.CROSS);//DEAD BUSH
		mesh_types.put((short) 35, MeshType.BLOCK);//WOOL
		mesh_types.put((short) 37, MeshType.CROSS);//FLOWER 1
		mesh_types.put((short) 38, MeshType.CROSS);//FLOWER 2
		mesh_types.put((short) 39, MeshType.CROSS);//MUSHROOM 1
		mesh_types.put((short) 40, MeshType.CROSS);//MUSHROOM 2		
		mesh_types.put((short) 41, MeshType.BLOCK);
		mesh_types.put((short) 42, MeshType.BLOCK);
		mesh_types.put((short) 43, MeshType.BLOCK);
		mesh_types.put((short) 44, MeshType.HALFBLOCK);//SLABS
		mesh_types.put((short) 45, MeshType.BLOCK);
		mesh_types.put((short) 46, MeshType.BLOCK);
		mesh_types.put((short) 47, MeshType.BLOCK);
		mesh_types.put((short) 48, MeshType.BLOCK);
		mesh_types.put((short) 49, MeshType.BLOCK);
		mesh_types.put((short) 50, MeshType.TORCH);//TORCH
		mesh_types.put((short) 51, MeshType.CROSS);//FIRE
		mesh_types.put((short) 52, MeshType.BLOCK);//SPAWNER(alpha)
		mesh_types.put((short) 53, MeshType.STAIRS);//WOOD STAIRS
		mesh_types.put((short) 54, MeshType.BLOCK);
		mesh_types.put((short) 56, MeshType.BLOCK);
		mesh_types.put((short) 57, MeshType.BLOCK);
		mesh_types.put((short) 58, MeshType.BLOCK);
		mesh_types.put((short) 59, MeshType.CROSS);//WHEAT
		mesh_types.put((short) 60, MeshType.BLOCK);
		mesh_types.put((short) 61, MeshType.BLOCK);
		mesh_types.put((short) 62, MeshType.BLOCK);
		mesh_types.put((short) 67, MeshType.STAIRS);//cobble stairs
		mesh_types.put((short) 73, MeshType.BLOCK);
		mesh_types.put((short) 74, MeshType.BLOCK);
		mesh_types.put((short) 75, MeshType.TORCH);//REDSTONE TORCH OFF
		mesh_types.put((short) 76, MeshType.TORCH);//REDSTONE TORCH ON
		mesh_types.put((short) 79, MeshType.BLOCK);//ICE(alpha?)
		mesh_types.put((short) 80, MeshType.BLOCK);
		mesh_types.put((short) 81, MeshType.BLOCK);
		mesh_types.put((short) 82, MeshType.BLOCK);
		mesh_types.put((short) 83, MeshType.CROSS);//SUGAR CANE
		mesh_types.put((short) 84, MeshType.BLOCK);
		mesh_types.put((short) 86, MeshType.BLOCK);
		mesh_types.put((short) 87, MeshType.BLOCK);
		mesh_types.put((short) 88, MeshType.BLOCK);
		mesh_types.put((short) 89, MeshType.BLOCK);
		mesh_types.put((short) 91, MeshType.BLOCK);
		mesh_types.put((short) 95, MeshType.BLOCK);
		mesh_types.put((short) 97, MeshType.BLOCK);
		mesh_types.put((short) 98, MeshType.BLOCK);
		mesh_types.put((short) 103, MeshType.BLOCK);
		mesh_types.put((short) 104, MeshType.CROSS);//PUMPKIN  STALK
		mesh_types.put((short) 105, MeshType.CROSS);//MELON STALK
		mesh_types.put((short) 106, MeshType.CROSS);//VINE
		mesh_types.put((short) 108, MeshType.STAIRS);//brick stairs
		mesh_types.put((short) 109, MeshType.STAIRS);//stone brick stairs
		mesh_types.put((short) 110, MeshType.BLOCK);
		mesh_types.put((short) 112, MeshType.BLOCK);
		mesh_types.put((short) 114, MeshType.STAIRS);//nether brick stairs
		mesh_types.put((short) 115, MeshType.CROSS);//NETHERWART
		mesh_types.put((short) 121, MeshType.BLOCK);
		mesh_types.put((short) 123, MeshType.BLOCK);
		mesh_types.put((short) 124, MeshType.BLOCK);		

		
		identifier=ident;
		material=mtl;
		colors = MainWindow.settings.minecraft_colors;
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
	public void addCube(float x, float y, float z, short id, byte data, boolean [] drawside, Transform trans)
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
	public void addCross(float x, float y, float z, short id, byte data, Transform trans)
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
	public void addHalfblock(float x, float y, float z, short id, byte data, boolean [] drawside)
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
	public void addTorch(float x, float y, float z, short id, byte data, boolean [] drawside)
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
	public void addLiquid(float x, float y, float z, short id, byte data, boolean [] drawside)
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
	public void addSnow(float x, float y, float z, short id, byte data, boolean [] drawside)
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
	public void addStairs(float x, float y, float z, short id, byte data, boolean [] drawside)
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
	private void addFace(Vertex [] verts, Transform trans, Side side, short id, byte data)
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

	private final short getValue(boolean is_anvil, short [] array, int x, int y, int z, int ymax)
	{
		if(x<0 || x>15 || y<0 || y>=ymax || z<0 || z>15) return -1;
		if(is_anvil)
			return array[x + (z * 16) + (y * 16) * 16];
		else
			return array[y + (z * 128) + (x * 128) * 16];
	}
	
	
	private final byte getValue(boolean is_anvil, byte [] array, int x, int y, int z, int ymax)
	{
		if(x<0 || x>15 || y<0 || y>=ymax || z<0 || z>15) return -1;
		if(is_anvil)
			return array[x + (z * 16) + (y * 16) * 16];
		else
			return array[y + (z * 128) + (x * 128) * 16];
	}

	
	public void addChunk(Chunk chunk, Rectangle bounds, int ymin, int ymax)
	{
		int pos_x=chunk.getPosX();
		int pos_z=chunk.getPosZ();
		int xmin=bounds.x-pos_x*16;
		int zmin=bounds.y-pos_z*16;
		int xmax=bounds.x+bounds.width-pos_x*16;
		int zmax=bounds.y+bounds.height-pos_z*16;

		if(xmin>15 || zmin>15 || xmax<0 || zmax<0) return;

		if(xmin<0) xmin=0;
		if(zmin<0) zmin=0;
		if(xmax>15) xmax=15;
		if(zmax>15) zmax=15;
		
		boolean is_anvil=chunk.isAnvil();

		boolean drawside[]=new boolean[6];

		short BlockID;
		byte BlockData;
		Blocks bd=chunk.getBlocks();
		short blocks[]=bd.id;
		byte data[]=bd.data;

		int ymax_f;
		if(is_anvil)
			ymax_f=blocks.length/(16*16);
		else 
			ymax_f=128;
		
		if(ymax>ymax_f)
			ymax=ymax_f;

		int x,y,z,rx,rz;
		for(z = zmin; z <= zmax; z++)
		{
			for(x = xmin; x <= xmax; x++)
			{
				for(y = ymin; y < ymax; y++)
				{						
					BlockID=getValue(is_anvil,blocks, x, y, z, ymax);
					BlockData=getValue(is_anvil,data, x, y, z, ymax);

					if(BlockID==0) continue;

					if(y==ymax-1 || isDrawable(BlockID,getValue(is_anvil,blocks,x,y+1,z,ymax)))
						drawside[0]=true; else drawside[0]=false;
					if(y==ymin || isDrawable(BlockID,getValue(is_anvil,blocks,x,y-1,z,ymax)))
						drawside[1]=true; else drawside[1]=false;
					if(x==xmin || isDrawable(BlockID,getValue(is_anvil,blocks,x-1,y,z,ymax)))
						drawside[2]=true; else drawside[2]=false;
					if(x==xmax || isDrawable(BlockID,getValue(is_anvil,blocks,x+1,y,z,ymax)))
						drawside[3]=true; else drawside[3]=false;
					if(z==zmin || isDrawable(BlockID,getValue(is_anvil,blocks,x,y,z-1,ymax)))
						drawside[4]=true; else drawside[4]=false;
					if(z==zmax || isDrawable(BlockID,getValue(is_anvil,blocks,x,y,z+1,ymax)))
						drawside[5]=true; else drawside[5]=false;

					rx=x+pos_x*16;
					rz=z+pos_z*16;
					
					Transform identity=new Transform();
					MeshType mt=mesh_types.get(BlockID);
					if(mt!=null)
					{
						switch(mt)
						{
						case BLOCK:
							addCube(rx, y, rz, BlockID, BlockData, drawside,identity);
							break;
						case STAIRS:
							addStairs(rx, y, rz, BlockID, BlockData, drawside);
							break;
						case HALFBLOCK:
							addHalfblock(rx, y, rz, BlockID, BlockData, drawside);
							break;
						case LIQUID:
							addLiquid(rx, y, rz, BlockID, BlockData, drawside);
							break;
						case SNOW:
							addSnow(rx, y,rz, BlockID, BlockData, drawside);
						case CROSS:
							addCross(rx, y, rz, BlockID, BlockData,identity);
							break;
						case TORCH:
							addTorch(rx, y, rz, BlockID, BlockData, drawside);
							break;
						default:
						}
					}
				}									
			}
		}		
	}

	public void addChunkBuffer(ChunkDataBuffer chunk)
	{
		int x,y,z;
		int xmin,xmax,ymin,ymax,zmin,zmax;
		Rectangle xy,xz;
		xy=chunk.getXYBoundaries();
		xz=chunk.getXZBoundaries();
		xmin=xy.x;
		xmax=xmin+xy.width;
		ymin=xy.y;
		ymax=ymin+xy.height;
		zmin=xz.y;
		zmax=zmin+xz.height;
		
		short BlockID;
		byte BlockData;
		
		boolean drawside[]=new boolean[6];
		
		for(z = zmin; z < zmax; z++)
		{
			for(x = xmin; x < xmax; x++)
			{
				for(y = ymin; y < ymax; y++)
				{						
					BlockID=chunk.getBlockID(x, y, z);
					BlockData=chunk.getBlockData(x, y, z);

					if(BlockID==0) continue;

					if(y==ymax-1 || isDrawable(BlockID,chunk.getBlockID(x,y+1,z)))
						drawside[0]=true; else drawside[0]=false;
					if(y==ymin || isDrawable(BlockID,chunk.getBlockID(x,y-1,z)))
						drawside[1]=true; else drawside[1]=false;
					if(x==xmin || isDrawable(BlockID,chunk.getBlockID(x-1,y,z)))
						drawside[2]=true; else drawside[2]=false;
					if(x==xmax-1 || isDrawable(BlockID,chunk.getBlockID(x+1,y,z)))
						drawside[3]=true; else drawside[3]=false;
					if(z==zmin || isDrawable(BlockID,chunk.getBlockID(x,y,z-1)))
						drawside[4]=true; else drawside[4]=false;
					if(z==zmax-1 || isDrawable(BlockID,chunk.getBlockID(x,y,z+1)))
						drawside[5]=true; else drawside[5]=false;
					
					Transform identity=new Transform();
					MeshType mt=mesh_types.get(BlockID);
					if(mt!=null)
					{
						switch(mt)
						{
						case BLOCK:
							addCube(x, y, z, BlockID, BlockData, drawside,identity);
							break;
						case STAIRS:
							addStairs(x, y, z, BlockID, BlockData, drawside);
							break;
						case HALFBLOCK:
							addHalfblock(x, y, z, BlockID, BlockData, drawside);
							break;
						case LIQUID:
							addLiquid(x, y, z, BlockID, BlockData, drawside);
							break;
						case SNOW:
							addSnow(x, y,z, BlockID, BlockData, drawside);
						case CROSS:
							addCross(x, y, z, BlockID, BlockData,identity);
							break;
						case TORCH:
							addTorch(x, y, z, BlockID, BlockData, drawside);
							break;
						default:
						}
					}
				}									
			}
		}		
	}

	
	/**
	 * Private method used for checking if the given block ID is drawable 
	 * from the point of view of of a neighboring block.
	 * @param block_id block id of block being checked
	 * @param neighbour_id block id of its neighbor
	 * @return is it drawable
	 */
	private boolean isDrawable(short block_id, short neighbour_id)
	{
		Color nc=colors.getColor(neighbour_id,(byte) 0);
		MeshType nm=mesh_types.get(neighbour_id);

		if(block_id==8 && nc!=null && nm==MeshType.BLOCK) return false;
		if(block_id==9 && nc!=null && nm==MeshType.BLOCK) return false;			

		if(transparent_blocks.contains(neighbour_id) || nc==null || nm!=MeshType.BLOCK)
			return true;

		//TODO: this is linked to not drawing unknown chunks - remove when removing other comment
		if(colors.getColor(block_id,(byte) 0)==null)
			return true;

		return false;
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