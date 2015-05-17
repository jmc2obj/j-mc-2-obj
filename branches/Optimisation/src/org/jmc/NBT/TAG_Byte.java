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
 * NBT byte tag.
 * Used to store a byte. 
 * Tag contains the name and a byte value. 
 * @author danijel
 *
 */
public class TAG_Byte extends NBT_Tag {

	/**
	 * Byte value of this tag.
	 */
	public byte value;
	
	/**
	 * Main constructor.
	 * @param name name of tag
	 */
	public TAG_Byte(String name) {
		super(name);
	}

	/**
	 * Id of tag. (see NBT_Tag)
	 */
	public byte ID() {		
		return 1;
	}

	/**
	 * Loading method. (see NBT_Tag)
	 */
	protected void parse(DataInputStream stream) throws Exception {	
		value=stream.readByte();
	}

	/**
	 * Saving method.  (see NBT_Tag)
	 */
	protected void write(DataOutputStream stream) throws Exception {
		stream.writeByte(value);
	}

	/**
	 * Debug output.  (see NBT_Tag)
	 */
	public String toString() {
		return "TAG_Byte(\""+name+"\"): dec="+(int)value;
	}
}
