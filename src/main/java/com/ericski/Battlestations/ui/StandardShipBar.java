package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.Ship;
import com.ericski.Battlestations.ShipFactory;
import com.l2fprod.common.swing.JOutlookBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicLabelUI;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXCollapsiblePane.Direction;

public class StandardShipBar extends JPanel
{
	private static final String TOGGLEACTION = "TOGGLEACTION";
	private static final long serialVersionUID = 1L;
	private HashMap<String, Ship> shipMap;
	private HashMap<String, JPanel> panelMap;
	ShipCreatorPanel parentPanel;
	JButton hiderButton;
	JOutlookBar moduleBar;
	JXCollapsiblePane modulePane;
	JPanel hider;
	boolean collapsed = false;

	@Override
	public Dimension getPreferredSize()
	{
		if (collapsed)
		{
			return hider.getPreferredSize();
		}
		else
		{
			Dimension dim1 = hider.getPreferredSize();
			Dimension dim2 = modulePane.getPreferredSize();

			return new Dimension(dim1.width + dim2.width, dim1.height);
		}
	}

	public StandardShipBar(final ShipCreatorPanel parentPanel)
	{
		super(new BorderLayout());
		this.parentPanel = parentPanel;
		hider = new JGradPanel(Color.BLACK.brighter().brighter(), Color.WHITE);
		hider.setLayout(new BoxLayout(hider, BoxLayout.Y_AXIS));
		hiderButton = new JButton(">>");
		hiderButton.setFont(hiderButton.getFont().deriveFont(6));
		hiderButton.setMargin(new Insets(0, 0, 0, 0));
		hider.add(hiderButton);
		hider.add(Box.createVerticalStrut(5));

		hider.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent evt)
			{
				if (!evt.isPopupTrigger())
				{
					showHideShipBar();
				}
			}
		});

		JLabel label = new JLabel("Standard Ships");
		label.setForeground(Color.WHITE);
		label.setUI(new VerticalLabelUI(true));
		hider.add(label);
		label.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent evt)
			{
				if (!evt.isPopupTrigger())
				{
					showShipBar();
				}
			}
		});

		hider.add(Box.createVerticalStrut(10));

		add(hider, BorderLayout.EAST);
		hiderButton.setActionCommand(TOGGLEACTION);
		hiderButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				showHideShipBar();
			}
		});

		moduleBar = new JOutlookBar(JTabbedPane.RIGHT);

		List<Ship> ships = ShipFactory.INSTANCE.getStandardTemplates();
		Collections.sort(ships);
		shipMap = new HashMap<>();
		panelMap = new HashMap<>();

		for (Ship ship : ships)
		{
			String species = ship.getSpecies();

			if (!panelMap.containsKey(species))
			{
				JPanel panel = new JGradPanel(Color.GRAY.brighter().brighter());
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				panelMap.put(species, panel);
				JScrollPane allScroller = new JScrollPane(panel);
				allScroller.setPreferredSize(new Dimension(115, 256));
				allScroller.setMinimumSize(new Dimension(100, 128));
				moduleBar.addTab(species, allScroller);
			}
			JPanel panel = panelMap.get(species);

			panel.add(Box.createVerticalStrut(3));
			String key = ship.toString();
			shipMap.put(key, ship);
			JButton button = new JButton(ship.getName(), new ImageIcon(ship.generateThumbnailImage()));
			button.setMargin(new Insets(1, 1, 1, 1));
			button.setActionCommand(key);
			button.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent ae)
				{
					Ship ship = shipMap.get(ae.getActionCommand());
					if (ship != null)
					{
						parentPanel.setShip(new Ship(ship));
					}
				}
			});

			button.setVerticalTextPosition(AbstractButton.BOTTOM);
			button.setHorizontalTextPosition(AbstractButton.CENTER);
			JPanel h = new JPanel(new FlowLayout(FlowLayout.CENTER));
			h.setOpaque(false);
			h.add(button);
			panel.add(h);

		}
		modulePane = new JXCollapsiblePane(Direction.RIGHT);
		modulePane.setLayout(new BorderLayout());
		modulePane.setCollapsed(false);
		modulePane.add(moduleBar, BorderLayout.CENTER);

		modulePane.addPropertyChangeListener("collapsed", new PropertyChangeListener()
										 {
											 @Override
											 public void propertyChange(PropertyChangeEvent pce)
											 {

												 if ((boolean) pce.getNewValue())
												 {
													 //case "collapsed":
													 hiderButton.setText("<<");
													 collapsed = true;
												 }
												 else
												 {
													 //case "expanded":
													 hiderButton.setText(">>");
													 collapsed = false;
												 }
											 }
										 });
		add(modulePane, BorderLayout.CENTER);
	}

	private void showHideShipBar()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				if (modulePane.isCollapsed())
				{
					collapsed = false;
					hiderButton.setText("<>");
					modulePane.setCollapsed(false);
				}
				else
				{
					hiderButton.setText("<>");
					modulePane.setCollapsed(true);
				}
			}
		});
	}

	private void showShipBar()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				if (modulePane.isCollapsed())
				{
					collapsed = false;
					hiderButton.setText("<>");
					modulePane.setCollapsed(false);
				}
			}
		});
	}
}

