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
		description = "The method used to sort bank items"
	)
	default SortMethod sortMethod()
	{
		return SortMethod.ALPHABETICAL;
	}
}
