package org.jmc.NBT;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class TAG_Int_Array extends NBT_Tag {

	public int [] data;
	
	public TAG_Int_Array(String name) {
		super(name);
	}
	
	public byte ID() {
		return 11;
	}

	protected void parse(DataInputStream stream) throws Exception {
		int len=stream.readInt();
		
		data = new int[len];
		
		for(int i=0; i<len; i++)
			data[i]=stream.readInt();

	}

	protected void write(DataOutputStream stream) throws Exception {
		stream.writeInt(data.length);
		for(int i=0; i<data.length; i++)
			stream.writeInt(data[i]);
		
	}

	public String toString() {
		return "TAG_IntArray(\""+name+"\"): size="+data.length;
	}

}
