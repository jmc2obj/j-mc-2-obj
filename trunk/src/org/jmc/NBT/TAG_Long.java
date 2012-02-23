package org.jmc.NBT;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class TAG_Long extends NBT_Tag {

	public long value;
	
	public TAG_Long(String name) {
		super(name);
	}

	public byte ID() {
		return 4;
	}

	protected void parse(DataInputStream stream) throws Exception {
		value=stream.readLong();
	}

	protected void write(DataOutputStream stream) throws Exception {
		stream.writeLong(value);
		
	}

	public String toString() {
		return "TAG_Long(\""+name+"\"): val="+value;
	}

}
