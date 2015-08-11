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
 * NBT float tag.
 * Used to store a floating point number. 
 * Tag contains the name and a floating point value. 
 * @author danijel
 *
 */
public class TAG_Float extends NBT_Tag {

	/**
	 * Float value of this tag.
	 */
	public float value;
	
	/**
	 * Main constructor.
	 * @param name name of tag
	 */
	public TAG_Float(String name) {
		super(name);
	}
	
	public TAG_Float(String name, float value) {
		super(name);
		this.value = value;
	}

	/**
	 * Id of tag. (see NBT_Tag)
	 */
	public byte ID() {
		return 5;
	}

	/**
	 * Loading method. (see NBT_Tag)
	 */
	protected void parse(DataInputStream stream) throws Exception {
			value=stream.readFloat();		
	}

	/**
	 * Saving method.  (see NBT_Tag)
	 */
	protected void write(DataOutputStream stream) throws Exception {
			stream.writeFloat(value);
		
	}
	
	/**
	 * Debug output.  (see NBT_Tag)
	 */
	public String toString() {
		return "TAG_Float(\""+name+"\"): val="+value;
	}

}
