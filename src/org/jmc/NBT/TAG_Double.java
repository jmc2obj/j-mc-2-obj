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

public class TAG_Double extends NBT_Tag {

	public double value;
	
	public TAG_Double(String name) {
		super(name);
	}

	@Override
	public byte ID() {
		return 6;
	}

	@Override
	protected void parse(DataInputStream stream) throws Exception {
			value=stream.readDouble();
	}

	@Override
	protected void write(DataOutputStream stream) throws Exception {
		stream.writeDouble(value);
		
	}

	public String toString() {
		return "TAG_Double(\""+name+"\"): val="+value;
	}

}
