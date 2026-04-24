package com.sortedbank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortMethod
{
	ALPHABETICAL("Alphabetical"),
	VALUE("Value (High to Low)"),
	VALUE_ASC("Value (Low to High)");

	private final String displayName;

	@Override
	public String toString()
	{
		return displayName;
	}
}
