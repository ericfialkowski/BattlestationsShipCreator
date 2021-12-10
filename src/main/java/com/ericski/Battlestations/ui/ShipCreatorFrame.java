package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.ModuleFactory;
import com.ericski.Battlestations.PDFShipWriter;
import com.ericski.Battlestations.PDFShipWriterOptions;
import com.ericski.Battlestations.RuleSetVersion;
import com.ericski.Battlestations.Ship;
import com.ericski.ui.FileChooserExtensionFileFilter;
import com.ericski.ui.ImageIconScalable;
import com.ericski.ui.InfiniteProgressPanel;
import com.ericski.ui.JPopupTextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.prefs.Preferences;

public class ShipCreatorFrame extends JFrame
{
	private static final Logger logger = LogManager.getLogger(ShipCreatorFrame.class);

	private static final String SHOW_TOOL_BAR_PREFS_KEY = "showToolBar";
	private static final String EXITACTION = "EXIT";
	private static final String EXPORTJPGACTION = "ExportJPG";
	private static final String EXPORTLARGEACTION = "ExportLARGE";
	private static final String EXPORTPDFACTION = "ExportPDF";
	private static final String SAVEACTION = "SAVE";
	private static final String LOADACTION = "LOAD";
	private static final String NEWACTION = "NEWACTION";
	private static final String PRINTACTION = "PRINTACTION";
	private static final String VIEWACTION = "VIEWACTION";
	private static final String XMLVIEWACTION = "XMLVIEWACTION";
	private static final String ABOUT = "ABOUT";
	private static final String CHANGELOG = "CHANGELOG";
	private static final String COPYRIGHT = "COPYRIGHT";

	private static final String PDFOPTIONS = "PDFOPTIONS";
	private static final String PATHOPTIONS = "PATHOPTIONS";

	private static final long serialVersionUID = 1L;

	String exportPDFDirectory = "";

	ShipCreatorPanel shipPanel;
	InfiniteProgressPanel glassPane;
	Desktop desktop;
	JToolBar toolBar;
	JCheckBoxMenuItem showToolBarMenu;

	public ShipCreatorFrame()
	{
		super("Battlestations Ship Sheet Creator");

		loadPrefs();
		if (Desktop.isDesktopSupported())
		{
			desktop = Desktop.getDesktop();
		}
		JPanel contentPane = new JPanel(new BorderLayout());

		shipPanel = new ShipCreatorPanel();

		contentPane.add(shipPanel, BorderLayout.CENTER);

		ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/tentac_ship_icon.png")));
		setIconImage(icon.getImage());

		glassPane = new InfiniteProgressPanel();
		glassPane.setForeground(Color.BLUE);
		setGlassPane(glassPane);

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);

		ActionListener actionListener = new Actioner();

		JMenuItem newMenu = new JMenuItem("New");
		newMenu.addActionListener(actionListener);
		newMenu.setMnemonic('N');
		newMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		newMenu.setActionCommand(NEWACTION);
		fileMenu.add(newMenu);

		JMenuItem loadMenu = new JMenuItem("Open");
		loadMenu.addActionListener(actionListener);
		loadMenu.setMnemonic('o');
		loadMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		loadMenu.setActionCommand(LOADACTION);
		fileMenu.add(loadMenu);

		JMenuItem saveMenu = new JMenuItem("Save");
		saveMenu.addActionListener(actionListener);
		saveMenu.setMnemonic('S');
		saveMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		saveMenu.setActionCommand(SAVEACTION);
		fileMenu.add(saveMenu);

		if (desktop != null && desktop.isSupported(Desktop.Action.PRINT))
		{
			JMenuItem printMenu = new JMenuItem("Print");
			printMenu.addActionListener(actionListener);
			printMenu.setMnemonic('P');
			printMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
			printMenu.setActionCommand(PRINTACTION);
			fileMenu.add(printMenu);
		}

		if (desktop != null && desktop.isSupported(Desktop.Action.OPEN))
		{
			JMenuItem viewMenu = new JMenuItem("View in Reader");
			viewMenu.addActionListener(actionListener);
			viewMenu.setMnemonic('V');
			viewMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
			viewMenu.setActionCommand(VIEWACTION);
			fileMenu.add(viewMenu);
		}

