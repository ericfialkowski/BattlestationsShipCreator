package com.ericski.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_FRACTIONALMETRICS;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_ON;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import static java.awt.geom.AffineTransform.getRotateInstance;
import static java.awt.geom.AffineTransform.getTranslateInstance;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import static java.lang.Math.PI;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;
import static java.lang.Thread.yield;
import javax.swing.JComponent;

public class InfiniteProgressPanel extends JComponent implements MouseListener
{

	private static final long serialVersionUID = 1L;
	protected Area[] ticker = null;
	protected Thread animation = null;
	protected boolean started = false;
	protected int alphaLevel = 0;
	protected int rampDelay = 300;
	protected float shield = 0.70f;
	protected String text = "";
	protected int barsCount = 14;
	protected float fps = 15.0f;

	protected RenderingHints hints = null;

	public InfiniteProgressPanel()
	{
		this("");
	}

	public InfiniteProgressPanel(String text)
	{
		this(text, 14);
	}

	public InfiniteProgressPanel(String text, int barsCount)
	{
		this(text, barsCount, 0.70f);
	}

	public InfiniteProgressPanel(String text, int barsCount, float shield)
	{
		this(text, barsCount, shield, 15.0f);
	}

	public InfiniteProgressPanel(String text, int barsCount, float shield, float fps)
	{
		this(text, barsCount, shield, fps, 300);
	}

	public InfiniteProgressPanel(String text, int barsCount, float shield, float fps, int rampDelay)
	{
		this.text = text;
		this.rampDelay = rampDelay >= 0 ? rampDelay : 0;
		this.shield = shield >= 0.0f ? shield : 0.0f;
		this.fps = fps > 0.0f ? fps : 15.0f;
		this.barsCount = barsCount > 0 ? barsCount : 14;

		this.hints = new RenderingHints(KEY_RENDERING, VALUE_RENDER_QUALITY);
		this.hints.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		this.hints.put(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON);
	}

	public void setText(String text)
	{
		repaint();
		this.text = text;
	}

	public String getText()
	{
		return text;
	}

	public void start()
	{
		addMouseListener(this);
		setVisible(true);
		ticker = buildTicker();
		animation = new Thread(new Animator(true));
		animation.start();
	}

	public void stop()
	{
		if (animation != null)
		{
			animation.interrupt();
			animation = null;
			animation = new Thread(new Animator(false));
			animation.start();
		}
	}

	public void interrupt()
	{
		if (animation != null)
		{
			animation.interrupt();
			animation = null;

			removeMouseListener(this);
			setVisible(false);
		}
	}

	@Override
	public void paintComponent(Graphics g)
	{
		if (started)
		{
			int width = getWidth();
			//int height = getHeight();

			double maxY = 0.0;

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHints(hints);

			g2.setColor(new Color(255, 255, 255, (int) (alphaLevel * shield)));
			g2.fillRect(0, 0, getWidth(), getHeight());

			for (int i = 0; i < ticker.length; i++)
			{
				int channel = 224 - 128 / (i + 1);
				g2.setColor(new Color(channel, channel, channel, alphaLevel));
				g2.fill(ticker[i]);

				Rectangle2D bounds = ticker[i].getBounds2D();
				if (bounds.getMaxY() > maxY)
				{
					maxY = bounds.getMaxY();
				}
			}

			if (text != null && text.length() > 0)
			{
				FontRenderContext context = g2.getFontRenderContext();
				TextLayout layout = new TextLayout(text, getFont(), context);
				Rectangle2D bounds = layout.getBounds();
				g2.setColor(getForeground());
				layout.draw(g2, (float) (width - bounds.getWidth()) / 2,
							(float) (maxY + layout.getLeading() + 2 * layout.getAscent()));
			}
		}
	}

	private Area[] buildTicker()
	{
		Area[] builtTicker = new Area[barsCount];
		Point2D.Double center = new Point2D.Double((double) getWidth() / 2, (double) getHeight() / 2);
		double fixedAngle = 2.0 * PI / ((double) barsCount);

		for (double i = 0.0; i < (double) barsCount; i++)
		{
			Area primitive = buildPrimitive();

			AffineTransform toCenter = getTranslateInstance(center.getX(), center.getY());
			AffineTransform toBorder = getTranslateInstance(45.0, -6.0);
			AffineTransform toCircle = getRotateInstance(-i * fixedAngle, center.getX(), center.getY());

			AffineTransform toWheel = new AffineTransform();
			toWheel.concatenate(toCenter);
			toWheel.concatenate(toBorder);

			primitive.transform(toWheel);
			primitive.transform(toCircle);

			builtTicker[(int) i] = primitive;
		}

		return builtTicker;
	}

	private Area buildPrimitive()
	{
		Rectangle2D.Double body = new Rectangle2D.Double(6, 0, 30, 12);
		Ellipse2D.Double head = new Ellipse2D.Double(0, 0, 12, 12);
		Ellipse2D.Double tail = new Ellipse2D.Double(30, 0, 12, 12);

		Area tick = new Area(body);
		tick.add(new Area(head));
		tick.add(new Area(tail));

		return tick;
	}

	protected class Animator implements Runnable
	{
		private boolean rampUp = true;

		protected Animator(boolean rampUp)
		{
			this.rampUp = rampUp;
		}

		@Override
		public void run()
		{
			Point2D.Double center = new Point2D.Double((double) getWidth() / 2, (double) getHeight() / 2);
			double fixedIncrement = 2.0 * PI / ((double) barsCount);
			AffineTransform toCircle = getRotateInstance(fixedIncrement, center.getX(), center.getY());

			long start = currentTimeMillis();
			if (rampDelay == 0)
			{
				alphaLevel = rampUp ? 255 : 0;
			}

			started = true;
			boolean inRamp = rampUp;

			while (!interrupted())
			{
				if (!inRamp)
				{
					for (Area ticker1 : ticker)
					{
						ticker1.transform(toCircle);
					}
				}

				repaint();

				if (rampUp)
				{
					if (alphaLevel < 255)
					{
						alphaLevel = (int) (255 * (currentTimeMillis() - start) / rampDelay);
						if (alphaLevel >= 255)
						{
							alphaLevel = 255;
							inRamp = false;
						}
					}
				}
				else if (alphaLevel > 0)
				{
					alphaLevel = (int) (255 - (255 * (currentTimeMillis() - start) / rampDelay));
					if (alphaLevel <= 0)
					{
						alphaLevel = 0;
						break;
					}
				}

				try
				{
					sleep(inRamp ? 10 : (int) (1000 / fps));
				}
				catch (InterruptedException ie)
				{
					break;
				}
				yield();
			}

			if (!rampUp)
			{
				started = false;
				repaint();

				setVisible(false);
				removeMouseListener(InfiniteProgressPanel.this);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}
}
