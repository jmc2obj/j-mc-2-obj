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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * NBT compound collection tag.
 * Used to store a collection of data of various types. 
 * Tag contains the name and a collection of other tags until the end tag. 
 * @author danijel
 *
 */
public class TAG_Compound extends NBT_Tag {

	/**
	 * List of items stored in this tag.
	 */
	public List<NBT_Tag> elements;
	
	/**
	 * Main constructor.
	 * @param name name of tag
	 */
	public TAG_Compound(String name) {
		super(name);
		elements=new LinkedList<NBT_Tag>();
	}
	
	public TAG_Compound(String name, List<NBT_Tag> tags) {
		super(name);
		elements=new LinkedList<NBT_Tag>();
		elements.addAll(tags);
	}

	/**
	 * Loading method. (see NBT_Tag)
	 */
	protected void parse(DataInputStream stream) throws Exception {
		
		while(true)
		{
			NBT_Tag tag=NBT_Tag.make(stream);
			if(tag.ID()==0) return;
			elements.add(tag);
		}
		
	}

	/**
	 * Id of tag. (see NBT_Tag)
	 */
	public byte ID() {
		return 10;
	}	
	
	/**
	 * Retrieves the tag element with the given name from within this collection
	 * @param name name of the element we wish to retrieve
	 * @return element with the given name or null of no such element exists
	 */
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

	/**
	 * Saving method.  (see NBT_Tag)
	 */
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
	
	/**
	 * Debug output.  (see NBT_Tag)
	 */
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
