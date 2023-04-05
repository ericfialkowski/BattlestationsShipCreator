package com.ericski.Battlestations;

import static com.ericski.Battlestations.ModuleFactory.INSTANCE;
import static com.ericski.Battlestations.ModuleFactory.getBlankModule;
import static java.awt.AlphaComposite.Src;
import java.awt.Color;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.awt.Font.BOLD;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
import static java.awt.Transparency.BITMASK;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import static java.lang.Integer.parseInt;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static org.apache.logging.log4j.LogManager.getLogger;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import static org.jdom2.output.Format.TextMode.TRIM;
import static org.jdom2.output.Format.getPrettyFormat;
import org.jdom2.output.XMLOutputter;

public final class Ship implements Comparable<Ship>
{
	private final static Logger logger = getLogger(Ship.class);
	String name = "";
	String species = "Generic";
	int size = 3;
	int damageSize = 3;
	private List<String> notes;

	Map<Integer, Module> modules = new HashMap<>();

	public Ship()
	{
		name = "";
		species = "";
		size = 3;
	}

	public List<String> getNotes()
	{
		if (notes == null)
		{
			return new ArrayList<>();
		}
		return notes;
	}

	public String getNotesAsString()
	{
		StringBuilder buf = new StringBuilder();
		if (notes != null && notes.size() > 0)
		{
			for (String note : notes)
			{
				buf.append(note);
				buf.append('\n');
			}
			buf.deleteCharAt(buf.length() - 1); // kill the last newline
		}
		return buf.toString();
	}

	public void setNotes(LinkedList<String> notes)
	{
		this.notes = notes;
	}

	public void setNotes(String notesString)
	{
		notes = new ArrayList<>();
		if (notesString != null)
		{
			notes.addAll(asList(notesString.split("\n")));
		}
	}

	public void addNote(String note)
	{
		if (notes == null)
		{
			notes = new ArrayList<>();
		}
		notes.add(note);
	}