		JMenuItem xmlViewMenu = new JMenuItem("View XML Code");
		xmlViewMenu.addActionListener(actionListener);
		xmlViewMenu.setActionCommand(XMLVIEWACTION);
		xmlViewMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.CTRL_DOWN_MASK));
		fileMenu.add(xmlViewMenu);

		fileMenu.addSeparator();

		JMenuItem exportJPGMenu = new JMenuItem("Export as JPG");
		exportJPGMenu.addActionListener(actionListener);
		exportJPGMenu.setMnemonic('J');
		exportJPGMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK));
		exportJPGMenu.setActionCommand(EXPORTJPGACTION);
		fileMenu.add(exportJPGMenu);

		JMenuItem exportPDFMenu = new JMenuItem("Export as PDF");
		exportPDFMenu.addActionListener(actionListener);
		exportPDFMenu.setMnemonic('D');
		exportPDFMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
		exportPDFMenu.setActionCommand(EXPORTPDFACTION);
		fileMenu.add(exportPDFMenu);

		fileMenu.addSeparator();

		JMenuItem exitMenu = new JMenuItem("Exit");
		exitMenu.addActionListener(actionListener);
		exitMenu.setMnemonic('x');
		exitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
		exitMenu.setActionCommand(EXITACTION);
		fileMenu.add(exitMenu);

		JMenu optionMenu = new JMenu("Options");
		optionMenu.setMnemonic('O');
		menuBar.add(optionMenu);

		JMenuItem pathOptionsMenu = new JMenuItem("Open Module Directory");
		pathOptionsMenu.addActionListener(actionListener);
		pathOptionsMenu.setMnemonic('p');
		pathOptionsMenu.setActionCommand(PATHOPTIONS);
		optionMenu.add(pathOptionsMenu);

		JMenuItem pdfOptionsMenu = new JMenuItem("PDF Output");
		pdfOptionsMenu.addActionListener(actionListener);
		pdfOptionsMenu.setMnemonic('p');
		pdfOptionsMenu.setActionCommand(PDFOPTIONS);
		optionMenu.add(pdfOptionsMenu);

		showToolBarMenu = new JCheckBoxMenuItem("Show Toolbar");
		showToolBarMenu.setSelected(true);
		showToolBarMenu.setMnemonic('t');
		showToolBarMenu.addChangeListener(e -> {
			if (e.getSource() instanceof JCheckBoxMenuItem)
			{
				JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
				boolean value = source.isSelected();
				Preferences prefs = Preferences.userNodeForPackage(ShipCreatorFrame.class);
				prefs.put(SHOW_TOOL_BAR_PREFS_KEY, Boolean.toString(value));
				toolBar.setVisible(value);
			}
		});
		optionMenu.add(showToolBarMenu);

		RuleSetVersion.getInstance().loadPreferences();

		menuBar.add(Box.createHorizontalGlue());

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('h');
		menuBar.add(helpMenu);

		JMenuItem copyrightMenu = new JMenuItem("Copyright");
		copyrightMenu.setActionCommand(COPYRIGHT);
		copyrightMenu.addActionListener(actionListener);
		helpMenu.add(copyrightMenu);

		JMenuItem aboutMenu = new JMenuItem("About");
		aboutMenu.setActionCommand(ABOUT);
		aboutMenu.addActionListener(actionListener);
		helpMenu.add(aboutMenu);

		JMenuItem changeLogMenu = new JMenuItem("Changelog");
		changeLogMenu.setActionCommand(CHANGELOG);
		changeLogMenu.addActionListener(actionListener);
		helpMenu.add(changeLogMenu);

		setJMenuBar(menuBar);

		toolBar = new JToolBar("File Menu");
		JButton newButton = createToolBarButton("/toolbarButtonGraphics/general/New24.gif", NEWACTION, actionListener);
		newButton.setText("New");
		toolBar.add(newButton);

		JButton openButton = createToolBarButton("/toolbarButtonGraphics/general/Open24.gif", LOADACTION, actionListener);
		openButton.setText("Open");
		toolBar.add(openButton);

		JButton saveButton = createToolBarButton("/toolbarButtonGraphics/general/Save24.gif", SAVEACTION, actionListener);
		saveButton.setText("Save");
		toolBar.add(saveButton);

		if (desktop != null && desktop.isSupported(Desktop.Action.PRINT))
		{
			JButton printButton = createToolBarButton("/toolbarButtonGraphics/general/Print24.gif", PRINTACTION, actionListener);
			printButton.setText("Print");
			toolBar.add(printButton);
		}

		if (desktop != null && desktop.isSupported(Desktop.Action.OPEN))
		{
			JButton viewButton = createToolBarButton("/toolbarButtonGraphics/general/PrintPreview24.gif", VIEWACTION, actionListener);
			viewButton.setText("View");
			toolBar.add(viewButton);
		}
		toolBar.addSeparator();

		JButton exportPDFButton = createToolBarButton("/toolbarButtonGraphics/general/Export24.gif", EXPORTPDFACTION, actionListener);
		exportPDFButton.setText("Export as PDF");
		toolBar.add(exportPDFButton);

		JButton exportJPGButton = createToolBarButton("/toolbarButtonGraphics/general/Export24.gif", EXPORTJPGACTION, actionListener);
		exportJPGButton.setText("Export as JPG");
		toolBar.add(exportJPGButton);

		JButton exportLargeButton = createToolBarButton("/toolbarButtonGraphics/general/Export24.gif", EXPORTLARGEACTION, actionListener);
		exportLargeButton.setText("Export as Large JPG");
		toolBar.add(exportLargeButton);

		toolBar.addSeparator();

		JButton exitButton = createToolBarButton("/toolbarButtonGraphics/general/Stop24.gif", EXITACTION, actionListener);
		exitButton.setText("Exit");
		toolBar.add(exitButton);

		contentPane.add(toolBar, BorderLayout.PAGE_START);

		setContentPane(contentPane);
		pack();
		Dimension dim = new Dimension((int) toolBar.getPreferredSize().getWidth(), (int) shipPanel.getMinimumSize().getHeight());
		setMinimumSize(dim);

		setLocationRelativeTo(null);
	}

	private JButton createToolBarButton(String path, String action, ActionListener actionListener)
	{
		JButton button = new JButton();
		if (path != null && path.length() > 0)
		{
			try
			{
				URL url = this.getClass().getResource(path);
				ImageIconScalable icon = new ImageIconScalable(url);
				icon.setScaledSize(24, 24);
				button.setIcon(icon);
			}
			catch (Exception iggy)
			{
			}
		}
		button.addActionListener(actionListener);
		button.setActionCommand(action);
		return button;
	}

	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(() -> {
			JFrame.setDefaultLookAndFeelDecorated(true);
			try
			{
				// Set System L&F
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e)
			{
				// handle exception
			}
			ShipCreatorFrame frame = new ShipCreatorFrame();
			frame.toFront();
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		});

	}

	private class Actioner implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent ae)
		{
			String action = ae.getActionCommand();
			switch (action)
			{
				case NEWACTION:
					shipPanel.setShip(new Ship());
					break;
				case SAVEACTION:
					saveShip();
					break;
				case LOADACTION:
					loadShip();
					break;
				case EXPORTJPGACTION:
					exportJPG();
					break;
				case EXPORTLARGEACTION:
					exportLarge();
					break;
				case EXPORTPDFACTION:
					exportPDF();
					break;
				case PRINTACTION:
					print();
					break;
				case VIEWACTION:
					view();
					break;
				case XMLVIEWACTION:
					Ship ship = shipPanel.generateShip();
					String text = ship.toXML();
					RawXmlDialog forumDialog = new RawXmlDialog(text, ShipCreatorFrame.this, "XML view", "Raw file view");
					forumDialog.setVisible(true);
					break;
				case EXITACTION:
					glassPane.setVisible(false);
					glassPane.interrupt();
					if (getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE)
					{
						System.exit(0);
					}
					else
					{
						setVisible(false);
					}
					break;
				case ABOUT:
				{
					String info = "Battlestations Ship Sheet Creator\n"
									  + "Version: " + Version.INSTANCE.getVersion() + "\n"
									  + "Built at: " + Version.INSTANCE.getBuildDate() + "\n"
									  + "Contact: eric.fialkowski@gmail.com";
					if (Version.INSTANCE.isUptoDate() == Version.VersionUpToDate.No)
					{
						info += "\n\nA newer version is available for download";
					}
					else
					{
						info += "\n\nYou have the most current version.";
					}
					JOptionPane.showMessageDialog(ShipCreatorFrame.this, info, "About", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				case CHANGELOG:
				{
					String info = "2008(ish) Initial Public Release"
									  + "\n4/4/2012  Updates to current rules & expansions"
									  + "\n7/14/2014  Added Ships from How Much For Your Planet"
									  + "\n7/15/2014  ALT+Dragging a module will duplicate it. Ability to zoom modules";
					JOptionPane.showMessageDialog(ShipCreatorFrame.this, info, "Change log", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				case COPYRIGHT:
					String copyright = "Battlestations Copyright 2004, 2008 Jeff Siadek and Jason Siadek\n"
										   + "Battlestations and the Battlestations logo are trademarks owned by Jeff Siadek and Jason Siadek. All rights reserved.\n\n"
									   + "For more information see: " + "http://www.battlestations.info\n";
					JOptionPane.showMessageDialog(ShipCreatorFrame.this, copyright, "Copyright Information", JOptionPane.INFORMATION_MESSAGE);
					break;
				case PDFOPTIONS:
					PDFPreferencesDialog prefs = new PDFPreferencesDialog(ShipCreatorFrame.this);
					prefs.setVisible(true);
					break;
				case PATHOPTIONS:
					try
					{
						desktop.open(ModuleFactory.INSTANCE.getModuleDirectory());
					}
					catch (IOException ex)
					{
						logger.warn("Couldn't open directory", ex);
					}
					break;
			}
		}
	}

	protected void print()
	{
		glassPane.setText("Printing");
		glassPane.start();
		PrintShipTask task = new PrintShipTask(this, shipPanel.generateShip(), glassPane, desktop);
		task.execute();
	}

	protected void view()
	{
		glassPane.setText("Opening default viewer");
		glassPane.start();
		ViewShipTask task = new ViewShipTask(this, shipPanel.generateShip(), glassPane, desktop);
		task.execute();
	}

	protected void saveShip()
	{
		glassPane.setText("Preparing to Save Ship");
		glassPane.start();
		JFileChooser saver = getFileChooser();
		FileChooserExtensionFileFilter shipFilter = new FileChooserExtensionFileFilter(".ship", "Ship files");
		saver.addChoosableFileFilter(shipFilter);
		saver.setAcceptAllFileFilterUsed(false);
		int result = saver.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File f = saver.getSelectedFile();
			setDirectory(f);
			if (!f.getAbsolutePath().toLowerCase().endsWith(shipFilter.getExtension()))
			{
				f = new File(f.getAbsolutePath() + shipFilter.getExtension());
			}
			glassPane.setText("Saving");
			glassPane.start();
			SaveShipTask task = new SaveShipTask(this, shipPanel.generateShip(), f, glassPane);
			task.execute();
		}
		else
		{
			glassPane.setVisible(false);
		}

	}

	protected void exportJPG()
	{
		glassPane.setText("Preparing to Export JPEG");
		glassPane.start();
		JFileChooser saver = getFileChooser();
		FileChooserExtensionFileFilter jpgFilter = new FileChooserExtensionFileFilter(".jpg", "JPEG files");
		saver.addChoosableFileFilter(jpgFilter);
		saver.setAcceptAllFileFilterUsed(false);

		int result = saver.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			glassPane.setText("Exporting JPEG");
			glassPane.start();
			File jpgFile = saver.getSelectedFile();
			setDirectory(jpgFile);
			if (!jpgFile.getAbsolutePath().toLowerCase().endsWith(jpgFilter.getExtension()))
			{
				jpgFile = new File(jpgFile.getAbsolutePath() + jpgFilter.getExtension());
			}
			ExportJPGTask task = new ExportJPGTask(this, shipPanel.generateShip(), jpgFile, glassPane);
			task.execute();
		}
		else
		{
			glassPane.setVisible(false);
		}
	}

	protected void exportLarge()
	{
		glassPane.setText("Preparing to Export Large JPEG");
		glassPane.start();
		JFileChooser saver = getFileChooser();
		FileChooserExtensionFileFilter filter = new FileChooserExtensionFileFilter(".jpg", "JPEG files");
		saver.addChoosableFileFilter(filter);
		saver.setAcceptAllFileFilterUsed(false);

		int result = saver.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			glassPane.setText("Exporting Large JPEG");
			glassPane.start();
			File file = saver.getSelectedFile();
			setDirectory(file);
			if (!file.getAbsolutePath().toLowerCase().endsWith(filter.getExtension()))
			{
				file = new File(file.getAbsolutePath() + filter.getExtension());
			}
			ExportLargeTask task = new ExportLargeTask(this, shipPanel.generateShip(), file, glassPane);
			task.execute();
		}
		else
		{
			glassPane.setVisible(false);
		}
	}

	protected void exportPDF()
	{
		glassPane.setText("Preparing to Export PDF");
		glassPane.start();
		JFileChooser saver = getFileChooser();
		FileChooserExtensionFileFilter pdfFilter = new FileChooserExtensionFileFilter(".pdf", "PDF files");
		saver.addChoosableFileFilter(pdfFilter);
		saver.setAcceptAllFileFilterUsed(false);
		int result = saver.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			glassPane.setText("Exporting PDF");
			glassPane.start();
			File pdfFile = saver.getSelectedFile();
			setDirectory(pdfFile);
			if (!pdfFile.getAbsolutePath().toLowerCase().endsWith(pdfFilter.getExtension()))
			{
				pdfFile = new File(pdfFile.getAbsolutePath() + pdfFilter.getExtension());
			}
			ExportPDFTask task = new ExportPDFTask(this, shipPanel.generateShip(), pdfFile, glassPane);
			task.execute();
		}
		else
		{
			glassPane.setVisible(false);
		}

	}

	protected void loadShip()
	{
		glassPane.setText("Preparing to Load Ship");
		glassPane.start();
		JFileChooser loader = getFileChooser();
		FileChooserExtensionFileFilter shipFilter = new FileChooserExtensionFileFilter(".ship", "Ship files");
		loader.addChoosableFileFilter(shipFilter);
		loader.setAcceptAllFileFilterUsed(false);
		int result = loader.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION)
		{
			File f = loader.getSelectedFile();
			setDirectory(f);
			if (!f.getAbsolutePath().toLowerCase().endsWith(shipFilter.getExtension()))
			{
				f = new File(f.getAbsolutePath() + shipFilter.getExtension());
			}
			glassPane.setText("Loading");
			glassPane.start();
			LoadShipTask task = new LoadShipTask(this, shipPanel, f, glassPane);
			task.execute();
		}
		else
		{
			glassPane.setVisible(false);
		}

	}

	protected void setDirectory(File f)
	{
		String dir = f.getParent();
		if (dir != null)
		{
			exportPDFDirectory = dir;
			savePrefs();
		}
	}

	protected JFileChooser getFileChooser()
	{
		JFileChooser chooser;
		if (exportPDFDirectory.length() > 0)
		{
			chooser = new JFileChooser(exportPDFDirectory);
		}
		else
		{
			chooser = new JFileChooser();
		}
		return chooser;
	}

	protected final void loadPrefs()
	{
		Preferences prefs = Preferences.userNodeForPackage(ShipCreatorFrame.class);
		exportPDFDirectory = prefs.get("exportPDFDirectory", "");
	}

	protected void savePrefs()
	{
		Preferences prefs = Preferences.userNodeForPackage(ShipCreatorFrame.class);
		prefs.put("exportPDFDirectory", exportPDFDirectory);
	}

}

