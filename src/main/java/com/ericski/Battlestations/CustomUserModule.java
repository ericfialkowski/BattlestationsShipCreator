package com.ericski.Battlestations;

import org.apache.logging.log4j.message.FormattedMessage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static javax.imageio.ImageIO.read;

@XmlRootElement(name = "Module")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CustomUserModule extends Module
{
	public CustomUserModule(String name, String description, String profession, String fileName)
	{
		this(name, 0, description, profession, fileName);
	}

	public CustomUserModule(String name, int rotation, String description, String profession, String fileName)
	{
		super(name, rotation, description, profession, fileName);
	}

	@Override
	public Module copy()
	{
		return new CustomUserModule(name, rotation, description, profession, fileName);
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
				try (InputStream imageStream = new FileInputStream(fileName))
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

	public static CustomUserModule fromXml(File f)
	{
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CustomUserModule.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			CustomUserModule m = (CustomUserModule)jaxbUnmarshaller.unmarshal(f);

			String dir = f.getParent();
			m.setFileName(dir + "/" + m.getFileName());

			return m;
		}
		catch (Exception ex)
		{
			logger.warn("Couldn't load module", ex);
			return null;
		}
	}
}