	public Ship(Ship copy)
	{
		name = copy.getName();
		species = copy.getSpecies();
		size = copy.getSize();
		damageSize = copy.getDamageSize();
		notes = copy.getNotes();
		for (int i = 0; i < 49; i++)
		{
			Module mod = copy.getModule(i);
			if (!"blank".equals(mod.getName()))
			{
				addModule(mod.copy(), i);
			}
		}

	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSpecies()
	{
		return species;
	}

	public void setSpecies(String species)
	{
		if (species == null || species.isEmpty())
		{
			this.species = "Generic";
		}
		else
		{
			this.species = species;
		}
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

	public int getDamageSize()
	{
		return damageSize;
	}

	public void setDamageSize(int size)
	{
		this.damageSize = size;
	}

	public void addModule(Module module, int key)
	{
		Integer ndx = key;
		modules.put(ndx, module);
	}

	public void addModule(Module module, int row, int col)
	{
		int key = (7 * (row - 4)) + (col - 4);
		addModule(module, key);
	}

	public Module getModule(int key)
	{
		Integer ndx = key;
		if (modules.containsKey(ndx))
		{
			return modules.get(ndx);
		}
		else
		{
			return getBlankModule();
		}
	}

	public int getLifeSupportCount()
	{
		int ls = 0;
		for (Module module : modules.values())
		{
			if ("life_support".equals(module.name))
			{
				ls += module.isUpgraded() ? 5 : 4;
			}
		}
		return ls;
	}

	public Document toDocument()
	{
		Element shipElement = new Element("Ship");
		Document shipDocument = new Document(shipElement);
		shipElement.setAttribute("name", name);
		shipElement.setAttribute("species", species);
		shipElement.setAttribute("size", Integer.toString(size));
		shipElement.setAttribute("damageSize", Integer.toString(damageSize));

		if (notes != null && notes.size() > 0)
		{
			Element notesElement = new Element("Notes");
			int i = 0;
			if (notes != null)
			{
				for (String note : notes)
				{
					Element noteElement = new Element("Note");
					noteElement.setAttribute("ndx", Integer.toString(i++));
					noteElement.setText(note);
					notesElement.addContent(noteElement);
				}
			}
			shipElement.addContent(notesElement);
		}

		Element modulesElement = new Element("Modules");
		shipElement.addContent(modulesElement);
		for (Entry<Integer, Module> entry : modules.entrySet())
		{
			Element moduleElement = new Element("Module");
			moduleElement.setAttribute("name", entry.getValue().getName());
			moduleElement.setAttribute("rotation", Integer.toString(entry.getValue().getRotation()));
			moduleElement.setAttribute("location", entry.getKey().toString());
			if (entry.getValue().isUpgraded())
			{
				moduleElement.setAttribute("upgraded", entry.getValue().isUpgraded() ? "T" : "F");
			}
			modulesElement.addContent(moduleElement);
		}
		return shipDocument;
	}

	public String toXML()
	{
		XMLOutputter outputer = new XMLOutputter();
		Format format = getPrettyFormat().setIndent("\t");
		format.setTextMode(TRIM);
		outputer.setFormat(format);
		return outputer.outputString(toDocument());
	}

	public static Ship fromXML(String xml)
	{
		SAXBuilder builder = new SAXBuilder();
		StringReader sr = new StringReader(xml);
		try
		{
			//builder.setValidation(true);
			builder.setIgnoringElementContentWhitespace(true);
			//builder.setIgnoringBoundaryWhitespace(true);
			Document shipDocument = builder.build(sr);
			Element shipElement = shipDocument.getRootElement();
			return fromXML(shipElement);
		}
		catch (JDOMException | IOException e)
		{
			logger.error("Couldn't deserialize xml into a Ship object", e);
		}

		return new Ship();
	}

	public static Ship fromXML(Element shipElement)
	{
		Ship ship = new Ship();
		ship.setName(shipElement.getAttributeValue("name"));
		ship.setSpecies(shipElement.getAttributeValue("species"));
		String sizeString = shipElement.getAttributeValue("size");
		Element notesElement = shipElement.getChild("Notes");
		if (notesElement != null)
		{
			Object notesArray[] = notesElement.getChildren("Note").toArray();
			for (Object noteObj : notesArray)
			{
				Element noteElem = (Element) noteObj;
				String note = noteElem.getTextNormalize();
				ship.addNote(note);
			}
		}
		try
		{
			ship.setSize(parseInt(sizeString));
			ship.setDamageSize(parseInt(sizeString));
		}
		catch (NumberFormatException iggy)
		{
		}
		sizeString = shipElement.getAttributeValue("damageSize");
		try
		{
			ship.setDamageSize(parseInt(sizeString));
		}
		catch (NumberFormatException iggy)
		{
		}

		if (logger.isTraceEnabled())
		{
			logger.trace("Ship: " + ship.toString());
		}

		Element modulesElement = shipElement.getChild("Modules");
		Object array[] = modulesElement.getChildren("Module").toArray();
		for (Object obj : array)
		{
			Element moduleElem = (Element) obj;
			String nameString = moduleElem.getAttributeValue("name");
			String rotationString = moduleElem.getAttributeValue("rotation");
			String locationString = moduleElem.getAttributeValue("location");
			String rowString = moduleElem.getAttributeValue("row");
			String colString = moduleElem.getAttributeValue("col");
			String upgradedString = moduleElem.getAttributeValue("upgraded");

			if (logger.isTraceEnabled())
			{
				StringBuilder sb = new StringBuilder("Module Data:");
				sb.append(" name = ").append(nameString);
				sb.append(" location = ").append(locationString);
				sb.append(" rotation = ").append(rotationString);
				sb.append(" upgrade = ").append(upgradedString);
				logger.trace(sb.toString());
			}

			try
			{
				Module module = INSTANCE.getModuleByName(nameString);
				if (logger.isTraceEnabled())
				{
					logger.trace("Before " + module.toString());
				}
				module.setRotation(parseInt(rotationString));
				module.setUpgraded("T".equals(upgradedString));

				if (logger.isTraceEnabled())
				{
					logger.trace("After " + module.toString());
				}

				if (locationString != null && locationString.length() > 0)
				{
					ship.addModule(module, parseInt(locationString));
				}
				else
				{
					ship.addModule(module, parseInt(rowString), parseInt(colString));
				}
			}
			catch (NumberFormatException iggy)
			{
			}
		}
		return ship;
	}

	public static List<Ship> fromShipsXML(String xml)
	{
		StringReader sr = new StringReader(xml);
		return fromShipsXML(sr);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (species.equals(name))
		{
			sb.append(name);
		}
		else
		{
			sb.append(species);
			sb.append(" ");
			sb.append(name);
		}
		sb.append(" (Size ");
		sb.append(size);
		sb.append(")");
		return sb.toString();
	}

	public static List<Ship> fromShipsXML(Reader reader)
	{
		List<Ship> ships = new ArrayList<>();
		try
		{
			SAXBuilder builder = new SAXBuilder();
			Document shipDocument = builder.build(reader);
			Element shipsElement = shipDocument.getRootElement();
			Object array[] = shipsElement.getChildren("Ship").toArray();
			for (Object obj : array)
			{
				Element shipElem = (Element) obj;
				Ship ship = fromXML(shipElem);
				if (ship != null)
				{
					ships.add(ship);
				}
			}
			reader.close();
		}
		catch (IOException | JDOMException ioex)
		{
			logger.error("Couldn't read stream into Ship objects", ioex);
		}

		return ships;
	}

	public BufferedImage generateImage()
	{
//		BufferedImage shipImage = new BufferedImage(1821,1821,BufferedImage.TYPE_INT_RGB);
//		int keyOffset = 50;

		BufferedImage shipImage = new BufferedImage(1771, 1771, TYPE_INT_RGB);
		int keyOffset = 0;

		Graphics2D g = shipImage.createGraphics();
		//
		// Set up some anti-aliasing to look pretty
		//
		// for antialising geometric shapes
		g.addRenderingHints(new RenderingHints(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON));
		// for antialiasing text
		g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
		java.awt.Font f = new java.awt.Font("Courier", BOLD, 35);
		g.setFont(f);

		for (int i = 0; i < 7; i++)
		{
			for (int j = 0; j < 7; j++)
			{
				int key = (i * 7) + j;
				Module module = getModule(key);
				if (logger.isDebugEnabled())
					logger.debug("Module: " + module.toString());
				Image img = module.getImage();
				if (img != null)
				{
					g.drawImage(img, keyOffset + j * 253, keyOffset + i * 253, null);
					if (module.isUpgraded())
					{
						int x = (keyOffset + j * 253) + 120;
						int y = (keyOffset + i * 253) + 120;
						g.setColor(BLACK);
						g.drawString("UPG", x, y);
						g.setColor(WHITE);
						g.drawString("UPG", x - 1, y - 1);
					}
				}
			}
		}
		/*
         // draw the silhouette
         g.setColor(Color.BLACK);
         for ( int i = 4; i < 11; i++)
         {
         Integer s = new Integer(i);
         int offset = keyOffset + 120 + ((i-4) * 253);
         g.drawString(s.toString(),offset, 30);
         g.drawString(s.toString(),offset, 1761);
         if ( i > 9)
         {
         g.drawString(s.toString(), 5, offset);
         g.drawString(s.toString(), 1727, offset);
         }
         else
         {
         g.drawString(s.toString(), 10, offset);
         g.drawString(s.toString(), 1742, offset);
         }
         }
		 */
		return shipImage;
	}

	public BufferedImage generatePrintImage()
	{
		BufferedImage shipImage = new BufferedImage(21000, 21000, BufferedImage.TYPE_INT_RGB);
		int keyOffset = 0;

		Graphics2D g = shipImage.createGraphics();
		g.addRenderingHints(new RenderingHints(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON));


		for (int i = 0; i < 7; i++)
		{
			for (int j = 0; j < 7; j++)
			{
				int key = (i * 7) + j;
				Module module = getModule(key);
				Image img = module.getLargeImage();
				if (img != null)
				{
					g.drawImage(img, keyOffset + j * 3000, keyOffset + i * 3000, null);
				}
			}
		}
		return shipImage;
	}

	public BufferedImage generateThumbnailImage()
	{
		return generateThumbnailImage(4);
	}

	public BufferedImage generateThumbnailImage(int pixelSize)
	{
//		BufferedImage shipImage = new BufferedImage(35,35,BufferedImage.TYPE_INT_ARGB);
		GraphicsEnvironment ge = getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gs.getDefaultConfiguration();

		// Create an image that supports transparent pixels
		BufferedImage shipImage = gc.createCompatibleImage(pixelSize * 7, pixelSize * 7, BITMASK);

		Graphics2D g = shipImage.createGraphics();
		//
		// Set up some anti-aliasing to look pretty
		//
		// for antialising geometric shapes
		g.addRenderingHints(new RenderingHints(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON));
		// for antialiasing text
		g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
		java.awt.Font f = new java.awt.Font("Courier", BOLD, pixelSize * 7);
		g.setFont(f);

		Color transparent = new Color(0, 0, 0, 0);
		g.setColor(transparent);
		g.setComposite(Src);

		for (int i = 0; i < 7; i++)
		{
			for (int j = 0; j < 7; j++)
			{
				int key = (i * 7) + j;
				Module module = getModule(key);
				if ("blank".equals(module.getName()))
				{
					g.setColor(transparent);
				}
				else
				{
					//String profName = ModuleImageMapFactory.getInstance().getProfessionForModule(module.getName());
					//g.setColor(BattlestationColors.getColorFromName(profName));
					g.setColor(BLACK);
					g.drawRect(j * pixelSize, i * pixelSize, pixelSize, pixelSize);
					g.fillRect(j * pixelSize, i * pixelSize, pixelSize, pixelSize);
				}
			}
		}
		g.dispose();
		return shipImage;
	}

	public void autoSize()
	{
		int podCount = 0;
		int modCount = modules.size();
		for (Module mod : modules.values())
		{
			if (mod.isPod())
			{
				podCount++;
			}
		}
		modCount -= podCount;

		int partialSize = (int) ceil(modCount / 3.0);
		size = partialSize + 2;
		size += (int) ceil(podCount / 2.0);

		partialSize = (int) floor(modCount / 3.0);
		damageSize = partialSize + 2;
	}

	@Override
	public int compareTo(Ship otherShip)
	{
		//Species
		if (species.equals(otherShip.getSpecies()))
		{
			// size
			if (size == otherShip.getSize())
			{
				// name
				return name.compareTo(otherShip.getName());
			}
			else
			{
				Integer _size = size;
				Integer otherSize = otherShip.getSize();
				return _size.compareTo(otherSize);
			}
		}
		else if (species.equalsIgnoreCase("generic") && otherShip.getSpecies().equalsIgnoreCase("generic"))
		{
			return 0;
		}
		else if (species.equalsIgnoreCase("generic") && !otherShip.getSpecies().equalsIgnoreCase("generic"))
		{
			return -1;
		}
		else if (!species.equalsIgnoreCase("generic") && otherShip.getSpecies().equalsIgnoreCase("generic"))
		{
			return 1;
		}
		else
		{
			return species.compareTo(otherShip.getSpecies());
		}
	}
}
