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

import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.util.Log;


/**
 * Class for reading simple OBJ meshes.
 * @author danijel
 *
 */
public class OBJInputFile extends OBJFileBase
{

	private static class Group
	{
		public Group()
		{
			faces=new LinkedList<Face>();
		}
		public List<Face> faces;
	}

	
	Map<String,Group> objects;

	
	public OBJInputFile() 
	{
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

		int vertex_count=1;
		int uv_count=1;
		int norm_count=1;

		Group group=new Group();
		objects.put(objfile.getName(),group);
		String material=null;
		String line;
		int line_count=0;
		while((line=in.readLine())!=null)
		{
			line_count++;

			if(line.length()==0) continue;

			// we ignore these lines
			if(line.startsWith("#")) continue;
			if(line.startsWith("mtllib ")) continue;
			if(line.startsWith("s ")) continue;

			if(line.startsWith("o ") || line.startsWith("g "))
			{
				group=new Group();
				objects.put(objfile.getName()+"#"+line.substring(2).trim(), group);
				continue;
			}

			if(line.startsWith("usemtl "))
			{
				material=line.substring(7);
				continue;
			}

			if(line.startsWith("v "))
			{
				try {
					float x,y,z;				
					Scanner scanner=new Scanner(line.substring(2));
					scanner.useLocale(Locale.ROOT);
					x=scanner.nextFloat();
					y=scanner.nextFloat();
					z=scanner.nextFloat();
					Vertex v=new Vertex(x, y, z);
					
					vertices.add(v);
					vertex_count++;
				}
				catch (Exception e) {
					Log.info("ERROR vertex format exception in file "+objfile.getName()+"["+line_count+"]: "+e);
				}
				continue;
			}

			if(line.startsWith("vt "))
			{
				try {
					UV uv = new UV(0,0);
					String[] parts = line.split("\\s+");
					if (parts.length > 1) uv.u = Float.parseFloat(parts[1]);
					if (parts.length > 2) uv.v = Float.parseFloat(parts[2]);
					
					texCoords.add(uv);
					uv_count++;
				}
				catch (Exception e) {
					Log.info("ERROR texture format exception in file "+objfile.getName()+"["+line_count+"]: "+e);
				}
				continue;
			}

			if(line.startsWith("vn "))
			{
				try {
					Vertex norm = new Vertex(0,0,0);
					String[] parts = line.split("\\s+");
					if (parts.length > 1) norm.x = Float.parseFloat(parts[1]);
					if (parts.length > 2) norm.y = Float.parseFloat(parts[2]);
					if (parts.length > 3) norm.z = Float.parseFloat(parts[3]);

					normals.add(norm);
					norm_count++;
				}
				catch (Exception e) {
					Log.info("ERROR normal format exception in file "+objfile.getName()+"["+line_count+"]: "+e);
				}
				continue;
			}

			if(line.startsWith("f "))
			{
				String [] vs=line.substring(2).split("\\s+");	
				if(vs.length < 3)
				{
					Log.info("ERROR wrong number of vertices in face in file "+objfile.getName()+"["+line_count+"]");
					continue;
				}
				Face f=new Face(vs.length);
				boolean has_uv=false;
				boolean has_norm=false;

				for(int i=0; i<vs.length; i++)
				{
					try {
						int v,n,t;
						
						String [] vt=vs[i].split("/+");
						if(vt.length==1)
						{
							v=Integer.parseInt(vs[i]);
							if(v<0) v=vertex_count-v;
							f.vertices[i]=v;
						}
						else if(vt.length==2)
						{							
							if(vs[i].contains("//"))
							{
								v=Integer.parseInt(vt[0]);
								n=Integer.parseInt(vt[1]);
								if(v<0) v=vertex_count+v+1;
								if(n<0) n=norm_count+n+1;
								f.vertices[i]=v;						
								f.normals[i]=n;
								has_norm=true;
							}
							else
							{
								v=Integer.parseInt(vt[0]);
								t=Integer.parseInt(vt[1]);
								if(v<0) v=vertex_count+v+1;
								if(t<0) t=uv_count+t+1;
								f.vertices[i]=v;
								f.uv[i]=t;
								has_uv=true;
							}
						}
						else if(vt.length==3)
						{
							v=Integer.parseInt(vt[0]);
							t=Integer.parseInt(vt[1]);
							n=Integer.parseInt(vt[2]);							
							if(v<0) v=vertex_count+v+1;
							if(t<0) t=uv_count+t+1;
							if(n<0) n=norm_count+n+1;
							f.vertices[i]=v;
							f.uv[i]=t;
							f.normals[i]=n;
							has_uv=true;
							has_norm=true;
						}
						else 
							Log.info("ERROR unknown vertex format in file "+objfile.getName()+"["+line_count+"]");
					} catch (Exception e) {
						Log.info("ERROR unknown vertex format in file "+objfile.getName()+"["+line_count+"]: "+e);
					}
				}
				if(!has_uv) f.uv=null;
				if(!has_norm) f.normals=null;
				f.mtl=material;
				
				group.faces.add(f);
				continue;
			}

			Log.info("ERROR unknown line in OBJ file "+objfile.getName()+"["+line_count+"]: "+line);
		}
	}

	/**
	 * Method to add a named object to the output model. 
	 * @param objname
	 * @param trans
	 * @param out
	 */
	public void addObject(String objname, Transform trans, OBJOutputFile out)
	{
		if(!objects.containsKey(objname))
		{
			Log.info("ERROR mesh object "+objname+" doesn't exist!");
			return;
		}

		Group group=objects.get(objname);

		for(Face f:group.faces)
		{
			int n = f.vertices.length;

			Vertex[] v = new Vertex[n];
			UV[] uv = f.uv == null ? null : new UV[n];
			Vertex[] norm = f.normals == null ? null : new Vertex[n];

			for(int i=0; i<n; i++)
			{
				v[i] = new Vertex(vertices.get(f.vertices[i]-1));
				if (uv != null)
					uv[i] = new UV(texCoords.get(f.uv[i]-1));
				if (norm != null)
					norm[i] = new Vertex(normals.get(f.normals[i]-1));		
			}

			out.addFace(v, norm, uv, trans, f.mtl);
		}
	}
}
