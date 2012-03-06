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

public class TAG_Float extends NBT_Tag {

	public float value;
	
	public TAG_Float(String name) {
		super(name);
	}

	public byte ID() {
		return 5;
	}

	protected void parse(DataInputStream stream) throws Exception {
			value=stream.readFloat();		
	}

	protected void write(DataOutputStream stream) throws Exception {
			stream.writeFloat(value);
		
	}
	
	public String toString() {
		return "TAG_Float(\""+name+"\"): val="+value;
	}

}