class ViewShipTask extends SwingWorker<Void, Void>
{
	private static final Logger logger = LogManager.getLogger(ShipCreatorFrame.class);
	Ship shipToPrint;
	JFrame parent;
	InfiniteProgressPanel progressPanel;
	Desktop desktop;

	public ViewShipTask(JFrame parent, Ship shipToPrint, InfiniteProgressPanel progressPanel, Desktop desktop)
	{
		this.parent = parent;
		this.shipToPrint = shipToPrint;
		this.progressPanel = progressPanel;
		this.desktop = desktop;
	}

	@Override
	protected Void doInBackground()
	{
		try
		{
			File f = File.createTempFile("shiptoprint", ".pdf");
			FileOutputStream fout = new FileOutputStream(f);
			PDFShipWriterOptions options = new PDFShipWriterOptions();
			options.loadPreferences();
			PDFShipWriter.drawPDF(shipToPrint, fout, options);
			desktop.open(f);
			progressPanel.stop();
		}
		catch (IOException ex)
		{
			progressPanel.stop();
			logger.warn("Error Viewing Ship", ex);
			JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Viewing Ship", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			progressPanel.setVisible(false);
		}
		return null;
	}
}

class PrintShipTask extends SwingWorker<Void, Void>
{
	private static final Logger logger = LogManager.getLogger(ShipCreatorFrame.class);
	Ship shipToPrint;
	JFrame parent;
	InfiniteProgressPanel progressPanel;
	Desktop desktop;

