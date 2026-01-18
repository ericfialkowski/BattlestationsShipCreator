package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.Module;
import com.ericski.Battlestations.Ship;
import static com.ericski.Battlestations.ui.ModuleSelectionPanel.DEFAULTSIZE;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

public class ModuleCreatorPanel extends JPanel
{
	private final int sizer = DEFAULTSIZE;
	private static final long serialVersionUID = 1L;
	private final JPanel shipgrid;
	List<ModuleSelectionPanel> modulePanels = new ArrayList<>();

	public ModuleCreatorPanel()
	{
		super(new FlowLayout());
		shipgrid = new JPanel(new GridLayout(7, 7, 0, 0));
		shipgrid.setPreferredSize(new Dimension(sizer * 7, sizer * 7));

		for (int y = 0; y < 7; y++)
		{
			for (int x = 0; x < 7; x++)
			{
				ModuleSelectionPanel moduleSelectionPanel = new ModuleSelectionPanel(x, y);
				shipgrid.add(moduleSelectionPanel);
				modulePanels.add(moduleSelectionPanel);
			}
		}
		add(shipgrid);
	}

	public List<ModuleSelectionPanel> getModulePanels()
	{
		return new ArrayList<>(modulePanels);
	}

	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension((sizer * 7) + 5, (sizer * 7) + 5);
	}

	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension((sizer * 7) + 5, (sizer * 7) + 5);
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension((sizer * 7) + 5, (sizer * 7) + 5);
	}

	public Map<Integer, Module> getModules()
	{
		Map<Integer, Module> modules = new HashMap<>();
		for (ModuleSelectionPanel panel : modulePanels)
		{
			Module module = panel.getModule();
			int key = panel.getShipY() * 7 + panel.getShipX();
			if (!module.isBlankModule())
			{
				modules.put(key, module.copy());
			}
		}
		return modules;
	}

	public void setModules(Ship ship)
	{
		for (ModuleSelectionPanel panel : modulePanels)
		{
			int key = panel.getShipY() * 7 + panel.getShipX();
			Module module = ship.getModule(key);
			panel.setModule(module.copy());
		}
	}

}
