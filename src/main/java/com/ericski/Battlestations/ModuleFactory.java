package com.ericski.Battlestations;

import static com.ericski.Battlestations.Module.BLANK;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ModuleFactory
{
	INSTANCE;

	private final Logger logger;
	private final Map<String, Module> nameMap;
	private final Set<String> professions;
	private File userDir;

	private ModuleFactory()
	{
		logger = LogManager.getLogger(ModuleFactory.class);
		nameMap = new HashMap<>();
		professions = new HashSet<>();
		userDir = getModuleDirectory();

		addModule("cannon", "Cannon", "combat");
		addModule("cargo_bay", "Cargo Bay", "general");
		addModule("std_cargo_bay_one", "Hull Stabilizer Cargo Bay", "general", false);
		addModule("std_cargo_bay_two", "Extra Life Support Cargo Bay", "general", false);
		addModule("tractor_cargo_bay", "Tractor Cargo Bay", "general", false);
		addModule("bot_cargo_bay", "Bot Cargo Bay", "general", false);
		addModule("cargo_pod", "Cargo Pod", "general", false);
		addModule("cloaking_device", "Cloaking Device", "science");
		addModule("damage_control", "Damage Control", "engineering", false);
		addModule("engine", "Engine", "engineering");
		addModule("fighter_bay", "Fighter Bay", "pilotting", false);
		addModule("fusion_cannon", "Fusion Canon", "combat", false);
		addModule("gravity_lance", "Gravity Lance", "combat", false);
		addModule("helm", "Helm", "pilotting");
		addModule("hyperdrive", "Hyperdrive", "science");
		addModule("life_support", "Life Support", "general");
		addModule("mine_layer", "Mine Layer", "engineering");
		addModule("missile_bay", "Missile Bay", "combat");
		addModule("missile_pod", "Missile Pod", "combat", false);
		addModule("science", "Science Bay", "science");
		addModule("sick_bay", "Sick Bay", "science");
		addModule("teleporter", "Teleporter", "science");
		addModule("tractor", "Tractor Beam", "obsolete", false);
		addModule("hull_stabilizer", "Hull Stabilizer", "obsolete", false);
		nameMap.put(Module.BLANK, getBlankModule());
		processUserModules();
	}

	public static Module getBlankModule()
	{
		return new InternalModule(BLANK, 0, "Empty Space", "general", "/com/ericski/Battlestations/Images/Modules/blank.jpg");
	}

	private void addModule(String name, String description, String profession)
	{
		addModule(name, description, profession, true);
	}

	private void addModule(String name, String description, String profession, boolean newEdition)
	{
		professions.add(profession);

		Module m;
		if (newEdition)
			m = new InternalModule(name, 0, description, profession, "/com/ericski/Battlestations/Images/Modules300dpi/"
																		 + name + ".jpg");
		else
			m = new InternalModule(name, 0, description, profession, "/com/ericski/Battlestations/Images/Modules/"
																		 + name + ".jpg");
		nameMap.put(name, m);
	}

	public List<String> getProfessions()
	{
		List<String> rtn = new ArrayList<>(professions);
		Collections.sort(rtn);
		return Collections.unmodifiableList(rtn);
	}

	public Collection<Module> getAllModules()
	{
		List<Module> rtn = new ArrayList<>();
		for (Module m : nameMap.values())
		{
			rtn.add(m.copy());
		}
		Collections.sort(rtn);
		return Collections.unmodifiableCollection(rtn);
	}

	public List<Module> getAllModulesForProfession(String profession)
	{
		List<Module> professionModules = new ArrayList<>();
		for (Module m : nameMap.values())
		{
			if (profession.equalsIgnoreCase(m.getProfession()))
			{
				professionModules.add(m.copy());
			}
		}
		Collections.sort(professionModules);
		return Collections.unmodifiableList(professionModules);
	}

	public Module getModuleByName(String nameString)
	{
		Module fromMap = nameMap.get(nameString);
		return fromMap.copy();
	}

	public File getModuleDirectory()
	{
		if (userDir == null)
		{
			userDir = new File(System.getProperty("user.home") + "/.shipcreator/modules");
			if (!userDir.exists())
			{
				userDir.mkdirs();
			}
		}
		return userDir;
	}

	private void processUserModules()
	{
		File[] moduleFiles = userDir.listFiles((File dir, String fileName) -> (fileName.endsWith(".module")));
		for (File f : moduleFiles)
		{
			Module c = CustomUserModule.fromXml(f);
			if (c != null)
			{
				professions.add(c.getProfession());
				nameMap.put(c.getName(), c);
			}
		}
	}
}
