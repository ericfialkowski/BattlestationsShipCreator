package com.ericski.ui;

public class FileChooserExtensionFileFilter extends javax.swing.filechooser.FileFilter
{

	protected String extension;
	protected String description;

	public FileChooserExtensionFileFilter(String extension, String description)
	{
		this.extension = extension;
		this.description = description;
	}

	@Override
	public boolean accept(java.io.File f)
	{
		return (f.isDirectory() || f.getAbsolutePath().toLowerCase().endsWith(
				extension));
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	public String getExtension()
	{
		return extension;
	}
}
