package com.ericski.Battlestations;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import static javax.imageio.ImageIO.read;
import org.apache.logging.log4j.message.FormattedMessage;

public class InternalModule extends Module
{
	public InternalModule(String name, String description, String profession, String fileName)
	{
		this(name, 0, description, profession, fileName);
	}

	public InternalModule(String name, int rotation, String description, String profession, String fileName)
	{
		super(name, rotation, description, profession, fileName);
	}

	@Override
	public Module copy()
	{
		return new InternalModule(name, rotation, description, profession, fileName);
	}

	@Override
	protected Image loadImage()
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("loading image for " + toString());
		}
		if (!imageMap.containsKey(name))
		{
			try
			{
				try (InputStream imageStream = getClass().getResourceAsStream(fileName))
				{
					Image img = read(imageStream);
					if (img != null)
					{
						image = img;
						imageMap.putIfAbsent(name, image);
					}
				}
			}
			catch (IOException ignore)
			{
				if (logger.isWarnEnabled())
				{
					FormattedMessage fm = new FormattedMessage("Error loading module %s", toString());
					logger.warn(fm, ignore);
				}
			}
		}
		return imageMap.get(name);
	}
}