	public PrintShipTask(JFrame parent, Ship shipToPrint, InfiniteProgressPanel progressPanel, Desktop desktop)
	{
		this.parent = parent;
		this.shipToPrint = shipToPrint;
		this.progressPanel = progressPanel;
		this.desktop = desktop;
	}

	@Override
	protected Void doInBackground()
	{
		try
		{
			File f = File.createTempFile("shiptoprint", ".pdf");
			FileOutputStream fout = new FileOutputStream(f);
			PDFShipWriterOptions options = new PDFShipWriterOptions();
			options.loadPreferences();
			PDFShipWriter.drawPDF(shipToPrint, fout, options);
			desktop.print(f);
			progressPanel.stop();
		}
		catch (IOException ex)
		{
			progressPanel.stop();
			logger.warn("Error Printing Ship", ex);
			JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Printing Ship", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			progressPanel.setVisible(false);
		}
		return null;
	}
}

class SaveShipTask extends SwingWorker<Void, Void>
{
	private static final Logger logger = LogManager.getLogger(ShipCreatorFrame.class);
	Ship shipToSave;
	JFrame parent;
	File shipFile;
	InfiniteProgressPanel progressPanel;

	public SaveShipTask(JFrame parent, Ship shipToSave, File shipFile, InfiniteProgressPanel progressPanel)
	{
		this.parent = parent;
		this.shipToSave = shipToSave;
		this.shipFile = shipFile;
		this.progressPanel = progressPanel;
	}

