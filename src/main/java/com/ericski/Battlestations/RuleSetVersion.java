package com.ericski.Battlestations;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RuleSetVersion
{
	private static final Logger logger = LogManager.getLogger(RuleSetVersion.class);
	private static final String IS_V1_PREF = "IsV1";
	private static final String IS_V1_1_PREF = "IsV1_1";

	protected RuleSetEnum currentVersion = RuleSetEnum.Version1_1;  // current rules are 1.1

	private static class SingletonHolder
	{

		private final static RuleSetVersion INSTANCE = new RuleSetVersion();
	}

	public static RuleSetVersion getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	public RuleSetEnum getCurrentVersion()
	{
		return currentVersion;
	}

	public void setCurrentVersion(RuleSetEnum currentVersion)
	{
		this.currentVersion = currentVersion;
	}

	public boolean isV1()
	{
		return (currentVersion == RuleSetEnum.Version1);
	}

	public boolean isV1_1()
	{
		return (currentVersion == RuleSetEnum.Version1_1);
	}

	public void loadPreferences()
	{
		Preferences prefs = Preferences.userNodeForPackage(RuleSetVersion.class);
		boolean v1 = prefs.getBoolean(IS_V1_PREF, false);
		if (v1)
		{
			currentVersion = RuleSetEnum.Version1;
		}
		else
		{
			currentVersion = RuleSetEnum.Version1_1;
		}
	}

	public void savePreferences()
	{
		Preferences prefs = Preferences.userNodeForPackage(RuleSetVersion.class);
		prefs.putBoolean(IS_V1_PREF, isV1());
		prefs.putBoolean(IS_V1_1_PREF, isV1_1());
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			logger.warn("Couldn't store ruleset preference", e);
		}
	}
}
