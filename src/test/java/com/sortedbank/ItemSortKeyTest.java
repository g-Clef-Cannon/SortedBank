package com.sortedbank;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ItemSortKeyTest
{
	@Test
	public void ordersHerbsByStateThenSkillLevel()
	{
		assertOrder(ItemCategory.HERB,
			"Grimy guam leaf",
			"Grimy marrentill",
			"Grimy tarromin",
			"Grimy ranarr weed",
			"Guam leaf",
			"Marrentill",
			"Tarromin",
			"Ranarr weed",
			"Limpwurt root");
	}

	@Test
	public void ordersToolsByTypeThenLevel()
	{
		assertOrder(ItemCategory.TOOL,
			"Iron pickaxe",
			"Mithril pickaxe",
			"Adamant pickaxe",
			"Rune pickaxe");
		assertOrder(ItemCategory.TOOL,
			"Bronze axe",
			"Steel axe",
			"Mithril axe",
			"Rune axe");
	}

	@Test
	public void ordersMaterialsByLikenessThenLevel()
	{
		assertOrder(ItemCategory.LOG,
			"Logs",
			"Oak logs",
			"Willow logs",
			"Maple logs",
			"Mahogany logs",
			"Yew logs");
		assertOrder(ItemCategory.ORE,
			"Tin ore",
			"Iron ore",
			"Coal",
			"Mithril ore",
			"Runite ore",
			"Bronze bar",
			"Iron bar",
			"Steel bar",
			"Mithril bar",
			"Adamantite bar");
		assertOrder(ItemCategory.GEM,
			"Uncut sapphire",
			"Uncut emerald",
			"Uncut ruby",
			"Uncut diamond",
			"Sapphire",
			"Emerald",
			"Ruby",
			"Diamond");
	}

	@Test
	public void ordersAmmunitionAndFletchingByFamilyThenLevel()
	{
		assertOrder(ItemCategory.AMMO,
			"Bronze arrow",
			"Iron arrow",
			"Steel arrow",
			"Mithril arrow",
			"Adamant arrow",
			"Rune arrow",
			"Bronze bolts",
			"Iron bolts",
			"Bronze knife",
			"Iron knife");
		assertOrder(ItemCategory.FLETCHING_COMPONENT,
			"Arrow shaft",
			"Feather",
			"Headless arrow",
			"Bronze arrowtips",
			"Iron arrowtips",
			"Opal bolt tips",
			"Bronze bolts (unf)",
			"Bow string",
			"Oak longbow (u)",
			"Willow shortbow (u)",
			"Willow longbow (u)",
			"Maple shortbow (u)");
	}

	@Test
	public void ordersConsumablesAndSeedsByProgression()
	{
		assertOrder(ItemCategory.POTION,
			"Attack potion(4)",
			"Attack potion(1)",
			"Super attack(4)",
			"Strength potion(4)",
			"Super strength(4)",
			"Defence potion(4)",
			"Super defence(4)");
		assertOrder(ItemCategory.SEED,
			"Potato seed",
			"Onion seed",
			"Cabbage seed",
			"Tomato seed",
			"Sweetcorn seed",
			"Strawberry seed",
			"Watermelon seed");
		assertOrder(ItemCategory.FOOD,
			"Raw tuna",
			"Tuna",
			"Raw lobster",
			"Lobster",
			"Burnt lobster",
			"Raw swordfish",
			"Swordfish");
	}

	private static void assertOrder(ItemCategory category, String... orderedItems)
	{
		for (int i = 0; i < orderedItems.length - 1; i++)
		{
			String first = orderedItems[i];
			String second = orderedItems[i + 1];
			String firstKey = ItemSortKey.categorySortKey(category, first);
			String secondKey = ItemSortKey.categorySortKey(category, second);
			assertTrue(first + " should sort before " + second + " (" + firstKey + " vs " + secondKey + ")",
				firstKey.compareTo(secondKey) < 0);
		}
	}
}
