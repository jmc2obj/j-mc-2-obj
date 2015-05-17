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
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Notch's Binary Tag class.
 * This is the main NBT class defining the minimum requirements of other tags
 * and containing the main loading/saving routines.
 * @author danijel
 *
 */
public abstract class NBT_Tag {

	/**
	 * Name of the tag.
	 * Most tags have a name describing their contents.
	 */
	protected String name;
	
	/**
	 * Get the name of the Tag.
	 * @return name of the tag
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Main constructor.
	 * @param name name of the tag
	 */
	public NBT_Tag(String name)
	{
		this.name=name;
	}	
	
	/**
	 * Main loading routine. 
	 * Reads the tag starting from the current point in the input stream.
	 * If the tag is a collection, it also recursively reads the internal items.
	 * @param is input stream located at the start of the tag
	 * @return tag object
	 * @throws Exception if there is an error parsing the file
	 */
	public static NBT_Tag make(InputStream is) throws Exception
	{
		NBT_Tag ret=null;
		DataInputStream stream=new DataInputStream(is);
		byte t=stream.readByte();
		int type=(int)t;
		
		String name="";
		if(type>0)
			name=stream.readUTF();
		
		//System.out.println("READ "+type+": "+name);
		
		switch(type)
		{
		case 0:
			ret=new TAG_End(name);
			break;
		case 1:
			ret=new TAG_Byte(name);
			break;
		case 2:
			ret=new TAG_Short(name);
			break;
		case 3:
			ret=new TAG_Int(name);
			break;
		case 4:
			ret=new TAG_Long(name);
			break;
		case 5:
			ret=new TAG_Float(name);
			break;
		case 6:
			ret=new TAG_Double(name);
			break;
		case 7:
			ret=new TAG_Byte_Array(name);
			break;
		case 8:
			ret=new TAG_String(name);
			break;
		case 9:
			ret=new TAG_List(name);
			break;
		case 10:
			ret=new TAG_Compound(name);
			break;
		case 11:
			ret=new TAG_Int_Array(name);
			break;
		default:
			throw(new Exception("NBT_Tag type error: "+type));
		}
		
		ret.parse(stream);
		
		return ret;
	}
	
	/**
	 * Main saving routine.
	 * Saves the tag and if it's a collection, saves its contents as well.
	 * @param os output stream pointing at the location where to save the tag
	 * @throws Exception if there is an error saving the tag
	 */
	public void save(OutputStream os) throws Exception
	{
		DataOutputStream out=new DataOutputStream(os);
		
		out.writeByte(ID());
		
		if(ID()>0)
		{
			out.writeUTF(name);
		}
		
		write(out);
	}
	
	/**
	 * Type dependent method used for loading the tag contents.
	 * @param stream stream at the location where the tag begins
	 * @throws Exception in case there is an error parsing the tag
	 */
	protected abstract void parse(DataInputStream stream) throws Exception;
	/**
	 * Type dependent method used for saving the tag contents.
	 * @param stream stream at the location where the tag is to be written
	 * @throws Exception in case there is an error saving the tag
	 */
	protected abstract void write(DataOutputStream stream) throws Exception;
	/**
	 * Type dependent method for retrieving the type of the tag.
	 * @return type ID of the tag
	 */
	public abstract byte ID();
	/**
	 * Debug method for printing the description of the tag and its contents.
	 */
	public abstract String toString();
}
