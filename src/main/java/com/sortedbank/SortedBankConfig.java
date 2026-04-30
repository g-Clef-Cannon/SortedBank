package com.sortedbank;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("sortedbank")
public interface SortedBankConfig extends Config
{
	@ConfigItem(
		keyName = "sortMethod",
		name = "Sort Method",
		description = "The method used to sort bank items",
		position = 1
	)
	default SortMethod sortMethod()
	{
		return SortMethod.CATEGORY;
	}

	@ConfigItem(
		keyName = "priceSource",
		name = "Price Source",
		description = "Price source used for value-based sorting",
		position = 2
	)
	default PriceSource priceSource()
	{
		return PriceSource.GE_PRICE;
	}

	@ConfigItem(
		keyName = "showGhostItems",
		name = "Show Ghost Items",
		description = "Show faded placeholders for missing items in common groups (ores, bars, armor sets, etc.)",
		position = 3
	)
	default boolean showGhostItems()
	{
		return false;
	}
}
