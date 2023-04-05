package com.ericski.Battlestations.ui;

import static com.ericski.Battlestations.BattlestationColors.Combat;
import static com.ericski.Battlestations.BattlestationColors.Engineering;
import static com.ericski.Battlestations.BattlestationColors.Piloting;
import static com.ericski.Battlestations.BattlestationColors.Science;
import com.ericski.Battlestations.Module;
import static com.ericski.Battlestations.ModuleFactory.INSTANCE;
import com.l2fprod.common.swing.JOutlookBar;
import java.awt.Color;
import java.awt.Dimension;
import static javax.swing.Box.createVerticalStrut;
import javax.swing.BoxLayout;
import static javax.swing.BoxLayout.Y_AXIS;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ModuleSelectorBar extends JOutlookBar
{

	private static final long serialVersionUID = 1L;
	private static final int MODULESIZE = 64;

	public ModuleSelectorBar()
	{
		super(RIGHT);
		JPanel allPanel = new JPanel();
		allPanel.setLayout(new BoxLayout(allPanel, Y_AXIS));

		for (Module m : INSTANCE.getAllModules())
		{
			if (!"obsolete".equals(m.getProfession()))
			{
				allPanel.add(createVerticalStrut(3));
				ModuleSelectionPanel msp = new ModuleSelectionPanel(-1, -1, false);
				msp.setModule(m);
				msp.setWidth(MODULESIZE);
				msp.setHeight(MODULESIZE);
				msp.setShowToolTip(true);
				allPanel.add(msp);
			}
		}
		allPanel.add(createVerticalStrut(3));
		JScrollPane allScroller = new JScrollPane(allPanel);
		allScroller.setPreferredSize(new Dimension(84, 256));
		allScroller.setMinimumSize(new Dimension(84, 128));
		addTab("All", allScroller);

		JPanel generalPanel = new JGradPanel();
		generalPanel.setLayout(new BoxLayout(generalPanel, Y_AXIS));
		ModuleSelectionPanel msp;
		for (Module m : INSTANCE.getAllModulesForProfession("general"))
		{
			generalPanel.add(createVerticalStrut(3));
			msp = new ModuleSelectionPanel(-1, -1, false);
			msp.setModule(m);
			msp.setWidth(MODULESIZE);
			msp.setHeight(MODULESIZE);
			msp.setShowToolTip(true);
			generalPanel.add(msp);
		}
		generalPanel.add(createVerticalStrut(3));
		JScrollPane generalScroller = new JScrollPane(generalPanel);
		generalScroller.setPreferredSize(new Dimension(84, 256));
		generalScroller.setMinimumSize(new Dimension(84, 128));
		addTab("General", generalScroller);

		JPanel pilotPanel = new JGradPanel();
		pilotPanel.setLayout(new BoxLayout(pilotPanel, Y_AXIS));
		pilotPanel.setBackground(Piloting.getColor());
		for (Module m : INSTANCE.getAllModulesForProfession("pilotting"))
		{
			pilotPanel.add(createVerticalStrut(3));
			msp = new ModuleSelectionPanel(-1, -1, false);
			msp.setModule(m);
			msp.setWidth(MODULESIZE);
			msp.setHeight(MODULESIZE);
			msp.setShowToolTip(true);
			pilotPanel.add(msp);
		}
		pilotPanel.add(createVerticalStrut(3));
		JScrollPane pilotScroller = new JScrollPane(pilotPanel);
		pilotScroller.setPreferredSize(new Dimension(84, 256));
		pilotScroller.setMinimumSize(new Dimension(84, 128));
		addTab("Pilotting", pilotScroller);

		JPanel sciencePanel = new JGradPanel();
		sciencePanel.setLayout(new BoxLayout(sciencePanel, Y_AXIS));
		sciencePanel.setBackground(Science.getColor());
		for (Module m : INSTANCE.getAllModulesForProfession("science"))
		{
			sciencePanel.add(createVerticalStrut(3));
			msp = new ModuleSelectionPanel(-1, -1, false);
			msp.setModule(m);
			msp.setWidth(MODULESIZE);
			msp.setHeight(MODULESIZE);
			msp.setShowToolTip(true);
			sciencePanel.add(msp);
		}
		sciencePanel.add(createVerticalStrut(3));
		JScrollPane scienceScroller = new JScrollPane(sciencePanel);
		scienceScroller.setPreferredSize(new Dimension(84, 256));
		scienceScroller.setMinimumSize(new Dimension(84, 128));
		addTab("Science", scienceScroller);

		JPanel engineerPanel = new JGradPanel();
		engineerPanel.setLayout(new BoxLayout(engineerPanel, Y_AXIS));
		engineerPanel.setBackground(Engineering.getColor());

		for (Module m : INSTANCE.getAllModulesForProfession("engineering"))
		{
			engineerPanel.add(createVerticalStrut(3));
			msp = new ModuleSelectionPanel(-1, -1, false);
			msp.setModule(m);
			msp.setWidth(MODULESIZE);
			msp.setHeight(MODULESIZE);
			msp.setShowToolTip(true);
			engineerPanel.add(msp);
		}
		engineerPanel.add(createVerticalStrut(3));
		JScrollPane engineerScroller = new JScrollPane(engineerPanel);
		engineerScroller.setPreferredSize(new Dimension(84, 256));
		engineerScroller.setMinimumSize(new Dimension(84, 128));
		addTab("Engineering", engineerScroller);

		JPanel combatPanel = new JGradPanel();
		combatPanel.setLayout(new BoxLayout(combatPanel, Y_AXIS));
		combatPanel.setBackground(Combat.getColor());
		combatPanel.add(createVerticalStrut(3));
		for (Module m : INSTANCE.getAllModulesForProfession("combat"))
		{
			combatPanel.add(createVerticalStrut(3));
			msp = new ModuleSelectionPanel(-1, -1, false);
			msp.setModule(m);
			msp.setWidth(MODULESIZE);
			msp.setHeight(MODULESIZE);
			msp.setShowToolTip(true);
			combatPanel.add(msp);
		}
		combatPanel.add(createVerticalStrut(3));
		JScrollPane combatScroller = new JScrollPane(combatPanel);
		combatScroller.setPreferredSize(new Dimension(84, 256));
		combatScroller.setMinimumSize(new Dimension(84, 128));
		addTab("Combat", combatScroller);

		JPanel obsoletePanel = new JGradPanel();
		obsoletePanel.setLayout(new BoxLayout(obsoletePanel, Y_AXIS));
		obsoletePanel.setBackground(new Color(50, 40, 10));
		obsoletePanel.add(createVerticalStrut(3));
		for (Module m : INSTANCE.getAllModulesForProfession("obsolete"))
		{
			obsoletePanel.add(createVerticalStrut(3));
			msp = new ModuleSelectionPanel(-1, -1, false);
			msp.setModule(m);
			msp.setWidth(MODULESIZE);
			msp.setHeight(MODULESIZE);
			msp.setShowToolTip(true);
			obsoletePanel.add(msp);
		}
		obsoletePanel.add(createVerticalStrut(3));
		JScrollPane obsoleteScroller = new JScrollPane(obsoletePanel);
		obsoleteScroller.setPreferredSize(new Dimension(84, 256));
		obsoleteScroller.setMinimumSize(new Dimension(84, 128));
		addTab("Obsolete", obsoleteScroller);

		setSelectedIndex(1);
	}
}
