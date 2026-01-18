package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.Module;
import com.ericski.Battlestations.ModuleFactory;
import com.ericski.ui.ImageIconScalable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

public class ModuleSelectionPanel extends JPanel
{

	public static final int DEFAULTSIZE = 80;
	private static final boolean DEBUG = false;
	private static final String IMAGEACTION = "IMAGEACTION";
	private static final String DELETEACTION = "DELETE";
	private static final long serialVersionUID = 1L;
	private static final String ROTATECWACTION = "ROTATECWACTION";
	private static final String ROTATECCWACTION = "ROTATECCWACTION";
	private static final String UPGRADEDACTION = "UPGRADEDACTION";
	private static final String ZOOMACTION = "ZOOMDACTION";
	private Image image;
	private int width = DEFAULTSIZE;
	private int height = DEFAULTSIZE;
	private int clickXOffset = 0;
	private int clickYOffset = 0;
	private long angle;
	private Dimension dim;
	private JPopupMenu menu;
	private boolean canRotate = false;
	private boolean draggable = false;
	private int shipX;
	private int shipY;
	private Module module = null;
	private int scrollWheelie = 0;
	private boolean showTooltip;
	private JMenuItem nameItem;
	private boolean showZoom = true;

	public Module getModule()
	{
		return module;
	}

	public int getShipX()
	{
		return shipX;
	}

	public void setShipX(int shipX)
	{
		this.shipX = shipX;
	}

	public int getShipY()
	{
		return shipY;
	}

	public void setShipY(int shipY)
	{
		this.shipY = shipY;
	}

	public boolean isDraggable()
	{
		return draggable;
	}

	public void setDraggable(boolean draggable)
	{
		this.draggable = draggable;
	}

	public void setShowToolTip(boolean showToolTip)
	{
		this.showTooltip = showToolTip;
		if (showToolTip)
		{
			setToolTipText(module.getDescription());
		}
	}

	public boolean isShowToolTip()
	{
		return showTooltip;
	}

	public ModuleSelectionPanel()
	{
		this(0, 0);
	}

	public ModuleSelectionPanel(boolean showZoom)
	{
		this(0, 0, true, showZoom);
	}

	public ModuleSelectionPanel(int shipX, int shipY)
	{
		this(shipX, shipY, true, true);
	}

	public ModuleSelectionPanel(int shipX, int shipY, boolean addMenu)
	{
		this(shipX, shipY, addMenu, true);
	}

