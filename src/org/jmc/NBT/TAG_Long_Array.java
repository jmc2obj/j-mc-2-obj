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
 * NBT long array tag.
 * Used to store a sequence of longs. 
 * Tag contains the name, the size of the array and a sequence of long values. 
 * @author danijel
 * @author mmdanggg2
 *
 */
public class TAG_Long_Array extends NBT_Tag {

	/**
	 * Long array stored in this tag.
	 */
	public long [] data;
	
	/**
	 * Main constructor.
	 * @param name name of tag
	 */
	public TAG_Long_Array(String name) {
		super(name);
	}
	
	public TAG_Long_Array(String name, long[] data) {
		super(name);
		this.data = data;
	}
	
	/**
	 * Id of tag. (see NBT_Tag)
	 */
	public byte ID() {
		return 12;
	}

	/**
	 * Loading method. (see NBT_Tag)
	 */
	protected void parse(DataInputStream stream) throws Exception {
		int len=stream.readInt();
		
		data = new long[len];
		
		for(int i=0; i<len; i++)
			data[i]=stream.readLong();

	}

	/**
	 * Saving method.  (see NBT_Tag)
	 */
	protected void write(DataOutputStream stream) throws Exception {
		stream.writeLong(data.length);
		for(int i=0; i<data.length; i++)
			stream.writeLong(data[i]);
		
	}

	/**
	 * Debug output.  (see NBT_Tag)
	 */
	public String toString() {
		return "TAG_LongArray(\""+name+"\"): size="+data.length;
	}

}
