package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.Module;
import com.ericski.Battlestations.Ship;
import com.ericski.Battlestations.SpeciesFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ShipCreatorPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	JComboBox<String> shipTemplates;
	JTextField shipName;
	JComboBox<String> shipRegistry;
	JComboBox<String> shipSize;
	JComboBox<String> shipDamageSize;
	ModuleCreatorPanel modulePanel;
	JTextArea shipNotes;

	public ShipCreatorPanel()
	{
		super(new BorderLayout());

		JPanel topHolder = new JPanel();
		topHolder.setLayout(new GridLayout(2, 4, 1, 4));
		topHolder.setBorder(BorderFactory.createTitledBorder("Ship Details"));

		topHolder.add(new JLabel("Name", JLabel.CENTER));
		topHolder.add(shipName = new JTextField());
		topHolder.add(new JLabel("Registry", JLabel.CENTER));
		shipRegistry = new JComboBox<>();
		shipRegistry.setEditable(true);
		shipRegistry.addItem("");
		for (String name : SpeciesFactory.getInstance().getSpeciesNames())
		{
			shipRegistry.addItem(name);
		}
		shipRegistry.setSelectedIndex(0);

		topHolder.add(shipRegistry);
		topHolder.add(new JLabel("Size", JLabel.CENTER));
		topHolder.add(shipSize = new JComboBox<>(new String[]
		{
			"Auto Size", "3", "4", "5", "6", "7", "8", "9", "10"
		}));

		topHolder.add(new JLabel("Damage Size", JLabel.CENTER));
		topHolder.add(shipDamageSize = new JComboBox<>(new String[]
		{
			"Auto Size", "3", "4", "5", "6", "7", "8", "9", "10"
		}));

		JPanel mid = new JPanel(new BorderLayout());
		mid.add(topHolder, BorderLayout.NORTH);

		modulePanel = new ModuleCreatorPanel();
		JScrollPane shipscrollee = new JScrollPane(modulePanel);
		shipscrollee.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		shipscrollee.setAutoscrolls(true);
		shipscrollee.getVerticalScrollBar().setBlockIncrement(ModuleSelectionPanel.DEFAULTSIZE);
		shipscrollee.getHorizontalScrollBar().setBlockIncrement(ModuleSelectionPanel.DEFAULTSIZE);
		mid.add(shipscrollee, BorderLayout.CENTER);

		JPanel bottomHolder = new JPanel();
		bottomHolder.setLayout(new BorderLayout());
		bottomHolder.setBorder(BorderFactory.createTitledBorder("Notes"));

		shipNotes = new JTextArea(4, 40);
		shipNotes.setMaximumSize(new Dimension(2000, 100));
		shipNotes.setLineWrap(true);
		shipNotes.setWrapStyleWord(true);
		JScrollPane noteScrollee = new JScrollPane(shipNotes);
		noteScrollee.setAutoscrolls(true);
		bottomHolder.add(noteScrollee, BorderLayout.CENTER);

		mid.add(bottomHolder, BorderLayout.SOUTH);

		add(mid, BorderLayout.CENTER);
		add(new ModuleSelectorBar(), BorderLayout.WEST);
		add(new StandardShipBar(this), BorderLayout.EAST);

	}

	@Override
	public Dimension getMinimumSize()
	{
		return modulePanel.getMinimumSize();
	}

	public void setShip(final Ship shipToSet)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				shipNotes.setText(shipToSet.getNotesAsString());
				shipName.setText(shipToSet.getName());
				if (shipToSet.getName().length() * shipToSet.getSpecies().length() == 0)
				{
					if (shipToSet.getSize() == 3)
					{
						shipSize.setSelectedIndex(0);
						shipDamageSize.setSelectedIndex(0);
					}
					else
					{
						shipSize.setSelectedIndex(shipToSet.getSize() - 2);
						shipDamageSize.setSelectedIndex(shipToSet.getDamageSize() - 2);
					}
				}
				else
				{
					if (shipToSet.getSize() - 2 < shipSize.getItemCount())
					{
						shipSize.setSelectedIndex(shipToSet.getSize() - 2);
					}
					if (shipToSet.getDamageSize() - 2 < shipDamageSize.getItemCount())
					{
						shipDamageSize.setSelectedIndex(shipToSet.getDamageSize() - 2);
					}
				}
				boolean changed = false;
				for (int i = 0; i < shipRegistry.getItemCount(); i++)
				{
					if (shipRegistry.getItemAt(i).equals(shipToSet.getSpecies()))
					{
						shipRegistry.setSelectedIndex(i);
						changed = true;
						break;
					}
				}
				if (!changed)
				{
					shipRegistry.addItem(shipToSet.getSpecies());
					shipRegistry.setSelectedIndex(shipRegistry.getItemCount() - 1);
				}
				modulePanel.setModules(shipToSet);
			}
		});
	}

	public Ship generateShip()
	{
		//System.out.println("\n\n\tGenerate it\n");
		Ship ship = new Ship();
		ship.setName(shipName.getText());
		if (!shipNotes.getText().isEmpty())
		{
			ship.setNotes(shipNotes.getText());
		}
		ship.setSpecies(shipRegistry.getSelectedItem().toString());
		for (ModuleSelectionPanel panel : modulePanel.getModulePanels())
		{
			Module module = panel.getModule();
			int key = panel.getShipY() * 7 + panel.getShipX();
			if (!module.isBlankModule())
			{
				ship.addModule(module, key);
			}
		}

		int size;
		if (shipSize.getSelectedIndex() == 0)
		{
			ship.autoSize();
			size = ship.getSize() - 2;
			if (size >= shipSize.getItemCount())
				size = 0;
			shipSize.setSelectedIndex(size);
		}
		else
		{
			size = shipSize.getSelectedIndex() + 2;
			ship.setSize(size);
		}

		if (shipDamageSize.getSelectedIndex() == 0)
		{
			ship.autoSize();
			size = ship.getDamageSize() - 2;
			if (size >= shipDamageSize.getItemCount())
				size = 0;
			shipDamageSize.setSelectedIndex(size);
		}
		else
		{
			size = shipDamageSize.getSelectedIndex() + 2;
			ship.setDamageSize(size);
		}
		return ship;
	}

}
