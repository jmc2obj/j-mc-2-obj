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
 * NBT short integer tag.
 * Used to store a short (half-precision) integer. 
 * Tag contains the name and a short integer value. 
 * @author danijel
 *
 */
public class TAG_Short extends NBT_Tag {

	/**
	 * Short integer value of this tag.
	 */
	public short value;
	
	/**
	 * Main constructor.
	 * @param name name of tag
	 */
	public TAG_Short(String name) {
		super(name);
	}

	/**
	 * Id of tag. (see NBT_Tag)
	 */
	public byte ID() {
		return 2;
	}

	/**
	 * Loading method. (see NBT_Tag)
	 */
	protected void parse(DataInputStream stream) throws Exception {
		value=stream.readShort();
	}

	/**
	 * Saving method.  (see NBT_Tag)
	 */
	protected void write(DataOutputStream stream) throws Exception {
		stream.writeShort(value);
		
	}

	/**
	 * Debug output.  (see NBT_Tag)
	 */
	public String toString() {
		return "TAG_Short(\""+name+"\"): val="+value;
	}

}