	public ModuleSelectionPanel(int shipX, int shipY, boolean addMenu, boolean showZoom)
	{
		super();
		this.shipX = shipX;
		this.shipY = shipY;
		module = ModuleFactory.getBlankModule();
		this.showZoom = showZoom;

		if (shipX == 3 || shipY == 3)
		{
			setBackground(new Color(225, 225, 225));
		}
		else
		{
			setBackground(Color.WHITE);
		}
		dim = new Dimension(width, height);

		if (addMenu)
		{
			ActionListener actionListener = new Actioner();

			setFocusable(true);
			addKeyListener(new KeyHandler());
			addMouseWheelListener(new Mouser());

			menu = new JPopupMenu();

			nameItem = new JMenuItem(module.getDescription());
			menu.add(nameItem);
			menu.add(new JSeparator());

			URL url;
			ImageIcon icon;

			if (showZoom)
			{
				JMenuItem zoomMi = new JMenuItem("Zoom");
				url = getClass().getResource("/toolbarButtonGraphics/general/Zoom24.gif");
				icon = new ImageIcon(url);
				zoomMi.setIcon(icon);
				zoomMi.setActionCommand(ZOOMACTION);
				zoomMi.addActionListener(actionListener);
				menu.add(zoomMi);

				menu.add(new JSeparator());
			}
			JMenuItem rotateCW = new JMenuItem("Rotate Right");
			rotateCW.setActionCommand(ROTATECWACTION);
			rotateCW.addActionListener(actionListener);
			menu.add(rotateCW);

			JMenuItem rotateCCW = new JMenuItem("Rotate Left");
			rotateCCW.setActionCommand(ROTATECCWACTION);
			rotateCCW.addActionListener(actionListener);
			menu.add(rotateCCW);

			menu.add(new JSeparator());

			JMenuItem upgraded = new JMenuItem("Upgraded");
			upgraded.setActionCommand(UPGRADEDACTION);
			upgraded.addActionListener(actionListener);
			menu.add(upgraded);

			menu.add(new JSeparator());

			JMenuItem zeroDegree = new JMenuItem("Face Forward");
			url = getClass().getResource("/toolbarButtonGraphics/navigation/Up24.gif");
			icon = new ImageIcon(url);
			zeroDegree.setIcon(icon);
			zeroDegree.setActionCommand("0");
			zeroDegree.addActionListener(actionListener);
			menu.add(zeroDegree);

			JMenuItem ninetyDegree = new JMenuItem("Face Right");
			url = getClass().getResource("/toolbarButtonGraphics/navigation/Forward24.gif");
			icon = new ImageIcon(url);
			ninetyDegree.setIcon(icon);
			ninetyDegree.setActionCommand("90");
			ninetyDegree.addActionListener(actionListener);
			menu.add(ninetyDegree);

			JMenuItem oneeightyDegree = new JMenuItem("Face Backwards");
			url = getClass().getResource("/toolbarButtonGraphics/navigation/Down24.gif");
			icon = new ImageIcon(url);
			oneeightyDegree.setIcon(icon);
			oneeightyDegree.setActionCommand("180");
			oneeightyDegree.addActionListener(actionListener);
			menu.add(oneeightyDegree);

			JMenuItem twoseventyDegree = new JMenuItem("Face Left");
			url = getClass().getResource("/toolbarButtonGraphics/navigation/Back24.gif");
			icon = new ImageIcon(url);
			twoseventyDegree.setIcon(icon);
			twoseventyDegree.setActionCommand("270");
			twoseventyDegree.addActionListener(actionListener);
			menu.add(twoseventyDegree);

			menu.add(new JSeparator());

			JMenuItem deleteItem = new JMenuItem("Delete");
			deleteItem.setActionCommand(DELETEACTION);
			deleteItem.addActionListener(actionListener);
			menu.add(deleteItem);

			menu.add(new JSeparator());

			JMenu subMenu = new JMenu("Modules");
			menu.add(subMenu);

			for (Module m : ModuleFactory.INSTANCE.getAllModules())
			{
				if (!m.isBlankModule())
				{
					subMenu.add(createMenuItem(m, Color.WHITE, actionListener));
				}
			}
		}

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				canRotate = false;
				ModuleSelectionPanelActionController.getInstance().removeActee((ModuleSelectionPanel) e.getComponent());
				clickXOffset = 0;
				clickYOffset = 0;
				repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				ModuleSelectionPanelActionController.getInstance().setActee((ModuleSelectionPanel) e.getComponent());
				canRotate = true;
				requestFocusInWindow();
				repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (menu != null)
				{
					if (e.getClickCount() == 2)
					{
						//System.out.print(e.getX() + " X " + getWidth());
						if (e.getX() > (getWidth() / 3))
						{
							//System.out.println(" Rotate CW");
							setAngle((getAngle() + 90) % 360);
						}
						else if (e.getX() < (getWidth() / 3))
						{
							//System.out.println(" Rotate CCW");
							setAngle((getAngle() - 90) % 360);
						}
					}
				}

			}

