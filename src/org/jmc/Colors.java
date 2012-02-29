package org.jmc;

import java.awt.Color;

public class Colors
{
	private final Color[] colors;
	public Colors()
	{
		colors = new Color[256];
		colors[0] = null; 					//air
		colors[1] = new Color(0x686868); 	//stone
		colors[2] = new Color(0x5F9F35); 	//dirt-grass
		colors[3] = new Color(0x966C4A); 	//grass
		colors[4] = new Color(0x9E9E9E); 	//cobble
		colors[5] = new Color(0xC9A56F);	//planks
		colors[6] = new Color(0xFF0000);	//saplings.. need data values for these..
		colors[7] = new Color(0x575757);	//bedrock
		colors[8] = new Color(0x4281FF);	//water
		colors[9] = new Color(0x4281FF);	//stationary water
		colors[10] = new Color(0xFC5700);	//lava
		colors[11] = new Color(0xFC5700);	//stationary lava
		colors[12] = new Color(0xD2CB95);	//sand
		colors[13] = new Color(0xABABAB);	//gravel
		colors[14] = new Color(0xFCD100);	//gold ore
		colors[15] = new Color(0xAF8E77);	//iron ore
		colors[16] = new Color(0x3F3F3F);	//coal ore
		colors[17] = new Color(0x6A5534);	//wood.. need data values for these..
		colors[18] = new Color(0x105210);	//leaves.. need data values for these..
		colors[19] = new Color(0xBDBD35); 	//sponge
		colors[20] = new Color(0xD3FBFF22); 	//glass
		colors[21] = new Color(0x2A4F8C); 	//lapis ore
		colors[22] = new Color(0x1C57C6); 	//lapis block
		colors[23] = new Color(0x747474);	//dispenser (has data / tile value)
		colors[24] = new Color(0xC0BA78);	//sandstone
		colors[25] = new Color(0x945F44);	//noteblock (tile value)
		colors[26] = new Color(0x8C1416);	//bed (data value)
		colors[27] = new Color(0xA00002);	//powered rail (data)
		colors[28] = new Color(0xA8A8A8);	//detector rail (data)
		colors[29] = new Color(0x91C37D);	//sticky piston (data)
		colors[30] = new Color(0xFFFFFF);	//cobweb
		colors[31] = new Color(0x367C0F);	//tall grass (data)
		colors[32] = new Color(0x946428);	//dead bush 
		colors[33] = new Color(0xBC9862);	//piston (has data value)
		colors[34] = new Color(0xBC9862);	//piston extension (data)
		colors[35] = new Color(0xB7B7B7);	//wool.. need data values for these..
		colors[36] = null ;	//block moved by piston
		colors[37] = new Color(0xF1F902); 	//dandelion
		colors[38] = new Color(0xD10609); 	//rose
		colors[39] = new Color(0x725643); 	//brown shroom
		colors[40] = new Color(0xE21212); 	//red shroom
		colors[41] = new Color(0xFCE040);	//gold block
		colors[42] = new Color(0xE6E6E6);	//iron block
		colors[43] = new Color(0x9D9D9D);	//double slabs.. need data values for these..(data / block)
		colors[44] = new Color(0x808080);	//slabs
		colors[45] = new Color(0x9B5643);	//bricks
		colors[46] = new Color(0xC13C17);	//tnt
		colors[47] = new Color(0x118E6B);	//bookshelf
		colors[48] = new Color(0x3D743D);	//moss stone
		colors[49] = new Color(0x241E31);	//obsidian
		colors[50] = new Color(0xFFFF97);	//torch (data value)
		colors[51] = new Color(0xFC5700);	//fire (data value)
		colors[52] = new Color(0x1A2126);	//mob spawner (tile value)
		colors[53] = new Color(0xAD854A);	//wooden stairs (data value)
		colors[54] = new Color(0xA76E1F);	//chest (data / tile value)
	}
	
	public Color getColor(int id)
	{
		if(id>=0 && id<colors.length && colors[id] != null)
		{
			return colors[id];
		}
		return null;
	}
}
