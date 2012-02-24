package org.jmc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Vector;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class AnvilRegion {

	File region_file;
	private ByteBuffer offset,timestamp;

	public AnvilRegion(File file) throws IOException
	{
		if(!file.exists())
			throw new FileNotFoundException(file.getAbsolutePath());

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

	public static AnvilRegion findRegion(File saveFolder, int chunk_x, int chunk_z) throws IOException
	{
		int rx,rz;

		//equivalent to dividing by 32
		rx = chunk_x >> 5;
		rz = chunk_z >> 5;

		File file= new File(saveFolder.getAbsolutePath()+"/region/r."+rx+"."+rz+".mca");

		return new AnvilRegion(file);
	}

	public static Vector<AnvilRegion> loadAllRegions(File saveFolder) throws IOException
	{
		Vector<AnvilRegion> ret=new Vector<AnvilRegion>();

		File dir=new File(saveFolder.getAbsoluteFile()+"/region");
		if(!dir.exists() || !dir.isDirectory())
			throw new FileNotFoundException(dir.getAbsolutePath());

		File[] files=dir.listFiles();
		for(File f:files)
		{
			String name=f.getName();
			if(name.matches("r.[0-9-].[0-9-].mca"))
			{
				ret.add(new AnvilRegion(f));
			}
		}

		return ret;
	}

	public Chunk getChunk(int x, int z) throws Exception
	{
		int loc = 4 * ( (x % 32) + (z % 32) * 32 );
		int off = offset.getInt(loc);
		int sec = off >> 8;
		int len = off & 0xff;
		int ts = timestamp.getInt(loc);
		
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
            return new Chunk(is);
		}
		else if(compression_type==2)//Inflate
		{
			byte[] buf = new byte[len - 1];
            raf.read(buf);
            raf.close();
            InputStream is=new InflaterInputStream(new ByteArrayInputStream(buf));
            return new Chunk(is);
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

}
