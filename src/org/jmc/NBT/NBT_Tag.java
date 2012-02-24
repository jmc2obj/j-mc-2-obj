package org.jmc.NBT;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public abstract class NBT_Tag {

	protected String name;
	
	public String getName()
	{
		return name;
	}
	
	public NBT_Tag(String name)
	{
		this.name=name;
	}	
	
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
			ret=new TAG_IntArray(name);
			break;
		default:
			throw(new Exception("NBT_Tag type error: "+type));
		}
		
		ret.parse(stream);
		
		return ret;
	}
	
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
	
	protected abstract void parse(DataInputStream stream) throws Exception;
	protected abstract void write(DataOutputStream stream) throws Exception;
	public abstract byte ID();
	public abstract String toString();
}
