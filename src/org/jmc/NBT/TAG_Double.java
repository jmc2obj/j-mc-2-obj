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
