/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.Color;

/**
 * Color class
 * @author max, danijel
 *
 */
public class Colors
{
	private final Color[] colors;
	public Colors()
	{
		colors = new Color[256];
		colors[0] = null; 					//air
		colors[1] = new Color(0xC2C2C2); 	//stone
		colors[2] = new Color(0x4DAB43); 	//grass
		colors[3] = new Color(0x966C4A); 	//dirt
		colors[4] = new Color(0x787878); 	//cobble
		colors[5] = new Color(0xC9A56F);	//planks
		colors[7] = new Color(0x575757);	//bedrock
		colors[8] = new Color(0x4281FF);	//water
		colors[9] = new Color(0x4281FF);	//stationary water
		colors[10] = new Color(0xCF2600);	//lava
		colors[11] = new Color(0xCF2600);	//stationary lava
		colors[12] = new Color(0xD2CB95);	//sand
		colors[13] = new Color(0xABABAB);	//gravel
		colors[14] = new Color(0xFCD100);	//gold ore
		colors[15] = new Color(0xAF8E77);	//iron ore
		colors[16] = new Color(0x3F3F3F);	//coal ore
		colors[17] = new Color(0x6A5534);	//wood.. need data values for these..
		colors[18] = new Color(0x105210);	//leaves.. need data values for these..
		colors[19] = new Color(0xBDBD35); 	//sponge
		colors[20] = new Color(0xD3FBFF); 	//glass
		colors[21] = new Color(0x2A4F8C); 	//lapis ore
		colors[22] = new Color(0x1C57C6); 	//lapis block
		colors[23] = new Color(0x747474);	//dispenser (has data / tile value)
		colors[24] = new Color(0xC0BA78);	//sandstone
		colors[25] = new Color(0x945F44);	//noteblock (tile value)
		colors[26] = new Color(0xFF0000);	//bed
		colors[27] = new Color(0xA00002);	//powered rail (data)
		colors[28] = new Color(0xA8A8A8);	//detector rail (data)
		colors[29] = new Color(0x91C37D);	//sticky piston (data)
		colors[31] = new Color(0x367C0F);	//tall grass (data)
		colors[32] = new Color(0x946428);	//dead bush 
		colors[33] = new Color(0xBC9862);	//piston (has data value)
		colors[34] = new Color(0xBC9862);	//piston extension (data)
		colors[35] = new Color(0xFFFFFF);	//wool.. need data values for these..
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
		colors[56] = new Color(0x759094);	//diamond ore
		colors[57] = new Color(0x13ADB0);	//diamond block
		colors[58] = new Color(0x7D5216);	//crafting table
		colors[59] = new Color(0xF7E05E);	//wheat
		colors[60] = new Color(0x523308);	//farmland
		colors[61] = new Color(0x737373);	//furnace
		colors[62] = new Color(0x736262);	//furnace lit
		colors[67] = new Color(0x787878); 	//cobble stairs
		colors[73] = new Color(0x8F4747);	//redstone ore
		colors[74] = new Color(0xAB3333);	//redstone ore lit
		colors[78] = new Color(0xFFFFFF);	//snow layer
		colors[79] = new Color(0xBAD0FF);	//ice
		colors[80] = new Color(0xFFFFFF);	//snow block
		colors[81] = new Color(0x3D7500);	//cactus
		colors[82] = new Color(0xABABAB);	//clay
		colors[84] = new Color(0x422100);	//jukebox
		colors[86] = new Color(0xEB8621);	//pumpkin
		colors[87] = new Color(0x9C4E4E);	//netherrack
		colors[88] = new Color(0xFF9C9C);	//soulsand
		colors[89] = new Color(0xEDC421);	//glowstone
		colors[91] = new Color(0xFF3C00);	//jackolantern
		colors[95] = new Color(0xA76E1F);	//locked chest
		colors[97] = new Color(0x9C9C9C);	//hidden silverfish
		colors[98] = new Color(0x9C9C9C);	//stone brick
		colors[102] = new Color(0x9B5643);	//brick stairs		
		colors[103] = new Color(0xB0DE3C);	//melon
		colors[108] = new Color(0x9B5643);	//brick stairs
		colors[109] = new Color(0x9C9C9C);	//stone brick stairs
		colors[110] = new Color(0xCF9191);	//mycelium
		colors[112] = new Color(0x7A0000);	//netherbrick
		colors[114] = new Color(0x7A0000);	//netherbrick stairs		
		colors[121] = new Color(0xD6D5B4);	//endstone
		colors[123] = new Color(0xB58F1F);	//redstone lamp
		colors[124] = new Color(0xF5CE58);	//redstone lamp lit
	}
	
	public boolean hasData(int id)
	{
		if(id==35) return true;
		else return false;
	}
	
	public Color getColor(int id, byte data)
	{
		if(id==35)
		{
			//TODO: don't like the unsigned conversion here; needs fixing
			int uval=data;
			if(uval<0) uval=16+uval;
			switch(uval)
			{
			default:				
			case 0:
				return new Color(0xFFFFFF);
			case 1:
				return new Color(0xFF9900);				
			case 2:
				return new Color(0xCC00CC);
			case 3:
				return new Color(0x99CCFF);
			case 4:
				return new Color(0xFFFF66);
			case 5:
				return new Color(0x99FF33);
			case 6:
				return new Color(0xCC52A3);
			case 7:
				return new Color(0x555555);
			case 8:
				return new Color(0xCCCCCC);
			case 9:
				return new Color(0x009999);
			case 10:
				return new Color(0xCC3399);
			case 11:
				return new Color(0x000099);
			case 12:
				return new Color(0x4A1900);
			case 13:
				return new Color(0x336600);
			case 14:
				return new Color(0x990000);
			case 15:
				return new Color(0x000000);		
			}
		}
		if(id>=0 && id<colors.length && colors[id] != null)
		{
			return colors[id];
		}		
		return null;
	}
}
