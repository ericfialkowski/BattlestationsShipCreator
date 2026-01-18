package com.ericski.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class ImageIconScalable extends ImageIcon
{
	private static final long serialVersionUID = 1L;
	int width = -1;
	int height = -1;

	public ImageIconScalable()
	{
		super();
	}

	public ImageIconScalable(byte imageData[])
	{
		super(imageData);
	}

	public ImageIconScalable(byte imageData[],
							 String description)
	{
		super(imageData, description);
	}

	public ImageIconScalable(Image image)
	{
		super(image);
	}

	public ImageIconScalable(Image image,
							 String description)
	{
		super(image, description);
	}

	public ImageIconScalable(String filename)
	{
		super(filename);
	}

	public ImageIconScalable(String filename,
							 String description)
	{
		super(filename, description);
	}

	public ImageIconScalable(URL location)
	{
		super(location);
	}

	public ImageIconScalable(URL location,
							 String description)
	{
		super(location, description);
	}

	@Override
	public int getIconHeight()
	{
		int returnValue;
		if (height == -1)
		{
			returnValue = super.getIconHeight();
		}
		else
		{
			returnValue = height;
		}
		return returnValue;
	}

	@Override
	public int getIconWidth()
	{
		int returnValue;
		if (width == -1)
		{
			returnValue = super.getIconWidth();
		}
		else
		{
			returnValue = width;
		}
		return returnValue;
	}

	@Override
	public void paintIcon(Component c,
						  Graphics g,
						  int x,
						  int y)
	{
		if ((width == -1) && (height == -1))
		{
			g.drawImage(getImage(), x, y, c);
		}
		else
		{
			g.drawImage(getImage(), x, y, width, height, c);
		}
	}

	public void setScaledSize(int width,
							  int height)
	{
		this.width = width;
		this.height = height;
	}
}
