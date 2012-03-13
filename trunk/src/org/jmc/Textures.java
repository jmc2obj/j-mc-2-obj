package org.jmc;

public class Textures
{
	private final String[] textures;
	
	public Textures()
	{
		textures = new String[256];
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
		textures[19] = "tex\\49.png"; 	//sponge
		textures[20] = "tex\\50.png"; 	//glass
		textures[21] = "tex\\161.png"; 	//lapis ore
		textures[22] = "tex\\145.png"; 	//lapis block
		textures[23] = "tex\\47.png";	//dispenser (has data / tile value)
		textures[24] = "tex\\193.png";	//sandstone
		textures[25] = "tex\\75.png";	//noteblock (tile value)
		textures[27] = "tex\\180.png";	//powered rail (data)
		textures[28] = "tex\\196.png";	//detector rail (data)
		textures[29] = "tex\\164.png";	//sticky piston (data)
		textures[31] = "tex\\40.png";	//tall grass (data)
		textures[32] = "tex\\56.png";	//dead bush 
//		textures[33] = new Color(0xBC9862);	//piston (has data value)
//		textures[34] = new Color(0xBC9862);	//piston extension (data)
//		textures[35] = new Color(0xFFFFFF);	//wool.. need data values for these..
//		textures[37] = new Color(0xF1F902); 	//dandelion
//		textures[38] = new Color(0xD10609); 	//rose
//		textures[39] = new Color(0x725643); 	//brown shroom
//		textures[40] = new Color(0xE21212); 	//red shroom
//		textures[41] = new Color(0xFCE040);	//gold block
//		textures[42] = new Color(0xE6E6E6);	//iron block
//		textures[43] = new Color(0x9D9D9D);	//double slabs.. need data values for these..(data / block)
//		textures[44] = new Color(0x808080);	//slabs
//		textures[45] = new Color(0x9B5643);	//bricks
//		textures[46] = new Color(0xC13C17);	//tnt
//		textures[47] = new Color(0x118E6B);	//bookshelf
//		textures[48] = new Color(0x3D743D);	//moss stone
//		textures[49] = new Color(0x241E31);	//obsidian
//		textures[50] = new Color(0xFFFF97);	//torch (data value)
//		textures[51] = new Color(0xFC5700);	//fire (data value)
//		textures[52] = new Color(0x1A2126);	//mob spawner (tile value)
//		textures[53] = new Color(0xAD854A);	//wooden stairs (data value)
		textures[54] = "tex\\28.png";;	//chest (data / tile value)
//		textures[56] = new Color(0x759094);	//diamond ore
//		textures[57] = new Color(0x13ADB0);	//diamond block
//		textures[58] = new Color(0x7D5216);	//crafting table
//		textures[60] = new Color(0x523308);	//farmland
//		textures[61] = new Color(0x737373);	//furnace
//		textures[62] = new Color(0x736262);	//furnace lit
//		textures[67] = new Color(0x787878); 	//cobble stairs
//		textures[73] = new Color(0x8F4747);	//redstone ore
//		textures[74] = new Color(0xAB3333);	//redstone ore lit
//		textures[78] = new Color(0xFFFFFF);	//snow layer
//		textures[79] = new Color(0xBAD0FF);	//ice
//		textures[80] = new Color(0xFFFFFF);	//snow block
//		textures[81] = new Color(0x3D7500);	//cactus
//		textures[82] = new Color(0xABABAB);	//clay
//		textures[84] = new Color(0x422100);	//jukebox
//		textures[86] = new Color(0xEB8621);	//pumpkin
//		textures[87] = new Color(0x9C4E4E);	//netherrack
//		textures[88] = new Color(0xFF9C9C);	//soulsand
//		textures[89] = new Color(0xEDC421);	//glowstone
//		textures[91] = new Color(0xFF3C00);	//jackolantern
//		textures[95] = new Color(0xA76E1F);	//locked chest
//		textures[97] = new Color(0xC2C2C2);	//hidden silverfish
//		textures[98] = new Color(0x9C9C9C);	//stone brick
//		textures[102] = new Color(0x9B5643);	//brick stairs		
//		textures[98] = new Color(0x9C9C9C);	//stone brick stairs		
//		textures[103] = new Color(0xB0DE3C);	//melon
//		textures[110] = new Color(0xCF9191);	//mycelium
//		textures[112] = new Color(0x7A0000);	//netherbrick
//		textures[114] = new Color(0x7A0000);	//netherbrick stairs		
//		textures[121] = new Color(0xD6D5B4);	//endstone
//		textures[123] = new Color(0xB58F1F);	//redstone lamp
//		textures[124] = new Color(0xF5CE58);	//redstone lamp lit
	}
	
	public String getTexture(int id)
	{
		if(id>=0 && id<textures.length && textures[id] != null)
		{
			return textures[id];
		}		
		return null;
	}
}
