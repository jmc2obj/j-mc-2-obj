/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.NBT;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * NBT byte array tag.
 * Used to store an array of bytes. 
 * Tag contains the name, size of the array and a sequence of bytes. 
 * @author danijel
 *
 */
public class TAG_Byte_Array extends NBT_Tag {

	/**
	 * Array of bytes stored in this tag.
	 */
	public byte [] data;

	/**
	 * Main constructor.
	 * @param name name of tag
	 */
	public TAG_Byte_Array(String name) {
		super(name);
	}
	
	
	public TAG_Byte_Array(String name, byte[] data) {
		super(name);
		this.data = data;
	}

	/**
	 * Id of tag. (see NBT_Tag)
	 */
	public byte ID() {
		return 7;
	}

	/**
	 * Loading method. (see NBT_Tag)
	 */
	protected void parse(DataInputStream stream) throws Exception {
		int size=stream.readInt();
		int ret,read=0;

		//System.out.println("ByteArray size: "+size);

		data=new byte[size];

		while(size>0)
		{
			ret=stream.read(data, read, size);
			size-=ret;
			read+=ret;
		}
	}

	/**
	 * Saving method.  (see NBT_Tag)
	 */
	protected void write(DataOutputStream stream) throws Exception {				
		stream.writeInt(data.length);
		stream.write(data, 0, data.length);		
	}

	/**
	 * Debug output.  (see NBT_Tag)
	 */
	public String toString() {

		return "TAG_ByteArray(\""+name+"\"): size="+data.length;
	}

}
