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

/**
 * NBT list tag.
 * Used to store a collection of tags of the same type.
 * Tag contains the name, an ID denoting the type of items contained,
 * number of items and the sequence of these items. 
 * @author danijel
 *
 */
public class TAG_List extends NBT_Tag {

	public byte type;
	public NBT_Tag[] elements;

	/**
	 * Main constructor.
	 * @param name name of tag
	 */
	public TAG_List(String name) {
		super(name);
	}

	/**
	 * Id of tag. (see NBT_Tag)
	 */
	public byte ID() {
		return 9;
	}
	
	/**
	 * Retrieves the element at the given index.
	 * @param idx index of the given element
	 * @return element at the index or null if wrong index used
	 */
	public NBT_Tag getElement(int idx)
	{
		if(idx<0 || idx>=elements.length) return null;
		return elements[idx];
	}

	/**
	 * Loading method. (see NBT_Tag)
	 */
	protected void parse(DataInputStream stream) throws Exception {
		type=stream.readByte();
		int size=stream.readInt();
		elements=new NBT_Tag[size];

		//System.out.println("list elements: "+type+"x"+size);

		for(int i=0; i<size; i++)
		{

			switch(type)
			{
			case 1:
				elements[i]=new TAG_Byte("");
				break;
			case 2:
				elements[i]=new TAG_Short("");
				break;				
			case 3:
				elements[i]=new TAG_Int("");
				break;
			case 4:
				elements[i]=new TAG_Long("");
				break;
			case 5:
				elements[i]=new TAG_Float("");
				break;
			case 6:
				elements[i]=new TAG_Double("");
				break;
			case 7:
				elements[i]=new TAG_Byte_Array("");
				break;
			case 8:
				elements[i]=new TAG_String("");
				break;
			case 9:
				elements[i]=new TAG_List("");
				break;
			case 10:
				elements[i]=new TAG_Compound("");
				break;
			default:
				throw new Exception("Unkown NBT type in list: "+type);
			}
			elements[i].parse(stream);
		}

	}

	/**
	 * Saving method.  (see NBT_Tag)
	 */
	protected void write(DataOutputStream stream) throws Exception {
		stream.writeByte(type);
		stream.writeInt(elements.length);
		
		for(int i=0; i<elements.length; i++)
		{
			elements[i].write(stream);
		}
		
	}

	/**
	 * Debug output.  (see NBT_Tag)
	 */
	public String toString() {
		String ret="TAG_List(\""+name+"\"): count="+elements.length+" type="+(int)type+"\n";
		for(int i=0; i<elements.length; i++)
		{
			NBT_Tag tag=elements[i];
			ret+=tag.toString()+"\n";
		}
		ret+="ENDOF TAG_List(\""+name+"\")";
		return ret;
	}

}