	@Override
	protected Void doInBackground()
	{
		try
		{
			try (FileWriter fw = new FileWriter(shipFile))
			{
				fw.write(shipToSave.toXML());
			}
			progressPanel.stop();
			JOptionPane.showMessageDialog(parent, "Ship saved", "Success", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException | HeadlessException ex)
		{
			progressPanel.stop();
			logger.warn("Error Saving Ship", ex);
			JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Saving Ship", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			progressPanel.setVisible(false);
		}
		return null;
	}
}

class LoadShipTask extends SwingWorker<Void, Void>
{
	private static final Logger logger = LogManager.getLogger(ShipCreatorFrame.class);
	ShipCreatorPanel shipPanel;
	JFrame parent;
	File shipFile;
	InfiniteProgressPanel progressPanel;

	public LoadShipTask(JFrame parent, ShipCreatorPanel shipPanel, File shipFile, InfiniteProgressPanel progressPanel)
	{
		this.parent = parent;
		this.shipPanel = shipPanel;
		this.shipFile = shipFile;
		this.progressPanel = progressPanel;
	}

	@Override
	protected Void doInBackground()
	{
		try
		{
			//
			// load ship
			//
			BufferedReader br = new BufferedReader(new FileReader(shipFile));
			String line;
			StringBuilder sb = new StringBuilder(1024);
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}
			Ship ship = Ship.fromXML(sb.toString());
			shipPanel.setShip(ship);
			progressPanel.stop();
			JOptionPane.showMessageDialog(parent, "Ship Loaded", "Success", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException | HeadlessException ex)
		{
			progressPanel.stop();
			logger.warn("Error Loading Ship", ex);
			JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Loading Ship", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			progressPanel.setVisible(false);
		}
		return null;
	}
}

class ExportJPGTask extends SwingWorker<Void, Void>
{
	private static final Logger logger = LogManager.getLogger(ShipCreatorFrame.class);
	Ship shipToExport;
	JFrame parent;
	File jpgFile;
	InfiniteProgressPanel progressPanel;

