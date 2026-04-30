package com.sortedbank;

public enum SortMethod
{
	ALPHABETICAL("Alphabetical"),
	VALUE("Value (High to Low)"),
	VALUE_ASC("Value (Low to High)"),
	ITEM_ID("Item ID"),
	CATEGORY("Category");

	private final String displayName;

	SortMethod(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
