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
 * NBT long integer tag.
 * Used to store a long (double precision) integer. 
 * Tag contains the name and a long integer value. 
 * @author danijel
 *
 */
public class TAG_Long extends NBT_Tag {

	/**
	 * Long integer value of this tag.
	 */
	public long value;
	
	/**
	 * Main constructor.
	 * @param name name of tag
	 */
	public TAG_Long(String name) {
		super(name);
	}
	
	public TAG_Long(String name, long value) {
		super(name);
		this.value = value;
	}

	/**
	 * Id of tag. (see NBT_Tag)
	 */
	public byte ID() {
		return 4;
	}

	/**
	 * Loading method. (see NBT_Tag)
	 */
	protected void parse(DataInputStream stream) throws Exception {
		value=stream.readLong();
	}

	/**
	 * Saving method.  (see NBT_Tag)
	 */
	protected void write(DataOutputStream stream) throws Exception {
		stream.writeLong(value);
		
	}

	/**
	 * Debug output.  (see NBT_Tag)
	 */
	public String toString() {
		return "TAG_Long(\""+name+"\"): val="+value;
	}

}