			@Override
			public void mousePressed(MouseEvent evt)
			{
				if (menu != null && evt.isPopupTrigger())
				{
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
				else
				{
					ModuleSelectionPanelActionController.getInstance().setActor((ModuleSelectionPanel) evt.getComponent(), evt.isAltDown());
					clickXOffset = evt.getX();
					clickYOffset = evt.getY();
				}

			}

			@Override
			public void mouseReleased(MouseEvent evt)
			{
				if (menu != null && evt.isPopupTrigger())
				{
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
				else
				{
					ModuleSelectionPanelAction action = ModuleSelectionPanelActionController.getInstance().getAction();
					if (action == ModuleSelectionPanelAction.ROTATE)
					{
						dragRotate(evt.getX(), evt.getY());
					}
					else if (action == ModuleSelectionPanelAction.SWAP)
					{
						swap();
					}
					else if (action == ModuleSelectionPanelAction.DELETE)
					{
						ModuleSelectionPanelActionController.getInstance().clearAction();
						setModule(ModuleFactory.getBlankModule());
					}
					else if (action == ModuleSelectionPanelAction.ADD)
					{
						ModuleSelectionPanelActionController.getInstance().clearAction();
						ModuleSelectionPanelActionController.getInstance().getActee().setModule(ModuleSelectionPanelActionController.getInstance().getActor().getModule().copy());

						repaint();
					}
					clickXOffset = 0;
					clickYOffset = 0;
				}
			}
		});
	}

	private void rotateCW()
	{
		setAngle((getAngle() + 90) % 360);
	}

	private void rotateCCW()
	{
		setAngle((getAngle() - 90) % 360);
	}

	private void swap()
	{
		ModuleSelectionPanel acteePanel = ModuleSelectionPanelActionController.getInstance().getActee();
		ModuleSelectionPanel actorPanel = ModuleSelectionPanelActionController.getInstance().getActor();
		Module temp = acteePanel.getModule();
		acteePanel.setModule(actorPanel.getModule());
		actorPanel.setModule(temp);
		ModuleSelectionPanelActionController.getInstance().clearAction();
		getParent().repaint();
	}

	private void dragRotate(int x, int y)
	{
		if (canRotate && (image != null))
		{
			boolean xChangedCW = (clickYOffset < (getHeight() / 2)) ? (clickXOffset - x) < 0 : (clickXOffset - x) > 0;
			boolean yChangedCW = (clickXOffset < (getWidth() / 2)) ? (clickYOffset - y) > 0 : (clickYOffset - y) < 0;

			int xMagnitude = Math.abs(clickXOffset - x);
			int yMagnitude = Math.abs(clickYOffset - y);

			if ((xMagnitude > getWidth() / 8.0) || (yMagnitude > getHeight() / 8.0))
			{
				if (xMagnitude > yMagnitude)
				{
					if (xChangedCW)
					{
						rotateCW();
					}
					else
					{
						rotateCCW();
					}
				}
				else if (yChangedCW)
				{
					rotateCW();
				}
				else
				{
					rotateCCW();
				}
			}
		}
		ModuleSelectionPanelActionController.getInstance().clearAction();
	}

	public Image getImage()
	{
		return image;
	}

	public void setImage(String path)
	{
		URL url = this.getClass().getResource(path);
		image = Toolkit.getDefaultToolkit().getImage(url);
		this.repaint();
	}

	public void setImage(Image image)
	{
		this.image = image;
		this.repaint();
	}

	public void setModule(Module module)
	{
		this.module = module.copy();
		if (showTooltip)
		{
			setToolTipText(module.getDescription());
		}
		if (module.isBlankModule())
		{
			deleteModule();
		}
		else
		{
			if (nameItem != null)
			{
				nameItem.setText(module.getDescription());
			}
			setAngle(module.getRotation());
			Image loadedImage = module.getNonRotatedImage();
			setImage(loadedImage);
		}
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (image == null)
		{
			if (menu != null && canRotate)
			{
				g.setColor(Color.RED);
			}
			g.drawRect(0, 0, width - 1, height - 1);
			g.drawRect(1, 1, width - 2, height - 2);
			if (DEBUG)
			{
				g.drawString(Integer.toString(shipY * 7 + shipX), getWidth() / 2, getHeight() / 2);
			}
		}
		else
		{
			Graphics2D g2 = (Graphics2D) g;
			g2.rotate(Math.toRadians(module.getRotation()), width / 2, height / 2);
			g2.drawImage(image, 0, 0, width, height, this);
			if (module.isUpgraded())
			{
				int x = getWidth() / 2 - 5;
				int y = getHeight() / 2 - 5;
				g2.setColor(Color.BLACK);
				g2.drawString("UPG", x, y);
				g2.setColor(Color.WHITE);
				g2.drawString("UPG", x - 1, y - 1);
			}
			if (menu != null && canRotate)
			{
				g2.setColor(Color.RED);
				g2.drawRect(0, 0, width - 1, height - 1);
				g2.drawRect(1, 1, width - 2, height - 2);
			}
			g2.dispose();
		}
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		dim = new Dimension(width, height);
		this.height = height;
	}

	@Override
	public Dimension getMaximumSize()
	{
		return dim;
	}

	@Override
	public Dimension getMinimumSize()
	{
		return dim;
	}

	@Override
	public Dimension getPreferredSize()
	{
		return dim;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		dim = new Dimension(width, height);
		this.width = width;
	}

	public void setAngle(long degrees)
	{
		if (degrees < 0)
		{
			this.angle = 360 + degrees;
		}
		else
		{
			this.angle = degrees;
		}

		if (angle != module.getRotation())
		{
			module.setRotation((int) angle);
			repaint();
		}
	}

	public long getAngle()
	{
		return angle;
	}

	private void deleteModule()
	{
		setImage((Image) null);
		module = ModuleFactory.getBlankModule();
		if (nameItem != null)
		{
			nameItem.setText(module.getDescription());
		}
	}

	private void zoom()
	{
		if (!module.isBlankModule())
		{
			ModuleZoomDialog mzd = new ModuleZoomDialog(module);
			mzd.setModal(true);
			int x = getLocationOnScreen().x - (mzd.getWidth() / 2);
			int y = getLocationOnScreen().y - (mzd.getHeight() / 2);
			mzd.setLocation(x, y);
			mzd.setVisible(true);
			setAngle(module.getRotation());
		}
	}

	private JMenuItem createMenuItem(Module module, Color color, ActionListener actionListener)
	{
		JMenuItem menuItem = new JMenuItem();
		menuItem.setBackground(color);
		ImageIconScalable icon = new ImageIconScalable(module.getNonRotatedImage());
		icon.setScaledSize(32, 32);
		menuItem.setIcon(icon);
		menuItem.setActionCommand(IMAGEACTION + "_" + module.getName());
		menuItem.addActionListener(actionListener);
		return menuItem;
	}

	private class Actioner implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent ae)
		{

			String action = ae.getActionCommand();
			if (action.equals(DELETEACTION))
			{
				deleteModule();
			}
			else if (action.startsWith(IMAGEACTION))
			{
				if (ae.getSource() instanceof JMenuItem)
				{

					JMenuItem src = (JMenuItem) ae.getSource();
					if (src.getIcon() != null)
					{
						setImage(((ImageIcon) src.getIcon()).getImage());
					}
					String moduleName = action.substring(IMAGEACTION.length() + 1);
					module.setName(moduleName);
				}
			}
			else if (action.equals(ROTATECWACTION))
			{
				rotateCW();
				repaint();
			}
			else if (action.equals(ROTATECCWACTION))
			{
				rotateCCW();
				repaint();
			}
			else if (action.equals(UPGRADEDACTION))
			{
				module.setUpgraded(!module.isUpgraded());
				repaint();
			}
			else if (action.equals(ZOOMACTION))
			{
				zoom();
			}
			else
			{
				try
				{
					long rotate = Long.parseLong(ae.getActionCommand());
					setAngle(rotate);
				}
				catch (NumberFormatException iggy)
				{
				}
			}
		}
	}

	private class Mouser implements MouseWheelListener
	{

		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			if (e.getWheelRotation() < 0)
			{
				if (scrollWheelie > 0)
				{
					scrollWheelie = 0;
				}
				if (--scrollWheelie < -2)
				{
					rotateCCW();
					scrollWheelie = 0;
				}
			}
			else if (e.getWheelRotation() > 0)
			{
				if (scrollWheelie < 0)
				{
					scrollWheelie = 0;
				}
				if (++scrollWheelie > 2)
				{
					rotateCW();
					scrollWheelie = 0;
				}
			}

		}
	}

	private class KeyHandler implements KeyListener
	{

		@Override
		public void keyPressed(KeyEvent e)
		{
			// no op
		}

		@Override
		public void keyReleased(KeyEvent e)
		{
			int keyCode = e.getKeyCode();
			switch (keyCode)
			{
				case KeyEvent.VK_DELETE:
					deleteModule();
					break;
				case KeyEvent.VK_RIGHT:
					setAngle(90);
					break;
				case KeyEvent.VK_LEFT:
					setAngle(270);
					break;
				case KeyEvent.VK_UP:
					setAngle(0);
					break;
				case KeyEvent.VK_DOWN:
					setAngle(180);
					break;
				case KeyEvent.VK_EQUALS:
					rotateCW();
					break;
				case KeyEvent.VK_MINUS:
					rotateCCW();
					break;
				case KeyEvent.VK_Z:
					if (showZoom)
					{
						zoom();
					}
					break;
				default:
					break;
			}

		}

		@Override
		public void keyTyped(KeyEvent e)
		{
			// no op
		}
	}
}

