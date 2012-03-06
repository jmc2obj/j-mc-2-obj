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

public class TAG_Byte_Array extends NBT_Tag {

	public byte [] data;

	public TAG_Byte_Array(String name) {
		super(name);
	}

	public byte ID() {
		return 7;
	}

	protected void parse(DataInputStream stream) throws Exception {
		int size=stream.readInt();
		int ret,read=0;

		//System.out.println("ByteArray size: "+size);

		data=new byte[size];

		while(size>0)
		{
			ret=stream.read(data, read, size);
			size-=ret;
			read+=ret;
		}
	}

	protected void write(DataOutputStream stream) throws Exception {				
		stream.writeInt(data.length);
		stream.write(data, 0, data.length);		
	}

	public String toString() {

		return "TAG_ByteArray(\""+name+"\"): size="+data.length;
	}

}
