/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.jmc.registry.NamespaceID;

import javax.annotation.CheckForNull;

/**
 * Region file class.
 * This file contains individual chunks. It can be either the old MCRegion format or
 * the new Anvil format.
 * @author danijel
 *
 */
public class Region {

	/**
	 * Path to the file.
	 */
	private final File region_file;
	/**
	 * Path to the entities file.
	 */
	private final File region_entity_file;
	/**
	 * Buffer of offsets of individual chunks.
	 */
	private final ByteBuffer offset;
	/**
	 * Buffer of offsets of individual in the entiy file chunks.
	 */
	private ByteBuffer entity_offset;
	/**
	 * Is the file in anvil or old mcregion format.
	 */
	boolean is_anvil;

	/**
	 * Main constructor.
	 * @param file path to file
	 * @throws IOException if error occurs
	 */
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
		
		region_entity_file = new File(file.getParentFile().getParent()+"/entities", file.getName());

		FileInputStream fis=new FileInputStream(region_file);

		byte [] offset_array=new byte[4096];
		fis.read(offset_array);
		offset=ByteBuffer.wrap(offset_array);
		
		fis.close();
		if (is_anvil && region_entity_file.exists()) {
			fis=new FileInputStream(region_entity_file);
			
			offset_array=new byte[4096];
			fis.read(offset_array);
			entity_offset=ByteBuffer.wrap(offset_array);
			
			fis.close();
		}

	}
	
	/**
	 * Find a region file with given coordinates.
	 * @param saveFolder path to the world save
	 * @param dimension Dimension to load chunks from.
	 * @param regionCoord coordinate of the region
	 * @return region file object
	 * @throws IOException of error occurs
	 */
	public static Region findRegion(File saveFolder, NamespaceID dimension, Point regionCoord) throws IOException {
		File dir;
		if(dimension.equals(new NamespaceID("minecraft", "overworld"))) {
			dir = new File(saveFolder.getAbsolutePath(), "region");
		} else if (dimension.equals(new NamespaceID("minecraft", "the_nether"))) {
			dir = new File(saveFolder.getAbsolutePath(), "DIM-1/region");
		} else if (dimension.equals(new NamespaceID("minecraft", "the_end"))) {
			dir = new File(saveFolder.getAbsolutePath(), "DIM1/region");
		} else {
			dir = new File(saveFolder.getAbsolutePath(), String.format("dimensions/%s/%s/region", dimension.namespace, dimension.path));
		}
		
		File file = new File(dir, "/r."+regionCoord.x+"."+regionCoord.y+".mca");
		if(!file.exists())
			file = new File(dir, "/r."+regionCoord.x+"."+regionCoord.y+".mcr");
		
		return new Region(file);
	}
	
	/**
	 * Get the region coordinate for the given chunk coordinate.
	 * @param chunkCoord coordinate of chunk
	 * @return region coordinate
	 */
	public static Point getRegionCoord(Point chunkCoord) throws IOException
	{
		//equivalent to dividing by 32
		int rx = chunkCoord.x >> 5;
		int rz = chunkCoord.y >> 5;

		return new Point(rx, rz);
	}
	
	/**
	 * Get the list of all regions in the given save.
	 * @param saveFolder path to the world save
	 * @return collection of region file objects
	 * @throws IOException if error occurs
	 */
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
	
	/**
	 * Retrieve the given chunk.
	 * @param x x coordinate of the chunk
	 * @param z z coordinate of the chunk
	 * @return chunk object
	 * @throws Exception if error occurs while reading the chunk
	 */
	@CheckForNull
	public Chunk getChunk(int x, int z) throws Exception {
		int cx = Math.floorMod(x, 32);
		int cz = Math.floorMod(z, 32);
		int idx = cx + cz * 32;
		
		InputStream chunkIs = getChunkStream(region_file, offset, idx);
		if (chunkIs == null) {
			return null;
		}
		if (region_entity_file.exists()) {
			return new Chunk(chunkIs, getChunkStream(region_entity_file, entity_offset, idx), is_anvil);
		} else {
			return new Chunk(chunkIs,null, is_anvil);
		}
	}
	
	@CheckForNull
	private InputStream getChunkStream(File file, ByteBuffer offset, int idx) throws Exception {
		int off = offset.getInt(idx*4);
		int sec = off >> 8;
		int len = off & 0xff;

		if(sec<2)
			return null;

		RandomAccessFile raf=new RandomAccessFile(file, "r");
		raf.seek(sec*4096);

		len=raf.readInt();
		int compression_type=raf.readByte();
		byte[] buf = new byte[len - 1];
		raf.read(buf);
		raf.close();
		InputStream is;
		if(compression_type==1) { //GZIP
			is=new GZIPInputStream(new ByteArrayInputStream(buf));
		} else if(compression_type==2) {//Inflate
			is=new InflaterInputStream(new ByteArrayInputStream(buf));
		} else {
			throw new Exception("Wrong compression type!");
		}
		return is;
	}

	/**
	 * Prints a list of available chunks to the stnadard output.
	 */
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