class ModuleSelectionPanelActionController
{

	private static class SingletonHolder
	{

		private final static ModuleSelectionPanelActionController INSTANCE = new ModuleSelectionPanelActionController();
	}

	public static ModuleSelectionPanelActionController getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	ModuleSelectionPanel actee;
	ModuleSelectionPanel actor;
	ModuleSelectionPanelAction action;

	public void setAction(ModuleSelectionPanelAction action)
	{
		this.action = action;
	}

	public void clearAction()
	{
		action = ModuleSelectionPanelAction.NONE;
	}

	public ModuleSelectionPanel getActee()
	{
		return actee;
	}

	public void setActee(ModuleSelectionPanel actee)
	{
		this.actee = actee;
	}

	public void removeActee(ModuleSelectionPanel actee)
	{
		if (this.actee == actee)
		{
			this.actee = null;
		}
	}

	public ModuleSelectionPanel getActor()
	{
		return actor;
	}

	public void setActor(ModuleSelectionPanel actor, boolean modifiedAction)
	{
		this.actor = actor;

		// if we have an actor
		if (actor != null)
		{
			if (modifiedAction && (actor.getShipX() != -1 && actor.getShipY() != -1))
			{
				// alt was clicked also, and we're in the normal ship area....
				action = ModuleSelectionPanelAction.PREADD;
			}
			else if (actor.getShipX() == -1 && actor.getShipY() == -1)
			{
				// alt wasn't clicked, we aren't in the normal ship area (i.e. coming from the side bar)
				action = ModuleSelectionPanelAction.PREADD;
			}
		}
	}

	public ModuleSelectionPanelAction getAction()
	{
		if (actee == null || (actee.getShipX() == -1 && actee.getShipY() == -1))
		{
			action = ModuleSelectionPanelAction.NONE;
		}
		else if (actor == actee)
		{
			if (action == ModuleSelectionPanelAction.PREDELETE)
			{
				action = ModuleSelectionPanelAction.DELETE;
			}
			else
			{
				action = ModuleSelectionPanelAction.ROTATE;
			}
		}
		else if (action == ModuleSelectionPanelAction.PREADD)
		{
			action = ModuleSelectionPanelAction.ADD;
		}
		else
		{
			action = ModuleSelectionPanelAction.SWAP;
		}
		return action;
	}
}
