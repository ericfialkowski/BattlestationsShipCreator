package com.ericski.Battlestations;

import static com.ericski.Battlestations.BookSelectionEnum.Core;

public class Species
{

	private final String name;
	private BookSelectionEnum book;

	public Species(String name)
	{
		this.name = name;
		this.book = Core;
	}

	public String getName()
	{
		return name;
	}

	public BookSelectionEnum getBook()
	{
		return book;
	}

	public void setBook(BookSelectionEnum book)
	{
		this.book = book;
	}

}
