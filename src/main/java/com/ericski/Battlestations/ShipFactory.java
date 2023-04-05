package com.ericski.Battlestations;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 *
 */
public enum ShipFactory
{
	INSTANCE;

	public List<Ship> getStandardTemplates()
	{
		if (RuleSetVersion.getInstance().isV1())
		{
			return getStandardTemplatesV1();
		}
		else
		{
			return getStandardTemplatesV1_1();
		}
	}

	private List<Ship> getStandardTemplatesV1()
	{
		InputStream xmlStream = Ship.class.getResourceAsStream("/com/ericski/Battlestations/ShipTemplates.xml");
		InputStreamReader reader = new InputStreamReader(xmlStream);
		return Ship.fromShipsXML(reader);
	}

	private List<Ship> getStandardTemplatesV1_1()
	{
		InputStream xmlStream = Ship.class.getResourceAsStream("/com/ericski/Battlestations/ShipTemplatesv1.1.xml");
		InputStreamReader reader = new InputStreamReader(xmlStream);
		return Ship.fromShipsXML(reader);
	}
}
