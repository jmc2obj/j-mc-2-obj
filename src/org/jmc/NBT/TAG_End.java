package org.jmc.NBT;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class TAG_End extends NBT_Tag{
	
	public TAG_End(String name) {
		super(name);
	}
	protected void parse(DataInputStream stream) throws Exception {
	}

	public byte ID() {
		return 0;
	}
	
	
	protected void write(DataOutputStream stream) throws Exception {		
	}
	
	public String toString() {
		return "TAG_End";
	}
}
