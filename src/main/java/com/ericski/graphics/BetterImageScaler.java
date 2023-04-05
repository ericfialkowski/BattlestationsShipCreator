package com.ericski.graphics;

import static java.awt.Color.WHITE;
import java.awt.Graphics2D;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC;
import static java.awt.Transparency.OPAQUE;
import java.awt.geom.AffineTransform;
import static java.awt.geom.AffineTransform.getTranslateInstance;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class BetterImageScaler
{

	public static BufferedImage getScaledImage(BufferedImage src, int w, int h)
	{
		BufferedImage dest = new BufferedImage(w, h, src.getType());
		Graphics2D g2 = dest.createGraphics();
		g2.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
		g2.fillRect(0, 0, w, h);
		double xScale = (double) w / src.getWidth();
		double yScale = (double) h / src.getHeight();
		double scale = max(xScale, yScale);
		double x = (w - scale * src.getWidth()) / 2;
		double y = (h - scale * src.getHeight()) / 2;
		AffineTransform at = getTranslateInstance(x, y);
		at.scale(scale, scale);
		g2.drawRenderedImage(src, at);
		g2.dispose();
		return dest;
	}

	public static BufferedImage getScaledImageToFit(BufferedImage src, int w, int h)
	{
		BufferedImage dest = new BufferedImage(w, h, src.getType());
		Graphics2D g2 = dest.createGraphics();
		g2.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
		// fill background for scale to fit  (a better color choice/options might be handy)
		g2.setPaint(WHITE);
		g2.fillRect(0, 0, w, h);
		double xScale = (double) w / src.getWidth();
		double yScale = (double) h / src.getHeight();
		double scale = min(xScale, yScale);    // scale to fit
		double x = (w - scale * src.getWidth()) / 2;
		double y = (h - scale * src.getHeight()) / 2;
		AffineTransform at = getTranslateInstance(x, y);
		at.scale(scale, scale);
		g2.drawRenderedImage(src, at);
		g2.dispose();
		return dest;
	}

	/**
	 * Convenience method that returns a scaled instance of the provided BufferedImage.
	 *
	 *
	 * @param img the original image to be scaled
	 * @param targetWidth the desired width of the scaled instance, in pixels
	 * @param targetHeight the desired height of the scaled instance, in pixels
	 * @param hint one of the rendering hints that corresponds to RenderingHints.KEY_INTERPOLATION (e.g.
	 * RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, RenderingHints.VALUE_INTERPOLATION_BILINEAR,
	 * RenderingHints.VALUE_INTERPOLATION_BICUBIC)
	 * @param progressiveBilinear if true, this method will use a multi-step scaling technique that provides higher
	 * quality than the usual one-step technique (only useful in down-scaling cases, where targetWidth or targetHeight
	 * is smaller than the original dimensions)
	 * @return a scaled version of the original BufferedImage
	 */
	public static BufferedImage getFasterScaledInstance(BufferedImage img,
														int targetWidth, int targetHeight, Object hint,
														boolean progressiveBilinear)
	{
		int type = (img.getTransparency() == OPAQUE)
				   ? TYPE_INT_RGB : TYPE_INT_ARGB;
		BufferedImage ret = img;
		BufferedImage scratchImage = null;
		Graphics2D g2 = null;
		int w, h;
		int prevW = ret.getWidth();
		int prevH = ret.getHeight();
		boolean isTranslucent = img.getTransparency() != OPAQUE;

		if (progressiveBilinear)
		{
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		}
		else
		{
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do
		{
			if (progressiveBilinear && w > targetWidth)
			{
				w /= 2;
				if (w < targetWidth)
				{
					w = targetWidth;
				}
			}

			if (progressiveBilinear && h > targetHeight)
			{
				h /= 2;
				if (h < targetHeight)
				{
					h = targetHeight;
				}
			}

			if (scratchImage == null || isTranslucent)
			{
				// Use a single scratch buffer for all iterations
				// and then copy to the final, correctly-sized image
				// before returning
				scratchImage = new BufferedImage(w, h, type);
				g2 = scratchImage.createGraphics();
			}
			g2.setRenderingHint(KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);
			prevW = w;
			prevH = h;

			ret = scratchImage;
		}
		while (w != targetWidth || h != targetHeight);

		if (g2 != null)
		{
			g2.dispose();
		}

		// If we used a scratch buffer that is larger than our target size,
		// create an image of the right size and copy the results into it
		if (targetWidth != ret.getWidth() || targetHeight != ret.getHeight())
		{
			scratchImage = new BufferedImage(targetWidth, targetHeight, type);
			g2 = scratchImage.createGraphics();
			g2.drawImage(ret, 0, 0, null);
			g2.dispose();
			ret = scratchImage;
		}

		return ret;
	}

}
