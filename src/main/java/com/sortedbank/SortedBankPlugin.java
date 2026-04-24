package com.sortedbank;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Sorted Bank",
	description = "Automatically sort bank items client-side by various sort methods",
	tags = {"bank", "sort", "organize", "items"}
)
public class SortedBankPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SortedBankConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Sorted Bank started");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Sorted Bank stopped");
	}

	@Provides
	SortedBankConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SortedBankConfig.class);
	}
}
