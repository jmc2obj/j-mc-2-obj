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
 * NBT integer tag.
 * Used to store an integer. 
 * Tag contains the name and an integer value. 
 * @author danijel
 *
 */
public class TAG_Int extends NBT_Tag {

	/**
	 * Integer value of this tag.
	 */
	public int value; 
	
	/**
	 * Main constructor.
	 * @param name name of tag
	 */
	public TAG_Int(String name) {
		super(name);
	}
	
	public TAG_Int(String name, int value) {
		super(name);
		this.value = value;
	}

	/**
	 * Id of tag. (see NBT_Tag)
	 */
	public byte ID() {
		return 3;
	}

	/**
	 * Loading method. (see NBT_Tag)
	 */
	protected void parse(DataInputStream stream) throws Exception {
		value=stream.readInt();

	}

	/**
	 * Saving method.  (see NBT_Tag)
	 */
	protected void write(DataOutputStream stream) throws Exception {
		stream.writeInt(value);
		
	}

	/**
	 * Debug output.  (see NBT_Tag)
	 */
	public String toString() {
		return "TAG_Int(\""+name+"\"): val="+value;	
	}

}