	public ExportJPGTask(JFrame parent, Ship shipToExport, File jpgFile, InfiniteProgressPanel progressPanel)
	{
		this.parent = parent;
		this.shipToExport = shipToExport;
		this.jpgFile = jpgFile;
		this.progressPanel = progressPanel;
	}

	@Override
	protected Void doInBackground()
	{

		try
		{
			BufferedImage shipImage = shipToExport.generateImage();
			ImageIO.write(shipImage, "jpeg", jpgFile);
			progressPanel.stop();
			JOptionPane.showMessageDialog(parent, "JPEG Exported", "Success", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException | HeadlessException ex)
		{
			progressPanel.stop();
			logger.warn("Error Exporting Ship to JPEG", ex);
			JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Exporting Ship to JPEG", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			progressPanel.setVisible(false);
		}

		return null;
	}
}

class ExportLargeTask extends SwingWorker<Void, Void>
{
	private static final Logger logger = LogManager.getLogger(ShipCreatorFrame.class);
	Ship shipToExport;
	JFrame parent;
	File file;
	InfiniteProgressPanel progressPanel;

	public ExportLargeTask(JFrame parent, Ship shipToExport, File file, InfiniteProgressPanel progressPanel)
	{
		this.parent = parent;
		this.shipToExport = shipToExport;
		this.file = file;
		this.progressPanel = progressPanel;
	}