class VerticalLabelUI extends BasicLabelUI
{
	static
	{
		labelUI = new VerticalLabelUI(false);
	}
	protected boolean clockwise;

	VerticalLabelUI(boolean clockwise)
	{
		super();
		this.clockwise = clockwise;
	}

	@Override
	public Dimension getPreferredSize(JComponent c)
	{
		Dimension dim = super.getPreferredSize(c);
		return new Dimension(dim.height, dim.width);
	}
	private static Rectangle paintIconR = new Rectangle();
	private static Rectangle paintTextR = new Rectangle();
	private static Rectangle paintViewR = new Rectangle();
	private static Insets paintViewInsets = new Insets(0, 0, 0, 0);

	@Override
	public void paint(Graphics g, JComponent c)
	{
		JLabel label = (JLabel) c;
		String text = label.getText();
		Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

		if ((icon == null) && (text == null))
		{
			return;
		}

		FontMetrics fm = g.getFontMetrics();
		paintViewInsets = c.getInsets(paintViewInsets);

		paintViewR.x = paintViewInsets.left;
		paintViewR.y = paintViewInsets.top;

		// Use inverted height & width
		paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
		paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);

		paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
		paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

		String clippedText
			   = layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);

		Graphics2D g2 = (Graphics2D) g;
		AffineTransform tr = g2.getTransform();
		if (clockwise)
		{
			g2.rotate(Math.PI / 2);
			g2.translate(0, -c.getWidth());
		}
		else
		{
			g2.rotate(-Math.PI / 2);
			g2.translate(-c.getHeight(), 0);
		}

		if (icon != null)
		{
			icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
		}

		if (text != null)
		{
			int textX = paintTextR.x;
			int textY = paintTextR.y + fm.getAscent();

			if (label.isEnabled())
			{
				paintEnabledText(label, g, clippedText, textX, textY);
			}
			else
			{
				paintDisabledText(label, g, clippedText, textX, textY);
			}
		}

		g2.setTransform(tr);
	}
}

class JGradPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	Color color1;
	Color color2;

	public JGradPanel()
	{
		color1 = Color.WHITE.darker();
		color2 = Color.WHITE;
	}

	public JGradPanel(Color color)
	{
		this.color1 = color.darker().darker();
		this.color2 = color.brighter().brighter();
	}

	public JGradPanel(Color color1, Color color2)
	{
		this.color1 = color1;
		this.color2 = color2;
	}

	@Override
	public boolean isOpaque()
	{
		return true;
	}

	@Override
	public void setBackground(Color color)
	{
		this.color1 = color.darker().darker();
		this.color2 = color.brighter().brighter();
	}

	@Override
	public void paintComponent(Graphics _g)
	{
		Graphics2D g = (Graphics2D) _g;

		Rectangle bounds = getBounds();

		// Set Paint for filling Shape
		Paint gradientPaint = new GradientPaint(0, 0, color1, bounds.width, bounds.height, color2);
		g.setPaint(gradientPaint);
		g.fillRect(0, 0, bounds.width, bounds.height);
	}
}
