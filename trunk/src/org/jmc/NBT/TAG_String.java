package org.jmc.NBT;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class TAG_String extends NBT_Tag {

	String value;
	
	public TAG_String(String name) {
		super(name);
	}

	public byte ID() {
		return 8;
	}

	protected void parse(DataInputStream stream) throws Exception {
		value=stream.readUTF();		
	}

	protected void write(DataOutputStream stream) throws Exception {
		stream.writeUTF(value);
		
	}

	public String toString() {
		return "TAG_String(\""+name+"\"): val="+value;
	}
}