	@Override
	protected Void doInBackground()
	{

		try
		{
			BufferedImage shipImage = shipToExport.generatePrintImage();

			boolean written = ImageIO.write(shipImage, "jpeg", file);
			progressPanel.stop();
			if (written)
				JOptionPane.showMessageDialog(parent, "JPEG Exported", "Success", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(parent, "JPEG NOT Exported", "Failed", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException | HeadlessException ex)
		{
			progressPanel.stop();
			logger.warn("Error Exporting Ship to JPEG", ex);
			JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Exporting Ship to JPEG", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			progressPanel.setVisible(false);
		}

		return null;
	}
}

class ExportPDFTask extends SwingWorker<Void, Void>
{
	private static final Logger logger = LogManager.getLogger(ShipCreatorFrame.class);
	Ship shipToExport;
	JFrame parent;
	File pdfFile;
	InfiniteProgressPanel progressPanel;

	public ExportPDFTask(JFrame parent, Ship shipToExport, File pdfFile, InfiniteProgressPanel progressPanel)
	{
		this.parent = parent;
		this.shipToExport = shipToExport;
		this.pdfFile = pdfFile;
		this.progressPanel = progressPanel;
	}

	@Override
	protected Void doInBackground()
	{

		try
		{
			// Prepare to write to the file
			FileOutputStream fout = new FileOutputStream(pdfFile);
			PDFShipWriterOptions options = new PDFShipWriterOptions();
			options.loadPreferences();
			PDFShipWriter.drawPDF(shipToExport, fout, options);

			progressPanel.stop();
			JOptionPane.showMessageDialog(parent, "PDF Exported", "Success", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException | HeadlessException ex)
		{
			progressPanel.stop();
			logger.warn("Error Exporting Ship to PDF", ex);
			JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Exporting Ship to PDF", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			progressPanel.setVisible(false);
		}
		return null;
	}

}

class RawXmlDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public RawXmlDialog(String text, JFrame parent, String title, String msg)
	{
		super();
		setTitle(title);
		JPanel content = new JPanel(new BorderLayout());
		JTextArea xmlText = new JPopupTextArea(20, 40);
		xmlText.setText(text);
		JScrollPane scrollingArea = new JScrollPane(xmlText);
		content.add(scrollingArea, BorderLayout.CENTER);
		JLabel info = new JLabel(msg);
		content.add(info, BorderLayout.NORTH);

		setContentPane(content);
		pack();

		setLocationRelativeTo(parent);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}
}
