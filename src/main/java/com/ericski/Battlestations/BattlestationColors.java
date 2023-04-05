package com.ericski.Battlestations;

import java.awt.Color;

public enum BattlestationColors
{
	Athletics(0x999999),
	Combat(0xcc3300),
	Diplomacy(0x00ccff),
	Engineering(0x6666cc),
	Piloting(0xffcc00),
	Psion(0x9900ff),
	Science(0x669933),
	None(0);

	private final Color color;

	private BattlestationColors(int color)
	{
		this.color = new Color(color);
	}

	public Color getColor()
	{
		return color;
	}

	public static Color getColorFromName(String profession)
	{
		if ("Athlete".equalsIgnoreCase(profession))
		{
			return Athletics.getColor();
		}
		else if ("Marine".equalsIgnoreCase(profession) || "Combat".equalsIgnoreCase(profession))
		{
			return Combat.getColor();
		}
		else if ("Engineer".equalsIgnoreCase(profession) || "Engineering".equalsIgnoreCase(profession))
		{
			return Engineering.getColor();
		}
		else if ("Pilot".equalsIgnoreCase(profession) || "Pilotting".equalsIgnoreCase(profession))
		{
			return Piloting.getColor();
		}
		else if ("Scientist".equalsIgnoreCase(profession) || "Science".equalsIgnoreCase(profession))
		{
			return Science.getColor();
		}
		else if ("Diplomat".equalsIgnoreCase(profession))
		{
			return Diplomacy.getColor();
		}
		else if ("Psion".equalsIgnoreCase(profession))
		{
			return Psion.getColor();
		}
		return None.getColor();
	}
}
