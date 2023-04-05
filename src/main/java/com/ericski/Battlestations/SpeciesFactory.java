package com.ericski.Battlestations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpeciesFactory
{
	private final Map<String, Species> speciesHashtable;
	private final ArrayList<Species> speciesArray;

	private static class SingletonHolder
	{

		private final static SpeciesFactory INSTANCE = new SpeciesFactory();
	}

	public static SpeciesFactory getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private SpeciesFactory()
	{
		speciesArray = new ArrayList<>();
		speciesHashtable = new HashMap<>();

		Species alien = new Species("Tentac");
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Canosian");
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Human");
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Silicoid");
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Xeloxian");
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Zoallan");
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Whistler");
		alien.setBook(BookSelectionEnum.GCW);
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Fungaloid");
		alien.setBook(BookSelectionEnum.Pax);
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Beastmen");
		alien.setBook(BookSelectionEnum.Moreau);
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Trundlian");
		alien.setBook(BookSelectionEnum.Pirates);
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Eugene");
		alien.setBook(BookSelectionEnum.Doids);
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Bubbloid");
		alien.setBook(BookSelectionEnum.HMFYP);
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Kerbite");
		alien.setBook(BookSelectionEnum.HMFYP);
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Otyssian");
		alien.setBook(BookSelectionEnum.HMFYP);
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Vomeg");
		alien.setBook(BookSelectionEnum.HMFYP);
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);

		alien = new Species("Bot");
		alien.setBook(BookSelectionEnum.Bots);
		speciesArray.add(alien);
		speciesHashtable.put(alien.getName(), alien);
	}

	public List<String> getSpeciesNames()
	{
		List<String> names = new ArrayList<>();
		for (Species spec : speciesArray)
		{
			names.add(spec.getName());
		}
		Collections.sort(names);
		return names;
	}

	public int getSpeciesCount()
	{
		return speciesArray.size();
	}
}
