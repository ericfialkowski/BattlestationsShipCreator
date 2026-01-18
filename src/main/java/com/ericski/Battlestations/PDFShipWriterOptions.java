package com.ericski.Battlestations;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import static java.util.prefs.Preferences.userNodeForPackage;
import static org.apache.logging.log4j.LogManager.getLogger;
import org.apache.logging.log4j.Logger;

public class PDFShipWriterOptions
{
	private static final Logger logger = getLogger(PDFShipWriterOptions.class);

	boolean showNotes = false;
	boolean damageTrack = false;
	boolean damageChart = true;
	boolean showOCC = true;
	boolean showSpeed = true;
	boolean showHelm = true;
	boolean showGuns = true;
	boolean showShield = true;
	boolean reverseShield = false;
	int pageSize = 0;
	float outputQualityReduction = .375f;

	public boolean isShowOCC()
	{
		return showOCC;
	}

	public void setShowOCC(boolean showOCC)
	{
		this.showOCC = showOCC;
	}

	public boolean isShowSpeed()
	{
		return showSpeed;
	}

	public void setShowSpeed(boolean showSpeed)
	{
		this.showSpeed = showSpeed;
	}

	public boolean isShowHelm()
	{
		return showHelm;
	}

	public void setShowHelm(boolean showHelm)
	{
		this.showHelm = showHelm;
	}

	public boolean isShowGuns()
	{
		return showGuns;
	}

	public void setShowGuns(boolean showGuns)
	{
		this.showGuns = showGuns;
	}

	public boolean isShowShield()
	{
		return showShield;
	}

	public void setShowShield(boolean showShield)
	{
		this.showShield = showShield;
	}

	public boolean isReverseShield()
	{
		return reverseShield;
	}

	public void setReverseShield(boolean reverseShield)
	{
		this.reverseShield = reverseShield;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public boolean isDamageTrack()
	{
		return damageTrack;
	}

	public void setDamageTrack(boolean damageTrack)
	{
		this.damageTrack = damageTrack;
	}

	public boolean isDamageChart()
	{
		return damageChart;
	}

	public void setDamageChart(boolean damageChart)
	{
		this.damageChart = damageChart;
	}

	public void loadPreferences()
	{
		Preferences prefs = userNodeForPackage(PDFShipWriterOptions.class);
		showNotes = prefs.getBoolean("ShowNotes", false);
		showSpeed = prefs.getBoolean("ShowSpeedTrack", true);
		showOCC = prefs.getBoolean("ShowOOCTrack", true);
		showHelm = prefs.getBoolean("ShowHelmPowerTrack", true);
		showGuns = prefs.getBoolean("ShowGunPowerTrack", true);
		showShield = prefs.getBoolean("ShowShieldPowerTrack", true);
		reverseShield = prefs.getBoolean("ReverseShieldPowerTrack", false);
		pageSize = prefs.getInt("PageSize", 0);
		outputQualityReduction = prefs.getFloat("OutputQualityReduction", 50.0F);

		switch (prefs.getInt("DamageChart", 0))
		{
			case 1:
				damageChart = false;
				damageTrack = true;
				break;
			case 2:
				damageChart = false;
				damageTrack = false;
				break;
			default:
				damageChart = true;
				damageTrack = false;
				break;
		}
	}

	public void savePreferences()
	{
		Preferences prefs = userNodeForPackage(PDFShipWriterOptions.class);

		prefs.putBoolean("ShowNotes", showNotes);
		prefs.putBoolean("ShowSpeedTrack", showSpeed);
		prefs.putBoolean("ShowOOCTrack", showOCC);
		prefs.putBoolean("ShowHelmPowerTrack", showHelm);
		prefs.putBoolean("ShowGunPowerTrack", showGuns);
		prefs.putBoolean("ShowShieldPowerTrack", showShield);
		prefs.putBoolean("ReverseShieldPowerTrack", reverseShield);
		prefs.putInt("PageSize", pageSize);
		prefs.putFloat("OutputQualityReduction", outputQualityReduction);

		if (damageChart)
		{
			prefs.putInt("DamageChart", 0);
		}
		else if (damageTrack)
		{
			prefs.putInt("DamageChart", 1);
		}
		else
		{
			prefs.putInt("DamageChart", 2);
		}

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			logger.warn("Couldn't store preferences", e);
		}
	}

	public float getOutputQualityReduction()
	{
		return outputQualityReduction;
	}

	public void setOutputQualityReduction(float outputQualityReduction)
	{
		this.outputQualityReduction = outputQualityReduction;
	}

	public boolean isShowNotes()
	{
		return showNotes;
	}

	public void setShowNotes(boolean showNotes)
	{
		this.showNotes = showNotes;
	}

}
