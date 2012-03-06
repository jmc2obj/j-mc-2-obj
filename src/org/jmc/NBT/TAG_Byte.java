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

public class TAG_Byte extends NBT_Tag {

	public byte value;
	
	public TAG_Byte(String name) {
		super(name);
	}

	public byte ID() {		
		return 1;
	}

	protected void parse(DataInputStream stream) throws Exception {	
		value=stream.readByte();
	}

	protected void write(DataOutputStream stream) throws Exception {
		stream.writeByte(value);
	}

	public String toString() {
		return "TAG_Byte(\""+name+"\"): dec="+(int)value;
	}
}
