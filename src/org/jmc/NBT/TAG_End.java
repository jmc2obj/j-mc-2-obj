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
 * NBT end tag.
 * Used to denote the end of a compound tag. 
 * Tag doesn't contain anything. 
 * @author danijel
 *
 */
public class TAG_End extends NBT_Tag{
	
	/**
	 * Main constructor.
	 * @param name name of tag
	 */
	public TAG_End(String name) {
		super(name);
	}
	
	/**
	 * Loading method. (see NBT_Tag)
	 */
	protected void parse(DataInputStream stream) throws Exception {
	}

	/**
	 * Id of tag. (see NBT_Tag)
	 */
	public byte ID() {
		return 0;
	}
	
	/**
	 * Saving method.  (see NBT_Tag)
	 */
	protected void write(DataOutputStream stream) throws Exception {		
	}
	
	/**
	 * Debug output.  (see NBT_Tag)
	 */
	public String toString() {
		return "TAG_End";
	}
}
