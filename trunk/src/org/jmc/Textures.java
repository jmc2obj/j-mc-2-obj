package org.jmc;

public class Textures
{
	private final String[] textures;
	private final boolean[] alpha;
	
	public Textures()
	{
		textures = new String[256];
		alpha = new boolean[256];
		
		textures[0] = null; 					//air
		textures[1] = "tex\\2.png"; 	//stone
		textures[2] = "tex\\1.png"; 	//grass
		textures[3] = "tex\\3.png"; 	//dirt
		textures[4] = "tex\\17.png"; 	//cobble
		textures[5] = "tex\\5.png";	//planks
		textures[7] = "tex\\18.png";	//bedrock
		textures[8] = "tex\\206.png";	//water
		textures[9] = textures[8];	//stationary water
		textures[10] = "tex\\238.png";	//lava
		textures[11] = textures[10];	//stationary lava
		textures[12] = "tex\\19.png";	//sand
		textures[13] = "tex\\20.png";	//gravel
		textures[14] = "tex\\33.png";	//gold ore
		textures[15] = "tex\\34.png";	//iron ore
		textures[16] = "tex\\35.png";	//coal ore
		textures[17] = "tex\\21.png";	//wood.. need data values for these..
		textures[18] = "tex\\53.png";	//leaves.. need data values for these..
		alpha[18] = true;
		textures[19] = "tex\\49.png"; 	//sponge
		textures[20] = "tex\\50.png"; 	//glass
		alpha[20] = true;
		textures[21] = "tex\\161.png"; 	//lapis ore
		textures[22] = "tex\\145.png"; 	//lapis block
		textures[23] = "tex\\47.png";	//dispenser (has data / tile value)
		textures[24] = "tex\\193.png";	//sandstone
		textures[25] = "tex\\75.png";	//noteblock (tile value)
		textures[27] = "tex\\180.png";	//powered rail (data)
//		alpha[27] = true;
		textures[28] = "tex\\196.png";	//detector rail (data)
//		alpha[28] = true;
		textures[29] = "tex\\164.png";	//sticky piston (data)
		textures[31] = "tex\\40.png";	//tall grass (data)
//		alpha[31] = true;
		textures[32] = "tex\\56.png";	//dead bush 
//		alpha[32] = true;
		textures[33] = "tex\\108.png";	//piston (has data value)
		textures[34] = "tex\\108.png";	//piston extension (data)
		textures[35] = "tex\\65.png";	//wool.. need data values for these..
		textures[37] = "tex\\14.png"; 	//dandelion
//		alpha[37] = true;
		textures[38] = "tex\\13.png"; 	//rose
//		alpha[38] = true;
		textures[39] = "tex\\56.png"; 	//brown shroom
//		alpha[39] = true;
		textures[40] = "tex\\56.png"; 	//red shroom
//		alpha[40] = true;
		textures[41] = "tex\\24.png";	//gold block
		textures[42] = "tex\\23.png";	//iron block
		textures[43] = "tex\\6.png";	//double slabs.. need data values for these..(data / block)
		textures[44] = "tex\\6.png";	//slabs
		textures[45] = "tex\\8.png";	//bricks
		textures[46] = "tex\\9.png";	//tnt
		textures[47] = "tex\\36.png";	//bookshelf
		textures[48] = "tex\\37.png";	//moss stone
		textures[49] = "tex\\38.png";	//obsidian
		textures[50] = "tex\\81.png";	//torch (data value)
		alpha[50] = true;
//		textures[51] = "tex\\56.png";	//fire (data value)
		textures[52] = "tex\\66.png";	//mob spawner (tile value)
		alpha[52] = true;
		textures[53] = "tex\\5.png";	//wooden stairs (data value)
		textures[54] = "tex\\28.png";	//chest (data / tile value)
		textures[56] = "tex\\51.png";	//diamond ore
		textures[57] = "tex\\25.png";	//diamond block
		textures[58] = "tex\\44.png";	//crafting table
		textures[59] = "tex\\95.png";	//wheat
		alpha[59]=true;
		textures[60] = "tex\\88.png";	//farmland
		textures[61] = "tex\\45.png";	//furnace
		textures[62] = "tex\\62.png";	//furnace lit
		textures[67] = "tex\\17.png"; 	//cobble stairs
		textures[73] = "tex\\52.png";	//redstone ore
		textures[74] = "tex\\52.png";	//redstone ore lit
		textures[78] = "tex\\67.png";	//snow layer
		textures[79] = "tex\\68.png";	//ice
		textures[80] = "tex\\67.png";	//snow block
		textures[81] = "tex\\70.png";	//cactus
		textures[82] = "tex\\73.png";	//clay
		textures[84] = "tex\\76.png";	//jukebox
		textures[86] = "tex\\119.png";	//pumpkin
		textures[87] = "tex\\104.png";	//netherrack
		textures[88] = "tex\\105.png";	//soulsand
		textures[89] = "tex\\106.png";	//glowstone
		textures[91] = "tex\\120.png";	//jackolantern
		textures[95] = "tex\\28.png";	//locked chest
//		textures[97] = "tex\\56.png";	//hidden silverfish
		textures[98] = "tex\\55.png";	//stone brick
		textures[102] = "tex\\8.png";	//brick stairs
		textures[98] = "tex\\2.png";	//stone brick stairs
		textures[103] = "tex\\137.png";	//melon
		textures[110] = "tex\\79.png";	//mycelium
		textures[112] = "tex\\225.png";	//netherbrick
		textures[114] = "tex\\225.png";	//netherbrick stairs		
		textures[121] = "tex\\176.png";	//endstone
		textures[123] = "tex\\212.png";	//redstone lamp
		textures[124] = "tex\\213.png";	//redstone lamp lit
	}
	
	public String getTexture(int id)
	{
		if(id>=0 && id<textures.length && textures[id] != null)
		{
			return textures[id];
		}		
		return null;
	}
	
	public String getAlphaTexture(int id)
	{
		if(id>=0 && id<textures.length && textures[id] != null)
		{
			if(textures[id]==null) return null;
			if(!textures[id].endsWith(".png")) return textures[id]+"_a.png";
			return textures[id].substring(0,textures[id].length()-4)+"_a.png";
		}		
		return null;
	}
	
	public boolean hasAlpha(int id)
	{
		if(id >= 0 && id < alpha.length && alpha[id])
		{
			return true;
		}
		return false;
	}
}
