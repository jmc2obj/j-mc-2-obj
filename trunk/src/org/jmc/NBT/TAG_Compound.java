package org.jmc.NBT;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class TAG_Compound extends NBT_Tag {

	public List<NBT_Tag> elements;
	
	TAG_Compound(String name) {
		super(name);
		elements=new LinkedList<NBT_Tag>();
	}

	protected void parse(DataInputStream stream) throws Exception {
		
		while(true)
		{
			NBT_Tag tag=NBT_Tag.make(stream);
			if(tag.ID()==0) return;
			elements.add(tag);
		}
		
	}

	public byte ID() {
		return 10;
	}	
	
	public NBT_Tag getElement(String name)
	{	
		Iterator<NBT_Tag> iter=elements.iterator();
		while(iter.hasNext())
		{
			NBT_Tag ret=iter.next();
			if(ret.getName().equals(name)) return ret;
		}
		return null;
	}

	protected void write(DataOutputStream stream) throws Exception {
		Iterator<NBT_Tag> iter=elements.iterator();
		while(iter.hasNext())
		{
			NBT_Tag ret=iter.next();
			ret.save(stream);
		}
		TAG_End end=new TAG_End("");
		end.save(stream);
	}
	
	public String toString() {
		String ret="TAG_Compound(\""+name+"\"): count="+elements.size()+"\n";
		Iterator<NBT_Tag> iter=elements.iterator();
		while(iter.hasNext())
		{
			NBT_Tag tag=iter.next();
			ret+=tag.toString()+"\n";
		}
		ret+="ENDOF TAG_Compound(\""+name+"\")";
		return ret;
	}
}
