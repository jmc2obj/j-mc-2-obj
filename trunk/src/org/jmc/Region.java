/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class Region implements Iterable<Chunk> {

	private File region_file;
	private ByteBuffer offset,timestamp;
	boolean is_anvil;

	public Region(File file) throws IOException
	{
		if(!file.exists())
			throw new FileNotFoundException(file.getAbsolutePath());
		
		if(file.getName().endsWith("mcr"))
			is_anvil=false;
		else if(file.getName().endsWith("mca"))
			is_anvil=true;
		else
			throw new IOException("Unknown file extension! Only .mcr or .mca supported!");

		region_file=file;

		FileInputStream fis=new FileInputStream(region_file);

		byte [] offset_array=new byte[4096];
		fis.read(offset_array);
		offset=ByteBuffer.wrap(offset_array);

		byte [] timestamp_array=new byte[4096];
		fis.read(timestamp_array);
		timestamp=ByteBuffer.wrap(timestamp_array);

		fis.close();

	}	
	
	public static Region findRegion(File saveFolder, int chunk_x, int chunk_z) throws IOException
	{
		int rx,rz;

		//equivalent to dividing by 32
		rx = chunk_x >> 5;
		rz = chunk_z >> 5;

		File file= new File(saveFolder.getAbsolutePath()+"/region/r."+rx+"."+rz+".mca");
		
		if(!file.exists())
			file= new File(saveFolder.getAbsolutePath()+"/region/r."+rx+"."+rz+".mcr");

		return new Region(file);
	}
	
	public static Vector<Region> loadAllRegions(File saveFolder) throws IOException
	{
		Vector<Region> ret=new Vector<Region>();

		File dir=new File(saveFolder.getAbsoluteFile()+"/region");
		if(!dir.exists() || !dir.isDirectory())
			throw new FileNotFoundException(dir.getAbsolutePath());

		File[] files=dir.listFiles();
		for(File f:files)
		{
			String name=f.getName();
			if(name.matches("r\\.[0-9[-]]+\\.[0-9[-]]+\\.mca"))
			{
				ret.add(new Region(f));				
			}
		}
		
		if(ret.size()==0)
		{
			for(File f:files)
			{
				String name=f.getName();
				if(name.matches("r\\.[0-9[-]]+\\.[0-9[-]]+\\.mcr"))
				{
					ret.add(new Region(f));				
				}
			}
		}

		return ret;
	}
	
	public Chunk getChunk(int x, int z) throws Exception
	{			
		int cx=x%32;
		int cz=z%32;
		if(cz<0) 
			cz=32+cz;
		if(cx<0) 
			cx=32+cx;
		int loc = cx + cz * 32;		
		Chunk chunk = getChunk(loc);
		
		if(chunk!=null && (chunk.getPosX()!=x || chunk.getPosZ()!=z))
		{
			throw new Exception("Chunk coord don't match!");
		}
		 
		return chunk;
	}


	public Chunk getChunk(int idx) throws Exception
	{
		int off = offset.getInt(idx*4);
		int sec = off >> 8;
		int len = off & 0xff;
		int ts = timestamp.getInt(idx*4);

		if(sec<2)
			return null;

		RandomAccessFile raf=new RandomAccessFile(region_file, "r");
		raf.seek(sec*4096);

		len=raf.readInt();
		int compression_type=raf.readByte();
		if(compression_type==1) //GZIP
		{
			byte[] buf = new byte[len - 1];
			raf.read(buf);
			raf.close();
			InputStream is=new GZIPInputStream(new ByteArrayInputStream(buf));
			return new Chunk(is,is_anvil);
		}
		else if(compression_type==2)//Inflate
		{
			byte[] buf = new byte[len - 1];
			raf.read(buf);
			raf.close();
			InputStream is=new InflaterInputStream(new ByteArrayInputStream(buf));
			return new Chunk(is,is_anvil);
		}
		else
			throw new Exception("Wrong compression type!");

	}

	public void printAvailableChunks()
	{
		int i=0;
		for(int z=0; z<32; z++)
			for(int x=0; x<32; x++,i++)
			{
				int off=offset.getInt(4*i);
				if(off!=0)
				{
					System.out.println("{"+x+","+z+"} -> "+(off>>8)+"/"+(off&0xff));
				}
			}
	}

	class ChunkIterator implements Iterator<Chunk>
	{

		Region region;
		int pos;

		public ChunkIterator(Region region) {
			this.region=region;
			pos=0;
		}

		@Override
		public boolean hasNext() {
			for(int x=pos+1;x<1024;x++)
				if(region.offset.getInt(x*4)!=0)
					return true;

			return false;
		}

		@Override
		public Chunk next() {

			Chunk ret=null;

			while(offset.getInt(pos*4)==0)
			{				
				pos++;
			}

			try {
				ret=getChunk(pos);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			pos++;

			return ret;
		}

		@Override
		public void remove() {

			//TODO: not yet supported
			
		}

	}

	@Override
	public Iterator<Chunk> iterator() 
	{
		Iterator<Chunk> ret=new ChunkIterator(this);
		return ret;
	}
}
