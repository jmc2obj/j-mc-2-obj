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

public class TAG_Int extends NBT_Tag {

	public int value; 
	
	public TAG_Int(String name) {
		super(name);
	}

	public byte ID() {
		return 3;
	}

	protected void parse(DataInputStream stream) throws Exception {
		value=stream.readInt();

	}

	protected void write(DataOutputStream stream) throws Exception {
		stream.writeInt(value);
		
	}

	public String toString() {
		return "TAG_Int(\""+name+"\"): val="+value;	
	}

}
