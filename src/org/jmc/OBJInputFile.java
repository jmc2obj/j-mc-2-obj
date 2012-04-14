package org.jmc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Vector;

import org.jmc.geom.Face;
import org.jmc.geom.MaterialMap;
import org.jmc.geom.Side;
import org.jmc.geom.Transform;
import org.jmc.geom.UVNormMap;
import org.jmc.geom.Vertex;
import org.jmc.util.Log;

/**
 * Class for reading simple OBJ meshes.
 * @author danijel
 *
 */
public class OBJInputFile {

	Vector<Vertex> vertices;
	Map<Vertex, Integer> vertex_map;
	private int vertex_counter;

	class Group
	{
		public Group()
		{
			faces=new LinkedList<Face>();
		}
		public List<Face> faces;
	}

	Map<String,Group> objects;

	MaterialMap material_map;

	UVNormMap uvnorm_map;

	public OBJInputFile() 
	{
		vertices=new Vector<Vertex>();
		vertex_map=new TreeMap<Vertex, Integer>();
		vertex_counter=1;
		material_map=new MaterialMap();
		uvnorm_map=new UVNormMap();
		objects=new HashMap<String, OBJInputFile.Group>();
	}

	/**
	 * Loads a file into the buffer.
	 * @param objfile
	 * @throws IOException 
	 */
	public void loadFile(File objfile) throws IOException
	{
		BufferedReader in=new BufferedReader(new FileReader(objfile));

		Map<Integer,Integer> vert_remap=new TreeMap<Integer, Integer>();
		Map<Integer,Integer> uv_remap=new TreeMap<Integer, Integer>();
		Map<Integer,Integer> norm_remap=new TreeMap<Integer, Integer>();
		int lvertex_count=1;
		int uv_count=1;
		int norm_count=1;

		Group group=new Group();
		objects.put(objfile.getName(),group);
		int material_id=-1;
		String line;
		int line_count=0;
		while((line=in.readLine())!=null)
		{
			line_count++;

			if(line.length()==0) continue;

			//we ignore these line
			if(line.startsWith("#")) continue;
			if(line.startsWith("mtllib ")) continue;
			if(line.startsWith("s ")) continue;

			if(line.startsWith("o ") || line.startsWith("g "))
			{
				group=new Group();
				objects.put(objfile.getName()+"#"+line.substring(2),group);
				continue;
			}

			if(line.startsWith("usemtl "))
			{
				material_id=material_map.getMaterialID(line.substring(7));
				continue;
			}

			if(line.startsWith("v "))
			{
				try{
					float x,y,z;				
					Scanner scanner=new Scanner(line.substring(2));
					scanner.useLocale(Locale.ROOT);
					x=scanner.nextFloat();
					y=scanner.nextFloat();
					z=scanner.nextFloat();
					Vertex v=new Vertex(x, y, z);
					vertices.add(v);
					vertex_map.put(v, vertex_counter);
					vert_remap.put(lvertex_count, vertex_counter);
					vertex_counter++;
					lvertex_count++;
				}
				catch (Exception e) {
					Log.info("ERROR vertex format exception in file "+objfile.getName()+"["+line_count+"]: "+e);
				}
				continue;
			}

			if(line.startsWith("f "))
			{
				Face f=new Face();				
				String [] vs=line.substring(2).split("\\s+");	
				if(vs.length!=4)
				{
					Log.info("ERROR wrong number of vertices in face in file "+objfile.getName()+"["+line_count+"]");
					continue;
				}
				int v,n,t;
				uvnorm_map.calculate(Side.FRONT, f);//set defaults
				for(int i=0; i<4; i++)
				{
					try{
						String [] vt=vs[i].split("/+");
						if(vt.length==1)
						{
							v=Integer.parseInt(vs[i]);
							if(v<0) v=lvertex_count-v;
							f.vertices[i]=vert_remap.get(v);
						}
						else if(vt.length==2)
						{							
							if(vs[i].contains("//"))
							{
								v=Integer.parseInt(vt[0]);
								n=Integer.parseInt(vt[1]);
								if(v<0) v=lvertex_count-v;
								if(n<0) n=norm_count-n;
								f.vertices[i]=vert_remap.get(v);						
								f.normals[i]=norm_remap.get(n);
							}
							else
							{
								v=Integer.parseInt(vt[0]);
								t=Integer.parseInt(vt[1]);
								if(v<0) v=lvertex_count-v;
								if(t<0) t=uv_count-t;
								f.vertices[i]=vert_remap.get(v);
								f.uv[i]=uv_remap.get(t);
							}
						}
						else if(vt.length==3)
						{
							v=Integer.parseInt(vt[0]);
							t=Integer.parseInt(vt[1]);
							n=Integer.parseInt(vt[2]);							
							if(v<0) v=lvertex_count-v;
							if(t<0) t=uv_count-t;
							if(n<0) n=norm_count-n;
							f.vertices[i]=vert_remap.get(v);
							f.uv[i]=uv_remap.get(t);
							f.normals[i]=norm_remap.get(n);
						}
						else 
							Log.info("ERROR unknown vertex format in file "+objfile.getName()+"["+line_count+"]");

						f.mtl=material_id;
						group.faces.add(f);

					} catch (Exception e) {
						Log.info("ERROR unknown vertex format in file "+objfile.getName()+"["+line_count+"]: "+e);
					}
				}
				continue;
			}

			if(line.startsWith("vt " ))
			{
				uv_remap.put(uv_count, uvnorm_map.getUVId(line));
				uv_count++;
				continue;
			}

			if(line.startsWith("vn " ))
			{
				norm_remap.put(norm_count, uvnorm_map.getNormId(line));
				norm_count++;
				continue;
			}

			Log.info("ERROR unknown line in OBJ file "+objfile.getName()+"["+line_count+"]: "+line);
		}		
	}

	/**
	 * Method to add a named object to the output model. 
	 * @param objname
	 * @param x
	 * @param y
	 * @param z
	 * @param trans
	 * @param out
	 */
	public void addObject(String objname, int x, int y, int z, Transform trans, OBJOutputFile out)
	{
		if(!objects.containsKey(objname))
		{
			Log.info("ERROR mesh object "+objname+" doesn't exist!");
			return;
		}

		Group group=objects.get(objname);

		for(Face f:group.faces)
		{
			String mtl=material_map.getMaterialName(f.mtl);
			Vertex[] v=new Vertex[4];
			String [] uv=new String[4];
			String [] norm=new String[4];

			for(int i=0; i<4; i++)
			{
				v[i]=new Vertex(vertices.get(f.vertices[i]-1));

				if(trans!=null)
					v[i]=trans.multiply(v[i]);

				v[i].x+=x;
				v[i].y+=y;
				v[i].z+=z;

				uv[i]=uvnorm_map.getUV(f.uv[i]);
				norm[i]=uvnorm_map.getNorm(f.normals[i]);		
			}

			out.addFace(v, uv, norm, mtl);
		}
	}
}
