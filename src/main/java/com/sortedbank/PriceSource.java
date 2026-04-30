package com.sortedbank;

public enum PriceSource
{
	GE_PRICE("Grand Exchange"),
	HIGH_ALCH("High Alchemy");

	private final String displayName;

	PriceSource(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
