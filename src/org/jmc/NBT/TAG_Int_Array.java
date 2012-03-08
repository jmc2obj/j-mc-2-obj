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
 * NBT integer array tag.
 * Used to store a sequence of integers. 
 * Tag contains the name, the size of the array and a sequence of integer values. 
 * @author danijel
 *
 */
public class TAG_Int_Array extends NBT_Tag {

	/**
	 * Integer array stored in this tag.
	 */
	public int [] data;
	
	/**
	 * Main constructor.
	 * @param name name of tag
	 */
	public TAG_Int_Array(String name) {
		super(name);
	}
	
	/**
	 * Id of tag. (see NBT_Tag)
	 */
	public byte ID() {
		return 11;
	}

	/**
	 * Loading method. (see NBT_Tag)
	 */
	protected void parse(DataInputStream stream) throws Exception {
		int len=stream.readInt();
		
		data = new int[len];
		
		for(int i=0; i<len; i++)
			data[i]=stream.readInt();

	}

	/**
	 * Saving method.  (see NBT_Tag)
	 */
	protected void write(DataOutputStream stream) throws Exception {
		stream.writeInt(data.length);
		for(int i=0; i<data.length; i++)
			stream.writeInt(data[i]);
		
	}

	/**
	 * Debug output.  (see NBT_Tag)
	 */
	public String toString() {
		return "TAG_IntArray(\""+name+"\"): size="+data.length;
	}

}
