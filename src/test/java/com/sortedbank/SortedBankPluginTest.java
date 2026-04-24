package com.sortedbank;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SortedBankPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SortedBankPlugin.class);
		RuneLite.main(args);
	}
}
