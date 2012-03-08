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
 * NBT double tag.
 * Used to store a double precision floating point number. 
 * Tag contains the name and a double value. 
 * @author danijel
 *
 */
public class TAG_Double extends NBT_Tag {

	/**
	 * Double value of this tag.
	 */
	public double value;
	
	/**
	 * Main constructor.
	 * @param name name of tag
	 */
	public TAG_Double(String name) {
		super(name);
	}

	/**
	 * Id of tag. (see NBT_Tag)
	 */
	@Override
	public byte ID() {
		return 6;
	}

	/**
	 * Loading method. (see NBT_Tag)
	 */
	@Override
	protected void parse(DataInputStream stream) throws Exception {
			value=stream.readDouble();
	}

	/**
	 * Saving method.  (see NBT_Tag)
	 */
	@Override
	protected void write(DataOutputStream stream) throws Exception {
		stream.writeDouble(value);
		
	}

	/**
	 * Debug output.  (see NBT_Tag)
	 */
	public String toString() {
		return "TAG_Double(\""+name+"\"): val="+value;
	}

}
