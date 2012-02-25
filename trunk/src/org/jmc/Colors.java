package org.jmc;

import java.awt.Color;

public class Colors
{
	private final Color[] colors;
	public Colors()
	{
		colors = new Color[124];
		colors[0] = null;
		colors[1] = new Color(0x686868);
		colors[2] = new Color(0x5F9F35);
		colors[3] = new Color(0x966C4A);
		colors[4] = new Color(0x9E9E9E);
		colors[5] = new Color(0xC9A56F);
		colors[6] = new Color(0xFF0000);
		colors[7] = new Color(0x575757);
		colors[8] = new Color(0x4281FF);
		colors[9] = new Color(0x4281FF);
		colors[10] = new Color(0xFC5700);
		colors[11] = new Color(0xFC5700);
		colors[12] = new Color(0xD2CB95);
		
		colors[18] = new Color(0x105210);
	}
	
	public Color getColor(int id)
	{
		if(colors[id] != null)
		{
			return colors[id];
		}
		return null;
	}
}
