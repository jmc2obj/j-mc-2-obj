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
 * NBT string tag.
 * Used to store a text string. 
 * Tag contains the name and a text string. 
 * @author danijel
 *
 */
public class TAG_String extends NBT_Tag {

	/**
	 * Text string stored in this tag.
	 */
	public String value;
	
	/**
	 * Main constructor.
	 * @param name name of tag
	 */
	public TAG_String(String name) {
		super(name);
	}
	
	public TAG_String(String name, String value) {
		super(name);
		this.value = value;
	}

	/**
	 * Id of tag. (see NBT_Tag)
	 */
	public byte ID() {
		return 8;
	}

	/**
	 * Loading method. (see NBT_Tag)
	 */
	protected void parse(DataInputStream stream) throws Exception {
		value=stream.readUTF();		
	}

	/**
	 * Saving method.  (see NBT_Tag)
	 */
	protected void write(DataOutputStream stream) throws Exception {
		stream.writeUTF(value);
		
	}

	/**
	 * Debug output.  (see NBT_Tag)
	 */
	public String toString() {
		return "TAG_String(\""+name+"\"): val="+value;
	}
}
